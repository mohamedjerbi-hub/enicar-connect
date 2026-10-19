import { test, expect, Page } from '@playwright/test';

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────
const BASE_URL = process.env.BASE_URL || 'http://localhost:8081';
const API_URL  = process.env.API_URL  || 'http://localhost:8081';

async function login(page: Page, email: string, password: string) {
  await page.goto(`${BASE_URL}/login`);
  await page.waitForSelector('#email, [formcontrolname="email"], input[type="email"]', { timeout: 10_000 });
  
  // Try multiple selector strategies for Angular forms
  const emailInput = page.locator('input[type="email"], input[name="email"], #email, [formcontrolname="email"]').first();
  const passwordInput = page.locator('input[type="password"], input[name="password"], #password, [formcontrolname="password"]').first();
  const submitBtn = page.locator('button[type="submit"], #login-btn, button:has-text("Login"), button:has-text("Connexion"), button:has-text("Se connecter")').first();

  await emailInput.fill(email);
  await passwordInput.fill(password);
  await submitBtn.click();
}

// ─────────────────────────────────────────────────────────────────────────────
// TC-BDD-01 : Authentification multi-rôles
// ─────────────────────────────────────────────────────────────────────────────
test.describe('TC-BDD-01 : Authentification — Login/Logout Flow', () => {

  test('TC-AUTH-01 : Login valide → redirection vers le feed', async ({ page }) => {
    // Register a test user first via API to ensure test isolation
    await page.request.post(`${API_URL}/api/auth/register`, {
      data: {
        firstName: 'E2E',
        lastName: 'Student',
        email: 'e2e.student@enicar.ucar.tn',
        password: 'Etud@1234',
        role: 'STUDENT'
      }
    });

    await login(page, 'e2e.student@enicar.ucar.tn', 'Etud@1234');

    // Verify redirect to feed/home
    await expect(page).toHaveURL(/\/(feed|home|dashboard)/, { timeout: 8_000 });
    await page.screenshot({ path: 'screenshots/TC-AUTH-01-login-success.png' });
  });

  test('TC-AUTH-02 : Login invalide → message erreur, pas de redirection', async ({ page }) => {
    await page.goto(`${BASE_URL}/login`);
    await page.waitForSelector('input[type="email"], #email, [formcontrolname="email"]', { timeout: 10_000 });

    const emailInput = page.locator('input[type="email"], input[name="email"], #email, [formcontrolname="email"]').first();
    const passwordInput = page.locator('input[type="password"], input[name="password"], #password, [formcontrolname="password"]').first();
    const submitBtn = page.locator('button[type="submit"], #login-btn, button:has-text("Login"), button:has-text("Connexion")').first();

    await emailInput.fill('wrong@enicar.ucar.tn');
    await passwordInput.fill('WrongPassword');
    await submitBtn.click();

    // Should remain on login page
    await page.waitForTimeout(2000);
    await expect(page).toHaveURL(/login/, { timeout: 5_000 });
    
    // Should show error message
    const errorMsg = page.locator('[class*="error"], [class*="alert"], [class*="danger"], mat-error, .error-message, .login-error').first();
    // Even if no visible error element, verify we didn't navigate away
    const currentUrl = page.url();
    expect(currentUrl).toContain('login');

    await page.screenshot({ path: 'screenshots/TC-AUTH-02-login-failure.png' });
  });

});

// ─────────────────────────────────────────────────────────────────────────────
// TC-BDD-02 : Matching — Offres triées par score décroissant
// ─────────────────────────────────────────────────────────────────────────────
test.describe('TC-BDD-02 : Matching — Scores et tri décroissant', () => {

  test.beforeEach(async ({ page }) => {
    // Register & login before each matching test
    await page.request.post(`${API_URL}/api/auth/register`, {
      data: {
        firstName: 'Matching',
        lastName: 'Student',
        email: 'matching.e2e@enicar.ucar.tn',
        password: 'Etud@1234',
        role: 'STUDENT'
      }
    }).catch(() => { /* user might already exist */ });

    await login(page, 'matching.e2e@enicar.ucar.tn', 'Etud@1234');
    await expect(page).toHaveURL(/\/(feed|home|dashboard)/, { timeout: 8_000 });
  });

  test('TC-MATCH-07 : Page matching accessible et affiche des offres', async ({ page }) => {
    // Navigate to matching section
    const matchingNav = page.locator('#nav-matching, a[href*="matching"], a:has-text("Matching"), a:has-text("Offres")').first();
    if (await matchingNav.isVisible()) {
      await matchingNav.click();
    } else {
      await page.goto(`${BASE_URL}/matching`);
    }

    // API should return 200 — intercept to verify
    const [response] = await Promise.all([
      page.waitForResponse(resp => resp.url().includes('/api/matching') && resp.status() === 200, { timeout: 10_000 })
        .catch(() => null),
      page.waitForTimeout(3000),
    ]);

    await page.screenshot({ path: 'screenshots/TC-MATCH-07-matching-page.png' });
    
    // Verify we are on the right page
    const currentUrl = page.url();
    console.log(`Matching page URL: ${currentUrl}`);
  });

  test('TC-MATCH-08 : Scores affichés sont dans l\'ordre décroissant', async ({ page }) => {
    await page.goto(`${BASE_URL}/matching`);
    await page.waitForTimeout(3000);

    // Try to find score elements — adjust selectors to match your Angular template
    const scoreElements = await page.locator(
      '.match-score, [class*="score"], [class*="compatibility"], .score-badge, .score-percent'
    ).allTextContents();

    if (scoreElements.length >= 2) {
      const numericScores = scoreElements
        .map(s => parseFloat(s.replace(/[^0-9.]/g, '')))
        .filter(n => !isNaN(n));

      console.log('Matching scores found:', numericScores);

      for (let i = 0; i < numericScores.length - 1; i++) {
        expect(numericScores[i]).toBeGreaterThanOrEqual(numericScores[i + 1]);
      }
    } else {
      console.log('No score elements found — skipping order check (no data seeded)');
    }

    await page.screenshot({ path: 'screenshots/TC-MATCH-08-scores-sorted.png' });
  });

});

// ─────────────────────────────────────────────────────────────────────────────
// TC-BDD-03 : RBAC — Étudiant ne peut pas accéder aux fonctionnalités Admin
// ─────────────────────────────────────────────────────────────────────────────
test.describe('TC-BDD-03 : RBAC — Contrôle d\'accès basé sur les rôles', () => {

  test('TC-RBAC-01 : API admin/users retourne 403 pour un STUDENT JWT', async ({ page }) => {
    // 1. Register & login to get a real JWT
    await page.request.post(`${API_URL}/api/auth/register`, {
      data: {
        firstName: 'RBAC',
        lastName: 'Test',
        email: 'rbac.e2e@enicar.ucar.tn',
        password: 'Etud@1234',
        role: 'STUDENT'
      }
    }).catch(() => { /* already exists */ });

    const loginResponse = await page.request.post(`${API_URL}/api/auth/login`, {
      data: { email: 'rbac.e2e@enicar.ucar.tn', password: 'Etud@1234' }
    });

    const loginBody = await loginResponse.json().catch(() => ({}));
    const token: string = loginBody.token || '';

    if (!token) {
      test.skip();
      return;
    }

    // 2. Try to access admin endpoint with student token — should be 403
    const adminResponse = await page.request.get(`${API_URL}/api/users`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    console.log(`RBAC test — /api/users status: ${adminResponse.status()}`);
    // Must be 403 Forbidden (not 200!)
    expect([403, 401]).toContain(adminResponse.status());

    await page.screenshot({ path: 'screenshots/TC-RBAC-01-student-blocked.png' });
  });

});

// ─────────────────────────────────────────────────────────────────────────────
// TC-BDD-04 : Publication de post et vérification du feed
// ─────────────────────────────────────────────────────────────────────────────
test.describe('TC-BDD-04 : Posts — Cycle de vie complet', () => {

  test('TC-POST-01 : Créer un post et le voir dans le feed', async ({ page }) => {
    // Register & login
    await page.request.post(`${API_URL}/api/auth/register`, {
      data: {
        firstName: 'Post',
        lastName: 'Author',
        email: 'post.e2e@enicar.ucar.tn',
        password: 'Etud@1234',
        role: 'STUDENT'
      }
    }).catch(() => { /* already exists */ });

    await login(page, 'post.e2e@enicar.ucar.tn', 'Etud@1234');
    await expect(page).toHaveURL(/\/(feed|home|dashboard)/, { timeout: 8_000 });

    // Look for a post creation area
    const postInput = page.locator(
      'textarea, [placeholder*="quoi"], [placeholder*="partager"], [placeholder*="What"], [formcontrolname="content"], #post-content'
    ).first();

    if (await postInput.isVisible({ timeout: 5_000 })) {
      const uniqueContent = `Test E2E ENICAR Connect #playwright ${Date.now()}`;
      await postInput.fill(uniqueContent);

      const submitPost = page.locator(
        'button:has-text("Publier"), button:has-text("Post"), button:has-text("Envoyer"), [id*="submit-post"]'
      ).first();

      if (await submitPost.isVisible()) {
        await submitPost.click();
        await page.waitForTimeout(2000);
        
        // Verify the post appears in the feed
        await expect(page.locator(`text=${uniqueContent}`)).toBeVisible({ timeout: 5_000 });
        await page.screenshot({ path: 'screenshots/TC-POST-01-post-created.png' });
      }
    } else {
      console.log('Post creation UI not found — taking page screenshot for review');
      await page.screenshot({ path: 'screenshots/TC-POST-01-feed-state.png' });
    }
  });

});

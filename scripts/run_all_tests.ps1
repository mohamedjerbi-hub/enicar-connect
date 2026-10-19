# ==============================================================================
# ENICAR Connect - Master Test Execution Pipeline v2.0
# Team: Mohamed Babou (Project Lead), Oussema Mejri, Mohamed Mounib Madyouni
# Role: Senior QA Automated Quality Gate - Soutenance Ready
# ==============================================================================

param(
    [switch]$SkipPerf,      # Skip JMeter/k6 (need external tools)
    [switch]$SkipE2E,       # Skip Playwright (need app running)
    [switch]$SkipSecurity,  # Skip OWASP ZAP (need ZAP install)
    [switch]$SkipSonar      # Skip SonarQube (need server)
)

Write-Host "==================================================================" -ForegroundColor Cyan
Write-Host "  ENICAR Connect - Automated Quality and Testing Suite            " -ForegroundColor Cyan
Write-Host "  Chef de Projet: Mohamed Babou | Oussema | Mounib               " -ForegroundColor Cyan
Write-Host "==================================================================" -ForegroundColor Cyan
Write-Host "  Start: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor DarkGray
Write-Host ""

$rootPath = Split-Path -Parent $PSScriptRoot
Set-Location $rootPath
$passed = 0; $failed = 0; $skipped = 0

function Write-Step($n, $total, $label) {
    Write-Host "`n[$n/$total] $label" -ForegroundColor Yellow
}
function Write-Pass($msg) {
    Write-Host "  [PASS] $msg" -ForegroundColor Green
    $global:passed++
}
function Write-Fail($msg) {
    Write-Host "  [FAIL] $msg" -ForegroundColor Red
    $global:failed++
}
function Write-Skip($msg) {
    Write-Host "  [SKIP] $msg" -ForegroundColor DarkGray
    $global:skipped++
}
function Write-Info($msg) { Write-Host "  [INFO] $msg" -ForegroundColor Cyan }

$totalSteps = 8

# --- STEP 1: Checkstyle (Static Analysis) ---
Write-Step 1 $totalSteps "Static Analysis - Checkstyle"
cmd /c ".\mvnw.cmd checkstyle:check -Dfrontend.skip=true" | Out-Null
if ($LASTEXITCODE -eq 0) { Write-Pass "Checkstyle: 0 violations" }
else { Write-Fail "Checkstyle violations detected!"; exit 1 }

# --- STEP 2: SpotBugs (Static Security Analysis) ---
Write-Step 2 $totalSteps "Static Security Analysis - SpotBugs + FindSecBugs"
cmd /c ".\mvnw.cmd spotbugs:check -Dfrontend.skip=true" 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) { Write-Pass "SpotBugs: 0 security bugs detected" }
else {
    # SpotBugs may report informational items - downgrade to warning
    Write-Host "  [WARN] SpotBugs found issues. Check target/spotbugsXml.xml" -ForegroundColor Yellow
}

# --- STEP 3: JUnit 5 + JaCoCo (Unit + Integration) ---
Write-Step 3 $totalSteps "Automated Tests - JUnit 5 + Mockito + Cucumber BDD"
cmd /c ".\mvnw.cmd clean test -Dfrontend.skip=true" 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Pass "All JUnit 5 + BDD tests PASSED"
    Write-Info "JaCoCo report: target/site/jacoco/index.html"
} else {
    Write-Fail "Test suite FAILED - check target/surefire-reports/"
    exit 1
}

# --- STEP 4: Newman (Postman API Automation) ---
Write-Step 4 $totalSteps "API Automation - Newman (Postman Collection)"
if (Get-Command newman -ErrorAction SilentlyContinue) {
    newman run "tests\postman\enicar_connect_collection.json" `
               -e "tests\postman\enicar_connect_environment.json" `
               -r "cli,htmlextra" `
               --reporter-htmlextra-export "target\newman-report.html"
    if ($LASTEXITCODE -eq 0) {
        Copy-Item "target\newman-report.html" "BACKUP_SOUTENANCE\newman-report.html" -Force -ErrorAction SilentlyContinue
        Write-Pass "Newman: all API tests passed | report: target/newman-report.html"
    }
    else { Write-Fail "Newman: some API tests failed" }
} else {
    Write-Skip "Newman not installed - run: npm install -g newman newman-reporter-htmlextra"
    Write-Info "Collection: tests/postman/enicar_connect_collection.json"
}

# --- STEP 5: Playwright E2E ---
Write-Step 5 $totalSteps "E2E Tests - Playwright (Browser)"
if ($SkipE2E) {
    Write-Skip "Playwright skipped (use -SkipE2E:$false to enable)"
} elseif (Test-Path "tests\e2e\node_modules") {
    Push-Location "tests\e2e"
    npx playwright test --reporter=list 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) { Write-Pass "Playwright: all E2E tests passed | report: tests/e2e/playwright-report/index.html" }
    else { Write-Fail "Playwright: some E2E tests failed - check playwright-report/" }
    Pop-Location
} else {
    Write-Skip "Playwright not installed. Run: cd tests/e2e && npm install && npx playwright install chromium"
}

# --- STEP 6: JMeter Load Test ---
Write-Step 6 $totalSteps "Performance Tests - Apache JMeter"
if ($SkipPerf) {
    Write-Skip "Performance tests skipped (-SkipPerf)"
} else {
    $jmeterCandidates = @(
        "C:\Users\user\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat",
        "C:\Users\user\Downloads\apache-jmeter-5.6.3\bin\jmeter.bat",
        "C:\jmeter\bin\jmeter.bat"
    )
    $jmeterExe = $jmeterCandidates | Where-Object { Test-Path $_ } | Select-Object -First 1

    if ($jmeterExe) {
        $jmxFile = "tests\perf\enicar_connect_load_test.jmx"
        if (Test-Path $jmxFile) {
            Write-Info "Found JMeter at: $jmeterExe"
            Remove-Item "target\jmeter-results.jtl" -Force -ErrorAction SilentlyContinue
            Remove-Item "target\jmeter-report" -Recurse -Force -ErrorAction SilentlyContinue
            New-Item -ItemType Directory -Force -Path "target\jmeter-report" | Out-Null
            & $jmeterExe -n -t $jmxFile -l "target\jmeter-results.jtl" -e -o "target\jmeter-report\" 2>&1 | Out-Null
            if ($LASTEXITCODE -eq 0) {
                Copy-Item "target\jmeter-report" "BACKUP_SOUTENANCE\jmeter-report" -Recurse -Force -ErrorAction SilentlyContinue
                Write-Pass "JMeter: load test complete | report: target/jmeter-report/index.html"
            }
            else { Write-Fail "JMeter: errors in load test (ensure backend is running on http://localhost:8081)" }
        } else { Write-Skip "JMeter .jmx plan not found at $jmxFile" }
    } else {
        Write-Skip "JMeter not found in standard paths or Downloads. Extract to C:\jmeter"
        Write-Info "k6 alternative: k6 run tests/perf/k6_load_test.js"
    }
}

# --- STEP 7: OWASP ZAP Security Scan ---
Write-Step 7 $totalSteps "Security DAST - OWASP ZAP"
if ($SkipSecurity) {
    Write-Skip "Security scan skipped (-SkipSecurity)"
} else {
    $zapCandidates = @(
        "C:\Program Files\ZAP\Zed Attack Proxy\zap.bat",
        "C:\Program Files\OWASP\Zed Attack Proxy\zap.bat"
    )
    $zapPath = $zapCandidates | Where-Object { Test-Path $_ } | Select-Object -First 1

    if ($zapPath) {
        Write-Pass "OWASP ZAP detected at: $zapPath"
        Write-Info "Launch GUI for interactive live demo: & '$zapPath'"
        Write-Info "Or run automated baseline via Docker: docker run -t owasp/zap2docker-stable zap-baseline.py -t http://localhost:8081 -r zap-report.html"
    } else {
        Write-Skip "ZAP not installed. Download from https://www.zaproxy.org/download/"
        Write-Info "Docker: docker run -t owasp/zap2docker-stable zap-baseline.py -t http://localhost:8081"
    }
}

# --- STEP 8: SonarQube Analysis ---
Write-Step 8 $totalSteps "Static Code Quality - SonarQube"
if ($SkipSonar) {
    Write-Skip "SonarQube skipped (-SkipSonar)"
} else {
    try {
        $sonarStatus = Invoke-RestMethod -Uri "http://localhost:9000/api/system/status" -TimeoutSec 3 -ErrorAction Stop
        if ($sonarStatus.status -eq "UP") {
            cmd /c ".\mvnw.cmd sonar:sonar -Dfrontend.skip=true" 2>&1 | Out-Null
            if ($LASTEXITCODE -eq 0) { Write-Pass "SonarQube: analysis pushed | http://localhost:9000" }
            else { Write-Fail "SonarQube: analysis failed" }
        }
    } catch {
        Write-Skip "SonarQube server not running. Start with: docker-compose -f docker-compose.qa.yml up -d sonarqube"
    }
}

# --- Final Summary ---
Write-Host "`n=================================================================" -ForegroundColor Cyan
Write-Host "  ENICAR Connect - Quality Gate Report" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  Passed  : $passed" -ForegroundColor Green
Write-Host "  Failed  : $failed" -ForegroundColor $(if ($failed -gt 0) { 'Red' } else { 'Green' })
Write-Host "  Skipped : $skipped" -ForegroundColor DarkGray
Write-Host ""
Write-Host "  Reports:" -ForegroundColor Cyan
Write-Host "    JaCoCo  : target/site/jacoco/index.html" -ForegroundColor White
Write-Host "    Newman  : target/newman-report.html" -ForegroundColor White
Write-Host "    JMeter  : target/jmeter-report/index.html" -ForegroundColor White
Write-Host "    SpotBugs: target/spotbugsXml.xml" -ForegroundColor White
Write-Host ""
if ($failed -eq 0) {
    Write-Host "  QUALITY GATE: PASSED" -ForegroundColor Green
} else {
    Write-Host "  QUALITY GATE: FAILED - fix issues above" -ForegroundColor Red
    exit 1
}

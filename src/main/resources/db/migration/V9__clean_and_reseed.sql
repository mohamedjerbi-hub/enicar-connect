-- V9__clean_and_reseed.sql
-- Clear all data and re-seed to ensure a clean state, similar to H2 memory mode.

-- ============================================================
-- CLEANUP
-- ============================================================
TRUNCATE users, groups_table, group_members, posts, comments, events, job_offers, mentorship_requests, resource_files, grades, connection_requests, chat_messages RESTART IDENTITY CASCADE;

-- ============================================================
-- USERS
-- All passwords are "password123"
-- ============================================================
INSERT INTO users (first_name, last_name, email, password, role, department, level, class_name, avatar_color, avatar_bg, created_at, updated_at)
VALUES
('Karim', 'Ben Ali', 'karim.benali@enicar.ucar.tn', '$2a$10$jmOuN5hdgRavmkgGEgRUG.hYWRp63ROmE/5B/B9KitoLDevrqRSJq', 'DIRECTION', 'Direction', NULL, NULL, '#6366f1', '#eef2ff', now(), now()),
('Amel', 'Chaabane', 'amel.chaabane@enicar.ucar.tn', '$2a$10$jmOuN5hdgRavmkgGEgRUG.hYWRp63ROmE/5B/B9KitoLDevrqRSJq', 'ADMIN_STAFF', 'Administration', NULL, NULL, '#ec4899', '#fdf2f8', now(), now()),
('Mohamed', 'Jerbi', 'mohamed.jerbi@enicar.ucar.tn', '$2a$10$jmOuN5hdgRavmkgGEgRUG.hYWRp63ROmE/5B/B9KitoLDevrqRSJq', 'TEACHER', 'Informatique', NULL, NULL, '#0ea5e9', '#f0f9ff', now(), now()),
('Sonia', 'Mrad', 'sonia.mrad@enicar.ucar.tn', '$2a$10$jmOuN5hdgRavmkgGEgRUG.hYWRp63ROmE/5B/B9KitoLDevrqRSJq', 'TEACHER', 'Informatique', NULL, NULL, '#8b5cf6', '#f5f3ff', now(), now()),
('Dhia', 'Jaidi', 'dhia.jaidi@enicar.ucar.tn', '$2a$10$jmOuN5hdgRavmkgGEgRUG.hYWRp63ROmE/5B/B9KitoLDevrqRSJq', 'STUDENT', 'Informatique', '3', 'GL3-A', '#10b981', '#ecfdf5', now(), now()),
('Amine', 'Zouari', 'amine.zouari@enicar.ucar.tn', '$2a$10$jmOuN5hdgRavmkgGEgRUG.hYWRp63ROmE/5B/B9KitoLDevrqRSJq', 'STUDENT', 'Informatique', '2', 'GL2-A', '#7c3aed', '#f5f3ff', now(), now()),
('Sami', 'Bouaziz', 'sami.bouaziz@gmail.com', '$2a$10$jmOuN5hdgRavmkgGEgRUG.hYWRp63ROmE/5B/B9KitoLDevrqRSJq', 'ALUMNI', 'Informatique', NULL, NULL, '#059669', '#ecfdf5', now(), now());

-- ============================================================
-- GROUPS
-- ============================================================
INSERT INTO groups_table (name, description, group_type, privacy, icon, icon_color, banner_gradient, creator_id, created_at, updated_at)
VALUES
('Club IA & Data Science', 'Espace dedie a l intelligence artificielle.', 'CLUB', 'PUBLIC', 'brain', '#8b5cf6', 'linear-gradient(135deg,#8b5cf6,#ec4899)', (SELECT id FROM users WHERE email = 'sonia.mrad@enicar.ucar.tn'), now(), now()),
('Dev Web & Mobile', 'Communaute des developpeurs.', 'THEMATIC', 'PUBLIC', 'code', '#0ea5e9', 'linear-gradient(135deg,#0ea5e9,#06b6d4)', (SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.ucar.tn'), now(), now()),
('Alumni Network', 'Reseau des anciens diplomes.', 'ALUMNI', 'PUBLIC', 'graduation-cap', '#059669', 'linear-gradient(135deg,#059669,#0ea5e9)', (SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'), now(), now());

-- ============================================================
-- GROUP MEMBERS
-- ============================================================
INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'MEMBER', now() FROM groups_table g CROSS JOIN users u WHERE u.email IN ('dhia.jaidi@enicar.ucar.tn', 'amine.zouari@enicar.ucar.tn');

-- ============================================================
-- POSTS
-- ============================================================
INSERT INTO posts (author_id, body, visibility, hashtags, moderated, created_at, updated_at)
VALUES
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.ucar.tn'), 'Bienvenue sur ENICAR Connect ! #ENICAR', 'PUBLIC', 'ENICAR', false, now(), now()),
((SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'), 'Offre de stage chez Vermeg ! #Stage', 'PUBLIC', 'Stage', false, now(), now());

-- ============================================================
-- EVENTS
-- ============================================================
INSERT INTO events (title, date, time, location, description, category, organizer, color, max_capacity, owner_id, created_at)
VALUES
('Hackathon ENICAR 2026', '2026-05-20', '09:00', 'Amphi A', '48h de code.', 'Hackathon', 'Club Info', '#6366f1', 150, (SELECT id FROM users WHERE email = 'karim.benali@enicar.ucar.tn'), now()),
('Conference IA', '2026-05-15', '14:00', 'Salle B2', 'LLMs et Applications.', 'Conference', 'Dr. Sonia Mrad', '#8b5cf6', 80, (SELECT id FROM users WHERE email = 'sonia.mrad@enicar.ucar.tn'), now());

-- ============================================================
-- JOB OFFERS
-- ============================================================
INSERT INTO job_offers (title, company, location, type, description, tags, author_id, created_at, updated_at)
VALUES
('Stage PFE - Java/Angular', 'Vermeg', 'Tunis', 'Stage', 'Full-stack development.', 'Java,Angular', (SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'), now(), now()),
('Ingenieur Data', 'Orange', 'Tunis', 'CDI', 'Data pipelines.', 'Python,Spark', (SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'), now(), now());

-- ============================================================
-- RESOURCE FILES
-- ============================================================
INSERT INTO resource_files (title, author_id, file_size, category, file_path, icon, upload_date)
VALUES
('Cours Deep Learning', (SELECT id FROM users WHERE email = 'sonia.mrad@enicar.ucar.tn'), '4.2 MB', 'Cours', '/uploads/dl.pdf', 'file-pdf', now()),
('TP Securite Reseau', (SELECT id FROM users WHERE email = 'mohamed.jerbi@enicar.ucar.tn'), '1.8 MB', 'TP', '/uploads/tp.pdf', 'file-pdf', now());

-- ============================================================
-- GRADES
-- ============================================================
INSERT INTO grades (student_id, subject, grade_value, semester, academic_year)
VALUES
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.ucar.tn'), 'Architecture Logicielle', 16.5, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.ucar.tn'), 'Bases de Donnees', 15.0, 1, '2025-2026');

-- ============================================================
-- CHAT MESSAGES
-- ============================================================
INSERT INTO chat_messages (sender_id, recipient_id, content, is_read, timestamp)
VALUES
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.ucar.tn'), (SELECT id FROM users WHERE email = 'amine.zouari@enicar.ucar.tn'), 'Salut Amine, ça va ?', false, now()),
((SELECT id FROM users WHERE email = 'amine.zouari@enicar.ucar.tn'), (SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.ucar.tn'), 'Ça va super et toi ?', false, now());

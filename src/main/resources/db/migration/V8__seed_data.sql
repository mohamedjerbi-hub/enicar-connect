-- V8__seed_data.sql
-- Seed data for ENICAR Connect (mirrors H2 dev data)
-- All passwords are BCrypt of "password123"

-- ============================================================
-- USERS
-- ============================================================
INSERT INTO users (first_name, last_name, email, password, phone, bio, role, department, level, class_name, avatar_color, avatar_bg, created_at, updated_at)
VALUES
('Karim',   'Ben Ali',    'karim.benali@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 71 000 001', 'Directeur general de l''ENICAR.', 'DIRECTION', 'Direction', NULL, NULL, '#6366f1', '#eef2ff', now(), now()),

('Amel',    'Chaabane',   'amel.chaabane@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 71 000 002', 'Responsable administrative et financiere.', 'ADMIN_STAFF', 'Administration', NULL, NULL, '#ec4899', '#fdf2f8', now(), now()),

('Mohamed', 'Jerbi',      'mohamed.jerbi@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 71 000 003', 'Enseignant en Genie Logiciel et Architecture Microservices.', 'TEACHER', 'Informatique', NULL, NULL, '#0ea5e9', '#f0f9ff', now(), now()),

('Sonia',   'Mrad',       'sonia.mrad@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 71 000 004', 'Enseignante en Intelligence Artificielle et Data Science.', 'TEACHER', 'Informatique', NULL, NULL, '#8b5cf6', '#f5f3ff', now(), now()),

('Hedi',    'Gharbi',     'hedi.gharbi@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 71 000 005', 'Enseignant en Reseaux et Systemes Embarques.', 'TEACHER', 'Reseaux', NULL, NULL, '#f59e0b', '#fffbeb', now(), now()),

('Dhia',    'Jaidi',      'dhia.jaidi@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 55 123 456', 'Etudiant 3eme annee Genie Logiciel. Passionne par le developpement web et mobile.', 'STUDENT', 'Informatique', '3', 'GL3-A', '#10b981', '#ecfdf5', now(), now()),

('Rania',   'Souissi',    'rania.souissi@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 55 234 567', 'Etudiante 2eme annee Systemes Embarques. Fan de robotique et d''electronique.', 'STUDENT', 'Systemes Embarques', '2', 'SE2-B', '#f43f5e', '#fff1f2', now(), now()),

('Yassine', 'Hamdi',      'yassine.hamdi@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 55 345 678', 'Etudiant 1ere annee Cycle Ingenieur. Curieux de tout, surtout la cybersecurite.', 'STUDENT', 'Informatique', '1', 'Info1-C', '#06b6d4', '#ecfeff', now(), now()),

('Mariem',  'Trabelsi',   'mariem.trabelsi@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 55 456 789', 'Etudiante 3eme annee Genie Industriel. Interessee par l''optimisation et l''IA.', 'STUDENT', 'Genie Industriel', '3', 'GI3-A', '#d97706', '#fffbeb', now(), now()),

('Amine',   'Zouari',     'amine.zouari@enicar.u-carthage.tn',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 55 567 890', 'Etudiant 2eme annee Genie Logiciel. Developpeur React et Spring passionne.', 'STUDENT', 'Informatique', '2', 'GL2-A', '#7c3aed', '#f5f3ff', now(), now()),

('Sami',    'Bouaziz',    'sami.bouaziz@gmail.com',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 50 111 222', 'Ingenieur Software @ Vermeg. Diplome ENICAR 2022. Mentor benevole.', 'ALUMNI', 'Informatique', NULL, NULL, '#059669', '#ecfdf5', now(), now()),

('Nour',    'Sfaxi',      'nour.sfaxi@gmail.com',
 '$2a$10$D9OKzfV8fHRvPQVxbmw6T.a1gOJTXx9LfZVQAqjZOt5XbSmD8uf5K',
 '+216 50 333 444', 'Data Engineer @ Orange Tunisie. Diplomee ENICAR 2021. Passionnee de Python et Spark.', 'ALUMNI', 'Informatique', NULL, NULL, '#be185d', '#fdf2f8', now(), now());

-- ============================================================
-- GROUPS
-- ============================================================
INSERT INTO groups_table (name, description, group_type, privacy, icon, icon_color, banner_gradient, creator_id, created_at, updated_at)
VALUES
('Club IA & Data Science',
 'Espace dedie a l''intelligence artificielle, machine learning et data science a l''ENICAR.',
 'THEMATIC', 'PUBLIC', 'brain', '#8b5cf6', 'linear-gradient(135deg,#8b5cf6,#ec4899)',
 (SELECT id FROM users WHERE email = 'sonia.mrad@enicar.u-carthage.tn'), now(), now()),

('Developpement Web & Mobile',
 'Communaute des developpeurs Angular, React, Flutter, Spring Boot.',
 'THEMATIC', 'PUBLIC', 'code', '#0ea5e9', 'linear-gradient(135deg,#0ea5e9,#06b6d4)',
 (SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'), now(), now()),

('Cybersecurite ENICAR',
 'Groupe dedie a la securite informatique, CTF, pentesting et bonnes pratiques securite.',
 'THEMATIC', 'PUBLIC', 'shield', '#ef4444', 'linear-gradient(135deg,#ef4444,#f97316)',
 (SELECT id FROM users WHERE email = 'hedi.gharbi@enicar.u-carthage.tn'), now(), now()),

('Alumni Network',
 'Reseau des anciens diplomes de l''ENICAR. Offres d''emploi, mentorat et actualites.',
 'ALUMNI', 'PUBLIC', 'graduation-cap', '#059669', 'linear-gradient(135deg,#059669,#0ea5e9)',
 (SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'), now(), now());

-- ============================================================
-- GROUP MEMBERS (explicit inserts, no dynamic CASE subquery)
-- ============================================================
-- Club IA: creator = sonia.mrad (ADMIN), others = MEMBER
INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'ADMIN', now()
FROM groups_table g, users u
WHERE g.name = 'Club IA & Data Science'
  AND u.email = 'sonia.mrad@enicar.u-carthage.tn'
ON CONFLICT ON CONSTRAINT uk_group_member DO NOTHING;

INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'MEMBER', now()
FROM groups_table g, users u
WHERE g.name = 'Club IA & Data Science'
  AND u.email IN ('dhia.jaidi@enicar.u-carthage.tn', 'amine.zouari@enicar.u-carthage.tn',
                  'mariem.trabelsi@enicar.u-carthage.tn', 'nour.sfaxi@gmail.com')
ON CONFLICT ON CONSTRAINT uk_group_member DO NOTHING;

-- Dev Web: creator = dhia.jaidi (ADMIN), others = MEMBER
INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'ADMIN', now()
FROM groups_table g, users u
WHERE g.name = 'Developpement Web & Mobile'
  AND u.email = 'dhia.jaidi@enicar.u-carthage.tn'
ON CONFLICT ON CONSTRAINT uk_group_member DO NOTHING;

INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'MEMBER', now()
FROM groups_table g, users u
WHERE g.name = 'Developpement Web & Mobile'
  AND u.email IN ('amine.zouari@enicar.u-carthage.tn', 'sami.bouaziz@gmail.com',
                  'rania.souissi@enicar.u-carthage.tn', 'yassine.hamdi@enicar.u-carthage.tn')
ON CONFLICT ON CONSTRAINT uk_group_member DO NOTHING;

-- Cybersec: creator = hedi.gharbi (ADMIN)
INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'ADMIN', now()
FROM groups_table g, users u
WHERE g.name = 'Cybersecurite ENICAR'
  AND u.email = 'hedi.gharbi@enicar.u-carthage.tn'
ON CONFLICT ON CONSTRAINT uk_group_member DO NOTHING;

INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'MEMBER', now()
FROM groups_table g, users u
WHERE g.name = 'Cybersecurite ENICAR'
  AND u.email IN ('yassine.hamdi@enicar.u-carthage.tn', 'dhia.jaidi@enicar.u-carthage.tn')
ON CONFLICT ON CONSTRAINT uk_group_member DO NOTHING;

-- Alumni Network: creator = sami.bouaziz (ADMIN)
INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'ADMIN', now()
FROM groups_table g, users u
WHERE g.name = 'Alumni Network'
  AND u.email = 'sami.bouaziz@gmail.com'
ON CONFLICT ON CONSTRAINT uk_group_member DO NOTHING;

INSERT INTO group_members (group_id, user_id, member_role, joined_at)
SELECT g.id, u.id, 'MEMBER', now()
FROM groups_table g, users u
WHERE g.name = 'Alumni Network'
  AND u.email IN ('nour.sfaxi@gmail.com', 'dhia.jaidi@enicar.u-carthage.tn',
                  'amine.zouari@enicar.u-carthage.tn')
ON CONFLICT ON CONSTRAINT uk_group_member DO NOTHING;

-- ============================================================
-- POSTS
-- ============================================================
INSERT INTO posts (author_id, body, visibility, hashtags, moderated, created_at, updated_at)
VALUES
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'),
 'Bienvenue sur ENICAR Connect ! La plateforme officielle de la communaute ENICAR est en ligne. Partagez vos projets, trouvez des stages et connectez-vous avec vos camarades et alumni ! #ENICARConnect #Bienvenue',
 'PUBLIC', 'ENICARConnect,Bienvenue', false, now() - interval '3 days', now() - interval '3 days'),

((SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'),
 'Offre de stage chez Vermeg ! Nous recherchons des etudiants ENICAR en 3eme annee pour un stage PFE en developpement Java/Spring. DM moi pour plus d''infos ! #Stage #PFE #Java',
 'PUBLIC', 'Stage,PFE,Java', false, now() - interval '2 days', now() - interval '2 days'),

((SELECT id FROM users WHERE email = 'sonia.mrad@enicar.u-carthage.tn'),
 'Rappel : le cours de Deep Learning du jeudi est annule cette semaine. Un cours de rattrapage sera organise la semaine prochaine. #DL #ENICAR',
 'PUBLIC', 'DL,ENICAR', false, now() - interval '1 day', now() - interval '1 day'),

((SELECT id FROM users WHERE email = 'nour.sfaxi@gmail.com'),
 'Retour d''experience : mon parcours de Data Engineer apres 3 ans a l''ENICAR. Les competences cles : Python, SQL, Spark. N''hesitez pas a me contacter pour des conseils. #DataEngineering #Alumni',
 'PUBLIC', 'DataEngineering,Alumni,Career', false, now() - interval '12 hours', now() - interval '12 hours'),

((SELECT id FROM users WHERE email = 'yassine.hamdi@enicar.u-carthage.tn'),
 'Quelqu''un connait de bonnes ressources pour commencer la cybersecurite ? Je veux preparer la certification CEH. #Cybersecurite #CEH',
 'PUBLIC', 'Cybersecurite,CEH,Debutant', false, now() - interval '6 hours', now() - interval '6 hours'),

((SELECT id FROM users WHERE email = 'amine.zouari@enicar.u-carthage.tn'),
 'Je viens de terminer mon projet perso : une app de gestion de notes avec React + Spring Boot + PostgreSQL, deployee sur Railway. #React #SpringBoot #SideProject',
 'PUBLIC', 'React,SpringBoot,SideProject', false, now() - interval '2 hours', now() - interval '2 hours');

-- ============================================================
-- COMMENTS (on first post)
-- ============================================================
INSERT INTO comments (post_id, author_id, text, created_at)
VALUES
((SELECT id FROM posts WHERE author_id = (SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn') LIMIT 1),
 (SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'),
 'Bravo Dhia pour ce travail ! ENICAR Connect va changer la donne', now() - interval '2 days'),

((SELECT id FROM posts WHERE author_id = (SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn') LIMIT 1),
 (SELECT id FROM users WHERE email = 'rania.souissi@enicar.u-carthage.tn'),
 'Trop cool ! Je me suis inscrite direct', now() - interval '2 days'),

((SELECT id FROM posts WHERE author_id = (SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn') LIMIT 1),
 (SELECT id FROM users WHERE email = 'amine.zouari@enicar.u-carthage.tn'),
 'Super initiative, hâte de voir la plateforme evoluer !', now() - interval '2 days');

-- ============================================================
-- EVENTS
-- ============================================================
INSERT INTO events (title, date, time, location, description, category, organizer, color, max_capacity, owner_id, created_at)
VALUES
('Hackathon ENICAR 2026',
 '2026-05-20', '09:00', 'Amphi A - ENICAR Tunis',
 '48h de code pour resoudre des problemes reels proposes par des entreprises partenaires. Prix : stages et cadeaux tech !',
 'Hackathon', 'Club Informatique ENICAR', '#6366f1', 150,
 (SELECT id FROM users WHERE email = 'karim.benali@enicar.u-carthage.tn'), now()),

('Conference IA : LLMs et Applications',
 '2026-05-15', '14:00', 'Salle de Conferences B2',
 'Presentation des grands modeles de langage (LLMs), leurs usages industriels et les opportunites de recherche.',
 'Conference', 'Dr. Sonia Mrad', '#8b5cf6', 80,
 (SELECT id FROM users WHERE email = 'sonia.mrad@enicar.u-carthage.tn'), now()),

('Atelier Cybersecurite : CTF pour debutants',
 '2026-05-10', '10:00', 'Labo Reseaux - Bloc C',
 'Initiation aux Capture The Flag (CTF). Aucun prerequis necessaire. Venez avec votre laptop !',
 'Atelier', 'Club Cybersecurite ENICAR', '#ef4444', 30,
 (SELECT id FROM users WHERE email = 'hedi.gharbi@enicar.u-carthage.tn'), now()),

('Forum Emploi & Stages ENICAR',
 '2026-06-05', '09:00', 'Hall Principal - ENICAR',
 'Rencontrez les recruteurs de 20+ entreprises tunisiennes et internationales. CV obligatoire.',
 'Forum', 'Direction ENICAR', '#f59e0b', 500,
 (SELECT id FROM users WHERE email = 'karim.benali@enicar.u-carthage.tn'), now());

-- ============================================================
-- JOB OFFERS
-- ============================================================
INSERT INTO job_offers (title, company, location, type, description, tags, author_id, created_at, updated_at)
VALUES
('Stage PFE - Developpeur Full-Stack Java/Angular',
 'Vermeg', 'Tunis, Tunisie (Hybride)', 'Stage',
 'Rejoignez l''equipe produit de Vermeg pour developper des fonctionnalites sur notre plateforme FinTech. Stack : Java 17, Spring Boot, Angular 16, PostgreSQL. Duree : 6 mois.',
 'Java,Spring Boot,Angular,FinTech,PFE',
 (SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'), now(), now()),

('Ingenieur Data - CDI',
 'Orange Tunisie', 'Tunis, Tunisie', 'CDI',
 'Rejoindre l''equipe Data pour construire et maintenir des pipelines de donnees a grande echelle. Competences requises : Python, Apache Spark, Kafka, SQL.',
 'Python,Spark,Kafka,Data Engineering,CDI',
 (SELECT id FROM users WHERE email = 'nour.sfaxi@gmail.com'), now(), now()),

('Stage - Developpeur Mobile Flutter',
 'Sofrecom Tunisie', 'Lac 2, Tunis', 'Stage',
 'Developpement d''une application mobile cross-platform avec Flutter. Integration d''APIs REST Spring Boot. Duree : 4 mois.',
 'Flutter,Dart,Mobile,API REST',
 (SELECT id FROM users WHERE email = 'mohamed.jerbi@enicar.u-carthage.tn'), now(), now()),

('Alternance - Cybersecurite',
 'Telnet Holding', 'Ariana, Tunisie', 'Alternance',
 'Participer aux audits de securite, tests d''intrusion et mise en place de politiques de securite. Profil : etudiant en cybersecurite, connaissance Linux, reseaux.',
 'Cybersecurite,Pentest,Linux,Audit',
 (SELECT id FROM users WHERE email = 'hedi.gharbi@enicar.u-carthage.tn'), now(), now());

-- ============================================================
-- MENTORSHIP REQUESTS
-- ============================================================
INSERT INTO mentorship_requests (mentor_id, mentee_id, objective, status, created_at)
VALUES
((SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'),
 (SELECT id FROM users WHERE email = 'amine.zouari@enicar.u-carthage.tn'),
 'Je souhaite etre guide sur mon parcours de developpeur Full-Stack Java/React et preparer mon PFE.',
 'ACCEPTED', now() - interval '5 days'),

((SELECT id FROM users WHERE email = 'nour.sfaxi@gmail.com'),
 (SELECT id FROM users WHERE email = 'mariem.trabelsi@enicar.u-carthage.tn'),
 'Je veux me specialiser en Data Engineering et comprendre le marche de l''emploi en data.',
 'PENDING', now() - interval '2 days'),

((SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'),
 (SELECT id FROM users WHERE email = 'yassine.hamdi@enicar.u-carthage.tn'),
 'Je debute en cybersecurite et j''aurais besoin de conseils sur les certifications et ressources.',
 'PENDING', now() - interval '1 day');

-- ============================================================
-- RESOURCE FILES
-- ============================================================
INSERT INTO resource_files (title, author_id, file_size, category, file_path, icon, upload_date)
VALUES
('Cours Deep Learning - Chapitre 1 : Introduction aux RNN',
 (SELECT id FROM users WHERE email = 'sonia.mrad@enicar.u-carthage.tn'),
 '4.2 MB', 'Cours', '/uploads/dl-chap1-rnn.pdf', 'file-pdf', now()),

('TP Securite Reseau - Wireshark & Snort',
 (SELECT id FROM users WHERE email = 'hedi.gharbi@enicar.u-carthage.tn'),
 '1.8 MB', 'TP', '/uploads/tp-securite-reseau.pdf', 'file-pdf', now()),

('Projet Spring Boot - Architecture Microservices',
 (SELECT id FROM users WHERE email = 'amine.zouari@enicar.u-carthage.tn'),
 '22 KB', 'Projet', 'https://github.com/amine-zouari/microservices-demo', 'link', now()),

('Guide Preparation CEH - Ressources et roadmap',
 (SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'),
 '890 KB', 'Guide', '/uploads/guide-ceh-preparation.pdf', 'file-pdf', now());

-- ============================================================
-- GRADES
-- ============================================================
INSERT INTO grades (student_id, subject, grade_value, semester, academic_year)
VALUES
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'), 'Architecture Logicielle',       16.5, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'), 'Bases de Donnees Avancees',     15.0, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'), 'Developpement Web',             17.0, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'), 'Intelligence Artificielle',     14.5, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'), 'Securite des Systemes',         13.0, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'amine.zouari@enicar.u-carthage.tn'), 'Algorithmique Avancee',       14.0, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'amine.zouari@enicar.u-carthage.tn'), 'Programmation Orientee Objet', 16.0, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'rania.souissi@enicar.u-carthage.tn'), 'Systemes Embarques',         15.5, 1, '2025-2026'),
((SELECT id FROM users WHERE email = 'rania.souissi@enicar.u-carthage.tn'), 'Electronique Numerique',     13.5, 1, '2025-2026');

-- ============================================================
-- CONNECTION REQUESTS
-- ============================================================
INSERT INTO connection_requests (sender_id, receiver_id, status, timestamp)
VALUES
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'),
 (SELECT id FROM users WHERE email = 'amine.zouari@enicar.u-carthage.tn'), 'ACCEPTED', now() - interval '4 days'),
((SELECT id FROM users WHERE email = 'dhia.jaidi@enicar.u-carthage.tn'),
 (SELECT id FROM users WHERE email = 'sami.bouaziz@gmail.com'), 'ACCEPTED', now() - interval '3 days'),
((SELECT id FROM users WHERE email = 'rania.souissi@enicar.u-carthage.tn'),
 (SELECT id FROM users WHERE email = 'nour.sfaxi@gmail.com'), 'PENDING', now() - interval '1 day'),
((SELECT id FROM users WHERE email = 'yassine.hamdi@enicar.u-carthage.tn'),
 (SELECT id FROM users WHERE email = 'hedi.gharbi@enicar.u-carthage.tn'), 'PENDING', now() - interval '6 hours');

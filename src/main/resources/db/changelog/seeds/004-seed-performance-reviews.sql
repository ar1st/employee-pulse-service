--liquibase formatted sql
--changeset Aristeidis_Tsachlaris:5 context:data-seed

-- ======================================================================
-- PERFORMANCE REVIEWS
-- Purpose:
--   Seed realistic employee performance review data across 2024–2025.
--   Covers multiple quarters and all major departments/orgs.
--   (This file includes the original 1–10 reviews PLUS more 11–25.)
-- ======================================================================

INSERT INTO public.performance_reviews
(id, raw_text, comments, overall_rating, reporter_id, employee_id, review_date)
VALUES
    -- === 2025 Q3 ===
    -- Giorgos (Software Engineer, Technology Dept.) reviewed by Nikos (CEO)
    (1,
     'Quarterly review for Giorgos: good progress on backend tasks, solid collaboration.',
     'Focus on SQL tuning and code reviews.',
     4.1,
     1,
     2,
     '2025-09-15'),

    -- === 2025 Q3 ===
    -- Maria (HR Officer, IHU Administration) reviewed by Nikos
    (2,
     'Annual review for Maria: excellent stakeholder communication and process improvements.',
     'Recommend mentorship responsibilities.',
     4.5,
     1,
     3,
     '2025-08-20'),

    -- === 2025 Q3 ===
    -- Alexandros (IT Services, IHU) reviewed by Maria
    (3,
     'Mid-year review for Alexandros: strong testing strategy and Python tooling.',
     'Grow cross-team knowledge sharing.',
     4.2,
     3,
     4,
     '2025-07-10'),

    -- === 2025 Q4 ===
    -- Elena (Engineer, BestSecret) reviewed by Thomas (Ops Lead)
    (4,
     'Probation review for Elena: great start in Engineering; analytical thinking evident.',
     'Encourage deeper system design exposure.',
     4.0,
     6,
     5,
     '2025-10-01'),

    -- === 2025 Q1 ===
    -- Giorgos (Technology) second review earlier in the year
    (5,
     'Q1 review for Giorgos: completed migration to microservices.',
     'Maintain API documentation quality.',
     4.3,
     1,
     2,
     '2025-03-10'),

    -- === 2024 Q4 ===
    -- Maria (HR) earlier review from previous year
    (6,
     'Q4 2024 review for Maria: led HR digitization project successfully.',
     'Continue mentoring junior HR staff.',
     4.6,
     1,
     3,
     '2024-12-18'),

    -- === 2024 Q2 ===
    -- Nikos (CEO, SLT) reviewed by Maria for cross-evaluation
    (7,
     'Q2 2024 review for Nikos: successfully restructured SLT meetings.',
     'Focus next on strategic metrics alignment.',
     4.7,
     3,
     1,
     '2024-05-22'),

    -- === 2024 Q3 ===
    -- Thomas (Operations, BestSecret) reviewed by Elena (Engineering)
    (8,
     'Q3 2024 review for Thomas: improved shift efficiency and staff scheduling.',
     'Expand knowledge on logistics automation.',
     4.1,
     5,
     6,
     '2024-08-05'),

    -- === 2025 Q1 ===
    -- Elena (Engineering, BestSecret) reviewed by Thomas (peer feedback)
    (9,
     'Q1 2025 review for Elena: onboarding success and proactive reporting.',
     'Increase involvement in architecture discussions.',
     4.2,
     5,
     5,
     '2025-02-10'),

    -- === 2025 Q2 ===
    -- Alexandros (IT Services) reviewed again for technical growth
    (10,
     'Q2 2025 review for Alexandros: contributed to testing automation pipeline.',
     'Explore CI/CD improvements.',
     4.4,
     3,
     4,
     '2025-04-16'),

    -- === EXTRA REVIEWS (11–25) to support richer aggregates ===
    (11,'Follow-up for Nikos: board comms cadence and OKR clarity.','Delegate weekly ops standups.',4.6,3,1,'2024-10-15'),
    (12,'Giorgos Q4 prep: feature flags and resiliency tests.','Harden rollback strategy.',4.3,1,2,'2024-11-05'),
    (13,'Maria hiring cycle review: pipeline quality improved.','Document interview rubric.',4.7,1,3,'2025-01-14'),
    (14,'Alexandros SRE cooperation: observability dashboards.','Expand alert hygiene.',4.4,3,4,'2025-02-27'),
    (15,'Elena DS sprint: model evaluation & drift checks.','Automate data QA.',4.3,6,5,'2025-03-22'),
    (16,'Thomas ops: inbound logistics throughput improved.','Cross-train night shift.',4.2,5,6,'2025-04-30'),
    (17,'Nikos H1: strategic metrics & stakeholder syncs.','Increase async updates.',4.5,3,1,'2025-06-12'),
    (18,'Giorgos Q3: performance profiling & caching wins.','Share perf playbook.',4.4,1,2,'2025-07-25'),
    (19,'Maria policy rollout: onboarding automation KPIs.','Pilot survey for feedback.',4.6,1,3,'2025-09-05'),
    (20,'Alexandros tooling: CI templates & test data factories.','Tighten code owners.',4.5,3,4,'2025-11-20'),
    (21,'Thomas H2: shift forecasting and SLA adherence.','Introduce rota simulator.',4.2,5,6,'2025-08-28'),
    (22,'Elena H2: feature store adoption and governance.','Add data tests in CI.',4.4,6,5,'2025-10-18'),
    (23,'Nikos year-end: stakeholder satisfaction and OKRs.','Refine comms cadence.',4.6,3,1,'2025-12-08'),
    (24,'Giorgos year-end: platform stability and SLOs.','Codify runbooks.',4.5,1,2,'2025-12-12'),
    (25,'Maria year-end: onboarding NPS uplift.','Scale mentorship program.',4.7,1,3,'2025-12-15'),
    (26,'Q4 2025 review for Anna: contributed to backend APIs and refactoring.','Focus on database indexing and query optimization.',4.3,1,7,'2025-12-10'),
    (27,'Q4 2025 review for Kostas: strong contributions on feature flags and monitoring.','Improve documentation and knowledge sharing.',4.4,1,8,'2025-12-14');

-- ======================================================================
-- SKILL ENTRIES
-- Notes:
--   All entries are linked to the reviews above (no standalone rows).
--   Ratings vary (3.9–4.8) to create meaningful avg/min/max per period/skill.
-- ======================================================================

INSERT INTO public.skill_entries
(id, skill_id, rating, entry_date, employee_id, performance_review_id)
VALUES
    -- === PR #1 (Giorgos, 2025-09-15) ===
    (1, 30567, 4.3, '2025-09-15', 2, 1),  -- Java (coding proficiency)
    (2, 34122, 4.0, '2025-09-15', 2, 1),  -- SQL (query optimization)

    -- === PR #2 (Maria, 2025-08-20) ===
    (3, 30366, 4.7, '2025-08-20', 3, 2),  -- Communication
    (4, 35721, 4.4, '2025-08-20', 3, 2),  -- Leadership

    -- === PR #3 (Alexandros, 2025-07-10) ===
    (5, 40361, 4.2, '2025-07-10', 4, 3),  -- Python (automation & testing)
    (6, 34122, 4.1, '2025-07-10', 4, 3),  -- SQL (data validation)

    -- === PR #4 (Elena, 2025-10-01) ===
    (7, 37563, 4.3, '2025-10-01', 5, 4),  -- Data Analytics
    (8, 30366, 4.2, '2025-10-01', 5, 4),  -- Communication
    (9, 30567, 3.9, '2025-10-01', 5, 4),  -- Java (core logic)

    -- === PR #5 (Giorgos, 2025-03-10) ===
    (10, 30567, 4.4, '2025-03-10', 2, 5), -- Java (API migration)
    (11, 34122, 4.2, '2025-03-10', 2, 5), -- SQL (schema tuning)

    -- === PR #6 (Maria, 2024-12-18) ===
    (12, 30366, 4.8, '2024-12-18', 3, 6), -- Communication (mentoring)
    (13, 35721, 4.6, '2024-12-18', 3, 6), -- Leadership (team guidance)

    -- === PR #7 (Nikos, 2024-05-22) ===
    (14, 35721, 4.7, '2024-05-22', 1, 7), -- Leadership (strategy)
    (15, 30366, 4.5, '2024-05-22', 1, 7), -- Communication (executive alignment)

    -- === PR #8 (Thomas, 2024-08-05) ===
    (16, 37563, 4.0, '2024-08-05', 6, 8), -- Data Analytics (warehouse ops)
    (17, 30366, 4.1, '2024-08-05', 6, 8), -- Communication (shift reports)

    -- === PR #9 (Elena, 2025-02-10) ===
    (18, 37563, 4.1, '2025-02-10', 5, 9), -- Data Analytics (model tracking)
    (19, 40361, 4.3, '2025-02-10', 5, 9), -- Python (ETL pipelines)

    -- === PR #10 (Alexandros, 2025-04-16) ===
    (20, 40361, 4.5, '2025-04-16', 4, 10), -- Python (CI/CD improvements)
    (21, 34122, 4.2, '2025-04-16', 4, 10), -- SQL (test data setup)

    -- === PR #11 (Nikos, 2024-10-15) ===
    (22, 35721, 4.6, '2024-10-15', 1, 11),
    (23, 30366, 4.5, '2024-10-15', 1, 11),
    (24, 34122, 4.2, '2024-10-15', 1, 11),
    (25, 30567, 4.1, '2024-10-15', 1, 11),

    -- === PR #12 (Giorgos, 2024-11-05) ===
    (26, 30567, 4.4, '2024-11-05', 2, 12),
    (27, 34122, 4.2, '2024-11-05', 2, 12),
    (28, 40361, 4.3, '2024-11-05', 2, 12),
    (29, 30366, 4.2, '2024-11-05', 2, 12),

    -- === PR #13 (Maria, 2025-01-14) ===
    (30, 30366, 4.8, '2025-01-14', 3, 13),
    (31, 35721, 4.6, '2025-01-14', 3, 13),
    (32, 37563, 4.4, '2025-01-14', 3, 13),
    (33, 34122, 4.3, '2025-01-14', 3, 13),

    -- === PR #14 (Alexandros, 2025-02-27) ===
    (34, 40361, 4.5, '2025-02-27', 4, 14),
    (35, 34122, 4.2, '2025-02-27', 4, 14),
    (36, 30366, 4.3, '2025-02-27', 4, 14),
    (37, 30567, 4.0, '2025-02-27', 4, 14),

    -- === PR #15 (Elena, 2025-03-22) ===
    (38, 37563, 4.3, '2025-03-22', 5, 15),
    (39, 40361, 4.2, '2025-03-22', 5, 15),
    (40, 34122, 4.2, '2025-03-22', 5, 15),
    (41, 30366, 4.3, '2025-03-22', 5, 15),

    -- === PR #16 (Thomas, 2025-04-30) ===
    (42, 37563, 4.2, '2025-04-30', 6, 16),
    (43, 30366, 4.1, '2025-04-30', 6, 16),
    (44, 34122, 4.0, '2025-04-30', 6, 16),
    (45, 40361, 4.0, '2025-04-30', 6, 16),

    -- === PR #17 (Nikos, 2025-06-12) ===
    (46, 35721, 4.6, '2025-06-12', 1, 17),
    (47, 30366, 4.4, '2025-06-12', 1, 17),
    (48, 37563, 4.3, '2025-06-12', 1, 17),
    (49, 34122, 4.2, '2025-06-12', 1, 17),

    -- === PR #18 (Giorgos, 2025-07-25) ===
    (50, 30567, 4.5, '2025-07-25', 2, 18),
    (51, 34122, 4.3, '2025-07-25', 2, 18),
    (52, 40361, 4.2, '2025-07-25', 2, 18),
    (53, 30366, 4.3, '2025-07-25', 2, 18),

    -- === PR #19 (Maria, 2025-09-05) ===
    (54, 30366, 4.8, '2025-09-05', 3, 19),
    (55, 35721, 4.6, '2025-09-05', 3, 19),
    (56, 37563, 4.4, '2025-09-05', 3, 19),
    (57, 34122, 4.3, '2025-09-05', 3, 19),

    -- === PR #20 (Alexandros, 2025-11-20) ===
    (58, 40361, 4.6, '2025-11-20', 4, 20),
    (59, 34122, 4.3, '2025-11-20', 4, 20),
    (60, 30567, 4.2, '2025-11-20', 4, 20),
    (61, 30366, 4.3, '2025-11-20', 4, 20),

    -- === PR #21 (Thomas, 2025-08-28) ===
    (62, 37563, 4.2, '2025-08-28', 6, 21),
    (63, 30366, 4.1, '2025-08-28', 6, 21),
    (64, 34122, 4.1, '2025-08-28', 6, 21),
    (65, 35721, 4.2, '2025-08-28', 6, 21),

    -- === PR #22 (Elena, 2025-10-18) ===
    (66, 37563, 4.4, '2025-10-18', 5, 22),
    (67, 40361, 4.3, '2025-10-18', 5, 22),
    (68, 34122, 4.2, '2025-10-18', 5, 22),
    (69, 30366, 4.3, '2025-10-18', 5, 22),

    -- === PR #23 (Nikos, 2025-12-08) ===
    (70, 35721, 4.7, '2025-12-08', 1, 23),
    (71, 30366, 4.6, '2025-12-08', 1, 23),
    (72, 37563, 4.4, '2025-12-08', 1, 23),
    (73, 34122, 4.3, '2025-12-08', 1, 23),

    -- === PR #24 (Giorgos, 2025-12-12) ===
    (74, 30567, 4.6, '2025-12-12', 2, 24),
    (75, 34122, 4.4, '2025-12-12', 2, 24),
    (76, 40361, 4.3, '2025-12-12', 2, 24),
    (77, 30366, 4.4, '2025-12-12', 2, 24),

    -- === PR #25 (Maria, 2025-12-15) ===
    (78, 30366, 4.8, '2025-12-15', 3, 25),
    (79, 35721, 4.7, '2025-12-15', 3, 25),
    (80, 37563, 4.5, '2025-12-15', 3, 25),
    (81, 34122, 4.4, '2025-12-15', 3, 25),
    (82, 30567, 4.4, '2025-12-10', 7, 26),
    (83, 34122, 4.2, '2025-12-10', 7, 26),
    (84, 30366, 4.5, '2025-12-10', 7, 26),
    (85, 37563, 4.3, '2025-12-10', 7, 26),
    (86, 30567, 4.5, '2025-12-14', 8, 27),
    (87, 34122, 4.3, '2025-12-14', 8, 27),
    (88, 30366, 4.4, '2025-12-14', 8, 27),
    (89, 37563, 4.2, '2025-12-14', 8, 27);

-- ======================================================================
-- SEQUENCE UPDATES
-- ======================================================================
ALTER SEQUENCE IF EXISTS public.performance_reviews_seq RESTART WITH 28;
ALTER SEQUENCE IF EXISTS public.skill_entries_seq RESTART WITH 90;

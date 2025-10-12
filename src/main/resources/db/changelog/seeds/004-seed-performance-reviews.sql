--liquibase formatted sql

--changeset Aristeidis_Tsachlaris:5

-- Performance Reviews
INSERT INTO public.performance_reviews
(id, raw_text, comments, overall_rating, reporter_id, employee_id, review_date)
VALUES
    (1,
     'Quarterly review for Giorgos: good progress on backend tasks, solid collaboration.',
     'Focus on SQL tuning and code reviews.',
     4.1,
     1,    -- reporter: Nikos (CEO)
     2,    -- refersTo: Giorgos (Software Engineer)
     '2025-09-15'),

    (2,
     'Annual review for Maria: excellent stakeholder communication and process improvements.',
     'Recommend mentorship responsibilities.',
     4.5,
     1,    -- reporter: Nikos
     3,    -- refersTo: Maria (HR Officer)
     '2025-08-20'),

    (3,
     'Mid-year review for Alexandros: strong testing strategy and Python tooling.',
     'Grow cross-team knowledge sharing.',
     4.2,
     3,    -- reporter: Maria
     4,    -- refersTo: Alexandros (SET)
     '2025-07-10'),

    (4,
     'Probation review for Elena: great start in Engineering; analytical thinking evident.',
     'Encourage deeper system design exposure.',
     4.0,
     6,    -- reporter: Thomas
     5,    -- refersTo: Elena (Data Analyst)
     '2025-10-01');

-- Skill entries linked to the above performance reviews
-- Note: employee_id matches the reviewed employee, and performance_review_id links to the PR above.

INSERT INTO public.skill_entries
(id, skill_id, rating, entry_date, employee_id, performance_review_id)
VALUES
    -- PR #1 (Giorgos, employee_id=2)
    (1, 30567, 4.3, '2025-09-15', 2, 1),  -- Java (computer programming)
    (2, 34122, 4.0, '2025-09-15', 2, 1),  -- SQL

    -- PR #2 (Maria, employee_id=3)
    (3, 30366, 4.7, '2025-08-20', 3, 2),  -- communication
    (4, 35721, 4.4, '2025-08-20', 3, 2),  -- lead others

    -- PR #3 (Alexandros, employee_id=4)
    (5, 40361, 4.2, '2025-07-10', 4, 3),  -- Python (computer programming)
    (6, 34122, 4.1, '2025-07-10', 4, 3),  -- SQL

    -- PR #4 (Elena, employee_id=5)
    (7, 37563, 4.3, '2025-10-01', 5, 4),  -- data analytics
    (8, 30366, 4.2, '2025-10-01', 5, 4),  -- communication
    (9, 30567, 3.9, '2025-10-01', 5, 4);  -- Java (computer programming)

ALTER SEQUENCE IF EXISTS public.performance_reviews_seq RESTART WITH 5;

ALTER SEQUENCE IF EXISTS public.skill_entries_seq RESTART WITH 10;
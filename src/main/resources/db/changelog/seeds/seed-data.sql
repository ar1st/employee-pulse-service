--liquibase formatted sql

--changeset Aristeidis Tsachlaris:2

INSERT INTO public.organizations (id, "location", "name")
VALUES (1, 'Thessaloniki', 'University of Macedonia'),
       (2, 'Thessaloniki', 'International Hellenic University'),
       (3, 'Munich', 'BestSecret');

INSERT INTO public.departments (id, manager_id, organization_id, "name")
VALUES (1, NULL, 1, 'HR'),
       (2, NULL, 1, 'Marketing'),
       (3, NULL, 1, 'Supply'),
       (4, NULL, 1, 'Senior Leadership Team'),
       (5, NULL, 1, 'Technology'),
       (6, NULL, 2, 'Administration'),
       (7, NULL, 2, 'IT Services'),
       (8, NULL, 3, 'Engineering'),
       (9, NULL, 3, 'Operations');

INSERT INTO public.skills (id, description, esco_id, "name")
VALUES (1, 'Software development with Java & Spring.', '19a8293b-8e95-4de3-983f-77484079c389', 'Java'),
       (2, 'Relational database querying and optimization.', '598de5b0-5b58-4ea7-8058-a4bc4d18c742', 'SQL'),
       (3, 'Software development with Python.', 'ccd0a1d9-afda-43d9-b901-96344886e14d', 'Python'),
       (4, 'Clear written and verbal communication.', '15d76317-c71a-4fa2-aadc-2ecc34e627b7', 'Communication'),
       (5, 'People leadership and decision-making.', 'esco-leadership-001', 'Leadership'),
       (6, 'Data analysis and insights.', 'esco-analytics-001', 'Analytics');

INSERT INTO public.occupations (id, description, esco_id, title)
VALUES (1, 'Software Engineer', 'ESCO-SE', 'Software Engineer'),
       (2, 'Software Engineer in Test', 'ESCO-SET', 'Software Engineer in Test'),
       (3, 'HR Officer', 'ESCO-HR', 'HR Officer'),
       (4, 'Chief Executive Officer', 'ESCO-CEO', 'CEO'),
       (5, 'Data Analyst', 'ESCO-DA', 'Data Analyst');

INSERT INTO public.employees (department_id, hire_date, id, occupation_id, organization_id, email, first_name, last_name)
VALUES (4, '2020-10-12', 1, 4, 1, 'nikos.nikas@gmail.com', 'Nikos', 'Nikas'),
       (5, '2023-10-12', 2, 1, 1, 'giorgos.giorgou@gmail.com', 'Giorgos', 'Giorgou'),
       (6, '2022-10-12', 3, 3, 2, 'maria.p@ihu.edu', 'Maria', 'Papadopoulou'),
       (7, '2024-04-12', 4, 2, 2, 'alex.i@ihu.edu', 'Alexandros', 'Ioannou'),
       (8, '2024-10-12', 5, 5, 3, 'elena.k@bestsecret.com', 'Elena', 'K.'),
       (9, '2025-02-12', 6, 1, 3, 'thomas.m@bestsecret.com', 'Thomas', 'M.');



--liquibase formatted sql

--changeset Aristeidis_Tsachlaris:4

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

INSERT INTO public.employees (department_id, hire_date, id, occupation_id, organization_id, email, first_name, last_name)
VALUES (4, '2020-10-12', 1, 33553, 1, 'nikos.nikas@gmail.com', 'Nikos', 'Nikas'),
       (5, '2023-10-12', 2, 35401, 1, 'giorgos.giorgou@gmail.com', 'Giorgos', 'Giorgou'),
       (6, '2022-10-12', 3, 34789, 2, 'maria.p@ihu.edu', 'Maria', 'Papadopoulou'),
       (7, '2024-04-12', 4, 35401, 2, 'alex.i@ihu.edu', 'Alexandros', 'Ioannou'),
       (8, '2024-10-12', 5, 32627, 3, 'elena.k@bestsecret.com', 'Elena', 'K.'),
       (9, '2025-02-12', 6, 35401, 3, 'thomas.m@bestsecret.com', 'Thomas', 'M.');


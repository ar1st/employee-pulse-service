--liquibase formatted sql

--changeset Aristeidis_Tsachlaris:6

CREATE OR REPLACE VIEW v_skill_entries AS
SELECT
    skill_entries.id AS skill_entry_id,

    skill_entries.employee_id,
    employees.first_name,
    employees.last_name,

    skill_entries.entry_date::date AS entry_date,

        employees.department_id,
    departments.name AS department_name,

    employees.organization_id,
    organizations.name AS organization_name,

    skill_entries.skill_id,
    skills.name AS skill_name,
    skills.description AS skill_description,

    skill_entries.rating

FROM skill_entries
         JOIN employees      ON employees.id = skill_entries.employee_id
         JOIN departments    ON departments.id = employees.department_id
         JOIN organizations  ON organizations.id = employees.organization_id
         JOIN skills         ON skills.id = skill_entries.skill_id;

CREATE OR REPLACE VIEW v_org_department_skill_period AS
SELECT
    organization_id,
    organization_name,

    department_id,
    department_name,

    employee_id,

    skill_id,
    skill_name,
    skill_description,

    entry_date,
    rating
FROM v_skill_entries;

CREATE OR REPLACE VIEW v_employee_skill_period AS
SELECT
    employee_id,
    first_name,
    last_name,

    skill_id,
    skill_name,
    skill_description,

    entry_date,
    rating
FROM v_skill_entries;

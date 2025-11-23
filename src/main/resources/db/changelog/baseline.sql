--liquibase formatted sql

--changeset Aristeidis_Tsachlaris:1
CREATE SEQUENCE if NOT EXISTS "departments_seq" AS bigint START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE SEQUENCE if NOT EXISTS "employees_seq" AS bigint START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE SEQUENCE if NOT EXISTS "occupations_seq" AS bigint START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE SEQUENCE if NOT EXISTS "organizations_seq" AS bigint START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE SEQUENCE if NOT EXISTS "performance_reviews_seq" AS bigint START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE SEQUENCE if NOT EXISTS "skill_entries_seq" AS bigint START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE SEQUENCE if NOT EXISTS "skills_seq" AS bigint START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE TABLE "departments" (
    "id"              INTEGER NOT NULL,
    "manager_id"      INTEGER,
    "organization_id" INTEGER,
    "name"            VARCHAR(255),
    CONSTRAINT "departments_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "employees" (
    "department_id"   INTEGER,
    "hire_date"       DATE,
    "id"              INTEGER NOT NULL,
    "occupation_id"   INTEGER,
    "organization_id" INTEGER,
    "email"           VARCHAR(255),
    "first_name"      VARCHAR(255),
    "last_name"       VARCHAR(255),
    CONSTRAINT "employees_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "occupations" (
    "id"          INTEGER NOT NULL,
    "description" VARCHAR(10000),
    "esco_id"     VARCHAR(255),
    "title"       VARCHAR(255),
    CONSTRAINT "occupations_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "organizations" (
    "id"       INTEGER NOT NULL,
    "location" VARCHAR(255),
    "name"     VARCHAR(255),
    CONSTRAINT "organizations_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "performance_reviews" (
    "employee_id"    INTEGER,
    "id"             INTEGER NOT NULL,
    "overall_rating" float8,
    "reporter_id"    INTEGER,
    "review_date"    DATE,
    "review_date_time" TIMESTAMP,
    "comments"       VARCHAR(255),
    "raw_text"       VARCHAR(255),
    CONSTRAINT "performance_reviews_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "skill_entries" (
    "employee_id"           INTEGER,
    "entry_date"            DATE,
    "id"                    INTEGER NOT NULL,
    "performance_review_id" INTEGER,
    "rating"                float8,
    "skill_id"              INTEGER,
    CONSTRAINT "skill_entries_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "skills" (
    "id"          INTEGER NOT NULL,
    "description" VARCHAR(10000),
    "esco_id"     VARCHAR(255),
    "name"        VARCHAR(255),
    CONSTRAINT "skills_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "skill_entries"
ADD CONSTRAINT "fk4nhorbvermp1wnlk3nrq0su1y" FOREIGN KEY ("employee_id") REFERENCES "employees" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "departments"
ADD CONSTRAINT "fk56q3esufky8u69xbmo4n63c4r" FOREIGN KEY ("manager_id") REFERENCES "employees" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "departments"
ADD CONSTRAINT "fk69kdxq27lkb5p622ypc93tcr4" FOREIGN KEY ("organization_id") REFERENCES "organizations" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "performance_reviews"
ADD CONSTRAINT "fk75f19q3rvitsw5bl5o3k0lirt" FOREIGN KEY ("employee_id") REFERENCES "employees" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "employees"
ADD CONSTRAINT "fk7x5c0jw3gxip8qidd1xla7w7y" FOREIGN KEY ("occupation_id") REFERENCES "occupations" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "skill_entries"
ADD CONSTRAINT "fke6em2g34e7ynoxeb5u0hlj9ws" FOREIGN KEY ("skill_id") REFERENCES "skills" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "employees"
ADD CONSTRAINT "fkgy4qe3dnqrm3ktd76sxp7n4c2" FOREIGN KEY ("department_id") REFERENCES "departments" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "employees"
ADD CONSTRAINT "fkh62l7gpgesex8wjd6himtb3e1" FOREIGN KEY ("organization_id") REFERENCES "organizations" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "performance_reviews"
ADD CONSTRAINT "fkhv0lu3p1ar7x8aqbta2nxr8fv" FOREIGN KEY ("reporter_id") REFERENCES "employees" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE "skill_entries"
ADD CONSTRAINT "fklfqq1qu0ynxkvl8kwglc0pt90" FOREIGN KEY ("performance_review_id") REFERENCES "performance_reviews" ("id")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


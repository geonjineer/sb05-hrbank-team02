DROP TABLE IF EXISTS "change_logs";
DROP TABLE IF EXISTS "departments";
DROP TABLE IF EXISTS "files";
DROP TABLE IF EXISTS "backups";
DROP TABLE IF EXISTS "change_log_diffs";
DROP TABLE IF EXISTS "employees";

CREATE TABLE IF NOT EXISTS "change_logs"
(
    "id"              BIGSERIAL PRIMARY KEY,
    "type"            VARCHAR(10)  NOT NULL CHECK (type IN ('CREATED', 'UPDATED', 'DELETED')),
    "employee_number" VARCHAR(100) NOT NULL,
    "memo"            VARCHAR(500) NULL,
    "ip_address"      VARCHAR(50)  NOT NULL,
    "at"              timestamptz  NOT NULL
);

CREATE TABLE IF NOT EXISTS "departments"
(
    "id"               BIGSERIAL PRIMARY KEY,
    "name"             VARCHAR(100)  NOT NULL UNIQUE,
    "description"      VARCHAR(1000) NULL,
    "established_date" DATE          NOT NULL
);

CREATE TABLE IF NOT EXISTS "files"
(
    "id"           BIGSERIAL PRIMARY KEY,
    "file_name"    VARCHAR(255) NULL,
    "content_type" VARCHAR(100) NULL,
    "size"         BIGINT       NULL
);

CREATE TABLE IF NOT EXISTS "backups"
(
    "id"         BIGSERIAL PRIMARY KEY,
    "file_id"    BIGINT      NOT NULL,
    "worker"     VARCHAR(50) NOT NULL,
    "started_at" timestamptz NOT NULL,
    "ended_at"   timestamptz NOT NULL,
    "status"     VARCHAR(20) NULL CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'SKIPPED', 'FAILED')),
    CONSTRAINT "FK_files_TO_backups_1" FOREIGN KEY ("file_id")
        REFERENCES "files" ("id")
);

CREATE TABLE IF NOT EXISTS "change_log_diffs"
(
    "id"            BIGSERIAL PRIMARY KEY,
    "change_log_id" BIGINT       NOT NULL,
    "property_name" VARCHAR(10)  NULL,
    "before"        VARCHAR(500) NULL,
    "after"         VARCHAR(500) NULL,
    CONSTRAINT "FK_change_logs_TO_change_log_diffs_1" FOREIGN KEY ("change_log_id")
        REFERENCES "change_logs" ("id") ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS "employees"
(
    "id"               BIGSERIAL PRIMARY KEY,
    "department_id"    BIGINT                       NULL,
    "profile_image_id" BIGINT                       NULL,
    "name"             VARCHAR(100)                 NOT NULL,
    "email"            VARCHAR(100)                 NOT NULL UNIQUE,
    "employee_number"  VARCHAR(100)                 NOT NULL UNIQUE,
    "position"         VARCHAR(50)                  NULL,
    "hire_date"        DATE                         NOT NULL,
    "status"           VARCHAR(10) DEFAULT 'ACTIVE' NOT NULL CHECK (status IN ('ACTIVE', 'ON_LEAVE', 'RESIGNED')),
    CONSTRAINT "FK_departments_TO_employees_1" FOREIGN KEY ("department_id")
        REFERENCES "departments" ("id") ON DELETE NO ACTION,
    CONSTRAINT "FK_files_TO_employees_1" FOREIGN KEY ("profile_image_id")
        REFERENCES "files" ("id")
);


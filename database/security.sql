-- =============================================================================
-- SQLLens Security: Least Privilege Database Configuration
-- Creates a dedicated read-only role that can ONLY execute SELECT queries.
-- Protects against unauthorized DDL or DML even if application validation is bypassed.
-- =============================================================================

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'sqlens_readonly') THEN
        CREATE ROLE sqlens_readonly WITH LOGIN;
    END IF;
END
$$;

-- Grant connection to target database
GRANT CONNECT ON DATABASE sqllens TO sqlens_readonly;

-- Grant usage on public schema
GRANT USAGE ON SCHEMA public TO sqlens_readonly;

-- Grant SELECT only on all existing tables
GRANT SELECT ON ALL TABLES IN SCHEMA public TO sqlens_readonly;

-- Ensure future tables also default to read-only for this user
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO sqlens_readonly;

-- Revoke all modifying permissions explicitly
REVOKE INSERT, UPDATE, DELETE, TRUNCATE ON ALL TABLES IN SCHEMA public FROM sqlens_readonly;

-- Additional read-only protection
ALTER ROLE sqlens_readonly
SET default_transaction_read_only = on;

-- Limit query execution time
ALTER ROLE sqlens_readonly
SET statement_timeout = '5s';

-- Restrict schema creation
REVOKE CREATE ON SCHEMA public FROM sqlens_readonly;

-- Restrict temporary table creation
REVOKE TEMPORARY ON DATABASE sqllens FROM PUBLIC;
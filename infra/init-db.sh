#!/usr/bin/env bash
set -e

DB_HOST="${IUH_PTUD_DB_HOST:-mssql}"
DB_PORT="${IUH_PTUD_DB_PORT:-1433}"
DB_USER="${IUH_PTUD_DB_USER:-sa}"
DB_PASSWORD="${IUH_PTUD_DB_PASSWORD:-YourStrong!Passw0rd}"
DB_NAME="${IUH_PTUD_DB_NAME:-iuh_ptud_db}"

echo "Waiting for MSSQL to become available at ${DB_HOST}:${DB_PORT}..."

until /opt/mssql-tools18/bin/sqlcmd -S "${DB_HOST},${DB_PORT}" -U "${DB_USER}" -P "${DB_PASSWORD}" -C -Q "SELECT 1;" > /dev/null 2>&1; do
    echo "MSSQL is not ready yet. Waiting 2 seconds..."
    sleep 2
done

echo "MSSQL is ready. Ensuring database [${DB_NAME}] exists..."

/opt/mssql-tools18/bin/sqlcmd -S "${DB_HOST},${DB_PORT}" -U "${DB_USER}" -P "${DB_PASSWORD}" -C -Q "
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'${DB_NAME}')
BEGIN
    PRINT 'Creating database [${DB_NAME}]...';
    CREATE DATABASE [${DB_NAME}];
    PRINT 'Database [${DB_NAME}] created successfully.';
END
ELSE
BEGIN
    PRINT 'Database [${DB_NAME}] already exists.';
END
"

echo "Database initialization completed successfully!"

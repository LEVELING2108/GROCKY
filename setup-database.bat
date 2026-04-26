@echo off
echo ========================================
echo Setting up GROCKY Database
echo ========================================
echo.

set PGPASSWORD=Pune@2314110523
set "PGPATH=C:\Program Files\PostgreSQL\18\bin"

echo.
echo Creating database grocky_db...
"%PGPATH%\psql.exe" -U postgres -c "CREATE DATABASE grocky_db;"

if %ERRORLEVEL% EQU 0 (
    echo [OK] Database created successfully
) else (
    echo [INFO] Database may already exist, continuing...
)

echo.
echo Loading schema...
"%PGPATH%\psql.exe" -U postgres -d grocky_db -f "database\schema.sql"

if %ERRORLEVEL% EQU 0 (
    echo [OK] Schema loaded successfully
    echo.
    echo ========================================
    echo Database Setup Complete!
    echo ========================================
) else (
    echo [ERROR] Failed to load schema
)

echo.
echo Next steps:
echo 1. Run backend: cd backend ^&^& .\mvnw.cmd spring-boot:run
echo 2. Run frontend: cd frontend ^&^& npm install ^&^& npm run dev
echo.

pause

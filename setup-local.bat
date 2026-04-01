@echo off
echo ========================================
echo   GROCKY - Local Setup Without Docker
echo ========================================
echo.

REM Check Java
echo [1/5] Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17+ from https://adoptium.net/
    pause
    exit /b 1
)
echo Java is installed - OK
echo.

REM Check Node.js
echo [2/5] Checking Node.js...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js is not installed
    echo Please install Node.js from https://nodejs.org/
    pause
    exit /b 1
)
echo Node.js is installed - OK
echo.

REM Check PostgreSQL
echo [3/5] Checking PostgreSQL...
where psql >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL 15+ from https://www.postgresql.org/download/windows/
    echo.
    echo After installation, run these commands:
    echo   createdb -U postgres grocky_db
    echo   psql -U postgres -d grocky_db -f "database\schema.sql"
    pause
    exit /b 1
)
echo PostgreSQL is installed - OK
echo.

REM Setup Database
echo [4/5] Setting up Database...
echo Creating database and running schema...
psql -U postgres -c "CREATE DATABASE grocky_db;" >nul 2>&1 || echo Database may already exist
psql -U postgres -d grocky_db -f "database\schema.sql"
if %errorlevel% neq 0 (
    echo WARNING: Database setup had issues - you can run it manually later
)
echo.

REM Install Frontend Dependencies
echo [5/5] Installing Frontend dependencies...
cd frontend
call npm install
if %errorlevel% neq 0 (
    echo ERROR: npm install failed
    cd ..
    pause
    exit /b 1
)
cd ..
echo.

echo ========================================
echo   Setup Complete!
echo ========================================
echo.
echo Next steps:
echo   1. Start Backend: cd backend ^&^& mvnw.cmd spring-boot:run
echo   2. Start Frontend: cd frontend ^&^& npm run dev
echo.
echo Or use the start-local.bat script to start both
echo.
pause

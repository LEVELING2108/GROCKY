@echo off
REM GROCKY Quick Start Script for Windows
REM This script helps you set up and run GROCKY quickly

echo ========================================
echo GROCKY - Online Grocery Store
echo Quick Start Script
echo ========================================
echo.

REM Check if Docker is installed
where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker is not installed or not in PATH
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop
    echo.
    pause
    exit /b 1
)

echo [OK] Docker is installed
echo.

REM Check if Docker is running
docker info >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker is not running
    echo Please start Docker Desktop and try again
    echo.
    pause
    exit /b 1
)

echo [OK] Docker is running
echo.

REM Ask user for startup preference
echo Choose how you want to start GROCKY:
echo.
echo 1. Start with Docker (Recommended - All services)
echo 2. Start Backend only (Manual frontend)
echo 3. Start Database only (Manual backend and frontend)
echo 4. Setup and Start (First time setup)
echo 5. Stop All Services
echo 6. View Logs
echo.

set /p choice="Enter your choice (1-6): "

if "%choice%"=="1" goto START_ALL
if "%choice%"=="2" goto START_BACKEND
if "%choice%"=="3" goto START_DATABASE
if "%choice%"=="4" goto SETUP
if "%choice%"=="5" goto STOP_ALL
if "%choice%"=="6" goto VIEW_LOGS

echo Invalid choice. Please run the script again.
pause
exit /b 1

:START_ALL
echo.
echo Starting all services with Docker Compose...
echo.
docker-compose up -d

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed to start services
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! All services started
echo ========================================
echo.
echo Access the application:
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080/api
echo   Database: localhost:5432
echo.
echo Test Login:
echo   Email: john@example.com
echo   Password: password
echo.
echo To view logs: docker-compose logs -f
echo To stop: docker-compose down
echo.
pause
exit /b 0

:START_BACKEND
echo.
echo Starting backend only...
echo.

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

echo [OK] Java is installed
java -version
echo.

REM Check if Maven wrapper exists
if not exist "backend\mvnw" (
    echo [ERROR] Maven wrapper not found
    echo Please run: mvn -N wrapper:wrapper in backend directory
    echo Or install Maven
    pause
    exit /b 1
)

cd backend
echo Starting Spring Boot backend...
call mvnw.cmd spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed to start backend
    pause
    exit /b 1
)

exit /b 0

:START_DATABASE
echo.
echo Starting PostgreSQL database...
echo.

docker-compose up -d db

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to start database
    pause
    exit /b 1
)

echo.
echo Database started successfully!
echo Connection: localhost:5432
echo Database: grocky_db
echo User: grocky_user
echo Password: grocky_password
echo.
pause
exit /b 0

:SETUP
echo.
echo Setting up GROCKY for the first time...
echo.

REM Check if Node.js is installed
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] Node.js is not installed
    echo Frontend development will require Node.js 18+
    echo Download from: https://nodejs.org/
    echo.
) else (
    echo [OK] Node.js is installed
    node -v
    echo.
)

REM Start database
echo Step 1: Starting database...
docker-compose up -d db
timeout /t 5 /nobreak >nul

echo.
echo Step 2: Database is ready
echo Schema will be automatically loaded on first run
echo.

echo Step 3: Starting all services...
docker-compose up -d

echo.
echo ========================================
echo SETUP COMPLETE!
echo ========================================
echo.
echo You can now access:
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080/api
echo.
echo Test Login:
echo   Email: john@example.com
echo   Password: password
echo.
pause
exit /b 0

:STOP_ALL
echo.
echo Stopping all services...
echo.

docker-compose down

echo.
echo All services stopped!
echo.
pause
exit /b 0

:VIEW_LOGS
echo.
echo Viewing logs...
echo.

docker-compose logs -f

exit /b 0

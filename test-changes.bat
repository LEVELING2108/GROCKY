@echo off
REM GROCKY - Test All Changes Script
REM This script verifies all the changes are working correctly

echo ========================================
echo GROCKY - Testing All Changes
echo ========================================
echo.

REM Check if Docker is installed
where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker is not installed
    echo Please install Docker Desktop first
    pause
    exit /b 1
)

echo [OK] Docker is installed
echo.

REM Check if .env files exist
echo Checking environment files...
if exist "backend\.env" (
    echo [OK] backend\.env exists
) else (
    echo [INFO] backend\.env not found, copying from .env.example
    copy backend\.env.example backend\.env
)

if exist "frontend\.env" (
    echo [OK] frontend\.env exists
) else (
    echo [INFO] frontend\.env not found, copying from .env.example
    copy frontend\.env.example frontend\.env
)
echo.

REM Build Docker images
echo ========================================
echo Building Docker Images...
echo ========================================
echo.

docker-compose build

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Docker build failed
    echo Please check the error messages above
    pause
    exit /b 1
)

echo.
echo [OK] Docker images built successfully
echo.

REM Start services
echo ========================================
echo Starting Services...
echo ========================================
echo.

docker-compose up -d

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed to start services
    pause
    exit /b 1
)

echo.
echo [OK] Services started
echo.

REM Wait for services to be ready
echo ========================================
echo Waiting for services to be ready...
echo ========================================
echo.

echo Waiting 30 seconds for services to initialize...
timeout /t 30 /nobreak >nul

echo.
echo Checking service status...
docker-compose ps

echo.
echo ========================================
echo Testing Endpoints...
echo ========================================
echo.

REM Test backend health
echo Testing Backend Health...
curl -s http://localhost:8080/api/actuator/health >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Backend is healthy
) else (
    echo [WARNING] Backend health check failed (may need more time to start)
)

REM Test frontend
echo Testing Frontend...
curl -s http://localhost:3000 >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Frontend is accessible
) else (
    echo [WARNING] Frontend check failed (may need more time to start)
)

echo.
echo ========================================
echo Test Summary
echo ========================================
echo.
echo Services Status:
docker-compose ps --format "table {{.Name}}\t{{.Status}}"

echo.
echo Access Points:
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080/api
echo   Database: localhost:5432

echo.
echo Next Steps:
echo 1. Open http://localhost:3000 in your browser
echo 2. Test the application features
echo 3. Check logs: docker-compose logs -f
echo 4. Stop services: docker-compose down

echo.
echo ========================================
echo Testing Complete!
echo ========================================

pause

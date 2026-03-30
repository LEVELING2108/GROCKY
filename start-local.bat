@echo off
echo ========================================
echo   Starting GROCKY (Without Docker)
echo ========================================
echo.

echo Starting Backend Server...
echo Backend will run at: http://localhost:8080/api
echo.
start "GROCKY Backend" cmd /k "cd backend && .\mvnw.cmd spring-boot:run"

timeout /t 15 /nobreak >nul

echo Starting Frontend Server...
echo Frontend will run at: http://localhost:5173
echo.
start "GROCKY Frontend" cmd /k "cd frontend && npm run dev"

echo.
echo ========================================
echo   Servers Starting...
echo ========================================
echo   Backend:  http://localhost:8080/api
echo   Frontend: http://localhost:5173
echo.
echo Press any key to view logs...
pause >nul

echo.
echo Opening log windows...
start "Backend Logs" cmd /k "cd backend && tail -f logs\spring-boot.log 2>nul || echo Logs will appear in the backend window"
start "Frontend Logs" cmd /k "cd frontend && echo Frontend logs appear in the frontend window"

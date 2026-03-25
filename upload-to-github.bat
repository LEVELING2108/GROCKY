@echo off
REM GROCKY GitHub Upload Script
REM This script helps you upload GROCKY to GitHub

echo ========================================
echo GROCKY - GitHub Upload Helper
echo ========================================
echo.

REM Check if Git is installed
where git >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Git is not installed or not in PATH
    echo Please install Git from: https://git-scm.com/downloads
    echo.
    pause
    exit /b 1
)

echo [OK] Git is installed
git --version
echo.

REM Check if already a git repository
if exist ".git" (
    echo [INFO] Git repository already initialized
) else (
    echo Initializing Git repository...
    git init
    echo.
)

REM Configure Git user (if not already configured)
git config user.name >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Please enter your Git username:
    set /p gitname="Username: "
    git config user.name "%gitname%"
    echo.
    
    echo Please enter your Git email:
    set /p gitemail="Email: "
    git config user.email "%gitemail%"
    echo.
)

echo ========================================
echo Step 1: Add all files to Git
echo ========================================
echo.

git add .

echo.
echo Files staged for commit:
git status --short
echo.

echo ========================================
echo Step 2: Create initial commit
echo ========================================
echo.

git commit -m "Initial commit: GROCKY - AI-Powered Online Grocery Store

Features:
- Spring Boot 3.2 backend with REST API
- React 18 + TypeScript frontend
- AI/ML: Demand forecasting, Customer segmentation, Price optimization
- Real-time order tracking with WebSocket
- Stripe payment integration
- PostgreSQL database
- Docker support
- Comprehensive documentation

Tech Stack: Java 17, Spring Boot, React, TypeScript, PostgreSQL, Docker"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [WARNING] Commit might have failed because there are no changes
    echo.
)

echo ========================================
echo Step 3: Add GitHub remote
echo ========================================
echo.

echo Please create a new repository on GitHub:
echo 1. Go to https://github.com/new
echo 2. Repository name: grocky
echo 3. Description: AI-Powered Online Grocery Store
echo 4. Public or Private (your choice)
echo 5. DO NOT initialize with README, .gitignore, or license
echo 6. Click "Create repository"
echo.

set /p repo_url="Enter your GitHub repository URL: "
echo.

REM Remove existing remote if exists
git remote remove origin >nul 2>nul

git remote add origin %repo_url%
echo.

echo Verifying remote...
git remote -v
echo.

echo ========================================
echo Step 4: Push to GitHub
echo ========================================
echo.

echo Renaming branch to main...
git branch -M main

echo.
echo Pushing to GitHub...
echo Note: You may be asked for GitHub credentials
echo Use your personal access token if prompted for password
echo.

git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! Project uploaded to GitHub
    echo ========================================
    echo.
    echo Next steps:
    echo 1. Visit your repository on GitHub
    echo 2. Add repository topics: java, spring-boot, react, ai, etc.
    echo 3. Enable GitHub Actions for CI/CD
    echo 4. Share your project!
    echo.
    echo Your repository: %repo_url%
    echo.
) else (
    echo.
    echo ========================================
    echo Upload completed with some issues
    echo ========================================
    echo.
    echo Troubleshooting:
    echo 1. Check your GitHub credentials
    echo 2. Verify the repository URL
    echo 3. Try using a personal access token
    echo 4. See GITHUB_UPLOAD.md for detailed help
    echo.
)

pause
exit /b 0

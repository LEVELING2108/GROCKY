# 📤 GitHub Upload Guide for GROCKY

This guide will help you upload your GROCKY project to GitHub.

## 🚀 Quick Upload (Command Line)

### Step 1: Create a New Repository on GitHub

1. Go to https://github.com
2. Click the "+" icon in the top right
3. Select "New repository"
4. Fill in:
   - **Repository name**: `grocky` (or your preferred name)
   - **Description**: "AI-Powered Online Grocery Store - Spring Boot + React"
   - **Visibility**: Public (recommended) or Private
   - **DO NOT** initialize with README, .gitignore, or license (we already have these)
5. Click "Create repository"

### Step 2: Initialize Git Repository (if not already done)

Open Command Prompt in your project folder:

```bash
cd "C:\Users\suman\Downloads\PERSONAL PROJECT\JAVA PROJECT\GROCKY"
```

Check if git is already initialized:
```bash
git status
```

If not initialized, run:
```bash
git init
```

### Step 3: Add All Files to Git

```bash
# Add all files
git add .

# Check what's being added
git status
```

### Step 4: Create Initial Commit

```bash
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
```

### Step 5: Link to GitHub Repository

Copy the repository URL from GitHub (it will look like):
```
https://github.com/YOUR_USERNAME/grocky.git
```

Then run:
```bash
git remote add origin https://github.com/YOUR_USERNAME/grocky.git
```

Verify the remote:
```bash
git remote -v
```

### Step 6: Push to GitHub

```bash
# Rename branch to main (if needed)
git branch -M main

# Push to GitHub
git push -u origin main
```

If you get an authentication error, you may need to authenticate with GitHub. See authentication section below.

### Step 7: Verify Upload

1. Go to your GitHub repository page
2. Refresh the page
3. You should see all your files!

## 🔐 GitHub Authentication

### Option 1: Personal Access Token (Recommended)

1. Go to GitHub Settings → Developer settings → Personal access tokens
2. Click "Generate new token (classic)"
3. Give it a name (e.g., "GROCKY Upload")
4. Select scopes: `repo`, `workflow`
5. Generate token
6. Copy the token (save it securely!)
7. When prompted for password, use this token instead

### Option 2: GitHub CLI

Install GitHub CLI: https://cli.github.com/

```bash
# Authenticate
gh auth login

# Then push normally
git push -u origin main
```

### Option 3: SSH Keys

Generate SSH key:
```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
```

Add to GitHub:
1. Copy the key: `cat ~/.ssh/id_ed25519.pub`
2. Go to GitHub Settings → SSH and GPG keys
3. Click "New SSH key"
4. Paste your key
5. Change remote URL to SSH:
   ```bash
   git remote set-url origin git@github.com:YOUR_USERNAME/grocky.git
   ```

## 📋 Pre-Upload Checklist

Before uploading, make sure:

- [ ] No sensitive data (passwords, API keys) in code
- [ ] `.gitignore` is properly configured
- [ ] All files are tracked: `git status`
- [ ] You have a good commit message
- [ ] Repository name is appropriate
- [ ] README.md is comprehensive

## 🔒 Remove Sensitive Data

Check for sensitive files:

```bash
# Search for potential secrets
git grep -i "password"
git grep -i "secret"
git grep -i "api_key"
git grep -i "token"
```

If you find any:
1. Remove or replace with environment variables
2. Add to `.gitignore`
3. Commit changes

## 📊 After Upload

### 1. Add Repository Topics

On GitHub, go to your repo settings and add topics:
- `java`
- `spring-boot`
- `react`
- `grocery`
- `ecommerce`
- `ai`
- `machine-learning`
- `postgresql`
- `docker`
- `typescript`

### 2. Enable GitHub Actions

1. Go to "Actions" tab
2. Click "I understand my workflows, go ahead and enable them"
3. CI/CD will run automatically on pushes

### 3. Add Repository Description

On the main page, click "Add a description":
```
🛒 AI-Powered Online Grocery Store | Spring Boot 3 + React 18 | 
Real-time order tracking, AI recommendations, customer segmentation, 
price optimization | Docker-ready
```

### 4. Pin the Repository

1. Go to your GitHub profile
2. Click "Customize your pins"
3. Select your grocky repository
4. Click "Save"

## 🔄 Making Updates

After initial upload, to make updates:

```bash
# Make your changes
# ...

# Stage changes
git add .

# Commit with descriptive message
git commit -m "Description of changes"

# Push to GitHub
git push origin main
```

## 📦 Large Files

If you have large files (>100MB), use Git LFS:

```bash
# Install Git LFS
git lfs install

# Track large files
git lfs track "*.jar"
git lfs track "*.zip"

# Commit and push
git add .gitattributes
git add .
git commit -m "Add large files with LFS"
git push origin main
```

## 🐛 Troubleshooting

### Error: "remote: Repository not found"
```bash
# Check remote URL
git remote -v

# If wrong, update it
git remote set-url origin https://github.com/YOUR_USERNAME/grocky.git
```

### Error: "failed to push some refs"
```bash
# This usually means remote has commits you don't have
git pull origin main --allow-unrelated-histories
git push origin main
```

### Error: "file too large"
```bash
# Remove large file from git
git reset HEAD path/to/large/file
git rm --cached path/to/large/file

# Or use Git LFS (see above)
```

### Error: "permission denied"
- Check your authentication
- Verify you have write access to the repository
- Try using a personal access token

## 📈 GitHub Pages (Optional)

To host frontend on GitHub Pages:

1. Build frontend:
   ```bash
   cd frontend
   npm run build
   ```

2. Create gh-pages branch:
   ```bash
   git checkout --orphan gh-pages
   git reset --hard
   cp -r dist/* .
   git add .
   git commit -m "Deploy to GitHub Pages"
   git push origin gh-pages --force
   ```

3. Enable in Settings → Pages → Select gh-pages branch

## 🎉 Success!

Your GROCKY project is now on GitHub! Share it with:
- Your portfolio
- LinkedIn
- Developer communities
- Potential collaborators

## 📚 Additional Resources

- [GitHub Docs](https://docs.github.com/)
- [Git Handbook](https://guides.github.com/introduction/git-handbook/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Git LFS](https://git-lfs.github.com/)

---

**Happy Coding! 🚀**

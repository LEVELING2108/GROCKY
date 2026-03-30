# 🔧 GROCKY - Local Setup Guide (Without Docker)

Complete guide to run GROCKY locally without Docker.

---

## ✅ What Was Fixed

| Issue | Status | Solution Applied |
|-------|--------|------------------|
| Missing `.env` files | ✅ Fixed | Created backend/.env and frontend/.env |
| Missing Maven wrapper | ✅ Fixed | Created mvnw.cmd and .mvn/wrapper/ |
| Frontend App.tsx import error | ✅ Fixed | Moved AdminRoutes component declaration |
| PostgreSQL not installed | ⚠️ Action Required | Install from postgresql.org |
| Maven not in PATH | ✅ Workaround | Use mvnw.cmd wrapper |

---

## 📋 Prerequisites

### 1. **Java 17+** ✅ Already Installed
- Your version: Java 22.0.2
- Status: Ready

### 2. **Node.js 18+** ✅ Already Installed
- Your version: v24.14.0
- Status: Ready

### 3. **PostgreSQL 15+** ❌ NOT INSTALLED
- **Download:** https://www.postgresql.org/download/windows/
- **During installation:**
  - Set password for `postgres` user (remember it!)
  - Keep default port: 5432

### 4. **Maven** ✅ Wrapper Created
- Use `mvnw.cmd` instead of `mvn`

---

## 🚀 Quick Start (After PostgreSQL Installed)

### Option 1: Automated Setup (Recommended)

```bash
# Double-click this file:
setup-local.bat
```

This will:
1. Check all prerequisites
2. Create database
3. Run schema.sql
4. Install frontend dependencies

### Option 2: Manual Setup

#### Step 1: Setup Database
```bash
# Open PowerShell or Command Prompt

# Create database
createdb -U postgres grocky_db

# OR using psql
psql -U postgres
CREATE DATABASE grocky_db;
\q

# Run schema
psql -U postgres -d grocky_db -f "database\schema.sql"
```

#### Step 2: Start Backend
```bash
cd backend
.\mvnw.cmd spring-boot:run
```

Wait for: `Started GroceryApplication in X seconds`

#### Step 3: Start Frontend (New Terminal)
```bash
cd frontend
npm run dev
```

Wait for: `Local: http://localhost:5173/`

---

## 🎯 Access the Application

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:5173 | john@example.com / password |
| Backend API | http://localhost:8080/api | - |
| Database | localhost:5432 | grocky_user / grocky_password |

### Test Accounts
- **Customer:** john@example.com / password
- **Admin:** admin@grocky.com / admin123

---

## 📁 Project Structure

```
GROCKY/
├── backend/
│   ├── src/main/java/com/grocky/    # 64 Java files
│   ├── src/main/resources/
│   │   └── application.yml          # Main config
│   ├── .env                         # ✅ Created
│   ├── mvnw.cmd                     # ✅ Created
│   └── pom.xml
│
├── frontend/
│   ├── src/                         # 27 TSX files
│   ├── .env                         # ✅ Created
│   ├── package.json
│   └── vite.config.ts
│
├── database/
│   └── schema.sql                   # Full schema + sample data
│
├── setup-local.bat                  # ✅ Setup script
└── start-local.bat                  # ✅ Start both servers
```

---

## 🐛 Troubleshooting

### Backend Won't Start

**Error: Database connection failed**
```bash
# Check PostgreSQL is running
pg_ctl status

# Check database exists
psql -U postgres -l | findstr grocky_db

# Restart PostgreSQL service
net stop postgresql
net start postgresql
```

**Error: Port 8080 already in use**
```bash
# Find and kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Or change port in backend/application.yml
server:
  port: 8081
```

**Error: Java version mismatch**
```bash
# Check JAVA_HOME
echo %JAVA_HOME%

# Should point to Java 17+ installation
```

### Frontend Won't Start

**Error: npm modules missing**
```bash
cd frontend
npm install
```

**Error: Port 5173 in use**
```bash
# Vite will auto-select next available port
# Or specify: npm run dev -- --port 3000
```

**Error: API connection failed**
```bash
# Check backend is running at http://localhost:8080/api
curl http://localhost:8080/api/products

# Check frontend .env has correct URL
cat frontend/.env
VITE_API_BASE_URL=http://localhost:8080/api
```

### Database Issues

**Reset database completely**
```bash
# Drop and recreate
psql -U postgres
DROP DATABASE IF EXISTS grocky_db;
CREATE DATABASE grocky_db;
\q

# Reload schema
psql -U postgres -d grocky_db -f "database\schema.sql"
```

**Check tables exist**
```bash
psql -U postgres -d grocky_db
\dt
```

Should show: customers, products, orders, order_items, payments, etc.

---

## 🔍 Verify Everything Works

### 1. Test Backend
```bash
# Products endpoint
curl http://localhost:8080/api/products

# Should return JSON with 10 products
```

### 2. Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"john@example.com\",\"password\":\"password\"}"
```

Should return JWT token.

### 3. Test Frontend
1. Open http://localhost:5173
2. Login with: john@example.com / password
3. Browse products
4. Add to cart
5. Try checkout

---

## 📊 Database Schema

### Tables Created
- customers (4 users including admin)
- products (10 sample products)
- orders (3 sample orders)
- order_items (10 items)
- payments (3 payments)
- reviews (3 reviews)
- cart (empty)
- inventory_log (empty)
- analytics (21 data points)

### Check Sample Data
```bash
psql -U postgres -d grocky_db

# Count products
SELECT COUNT(*) FROM products;  -- Should be 10

# Count customers
SELECT COUNT(*) FROM customers;  -- Should be 4

# View products
SELECT name, price, stock_quantity FROM products;
```

---

## 🛠️ Development Commands

### Backend
```bash
cd backend

# Run application
.\mvnw.cmd spring-boot:run

# Run tests
.\mvnw.cmd test

# Build JAR
.\mvnw.cmd clean package

# Skip tests
.\mvnw.cmd clean package -DskipTests
```

### Frontend
```bash
cd frontend

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Type check
npm run type-check

# Lint
npm run lint
```

---

## 📝 Configuration Files

### backend/.env
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/grocky_db
SPRING_DATASOURCE_USERNAME=grocky_user
SPRING_DATASOURCE_PASSWORD=grocky_password
JWT_SECRET=GrockySecretKeyForJWTTokenGenerationMustBeLongEnough2024ChangeInProduction
STRIPE_SECRET_KEY=sk_test_your_test_secret_key_replace_in_production
```

### frontend/.env
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_STRIPE_PUBLIC_KEY=pk_test_your_test_public_key_replace_in_production
```

---

## 🎯 Next Steps After Setup

1. **Test the application thoroughly**
   - Login/logout
   - Browse products
   - Add to cart
   - Create orders
   - Admin dashboard

2. **Customize**
   - Add your own products
   - Change branding
   - Configure real Stripe keys

3. **Deploy to production** (when ready)
   - See DEPLOYMENT.md
   - Use Docker or cloud platform

---

## 📞 Common Issues & Solutions

| Problem | Solution |
|---------|----------|
| `java: command not found` | Install Java 17+ and add to PATH |
| `npm: command not found` | Reinstall Node.js |
| `psql: command not found` | Add PostgreSQL to PATH: `C:\Program Files\PostgreSQL\15\bin` |
| `Port already in use` | Kill process or change port in config |
| `Database connection refused` | Start PostgreSQL service |
| `JWT token invalid` | Clear browser cookies/localStorage |

---

## ✅ Success Checklist

- [ ] PostgreSQL installed and running
- [ ] Database `grocky_db` created
- [ ] Schema loaded with sample data
- [ ] Backend starts without errors
- [ ] Frontend starts without errors
- [ ] Can access http://localhost:5173
- [ ] Can login with test account
- [ ] Can browse products
- [ ] Can create orders

---

**Happy Coding! 🚀**

For Docker deployment, see `DEPLOYMENT.md`

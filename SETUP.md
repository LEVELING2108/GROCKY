# GROCKY Setup Guide

## Quick Start

### Option 1: Using Docker (Recommended)

1. **Clone the repository**
```bash
cd "C:\Users\suman\Downloads\PERSONAL PROJECT\JAVA PROJECT\GROCKY"
```

2. **Start all services**
```bash
docker-compose up -d
```

3. **Access the application**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Database: localhost:5432

### Option 2: Manual Setup

## Prerequisites

Install the following software:

1. **Java 17 or higher**
   - Download from: https://adoptium.net/
   - Verify: `java -version`

2. **Node.js 18 or higher**
   - Download from: https://nodejs.org/
   - Verify: `node -v`

3. **PostgreSQL 15**
   - Download from: https://www.postgresql.org/download/
   - Or use Docker: `docker run --name postgres -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:15`

4. **Maven 3.6+** (if not using wrapper)
   - Download from: https://maven.apache.org/download.cgi
   - Or use: `sudo apt install maven` (Linux) or `brew install maven` (Mac)

## Step-by-Step Manual Setup

### 1. Database Setup

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database and user
CREATE DATABASE grocky_db;
CREATE USER grocky_user WITH PASSWORD 'grocky_password';
GRANT ALL PRIVILEGES ON DATABASE grocky_db TO grocky_user;

-- Exit psql
\q

-- Run schema
psql -U grocky_user -d grocky_db -f database/schema.sql
```

### 2. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Create Maven wrapper (if Maven is installed)
mvn -N wrapper:wrapper

# Or download Maven wrapper manually from https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/

# Configure environment variables (optional)
# Set JWT_SECRET and STRIPE_SECRET_KEY in application.yml or environment

# Run backend
./mvnw spring-boot:run
```

The backend will start on http://localhost:8080

### 3. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on http://localhost:5173

## Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/grocky_db
    username: grocky_user
    password: grocky_password

jwt:
  secret: YourSecretKeyForJWTTokenGenerationMustBeLongEnough2024
  expiration: 86400000

stripe:
  secret-key: sk_test_your_stripe_secret_key
  public-key: pk_test_your_stripe_public_key

# Email configuration (optional)
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

### Frontend Configuration

Edit `frontend/src/services/apiService.ts`:

```typescript
const BASE_URL = 'http://localhost:8080/api';
```

## Default Test Accounts

After running the schema, you can login with:

**Customer Account:**
- Email: john@example.com
- Password: password (BCrypt hashed in DB)

**Note:** The sample data uses BCrypt hashed passwords. The actual password for all test accounts is: `password`

## Building for Production

### Backend

```bash
cd backend
./mvnw clean package -DskipTests

# Run the JAR
java -jar target/grocky-backend-1.0.0.jar
```

### Frontend

```bash
cd frontend
npm run build

# The build artifacts will be in dist/ folder
# Serve with any static file server or deploy to hosting
```

## Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

**Database connection error:**
- Ensure PostgreSQL is running
- Check database credentials in application.yml
- Verify database exists: `psql -U grocky_user -d grocky_db`

**Maven wrapper not found:**
```bash
# Install Maven and run
mvn -N wrapper:wrapper
```

### Frontend Issues

**Dependencies installation failed:**
```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

**Port 5173 already in use:**
```bash
# Edit vite.config.ts and change port
# Or kill the process using port 5173
```

**API calls failing:**
- Ensure backend is running on port 8080
- Check BASE_URL in apiService.ts
- Verify CORS configuration in backend

## Testing

### Backend Tests

```bash
cd backend
./mvnw test
```

### Frontend Tests

```bash
cd frontend
npm test
```

## Development Tips

### Hot Reload

**Backend:**
- Use `./mvnw spring-boot:run` for auto-reload on changes
- Or configure IDE auto-compilation

**Frontend:**
- Vite already has hot module replacement (HMR)
- Changes reflect instantly

### Database

**View data:**
```bash
psql -U grocky_user -d grocky_db
SELECT * FROM products;
SELECT * FROM orders;
```

**Reset database:**
```bash
psql -U grocky_user -d grocky_db -f database/schema.sql
```

### Logs

**Backend logs:**
- Console output when running with `./mvnw spring-boot:run`
- Or check log files if configured

**Frontend logs:**
- Browser DevTools Console
- Terminal where `npm run dev` is running

## API Testing

Use tools like:
- **Postman**: Import API endpoints
- **Insomnia**: Alternative to Postman
- **cURL**: Command-line testing

Example cURL requests:

```bash
# Get all products
curl http://localhost:8080/api/products

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password"}'

# Get products with token
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Deployment

### Docker Deployment

```bash
# Build images
docker-compose build

# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Environment

1. Set environment variables:
   - `JWT_SECRET`
   - `STRIPE_SECRET_KEY`
   - `DATABASE_URL`
   - `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`

2. Use production database
3. Configure HTTPS
4. Set up reverse proxy (Nginx/Apache)
5. Enable production logging

## Support

For issues or questions:
- Check existing GitHub issues
- Create a new issue with details
- Email: support@grocky.com

## Next Steps

1. Customize the application logo and branding
2. Add your own products to the database
3. Configure Stripe for real payments
4. Set up email notifications
5. Deploy to production server

---

Happy coding! 🚀

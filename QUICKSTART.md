# 🚀 GROCKY - Quick Start Guide

## ⚡ Fastest Way to Get Started (30 seconds)

### Windows Users
1. **Double-click** `start.bat` file
2. Choose option **1** (Start with Docker)
3. Wait for services to start
4. Open browser to http://localhost:3000

### Mac/Linux Users
```bash
# Start all services
docker-compose up -d

# Access application
# Frontend: http://localhost:3000
# Backend: http://localhost:8080/api
```

## 📋 What You Get

After starting, you'll have access to:

### ✅ Frontend (React)
- **URL:** http://localhost:3000
- **Features:**
  - Browse products
  - Add to cart
  - Checkout with Stripe
  - Order tracking
  - User authentication

### ✅ Backend (Spring Boot)
- **URL:** http://localhost:8080/api
- **Features:**
  - REST API (50+ endpoints)
  - JWT Authentication
  - AI/ML Analytics
  - Real-time WebSocket
  - Stripe Payments

### ✅ Database (PostgreSQL)
- **Host:** localhost:5432
- **Database:** grocky_db
- **User:** grocky_user
- **Password:** grocky_password

## 🔑 Test Credentials

**Customer Account:**
- Email: `john@example.com`
- Password: `password`

**Note:** All test accounts use the password: `password`

## 📱 Quick Test Flow

1. **Login** at http://localhost:3000/login
   - Use: john@example.com / password

2. **Browse Products**
   - Navigate to Products page
   - See AI recommendations

3. **Add to Cart**
   - Click "Add to Cart" on any product
   - View cart icon updating

4. **Checkout**
   - Go to Cart
   - Click "Proceed to Checkout"
   - Fill in delivery address
   - Complete payment (test mode)

5. **Track Order**
   - View order confirmation
   - See real-time status updates
   - Track delivery

6. **Admin Dashboard**
   - Navigate to /dashboard
   - View AI insights
   - Check analytics
   - Run AI forecasting

## 🛠️ Development Commands

### Backend
```bash
cd backend

# Run with Maven
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build for production
./mvnw clean package
```

### Frontend
```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Run tests
npm test
```

### Docker
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild and restart
docker-compose up -d --build
```

## 📊 AI Features to Try

### 1. Demand Forecasting
```bash
# Via API
curl -X POST http://localhost:8080/api/analytics/ai/forecast \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**What it does:** Analyzes 30 days of sales data and predicts future demand for each product using Linear Regression.

### 2. Customer Segmentation
```bash
# Via API
curl http://localhost:8080/api/analytics/ai/customers/segmentation \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**What it does:** Segments customers into HIGH_VALUE, LOYAL, AT_RISK, and REGULAR using K-Means clustering.

### 3. Price Optimization
```bash
# Via API
curl http://localhost:8080/api/analytics/ai/pricing/optimization \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**What it does:** Calculates price elasticity and suggests optimal pricing for maximum revenue.

### 4. Product Recommendations
```bash
# Via API
curl http://localhost:8080/api/recommendations/personal/USER_ID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**What it does:** Recommends products based on customer's purchase history.

## 🔧 Configuration

### Change Ports
Edit `docker-compose.yml`:
```yaml
ports:
  - "8080:8080"  # Change first number for backend
  - "3000:80"    # Change first number for frontend
  - "5432:5432"  # Change first number for database
```

### Environment Variables
Create `.env` file in root directory:
```env
JWT_SECRET=YourSecretKeyHere
STRIPE_SECRET_KEY=sk_test_your_key
DATABASE_URL=jdbc:postgresql://db:5432/grocky_db
```

### Database Reset
```bash
# Stop services
docker-compose down

# Remove database volume
docker volume rm grocky_postgres_data

# Start fresh
docker-compose up -d
```

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Windows - Find and kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :8080
kill -9 <PID>
```

### Docker Issues
```bash
# Check Docker is running
docker info

# Restart Docker Desktop
# Or run:
docker-compose restart
```

### Database Connection Error
```bash
# Check database is running
docker-compose ps

# View database logs
docker-compose logs db

# Restart database
docker-compose restart db
```

### Frontend Not Loading
```bash
# Clear browser cache
# Or open in incognito mode

# Check backend is running
curl http://localhost:8080/api/products
```

## 📖 API Testing with cURL

```bash
# Get all products
curl http://localhost:8080/api/products

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password"}'

# Save token from response and use for authenticated requests
export TOKEN="your_token_here"

# Get products with authentication
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"

# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-uuid-here",
    "items": [{"productId": "product-uuid", "quantity": 2}],
    "deliveryAddress": "123 Main St"
  }'
```

## 🎯 Next Steps

1. **Explore the Code**
   - Backend: `backend/src/main/java/com/grocky/`
   - Frontend: `frontend/src/`
   - Database: `database/schema.sql`

2. **Customize**
   - Add your own products
   - Change branding/colors
   - Configure Stripe for real payments

3. **Deploy**
   - Push to GitHub
   - Deploy to Heroku/AWS/Azure
   - Use production database

4. **Enhance**
   - Add more AI features
   - Implement wishlist
   - Create mobile app

## 📞 Getting Help

### Documentation
- `README.md` - Full project documentation
- `SETUP.md` - Detailed setup instructions
- `PROJECT_SUMMARY.md` - Complete feature list

### Logs
```bash
# View all logs
docker-compose logs -f

# View specific service
docker-compose logs backend
docker-compose logs frontend
docker-compose logs db
```

### Common Issues
- Check `docker-compose ps` for service status
- Verify ports are not in use
- Ensure Docker has enough resources (4GB+ RAM)
- Check firewall settings

## ✅ Success Checklist

- [ ] All services running (green in Docker)
- [ ] Frontend accessible at http://localhost:3000
- [ ] Backend accessible at http://localhost:8080/api
- [ ] Can login with test account
- [ ] Can browse products
- [ ] Can add items to cart
- [ ] Can place order
- [ ] Can view dashboard

## 🎉 You're Ready!

Everything is set up and running. Start exploring, testing, and building amazing features!

**Happy Coding! 🚀**

---

For detailed documentation, see `README.md` and `SETUP.md`

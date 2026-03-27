# 🛒 GROCKY - AI-Powered Online Grocery Store

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-blue.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

**🏪 A modern, full-stack online grocery store with AI-driven recommendations, real-time analytics, and smart inventory management.**

> ✨ **Features:** User Authentication • Shopping Cart • Stripe Payments • AI Recommendations • Real-Time Order Tracking • Admin Dashboard

---

## 📋 Quick Navigation

- [Quick Start](#-quick-start)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [API Documentation](#-api-documentation)
- [AI Features](#-ai-features)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)

---

## 🚀 Features

### 🛍️ Core E-Commerce
- ✅ **Product Catalog** - Browse, search, and filter groceries by category
- ✅ **Shopping Cart** - Add items, manage quantities, persistent cart storage
- ✅ **User Accounts** - Registration, login, profile management, loyalty points
- ✅ **Order Tracking** - Real-time order status with timeline view
- ✅ **Reviews & Ratings** - Customer feedback with verified purchase badges
- ✅ **Secure Checkout** - Multi-step checkout with Stripe payment integration

### 🧠 AI & Analytics
- ✅ **Demand Forecasting** - Linear Regression predicts future product demand
- ✅ **Customer Segmentation** - K-Means clustering for RFM analysis
- ✅ **Price Optimization** - Price elasticity analysis and recommendations
- ✅ **Smart Inventory** - AI-powered auto-reorder suggestions
- ✅ **Admin Dashboard** - Real-time metrics, charts, and visual trends
- ✅ **Personalized Recommendations** - AI-driven product suggestions

### ⚡ Real-Time Features
- ✅ **WebSocket Integration** - Live order status updates
- ✅ **Real-Time Analytics** - Live dashboard metric updates
- ✅ **Inventory Alerts** - Instant low stock notifications

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Core language |
| Spring Boot | 3.2 | Application framework |
| Spring Data JPA | - | Database ORM |
| PostgreSQL | 15 | Primary database |
| Spring Security | - | Authentication & Authorization |
| JWT (jjwt) | 0.12.5 | Token-based authentication |
| Apache Commons Math | 3.6.1 | AI/ML algorithms |
| Stripe API | 24.22.0 | Payment processing |
| WebSocket/STOMP | - | Real-time bidirectional communication |
| Lombok | 1.18.30 | Reduce boilerplate code |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18.2 | UI component framework |
| TypeScript | 5.0 | Type-safe JavaScript |
| Vite | 5.2 | Fast build tool & dev server |
| React Router | 6.22 | Client-side routing |
| Chart.js | 4.4 | Data visualization & charts |
| STOMP.js | 7.0 | WebSocket client library |
| Stripe.js | 3.0 | Payment UI integration |
| Lucide React | 0.363 | Beautiful icon library |

### DevOps & Tools
- 🐳 Docker & Docker Compose - Containerization
- 🔄 GitHub Actions - CI/CD automation
- 📊 Spring Boot Actuator - Application monitoring
- 🏗️ Maven - Backend build tool
- 📦 npm - Frontend package manager

---

## 🏁 Quick Start

### ⚡ Fastest Way (Docker - 30 seconds)

```bash
# Clone the repository
git clone https://github.com/LEVELING2108/GROCKY.git
cd GROCKY

# Start all services (database, backend, frontend)
docker-compose up -d

# Wait 30 seconds for services to initialize

# Access the application
# 🛒 Frontend: http://localhost:3000
# 🔌 Backend API: http://localhost:8080/api
# 💾 Database: localhost:5432
```

### 🔑 Test Login Credentials

```
Email: john@example.com
Password: password123

(Or register a new account directly on the frontend)
```

### 🛑 Stop Services

```bash
docker-compose down
```

### 🔧 Restart Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart backend
docker-compose restart frontend
docker-compose restart db
```

### 📋 Check Service Status

```bash
# View all containers
docker-compose ps

# View logs
docker-compose logs -f backend
docker-compose logs -f frontend
```

---

## 📖 API Documentation

### Authentication Endpoints
```http
POST   /api/auth/register          Register new customer
POST   /api/auth/login             Customer login
GET    /api/auth/me                Get current user profile
```

### Product Endpoints
```http
GET    /api/products               Get all products (paginated)
GET    /api/products/{id}          Get product by ID
GET    /api/products/categories    Get all categories
GET    /api/products/search        Search products by keyword
POST   /api/products               Create product (ADMIN only)
PUT    /api/products/{id}          Update product (ADMIN only)
DELETE /api/products/{id}          Delete product (ADMIN only)
```

### Order Endpoints
```http
GET    /api/orders                 Get all orders (ADMIN only)
GET    /api/orders/{id}            Get order by ID
GET    /api/orders/customer/{id}   Get orders by customer
POST   /api/orders                 Create new order
PUT    /api/orders/{id}/status     Update order status (ADMIN)
PUT    /api/orders/{id}/cancel     Cancel order
```

### Cart Endpoints
```http
GET    /api/cart/{customerId}                    Get customer cart
POST   /api/cart/{customerId}/items              Add item to cart
PUT    /api/cart/{customerId}/items/{productId}  Update cart item quantity
DELETE /api/cart/{customerId}/items/{productId}  Remove item from cart
DELETE /api/cart/{customerId}/clear              Clear entire cart
```

### Payment Endpoints
```http
POST   /api/payments/{orderId}/create-intent  Create Stripe PaymentIntent
PUT    /api/payments/{id}/process             Process payment confirmation
GET    /api/payments/order/{orderId}          Get payments for order
```

### Analytics & AI Endpoints
```http
GET    /api/analytics/dashboard                       Get dashboard metrics
GET    /api/analytics/sales                           Get sales report
GET    /api/analytics/inventory                       Get inventory report
POST   /api/analytics/ai/forecast                     Run AI forecasting (ADMIN)
GET    /api/analytics/ai/customers/segmentation       Get customer segments (ADMIN)
GET    /api/analytics/ai/pricing/optimization         Get price optimization (ADMIN)
```

### Recommendation Endpoints
```http
GET    /api/recommendations/trending                            Get trending products
GET    /api/recommendations/personal/{customerId}               Get personalized recommendations
GET    /api/recommendations/frequently-together/{productId}     Get frequently bought together
```

---

## 🧠 AI Features Explained

### 1. Demand Forecasting 📈

**Algorithm:** Linear Regression

**What it does:**
- Analyzes 30 days of historical sales data
- Predicts daily demand for each product
- Automatically updates AI demand scores
- Suggests reorders when predicted demand exceeds current stock

**API Endpoint:** `POST /api/analytics/ai/forecast`

---

### 2. Customer Segmentation 🎯

**Algorithm:** K-Means Clustering (RFM Analysis)

**Customer Segments:**
- **HIGH_VALUE** - High spending, frequent purchases, recent activity
- **LOYAL** - Regular purchases, recent activity, moderate spending
- **AT_RISK** - Long time since last purchase, may need re-engagement
- **REGULAR** - Average behavior across all metrics

**API Endpoint:** `GET /api/analytics/ai/customers/segmentation`

---

### 3. Price Optimization 💰

**Algorithm:** Price Elasticity Analysis

**Elasticity Classifications:**
- **HIGHLY_INELASTIC** (elasticity > -0.5) - Can increase price without losing demand
- **ELASTIC** (elasticity < -2) - Should decrease price to increase revenue
- **UNIT_ELASTIC** (-2 ≤ elasticity ≤ -0.5) - Current price is near optimal

**API Endpoint:** `GET /api/analytics/ai/pricing/optimization`

---

## 🧪 Testing

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

### Run All Tests
```bash
# Backend tests with coverage
cd backend
./mvnw clean test jacoco:report

# View coverage report
open backend/target/site/jacoco/index.html
```

---

## 🚀 Deployment

### Docker Deployment (Production)

```bash
# Build optimized images
docker-compose build --no-cache

# Start services
docker-compose up -d

# Monitor logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Manual Production Build

#### Backend
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/grocky-backend-1.0.0.jar
```

#### Frontend
```bash
cd frontend
npm run build
# Serve dist/ folder with Nginx or similar
```

---

## 🔒 Security Features

- 🔐 **JWT Authentication** - Secure token-based auth with 24-hour expiration
- 🔒 **BCrypt Password Hashing** - Industry-standard password encryption
- 🛡️ **Role-Based Access Control** - CUSTOMER and ADMIN roles
- 🔏 **CORS Configuration** - Controlled cross-origin requests
- 💳 **PCI-Compliant Payments** - Stripe handles all card data
- ✅ **Input Validation** - Server-side validation on all endpoints
- 🔑 **Environment Variables** - Sensitive config stored securely

---

## 📱 Responsive Design

Fully responsive across all devices:

- 🖥️ **Desktop** (1200px and above)
- 📱 **Tablet** (768px - 1199px)
- 📱 **Mobile** (320px - 767px)

---

## 📝 Sample Data

The database includes pre-populated sample data:

- **3 Customers** - John Doe, Jane Smith, Bob Wilson
- **10 Products** - Multiple categories (Produce, Dairy, Bakery, Meat, etc.)
- **3 Sample Orders** - Different statuses for testing
- **Product Reviews** - Verified purchase reviews
- **7 Days Analytics** - Sales and order metrics

---

## 🔧 Configuration

### Backend Environment Variables

Create `backend/.env` file:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/grocky_db
SPRING_DATASOURCE_USERNAME=grocky_user
SPRING_DATASOURCE_PASSWORD=grocky_password

# JWT Secret (Change in production!)
JWT_SECRET=YourSecretKeyForJWTTokenGenerationMustBeLongEnough2024
JWT_EXPIRATION=86400000

# Stripe API Keys (Get from https://dashboard.stripe.com/test/apikeys)
STRIPE_SECRET_KEY=sk_test_your_test_secret_key
STRIPE_PUBLIC_KEY=pk_test_your_test_public_key

# Email Configuration (Optional - for Gmail)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
```

### Frontend Environment Variables

Create `frontend/.env` file:

```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080/api

# Stripe Public Key
VITE_STRIPE_PUBLIC_KEY=pk_test_your_test_public_key

# Application Info
VITE_APP_NAME=GROCKY
VITE_APP_VERSION=1.0.0
```

---

## 🐛 Troubleshooting

### Frontend shows "Failed to fetch" or can't connect to backend

```bash
# 1. Check if backend is running
docker-compose ps backend

# 2. Restart backend
docker-compose restart backend

# 3. Wait 30 seconds for backend to fully start
# 4. Refresh your browser
```

### Backend won't start or keeps restarting

```bash
# 1. Check backend logs
docker-compose logs backend

# 2. Ensure database is healthy first
docker-compose ps db

# 3. Restart database, wait, then restart backend
docker-compose restart db
sleep 15
docker-compose restart backend
```

### Database connection errors

```bash
# 1. Verify database container is running
docker-compose ps db

# 2. Check database logs
docker-compose logs db

# 3. Test database connection
docker exec grocky-db pg_isready -U grocky_user
```

### Port already in use (8080 or 3000)

```bash
# Find what's using the port
netstat -ano | findstr :8080
netstat -ano | findstr :3000

# Stop the conflicting process or change port in docker-compose.yml
```

### Out of disk space

```bash
# Clean up unused Docker resources
docker system prune -a

# Remove old images
docker image prune -a
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Development Guidelines

- Follow existing code style
- Write meaningful commit messages
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

See [CONTRIBUTING.md](.github/CONTRIBUTING.md) for detailed guidelines.

---

## 📞 Support

### Need Help?

- 📖 **Documentation** - This README
- 🐛 **Bug Reports** - [Create an issue](https://github.com/LEVELING2108/GROCKY/issues)
- 💡 **Feature Requests** - [Create an issue](https://github.com/LEVELING2108/GROCKY/issues)
- 💬 **Questions** - [Create an issue](https://github.com/LEVELING2108/GROCKY/issues)

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the amazing library
- Apache Commons Math for ML algorithms
- Stripe for payment processing
- All open-source contributors

---

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/LEVELING2108/GROCKY?style=social)
![GitHub forks](https://img.shields.io/github/forks/LEVELING2108/GROCKY?style=social)
![GitHub issues](https://img.shields.io/github/issues/LEVELING2108/GROCKY)
![GitHub pull requests](https://img.shields.io/github/issues-pr/LEVELING2108/GROCKY)

---

**Built with ❤️ using Spring Boot and React**

🌟 **Don't forget to star this repository if you find it useful!**

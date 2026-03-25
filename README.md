# 🛒 GROCKY - AI-Powered Online Grocery Store

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-blue.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**GROCKY** is a full-stack online grocery management system built with Spring Boot 3, React 18, and AI-driven features for intelligent analytics and recommendations.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [AI Features](#-ai-features)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [Support](#-support)

---

## 🚀 Features

### 🛍️ Core E-Commerce
- ✅ **Product Management** - Browse, search, and filter groceries by category
- ✅ **Shopping Cart** - Add items, manage quantities, persistent storage
- ✅ **Customer Profiles** - Registration, login, and loyalty point tracking
- ✅ **Order Tracking** - Real-time status updates with timeline view
- ✅ **Reviews & Ratings** - Customer feedback with verified purchase badges
- ✅ **Secure Checkout** - Multi-step checkout with Stripe integration

### 🧠 AI & Analytics
- ✅ **Demand Forecasting** - Linear Regression predicts future sales
- ✅ **Customer Segmentation** - K-Means clustering for RFM analysis
- ✅ **Price Optimization** - Price elasticity analysis and suggestions
- ✅ **Smart Inventory** - AI-powered reorder suggestions
- ✅ **Admin Dashboard** - Real-time metrics and visual trends
- ✅ **Personalized Recommendations** - AI-driven product suggestions

### ⚡ Real-time Features
- ✅ **WebSocket Integration** - Live order status updates
- ✅ **Real-time Analytics** - Live dashboard updates
- ✅ **Inventory Alerts** - Instant low stock notifications

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Core language |
| Spring Boot | 3.2 | Application framework |
| Spring Data JPA | - | Database ORM |
| PostgreSQL | 15 | Database |
| Spring Security | - | Authentication & Authorization |
| JWT | 0.12.5 | Token-based auth |
| Apache Commons Math | 3.6.1 | AI/ML algorithms |
| Stripe API | 24.22.0 | Payment processing |
| WebSocket/STOMP | - | Real-time communication |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18.2 | UI framework |
| TypeScript | 5.0 | Type safety |
| Vite | 5.2 | Build tool |
| React Router | 6.22 | Routing |
| Chart.js | 4.4 | Data visualization |
| STOMP.js | 7.0 | WebSocket client |
| Stripe.js | 3.0 | Payment UI |
| Lucide React | 0.363 | Icons |

### DevOps
- 🐳 Docker & Docker Compose
- 🔄 GitHub Actions (CI/CD)
- 📊 Spring Boot Actuator (Monitoring)

---

## 🏁 Quick Start

### Option 1: Docker (Recommended - 30 seconds)

```bash
# Clone the repository
git clone https://github.com/LEVELING2108/GROCKY.git
cd GROCKY

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost:3000
# Backend: http://localhost:8080/api
```

### Option 2: Manual Setup

#### Prerequisites
- JDK 17+
- Node.js 18+
- PostgreSQL 15
- Maven 3.6+

#### 1. Database Setup
```sql
CREATE DATABASE grocky_db;
CREATE USER grocky_user WITH PASSWORD 'grocky_password';
GRANT ALL PRIVILEGES ON DATABASE grocky_db TO grocky_user;
```

```bash
# Run schema
psql -U grocky_user -d grocky_db -f database/schema.sql
```

#### 2. Backend
```bash
cd backend
./mvnw spring-boot:run
```

#### 3. Frontend
```bash
cd frontend
npm install
npm run dev
```

### 🔑 Test Credentials
```
Email: john@example.com
Password: password
```

---

## 📖 API Documentation

### Authentication
```http
POST   /api/auth/register          # Register new customer
POST   /api/auth/login             # Login customer
GET    /api/auth/me                # Get current user profile
```

### Products
```http
GET    /api/products               # Get all products (paginated)
GET    /api/products/{id}          # Get product by ID
GET    /api/products/categories    # Get all categories
GET    /api/products/search        # Search products
POST   /api/products               # Create product (ADMIN)
PUT    /api/products/{id}          # Update product (ADMIN)
DELETE /api/products/{id}          # Delete product (ADMIN)
```

### Orders
```http
GET    /api/orders                 # Get all orders (ADMIN)
GET    /api/orders/{id}            # Get order by ID
GET    /api/orders/customer/{id}   # Get customer orders
POST   /api/orders                 # Create new order
PUT    /api/orders/{id}/status     # Update order status (ADMIN)
PUT    /api/orders/{id}/cancel     # Cancel order
```

### Cart
```http
GET    /api/cart/{customerId}                    # Get customer cart
POST   /api/cart/{customerId}/items              # Add item to cart
PUT    /api/cart/{customerId}/items/{productId}  # Update cart item
DELETE /api/cart/{customerId}/items/{productId}  # Remove from cart
DELETE /api/cart/{customerId}/clear              # Clear cart
```

### Payments
```http
POST   /api/payments/{orderId}/create-intent  # Create Stripe PaymentIntent
PUT    /api/payments/{id}/process             # Process payment
GET    /api/payments/order/{orderId}          # Get payments for order
```

### Analytics & AI
```http
GET    /api/analytics/dashboard                      # Get dashboard metrics
GET    /api/analytics/sales                          # Get sales report
GET    /api/analytics/inventory                      # Get inventory report
POST   /api/analytics/ai/forecast                    # Run AI forecasting (ADMIN)
GET    /api/analytics/ai/customers/segmentation      # Get customer segments (ADMIN)
GET    /api/analytics/ai/pricing/optimization        # Get price optimization (ADMIN)
```

### Recommendations
```http
GET    /api/recommendations/trending                           # Trending products
GET    /api/recommendations/personal/{customerId}              # Personalized recommendations
GET    /api/recommendations/frequently-together/{productId}    # Frequently bought together
```

---

## 🧠 AI Features Explained

### 1. Demand Forecasting 📈
Uses **Linear Regression** to analyze 30 days of historical sales data.

**What it does:**
- Predicts daily demand for each product
- Updates AI demand scores automatically
- Suggests reorders when predicted demand > stock

**Endpoint:** `POST /api/analytics/ai/forecast`

### 2. Customer Segmentation 🎯
Implements **K-Means Clustering** for RFM analysis.

**Segments:**
- **HIGH_VALUE** - High spending, frequent purchases
- **LOYAL** - Regular purchases, recent activity
- **AT_RISK** - Long time since last purchase
- **REGULAR** - Average behavior

**Endpoint:** `GET /api/analytics/ai/customers/segmentation`

### 3. Price Optimization 💰
Analyzes **price elasticity** for optimal pricing.

**Elasticity Types:**
- **HIGHLY_INELASTIC** - Can increase price
- **ELASTIC** - Should decrease price
- **UNIT_ELASTIC** - Current price is optimal

**Endpoint:** `GET /api/analytics/ai/pricing/optimization`

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

---

## 🚀 Deployment

### Production Build

#### Backend
```bash
cd backend
./mvnw clean package
java -jar target/grocky-backend-1.0.0.jar
```

#### Frontend
```bash
cd frontend
npm run build
```

### Docker Deployment
```bash
# Build images
docker-compose build

# Start services
docker-compose up -d

# View logs
docker-compose logs -f
```

---

## 🔒 Security

- 🔐 JWT authentication (24-hour expiration)
- 🔒 BCrypt password hashing
- 🛡️ Role-based access control (CUSTOMER, ADMIN)
- 🔏 CORS configuration
- 💳 Secure payment processing (Stripe)
- ✅ Input validation

---

## 📱 Responsive Design

Fully responsive on all devices:
- 🖥️ Desktop (1200px+)
- 📱 Tablet (768px - 1199px)
- 📱 Mobile (320px - 767px)

---

## 📝 Sample Data

Database includes:
- 3 customers
- 10 products (multiple categories)
- 3 sample orders
- Reviews & ratings
- 7 days of analytics data

---

## 🔧 Configuration

### Backend (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/grocky_db
    username: grocky_user
    password: grocky_password

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

stripe:
  secret-key: ${STRIPE_SECRET_KEY}
  public-key: ${STRIPE_PUBLIC_KEY}
```

### Frontend
Update `frontend/src/services/apiService.ts`:
```typescript
const BASE_URL = 'http://localhost:8080/api';
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

See [CONTRIBUTING.md](.github/CONTRIBUTING.md) for detailed guidelines.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

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

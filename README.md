# GROCKY - AI-Powered Online Grocery Store

GROCKY is a full-stack online grocery management system built with Spring Boot 3, React 18, and AI-driven features for intelligent analytics and recommendations.

![GROCKY Banner](https://via.placeholder.com/1200x400/2ECC71/ffffff?text=GROCKY+Online+Grocery+Store)

## 🚀 Features

### Core E-Commerce
- **Product Management:** Browse, search, and filter groceries by category with advanced filtering
- **Shopping Cart:** Add items, manage quantities, and save for later
- **Customer Profiles:** Registration, login, and loyalty point tracking
- **Order Tracking:** Real-time status updates from PENDING to DELIVERED
- **Reviews & Ratings:** Customer feedback with "Verified Purchase" badges
- **Secure Checkout:** Multi-step checkout with Stripe payment integration

### 🧠 AI & Analytics Features
- **Demand Forecasting:** Uses Linear Regression to predict future sales based on historical data
- **Customer Segmentation:** K-Means clustering for RFM (Recency, Frequency, Monetary) analysis
- **Price Optimization:** AI-powered price elasticity analysis and optimization suggestions
- **AI-Powered Inventory:** Automatically suggests reorders when predicted demand exceeds current stock
- **Admin Dashboard:** Real-time visual trends for revenue, active customers, and inventory health
- **Dynamic AI Demand Score:** Every product is assigned a demand score calculated via AI models
- **Personalized Recommendations:** AI-driven product recommendations based on purchase history

### 📊 Real-time Features
- **WebSocket Integration:** Live order status updates and notifications
- **Real-time Analytics:** Dashboard updates with live sales and inventory data
- **Inventory Alerts:** Instant notifications for low stock items

## 🛠️ Tech Stack

### Backend
- **Java 17 / Spring Boot 3.2**
- **Spring Data JPA & PostgreSQL**
- **Spring Security & JWT** (Stateless Auth)
- **Apache Commons Math 3** (For AI/ML logic - Linear Regression, K-Means)
- **MapStruct & Lombok**
- **Stripe API** (Payment processing)
- **WebSocket/STOMP** (Real-time communication)
- **JavaMailSender** (Email notifications)

### Frontend
- **React 18 / TypeScript**
- **Vite** (Build Tool)
- **React Router v6** (Routing)
- **Chart.js & React-ChartJS-2** (Data visualization)
- **Lucide React Icons**
- **STOMP.js** (WebSocket client)
- **Stripe.js** (Payment processing)

### DevOps & Tools
- **Docker & Docker Compose**
- **PostgreSQL 15**
- **Maven**

## 🏁 Getting Started

### Prerequisites
- JDK 17 or higher
- Node.js 18 or higher
- Docker and Docker Compose (optional, for containerized deployment)
- Maven 3.6+

### Quick Start with Docker

1. **Clone the repository:**
```bash
git clone https://github.com/yourusername/grocky.git
cd grocky
```

2. **Start all services with Docker Compose:**
```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- Backend API on port 8080
- Frontend on port 3000

3. **Access the application:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- API Documentation: http://localhost:8080/api/swagger-ui.html

### Manual Setup

#### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE grocky_db;
CREATE USER grocky_user WITH PASSWORD 'grocky_password';
GRANT ALL PRIVILEGES ON DATABASE grocky_db TO grocky_user;
```

2. Run the schema:
```bash
psql -U grocky_user -d grocky_db -f database/schema.sql
```

#### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Configure environment variables (optional):
```bash
export JWT_SECRET="YourSecretKeyForJWTTokenGenerationMustBeLongEnough2024"
export STRIPE_SECRET_KEY="sk_test_your_stripe_secret_key"
```

3. Run the backend:
```bash
./mvnw spring-boot:run
```

The backend will start on http://localhost:8080

#### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will start on http://localhost:5173

## 📖 API Documentation

### Authentication Endpoints

```
POST   /api/auth/register          Register new customer
POST   /api/auth/login             Login customer
GET    /api/auth/me                Get current user profile
```

### Product Endpoints

```
GET    /api/products               Get all products (with pagination & filters)
GET    /api/products/{id}          Get product by ID
GET    /api/products/categories    Get all categories
GET    /api/products/search        Search products
POST   /api/products               Create product (ADMIN)
PUT    /api/products/{id}          Update product (ADMIN)
DELETE /api/products/{id}          Delete product (ADMIN)
```

### Order Endpoints

```
GET    /api/orders                 Get all orders (ADMIN)
GET    /api/orders/{id}            Get order by ID
GET    /api/orders/customer/{id}   Get customer orders
POST   /api/orders                 Create new order
PUT    /api/orders/{id}/status     Update order status (ADMIN)
PUT    /api/orders/{id}/cancel     Cancel order
```

### Cart Endpoints

```
GET    /api/cart/{customerId}      Get customer cart
POST   /api/cart/{customerId}/items Add item to cart
PUT    /api/cart/{customerId}/items/{productId} Update cart item
DELETE /api/cart/{customerId}/items/{productId} Remove from cart
DELETE /api/cart/{customerId}/clear Clear cart
```

### Payment Endpoints

```
POST   /api/payments/{orderId}/create-intent  Create Stripe PaymentIntent
PUT    /api/payments/{id}/process  Process payment
GET    /api/payments/order/{orderId} Get payments for order
```

### Analytics & AI Endpoints

```
GET    /api/analytics/dashboard           Get dashboard metrics
GET    /api/analytics/sales               Get sales report
GET    /api/analytics/inventory           Get inventory report
POST   /api/analytics/ai/forecast         Run AI demand forecasting (ADMIN)
GET    /api/analytics/ai/customers/segmentation  Get customer segments (ADMIN)
GET    /api/analytics/ai/pricing/optimization    Get price optimization (ADMIN)
```

### Recommendation Endpoints

```
GET    /api/recommendations/trending              Get trending products
GET    /api/recommendations/personal/{customerId} Get personalized recommendations
GET    /api/recommendations/frequently-together/{productId} Get frequently bought together
```

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

## 📊 AI Features Explained

### 1. Demand Forecasting
Uses Linear Regression to analyze historical sales data (last 30 days) and predict future demand for each product. The model considers:
- Daily sales quantities
- Seasonal trends
- Recent purchase patterns

**Endpoint:** `POST /api/analytics/ai/forecast`

### 2. Customer Segmentation
Implements K-Means clustering to segment customers based on RFM analysis:
- **Recency:** Days since last purchase
- **Frequency:** Number of orders
- **Monetary:** Total amount spent

**Segments:**
- HIGH_VALUE: High spending, frequent purchases
- LOYAL: Regular purchases, recent activity
- AT_RISK: Long time since last purchase
- REGULAR: Average behavior

**Endpoint:** `GET /api/analytics/ai/customers/segmentation`

### 3. Price Optimization
Analyzes price elasticity for each product:
- Calculates how demand changes with price
- Suggests optimal pricing strategies
- Estimates revenue impact

**Elasticity Types:**
- HIGHLY_INELASTIC: Can increase price
- ELASTIC: Should decrease price
- UNIT_ELASTIC: Current price is optimal

**Endpoint:** `GET /api/analytics/ai/pricing/optimization`

## 🔒 Security

- JWT-based authentication with 24-hour expiration
- Password hashing using BCrypt
- Role-based access control (CUSTOMER, ADMIN)
- CORS configuration for cross-origin requests
- Secure payment processing with Stripe

## 📱 Responsive Design

The frontend is fully responsive and works on:
- Desktop (1200px+)
- Tablet (768px - 1199px)
- Mobile (320px - 767px)

## 🎨 UI Components

- Modern gradient backgrounds
- Card-based layouts
- Real-time charts and graphs
- Interactive timeline for order tracking
- Smooth animations and transitions

## 📝 Sample Data

The database schema includes sample data for:
- 3 customers
- 10 products across multiple categories
- 3 sample orders
- Reviews and ratings
- Analytics data for the last 7 days

## 🔧 Configuration

### Backend Configuration (application.yml)

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

### Frontend Configuration

Update `frontend/src/services/apiService.ts` with your backend URL:

```typescript
const BASE_URL = 'http://localhost:8080/api';
```

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

1. Build custom images:
```bash
docker-compose build
```

2. Deploy to production:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## 📊 Monitoring

The application includes Spring Boot Actuator for monitoring:

```
GET /api/actuator/health    Application health
GET /api/actuator/metrics   Application metrics
GET /api/actuator/info      Application info
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Team

- Full Stack Development: Grocky Team
- AI/ML Implementation: Grocky Team
- UI/UX Design: Grocky Team

## 📞 Support

For support, email:
- Technical Support: support@grocky.com
- Sales: sales@grocky.com
- General Info: hello@grocky.com

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the amazing library
- Apache Commons Math for ML algorithms
- Stripe for payment processing
- All open-source contributors

---

**Built with ❤️ using Spring Boot and React**
#   G R O C K Y  
 
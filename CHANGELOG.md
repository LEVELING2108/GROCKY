# Changelog

All notable changes to GROCKY will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned
- Mobile app (React Native)
- Wishlist functionality
- Advanced search with Elasticsearch
- Multi-language support
- Dark mode for UI

## [1.0.0] - 2024-03-25

### Added
- **Initial Release** of GROCKY Online Grocery Store
- Spring Boot 3.2 backend with REST API (50+ endpoints)
- React 18 + TypeScript frontend
- PostgreSQL database with comprehensive schema
- **AI/ML Features:**
  - Demand forecasting using Linear Regression
  - Customer segmentation using K-Means clustering
  - Price optimization with elasticity analysis
  - Personalized product recommendations
- **Real-time Features:**
  - WebSocket integration for live order updates
  - Real-time analytics dashboard
  - Inventory alerts
- **E-Commerce Functionality:**
  - Product catalog with search and filters
  - Shopping cart management
  - Multi-step checkout
  - Order tracking with timeline
  - Reviews and ratings system
- **Payment Integration:**
  - Stripe payment processing
  - Payment intent creation
  - Transaction tracking
- **Security:**
  - JWT authentication
  - BCrypt password hashing
  - Role-based access control (CUSTOMER, ADMIN)
- **Email Notifications:**
  - Order confirmation emails
  - Status update notifications
  - Low stock alerts
  - Welcome emails
- **DevOps:**
  - Docker and Docker Compose configuration
  - GitHub Actions CI/CD pipelines
  - Health checks and monitoring
  - Multi-stage Docker builds
- **Documentation:**
  - Comprehensive README
  - Setup guides
  - API documentation
  - Contributing guidelines

### Technical Details
- Java 17
- Spring Boot 3.2
- React 18.2
- TypeScript 5.2
- PostgreSQL 15
- Apache Commons Math 3.6.1 (for AI/ML)
- Chart.js 4.4 (for analytics)
- STOMP.js 7.0 (for WebSocket)

### Fixed
- Initial release - no fixes yet

### Changed
- N/A - Initial release

### Deprecated
- N/A - Initial release

### Removed
- N/A - Initial release

### Security
- JWT tokens with 24-hour expiration
- BCrypt password hashing
- CORS configuration
- Input validation
- SQL injection prevention

---

## Version History

- **v1.0.0** (2024-03-25) - Initial Release

---

**Notable Features:**
- 🛒 Full-featured e-commerce platform
- 🧠 AI-powered analytics and recommendations
- ⚡ Real-time updates via WebSocket
- 🔒 Secure authentication and authorization
- 📊 Comprehensive admin dashboard
- 🐳 Docker-ready for easy deployment
- 📱 Responsive design for all devices

**Built with ❤️ using Spring Boot and React**

# 🚀 GROCKY Deployment Guide

Complete guide for deploying GROCKY to production.

---

## 📋 Table of Contents

1. [Pre-Deployment Checklist](#pre-deployment-checklist)
2. [Local Development](#local-development)
3. [Docker Deployment](#docker-deployment)
4. [Cloud Deployment](#cloud-deployment)
5. [Production Configuration](#production-configuration)
6. [Monitoring & Maintenance](#monitoring--maintenance)

---

## ✅ Pre-Deployment Checklist

### Security
- [ ] Change all default passwords
- [ ] Generate strong JWT_SECRET (min 64 characters)
- [ ] Use production Stripe keys (not test keys)
- [ ] Enable HTTPS/SSL
- [ ] Configure CORS properly
- [ ] Remove debug endpoints from production
- [ ] Set up firewall rules

### Database
- [ ] Use production PostgreSQL instance
- [ ] Configure automated backups
- [ ] Set up database monitoring
- [ ] Create database user with limited privileges
- [ ] Enable SSL for database connections

### Application
- [ ] Test all features thoroughly
- [ ] Run performance tests
- [ ] Configure logging aggregation
- [ ] Set up error tracking (Sentry, etc.)
- [ ] Configure email SMTP settings

---

## 💻 Local Development

### Quick Start

```bash
# Clone repository
git clone https://github.com/LEVELING2108/GROCKY.git
cd GROCKY

# Copy environment files
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env

# Start with Docker
docker-compose up -d

# Access application
# Frontend: http://localhost:3000
# Backend: http://localhost:8080/api
```

### Manual Setup

See [SETUP.md](SETUP.md) for detailed instructions.

---

## 🐳 Docker Deployment

### Production Docker Compose

Create `docker-compose.prod.yml`:

```yaml
version: '3.8'

services:
  db:
    image: postgres:15-alpine
    container_name: grocky-db
    restart: always
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - grocky-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./backend
    container_name: grocky-backend
    restart: always
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      STRIPE_SECRET_KEY: ${STRIPE_SECRET_KEY}
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      db:
        condition: service_healthy
    networks:
      - grocky-network
    healthcheck:
      test: ["CMD", "wget", "--spider", "http://localhost:8080/api/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build: ./frontend
    container_name: grocky-frontend
    restart: always
    environment:
      VITE_API_BASE_URL: ${API_URL}
    depends_on:
      - backend
    networks:
      - grocky-network

  nginx:
    image: nginx:alpine
    container_name: grocky-nginx
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
    depends_on:
      - frontend
      - backend
    networks:
      - grocky-network

volumes:
  postgres_data:

networks:
  grocky-network:
    driver: bridge
```

### Deploy with Docker

```bash
# Build images
docker-compose -f docker-compose.prod.yml build

# Start services
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f

# Stop services
docker-compose -f docker-compose.prod.yml down
```

---

## ☁️ Cloud Deployment

### AWS Deployment

#### Using ECS (Elastic Container Service)

1. **Create ECR Repositories**
```bash
aws ecr create-repository --repository-name grocky-backend
aws ecr create-repository --repository-name grocky-frontend
```

2. **Build and Push Images**
```bash
# Backend
docker build -t grocky-backend ./backend
docker tag grocky-backend:latest <account-id>.dkr.ecr.region.amazonaws.com/grocky-backend:latest
docker push <account-id>.dkr.ecr.region.amazonaws.com/grocky-backend:latest

# Frontend
docker build -t grocky-frontend ./frontend
docker tag grocky-frontend:latest <account-id>.dkr.ecr.region.amazonaws.com/grocky-frontend:latest
docker push <account-id>.dkr.ecr.region.amazonaws.com/grocky-frontend:latest
```

3. **Create ECS Task Definitions**
4. **Create ECS Services**
5. **Configure Load Balancer**
6. **Set up RDS for PostgreSQL**

### Heroku Deployment

#### Backend

```bash
cd backend

# Login to Heroku
heroku login

# Create app
heroku create grocky-backend

# Add PostgreSQL
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set JWT_SECRET=your-secret-key
heroku config:set STRIPE_SECRET_KEY=your-stripe-key

# Deploy
git push heroku main
```

#### Frontend

```bash
cd frontend

# Create app
heroku create grocky-frontend

# Set API URL
heroku config:set VITE_API_BASE_URL=https://grocky-backend.herokuapp.com/api

# Deploy
git push heroku main
```

### Digital Ocean Deployment

1. **Create Droplet** (Ubuntu 20.04, 4GB RAM recommended)
2. **Install Docker**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
```

3. **Install Docker Compose**
```bash
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

4. **Clone and Deploy**
```bash
git clone https://github.com/LEVELING2108/GROCKY.git
cd GROCKY
docker-compose -f docker-compose.prod.yml up -d
```

---

## ⚙️ Production Configuration

### Environment Variables

Create `.env` file:

```bash
# Database
DB_NAME=grocky_production
DB_USER=grocky_user
DB_PASSWORD=STRONG_PASSWORD_HERE

# JWT
JWT_SECRET=VERY_LONG_SECRET_KEY_MIN_64_CHARACTERS_FOR_SECURITY
JWT_EXPIRATION=43200000

# Stripe
STRIPE_SECRET_KEY=sk_live_your_live_secret_key
STRIPE_PUBLIC_KEY=pk_live_your_live_public_key

# Email
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# API URLs
API_URL=https://your-domain.com/api
FRONTEND_URL=https://your-domain.com

# Redis (Optional)
REDIS_URL=redis://localhost:6379
```

### Nginx Configuration for Production

Create `nginx/nginx.conf`:

```nginx
events {
    worker_connections 1024;
}

http {
    upstream backend {
        server backend:8080;
    }

    upstream frontend {
        server frontend:80;
    }

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=one:10m rate=10r/s;

    server {
        listen 80;
        server_name your-domain.com www.your-domain.com;

        # Redirect to HTTPS
        return 301 https://$server_name$request_uri;
    }

    server {
        listen 443 ssl http2;
        server_name your-domain.com www.your-domain.com;

        # SSL Configuration
        ssl_certificate /etc/nginx/ssl/fullchain.pem;
        ssl_certificate_key /etc/nginx/ssl/privkey.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers HIGH:!aNULL:!MD5;

        # Security headers
        add_header Strict-Transport-Security "max-age=31536000" always;
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;

        # Frontend
        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }

        # Backend API
        location /api {
            limit_req zone=one burst=20 nodelay;
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
}
```

---

## 📊 Monitoring & Maintenance

### Health Checks

```bash
# Backend health
curl http://localhost:8080/api/actuator/health

# Database health
curl http://localhost:8080/api/actuator/health/db

# Metrics
curl http://localhost:8080/api/actuator/metrics
```

### Logging

```bash
# View backend logs
docker-compose logs -f backend

# View frontend logs
docker-compose logs -f frontend

# View database logs
docker-compose logs -f db
```

### Database Backup

```bash
# Backup
docker exec grocky-db pg_dump -U grocky_user grocky_db > backup_$(date +%Y%m%d).sql

# Restore
docker exec -i grocky-db psql -U grocky_user grocky_db < backup_20240325.sql
```

### Automated Backups Script

Create `backup.sh`:

```bash
#!/bin/bash

BACKUP_DIR="/backups/grocky"
DATE=$(date +%Y%m%d_%H%M%S)
CONTAINER_NAME="grocky-db"
DB_NAME="grocky_db"
DB_USER="grocky_user"

# Create backup directory
mkdir -p $BACKUP_DIR

# Backup database
docker exec $CONTAINER_NAME pg_dump -U $DB_USER $DB_NAME > $BACKUP_DIR/backup_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/backup_$DATE.sql

# Delete backups older than 7 days
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: backup_$DATE.sql.gz"
```

Add to crontab:
```bash
0 2 * * * /path/to/backup.sh
```

---

## 🔧 Troubleshooting

### Common Issues

**Backend won't start:**
```bash
# Check logs
docker-compose logs backend

# Verify database connection
docker-compose exec backend ping db
```

**Frontend can't connect to backend:**
```bash
# Check API_URL environment variable
# Verify backend is running
docker-compose ps
```

**Database connection errors:**
```bash
# Restart database
docker-compose restart db

# Check database logs
docker-compose logs db
```

---

## 📈 Performance Optimization

### Database Indexes

Already included in schema.sql:
- Customer email index
- Product category index
- Order customer index
- Order status index

### Caching (Optional)

Add Redis to `docker-compose.yml`:

```yaml
redis:
  image: redis:7-alpine
  container_name: grocky-redis
  ports:
    - "6379:6379"
  volumes:
    - redis_data:/data
  command: redis-server --appendonly yes
```

Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

---

## 🎯 Post-Deployment Checklist

- [ ] Verify all endpoints work
- [ ] Test user registration and login
- [ ] Test order creation flow
- [ ] Test payment processing
- [ ] Verify email notifications
- [ ] Check WebSocket connections
- [ ] Monitor error logs
- [ ] Set up uptime monitoring
- [ ] Configure alert notifications
- [ ] Document deployment process

---

## 📞 Support

For deployment issues:
- Check logs: `docker-compose logs -f`
- Review documentation: README.md, SETUP.md
- Create GitHub issue
- Contact: support@grocky.com

---

**Deployment completed! 🎉**

Your GROCKY application is now running in production!

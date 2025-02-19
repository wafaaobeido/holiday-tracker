# Holiday Tracker - Enterprise-Level Documentation

## ✨ Project Overview
Holiday Tracker is a **scalable, microservices-based** application designed to streamline holiday request management. It features **secure authentication**, **user activity tracking**, and **holiday request processing**, deployed in a **Kubernetes-based cloud-native environment**. The system follows **modern DevOps best practices**, ensuring high availability, maintainability, and observability.

---

## 🏗 Technology Stack
### Frontend:
- Angular (TypeScript)
- SCSS for UI Styling
- Cypress for End-to-End Testing

### Backend:
- Spring Boot (Java 17+)
- RESTful APIs with OpenAPI (Swagger)
- Asynchronous communication via Kafka

### Database & Storage:
- MySQL (Persistent Data Store)
- Redis (Caching Layer)

### Security & Authentication:
- Keycloak for OAuth2 / OpenID Connect Authentication
- JWT-based API security

### Infrastructure & CI/CD:
- Docker & Kubernetes (Helm for deployment)
- GitHub Actions for CI/CD
- Prometheus & Grafana for Monitoring
- Kong API Gateway

---

## 📁 Project Structure
```
├── backend
│   ├── auth-service  # Authentication & API security
│   ├── user-service  # User activity tracking
│   ├── holiday-service  # Holiday request management
│   ├── shared-libraries  # Common DTOs, event handling, and utilities
├── frontend  # Angular UI
│   ├── e2e  # Cypress testing
├── infra
│   ├── k8s  # Kubernetes YAML configurations
│   ├── ci_cd  # GitHub Actions & automation scripts
│   ├── monitoring  # Prometheus & Grafana setup
│   ├── mysql  # Database initialization scripts
```

---

## ⚙ Initialization & Running the Project
### Prerequisites
- Kubernetes Cluster (Minikube, EKS, GKE, or AKS)
- kubectl CLI configured
- Helm installed
- Docker and a Container Registry (e.g., Docker Hub, AWS ECR)
- Node.js and npm installed (for frontend development)

### Clone the Repository
```bash
git clone <repo-url>
cd holiday-tracker
```

### Build & Push Docker Images
```bash
docker-compose build
docker-compose push
```

### Start Backend Services Locally (for Development)
```bash
cd backend/auth-service
mvn spring-boot:run &
cd ../user-service
mvn spring-boot:run &
cd ../holiday-service
mvn spring-boot:run &
```

### Start Frontend Locally
```bash
cd frontend
npm install
npm start
```

### Deploy to Kubernetes
```bash
kubectl apply -f infra/k8s/deployment.yaml
kubectl apply -f infra/k8s/service.yaml
```

### Run Cypress End-to-End Tests
```bash
cd frontend
docker-compose up -d  # Ensure backend services are running
npx cypress open
```

### Access the Application
Get the **Frontend Service External IP**:
```bash
kubectl get services frontend-service
```
Access via the browser: `http://<external-ip>`

---

## 🔄 CI/CD Workflow
- **GitHub Actions** automates testing, building, and deployment.
- The workflow is defined in `.github/workflows/deploy.yml`.
- On **push to main**, the following steps execute:
  1. **Unit Tests** & **Static Code Analysis**
  2. **Build Docker Images** & Push to Registry
  3. **Run Cypress Tests** for UI validation
  4. **Deploy Updated Services to Kubernetes**

---

## 🔐 Security Considerations
- **Role-Based Access Control (RBAC)** enforced via Keycloak.
- **Secure API Gateway** with Kong for request authentication.
- **TLS Encryption** for data-in-transit security.

---

## 📌 API Documentation
Each microservice exposes RESTful APIs documented via **Swagger**.
To view API documentation:
```bash
http://<service-ip>/swagger-ui/
```

---

## 📊 Observability & Monitoring
- **Prometheus & Grafana** for performance monitoring.
- **Structured Logging** using centralized logging with a log aggregator (e.g., Fluentd, Loki, or a custom logging stack).

---

## 🔧 Best Practices
- Follow **Git Flow** for structured version control.
- Ensure **unit & integration tests** pass before merging to `develop`.
- Use **Helm charts** for managed Kubernetes deployments.
- Enable **pod auto-scaling** for optimal resource utilization.
- Utilize **CI/CD pipelines** for automated rollouts & rollbacks.
- Log all requests for **auditability** & security compliance.

---

## 🚀 Future Implementations
- **Machine Learning Insights**: Use AI models to analyze holiday request trends.
- **Multi-Tenancy Support**: Enable the platform to serve multiple organizations securely.
- **Advanced Role Management**: Fine-grained access control policies for different user roles.
- **Progressive Delivery**: Implement canary releases and blue-green deployments for safer rollouts.

---

## 👥 Contributors
- **Wafaa Obeido** - Software Engineer / Full stack Developer

---

## 📜 License
MIT License

# Holiday Tracker 🚀

## Overview
Holiday Tracker is a **microservices-based** system for managing holidays, users, and authentication using **Spring Boot**, **Angular**, **Docker**, and **Kubernetes**.

## Features
✅ Authentication via Keycloak  
✅ Holiday tracking and analytics  
✅ Secure API with JWT  
✅ CI/CD with GitHub Actions  
✅ Scalable architecture with Kubernetes  

## Architecture
- **Frontend:** Angular
- **Backend:** Spring Boot (auth-service, user-service, holiday-service)
- **Database:** MySQL
- **Orchestration:** Kubernetes

## 🚀 Quick Start
```bash
./start_deployment.sh
API Endpoints
Method	Endpoint	Description
POST	/auth/login	Authenticate user
GET	/holidays	Get holidays list
POST	/users/logs	Save user logs
Deployment
See DEPLOYMENT.md for detailed steps.

yaml
Copy
Edit

---

### **📄 `DEPLOYMENT.md`**
**Step-by-step** guide for deployment.

```md
# 📦 Deployment Guide

## 1️⃣ Prerequisites
- Docker
- Kubernetes (Minikube or AWS/GKE)
- Helm (for managing K8s charts)

## 2️⃣ Build & Push Images
```bash
./start_deployment.sh
3️⃣ Deploy to Kubernetes
bash
Copy
Edit
kubectl apply -f infra/k8s/
kubectl get pods
4️⃣ Verify Services
bash
Copy
Edit
kubectl get svc
kubectl logs -f deployment/auth-service
5️⃣ Access Frontend
bash
Copy
Edit
kubectl port-forward svc/frontend 4200:80
yaml
Copy
Edit

---

### **📄 `ARCHITECTURE.md`**
Explains **system design** for developers.

```md
# 🏗️ System Architecture

## Microservices Breakdown
- **Auth Service** → Handles authentication
- **User Service** → Manages users and logs
- **Holiday Service** → Manages holiday data

## API Gateway
Using Kong for **load balancing and security**.

## Tech Stack
- **Backend:** Spring Boot, Keycloak
- **Frontend:** Angular
- **DB:** MySQL, Redis
- **CI/CD:** GitHub Actions
- **Containerization:** Docker
- **Orchestration:** Kubernetes
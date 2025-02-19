# 🔐 Security Guide

## 1️⃣ Authentication
- Uses **Keycloak** for OAuth2.0
- JWT Tokens for API access

## 2️⃣ API Security
- **Role-based access control** via `@PreAuthorize`
- **CORS Protection** enabled

## 3️⃣ Database Security
- **SQL Injection Prevention**
- **Encrypted user passwords (BCrypt)**

## 4️⃣ Kubernetes Security
- **RBAC roles** enforced
- Pods use **non-root users**
# 🏋️ Fitness Full Stack Microservices Project

A **modern fitness platform** built on a **microservices architecture** leveraging Java Spring Boot, React, Kafka, OAuth2, and cutting-edge AI integrations.  
This system is designed for scalability, security, and modularity — enabling smooth communication between services through Kafka and centralized access through a Gateway.

---

## 🚀 Tech Stack

### 🖥️ Backend
- **Java 21**
- **SpringBoot**
- **Spring Security**
- **Spring AI**
- **Spring Cloud (Eureka, Config Server, Gateway)**
- **Apache Kafka**
- **OAuth 2.0 / JWT Authentication**
- **keyCloak Authentication**
- **Postman API**
-  **Microservices**

### 🌐 Frontend
- **React.js**
- **Tailwind CSS / Material UI (optional)**

### 🗄️ Databases
- **PostgreSQL** (Primary transactional data)
- **MongoDB** (Analytics & unstructured data)

---

---

## 🧩 Microservices Description

| Service Name          | Description                                                                 | Tech Used                               |
|------------------------|------------------------------------------------------------------------------|------------------------------------------|
| 🏃 **Activity Service**      | Manages workout activities, progress tracking, and exercise logs.              | Spring Boot, Kafka, PostgreSQL          |
| 🥗 **Nutrition Service**     | Handles meal plans, calorie tracking, and nutrition data.                       | Spring Boot, Kafka,Spring AI, MongoDB          |
| 👤 **User Service**          | Manages user registration, profiles, and authentication.                        | Spring Boot, Kafka, PostgreSQL          |
| 🧠 **AI Service**            | Uses **Spring AI** for personalized recommendations & activity predictions.    | Spring Boot, Spring AI, MongoDB         |
| ⚙️ **Config Server**        | Provides centralized configuration for all microservices.                       | Spring Cloud Config                    |
| 🧭 **Eureka Server**        | Enables service discovery and load balancing.                                   | Spring Cloud Eureka                    |
| 🌉 **API Gateway**          | Central entry point for all microservices with security & routing.              | Spring Cloud Gateway, OAuth2           |

---

## 🔐 Security & Authentication

- Implemented **OAuth 2.0** with JWT tokens for secure access.
- Gateway routes all requests and verifies tokens.
- Role-based access control ensures proper authorization for each service.

---

## 📡 Communication Flow

- **Synchronous**: Gateway → Service via REST.
- **Asynchronous**: Services communicate with each other using **Apache Kafka** event streams for better decoupling and scalability.
- **Centralized Config**: All services pull configuration from a Config Server.
- **Service Discovery**: Eureka handles service registration and discovery.

---

## 🛠️ Prerequisites

Before running the project, make sure you have:

- ☕ Java 21
- 🐘 PostgreSQL
- 🍃 MongoDB
- 🐳 Docker
- 🐦 Apache Kafka
- 🧰 Node.js (for React frontend)
- 🔐 OAuth2 Client credentials (configured in Gateway & Auth)
- 🔐Postman API

---


### 1. Clone the Repository
```bash
git clone https://github.com/your-username/fitness-microservices.git
cd fitness-microservices
---
## 🛠️ Getting Started

### 2. Start Infrastructure

Run **Kafka**, **PostgreSQL**, and **MongoDB**.  
You can also use **Docker Compose** for faster setup and orchestration.

---

### 3. Start Config & Discovery Servers

```bash
cd config-server
mvn spring-boot:run

cd ../eureka-server
mvn spring-boot:run


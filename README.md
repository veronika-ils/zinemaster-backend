
# ZineMaster Backend

This is the **backend** of the **ZineMaster** application.  
It is built with **Spring Boot** and provides the API, authentication, WebSocket notifications, and database access.  

---

## Features

- User authentication (JWT + OAuth2 Google login)  
- Role-based access (User, Product Administrator, User Administrator)  
- API for managing product requests and users  
- WebSocket/STOMP notifications for admins  
- 🗄PostgreSQL database integration  

---

## Tech Stack

- **Backend Framework**: Spring Boot (Java 17+)  
- **Security**: Spring Security, JWT, OAuth2 (Google)  
- **Database**: PostgreSQL  
- **ORM**: Hibernate / JPA  
- **Messaging**: Spring WebSocket + STOMP  
- **Build Tool**: Maven  

---

## Project Setup

### 1. Clone the repository
```bash
git clone https://github.com/veronika-ils/zinemaster-backend.git
cd zinemaster-backend


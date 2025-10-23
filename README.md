# 📘 Library Management System

A secure and efficient **Library Management System** built using **Spring Boot**, **Spring Security**, **JWT**, **Hibernate ORM**, and **PostgreSQL**.

---

## 🚀 Features
- **User Authentication & Authorization:** Implemented using Spring Security and JSON Web Tokens (JWT).
- **Role-based Access Control:** Admin and Member roles with distinct permissions.
- **Book Management:** CRUD operations for adding, editing, deleting, and listing books.
- **User Management:** Admin can view, add, and remove users.
- **Borrow/Return System:** Tracks issued and returned books.
- **Global Exception Handling:** Custom error responses for clean API results.
- **Validation:** Input validation using Jakarta/Spring annotations.
- **Lombok Integration:** Reduces boilerplate code with annotations like `@Getter`, `@Setter`, `@Builder`, etc.

---

## 🧰 Tech Stack

**Backend:**
- Java 17  
- Spring Boot  
- Spring Security  
- JWT (JSON Web Token)  
- Hibernate ORM  
- PostgreSQL  
- Lombok  

**Tools:**
- Maven  
- Postman (for API testing)  
- Git & GitHub  
- IntelliJ IDEA / VS Code  

---

## ⚙️ Setup & Installation

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/YourUsername/library-management-system.git
cd library-management-system

2️⃣ Configure Database

In your application.properties file:

spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true
jwt.secret=your_jwt_secret_key

3️⃣ Run the Application

mvn spring-boot:run

Server will start at:

http://localhost:8080

📡 API Endpoints (Example)
Method	Endpoint	Description
POST	/api/auth/signup	Register a new user
POST	/api/auth/login	Login and receive JWT token
GET	/api/books	View all books
POST	/api/books	Add a new book (Admin only)
PUT	/api/books/{id}	Update a book
DELETE	/api/books/{id}	Delete a book
POST	/api/loans/borrow/{bookId}	Borrow a book
POST	/api/loans/return/{loanId}	Return a borrowed book
👤 Roles
Role	Permissions
Admin	Manage users, books, and loans
Member	View and borrow books
🧾 Example JSON (Book)

{
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "isbn": "978-0134685991",
  "category": "Programming",
  "available": true
}

🛠️ Project Highlights

    Used Spring Security filters for JWT validation.

    Created modular architecture with controller, service, and repository layers.

    Implemented DTO classes for clean API responses.

    Used Lombok for boilerplate-free models and builder patterns.

📚 Learning Outcomes

    Gained deep understanding of authentication, ORM, and relational data management.

    Practiced production-grade REST API development with Spring Boot.

    Improved database modeling and service-layer design skills.

🧑‍💻 Author

Waleed Ahmad
📍 Java Developer | Spring Boot Enthusiast
🔗 github.com/walid0044


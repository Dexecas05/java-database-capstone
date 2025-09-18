---
### 🏗️ Application Architecture Description

The Smart Clinic Management System is designed using a three-tier architecture that separates the application into distinct layers: presentation, application, and data. This structure helps improve scalability, maintainability, and flexibility. The system supports both traditional web dashboards for admins and doctors—built using Spring MVC and Thymeleaf—and RESTful APIs for modules like appointments and patient records, making it suitable for mobile apps and future integrations.

At its core, the backend is powered by Spring Boot, which handles business logic, data processing, and communication between layers. It connects to two databases: MySQL for structured data like users and appointments, and MongoDB for flexible, document-based data such as prescriptions. This dual-database approach allows the system to efficiently manage both relational and non-relational data. The architecture also supports modern development practices like containerization with Docker and automated deployment pipelines, making it ready for cloud-native environments.

---

### 🔁 Data Flow Steps (1–7)

1. **User Interface Layer**  
   Users interact with the system through Thymeleaf-based dashboards or REST API clients like mobile apps and frontend modules.

2. **Controller Layer**  
   Requests are routed to either Thymeleaf Controllers (for HTML views) or REST Controllers (for JSON responses), depending on the type of client.

3. **Service Layer**  
   Controllers delegate tasks to services, which apply business rules, validations, and coordinate workflows across entities.

4. **Repository Layer**  
   Services call repositories to access data. MySQL repositories use Spring Data JPA, while MongoDB repositories use Spring Data MongoDB.

5. **Database Access**  
   Repositories interact directly with MySQL for structured data and MongoDB for flexible, document-based data.

6. **Model Binding**  
   Retrieved data is mapped into Java model classes—JPA entities for MySQL and document objects for MongoDB.

7. **Application Models in Use**  
   Models are passed to Thymeleaf templates for rendering HTML or serialized into JSON for REST API responses, completing the request-response cycle.

---

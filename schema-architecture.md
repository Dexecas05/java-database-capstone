# 🏥 Guided Project: CMS Backend Architecture

## Architecture Summary

This CMS application uses a **three-tier architecture** built on **Java and Spring Boot** to handle both administrative web dashboards and external API requests. 
It's a **hybrid application** that separates the user interface and data access from the core business logic.

The backend uses **Spring MVC with Thymeleaf** for server-side rendering of pages like the Admin and Doctor Dashboards. 
Simultaneously, it exposes **RESTful APIs** using JSON for modules like Appointments and Patient Records, enabling interoperability with external clients (e.g., mobile apps). 
A unique feature is its **dual-database strategy**: **MySQL** handles core relational data (Patient, Doctor, Appointment, Admin models) via **JPA**, while **MongoDB** is used for less structured data like Prescriptions, accessed via **Spring Data MongoDB**. 
This design ensures high scalability, maintainability, and deployment flexibility, supporting modern CI/CD practices and containerization with Docker and Kubernetes.

---

## Numbered Flow of Data and Control

The following steps trace the flow of a request from the user interface through the backend and back again, detailing the application's request-response cycle:

1.  **User Interface:** A request originates from the user. This can be a form submission from a **Thymeleaf-based web dashboard** (for Admin/Doctor views) or a call from an external client to a **REST API endpoint** (for Appointments/Patient data).

2.  **Controller Layer:** The request first hits a **Spring Boot Controller**. The request is directed to either a **Thymeleaf Controller** (for server-side views) or a **Rest Controller** (for JSON API calls). The controller validates the request and coordinates the subsequent flow.

3.  **Service Layer:** All controllers delegate the **business logic** to the **Service Layer**. This layer contains the core application logic, ensuring transactions, calculations, and complex processes are isolated and testable.

4.  **Repository Layer:** The Service Layer communicates with the appropriate **Repository Layer** to perform data access operations. It uses a **MySQL Repository** for relational data (via JPA) or a **MongoDB Repository** for document data.

5.  **Database Access:** The Repository layer executes the required operation (CRUD: Create, Read, Update, Delete) against the target database: either the **MySQL Database** or the **MongoDB Database**.

6.  **Model Binding (Data Retrieval):** Once the data is retrieved from the database, the data access framework (JPA for MySQL, Spring Data MongoDB for MongoDB) automatically maps the raw database output into structured **Java Model** classes (JPA Entities for MySQL, Document Objects for MongoDB).

7.  **Application Models in Use & Response:** The Service Layer processes the bound models, and the Controller then uses this final data to construct the response. This delivers either a fully rendered **web page** (from the Thymeleaf Controller) or structured **JSON API data** (from the Rest Controller) back to the user/client, completing the cycle.
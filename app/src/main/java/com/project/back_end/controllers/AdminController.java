
package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service; // Import the central Service class
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
// Base URL path for all methods in this controller
// Assuming api.path is configured, e.g., to "/api/v1/"
@RequestMapping("/api/v1/admin") // Placeholder for "${api.path}" + "admin"
public class AdminController {

    // Autowired Dependencies
    private final Service centralService;

    @Autowired
    public AdminController(Service centralService) {
        this.centralService = centralService;
    }

    /**
     * Handles POST requests for Admin login validation.
     * @param receivedAdmin The Admin object containing username and password from the request body.
     * @return ResponseEntity containing a token on success or an error message on failure.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin receivedAdmin) {

        // Call the validateAdmin method from the central Service
        // This method handles finding the admin, comparing the password,
        // and generating the token.
        return centralService.validateAdmin(receivedAdmin);
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to indicate that it's a REST controller, used to handle web requests and return JSON responses.
//    - Use `@RequestMapping("${api.path}admin")` to define a base path for all endpoints in this controller.
//    - This allows the use of an external property (`api.path`) for flexible configuration of endpoint paths.


// 2. Autowire Service Dependency:
//    - Use constructor injection to autowire the `Service` class.
//    - The service handles core logic related to admin validation and token checking.
//    - This promotes cleaner code and separation of concerns between the controller and business logic layer.


// 3. Define the `adminLogin` Method:
//    - Handles HTTP POST requests for admin login functionality.
//    - Accepts an `Admin` object in the request body, which contains login credentials.
//    - Delegates authentication logic to the `validateAdmin` method in the service layer.
//    - Returns a `ResponseEntity` with a `Map` containing login status or messages.



}


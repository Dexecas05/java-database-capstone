package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class DashboardController {

// 1. Set Up the MVC Controller Class:
//    - Annotate the class with `@Controller` to indicate that it serves as an MVC controller returning view names (not JSON).
//    - This class handles routing to admin and doctor dashboard pages based on token validation.


// 2. Autowire the Shared Service:
//    - Inject the common `Service` class, which provides the token validation logic used to authorize access to dashboards.

// 3. Define the `adminDashboard` Method:
//    - Handles HTTP GET requests to `/adminDashboard/{token}`.
//    - Accepts an admin's token as a path variable.
//    - Validates the token using the shared service for the `"admin"` role.
//    - If the token is valid (i.e., no errors returned), forwards the user to the `"admin/adminDashboard"` view.
//    - If invalid, redirects to the root URL, likely the login or home page.


// 4. Define the `doctorDashboard` Method:
//    - Handles HTTP GET requests to `/doctorDashboard/{token}`.
//    - Accepts a doctor's token as a path variable.
//    - Validates the token using the shared service for the `"doctor"` role.
//    - If the token is valid, forwards the user to the `"doctor/doctorDashboard"` view.
//    - If the token is invalid, redirects to the root URL.

    // Inject the validation service
    private final TokenValidationService tokenValidationService;

    @Autowired
    public DashboardController(TokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;
    }

    /**
     * Handles the secure route for the Admin Dashboard.
     * Requires a JWT token in the path variable.
     * @return Thymeleaf view name ("adminDashboard") or redirect to login ("/").
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Validate the token and extract the user's role
        String role = tokenValidationService.validateTokenAndGetRole(token);

        // Check if token is valid and the user is an admin
        if ("admin".equals(role)) {
            // Token is valid for admin, return the Thymeleaf template
            return "adminDashboard";
        }

        // If validation fails or role is incorrect, redirect to the login page (root path)
        return "redirect:/";
    }

    /**
     * Handles the secure route for the Doctor Dashboard.
     * Requires a JWT token in the path variable.
     * @return Thymeleaf view name ("doctorDashboard") or redirect to login ("/").
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        // Validate the token and extract the user's role
        String role = tokenValidationService.validateTokenAndGetRole(token);

        // Check if token is valid and the user is a doctor
        if ("doctor".equals(role)) {
            // Token is valid for doctor, return the Thymeleaf template
            return "doctorDashboard";
        }

        // If validation fails or role is incorrect, redirect to the login page (root path)
        return "redirect:/";
    }

    /**
     * Placeholder Service for JWT Token Validation.
     * In a real application, this would handle decoding, signature verification,
     * and expiration checks for the JWT.
     */
    @Service
    public static class TokenValidationService {

        /**
         * Mock validation logic for development.
         * @param token The JWT string.
         * @return The user role ('admin', 'doctor', or 'invalid').
         */
        public String validateTokenAndGetRole(String token) {
            // In a production app, this would be complex JWT logic.
            if (token == null || token.isEmpty()) {
                return "invalid";
            }

            // Simple mock logic based on token prefix:
            if (token.startsWith("admin_")) {
                return "admin";
            } else if (token.startsWith("doctor_")) {
                return "doctor";
            } else {
                return "invalid";
            }
        }
    }
}


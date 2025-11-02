package com.project.back_end.controllers;

import com.project.back_end.models.Patient;
import com.project.back_end.DTO.Login; // Ensure the correct import for the Login DTO
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service; // Import the central Service class
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    // Autowired Dependencies
    private final PatientService patientService;
    private final Service centralService;

    @Autowired
    public PatientController(PatientService patientService, Service centralService) {
        this.patientService = patientService;
        this.centralService = centralService;
    }

    // --- 1. Get Patient Details ---
    /**
     * Fetches the patient's details using the provided authentication token.
     * @param token The authentication token for the patient.
     * @return Patient details or an error message.
     */
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(@PathVariable("token") String token) {

        // Validate Token (must be a Patient)
        if (!centralService.validateToken(token, "patient").getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized access or invalid token."), HttpStatus.UNAUTHORIZED);
        }

        // Fetch details using the service
        return patientService.getPatientDetails(token);
    }

    // --- 2. Create a New Patient (Registration) ---
    /**
     * Handles patient registration, including duplicate checks.
     * @param patient The patient details to be created.
     * @return A response message indicating the result of the registration.
     */
    @PostMapping()
    public ResponseEntity<Map<String, String>> createNewPatient(@RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();

        // 1. Validates if the patient already exists by checking email or phone number
        if (!centralService.validatePatient(patient)) {
            response.put("error", "Patient with email id or phone no already exist");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict
        }

        // 2. If validation passes, create the patient
        int result = patientService.createPatient(patient);

        if (result == 1) {
            response.put("message", "Signup successful");
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
        } else {
            response.put("error", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // --- 3. Patient Login ---
    /**
     * Handles patient login validation and token issuance.
     * @param login The login credentials (email, password).
     * @return The result of the login validation.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        // We use the validatePatientLogin method from the central Service class
        return centralService.validatePatientLogin(login);
    }

    // --- 4. Get Patient Appointments ---
    /**
     * Fetches all appointments for a specific patient.
     * @param id The ID of the patient.
     * @param token The authentication token.
     * @return The list of patient appointments or an error message.
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable("id") Long id,
            @PathVariable("token") String token) {

        // Validate Token (must be a Patient)
        if (!centralService.validateToken(token, "patient").getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized access or invalid token."), HttpStatus.UNAUTHORIZED);
        }

        // Fetch appointments using the service (which includes an authorization check inside)
        return patientService.getPatientAppointment(id, token);
    }

    // --- 5. Filter Patient Appointments ---
    /**
     * Filters patient appointments based on condition and doctor name.
     * @param condition The condition to filter appointments ("past" or "future").
     * @param name The name of the doctor to filter by (can be "null").
     * @param token The authentication token.
     * @return The filtered appointments or an error message.
     */
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @PathVariable("condition") String condition,
            @PathVariable("name") String name,
            @PathVariable("token") String token) {

        // Validate Token (must be a Patient)
        if (!centralService.validateToken(token, "patient").getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized access or invalid token."), HttpStatus.UNAUTHORIZED);
        }

        // Convert "null" string from PathVariable to actual null
        String nameFilter = "null".equalsIgnoreCase(name) ? null : name;

        // Calls centralService.filterPatient() to handle filtering combinations
        return centralService.filterPatient(condition, nameFilter, token);
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.


// 2. Autowire Dependencies:
//    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
//    - Inject the shared `Service` class for tasks like token validation and login authentication.


// 3. Define the `getPatient` Method:
//    - Handles HTTP GET requests to retrieve patient details using a token.
//    - Validates the token for the `"patient"` role using the shared service.
//    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.


// 4. Define the `createPatient` Method:
//    - Handles HTTP POST requests for patient registration.
//    - Accepts a validated `Patient` object in the request body.
//    - First checks if the patient already exists using the shared service.
//    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.


// 5. Define the `login` Method:
//    - Handles HTTP POST requests for patient login.
//    - Accepts a `Login` DTO containing email/username and password.
//    - Delegates authentication to the `validatePatientLogin` method in the shared service.
//    - Returns a response with a token or an error message depending on login success.


// 6. Define the `getPatientAppointment` Method:
//    - Handles HTTP GET requests to fetch appointment details for a specific patient.
//    - Requires the patient ID, token, and user role as path variables.
//    - Validates the token using the shared service.
//    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.


// 7. Define the `filterPatientAppointment` Method:
//    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
//    - Accepts filtering parameters: `condition`, `name`, and a token.
//    - Token must be valid for a `"patient"` role.
//    - If valid, delegates filtering logic to the shared service and returns the filtered result.



}



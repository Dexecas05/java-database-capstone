package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import com.project.back_end.DTO.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
// Placeholder for "${api.path}" + "doctor"
@RequestMapping("/api/v1/doctor")
public class DoctorController {

    // Autowired Dependencies
    private final DoctorService doctorService;
    private final Service centralService;

    @Autowired
    public DoctorController(DoctorService doctorService, Service centralService) {
        this.doctorService = doctorService;
        this.centralService = centralService;
    }

    // --- 1. Get Doctor Availability ---
    /**
     * Fetches the available slots for a specific doctor on a given date.
     * Accessible to multiple user roles (checked via token).
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable("user") String user,
            @PathVariable("doctorId") Long doctorId,
            @PathVariable("date") String date,
            @PathVariable("token") String token) {

        // Validate Token against the user role provided in the path
        if (!centralService.validateToken(token, user).getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized access or invalid token."), HttpStatus.UNAUTHORIZED);
        }

        try {
            LocalDate localDate = LocalDate.parse(date);

            // Fetch availability via DoctorService
            List<String> availability = doctorService.getDoctorAvailability(doctorId, localDate);

            Map<String, Object> response = new HashMap<>();
            response.put("doctorId", doctorId);
            response.put("date", localDate);
            response.put("availableSlots", availability);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Invalid date format or internal error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. Get List of Doctors ---
    /**
     * Fetches a list of all doctors.
     * This endpoint is typically public or only requires a valid token (not specified, assuming public for search).
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        // Fetches a list of all doctors from the doctorService
        List<Doctor> doctors = doctorService.getDoctors();

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        response.put("count", doctors.size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- 3. Add New Doctor ---
    /**
     * Adds a new doctor to the database.
     * Requires "admin" role validation.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(
            @RequestBody Doctor doctor,
            @PathVariable("token") String token) {

        Map<String, String> response = new HashMap<>();

        // Validate Token (must be Admin)
        if (!centralService.validateToken(token, "admin").getStatusCode().equals(HttpStatus.OK)) {
            response.put("error", "Unauthorized: Only Admin can add doctors.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {
            response.put("message", "Doctor added to db");
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
        } else if (result == -1) {
            response.put("error", "Doctor already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict
        } else {
            response.put("error", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // --- 4. Doctor Login ---
    /**
     * Handles POST requests for Doctor login validation.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        // Calls doctorService.validateDoctor()
        return doctorService.validateDoctor(login);
    }

    // --- 5. Update Doctor Details ---
    /**
     * Updates the details of an existing doctor.
     * Requires "admin" role validation.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable("token") String token) {

        Map<String, String> response = new HashMap<>();

        // Validate Token (must be Admin)
        if (!centralService.validateToken(token, "admin").getStatusCode().equals(HttpStatus.OK)) {
            response.put("error", "Unauthorized: Only Admin can update doctors.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == 1) {
            response.put("message", "Doctor updated");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (result == -1) {
            response.put("error", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        } else {
            response.put("error", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // --- 6. Delete Doctor ---
    /**
     * Deletes a doctor by ID.
     * Requires "admin" role validation.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable("id") long id,
            @PathVariable("token") String token) {

        Map<String, String> response = new HashMap<>();

        // Validate Token (must be Admin)
        if (!centralService.validateToken(token, "admin").getStatusCode().equals(HttpStatus.OK)) {
            response.put("error", "Unauthorized: Only Admin can delete doctors.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.deleteDoctor(id);

        if (result == 1) {
            response.put("message", "Doctor deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (result == -1) {
            response.put("error", "Doctor not found with id " + id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        } else {
            response.put("error", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // --- 7. Filter Doctors ---
    /**
     * Filters doctors based on name, time, and specialty.
     * The path variables must be handled to allow optional filtering (using "null" string).
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public Map<String, Object> filterDoctors(
            @PathVariable("name") String name,
            @PathVariable("time") String time,
            @PathVariable("speciality") String speciality) {

        // Convert "null" strings from PathVariables to actual nulls for the service
        String nameFilter = "null".equalsIgnoreCase(name) ? null : name;
        String timeFilter = "null".equalsIgnoreCase(time) ? null : time;
        String specialityFilter = "null".equalsIgnoreCase(speciality) ? null : speciality;

        // Uses centralService.filterDoctor() to handle all filtering combinations
        return centralService.filterDoctor(nameFilter, specialityFilter, timeFilter);
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.


}

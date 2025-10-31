package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service; // Import the central Service class
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
// Placeholder for "${api.path}" + "prescription"
@RequestMapping("/api/v1/prescription")
public class PrescriptionController {

    // Autowired Dependencies
    private final PrescriptionService prescriptionService;
    private final Service centralService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, Service centralService) {
        this.prescriptionService = prescriptionService;
        this.centralService = centralService;
    }

    // --- 1. Save Prescription ---
    /**
     * Allows a doctor to save a new prescription document.
     * @param token The authentication token for the doctor.
     * @param prescription The prescription document to be saved.
     * @return A response message indicating the result.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable("token") String token,
            @RequestBody Prescription prescription) {

        // 1. Validate Token (must be a Doctor)
        if (!centralService.validateToken(token, "doctor").getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(
                    Collections.singletonMap("error", "Unauthorized access. Only doctors can save prescriptions."),
                    HttpStatus.UNAUTHORIZED
            );
        }

        // 2. Save the prescription using the PrescriptionService (MongoDB)
        return prescriptionService.savePrescription(prescription);
    }

    // --- 2. Get Prescription by Appointment ID ---
    /**
     * Allows a doctor to retrieve prescription documents associated with an appointment.
     * @param appointmentId The ID of the relational appointment.
     * @param token The authentication token for the doctor.
     * @return The prescription details or an error message.
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable("appointmentId") Long appointmentId,
            @PathVariable("token") String token) {

        // 1. Validate Token (must be a Doctor)
        if (!centralService.validateToken(token, "doctor").getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(
                    Collections.singletonMap("error", (Object) "Unauthorized access or invalid token."),
                    HttpStatus.UNAUTHORIZED
            );
        }

        // 2. Retrieve the prescription using the PrescriptionService (MongoDB)
        // This method handles finding/not finding the prescription.
        return prescriptionService.getPrescription(appointmentId);
    }
    
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
//    - This controller manages creating and retrieving prescriptions tied to appointments.


// 2. Autowire Dependencies:
//    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
//    - Inject the shared `Service` class for token validation and role-based access control.
//    - Inject `AppointmentService` to update appointment status after a prescription is issued.


// 3. Define the `savePrescription` Method:
//    - Handles HTTP POST requests to save a new prescription for a given appointment.
//    - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
//    - Validates the token for the `"doctor"` role.
//    - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
//    - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.


// 4. Define the `getPrescription` Method:
//    - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
//    - Accepts the appointment ID and a doctor’s token as path variables.
//    - Validates the token for the `"doctor"` role using the shared service.
//    - If the token is valid, fetches the prescription using the `PrescriptionService`.
//    - Returns the prescription details or an appropriate error message if validation fails.


}

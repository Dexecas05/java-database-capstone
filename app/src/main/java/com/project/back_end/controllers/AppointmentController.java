package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    // Autowired Dependencies
    private final AppointmentService appointmentService;
    private final Service centralService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service centralService) {
        this.appointmentService = appointmentService;
        this.centralService = centralService;
    }

    /**
     * Handles GET requests to retrieve appointments for a specific doctor.
     * Accessible only by users with the "doctor" role.
     *
     * @param date The date for which appointments are needed.
     * @param patientName The patient name filter (can be "null" if no filter).
     * @param token The authorization token.
     * @return A map containing the list of appointments or an error message.
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable("date") String date,
            @PathVariable("patientName") String patientName,
            @PathVariable("token") String token) {

        // 1. Validate Token (must be a Doctor)
        if (!centralService.validateToken(token, "doctor").getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized access or invalid token."), HttpStatus.UNAUTHORIZED);
        }

        try {
            // Handle "null" string passed from path variable for optional parameter
            String nameFilter = "null".equalsIgnoreCase(patientName) ? null : patientName;
            LocalDate localDate = LocalDate.parse(date);

            // 2. Fetch appointments via AppointmentService
            Map<String, Object> appointments = appointmentService.getAppointment(nameFilter, localDate, token);

            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Invalid date format or internal error: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Handles POST requests to book a new appointment.
     * Accessible only by users with the "patient" role.
     *
     * @param appointment The Appointment object to be booked.
     * @param token The authorization token.
     * @return A response message indicating the result.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable("token") String token) {

        Map<String, String> response = new HashMap<>();

        // 1. Validate Token (must be a Patient)
        if (!centralService.validateToken(token, "patient").getStatusCode().equals(HttpStatus.OK)) {
            response.put("error", "Unauthorized access or invalid token.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 2. Validate Appointment Availability
        int validationResult = centralService.validateAppointment(appointment);

        if (validationResult == -1) {
            response.put("error", "Doctor not found or invalid doctor ID.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else if (validationResult == 0) {
            response.put("error", "The selected appointment time is unavailable.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        // 3. Book the Appointment
        int bookingResult = appointmentService.bookAppointment(appointment);

        if (bookingResult == 1) {
            response.put("message", "Appointment successfully booked.");
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
        } else {
            response.put("error", "Failed to book appointment due to an internal error.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles PUT requests to update an existing appointment.
     * Accessible only by users with the "patient" role.
     *
     * @param appointment The Appointment object with updated details.
     * @param token The authorization token.
     * @return A response message indicating the result.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable("token") String token) {

        // 1. Validate Token (must be a Patient)
        if (!centralService.validateToken(token, "patient").getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized access or invalid token."), HttpStatus.UNAUTHORIZED);
        }

        // 2. Update the Appointment (Validation logic is inside the service)
        return appointmentService.updateAppointment(appointment);
    }

    /**
     * Handles DELETE requests to cancel an appointment.
     * Accessible only by users with the "patient" role.
     *
     * @param id The ID of the appointment to cancel.
     * @param token The authorization token.
     * @return A response message indicating the result.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable("id") long id,
            @PathVariable("token") String token) {

        // 1. Validate Token (must be a Patient)
        if (!centralService.validateToken(token, "patient").getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized access or invalid token."), HttpStatus.UNAUTHORIZED);
        }

        // 2. Cancel the Appointment
        return appointmentService.cancelAppointment(id, token);
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.


// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.


// 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.


// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.


// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.


// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.


}

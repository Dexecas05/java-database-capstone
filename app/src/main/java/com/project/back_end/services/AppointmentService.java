package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    // --- Repositories and Dependencies ---
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    // NOTE: TokenService is required by the prompt, but not yet implemented.
    private final TokenService tokenService;

    // Dependency Injection via Constructor
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService // Assuming TokenService exists for token operations
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    // --- Placeholder/Mock Methods (for validation and token logic) ---

    // Placeholder for real application validation logic
    private boolean validateAppointment(Appointment appointment) {
        // In a real application, this would check doctor availability,
        // overlapping appointments, business hours, etc.
        if (appointment.getDoctor() == null || appointment.getPatient() == null) {
            return false;
        }
        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            return false; // Cannot book in the past
        }
        // Additional checks would go here...
        return true;
    }

    // Placeholder for extracting the user's ID from a token
    private Long getUserIdFromToken(String token) {
        // Mocking: Assume a Patient ID (e.g., 1L) is returned for now.
        // In reality, this would use JWT parsing from the TokenService.
        return 1L;
    }

    // --- Core Business Methods ---

    /**
     * Books a new appointment.
     * @param appointment The appointment object to book.
     * @return 1 if successful, 0 if there's an error.
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        if (!validateAppointment(appointment)) {
            return 0; // Validation failed
        }
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            // Log the exception
            return 0;
        }
    }

    /**
     * Updates an existing appointment.
     * @param appointment The appointment object with updated details.
     * @return A response message indicating success or failure.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        if (appointment.getId() == null) {
            response.put("error", "Appointment ID is required for update.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(appointment.getId());

        if (existingAppointmentOpt.isEmpty()) {
            response.put("error", "Appointment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Apply specific validation logic (as requested in hint)
        if (!validateAppointment(appointment)) {
            response.put("error", "The updated appointment details are invalid (e.g., time conflict, past date).");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Update fields manually (or use a mapping tool like ModelMapper)
            Appointment existingAppointment = existingAppointmentOpt.get();
            existingAppointment.setAppointmentTime(appointment.getAppointmentTime());
            existingAppointment.setStatus(appointment.getStatus());
            existingAppointment.setDoctor(appointment.getDoctor());
            existingAppointment.setPatient(appointment.getPatient());

            appointmentRepository.save(existingAppointment);
            response.put("message", "Appointment successfully updated.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Failed to update appointment: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Cancels an existing appointment.
     * Ensures the requesting user (extracted from token) is the patient who booked it.
     * @param id The ID of the appointment to cancel.
     * @param token The authorization token.
     * @return A response message indicating success or failure.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isEmpty()) {
            response.put("error", "Appointment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Appointment appointment = appointmentOpt.get();
        Long requestingPatientId = getUserIdFromToken(token); // Get Patient ID from token

        // Hint: Ensure the patient attempting to cancel is the one who booked it.
        if (!appointment.getPatient().getId().equals(requestingPatientId)) {
            response.put("error", "Unauthorized: You can only cancel your own appointments.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        try {
            // A safer approach might be setting status to 'Cancelled' (e.g., status = 2)
            // instead of physical deletion, but the prompt specified `delete(appointment)`.
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment successfully canceled and deleted.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Failed to cancel appointment: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of appointments for a specific doctor, filterable by date and patient name.
     * This method assumes the Doctor ID is retrieved from the token, as it's an authenticated dashboard request.
     * @param pname Patient name to filter by (optional).
     * @param date The date for appointments.
     * @param token The authorization token (used to identify the doctor).
     * @return A map containing the list of appointments.
     */
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        // MOCK: Get Doctor ID from token (The token service should provide this)
        Long doctorId = getUserIdFromToken(token);
        if (doctorId == null) {
            result.put("error", "Invalid or missing token.");
            return result;
        }

        // Define the time range for the given date (start of day to end of day)
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<Appointment> appointments;

        if (pname != null && !pname.trim().isEmpty()) {
            // Use the complex query for filtering by doctor ID, patient name, and time range
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, pname, start, end
            );
        } else {
            // Use the simpler query for filtering by doctor ID and time range
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctorId, start, end
            );
        }

        result.put("appointments", appointments);
        result.put("count", appointments.size());
        return result;
    }
// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.


}

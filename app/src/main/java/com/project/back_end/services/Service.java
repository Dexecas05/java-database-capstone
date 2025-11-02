package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@org.springframework.stereotype.Service
public class Service {

    // --- Dependencies ---
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Constructor Injection
    public Service(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }



    // --- Core Methods ---

    /**
     * Checks the validity of a token for a given user.
     */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();

        // Use tokenService.validateToken()
        if (tokenService.validateToken(token, user)) {
            response.put("message", "Token is valid.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "Invalid or expired token.");
            // Return an Unauthorized response if the token is invalid or expired.
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Validates the login credentials of an admin.
     */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();

        // 1. Find the Admin by username
        Optional<Admin> adminOpt = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (adminOpt.isEmpty()) {
            response.put("error", "Invalid username or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Admin storedAdmin = adminOpt.get();

        // 2. Compare the password (In a real app, use BCryptEncoder)
        if (!storedAdmin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("error", "Invalid username or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 3. If valid, generate a token
        String token = tokenService.generateToken(storedAdmin.getUsername(), "ADMIN");
        response.put("token", token);
        response.put("message", "Admin login successful.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Filters doctors based on name, specialty, and available time.
     */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {

        // --- Filtering Logic (delegated to DoctorService) ---

        if (name != null && specialty != null && time != null) {
            // All three criteria provided
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && specialty != null) {
            // Name and Specialty provided
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (name != null && time != null) {
            // Name and Time provided
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (specialty != null && time != null) {
            // Specialty and Time provided
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (name != null) {
            // Only Name provided
            return doctorService.findDoctorByName(name);
        } else if (specialty != null) {
            // Only Specialty provided
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (time != null) {
            // Only Time provided
            return doctorService.filterDoctorsByTime(time);
        } else {
            // No filters provided - return all doctors
            return Collections.singletonMap("doctors", doctorService.getDoctors());
        }
    }

    /**
     * Validates whether an appointment is available based on the doctor's schedule.
     * @return 1 if the appointment time is valid, 0 if the time is unavailable, -1 if the doctor doesn't exist.
     */
    public int validateAppointment(Appointment appointment) {
        Long doctorId = appointment.getDoctor().getId();

        // -1 if the doctor doesn't exist
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return -1;
        }

        // Get the requested appointment time as a String (e.g., "14:00")
        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();

        // Use doctorService.getDoctorAvailability() to check available time slots for that doctor.
        List<String> availableTimes = doctorService.getDoctorAvailability(
                doctorId,
                appointment.getAppointmentTime().toLocalDate()
        );

        // 1 if the appointment time is valid, 0 if the time is unavailable
        if (availableTimes.contains(requestedTime)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Checks whether a patient exists based on their email or phone number.
     * @return true if the patient does **not** exist (safe to register), false if the patient exists already (duplicate).
     */
    public boolean validatePatient(Patient patient) {
        // Use patientRepository.findByEmailOrPhone() to check if the patient exists.
        // If the patient is found, return false.
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()).isEmpty();
    }

    /**
     * Validates a patient's login credentials (email and password).
     */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();

        // 1. Find the Patient by email
        Optional<Patient> patientOpt = patientRepository.findByEmail(login.getIdentifier());

        if (patientOpt.isEmpty()) {
            response.put("error", "Invalid email or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Patient patient = patientOpt.get();

        // 2. Compare the password (In a real app, use BCryptEncoder)
        if (!patient.getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid email or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 3. If valid, generate a token
        String token = tokenService.generateToken(patient.getEmail(), "PATIENT");
        response.put("token", token);
        response.put("message", "Patient login successful.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Filters patient appointments based on condition and doctor name.
     */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        // 1. Get the patient ID from the token
        String patientIdentifier = tokenService.extractIdentifier(token);

        if (patientIdentifier == null) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized: Invalid or missing token."), HttpStatus.UNAUTHORIZED);
        }

        // 2. Look up the Patient by identifier (email) to get the ID
        Optional<Patient> patientOpt = patientRepository.findByEmail(patientIdentifier);

        if (patientOpt.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Patient not found for token identifier."), HttpStatus.UNAUTHORIZED);
        }
        Long patientId = patientOpt.get().getId(); // Now we have the ID

        // --- Filtering Logic (delegated to PatientService) ---

        if (condition != null && name != null) {
            // Condition and Doctor Name provided
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        } else if (condition != null) {
            // Only Condition provided
            return patientService.filterByCondition(condition, patientId);
        } else if (name != null) {
            // Only Doctor Name provided
            return patientService.filterByDoctor(name, patientId);
        } else {
            // No filters provided - return all appointments
            return patientService.getPatientAppointment(patientId, token);
        }
    }


// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.

// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.

// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.

}
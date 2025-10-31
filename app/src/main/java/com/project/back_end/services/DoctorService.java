package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    // --- Repositories and Dependencies ---
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor Injection
    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // --- Utility/Placeholder Classes ---

    // Placeholder for the Login DTO used in validateDoctor
    public static class Login {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
    }

    // --- Core Business Methods ---

    /**
     * Fetches the available slots for a specific doctor on a given date.
     * @param doctorId The ID of the doctor.
     * @param date The date for which availability is needed.
     * @return A list of available time slots (Strings).
     */
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return Collections.emptyList(); // Doctor not found
        }

        Doctor doctor = doctorOpt.get();
        List<String> availableSlots = new ArrayList<>(doctor.getAvailableTimes()); // Doctor's default available times

        // 1. Define time range for the given date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        // 2. Fetch all booked appointments for the doctor on that date
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, startOfDay, endOfDay
        );

        // 3. Extract the start time (as String "HH:mm") of each booked appointment
        Set<String> bookedTimes = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toSet());

        // 4. Filter out booked times from the doctor's available slots
        return availableSlots.stream()
                .filter(slot -> !bookedTimes.contains(LocalTime.parse(slot).toString()))
                .collect(Collectors.toList());
    }

    /**
     * Saves a new doctor to the database, checking for duplicates by email.
     * @param doctor The doctor object to save.
     * @return 1 for success, -1 if the doctor already exists, 0 for internal errors.
     */
    @Transactional
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            return -1; // Doctor already exists
        }
        try {
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            // Log error
            return 0; // Internal error
        }
    }

    /**
     * Updates the details of an existing doctor.
     * @param doctor The doctor object with updated details.
     * @return 1 for success, -1 if doctor not found, 0 for internal errors.
     */
    @Transactional
    public int updateDoctor(Doctor doctor) {
        Optional<Doctor> existingDoctorOpt = doctorRepository.findById(doctor.getId());
        if (existingDoctorOpt.isEmpty()) {
            return -1; // Doctor not found
        }
        try {
            Doctor existingDoctor = existingDoctorOpt.get();
            // Update fields manually (or use a mapping tool)
            existingDoctor.setName(doctor.getName());
            existingDoctor.setSpecialty(doctor.getSpecialty());
            existingDoctor.setEmail(doctor.getEmail());
            // NOTE: Password update should be handled separately and requires encoding
            if (doctor.getPassword() != null && !doctor.getPassword().isEmpty()) {
                existingDoctor.setPassword(doctor.getPassword()); // In real app, use BCryptEncoder here
            }
            existingDoctor.setPhone(doctor.getPhone());
            existingDoctor.setAvailableTimes(doctor.getAvailableTimes());

            doctorRepository.save(existingDoctor);
            return 1; // Success
        } catch (Exception e) {
            // Log error
            return 0; // Internal error
        }
    }

    /**
     * Retrieves a list of all doctors.
     * @return A list of all doctors.
     */
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Deletes a doctor by ID and all associated appointments.
     * @param id The ID of the doctor to be deleted.
     * @return 1 for success, -1 if doctor not found, 0 for internal errors.
     */
    @Transactional
    public int deleteDoctor(long id) {
        if (!doctorRepository.existsById(id)) {
            return -1; // Doctor not found
        }
        try {
            // Hint: Delete all associated appointments first
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1; // Success
        } catch (Exception e) {
            // Log error
            return 0; // Internal error
        }
    }

    /**
     * Validates a doctor's login credentials.
     * @param login The login object containing email and password.
     * @return A response with a token if valid, or an error message if not.
     */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();

        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(login.getEmail());
        if (doctorOpt.isEmpty()) {
            response.put("error", "Invalid email or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Doctor doctor = doctorOpt.get();
        // NOTE: In a real app, use BCryptPasswordEncoder.matches(rawPassword, storedHash)
        if (!doctor.getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid email or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Authentication successful, generate token
        // MOCK: Replace with actual TokenService logic
        String token = tokenService.generateToken(doctor.getId(), "DOCTOR");
        response.put("token", token);
        response.put("message", "Login successful.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Finds doctors by their name using partial match.
     * @param name The name of the doctor to search for.
     * @return A map with the list of doctors matching the name.
     */
    public Map<String, Object> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        return createDoctorMap(doctors);
    }

    // --- Filtering Methods ---

    /**
     * Filters doctors by name, specialty, and availability during AM/PM.
     */
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        // 1. Filter by Name and Specialty (using the custom Repository query)
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        // 2. Filter by Time
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);

        return createDoctorMap(filteredDoctors);
    }

    /**
     * Filters doctors by name and their availability during AM/PM.
     */
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        // 1. Filter by Name (using a simple custom Repository query or convention)
        // Assuming findByNameLike is suitable for partial name search
        List<Doctor> doctors = doctorRepository.findByNameLike(name);

        // 2. Filter by Time
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);

        return createDoctorMap(filteredDoctors);
    }

    /**
     * Filters doctors by name and specialty.
     */
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        // Use the custom Repository query for Name and Specialty
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return createDoctorMap(doctors);
    }

    /**
     * Filters doctors by specialty and their availability during AM/PM.
     */
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        // 1. Filter by Specialty (using the case-insensitive Repository query)
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        // 2. Filter by Time
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);

        return createDoctorMap(filteredDoctors);
    }

    /**
     * Filters doctors by specialty.
     */
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return createDoctorMap(doctors);
    }

    /**
     * Filters doctors by their availability during AM/PM.
     */
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        // 1. Fetch all doctors
        List<Doctor> doctors = doctorRepository.findAll();

        // 2. Filter by Time
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);

        return createDoctorMap(filteredDoctors);
    }

    /**
     * Private helper method to filter a list of doctors by their available times (AM/PM).
     * @param doctors The list of doctors to filter.
     * @param amOrPm Time of day: "AM" (slots before 12:00) or "PM" (slots at or after 12:00).
     * @return A filtered list of doctors.
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        final boolean isAM = "AM".equalsIgnoreCase(amOrPm);

        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes().stream()
                        .anyMatch(slot -> {
                            try {
                                LocalTime time = LocalTime.parse(slot);
                                return isAM ? time.isBefore(LocalTime.NOON) : !time.isBefore(LocalTime.NOON);
                            } catch (Exception e) {
                                // Handle malformed time slot string gracefully
                                return false;
                            }
                        })
                )
                .collect(Collectors.toList());
    }

    /**
     * Private helper method to structure the response map for doctor lists.
     */
    private Map<String, Object> createDoctorMap(List<Doctor> doctors) {
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        result.put("count", doctors.size());
        return result;
    }

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.

// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 

// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.

// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.

// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.


// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.


// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).

// 14. **filterDoctorByNameAndSpecility Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.


// 15. **filterDoctorByTimeAndSpecility Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).

// 16. **filterDoctorBySpecility Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.

   
}

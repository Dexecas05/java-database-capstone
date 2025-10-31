package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    // --- Configuration Value (From application.properties) ---
    // Make sure you have a key like: jwt.secret=your-secure-base64-secret-here...
    @Value("${jwt.secret}")
    private String secret;

    // Token expiration time (7 days as requested in the hint)
    private final long EXPIRATION_TIME_MS = TimeUnit.DAYS.toMillis(7);

    // --- Repositories ---
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    // Constructor Injection
    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // --- Helper Method ---

    /**
     * Retrieves the signing key used for JWT token signing from the configured secret.
     * @return The SecretKey used for signing the JWT.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Core Methods ---

    /**
     * Generates a JWT token for a given user's identifier.
     * @param identifier The unique identifier (username/email) for the user.
     * @return The generated JWT token string.
     */
    public String generateToken(String identifier, String userType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_MS);

        return Jwts.builder()
                .setSubject(identifier) // identifier (username or email) is the subject
                .claim("userType", userType.toUpperCase()) // Optional claim for quick validation
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the identifier (subject) from a JWT token.
     * @param token The JWT token from which the identifier is to be extracted.
     * @return The identifier extracted from the token, or null if parsing fails.
     */
    public String extractIdentifier(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // Subject holds the identifier

        } catch (Exception e) {
            // Token is expired, invalid signature, or malformed
            return null;
        }
    }

    /**
     * Validates the JWT token for a given user type.
     * @param token The JWT token to be validated.
     * @param user The type of user (e.g., "admin", "doctor", "patient").
     * @return true if the token is valid for the specified user type and the user exists in the DB.
     */
    public boolean validateToken(String token, String user) {
        String identifier = extractIdentifier(token);

        if (identifier == null) {
            return false;
        }

        // Check if a corresponding user exists in the database based on the user type
        switch (user.toLowerCase()) {
            case "admin":
                Optional<Admin> adminOpt = adminRepository.findByUsername(identifier);
                return adminOpt.isPresent();
            case "doctor":
                Optional<Doctor> doctorOpt = doctorRepository.findByEmail(identifier);
                return doctorOpt.isPresent();
            case "patient":
                Optional<Patient> patientOpt = patientRepository.findByEmail(identifier);
                return patientOpt.isPresent();
            default:
                return false;
        }
    }
// 1. **@Component Annotation**
// The @Component annotation marks this class as a Spring component, meaning Spring will manage it as a bean within its application context.
// This allows the class to be injected into other Spring-managed components (like services or controllers) where it's needed.

// 2. **Constructor Injection for Dependencies**
// The constructor injects dependencies for `AdminRepository`, `DoctorRepository`, and `PatientRepository`,
// allowing the service to interact with the database and validate users based on their role (admin, doctor, or patient).
// Constructor injection ensures that the class is initialized with all required dependencies, promoting immutability and making the class testable.

// 3. **getSigningKey Method**
// This method retrieves the HMAC SHA key used to sign JWT tokens.
// It uses the `jwt.secret` value, which is provided from an external source (like application properties).
// The `Keys.hmacShaKeyFor()` method converts the secret key string into a valid `SecretKey` for signing and verification of JWTs.

// 4. **generateToken Method**
// This method generates a JWT token for a user based on their email.
// - The `subject` of the token is set to the user's email, which is used as an identifier.
// - The `issuedAt` is set to the current date and time.
// - The `expiration` is set to 7 days from the issue date, ensuring the token expires after one week.
// - The token is signed using the signing key generated by `getSigningKey()`, making it secure and tamper-proof.
// The method returns the JWT token as a string.

// 5. **extractEmail Method**
// This method extracts the user's email (subject) from the provided JWT token.
// - The token is first verified using the signing key to ensure it hasn’t been tampered with.
// - After verification, the token is parsed, and the subject (which represents the email) is extracted.
// This method allows the application to retrieve the user's identity (email) from the token for further use.

// 6. **validateToken Method**
// This method validates whether a provided JWT token is valid for a specific user role (admin, doctor, or patient).
// - It first extracts the email from the token using the `extractEmail()` method.
// - Depending on the role (`admin`, `doctor`, or `patient`), it checks the corresponding repository (AdminRepository, DoctorRepository, or PatientRepository)
//   to see if a user with the extracted email exists.
// - If a match is found for the specified user role, it returns true, indicating the token is valid.
// - If the role or user does not exist, it returns false, indicating the token is invalid.
// - The method gracefully handles any errors by returning false if the token is invalid or an exception occurs.
// This ensures secure access control based on the user's role and their existence in the system.


}

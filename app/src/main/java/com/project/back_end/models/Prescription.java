package com.project.back_end.models;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id; // Maps to MongoDB's ObjectId _id field

    @NotNull(message = "Patient name is required.")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters.")
    private String patientName;

    @NotNull(message = "Appointment ID reference is required.")
    private Long appointmentId; // Reference to MySQL Appointment Entity ID

    @NotNull(message = "Medication name is required.")
    @Size(min = 3, max = 100, message = "Medication must be between 3 and 100 characters.")
    private String medication;

    @NotNull(message = "Dosage details are required.")
    @Size(min = 3, max = 20, message = "Dosage must be between 3 and 20 characters.")
    private String dosage;

    @Size(max = 200, message = "Doctor notes cannot exceed 200 characters.")
    private String doctorNotes; // Optional field

    // Embedded Document for metadata
    @Field("metadata")
    private Metadata metadata;

    // No-argument constructor (Required for Spring Data MongoDB)
    public Prescription() {
        this.metadata = new Metadata(LocalDateTime.now(), 1); // Initialize metadata on creation
    }

    // Parameterized constructor for easy object creation
    public Prescription(String patientName, Long appointmentId, String medication, String dosage, String doctorNotes) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
        this.metadata = new Metadata(LocalDateTime.now(), 1); // Initialize metadata
    }

    // ------------------------------------------------------------------
    // Embedded Metadata Class
    // ------------------------------------------------------------------
    /**
     * Represents embedded metadata fields for auditing and versioning.
     */
    public static class Metadata {
        private LocalDateTime createdAt;
        private int version;

        public Metadata(LocalDateTime createdAt, int version) {
            this.createdAt = createdAt;
            this.version = version;
        }

        // Getters and Setters for Metadata fields
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
    }

    // ------------------------------------------------------------------

    // Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}

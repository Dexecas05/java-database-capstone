package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One relationship with Doctor entity (FK: doctor_id)
    @NotNull(message = "Doctor is required for an appointment.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Many-to-One relationship with Patient entity (FK: patient_id)
    @NotNull(message = "Patient is required for an appointment.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull
    @Future(message = "Appointment date and time must be in the future.")
    private LocalDateTime appointmentTime;

    @NotNull
    private int status; // 0: Scheduled, 1: Completed, 2: Cancelled

    // Constructors
    public Appointment() {
        // Required by JPA
    }

    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Helper methods (not persisted in the database)
    /**
     * Returns the end time of the appointment, calculated as 1 hour after the start time.
     */
    @Transient
    public LocalDateTime getEndTime() {
        // Assuming all appointments are 1 hour long as per the User Story
        return this.appointmentTime.plusHours(1);
    }

    /**
     * Returns only the date portion of the appointment.
     */
    @Transient
    public LocalDate getAppointmentDate() {
        return this.appointmentTime.toLocalDate();
    }

    /**
     * Returns only the time portion of the appointment.
     */
    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return this.appointmentTime.toLocalTime();
    }

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


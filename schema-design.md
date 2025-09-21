## 🧮 MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Not Null, Unique
- phone: VARCHAR(20), Not Null
- date_of_birth: DATE, Not Null
- gender: VARCHAR(10)
- address: TEXT
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

> Notes: Email and phone should be validated via backend logic. Patient records are retained even after account deactivation for medical history continuity.

---

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Not Null, Unique
- phone: VARCHAR(20), Not Null
- specialization: VARCHAR(100), Not Null
- working_days: VARCHAR(50), Not Null
- working_hours: VARCHAR(50), Not Null
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

> Notes: Doctors can update availability. Overlapping appointments should be prevented via backend logic.

---

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- notes: TEXT

> Notes: Appointments are retained for historical tracking. Deleting a patient should not auto-delete appointments unless explicitly requested.

---

### Table: admins
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Not Null, Unique
- email: VARCHAR(100), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- role: VARCHAR(20), Default 'admin'
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

> Notes: Admins manage access and oversee system operations. Passwords are stored as hashes for security.

---

### Table: payments (optional)
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Foreign Key → patients(id)
- appointment_id: INT, Foreign Key → appointments(id)
- amount: DECIMAL(10,2), Not Null
- payment_date: DATETIME, Not Null
- method: VARCHAR(50)
- status: VARCHAR(20) (e.g., Paid, Pending, Refunded)

> Notes: Useful for future billing features. Payment records are linked to appointments and patients.

---

## 🍃 MongoDB Collection Design

### Collection: prescriptions

{
  "_id": "ObjectId('64f9c2a1e3b7a9d5f1a2c3d4')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "instructions": "Take 1 tablet every 6 hours",
      "duration": "5 days"
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "instructions": "Take after meals",
      "duration": "3 days"
    }
  ],
  "doctorNotes": "Patient reports mild headache and muscle pain. Prescribed basic analgesics.",
  "tags": ["pain", "fever", "non-critical"],
  "refillCount": 1,
  "pharmacy": {
    "name": "Farmácia Popular",
    "location": "Florianópolis - Centro"
  },
  "attachments": [
    {
      "fileName": "blood_test_results.pdf",
      "uploadedAt": "2025-09-20T14:32:00Z",
      "fileType": "application/pdf"
    }
  ],
  "createdAt": "2025-09-21T18:45:00Z",
  "updatedAt": "2025-09-21T18:45:00Z"
}

> 🧠 Design Insights
References over Embedding: We use patientId, doctorId, and appointmentId to link to MySQL records. This avoids duplication and keeps documents lean.

Arrays for Medications: Each prescription can include multiple medications with detailed instructions.

Nested Structures: Pharmacy info and attachments are embedded for quick access.

Tags: Useful for filtering or analytics (e.g., tracking common symptoms).

Schema Evolution: Adding fields like attachments or tags later won’t break existing documents—MongoDB handles this gracefully.

---

### Collection: feedback

{
  "_id": "ObjectId('64f9c2a1e3b7a9d5f1a2c3d4')",
  "patientId": 12,
  "doctorId": 7,
  "appointmentId": 51,
  "rating": 4.5,
  "comments": "Dr. Almeida was very attentive and explained everything clearly.",
  "tags": ["communication", "professionalism", "clarity"],
  "submittedAt": "2025-09-21T19:30:00Z",
  "isAnonymous": false,
  "metadata": {
    "submittedVia": "mobile",
    "language": "pt-BR"
  }
}

> 🧠 Design Notes
References: Links to patientId, doctorId, and appointmentId keep feedback tied to real interactions.

Rating + Comments: Combines quantitative and qualitative input.

Tags: Useful for filtering or identifying common themes.

Anonymous Option: Supports privacy-conscious submissions.

Metadata: Tracks how and where feedback was submitted—great for UX insights.
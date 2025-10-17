## MySQL Database Design

The relational database (MySQL) will handle the core, structured data: users, appointments, and general system configuration.

### 📌 Tables

| Table Name                | Primary Key (PK)        | Foreign Key (FK)                                               | Important Fields                                                                                       | Notes on Relationships                                                                                           |
|:--------------------------|:------------------------|:---------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| **`admins`**              | `admin_id` (INT)        | None                                                           | `username`, `password_hash`, `full_name`                                                               | Stores system administrator credentials.                                                                         |
| **`doctors`**             | `doctor_id` (INT)       | None                                                           | `first_name`, `last_name`, `email`, `specialization`, `contact_info`                                   | Stores information about healthcare providers.                                                                   |
| **`patients`**            | `patient_id` (INT)      | None                                                           | `first_name`, `last_name`, `email`, `date_of_birth`, `address`                                         | Stores user information for patients.                                                                            |
| **`appointments`**        | `appointment_id` (INT)  | `patient_id` (FK to `patients`), `doctor_id` (FK to `doctors`) | `start_time` (DATETIME), `end_time` (DATETIME), `status` (ENUM: 'Scheduled', 'Completed', 'Cancelled') | Links a patient to a doctor at a specific time. **Constraint:** Must ensure no time overlap for a single doctor. |
| **`doctor_availability`** | `availability_id` (INT) | `doctor_id` (FK to `doctors`)                                  | `day_of_week` (ENUM or INT), `start_time` (TIME), `end_time` (TIME)                                    | Defines the standard recurring time slots a doctor is generally available (e.g., Mon 9:00-17:00).                |

### 💡 Handling Doctor Available Times

We will use the **`doctor_availability`** table to manage the general, recurring schedule.

For **specific day-to-day slots** and cancellations, the application logic will:
1.  **Generate Slots:** When a patient views a doctor's schedule, the Service Layer uses the `doctor_availability` table to generate all potential time slots.
2.  **Filter Conflicts:** It then checks the **`appointments`** table for any confirmed or blocked slots for that specific doctor and day, and removes them from the list presented to the patient.
3.  **Manual Unavailability:** A Doctor can use the **Mark Unavailability** feature (from the User Story) to temporarily block specific dates or times, which can be stored as special records in the **`appointments`** table with a unique status (e.g., `status='Blocked'`).

### ❓ Design Question Answers

| Question                                                             | Recommended Action                                                                   | SQL Implementation Strategy                                                                                                                                                                                      |
|:---------------------------------------------------------------------|:-------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **What happens if a patient is deleted?**                            | **Soft Delete.** Instead of permanent deletion, update a flag.                       | Add a `is_active` (BOOLEAN) or `deleted_at` (DATETIME) column to the `patients` table. The application logic ignores inactive records.                                                                           |
| **Should appointments also be deleted?**                             | **No.** Past appointment history must be retained for medical and auditing purposes. | Keep the records. If the patient is "soft-deleted," their past appointments remain linked but are no longer active/searchable in the scheduling view.                                                            |
| **Should a doctor be allowed to have overlapping appointments?**     | **No.** This is a fundamental constraint for a clinic portal.                        | Use a **database constraint** or enforce strict application-level validation on the `appointments` table to prevent two records for the same `doctor_id` from having overlapping `start_time`/`end_time` ranges. |
| **Should each doctor have their own available time slots?**          | **Yes.** Availability varies by practitioner.                                        | Handled by the **`doctor_availability`** table, which is tied directly to the `doctor_id`.                                                                                                                       |
| **Should a patient's past appointment history be retained forever?** | **Yes,** for medical record keeping.                                                 | The `appointments` table is designed to retain this history. Only future/scheduled appointments are actively managed.                                                                                            |

---

## MongoDB Collection Design (NoSQL Schema)

MongoDB will handle less structured, dynamic, or high-volume data, such as prescriptions and patient feedback. This choice leverages MongoDB's flexible schema and efficient handling of embedded documents and arrays.

### 📜 Collection: `prescriptions`

This collection stores the details of medications issued, serving as a record of the clinical outcome tied to a specific appointment.

| Field Name           | Data Type                    | Description                                                               | Notes on Relationships                                                            |
|:---------------------|:-----------------------------|:--------------------------------------------------------------------------|:----------------------------------------------------------------------------------|
| **`_id`**            | `ObjectId`                   | MongoDB's Primary Key.                                                    | Auto-generated by MongoDB.                                                        |
| **`appointment_id`** | `INT`                        | **Reference** to the associated `appointment_id` from the MySQL database. | Mandatory field; links the prescription to the clinical encounter.                |
| **`doctor_id`**      | `INT`                        | The ID of the prescribing doctor (reference to MySQL).                    |                                                                                   |
| **`patient_id`**     | `INT`                        | The ID of the patient (reference to MySQL).                               |                                                                                   |
| **`issue_date`**     | `Date`                       | The date the prescription was issued.                                     |                                                                                   |
| **`medications`**    | `Array of Objects`           | Flexible structure for medication details (e.g., dosage, instructions).   | Example: `[ {name: "Drug A", dosage: "50mg", instructions: "Twice daily"}, ... ]` |
| **`notes`**          | `String`                     | Any special instructions or clinical notes from the doctor.               |                                                                                   |
| **`metadata`**       | `Object` (Embedded Document) | Tracking and auditing information for the document.                       | Includes fields like `created_at` (Date) and `version` (INT).                     |

---

### 💬 Collection: `feedback`

This collection stores patient ratings and textual comments regarding their experience with doctors or the service. It is ideal for MongoDB due to its high write volume and flexible tagging structure.

| Field Name           | Data Type          | Description                                                                |
|:---------------------|:-------------------|:---------------------------------------------------------------------------|
| **`_id`**            | `ObjectId`         | Primary Key.                                                               |
| **`patient_id`**     | `INT`              | The ID of the patient giving feedback (reference to MySQL).                |
| **`doctor_id`**      | `INT`              | The ID of the doctor being rated (reference to MySQL).                     |
| **`appointment_id`** | `INT`              | Optional reference to the appointment that triggered the feedback.         |
| **`rating`**         | `INT`              | The patient's score (e.g., 1 to 5).                                        |
| **`comment`**        | `String`           | The actual text feedback provided by the patient.                          |
| **`tags`**           | `Array of Strings` | Categorization of the feedback (e.g., `["communication", "punctuality"]`). |
| **`submitted_at`**   | `Date`             | Timestamp of when the feedback was submitted.                              |


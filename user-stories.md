# Admin User Stories

## Story 1 

**Title:**  
_As an Admin, I want to log in securely, so that I can access the Admin Dashboard only with valid credentials._

**Acceptance Criteria:**
1. System verifies username/password against stored credentials.
2. On success, admin is directed to the main Admin Dashboard.
3. On failure, an "Invalid credentials" error message is displayed.

**Priority:** High  
**Story Points:** 1

**Notes:**
- N/A

**Labels:** `auth`, `backend`

## Story 2

**Title:**  
_As an Admin, I want to log out, so that my session is securely terminated and I return to the login page._

**Acceptance Criteria:**
1. User session is immediately terminated on the backend.
2. Admin is redirected to the public login page.

**Priority:** High  
**Story Points:** 1

**Notes:**
- N/A

**Labels:** `auth`, `backend`

## Story 3

**Title:**  
_As an Admin, I want to add a new doctor, so that they can be registered in the system with proper credentials._

**Acceptance Criteria:**
1. Admin can submit a form with the doctor's name, specialization, and initial credentials.
2. A new record is created in the MySQL database.
3. Confirmation message is displayed upon successful creation.

**Priority:** High  
**Story Points:** 3

**Notes:**
- Ensure credentials meet minimum security standards.

**Labels:** `admin`, `backend`, `form`

## Story 4

**Title:**  
_As an Admin, I want to delete a doctor profile, so that outdated or incorrect records can be removed from the system._

**Acceptance Criteria:**
1. Admin can select a doctor from a list and confirm deletion.
2. The doctor's record is permanently removed from the MySQL database.
3. Related historical data (e.g., past appointments) should be archived or handled according to policy.

**Priority:** Medium  
**Story Points:** 5

**Notes:**
- Requires confirmation step to prevent accidental deletion.

**Labels:** `admin`, `backend`, `data-management`


## Story 5

**Title:**  
_As an Admin, I want to track monthly appointments, so that I can monitor system usage and trends._

**Acceptance Criteria:**
1. Admin can trigger a pre-defined command or script.
2. The system executes a MySQL stored procedure.
3. The procedure returns a count of appointments grouped by month.

**Priority:** Medium  
**Story Points:** 8

**Notes:**
- The output format should be easily readable for reporting.

**Labels:** `reporting`, `backend`, `analytics`

---

# Doctor User Stories

## Story 1

**Title:**  
_As a Doctor, I want to log in to manage appointments, so that I can access my dashboard and calendar._

**Acceptance Criteria:**
1. System verifies username/password against stored credentials.
2. On success, doctor is directed to their main dashboard/calendar view.

**Priority:** High  
**Story Points:** 1

**Notes:**
- N/A

**Labels:** `auth`, `doctor`, `backend`

## Story 2

**Title:**  
_As a Doctor, I want to log out, so that my session is securely terminated and I return to the login page._

**Acceptance Criteria:**
1. User session is immediately terminated on the backend.
2. Doctor is redirected to the public login page.

**Priority:** High  
**Story Points:** 1

**Notes:**
- N/A

**Labels:** `auth`, `doctor`, `backend`

# Story 3

**Title:**  
_As a Doctor, I want to view my appointment calendar, so that I can see my schedule in a clear and organized format._

**Acceptance Criteria:**
1. Doctor's dashboard displays a monthly/weekly calendar view.
2. Scheduled appointments are clearly marked with patient names and times.
3. Doctor can toggle between different calendar views (day/week/month).

**Priority:** High  
**Story Points:** 5

**Notes:**
- The calendar should only show appointments for the logged-in doctor.

**Labels:** `doctor`, `frontend`, `calendar`

# Story 4

**Title:**  
_As a Doctor, I want to mark my unavailability, so that patients cannot book appointments during those times._

**Acceptance Criteria:**
1. Doctor can select specific time slots or days on the calendar to block.
2. The blocked time slots are immediately reflected as unavailable for patients attempting to book.

**Priority:** High  
**Story Points:** 3

**Notes:**
- Recurring unavailability (e.g., every Tuesday morning) should be supported.

**Labels:** `doctor`, `calendar`, `availability`

# Story 5

**Title:**  
_As a Doctor, I want to update my profile information, so that my contact and specialization details remain accurate._

**Acceptance Criteria:**
1. Doctor can access a profile editing page.
2. Fields for specialization, contact number, and other relevant details are editable.
3. Changes are successfully saved to the MySQL database upon submission.

**Priority:** Medium  
**Story Points:** 3

**Notes:**
- Input validation is required for all fields.

**Labels:** `doctor`, `profile`, `form`, `backend`

# Story 6

**Title:**  
_As a Doctor, I want to view patient details, so that I can prepare for upcoming appointments with relevant medical history._

**Acceptance Criteria:**
1. Doctor can click on an upcoming appointment in the calendar.
2. The system displays the associated patient's name and relevant medical history (if authorized).

**Priority:** High  
**Story Points:** 5

**Notes:**
- Patient data viewing must comply with privacy (HIPAA-like) rules.

**Labels:** `doctor`, `patient-data`, `privacy`, `backend`

---

# Patient User Stories

## Story 1

**Title:**  
_As a Patient, I want to browse doctors without logging in, so that I can find the right specialist before signing up._

**Acceptance Criteria:**
1. The public landing page displays a searchable list of all available doctors.
2. The list includes the doctor's name, specialization, and contact info.
3. The list is accessible without requiring a login or sign-up.

**Priority:** High  
**Story Points:** 2

**Notes:**
- Ability to filter/sort doctors (e.g., by specialization) should be included.

**Labels:** `patient`, `frontend`, `public-access`

## Story 2

**Title:**  
_As a Patient, I want to sign up, so that I can create an account and start booking appointments._

**Acceptance Criteria:**
1. Patient can submit a registration form with email, password, and basic details.
2. A new patient record is created in the MySQL database.
3. A confirmation email or prompt is displayed upon successful registration.

**Priority:** High  
**Story Points:** 3

**Notes:**
- Password hashing and verification are required.

**Labels:** `auth`, `patient`, `backend`, `form`

## Story 3

**Title:**  
_As a Patient, I want to log in to manage bookings, so that I can access my dashboard and schedule appointments._

**Acceptance Criteria:**
1. System verifies email/password against stored credentials.
2. On success, patient is directed to their main dashboard to manage appointments.

**Priority:** High  
**Story Points:** 1

**Notes:**
- N/A

**Labels:** `auth`, `patient`, `backend`

## Story 4

**Title:**  
_As a Patient, I want to log out, so that my session is securely terminated and I return to the login page._

**Acceptance Criteria:**
1. User session is immediately terminated on the backend.
2. Patient is redirected to the public login page.

**Priority:** High  
**Story Points:** 1

**Notes:**
- N/A

**Labels:** `auth`, `patient`, `backend`

## Story 5

**Title:**  
_As a Patient, I want to book an hour-long appointment, so that I can schedule time with a doctor._

**Acceptance Criteria:**
1. Patient selects a doctor and views their available time slots.
2. Patient can select an open 60-minute slot.
3. An appointment record is created in the MySQL database, consuming that time slot.

**Priority:** High  
**Story Points:** 5

**Notes:**
- Booking must respect the doctor's marked unavailability.

**Labels:** `patient`, `booking`, `backend`, `calendar`

## Story 6

**Title:**  
_As a Patient, I want to view my upcoming appointments, so that I can keep track of my scheduled visits._

**Acceptance Criteria:**
1. Patient's dashboard displays a list of all future appointments.
2. Each entry clearly shows the doctor's name, date, and time.

**Priority:** High  
**Story Points:** 2

**Notes:**
- Appointments should be easily cancelable by the patient up to a certain cutoff time.

**Labels:** `patient`, `frontend`, `appointments`

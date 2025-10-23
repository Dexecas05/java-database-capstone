// patientRecordRow.js
/*
export function createPatientRecordRow(patient) {
  const tr = document.createElement("tr");
  tr.innerHTML = `
      <td class="patient-id">${patient.appointmentDate}</td>
      <td>${patient.id}</td>
      <td>${patient.patientId}</td>
      <td><img src="../assets/images/addPrescriptionIcon/addPrescription.png" alt="addPrescriptionIcon" class="prescription-btn" data-id="${patient.id}"></img></td>
    `;

  // Attach event listeners
  tr.querySelector(".prescription-btn").addEventListener("click", () => {
    window.location.href = `/pages/addPrescription.html?mode=view&appointmentId=${patient.id}`;
  });

  return tr;
}
*/

/**
 * Generates a table row (<tr>) for a single historical patient record (completed appointment).
 * This is typically used in the Doctor's view of a patient's history.
 * @param {Object} record - The patient record object (expected to be a completed appointment).
 * @returns {HTMLTableRowElement} The created table row element.
 */
export function createPatientRecordRow(record) {
  const tr = document.createElement("tr");

  // Use a 'view' icon (eye) since this row represents a historical, completed record
  // where the doctor would want to view the prescription, not add a new one.
  const viewIcon = `
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-eye">
        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
        <circle cx="12" cy="12" r="3"></circle>
    </svg>
  `;

  // NOTE: Assuming the 'record' object contains the appointment ID and the date/time of consultation
  tr.innerHTML = `
      <td>${record.appointmentDate || 'N/A'}</td>
      <td>${record.appointmentTime || 'N/A'}</td>
      <td>${record.doctorId || 'Unknown'}</td>
      <td>${record.status || 'Consulted'}</td>
      <td>
          <button class="table-action-btn view-prescription-btn" title="View Prescription">
              ${viewIcon}
          </button>
      </td>
    `;

  // Attach event listener for viewing the prescription/full record details
  const viewButton = tr.querySelector(".view-prescription-btn");
  if (viewButton) {
    viewButton.addEventListener("click", (e) => {
        e.stopPropagation();
        // Redirects to the prescription page in 'view' mode using the appointment ID
        window.location.href = `/pages/addPrescription.html?mode=view&appointmentId=${record.id}`;
    });
  }

  return tr;
}

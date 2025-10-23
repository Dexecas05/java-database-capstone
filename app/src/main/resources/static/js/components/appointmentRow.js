// appointmentRow.js
/*
export function getAppointments(appointment) {
  const tr = document.createElement("tr");

  tr.innerHTML = `
      <td class="patient-id">${appointment.patientName}</td>
      <td>${appointment.doctorName}</td>
      <td>${appointment.date}</td>
      <td>${appointment.time}</td>
      <td><img src="../assets/images/edit/edit.png" alt="action" class="prescription-btn" data-id="${appointment.id}"></img></td>
    `;

  // Attach event listeners
  tr.querySelector(".prescription-btn").addEventListener("click", () => {
    window.location.href = `addPrescription.html?id=${patient.id}`;
  });

  return tr;
}
*/

/**
 * Generates a table row (<tr>) for a single appointment, used in the Patient Dashboard.
 * @param {Object} appointment - The appointment data object.
 * @returns {HTMLTableRowElement} The created table row element.
 */
export function createAppointmentRow(appointment) {
  const tr = document.createElement("tr");

  // Use a modern SVG icon for the action button (Pen/Edit icon to suggest reviewing/editing the booking).
  const editIcon = `
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-edit">
        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
    </svg>
  `;

  tr.innerHTML = `
      <td>${appointment.patientName}</td>
      <td>${appointment.doctorName}</td>
      <td>${appointment.date}</td>
      <td>${appointment.time}</td>
      <td>
          <button class="table-action-btn edit-appointment-btn" title="View/Edit Appointment">
              ${editIcon}
          </button>
      </td>
    `;

  // Attach event listener to redirect to an appointment detail page using the appointment ID
  const editButton = tr.querySelector(".edit-appointment-btn");
  if (editButton) {
    editButton.addEventListener("click", (e) => {
      e.stopPropagation();
      // Use the appointment ID for redirection
      window.location.href = `/pages/appointmentDetails.html?appointmentId=${appointment.id}`;
    });
  }

  return tr;
}

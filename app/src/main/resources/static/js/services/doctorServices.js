/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/

// doctorServices.js
// Centralized module for all API interactions related to doctor data (GET, POST, DELETE, Filter).

import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Sends a GET request to retrieve the list of all doctors.
 * @returns {Promise<Array>} A promise that resolves to an array of doctor objects, or an empty array on error.
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);

        if (!response.ok) {
            console.error(`Failed to fetch doctors. Status: ${response.status}`);
            return [];
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Network or unexpected error while fetching doctors:", error);
        return [];
    }
}

/**
 * Sends a POST request to add a new doctor record to the system.
 * @param {object} doctor - The doctor object containing details (name, email, specialty, etc.).
 * @param {string} token - The Admin authentication token.
 * @returns {Promise<{success: boolean, message: string}>} A structured response object.
 */
export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Secure API call with token
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();

        if (response.ok) {
            return {
                success: true,
                message: "Doctor added successfully!",
                doctor: data // Assuming the saved doctor object is returned
            };
        } else {
            console.error("Failed to save doctor:", data.message || response.statusText);
            return {
                success: false,
                message: data.message || "Failed to save doctor due to server error."
            };
        }
    } catch (error) {
        console.error("Network error while saving doctor:", error);
        return { success: false, message: "Network error occurred." };
    }
}

/**
 * Sends a DELETE request to remove a doctor record by ID.
 * @param {number} id - The unique ID of the doctor to delete.
 * @param {string} token - The Admin authentication token.
 * @returns {Promise<boolean>} True if deletion was successful, false otherwise.
 */
export async function deleteDoctor(id, token) {
    try {
        // Construct the specific deletion endpoint: /doctor/{id}
        const deleteUrl = `${DOCTOR_API}/${id}`;

        const response = await fetch(deleteUrl, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}` // Requires Admin token
            }
        });

        if (response.ok) {
            // Note: A successful DELETE often returns a 204 No Content, but checking response.ok is sufficient.
            console.log(`Doctor ID ${id} deleted successfully.`);
            return true;
        } else {
            const errorData = await response.json().catch(() => ({ message: response.statusText }));
            console.error(`Failed to delete doctor ${id}. Status: ${response.status}. Message: ${errorData.message}`);
            return false;
        }
    } catch (error) {
        console.error("Network or unexpected error during doctor deletion:", error);
        return false;
    }
}

/**
 * Sends a GET request to filter doctors based on name, time, and specialty.
 * @param {string} name - Doctor's name for searching.
 * @param {string} time - Availability time filter (e.g., "AM", "PM").
 * @param {string} specialty - Specialty filter.
 * @returns {Promise<Array>} A promise that resolves to an array of filtered doctor objects, or an empty array on error.
 */
export async function filterDoctors(name, time, specialty) {
    try {
        // Construct query parameters, ensuring empty values are handled gracefully
        const params = new URLSearchParams();
        if (name) params.append('name', name);
        if (time) params.append('time', time);
        if (specialty) params.append('specialty', specialty);

        const filterUrl = `${DOCTOR_API}/filter?${params.toString()}`;

        const response = await fetch(filterUrl);

        if (!response.ok) {
            console.error(`Failed to filter doctors. Status: ${response.status}`);
            return [];
        }

        const data = await response.json();
        return data; // Returns the filtered array of doctors
    } catch (error) {
        console.error("Network or unexpected error while filtering doctors:", error);
        return [];
    }
}


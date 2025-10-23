/*
// patientServices
import { API_BASE_URL } from "../config/config.js";
const PATIENT_API = API_BASE_URL + '/patient'


//For creating a patient in db
export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}`,
      {
        method: "POST",
        headers: {
          "Content-type": "application/json"
        },
        body: JSON.stringify(data)
      }
    );
    const result = await response.json();
    if (!response.ok) {
      throw new Error(result.message);
    }
    return { success: response.ok, message: result.message }
  }
  catch (error) {
    console.error("Error :: patientSignup :: ", error)
    return { success: false, message: error.message }
  }
}

//For logging in patient
export async function patientLogin(data) {
  console.log("patientLogin :: ", data)
  return await fetch(`${PATIENT_API}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(data)
  });


}

// For getting patient data (name ,id , etc ). Used in booking appointments
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);
    const data = await response.json();
    if (response.ok) return data.patient;
    return null;
  } catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

// the Backend API for fetching the patient record(visible in Doctor Dashboard) and Appointments (visible in Patient Dashboard) are same based on user(patient/doctor).
export async function getPatientAppointments(id, token, user) {
  try {
    const response = await fetch(`${PATIENT_API}/${id}/${user}/${token}`);
    const data = await response.json();
    console.log(data.appointments)
    if (response.ok) {
      return data.appointments;
    }
    return null;
  }
  catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

export async function filterAppointments(condition, name, token) {
  try {
    const response = await fetch(`${PATIENT_API}/filter/${condition}/${name}/${token}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.ok) {
      const data = await response.json();
      return data;

    } else {
      console.error("Failed to fetch doctors:", response.statusText);
      return { appointments: [] };

    }
  } catch (error) {
    console.error("Error:", error);
    alert("Something went wrong!");
    return { appointments: [] };
  }
}
*/

// patientServices.js
// Centralized module for all API interactions related to patient data and appointments.

import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = API_BASE_URL + '/patient';
const APPOINTMENT_API = API_BASE_URL + '/appointment';

/**
 * Handles the patient registration process.
 * @param {object} data - Patient details (name, email, password, etc.).
 * @returns {Promise<{success: boolean, message: string, token?: string}>} Structured response.
 */
export async function patientSignup(data) {
    try {
        const response = await fetch(`${PATIENT_API}/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        const responseData = await response.json();

        if (response.ok) {
            // Assuming successful signup returns a token
            return {
                success: true,
                message: "Signup successful!",
                token: responseData.token || null
            };
        } else {
            console.error("Signup failed:", responseData.message || response.statusText);
            return {
                success: false,
                message: responseData.message || "Signup failed due to server error."
            };
        }
    } catch (error) {
        console.error("Network error during patient signup:", error);
        return { success: false, message: "Network error occurred." };
    }
}

/**
 * Handles the patient login process.
 * @param {object} data - Patient credentials (email and password).
 * @returns {Promise<Response>} The raw fetch response object.
 */
export async function patientLogin(data) {
    // This function returns the raw response for the caller to handle token extraction and redirection.
    try {
        const response = await fetch(`${PATIENT_API}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        return response;
    } catch (error) {
        console.error("Network error during patient login:", error);
        // Returning a simulated error response for consistency
        return { ok: false, status: 500, json: async () => ({ message: "Network error" }) };
    }
}

/**
 * Retrieves the logged-in patient's profile data.
 * @param {string} token - The patient's authentication token.
 * @returns {Promise<object | null>} The patient object or null on failure.
 */
export async function getPatientData(token) {
    try {
        const response = await fetch(PATIENT_API, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Secure API call
            }
        });

        if (response.ok) {
            return await response.json();
        } else {
            console.error(`Failed to fetch patient data. Status: ${response.status}`);
            return null;
        }
    } catch (error) {
        console.error("Network error while fetching patient data:", error);
        return null;
    }
}

/**
 * Retrieves appointments for either a patient or a doctor.
 * @param {string} id - The ID of the patient or doctor.
 * @param {string} token - The user's authentication token.
 * @param {string} user - The requesting user role ("patient" or "doctor").
 * @returns {Promise<Array | null>} Array of appointments or null on failure.
 */
export async function getPatientAppointments(id, token, user) {
    try {
        // Construct a dynamic URL based on the requesting user
        let fetchUrl;
        if (user === "patient") {
            // Endpoint to get all appointments for a specific patient
            fetchUrl = `${APPOINTMENT_API}/patient/${id}`;
        } else if (user === "doctor") {
            // Endpoint to get all appointments scheduled with a specific doctor
            fetchUrl = `${APPOINTMENT_API}/doctor/${id}`;
        } else {
            console.error("Invalid user role provided to getPatientAppointments.");
            return null;
        }

        const response = await fetch(fetchUrl, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            return await response.json();
        } else {
            console.error(`Failed to fetch appointments. Status: ${response.status}`);
            return null;
        }
    } catch (error) {
        console.error("Network error while fetching appointments:", error);
        return null;
    }
}

/**
 * Filters appointments based on condition and name.
 * @param {string} condition - The status condition (e.g., "pending", "consulted").
 * @param {string} name - Patient's name filter.
 * @param {string} token - The doctor's authentication token.
 * @returns {Promise<Array>} Array of filtered appointments or empty array on failure.
 */
export async function filterAppointments(condition, name, token) {
    try {
        const params = new URLSearchParams();
        // Append parameters only if they exist
        if (condition) params.append('condition', condition);
        if (name) params.append('name', name);

        const filterUrl = `${APPOINTMENT_API}/filter?${params.toString()}`;

        const response = await fetch(filterUrl, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Assuming Doctor is performing the filtering
            }
        });

        if (response.ok) {
            return await response.json();
        } else {
            const errorData = await response.json().catch(() => ({ message: response.statusText }));
            console.error(`Failed to filter appointments. Status: ${response.status}. Message: ${errorData.message}`);
            return [];
        }
    } catch (error) {
        console.error("Network error during appointment filtering:", error);
        return [];
    }
}

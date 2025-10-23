// util.js
/*
  function setRole(role) {
    localStorage.setItem("userRole", role);
  }
  
  function getRole() {
    return localStorage.getItem("userRole");
  }
  
  function clearRole() {
    localStorage.removeItem("userRole");
  }
*/

/**
 * Global utility module for simple data management using localStorage.
 * Exports functions for getting, setting, and clearing the user role.
 */

/**
 * Stores the user's current role in local storage.
 * @param {string} role - The role to set ('admin', 'patient', 'doctor', 'loggedPatient').
 */
export function setRole(role) {
  try {
    localStorage.setItem("userRole", role);
  } catch (e) {
    console.error("Error setting role in localStorage:", e);
  }
}

/**
 * Retrieves the user's role from local storage.
 * @returns {string | null} The stored role or null if not found.
 */
export function getRole() {
  try {
    return localStorage.getItem("userRole");
  } catch (e) {
    console.error("Error getting role from localStorage:", e);
    return null;
  }
}

/**
 * Removes the stored user role from local storage.
 */
export function clearRole() {
  try {
    localStorage.removeItem("userRole");
  } catch (e) {
    console.error("Error clearing role from localStorage:", e);
  }
}

// NOTE: We don't need to export these globally via window object unless they are used
// directly in HTML event handlers, which they are not in this case.

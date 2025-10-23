// footer.js
// Defines and injects the reusable static footer component.

/**
 * Renders the static footer content into the placeholder div (#footer).
 */
function renderFooter() {
    const footerDiv = document.getElementById("footer");

    if (!footerDiv) {
        console.error("Footer placeholder div (#footer) not found.");
        return;
    }

    // Define the HTML content for the footer
    footerDiv.innerHTML = `
        <footer class="footer">
            <div class="footer-section">

                <!-- Branding and Copyright Section -->
                <div class="footer-column footer-logo-section">
                    <div class="footer-logo">
                        <img src="/assets/images/logo/logo.png" alt="ClinicMS Logo" class="logo-img">
                        <h4>Clinic Management System</h4>
                    </div>
                    <p class="copyright">&copy; Copyright ${new Date().getFullYear()} | ClinicMS. All rights reserved.</p>
                </div>

                <!-- Column 1: Company Links -->
                <div class="footer-column">
                    <h4>Company</h4>
                    <a href="#" class="footer-link">About Us</a>
                    <a href="#" class="footer-link">Careers</a>
                    <a href="#" class="footer-link">Press</a>
                </div>

                <!-- Column 2: Support Links -->
                <div class="footer-column">
                    <h4>Support</h4>
                    <a href="#" class="footer-link">My Account</a>
                    <a href="#" class="footer-link">Help Center</a>
                    <a href="#" class="footer-link">Contact Us</a>
                </div>

                <!-- Column 3: Legal Links -->
                <div class="footer-column">
                    <h4>Legal</h4>
                    <a href="#" class="footer-link">Terms of Service</a>
                    <a href="#" class="footer-link">Privacy Policy</a>
                    <a href="#" class="footer-link">Licensing</a>
                </div>

            </div>

            <!-- Small text to ensure proper attribution -->
            <div class="footer-bottom">
                <p>Designed and Developed for Capstone Project.</p>
            </div>
        </footer>
    `;
}

// Ensure the footer is rendered immediately when the script loads
document.addEventListener('DOMContentLoaded', renderFooter);

// Export the function for potential use by render.js or other modules
export { renderFooter };


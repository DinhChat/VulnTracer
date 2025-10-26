document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registerForm");
    const errorMessage = document.getElementById("errorMessage");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        errorMessage.textContent = "";

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (password !== confirmPassword) {
            errorMessage.textContent = "Passwords do not match!";
            return;
        }

        try {
            const response = await fetch("/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                window.location.href = "/auth/login";
            } else {
                const data = await response.json().catch(() => ({}));
                errorMessage.textContent = data.message || "Registration failed!";
            }
        } catch (error) {
            console.error("Error:", error);
            errorMessage.textContent = "Network or server error!";
        }
    });
});

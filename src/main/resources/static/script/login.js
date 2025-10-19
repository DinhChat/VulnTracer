document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("loginForm");
    const errorMessage = document.getElementById("errorMessage");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        try {
            const response = await fetch("/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (!response.ok) {
                errorMessage.textContent = data.message || "Login failed!";
                return;
            }

            localStorage.setItem("token", data.jwtToken);
            localStorage.setItem("username", data.username);
            if (data.jwtToken != null) {
                window.location.href = "/home";
            }
        } catch (err) {
            console.error(err);
            errorMessage.textContent = "Server error. Please try again later.";
        }
    });
});

const loginForm = document.getElementById("loginForm");
const loginError = document.getElementById("loginError");
const loginErrorText = document.getElementById("loginErrorText");
const loginButton = document.getElementById("loginButton");

loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;

    hideError();

    if (!username || !password) {
        showError("Username and password are required.");
        return;
    }

    loginButton.disabled = true;
    loginButton.textContent = "Logging in...";

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (!response.ok) {
            showError(
                data.message ||
                "Incorrect username or password. Please try again."
            );
            return;
        }

        localStorage.setItem("jwtToken", data.token);
        localStorage.setItem("userId", data.userId);
        localStorage.setItem("username", data.username);

        window.location.href = "users.html";
    } catch (error) {
        console.error(error);
        showError("Unable to connect to the server.");
    } finally {
        loginButton.disabled = false;
        loginButton.textContent = "Login";
    }
});

function showError(message) {
    loginErrorText.textContent = message;
    loginError.classList.remove("hidden");
}

function hideError() {
    loginError.classList.add("hidden");
}

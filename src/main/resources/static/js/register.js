const registerForm =
    document.getElementById("registerForm");


const registerError =
    document.getElementById("registerError");


const registerErrorText =
    document.getElementById("registerErrorText");


const registerSuccess =
    document.getElementById("registerSuccess");


const registerButton =
    document.getElementById("registerButton");


registerForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        hideMessages();


        const username =
            document.getElementById("username")
                .value
                .trim();


        const password =
            document.getElementById("password")
                .value;


        const confirmPassword =
            document.getElementById("confirmPassword")
                .value;


        /*
         * Client-side validation
         */

        if (!username) {

            showError("Username is required.");

            return;
        }


        if (!password) {

            showError("Password is required.");

            return;
        }


        if (!confirmPassword) {

            showError("Confirm password is required.");

            return;
        }


        if (password.length < 6) {

            showError(
                "Password must contain at least 6 characters."
            );

            return;
        }


        if (password !== confirmPassword) {

            showError(
                "Passwords do not match."
            );

            return;
        }


        registerButton.disabled = true;

        registerButton.textContent =
            "Creating Account...";


        try {

            const response = await fetch(
                "/api/auth/register",
                {

                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({

                        username: username,

                        password: password,

                        confirmPassword: confirmPassword

                    })

                }
            );


            const data =
                await response.json();


            if (!response.ok) {

                showError(
                    data.message ||
                    "Unable to create account."
                );

                return;
            }


            /*
             * Show success message
             */

            registerSuccess.classList.remove(
                "hidden"
            );


            registerForm.reset();


            /*
             * After successful registration,
             * return to login after 1.5 seconds.
             */

            setTimeout(() => {

                window.location.href =
                    "index.html";

            }, 1500);


        } catch (error) {

            console.error(error);

            showError(
                "Unable to connect to the server."
            );

        } finally {

            registerButton.disabled = false;

            registerButton.textContent =
                "Create Account";

        }

    }
);


function showError(message) {

    registerErrorText.textContent = message;

    registerError.classList.remove(
        "hidden"
    );

}


function hideMessages() {

    registerError.classList.add(
        "hidden"
    );

    registerSuccess.classList.add(
        "hidden"
    );

}
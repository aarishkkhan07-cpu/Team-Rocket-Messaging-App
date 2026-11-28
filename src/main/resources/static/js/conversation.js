const token = localStorage.getItem("jwtToken");
const currentUserId = Number(localStorage.getItem("userId"));
const currentUsername = localStorage.getItem("username");
const selectedUserId = Number(localStorage.getItem("selectedUserId"));
const selectedUsername = localStorage.getItem("selectedUsername");

if (!token || !selectedUserId || !selectedUsername) {
    window.location.href = "users.html";
}

document.getElementById("currentUsername").textContent = currentUsername;
document.getElementById("conversationUsername").textContent =
    selectedUsername;
document.getElementById("conversationFooterUsername").textContent =
    selectedUsername;

const messagesContainer =
    document.getElementById("messagesContainer");
const messageForm =
    document.getElementById("messageForm");
const messageInput =
    document.getElementById("messageInput");
const sendButton =
    document.getElementById("sendButton");

async function loadMessages() {
    try {
        const response = await fetch(
            `/api/messages/${selectedUserId}`,
            {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            }
        );

        if (response.status === 401) {
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error("Unable to load messages");
        }

        const messages = await response.json();
        renderMessages(messages);
    } catch (error) {
        console.error(error);

        messagesContainer.innerHTML = `
            <div class="no-users">
                Unable to load conversation.
            </div>
        `;
    }
}

function renderMessages(messages) {
    messagesContainer.innerHTML = "";

    const dateElement = document.createElement("div");
    dateElement.className = "conversation-date";
    dateElement.textContent =
        messages.length > 0
            ? formatDate(messages[0].sentAt)
            : "Start of conversation";

    messagesContainer.appendChild(dateElement);

    if (!messages.length) {
        const emptyMessage = document.createElement("div");
        emptyMessage.className = "no-users";
        emptyMessage.textContent =
            "No messages yet. Start the conversation.";
        messagesContainer.appendChild(emptyMessage);
        return;
    }

    messages.forEach(message => {
        addMessageToUI(message, false);
    });

    scrollToBottom();
}

function addMessageToUI(message, scroll = true) {
    const isSent =
        Number(message.senderId) === currentUserId;

    const row = document.createElement("div");
    row.className =
        `message-row ${isSent ? "sent" : "received"}`;

    const meta = document.createElement("div");
    meta.className = "message-meta";

    const senderName = isSent ? "You" : selectedUsername;

    meta.textContent =
        `${senderName} - ${formatTime(message.sentAt)}`;

    const bubble = document.createElement("div");
    bubble.className = "message-bubble";
    bubble.textContent = message.content;

    row.appendChild(meta);
    row.appendChild(bubble);
    messagesContainer.appendChild(row);

    if (scroll) {
        scrollToBottom();
    }
}

messageForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const content = messageInput.value.trim();

    if (!content) {
        return;
    }

    sendButton.disabled = true;

    try {
        const response = await fetch("/api/messages", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                receiverId: selectedUserId,
                content
            })
        });

        if (response.status === 401) {
            logout();
            return;
        }

        if (!response.ok) {
            const error = await response.json();
            alert(
                error.message ||
                "Unable to send message."
            );
            return;
        }

        const message = await response.json();

        messageInput.value = "";
        addMessageToUI(message, true);
        messageInput.focus();
    } catch (error) {
        console.error(error);
        alert("Unable to connect to the server.");
    } finally {
        sendButton.disabled = false;
    }
});

document.getElementById("backButton")
    .addEventListener("click", function () {
        window.location.href = "users.html";
    });

document.getElementById("logoutButton")
    .addEventListener("click", logout);

function logout() {
    localStorage.clear();
    window.location.href = "index.html";
}

function formatTime(timestamp) {
    if (!timestamp) {
        return "";
    }

    return new Date(timestamp).toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit"
    });
}

function formatDate(timestamp) {
    if (!timestamp) {
        return "";
    }

    return new Date(timestamp).toLocaleDateString("en-US", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric"
    });
}

function scrollToBottom() {
    messagesContainer.scrollTop =
        messagesContainer.scrollHeight;
}

loadMessages();
messageInput.focus();

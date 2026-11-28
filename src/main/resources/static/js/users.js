const token = localStorage.getItem("jwtToken");
const currentUsername = localStorage.getItem("username");

if (!token) {
    window.location.href = "index.html";
}

document.getElementById("currentUsername").textContent =
    currentUsername || "User";

const userList = document.getElementById("userList");
const userCount = document.getElementById("userCount");
const searchInput = document.getElementById("searchUsers");
const selectedUserLabel = document.getElementById("selectedUserLabel");
const openConversationButton =
    document.getElementById("openConversationButton");

let allUsers = [];
let selectedUser = null;

async function loadUsers() {
    try {
        const response = await fetch("/api/users", {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.status === 401) {
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error("Unable to load users");
        }

        allUsers = await response.json();
        renderUsers(allUsers);
    } catch (error) {
        console.error(error);
        userList.innerHTML = `
            <div class="no-users">
                Unable to load users.
            </div>
        `;
    }
}

function renderUsers(users) {
    userList.innerHTML = "";

    userCount.textContent =
        `${users.length} ${users.length === 1 ? "user" : "users"}`;

    if (users.length === 0) {
        userList.innerHTML = `
            <div class="no-users">
                No users found.
            </div>
        `;
        return;
    }

    users.forEach(user => {
        const item = document.createElement("div");
        item.className = "user-item";
        item.dataset.userId = user.id;

        if (selectedUser && selectedUser.id === user.id) {
            item.classList.add("selected");
        }

        item.innerHTML = `
            <span class="user-name">
                ${escapeHtml(user.username)}
            </span>
            <span class="selected-indicator">
                <span>Selected</span>
                <span class="check-mark">✓</span>
            </span>
        `;

        item.addEventListener("click", () => selectUser(user));
        userList.appendChild(item);
    });
}

function selectUser(user) {
    selectedUser = user;

    selectedUserLabel.innerHTML =
        `Selected user: <strong>${escapeHtml(user.username)}</strong>`;

    openConversationButton.disabled = false;
    renderUsers(getFilteredUsers());
}

searchInput.addEventListener("input", function () {
    renderUsers(getFilteredUsers());
});

function getFilteredUsers() {
    const search = searchInput.value.trim().toLowerCase();

    if (!search) {
        return allUsers;
    }

    return allUsers.filter(user =>
        user.username.toLowerCase().includes(search)
    );
}

openConversationButton.addEventListener("click", function () {
    if (!selectedUser) {
        return;
    }

    localStorage.setItem("selectedUserId", selectedUser.id);
    localStorage.setItem("selectedUsername", selectedUser.username);

    window.location.href = "conversation.html";
});

document.getElementById("logoutButton")
    .addEventListener("click", logout);

function logout() {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");
    localStorage.removeItem("selectedUserId");
    localStorage.removeItem("selectedUsername");

    window.location.href = "index.html";
}

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value;
    return div.innerHTML;
}

loadUsers();

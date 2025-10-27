function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Invalid token", e);
        return null;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    const userSection = document.getElementById("userSection");
    const userMenu = document.getElementById("userMenu");
    const avatar = document.getElementById("avatar");
    const username = document.getElementById("username");

    const mainLayout = document.querySelector(".layout"); // Scans layout
    const settingLayout = document.getElementById("settingLayout"); // Settings layout
    const topNavLinks = document.querySelectorAll(".top_nav a");

    // Nếu chưa login
    if (!token) {
        userSection.innerHTML = `<button id="loginBtn" class="btn primary">Login</button>`;
        document.getElementById("loginBtn").addEventListener("click", () => {
            window.location.href = "/auth/login";
        });
        return;
    }

    // Hiển thị tên user
    const userInfo = parseJwt(token);
    username.textContent = userInfo?.sub || "User";

    // Dropdown user menu
    avatar.addEventListener("click", (e) => {
        e.stopPropagation();
        userMenu.classList.toggle("show");
    });
    document.addEventListener("click", (e) => {
        if (!userSection.contains(e.target)) {
            userMenu.classList.remove("show");
        }
    });

    const logoutBtn = document.getElementById("logoutBtn");
    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "/home";
    });

    const scansLink = topNavLinks[0];
    const settingsLink = topNavLinks[1];

    scansLink.addEventListener("click", (e) => {
        e.preventDefault();
        toggleLayout("scans");
    });

    settingsLink.addEventListener("click", (e) => {
        e.preventDefault();
        toggleLayout("settings");
    });

    function toggleLayout(mode) {
        topNavLinks.forEach(link => link.classList.remove("active"));
        if (mode === "scans") {
            scansLink.classList.add("active");
            settingLayout.classList.add("hidden");
            mainLayout.classList.remove("hidden");
            loadDefaultScanSection();
        } else {
            settingsLink.classList.add("active");
            mainLayout.classList.add("hidden");
            settingLayout.classList.remove("hidden");
            loadDefaultSettingSection();
        }
    }

    const scanMenuItems = document.querySelectorAll(".layout .menu-item");
    scanMenuItems.forEach(item => {
        item.addEventListener("click", (e) => {
            e.preventDefault();
            scanMenuItems.forEach(i => i.classList.remove("active"));
            item.classList.add("active");
            switchScanContent(item.dataset.section);
        });
    });

    function switchScanContent(section) {
        const sectionTitle = document.getElementById("sectionTitle");
        const contentBody = document.getElementById("contentBody");

        contentBody.classList.add("fade-out");
        setTimeout(() => {
            const { title, html } = getSectionContent(section);
            sectionTitle.textContent = title;
            contentBody.innerHTML = html;
            contentBody.classList.remove("fade-out");
        }, 250);
    }

    const settingMenuItems = settingLayout.querySelectorAll(".menu-item");
    settingMenuItems.forEach(item => {
        item.addEventListener("click", (e) => {
            e.preventDefault();
            settingMenuItems.forEach(i => i.classList.remove("active"));
            item.classList.add("active");
            switchSettingContent(item.dataset.section);
        });
    });

    function switchSettingContent(section) {
        const titleEl = document.getElementById("settingSectionTitle");
        const bodyEl = document.getElementById("settingContentBody");

        bodyEl.classList.add("fade-out");
        setTimeout(() => {
            const { title, html } = getSectionContent(section);
            titleEl.textContent = title;
            bodyEl.innerHTML = html;
            bodyEl.classList.remove("fade-out");
        }, 250);
    }

    function getSectionContent(section) {
        switch (section) {
            case "myScans":
                return { title: "My Scans", html: `<p>Your scans will appear here.</p>` };
            case "allScans":
                return { title: "All Scans", html: `<p>All available scans in the system.</p>` };
            case "trash":
                return { title: "Trash", html: `<p>Deleted scans are listed here.</p>` };
            case "policies":
                return { title: "Policies", html: `<p>Policy configurations.</p>` };
            case "pluginRules":
                return { title: "Plugin Rules", html: `<p>Manage scanning plugins.</p>` };
            case "about":
                return { title: "About", html: `<p>About Vulnerable Tracer - version 1.0</p>` };
            case "vulnerable":
                return { title: "Search Vulnerable", html: `<p>All vulnerabilities in the database (CWE/CVE).</p>` };
            case "profile":
                return { title: "Profile", html: `<p>My profile and account settings.</p>` };
            default:
                return { title: "Not Found", html: `<p>Section not found.</p>` };
        }
    }

    function loadDefaultScanSection() {
        document.querySelector('.layout .menu-item[data-section="myScans"]').click();
    }

    function loadDefaultSettingSection() {
        settingLayout.querySelector('.menu-item[data-section="about"]').click();
    }

    loadDefaultScanSection();
});

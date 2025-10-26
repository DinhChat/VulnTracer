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
    const mainContent = document.getElementById("mainContent");

    if (!token) {
        userSection.innerHTML = `<button id="loginBtn" class="btn primary">Login</button>`;
        document.getElementById("loginBtn").addEventListener("click", () => {
            window.location.href = "/auth/login";
        });
        return;
    }

    const userInfo = parseJwt(token);
    username.textContent = userInfo?.sub || "User";

    avatar.addEventListener("click", (e) => {
        e.stopPropagation();
        userMenu.classList.toggle("show");
    });

    document.addEventListener("click", (e) => {
        if (!userSection.contains(e.target)) {
            userMenu.classList.remove("show");
        }
    });

    // Xử lý logout
    const logoutBtn = document.getElementById("logoutBtn");
    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "/home";
    });

    // Hiệu ứng chuyển nội dung sidebar
    const menuItems = document.querySelectorAll(".menu-item");
    menuItems.forEach(item => {
        item.addEventListener("click", (e) => {
            e.preventDefault();
            menuItems.forEach(i => i.classList.remove("active"));
            item.classList.add("active");

            const section = item.dataset.section;
            switchContent(section);
        });
    });

    function switchContent(section) {
        const sectionTitle = document.getElementById("sectionTitle");
        const contentBody = document.getElementById("contentBody");

        mainContent.classList.remove("show");
        mainContent.classList.add("fade");

        setTimeout(() => {
            const { title, html } = getSectionContent(section);
            sectionTitle.textContent = title;
            contentBody.innerHTML = html;
            mainContent.classList.add("show");
        }, 200);
    }

    function getSectionContent(section) {
        switch (section) {
            case "myScans":
                return {
                    title: "My Scans",
                    html: `<p>Your scans will appear here.</p>`
                };
            case "allScans":
                return {
                    title: "All Scans",
                    html: `<p>All available scans in the system.</p>`
                };
            case "trash":
                return {
                    title: "Trash",
                    html: `<p>Deleted scans are listed here.</p>`
                };
            case "policies":
                return {
                    title: "Policies",
                    html: `<p>Policy configurations.</p>`
                };
            case "pluginRules":
                return {
                    title: "Plugin Rules",
                    html: `<p>Manage scanning plugins.</p>`
                };
            case "terrascan":
                return {
                    title: "Terrascan",
                    html: `<p>Infrastructure scan configurations.</p>`
                };
            default:
                return {
                    title: "Not Found",
                    html: `<p>Section not found.</p>`
                };
        }
    }
});

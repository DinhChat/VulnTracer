const CONFIG = {
    API_HOST: "http://localhost:8080",
    ENDPOINTS: {
        LOGIN: "/auth/login",
        ME: "/scan/me",
        APPS: "/application",
        APP_DETAIL: (id) => `/application/${id}`,
        APP_SCANS: (id) => `/scan/application/${id}`,
        TOOLS: "/tool",
        CREATE_SCAN: "/scan"
    }
};

// === UTILS ===
const Utils = {
    parseJwt(token) {
        try {
            return JSON.parse(decodeURIComponent(atob(token.split('.')[1]).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')));
        } catch (e) { return null; }
    },
    formatDate(dateString) {
        if (!dateString) return "N/A";
        return new Date(dateString).toLocaleString('vi-VN');
    },
    showLoading(element) {
        element.innerHTML = '<div class="empty-state"><div class="spinner" style="margin:0 auto"></div><p>Loading...</p></div>';
    }
};

// === API MANAGER ===
const API = {
    getHeaders() {
        const token = localStorage.getItem("token");
        if (!token) {
            window.location.href = CONFIG.ENDPOINTS.LOGIN;
            throw new Error("No token");
        }
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    },
    async fetch(endpoint, options = {}) {
        try {
            const res = await fetch(`${CONFIG.API_HOST}${endpoint}`, {
                ...options,
                headers: this.getHeaders()
            });
            if (res.status === 401) {
                localStorage.removeItem("token");
                window.location.href = CONFIG.ENDPOINTS.LOGIN;
            }
            if (!res.ok) throw new Error(await res.text());
            return await res.json();
        } catch (err) {
            console.error("API Error:", err);
            return null;
        }
    }
};

document.addEventListener("DOMContentLoaded", () => {
    // === AUTH CHECK ===
    const token = localStorage.getItem("token");
    if (!token) return window.location.href = CONFIG.ENDPOINTS.LOGIN;

    // === UI INITIALIZATION ===
    const userInfo = Utils.parseJwt(token);
    document.getElementById("username").textContent = userInfo?.sub || "Admin";

    // User Menu Toggle
    document.getElementById("avatar").addEventListener("click", (e) => {
        e.stopPropagation();
        document.getElementById("userMenu").classList.toggle("show");
    });
    document.addEventListener("click", () => document.getElementById("userMenu").classList.remove("show"));
    document.getElementById("logoutBtn").addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = CONFIG.ENDPOINTS.LOGIN;
    });

    // === CONTROLLERS ===

    // 1. Navigation Controller
    const NavController = {
        scanLayout: document.querySelector(".layout"),
        settingLayout: document.getElementById("settingLayout"),
        links: document.querySelectorAll(".top_nav a"),

        init() {
            this.links[0].addEventListener("click", (e) => { e.preventDefault(); this.switch("scans"); });
            this.links[1].addEventListener("click", (e) => { e.preventDefault(); this.switch("settings"); });

            // Sidebar clicks
            document.querySelectorAll(".sidebar .menu-item").forEach(item => {
                item.addEventListener("click", (e) => {
                    e.preventDefault();
                    this.updateSidebarActive(item);
                    const section = item.dataset.section;
                    item.closest('#scanLayout') ? ScanController.loadSection(section) : SettingsController.load(section);
                });
            });

            // Default load
            ScanController.loadSection("myScans");
        },

        switch(mode) {
            this.links.forEach(l => l.classList.remove("active"));
            if (mode === "scans") {
                this.links[0].classList.add("active");
                this.settingLayout.classList.add("hidden");
                this.scanLayout.classList.remove("hidden");
                document.querySelector('[data-section="myScans"]').click();
            } else {
                this.links[1].classList.add("active");
                this.scanLayout.classList.add("hidden");
                this.settingLayout.classList.remove("hidden");
            }
        },

        updateSidebarActive(item) {
            item.closest('.sidebar').querySelectorAll('.menu-item').forEach(i => i.classList.remove("active"));
            item.classList.add("active");
        }
    };

    // 2. Scan & App Controller (Main Logic)
    const ScanController = {
        container: document.getElementById("contentBody"),
        title: document.getElementById("sectionTitle"),
        headerActions: document.querySelector(".content-header .action-buttons"),

        async loadSection(section) {
            this.headerActions.classList.remove("hidden"); // Show buttons
            Utils.showLoading(this.container);

            if (section === "myScans") {
                this.title.textContent = "My Scans";
                const data = await API.fetch(CONFIG.ENDPOINTS.ME);
                this.renderScanTable(data);
            } else if (section === "allApplications") {
                this.title.textContent = "All Applications";
                const data = await API.fetch(CONFIG.ENDPOINTS.APPS);
                this.renderAppTable(data);
            } else if (section === "trash") {
                this.title.textContent = "Trash";
                this.container.innerHTML = '<div class="empty-state">Trash is empty.</div>';
            }
        },

        // --- RENDERERS ---
        renderScanTable(scans, targetContainer = this.container) {
            if (!scans || scans.length === 0) {
                targetContainer.innerHTML = '<div class="empty-state">No scans found.</div>';
                return;
            }
            const html = `
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Status</th>
                    <th>Started At</th>
                    <th>Message</th>
                </tr>
            </thead>
            <tbody>
                ${scans.map(s => `
                <tr onclick="window.location.href='/scan-detail/${s.scanId}'" style="cursor:pointer">
                    <td>#${s.scanId}</td>
                    <td>
                        <div class="status-cell status-${s.status.toLowerCase()}">
                            ${s.status === 'PENDING' ? '<div class="spinner"></div>' : ''} 
                            ${s.status}
                        </div>
                    </td>
                    <td>${Utils.formatDate(s.startedAt)}</td>
                    <td>${s.message || '-'}</td>
                </tr>
                `).join('')}
            </tbody>
        </table>`;
            targetContainer.innerHTML = html;
        },

        renderAppTable(apps) {
            if (!apps || apps.length === 0) return this.container.innerHTML = '<div class="empty-state">No apps found.</div>';

            const html = `
                <table class="data-table">
                    <thead><tr><th>ID</th><th>App Name</th><th>URL</th><th>Type</th><th>Last Scanned</th><th>Action</th></tr></thead>
                    <tbody>${apps.map(app => `
                        <tr>
                            <td>${app.applicationId}</td>
                            <td><a href="#" class="clickable-name" onclick="ScanController.viewAppDetail(${app.applicationId})">${app.applicationName}</a></td>
                            <td><a href="${app.applicationUrl}" target="_blank" class="app-link">${app.applicationUrl}</a></td>
                            <td><span class="badge">${app.applicationType}</span></td>
                            <td>${Utils.formatDate(app.lastScannedAt)}</td>
                            <td><button class="btn primary btn-sm" onclick="ModalController.openAddScan(${app.applicationId}, '${app.applicationName}')">+ Scan</button></td>
                        </tr>`).join('')}
                    </tbody>
                </table>`;
            this.container.innerHTML = html;
        },

        // --- NEW FEATURE: VIEW APP DETAIL ---
        async viewAppDetail(appId) {
            this.headerActions.classList.add("hidden");

            // Breadcrumb Header
            this.title.innerHTML = `
                <div class="breadcrumb-nav">
                    <button class="btn-back" onclick="NavController.links[0].click()">&#8592;</button>
                    <span>Applications</span> <span style="color:#ccc">/</span> <span>Details</span>
                </div>`;

            Utils.showLoading(this.container);

            try {
                // Gọi song song 2 API: Lấy thông tin App và List Scan
                const [appInfo, scanHistory] = await Promise.all([
                    API.fetch(CONFIG.ENDPOINTS.APP_DETAIL(appId)),
                    API.fetch(CONFIG.ENDPOINTS.APP_SCANS(appId))
                ]);

                if (!appInfo) throw new Error("App not found");

                // Render Info Card + Scan List
                this.container.innerHTML = `
                    <div class="app-detail-container">
                        <!-- 1. App Info Card -->
                        <div class="app-info-card">
                            <div class="info-item"><span class="info-label">Application Name</span><span class="info-value"><strong>${appInfo.applicationName}</strong></span></div>
                            <div class="info-item"><span class="info-label">URL</span><span class="info-value"><a href="${appInfo.applicationUrl}" target="_blank">${appInfo.applicationUrl}</a></span></div>
                            <div class="info-item"><span class="info-label">Type</span><span class="info-value">${appInfo.applicationType}</span></div>
                            <div class="info-item"><span class="info-label">Status</span><span class="info-value status-${appInfo.applicationStatus?.toLowerCase() || 'new'}">${appInfo.applicationStatus || 'NEW'}</span></div>
                            <div class="info-item"><span class="info-label">Created At</span><span class="info-value">${Utils.formatDate(appInfo.applicationCreatedAt)}</span></div>
                            <div class="info-item"><span class="info-label">Last Scanned</span><span class="info-value">${Utils.formatDate(appInfo.lastScannedAt)}</span></div>
                        </div>

                        <!-- 2. Scan History Section -->
                        <div class="section-divider">
                            <h3>Scan History</h3>
                            <button class="btn primary" onclick="ModalController.openAddScan(${appId}, '${appInfo.applicationName}')">+ New Scan</button>
                        </div>
                        <div id="appScanList"></div>
                    </div>
                `;

                // Render Table vào div con
                this.renderScanTable(scanHistory, document.getElementById("appScanList"));

            } catch (e) {
                this.container.innerHTML = `<div class="empty-state">Error: ${e.message}</div>`;
            }
        }
    };

    // 3. Modal Controller (Create & Add Scan)
    const ModalController = {
        createModal: document.getElementById('createScanModal'),
        addModal: document.getElementById('addScanToAppModal'),

        init() {
            // Bind Close Buttons
            document.querySelectorAll('.close-modal, .btn[onclick*="close"]').forEach(btn => {
                btn.removeAttribute("onclick"); // Clear html onclick
                btn.addEventListener("click", () => this.closeAll());
            });

            // Bind Open Button (Global New Scan)
            const btnOpen = document.getElementById("btnOpenScan");
            if (btnOpen) btnOpen.addEventListener("click", () => this.openCreate());

            // Bind Forms
            this.bindForm('createScanForm', true);
            this.bindForm('addScanToAppForm', false);
        },

        closeAll() {
            this.createModal.classList.add('hidden');
            this.addModal.classList.add('hidden');
        },

        async loadTools(containerId) {
            const container = document.getElementById(containerId);
            container.innerHTML = '<p class="loading-text">Loading tools...</p>';
            const tools = await API.fetch(CONFIG.ENDPOINTS.TOOLS);
            container.innerHTML = '';

            if (tools?.length) {
                tools.forEach(t => {
                    container.innerHTML += `
                        <div class="checkbox-item">
                            <input type="checkbox" id="t_${t.toolId}" name="scanTool" value="${t.toolName}">
                            <label for="t_${t.toolId}">${t.toolName} (${t.toolDescription}</label>
                        </div>`;
                });
            } else {
                container.innerHTML = '<p style="color:red">No tools available</p>';
            }
        },

        // Open "Create New App & Scan"
        async openCreate() {
            this.createModal.classList.remove('hidden');
            document.getElementById('createScanForm').reset();
            await this.loadTools('toolsContainer');
        },

        // Open "Add Scan to Existing App"
        async openAddScan(appId, appName) {
            document.getElementById('targetAppName').textContent = appName;
            document.getElementById('targetAppId').value = appId;
            this.addModal.classList.remove('hidden');
            await this.loadTools('toolsContainerForAdd');
        },

        bindForm(formId, isNewApp) {
            const form = document.getElementById(formId);
            if (!form) return;

            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                const btn = e.target.querySelector('button[type="submit"]');
                const oldText = btn.textContent;
                btn.textContent = "Processing..."; btn.disabled = true;

                // Collect Data
                const tools = Array.from(form.querySelectorAll('input[name="scanTool"]:checked')).map(cb => ({ name: cb.value }));
                if (!tools.length) { alert("Select a tool!"); btn.textContent = oldText; btn.disabled = false; return; }

                let payload = { scanTools: tools };
                let endpoint = "";

                if (isNewApp) {
                    payload.applicationName = document.getElementById('appName').value;
                    payload.applicationUrl = document.getElementById('appUrl').value;
                    payload.applicationType = document.getElementById('appType').value;
                    payload.applicationDescription = document.getElementById('appDesc').value;
                    endpoint = CONFIG.ENDPOINTS.CREATE_SCAN;
                } else {
                    const appId = document.getElementById('targetAppId').value;
                    // Các trường dummy để pass @Data validation (nếu cần)
                    payload.applicationName = "Existing"; payload.applicationUrl = "http://dummy"; payload.applicationType = "WEB";
                    endpoint = CONFIG.ENDPOINTS.APP_SCANS(appId);
                }

                const res = await fetch(CONFIG.API_HOST + endpoint, {
                    method: 'POST',
                    headers: API.getHeaders(),
                    body: JSON.stringify(payload)
                });

                if (res.ok) {
                    alert("Success!");
                    this.closeAll();
                    // Reload current view context
                    const currentTitle = document.getElementById("sectionTitle").textContent;
                    if (currentTitle.includes("Details")) {
                        const appId = document.getElementById(isNewApp ? 'N/A' : 'targetAppId').value;
                        if(appId) ScanController.viewAppDetail(appId);
                    } else {
                        document.querySelector('[data-section="myScans"]').click();
                    }
                } else {
                    alert("Error: " + await res.text());
                }
                btn.textContent = oldText; btn.disabled = false;
            });
        }
    };

    // Placeholder for Settings
    const SettingsController = {
        load(section) {
            document.getElementById("settingSectionTitle").textContent = section.toUpperCase();
            document.getElementById("settingContentBody").innerHTML = `<p>Setting section: ${section}</p>`;
        }
    };

    // Expose needed functions to Window for HTML onClick events (nếu cần)
    window.ScanController = ScanController;
    window.ModalController = ModalController;
    window.NavController = NavController;

    // === RUN ===
    NavController.init();
    ModalController.init();
});
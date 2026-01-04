const CONFIG = {
    API_HOST: "http://localhost:8080",
    ENDPOINTS: {
        LOGIN: "/auth/login",
        ME: "/scan/me",
        APPS: "/application",
        APP_DETAIL: (id) => `/application/${id}`,
        APP_SCANS: (id) => `/scan/application/${id}`,
        TOOLS: "/tool",
        CREATE_SCAN: "/scan",
        CWE_LIST: (page, size) => `/cwe?page=${page}&size=${size}`,
        CWE_DETAIL: (id) => `/cwe/id/${id}`,
        IMPORT_CWE: "/admin/upload-cwe"
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
    const isAdmin = userInfo?.role === "ADMIN" || userInfo?.role === "ROLE_ADMIN";

    if (isAdmin) {
        document.getElementById("adminSection").style.display = "block";
    }

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

                // Render Table
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

    //CWE controller

    const CweController = {
        currentPage: 0,
        pageSize: 15,

        // Gọi khi click vào menu "Vulnerability Database"
        async loadList(page = 0) {
            this.currentPage = page;
            const container = document.getElementById("settingContentBody");
            const title = document.getElementById("settingSectionTitle");
            const actions = document.getElementById("settingActions");

            title.textContent = "Common Weakness Enumeration (CWE)";
            actions.innerHTML = ""; // Xóa các nút cũ nếu có
            Utils.showLoading(container);

            try {
                // Fetch API: GET /cwe?page=x&size=y
                const data = await API.fetch(CONFIG.ENDPOINTS.CWE_LIST(page, this.pageSize));

                // Spring Page Response: { content: [], totalPages: 10, number: 0, ... }
                if (!data || !data.content) throw new Error("Invalid data format");

                this.renderTable(data, container);
            } catch (e) {
                container.innerHTML = `<div class="empty-state">Error loading CWEs: ${e.message}</div>`;
            }
        },

        renderTable(pageData, container) {
            const list = pageData.content;
            if (list.length === 0) {
                container.innerHTML = '<div class="empty-state">No CWEs found in database.</div>';
                return;
            }

            let html = `
                <table class="data-table">
                    <thead>
                        <tr>
                            <th width="10%">ID</th>
                            <th width="30%">Name</th>
                            <th width="50%">Short Description</th>
                            <th width="10%">Source</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            list.forEach(item => {
                // Cắt ngắn description
                const shortDesc = item.shortDescription
                    ? (item.shortDescription.length > 100 ? item.shortDescription.substring(0, 100) + "..." : item.shortDescription)
                    : "";

                html += `
                    <tr onclick="CweController.loadDetail('${item.cweId}')" style="cursor:pointer">
                        <td><strong>${item.cweId}</strong></td>
                        <td>${item.cweName}</td>
                        <td style="color:#666">${shortDesc}</td>
                        <td><span class="badge">${item.sourceFile || 'N/A'}</span></td>
                    </tr>
                `;
            });

            html += `</tbody></table>`;

            // Pagination Controls
            const hasNext = !pageData.last;
            const hasPrev = !pageData.first;

            html += `
                <div style="margin-top: 20px; display: flex; justify-content: center; gap: 10px; align-items: center;">
                    <button class="btn" ${!hasPrev ? 'disabled' : ''} onclick="CweController.loadList(${this.currentPage - 1})">Previous</button>
                    <span>Page ${pageData.number + 1} of ${pageData.totalPages}</span>
                    <button class="btn" ${!hasNext ? 'disabled' : ''} onclick="CweController.loadList(${this.currentPage + 1})">Next</button>
                </div>
            `;

            container.innerHTML = html;
        },

        async loadDetail(cweId) {
            const container = document.getElementById("settingContentBody");
            const title = document.getElementById("settingSectionTitle");

            // Breadcrumb Header
            title.innerHTML = `
                <div class="breadcrumb-nav">
                    <button class="btn-back" onclick="CweController.loadList(${this.currentPage})">&#8592;</button>
                    <span>CWE List</span> <span style="color:#ccc">/</span> <span>${cweId}</span>
                </div>`;

            Utils.showLoading(container);

            try {
                // Fetch API: GET /cwe/id/{cweId}
                const cwe = await API.fetch(CONFIG.ENDPOINTS.CWE_DETAIL(cweId));

                // Render Detail View
                container.innerHTML = `
                    <div class="app-detail-container">
                        <div class="app-info-card" style="display:block"> <!-- Reuse class but block layout -->
                            <h2 style="margin-top:0; color:var(--primary)">${cwe.cweId}: ${cwe.cweName}</h2>
                            
                            <div class="detail-group">
                                <label>Short Description</label>
                                <p>${cwe.shortDescription || 'N/A'}</p>
                            </div>

                            ${cwe.extendedDescription ? `
                            <div class="detail-group">
                                <label>Extended Description</label>
                                <p>${cwe.extendedDescription}</p>
                            </div>` : ''}

                            <div class="detail-group">
                                <label>Likelihood</label>
                                <span class="badge">${cwe.likelihood || 'Unknown'}</span>
                            </div>

                            ${cwe.related ? `
                            <div class="detail-group">
                                <label>Related Weaknesses</label>
                                <p>${cwe.related}</p>
                            </div>` : ''}

                            ${cwe.example ? `
                            <div class="detail-group">
                                <label>Example Code / Scenario</label>
                                <pre>${cwe.example}</pre>
                            </div>` : ''}

                            ${cwe.notes ? `
                            <div class="detail-group">
                                <label>Notes</label>
                                <p style="font-style:italic">${cwe.notes}</p>
                            </div>` : ''}

                            <div style="margin-top:20px; font-size:12px; color:#999">
                                Source: ${cwe.sourceFile} | Updated: ${Utils.formatDate(cwe.updatedAt)}
                            </div>
                        </div>
                    </div>
                `;
            } catch (e) {
                container.innerHTML = `<div class="empty-state">Error loading detail: ${e.message}</div>`;
            }
        },

        async handleFileUpload(inputElement) {
            const file = inputElement.files[0];
            if (!file) return;

            if (!confirm(`Import file "${file.name}" to database? This might take a while.`)) {
                inputElement.value = '';
                return;
            }

            const formData = new FormData();
            formData.append("file", file);

            alert("Uploading... Please wait.");

            try {
                const response = await fetch(CONFIG.API_HOST + CONFIG.ENDPOINTS.IMPORT_CWE, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem("token")}`
                    },
                    body: formData
                });

                if (response.ok) {
                    alert("Import CWE successful!");
                    if (document.querySelector('[data-section="CWE"]').classList.contains("active")) {
                        this.loadList(0);
                    }
                } else {
                    const txt = await response.text();
                    alert("Import failed: " + txt);
                }
            } catch (e) {
                console.error(e);
                alert("Error uploading file.");
            } finally {
                inputElement.value = '';
            }
        }
    };


    // Placeholder for Settings
    const SettingsController = {
        load(section) {
            if (section === "CWE") {
                CweController.loadList(0);
            } else if (section === "about") {
                this.renderAbout();
            } else if (section === "profile") {
                this.renderProfile();
            }
        },
        renderAbout() {
            document.getElementById("settingSectionTitle").textContent = "About";
            document.getElementById("settingContentBody").innerHTML = `<p>VulnTracer v1.0. Security Scanner Project.</p>`;
            document.getElementById("settingActions").innerHTML = "";
        },
        renderProfile() {
            document.getElementById("settingSectionTitle").textContent = "My Profile";
            document.getElementById("settingContentBody").innerHTML = `<p>Username: <strong>${userInfo?.sub}</strong></p><p>Role: ${userInfo?.role || 'User'}</p>`;
            document.getElementById("settingActions").innerHTML = "";
        }
    };

    // Expose needed functions to Window for HTML onClick events (nếu cần)
    window.ScanController = ScanController;
    window.ModalController = ModalController;
    window.NavController = NavController;
    window.CweController = CweController;

    // === RUN ===
    NavController.init();
    ModalController.init();
});
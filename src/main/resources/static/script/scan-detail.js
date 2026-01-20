const API_HOST = "http://localhost:8080";

// --- Utils ---
function getAuthHeaders() {
    const token = localStorage.getItem("token");
    if (!token) window.location.href = "/auth/login";
    return { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' };
}

function formatDate(dateStr) {
    if (!dateStr) return "N/A";
    return new Date(dateStr).toLocaleString("vi-VN");
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
        );
        return JSON.parse(jsonPayload);
    } catch (e) { return null; }
}


document.addEventListener("DOMContentLoaded", async () => {
    // 1. Lấy ID từ URL
    const pathSegments = window.location.pathname.split('/');
    const scanId = pathSegments[pathSegments.length - 1];

    if (!scanId || isNaN(scanId)) {
        alert("Invalid Scan ID!");
        window.location.href = "/home";
        return;
    }

    // 2. Setup User UI
    const token = localStorage.getItem("token");
    if (!token) window.location.href = "/auth/login";

    const userInfo = parseJwt(token);
    document.getElementById("username").textContent = userInfo?.sub || "User";
    // Có thể parse JWT lấy username ở đây nếu cần

    // 3. Setup Logout
    const logoutBtn = document.getElementById("logoutBtn");
    if(logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            localStorage.removeItem("token");
            window.location.href = "/auth/login";
        });
    }

    // 4. Fetch Scan Data
    await loadScanDetails(scanId);

    // 5. Setup Drawer Close Events
    setupDrawer();

    setupCweModal();
});

async function loadScanDetails(scanId) {
    document.getElementById("pageTitle").textContent = `Scan Report #${scanId}`;

    try {
        const response = await fetch(`${API_HOST}/scan/${scanId}`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error("Failed to load scan");
        const data = await response.json();

        renderInfo(data.scan);
        renderChart(data.summary);
        renderVulnerabilities(data.vulnerabilities);

    } catch (e) {
        console.error(e);
        alert("Error loading data: " + e.message);
    }
}

// --- RENDER INFO ---
function renderInfo(scan) {
    document.getElementById("appName").textContent = scan.applicationName;
    const statusClass = scan.status ? `status-${scan.status.toLowerCase()}` : '';
    document.getElementById("scanStatus").innerHTML = `<span class="${statusClass}">${scan.status}</span>`;
    document.getElementById("startTime").textContent = formatDate(scan.startTime);
    document.getElementById("completedTime").textContent = formatDate(scan.completedAt);

    if (scan.startTime && scan.completedAt) {
        const start = new Date(scan.startTime);
        const end = new Date(scan.completedAt);
        const diffMs = end - start;
        const diffMins = Math.floor(diffMs / 60000);
        document.getElementById("duration").textContent = diffMins > 0 ? `${diffMins} mins` : "< 1 min";
    } else {
        document.getElementById("duration").textContent = "...";
    }
}

// --- RENDER CHART ---
function renderChart(summary) {
    const ctx = document.getElementById('summaryChart').getContext('2d');
    const colors = { critical: '#721c24', high: '#d93025', medium: '#b05c06', low: '#1e40af', info: '#4b5563' };
    const dataValues = [summary.critical, summary.high, summary.medium, summary.low, summary.info];

    if (summary.total === 0) {
        document.getElementById('chartLegend').innerHTML = "No vulnerabilities found.";
        return;
    }

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Critical', 'High', 'Medium', 'Low', 'Info'],
            datasets: [{
                data: dataValues,
                backgroundColor: [colors.critical, colors.high, colors.medium, colors.low, colors.info],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } }
        }
    });

    const legendContainer = document.getElementById('chartLegend');
    const labels = ['Critical', 'High', 'Medium', 'Low', 'Info'];
    const bg = [colors.critical, colors.high, colors.medium, colors.low, colors.info];

    labels.forEach((label, index) => {
        if (dataValues[index] > 0) {
            legendContainer.innerHTML += `
                <div class="legend-item">
                    <div class="legend-color" style="background:${bg[index]}"></div>
                    <span>${label}: <strong>${dataValues[index]}</strong></span>
                </div>
            `;
        }
    });
}

// --- RENDER TABLE ---
let allVulns = [];

function renderVulnerabilities(vulns) {
    allVulns = vulns;
    const tbody = document.getElementById("vulnTableBody");
    tbody.innerHTML = "";

    if (!vulns || vulns.length === 0) {
        tbody.innerHTML = "<tr><td colspan='4' style='text-align:center'>No findings.</td></tr>";
        return;
    }

    vulns.forEach(v => {
        const tr = document.createElement("tr");
        const severityClass = `sev-${v.severity.toLowerCase()}`;

        const isZap = v.hasOwnProperty('zapFindingId');

        const displayId = isZap ? v.pluginId : v.templateId;
        const displayMatchedAt = isZap ? "-" : (v.matchedAt || "-");

        tr.innerHTML = `
            <td><span class="${severityClass}">${v.severity}</span></td>
            <td><strong>${v.name}</strong></td>
            <td class="text-truncate" style="max-width: 250px;" title="${displayMatchedAt}">
                ${displayMatchedAt}
            </td>
            <td style="color:#666">${displayId}</td>
        `;

        // Pass toàn bộ object v vào hàm detail
        tr.addEventListener("click", () => openVulnDetail(v));
        tbody.appendChild(tr);
    });

    // Search Logic (Updated)
    const searchInput = document.getElementById("vulnSearch");
    if(searchInput){
        searchInput.addEventListener("input", (e) => {
            const term = e.target.value.toLowerCase();
            const rows = tbody.querySelectorAll("tr");
            rows.forEach((row, index) => {
                const v = allVulns[index];
                if (!v) return;

                const isZap = v.hasOwnProperty('zapFindingId');
                const idToCheck = isZap ? v.pluginId : v.templateId;

                if (v.name.toLowerCase().includes(term) || idToCheck.toLowerCase().includes(term)) {
                    row.style.display = "";
                } else {
                    row.style.display = "none";
                }
            });
        });
    }
}

// --- DRAWER DETAILS LOGIC (FETCH API THẬT) ---
async function openVulnDetail(basicInfo) {
    const drawer = document.getElementById("vulnDrawer");
    const isZap = basicInfo.hasOwnProperty('zapFindingId');

    // --- 1. Hiển thị thông tin Header (Chung) ---
    document.getElementById("drawerTitle").textContent = basicInfo.name;
    const sevEl = document.getElementById("drawerSeverity");
    sevEl.textContent = basicInfo.severity;
    sevEl.className = `badge-large sev-${basicInfo.severity.toLowerCase()}`;

    // ID hiển thị (PluginID hoặc TemplateID)
    document.getElementById("drawerTemplateId").textContent = isZap ? basicInfo.pluginId : basicInfo.templateId;

    // URL Header (Nuclei có ngay, ZAP thì chưa chắc có ở list, tạm để trống hoặc update sau khi fetch detail)
    const urlEl = document.getElementById("drawerUrl");
    if (!isZap) {
        urlEl.textContent = basicInfo.matchedAt;
        urlEl.href = basicInfo.matchedAt.startsWith('http') ? basicInfo.matchedAt : '#';
        urlEl.parentElement.style.display = "block"; // Show dòng URL
    } else {
        urlEl.parentElement.style.display = "none"; // Hide dòng URL tạm thời vì ZAP list ko có
    }

    // --- 2. Reset UI state ---
    const descEl = document.getElementById("drawerDesc");
    const solutionContainer = document.getElementById("drawerSolutionContainer"); // Element mới
    const solutionEl = document.getElementById("drawerSolution"); // Element mới
    const evidenceEl = document.getElementById("drawerEvidence");
    const cweListEl = document.getElementById("drawerCweList");

    descEl.innerHTML = "Loading description..."; // Dùng innerHTML để support ZAP
    evidenceEl.textContent = "Loading evidence...";
    cweListEl.innerHTML = '<span style="color:#999; font-style:italic">Loading details...</span>';

    // Reset Solution
    if(solutionContainer) solutionContainer.style.display = "none";
    if(solutionEl) solutionEl.innerHTML = "";

    // Mở Drawer
    drawer.classList.remove("hidden");
    setTimeout(() => drawer.classList.add("open"), 10);

    // --- 3. Fetch Detail theo từng Tool ---
    try {
        let apiUrl = isZap
            ? `${API_HOST}/zap_finding/${basicInfo.zapFindingId}`
            : `${API_HOST}/vulnerability/${basicInfo.id}`;

        const response = await fetch(apiUrl, { headers: getAuthHeaders() });
        if (!response.ok) throw new Error("Failed to fetch details");
        const detail = await response.json();

        if (isZap) {
            descEl.innerHTML = detail.description || "No description.";

            if (detail.solution && solutionContainer && solutionEl) {
                solutionContainer.style.display = "block";
                solutionEl.innerHTML = detail.solution;
            }

            if (detail.evidences && detail.evidences.length > 0) {
                const evidenceHtml = detail.evidences.map(e => {
                    return `
<div style="background: #050a0e; padding: 10px; border-radius: 4px; margin-bottom: 10px; border-left: 3px solid #03090e;">
    <div><strong>Method:</strong> ${e.method}</div>
    <div><strong>URI:</strong> <a style="color: #cbd5e1" href="${e.uri}" target="_blank">${e.uri}</a></div>
    ${e.param ? `<div><strong>Param:</strong> ${e.param}</div>` : ''}
    ${e.evidence ? `<div style="margin-top:5px; color:#d63333; font-family:monospace; word-break:break-all;">Match: ${e.evidence}</div>` : ''}
</div>`;
                }).join('');
                evidenceEl.innerHTML = evidenceHtml;
            } else {
                evidenceEl.textContent = "No evidence provided.";
            }

            // CWE (ZAP trả về "cweId": "942" - Single value hoặc null)
            cweListEl.innerHTML = "";
            if (detail.cweId) {
                const cweIdNum = detail.cweId;
                const cweLabel = `CWE-${cweIdNum}`;
                createCweTag(cweListEl, cweLabel, cweIdNum);
            } else {
                cweListEl.textContent = "None";
            }
        }

        // === XỬ LÝ NUCLEI ===
        else {
            descEl.textContent = detail.description || "No description.";

            if (detail.evidences && detail.evidences.length > 0) {
                const evidenceText = detail.evidences.map(e => {
                    if (e.command) return `Command:\n${e.command}`;
                    return JSON.stringify(e, null, 2);
                }).join('\n\n----------------\n\n');
                evidenceEl.textContent = evidenceText; // Nuclei evidence thường là raw text/code
            } else {
                evidenceEl.textContent = "No evidence available.";
            }

            cweListEl.innerHTML = "";
            if (detail.cwe && detail.cwe.length > 0) {
                detail.cwe.forEach(cweString => {
                    // Extract ID: "CWE-200" từ "CWE-200: Info"
                    const cweIdFull = cweString.split(':')[0].trim(); // CWE-200
                    const cweIdNum = cweIdFull.replace('CWE-', '');   // 200

                    createCweTag(cweListEl, cweString, cweIdNum);
                });
            } else {
                cweListEl.textContent = "None";
            }
        }

    } catch (e) {
        console.error(e);
        descEl.textContent = "Error loading details.";
        evidenceEl.textContent = "";
        cweListEl.textContent = "Error.";
    }
}

// Helper tạo thẻ CWE để tái sử dụng logic
function createCweTag(container, label, idNum) {
    const tag = document.createElement("span");
    tag.className = "cwe-tag";
    tag.textContent = label;
    tag.style.cursor = "pointer";
    tag.style.marginRight = "5px";

    tag.addEventListener("click", (e) => {
        e.stopPropagation();
        openCweDetailsModal(idNum);
    });

    container.appendChild(tag);

    container.appendChild(tag);
}

function setupDrawer() {
    const drawer = document.getElementById("vulnDrawer");
    const overlay = document.getElementById("drawerOverlay");
    const closeBtn = document.getElementById("closeDrawer");

    const close = () => {
        drawer.classList.remove("open");
        setTimeout(() => drawer.classList.add("hidden"), 300);
    };

    if(closeBtn) closeBtn.addEventListener("click", close);
    if(overlay) overlay.addEventListener("click", close);
}

async function openCweDetailsModal(cweNum) {
    const overlay = document.getElementById("cweModalOverlay");

    const idEl = document.getElementById("modalCweId");
    const nameEl = document.getElementById("modalCweName");
    const descEl = document.getElementById("modalCweDesc");
    const likelihoodEl = document.getElementById("modalCweLikelihood");
    const relatedContainer = document.getElementById("modalRelatedContainer");
    const relatedEl = document.getElementById("modalCweRelated");

    idEl.textContent = `CWE-${cweNum}`;
    nameEl.textContent = "Loading...";
    descEl.textContent = "Fetching CWE definition from database...";
    likelihoodEl.textContent = "...";
    relatedContainer.classList.add("hidden");

    overlay.classList.remove("hidden");
    setTimeout(() => overlay.classList.add("open"), 10);

    try {
        const response = await fetch(`${API_HOST}/cwe/num/${cweNum}`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error("CWE not found");

        const data = await response.json();

        idEl.textContent = data.cweId;
        nameEl.textContent = data.cweName;

        descEl.textContent = data.shortDescription || "No description available.";

        likelihoodEl.textContent = data.likelihood || "Unknown";

        if (data.related) {
            try {
                const relatedJson = JSON.parse(data.related);
                relatedEl.textContent = JSON.stringify(relatedJson, null, 2);
                relatedContainer.classList.remove("hidden");
            } catch (e) {
                relatedEl.textContent = data.related;
                relatedContainer.classList.remove("hidden");
            }
        }

    } catch (e) {
        console.error(e);
        nameEl.textContent = "Error Loading CWE";
        descEl.textContent = "Could not fetch details. Please try again later.";
    }
}

function setupCweModal() {
    const overlay = document.getElementById("cweModalOverlay");
    const closeBtn = document.getElementById("closeCweModal");
    const container = overlay.querySelector('.modal-container');

    const closeModal = () => {
        overlay.classList.remove("open");
        setTimeout(() => overlay.classList.add("hidden"), 300);
    };

    if (closeBtn) closeBtn.addEventListener("click", closeModal);

    if (overlay) {
        overlay.addEventListener("click", (e) => {
            if (e.target === overlay) {
                closeModal();
            }
        });
    }

    if (container) {
        container.addEventListener("click", (e) => e.stopPropagation());
    }
}
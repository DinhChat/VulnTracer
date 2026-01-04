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
        tr.innerHTML = `
            <td><span class="${severityClass}">${v.severity}</span></td>
            <td><strong>${v.name}</strong></td>
            <td>${v.matchedAt}</td>
            <td style="color:#666">${v.templateId}</td>
        `;
        // Pass ID vào hàm openVulnDetail
        tr.addEventListener("click", () => openVulnDetail(v));
        tbody.appendChild(tr);
    });

    // Search Logic
    const searchInput = document.getElementById("vulnSearch");
    if(searchInput){
        searchInput.addEventListener("input", (e) => {
            const term = e.target.value.toLowerCase();
            const rows = tbody.querySelectorAll("tr");
            rows.forEach((row, index) => {
                const v = allVulns[index];
                if (v && (v.name.toLowerCase().includes(term) || v.templateId.toLowerCase().includes(term))) {
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

    // 1. Hiển thị thông tin cơ bản
    document.getElementById("drawerTitle").textContent = basicInfo.name;
    const sevEl = document.getElementById("drawerSeverity");
    sevEl.textContent = basicInfo.severity;
    sevEl.className = `badge-large sev-${basicInfo.severity.toLowerCase()}`;

    document.getElementById("drawerTemplateId").textContent = basicInfo.templateId;
    const urlEl = document.getElementById("drawerUrl");
    urlEl.textContent = basicInfo.matchedAt;
    urlEl.href = basicInfo.matchedAt.startsWith('http') ? basicInfo.matchedAt : '#';

    // 2. Reset các trường chi tiết về trạng thái Loading
    const descEl = document.getElementById("drawerDesc");
    const evidenceEl = document.getElementById("drawerEvidence");
    const cweListEl = document.getElementById("drawerCweList");

    descEl.textContent = "Loading description...";
    evidenceEl.textContent = "Loading evidence...";
    cweListEl.innerHTML = '<span style="color:#999; font-style:italic">Loading details...</span>';

    // 3. Mở Drawer
    drawer.classList.remove("hidden");
    setTimeout(() => drawer.classList.add("open"), 10);

    // 4. Fetch chi tiết từ API /vulnerability/{id}
    try {
        const response = await fetch(`${API_HOST}/vulnerability/${basicInfo.id}`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error("Failed to fetch details");

        // Data trả về theo JSON bạn cung cấp: { id, templateId, name, severity, description, evidences[], cwe[] }
        const detail = await response.json();

        // --- Bind Description ---
        descEl.textContent = detail.description || "No description provided.";

        // --- Bind Evidences ---
        if (detail.evidences && detail.evidences.length > 0) {
            // Lấy ra command curl hoặc hiển thị JSON nếu không có command
            const evidenceText = detail.evidences.map(e => {
                if (e.command) return `Command:\n${e.command}`;
                return JSON.stringify(e, null, 2);
            }).join('\n\n----------------\n\n');
            evidenceEl.textContent = evidenceText;
        } else {
            evidenceEl.textContent = "No evidence available.";
        }

        // --- Bind CWEs ---
        cweListEl.innerHTML = "";
        if (detail.cwe && detail.cwe.length > 0) {
            detail.cwe.forEach(cweString => {

                const tag = document.createElement("span");
                tag.className = "cwe-tag";
                tag.textContent = cweString;
                const cweId = cweString.split(':')[0].trim();

                // Sự kiện click chuyển trang
                tag.addEventListener("click", () => {
                    window.location.href = `/cwe-detail/${cweId}`;
                });

                cweListEl.appendChild(tag);
            });
        } else {
            cweListEl.textContent = "None";
        }

    } catch (e) {
        console.error(e);
        descEl.textContent = "Error loading details.";
        evidenceEl.textContent = "Error loading details.";
        cweListEl.textContent = "Error.";
    }
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
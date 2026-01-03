const API_HOST = "http://localhost:8080";

// --- Utils (Giống dashboard) ---
function getAuthHeaders() {
    const token = localStorage.getItem("token");
    if (!token) window.location.href = "/auth/login";
    return { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' };
}
function formatDate(dateStr) {
    if (!dateStr) return "N/A";
    return new Date(dateStr).toLocaleString("vi-VN");
}

document.addEventListener("DOMContentLoaded", async () => {
    // 1. Lấy ID từ URL
    const pathSegments = window.location.pathname.split('/');
    const scanId = pathSegments[pathSegments.length - 1];

    if (!scanId || isNaN(scanId)) {
        alert("Invalid Scan ID!");
        window.location.href = "/";
        return;
    }

    // 2. Setup User UI (Minimal)
    const token = localStorage.getItem("token");
    if(!token) window.location.href = "/auth/login";
    document.getElementById("username").textContent = "User";

    // 3. Fetch Scan Data
    await loadScanDetails(scanId);

    // 4. Setup Drawer Close Events
    setupDrawer();
});

async function loadScanDetails(scanId) {
    document.getElementById("pageTitle").textContent = `Scan Report #${scanId}`;

    try {
        const response = await fetch(`${API_HOST}/scan/${scanId}`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error("Failed to load scan");
        const data = await response.json(); // Data format như bạn cung cấp

        // Render từng phần
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
    document.getElementById("scanStatus").innerHTML = `<span class="status-${scan.status.toLowerCase()}">${scan.status}</span>`; // Tái sử dụng class css dashboard
    document.getElementById("startTime").textContent = formatDate(scan.startTime);
    document.getElementById("completedTime").textContent = formatDate(scan.completedAt);

    // Tính duration đơn giản
    if (scan.startTime && scan.completedAt) {
        const start = new Date(scan.startTime);
        const end = new Date(scan.completedAt);
        const diffMs = end - start;
        const diffMins = Math.floor(diffMs / 60000);
        document.getElementById("duration").textContent = `${diffMins} mins`;
    }
}

// --- RENDER CHART (Chart.js) ---
function renderChart(summary) {
    const ctx = document.getElementById('summaryChart').getContext('2d');

    // Màu sắc theo Severity
    const colors = {
        critical: '#721c24',
        high: '#d93025',
        medium: '#b05c06',
        low: '#1e40af',
        info: '#4b5563'
    };

    const dataValues = [summary.critical, summary.high, summary.medium, summary.low, summary.info];

    // Nếu tổng = 0 -> Vẽ màu xám
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
            plugins: {
                legend: { display: false } // Tắt legend mặc định để tự custom
            }
        }
    });

    // Custom Legend
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
let allVulns = []; // Store global để search

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

        // Click row -> Open Details
        tr.addEventListener("click", () => openVulnDetail(v));
        tbody.appendChild(tr);
    });

    // Setup Search Logic
    document.getElementById("vulnSearch").addEventListener("input", (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allVulns.filter(v => v.name.toLowerCase().includes(term) || v.templateId.toLowerCase().includes(term));

        // Re-render (tốt nhất là tách hàm tạo tr ra, nhưng viết nhanh ở đây)
        tbody.innerHTML = "";
        filtered.forEach(v => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><span class="sev-${v.severity.toLowerCase()}">${v.severity}</span></td>
                <td><strong>${v.name}</strong></td>
                <td>${v.matchedAt}</td>
                <td style="color:#666">${v.templateId}</td>
            `;
            tr.addEventListener("click", () => openVulnDetail(v));
            tbody.appendChild(tr);
        });
    });
}

// --- DRAWER DETAILS LOGIC ---
async function openVulnDetail(vuln) {
    const drawer = document.getElementById("vulnDrawer");
    const overlay = document.getElementById("drawerOverlay");

    // Fill basic info from List Item
    document.getElementById("drawerTitle").textContent = vuln.name;
    document.getElementById("drawerSeverity").textContent = vuln.severity;
    document.getElementById("drawerSeverity").className = `badge-large sev-${vuln.severity.toLowerCase()}`;
    document.getElementById("drawerTemplateId").textContent = vuln.templateId;
    document.getElementById("drawerUrl").textContent = vuln.matchedAt;
    document.getElementById("drawerUrl").href = vuln.matchedAt.startsWith('http') ? vuln.matchedAt : '#';

    // Show drawer first with loading state for details
    drawer.classList.remove("hidden");
    // Trick to trigger animation
    setTimeout(() => drawer.classList.add("open"), 10);

    // FETCH MORE DETAILS (CWE, Description, Evidence)
    // Giả sử có API: /scan/result/{id} hoặc trả về detail trong API cũ nhưng ẩn đi
    // Ở đây tôi giả lập fetch hoặc hiển thị placeholder

    const descEl = document.getElementById("drawerDesc");
    const evidenceEl = document.getElementById("drawerEvidence");
    const cweListEl = document.getElementById("drawerCweList");

    descEl.textContent = "Loading description...";
    evidenceEl.textContent = "Loading evidence...";

    try {
        // Thực tế: const detail = await fetchAPI(`/scan/vulnerability/${vuln.id}`);
        // Giả lập data detail vì API trên chưa có field này

        // --- MOCK DATA START ---
        const detail = {
            description: "Detects usage of cookies without the Secure flag. This means cookies can be transmitted over unencrypted connections.",
            evidence: "Set-Cookie: PHPSESSID=12345; path=/",
            cwes: ["CWE-614", "CWE-1004"]
        };
        // --- MOCK DATA END ---

        descEl.textContent = detail.description;
        evidenceEl.textContent = detail.evidence;

        // Render CWEs
        cweListEl.innerHTML = "";
        if (detail.cwes && detail.cwes.length > 0) {
            detail.cwes.forEach(cwe => {
                const tag = document.createElement("span");
                tag.className = "cwe-tag";
                tag.textContent = cwe;
                tag.onclick = () => window.open(`https://cwe.mitre.org/data/definitions/${cwe.split('-')[1]}.html`, '_blank');
                cweListEl.appendChild(tag);
            });
        } else {
            cweListEl.textContent = "None";
        }

    } catch (e) {
        descEl.textContent = "Could not load details.";
    }
}

function setupDrawer() {
    const drawer = document.getElementById("vulnDrawer");
    const overlay = document.getElementById("drawerOverlay");
    const closeBtn = document.getElementById("closeDrawer");

    const close = () => {
        drawer.classList.remove("open");
        setTimeout(() => drawer.classList.add("hidden"), 300); // Wait for animation
    };

    closeBtn.addEventListener("click", close);
    overlay.addEventListener("click", close);
}
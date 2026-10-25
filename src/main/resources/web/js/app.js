// Campus Placement Portal - Frontend Logic
// Written in standard, clean student style

let studentsList = [];
let drivesList = [];
let applicationsList = [];

document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    loadDashboard();
    loadDrives();
    loadStudents();
    loadApplications();
});

// ================= TABS =================
function initTabs() {
    const tabs = document.querySelectorAll('.nav-link');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            const targetId = tab.getAttribute('data-tab');
            document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
            document.getElementById(targetId).classList.add('active');

            if (targetId === 'tab-dashboard') loadDashboard();
            if (targetId === 'tab-drives') loadDrives();
            if (targetId === 'tab-students') loadStudents();
            if (targetId === 'tab-eligibility') populateEligibilityDropdowns();
            if (targetId === 'tab-pipeline') loadApplications();
            if (targetId === 'tab-reports') generateReportsText();
        });
    });
}

function showToast(message) {
    const box = document.getElementById('toast');
    box.textContent = message;
    box.classList.add('show');
    setTimeout(() => box.classList.remove('show'), 3000);
}

// ================= DASHBOARD =================
async function loadDashboard() {
    try {
        const res = await fetch('/api/stats');
        const data = await res.json();

        document.getElementById('kpi-total').textContent = data.totalStudents;
        document.getElementById('kpi-placed').textContent = data.placedStudents;
        document.getElementById('kpi-rate').textContent = data.placementPercentage.toFixed(1) + '%';
        document.getElementById('kpi-highest').textContent = data.highestPackageLpa.toFixed(1) + ' LPA';
        document.getElementById('kpi-average').textContent = data.averagePackageLpa.toFixed(1) + ' LPA';

        const tbody = document.getElementById('branch-table-body');
        tbody.innerHTML = '';
        if (data.branchStats) {
            data.branchStats.forEach(b => {
                const tr = document.createElement('tr');
                const unplaced = b.total - b.placed;
                tr.innerHTML = `
                    <td><strong>${b.branch}</strong></td>
                    <td>${b.total}</td>
                    <td><span class="badge badge-success">${b.placed}</span></td>
                    <td>${unplaced}</td>
                    <td><strong>${b.percent.toFixed(1)}%</strong></td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        console.error('Failed to load stats:', e);
    }
}

// ================= DRIVES =================
async function loadDrives() {
    try {
        const res = await fetch('/api/drives');
        drivesList = await res.json();
        const tbody = document.getElementById('drives-table-body');
        tbody.innerHTML = '';

        drivesList.forEach(d => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><code>${d.driveId}</code></td>
                <td><strong>${escapeHtml(d.companyName)}</strong></td>
                <td>${escapeHtml(d.jobRole)}</td>
                <td><strong class="text-green">${d.ctcLpa.toFixed(2)} LPA</strong></td>
                <td>${d.minCgpa.toFixed(2)}</td>
                <td>${d.maxBacklogs}</td>
                <td><span class="badge badge-info">${escapeHtml(d.eligibleBranches)}</span></td>
                <td>${d.driveDate}</td>
                <td><span class="badge badge-success">${d.status}</span></td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="deleteDrive('${d.driveId}')">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('Failed to load drives:', e);
    }
}

function openDriveModal() { document.getElementById('modal-drive').style.display = 'flex'; }
function closeDriveModal() { document.getElementById('modal-drive').style.display = 'none'; }

async function submitDriveForm() {
    const comp = document.getElementById('new-drv-comp').value.trim();
    const role = document.getElementById('new-drv-role').value.trim();
    const ctc = document.getElementById('new-drv-ctc').value.trim();
    const cgpa = document.getElementById('new-drv-cgpa').value.trim();
    const backlogs = document.getElementById('new-drv-backlogs').value.trim();
    const date = document.getElementById('new-drv-date').value.trim();
    const branches = document.getElementById('new-drv-branches').value.trim();

    if (!comp || !role || !ctc) {
        alert('Please fill company name, role, and package.');
        return;
    }

    try {
        await fetch('/api/drives', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                companyName: comp,
                jobRole: role,
                ctcLpa: ctc,
                minCgpa: cgpa || 7.5,
                maxBacklogs: backlogs || 0,
                driveDate: date || '2026-12-01',
                eligibleBranches: branches || 'CSE,ISE,ECE'
            })
        });
        closeDriveModal();
        showToast('Company drive published successfully!');
        loadDrives();
        loadDashboard();
    } catch (e) {
        alert('Error publishing drive: ' + e.message);
    }
}

async function deleteDrive(id) {
    if (!confirm('Delete placement drive ' + id + '?')) return;
    try {
        await fetch('/api/drives/' + id, { method: 'DELETE' });
        showToast('Drive deleted.');
        loadDrives();
        loadDashboard();
    } catch (e) {
        alert('Failed to delete drive');
    }
}

// ================= STUDENTS =================
async function loadStudents() {
    try {
        const res = await fetch('/api/students');
        studentsList = await res.json();
        filterStudentsTable();
    } catch (e) {
        console.error('Failed to load students:', e);
    }
}

function filterStudentsTable() {
    const query = document.getElementById('stu-search').value.trim().toLowerCase();
    const branch = document.getElementById('stu-branch-filter').value;
    const tbody = document.getElementById('students-table-body');
    tbody.innerHTML = '';

    studentsList.forEach(s => {
        const matchQuery = !query || s.usn.toLowerCase().includes(query) || s.name.toLowerCase().includes(query);
        const matchBranch = branch === 'ALL' || s.branch.toUpperCase() === branch.toUpperCase();

        if (matchQuery && matchBranch) {
            const tr = document.createElement('tr');
            const isPlaced = s.placementStatus === 'PLACED';
            const statusBadge = isPlaced
                ? '<span class="badge badge-success">PLACED</span>'
                : '<span class="badge badge-info">NOT PLACED</span>';

            tr.innerHTML = `
                <td><code>${s.usn}</code></td>
                <td><strong>${escapeHtml(s.name)}</strong></td>
                <td>${escapeHtml(s.email)}</td>
                <td><span class="badge badge-info">${s.branch}</span></td>
                <td><strong>${s.cgpa.toFixed(2)}</strong></td>
                <td>${s.activeBacklogs > 0 ? `<span class="badge badge-danger">${s.activeBacklogs}</span>` : '0'}</td>
                <td><span class="text-muted">${escapeHtml(s.skills)}</span></td>
                <td>${statusBadge}</td>
                <td>${isPlaced ? escapeHtml(s.placedCompany) : '-'}</td>
                <td>${isPlaced ? s.placedPackageLpa.toFixed(2) + ' LPA' : '-'}</td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="deleteStudent('${s.usn}')">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        }
    });
}

function openStudentModal() { document.getElementById('modal-student').style.display = 'flex'; }
function closeStudentModal() { document.getElementById('modal-student').style.display = 'none'; }

async function submitStudentForm() {
    const usn = document.getElementById('new-stu-usn').value.trim();
    const name = document.getElementById('new-stu-name').value.trim();
    const email = document.getElementById('new-stu-email').value.trim();
    const branch = document.getElementById('new-stu-branch').value;
    const cgpa = document.getElementById('new-stu-cgpa').value.trim();
    const backlogs = document.getElementById('new-stu-backlogs').value.trim();
    const skills = document.getElementById('new-stu-skills').value.trim();

    if (!usn || !name || !cgpa) {
        alert('Please fill USN, Name, and CGPA.');
        return;
    }

    try {
        await fetch('/api/students', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                usn: usn, name: name, email: email,
                branch: branch, cgpa: cgpa,
                activeBacklogs: backlogs || 0,
                skills: skills || 'Java'
            })
        });
        closeStudentModal();
        showToast('Student registered successfully!');
        loadStudents();
        loadDashboard();
    } catch (e) {
        alert('Error saving student: ' + e.message);
    }
}

async function deleteStudent(usn) {
    if (!confirm('Delete candidate ' + usn + '?')) return;
    try {
        await fetch('/api/students/' + usn, { method: 'DELETE' });
        showToast('Candidate deleted.');
        loadStudents();
        loadDashboard();
    } catch (e) {
        alert('Failed to delete student');
    }
}

// ================= ELIGIBILITY EVALUATOR =================
function populateEligibilityDropdowns() {
    const drvSelect = document.getElementById('eval-drive-select');
    drvSelect.innerHTML = '';
    drivesList.forEach(d => {
        const opt = document.createElement('option');
        opt.value = d.driveId;
        opt.textContent = `${d.companyName} - ${d.jobRole} (${d.ctcLpa.toFixed(1)} LPA | Cutoff: ${d.minCgpa.toFixed(1)})`;
        drvSelect.appendChild(opt);
    });

    const stuSelect = document.getElementById('eval-student-select');
    stuSelect.innerHTML = '';
    studentsList.forEach(s => {
        const opt = document.createElement('option');
        opt.value = s.usn;
        opt.textContent = `${s.name} (${s.usn} | ${s.branch} | CGPA: ${s.cgpa.toFixed(2)} | Backlogs: ${s.activeBacklogs} | ${s.placementStatus})`;
        stuSelect.appendChild(opt);
    });

    runEligibilityCheck();
}

async function runEligibilityCheck() {
    const usn = document.getElementById('eval-student-select').value;
    const driveId = document.getElementById('eval-drive-select').value;
    if (!usn || !driveId) return;

    try {
        const res = await fetch('/api/eligibility/check', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usn: usn, driveId: driveId })
        });
        const data = await res.json();

        const badge = document.getElementById('verdict-badge');
        const list = document.getElementById('verdict-checks-list');
        const btn = document.getElementById('btn-submit-app');

        list.innerHTML = '';

        if (data.isEligible) {
            badge.textContent = 'STATUS: VERIFIED ELIGIBLE - CANDIDATE CAN APPLY';
            badge.className = 'verdict-banner verdict-pass';
            btn.disabled = false;
        } else {
            badge.textContent = 'STATUS: INELIGIBLE FOR THIS DRIVE';
            badge.className = 'verdict-banner verdict-fail';
            btn.disabled = true;
        }

        data.checks.forEach(chk => {
            const div = document.createElement('div');
            div.className = 'check-item';
            const isPass = chk.startsWith('PASS');
            div.innerHTML = `<span style="color: ${isPass ? '#059669' : '#dc2626'}; font-weight: bold;">${isPass ? '✓' : '✗'}</span> ${escapeHtml(chk)}`;
            list.appendChild(div);
        });
    } catch (e) {
        console.error('Eligibility check error:', e);
    }
}

async function submitEligibilityApplication() {
    const usn = document.getElementById('eval-student-select').value;
    const driveId = document.getElementById('eval-drive-select').value;

    try {
        const res = await fetch('/api/applications/apply', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usn: usn, driveId: driveId })
        });
        const data = await res.json();
        if (data.error) {
            alert('Cannot apply: ' + data.error);
            return;
        }

        showToast('Application successfully submitted! Added to candidate pipeline.');
        loadApplications();
        runEligibilityCheck();
    } catch (e) {
        alert('Error: ' + e.message);
    }
}

// ================= PIPELINE =================
async function loadApplications() {
    try {
        const res = await fetch('/api/applications');
        applicationsList = await res.json();
        const tbody = document.getElementById('pipeline-table-body');
        tbody.innerHTML = '';

        const stages = ['APPLIED', 'SHORTLISTED', 'ONLINE_TEST', 'TECHNICAL_INTERVIEW', 'HR_INTERVIEW', 'OFFERED', 'REJECTED'];

        applicationsList.forEach(a => {
            const tr = document.createElement('tr');
            let stageBadge = '<span class="badge badge-info">APPLIED</span>';
            if (a.stage === 'OFFERED') stageBadge = '<span class="badge badge-success">OFFERED</span>';
            else if (a.stage === 'REJECTED') stageBadge = '<span class="badge badge-danger">REJECTED</span>';
            else if (a.stage.includes('INTERVIEW')) stageBadge = '<span class="badge badge-warning">' + a.stage.replace('_', ' ') + '</span>';

            let optionsHtml = '';
            stages.forEach(st => {
                optionsHtml += `<option value="${st}" ${st === a.stage ? 'selected' : ''}>${st.replace('_', ' ')}</option>`;
            });

            tr.innerHTML = `
                <td><code>${a.applicationId}</code></td>
                <td><code>${a.studentUsn}</code></td>
                <td><strong>${escapeHtml(a.studentName)}</strong></td>
                <td>${escapeHtml(a.companyName)}</td>
                <td>${escapeHtml(a.jobRole)}</td>
                <td>${a.appliedDate}</td>
                <td>${a.interviewDate}</td>
                <td>${stageBadge}</td>
                <td>
                    <select class="form-select" style="padding: 0.2rem; font-size: 0.75rem;" onchange="updateStage('${a.applicationId}', this.value)">
                        ${optionsHtml}
                    </select>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('Failed to load applications:', e);
    }
}

async function updateStage(appId, newStage) {
    let date = '';
    if (newStage.includes('INTERVIEW') || newStage === 'ONLINE_TEST') {
        date = prompt('Enter interview schedule date and time:', '2026-11-15 10:30 AM');
    }

    try {
        await fetch('/api/applications/update', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ applicationId: appId, stage: newStage, interviewDate: date })
        });

        showToast(`Candidate stage updated to ${newStage.replace('_', ' ')}!`);
        if (newStage === 'OFFERED') {
            showToast('Student automatically marked as PLACED!');
        }
        loadApplications();
        loadStudents();
        loadDashboard();
    } catch (e) {
        alert('Failed to update stage: ' + e.message);
    }
}

// ================= REPORTS & EXPORT =================
async function generateReportsText() {
    try {
        const [statsRes, stuRes] = await Promise.all([fetch('/api/stats'), fetch('/api/students')]);
        const stats = await statsRes.json();
        const students = await stuRes.json();

        let txt = "========================================================================================\n";
        txt += "                  DEPARTMENT OF TRAINING & PLACEMENT (T&P CELL)                         \n";
        txt += "                     ANNUAL CAMPUS PLACEMENT SUMMARY REPORT                             \n";
        txt += "                         Report Generated: " + new Date().toISOString().split('T')[0] + "\n";
        txt += "========================================================================================\n\n";

        txt += `Total Registered 4th Year Candidates: ${stats.totalStudents}\n`;
        txt += `Total Candidates Successfully Placed: ${stats.placedStudents}\n`;
        txt += `Overall Institutional Placement Rate: ${stats.placementPercentage.toFixed(1)}%\n`;
        txt += `Highest CTC Package Offered:         ${stats.highestPackageLpa.toFixed(2)} LPA\n`;
        txt += `Average CTC Package Offered:         ${stats.averagePackageLpa.toFixed(2)} LPA\n\n`;

        txt += "----------------------------------------------------------------------------------------\n";
        txt += " BRANCH-WISE PLACEMENT BREAKDOWN:\n";
        txt += "----------------------------------------------------------------------------------------\n";
        txt += "Branch       | Total Candidates | Placed Count   | Placement Rate (%)\n";
        txt += "----------------------------------------------------------------------------------------\n";

        if (stats.branchStats) {
            stats.branchStats.forEach(b => {
                txt += `${b.branch.padEnd(12)} | ${String(b.total).padEnd(16)} | ${String(b.placed).padEnd(14)} | ${b.percent.toFixed(1)}%\n`;
            });
        }
        txt += "----------------------------------------------------------------------------------------\n\n";

        txt += " LIST OF PLACED STUDENTS:\n";
        txt += "----------------------------------------------------------------------------------------\n";
        txt += "USN            | Candidate Name       | Branch   | Company Placed   | Package\n";
        txt += "----------------------------------------------------------------------------------------\n";

        students.forEach(s => {
            if (s.placementStatus === 'PLACED') {
                txt += `${s.usn.padEnd(14)} | ${s.name.padEnd(20)} | ${s.branch.padEnd(8)} | ${s.placedCompany.padEnd(16)} | ${s.placedPackageLpa.toFixed(2)} LPA\n`;
            }
        });
        txt += "========================================================================================\n";

        document.getElementById('reports-text-area').value = txt;
    } catch (e) {
        console.error('Failed to generate report text:', e);
    }
}

function downloadPlacedCsv() {
    let csv = "USN,Candidate_Name,Email,Branch,CGPA,Placed_Company,Package_LPA\n";
    studentsList.forEach(s => {
        if (s.placementStatus === 'PLACED') {
            csv += `"${s.usn}","${s.name}","${s.email}","${s.branch}",${s.cgpa.toFixed(2)},"${s.placedCompany}",${s.placedPackageLpa.toFixed(2)}\n`;
        }
    });

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "Placed_Students_List.csv";
    link.click();
}

async function resetData() {
    if (!confirm('Reset placement database to realistic sample data for examiner demonstration?')) return;
    try {
        await fetch('/api/reset', { method: 'POST' });
        showToast('Sample dataset reloaded!');
        loadDashboard();
        loadDrives();
        loadStudents();
        loadApplications();
    } catch (e) {
        alert('Reset failed');
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

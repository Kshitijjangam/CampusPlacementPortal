# Campus Placement Drive & Job Application Portal

> **Subject:** Mini Project (4th Year B.Tech CSE)  
> **Team Size:** 4 Students  
> **Technology Stack:** Java Swing (AWT / JFC), CSV Persistence, Object-Oriented Architecture  
> **Platform:** 100% Offline Desktop Application (Runs on Windows, macOS, and Linux)  

---

## 🌟 About the Project

**Campus Placement Portal** is a desktop software application designed for college Training & Placement (T&P) cells to automate campus recruitment drives, evaluate candidate eligibility, track interview selection rounds, and generate statistical placement reports.

It is built specifically as a classic, authentic university Java mini project:
- Uses standard **Java Swing (`JFrame`, `JTabbedPane`, `JTable`, `DefaultTableModel`, `JOptionPane`)** aligned with academic Java lab curricula.
- Uses **CSV-based file persistence** in the `data/` folder (easy to inspect and open directly in Microsoft Excel).
- **Zero database installation:** No MySQL or external server setup required.

---

## 🚀 How to Run

### Windows (1-Click):
Double-click **`run.bat`** (or double-click `CampusPlacementPortal.jar`).

### Terminal / Command Prompt:
```bash
java -jar CampusPlacementPortal.jar
```

---

## 👥 4-Student Module Breakdown

| Student | Assigned Role | Module Name | Core Java Classes Owned |
| :--- | :--- | :--- | :--- |
| **Student 1** | **Profile & Drive Management Lead** | Module 1: Student & Company Registry | `Student.java`, `CompanyDrive.java`, `DataStore.java`, `StudentPanel.java` |
| **Student 2** | **Eligibility & Filtering Engine Lead** | Module 2: Automated Eligibility Engine | `EligibilityService.java`, `EligibilityPanel.java` |
| **Student 3** | **Pipeline & Workflow Lead** | Module 3: Recruitment Pipeline Tracker | `Application.java`, `PipelinePanel.java` |
| **Student 4** | **GUI Integration & Analytics Lead** | Module 4: Statistics & Reporting System | `AnalyticsService.java`, `DashboardPanel.java`, `ReportPanel.java`, `MainFrame.java`, `Main.java` |

---

## 🎯 Viva Voce Demonstration Walkthrough

When presenting to the external examiners:

1. **Dashboard & Analytics Tab (Student 4):**
   - Show the summary cards: Total Registered Students (8), Placed Count (4), Placement Percentage (50.0%), Highest Package (34.0 LPA), Average Package (19.7 LPA).
   - Show the branch-wise placement breakdown table (CSE, ISE, ECE, MECH).

2. **Company Drives & Students Directory (Student 1):**
   - Click on the **Company Drives** tab to show upcoming drives (Google, Microsoft, Amazon, Infosys, TCS).
   - Click on **Students Directory** tab to show candidate details (USN, Name, Branch, CGPA, Backlogs, Status).
   - Use the **Search Bar** to filter students by name, USN, or branch.
   - Click **"+ Add New Student"** to register a new candidate live.

3. **Eligibility Verification Engine (Student 2):**
   - Switch to **Apply & Eligibility Checker** tab.
   - Select **Google (28.5 LPA, Min CGPA 8.5)**.
   - Select candidate **Aarav Sharma (CGPA 8.85, 0 backlogs)** -> The panel shows green checkmarks: `PASS: Meets CGPA`, `PASS: Branch eligible`. The "Submit Job Application" button turns active.
   - Select candidate **Karan Malhotra (CGPA 6.80, 1 backlog)** -> The panel displays red rejection reasons: `FAIL: CGPA below cutoff`, `FAIL: Active backlogs exceed limit`. The button disables automatically.
   - Explain the **Dream Company Rule**: Placed candidates can only apply if the new drive offers at least 1.5x their existing package.

4. **Recruitment Pipeline & Auto-Offer Sync (Student 3):**
   - Switch to **Application Pipeline** tab.
   - Select an application currently in `TECHNICAL_INTERVIEW`.
   - In the dropdown, change the stage to **`OFFERED`** and click "Update Candidate Stage".
   - **Show the automated update:** Notice the pop-up notification confirming the candidate is placed. Switch to the **Students Directory** and **Dashboard** tabs: the candidate is now automatically updated as **`PLACED`** with their new company and package, and institutional stats increment in real time!

5. **Placement Reports & CSV Export (Student 4):**
   - Switch to **Placement Reports** tab.
   - Click **"Export Placed Students to CSV (Excel)"** to demonstrate saving a clean spreadsheet file to your computer.

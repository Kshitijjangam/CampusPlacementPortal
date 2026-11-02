================================================================================
CAMPUS PLACEMENT DRIVE & JOB APPLICATION PORTAL
4th Year B.Tech Computer Science & Engineering - Mini Project
================================================================================

TEAM MEMBERS:
1. Kshitij Jangam (PRN: 1032230618) - Team Lead
   Role: Data Layer, Registries, Executive GUI Integration & Institutional Analytics
2. Aayush Sharma  (PRN: 1032220990)
   Role: Automated Eligibility Verification Engine, Dream Policy & Pipeline Automation

ACADEMIC YEAR: 2025 - 2026
DEPARTMENT: Computer Science & Engineering
TECHNOLOGY: Java Swing (Desktop GUI) + Java NIO CSV Flat-File Persistence + Built-in Web Server

================================================================================
1. 1-MINUTE QUICK START (HOW TO RUN ON ANY COMPUTER)
================================================================================

Option A (Recommended - Instant 1-Click Launch):
  • Double-click: 1-CLICK-RUN.bat
  • Result: Launches the Swing Desktop GUI window immediately and opens the
    official college intranet portal at http://localhost:8080 in your default browser.

Option B (Interactive Menu & Diagnostics):
  • Double-click: SETUP_AND_RUN.bat
  • Gives options to check Java, recompile, open reports, or launch the app.

Option C (Command Prompt / Terminal):
  • Open Command Prompt in this folder and run:
    java -jar CampusPlacementPortal.jar

================================================================================
2. PRE-REQUISITES
================================================================================
• Operating System: Windows 10/11, macOS, or Linux.
• Java Runtime: Java 17 or higher (Java 8+ compatible).
• Database Requirement: NONE (0% setup). The system uses local CSV flat-files
  in the 'data/' folder. No MySQL, Oracle, or external server installation needed.

================================================================================
3. WHAT IS IN THIS FOLDER (HANDOVER FILE INVENTORY)
================================================================================
• 1-CLICK-RUN.bat                           -> 1-click launcher for Windows.
• SETUP_AND_RUN.bat                         -> Interactive setup, compiler & helper menu.
• HANDOVER_README.txt                       -> This complete handover and viva guide.
• CampusPlacementPortal.jar                 -> Pre-compiled 71 KB standalone runnable application.
• Campus_Placement_Portal_Project_Report.docx -> Full academic report in editable Microsoft Word format.
• PROJECT_REPORT.html                       -> 1-click printable / PDF report (hit Ctrl+P in browser).
• PROJECT_REPORT.md                         -> Academic markdown report.
• README.md                                 -> Project overview & architecture guide.
• pom.xml                                   -> Standard Apache Maven project descriptor.
• build.bat & run.bat                       -> Standard build and run scripts.
• data/                                     -> Local CSV database:
    - students.csv                          (Includes Kshitij Jangam & Aayush Sharma records)
    - drives.csv                            (Upcoming corporate hiring drives)
    - applications.csv                      (Interview pipeline applications)
• src/                                      -> Complete, clean, commented Java source code & web assets:
    - com.placement.Main                    (Application Entry Point)
    - com.placement.model                   (Student, CompanyDrive, Application entity models)
    - com.placement.data                    (DataStore CSV persistence layer)
    - com.placement.service                 (EligibilityService, AnalyticsService)
    - com.placement.ui                      (Dashboard, Student, Drive, Eligibility, Pipeline, Report panels)
    - com.placement.server                  (Lightweight Intranet WebServer)
    - web/                                  (HTML, CSS, JS frontend assets)

================================================================================
4. 5-MINUTE VIVA VOCE DEMONSTRATION WALKTHROUGH
================================================================================

Step 1: Launch
  Double-click 1-CLICK-RUN.bat. Notice the instant startup (< 2 seconds) and zero
  database connection lag.

Step 2 (Kshitij Jangam): Executive Dashboard & Analytics
  Show the Summary Cards:
  • Total Students: 10
  • Placed Count: 4
  • Placement Percentage: 40.0%
  • Highest CTC: 34.0 LPA (Microsoft)
  • Average CTC: 19.7 LPA
  Show the Branch Performance Table: real-time placement stats for CSE, ISE, ECE, MECH.

Step 3 (Kshitij Jangam): Candidate & Company Registries
  • Open the "Company Drives" tab: show upcoming drives (Google 28.5 LPA, Microsoft 34 LPA, Infosys, TCS).
  • Open the "Students Directory" tab: show pre-seeded candidate records, including
    Kshitij Jangam (PRN: 1032230618) and Aayush Sharma (PRN: 1032220990).
  • Type "Kshitij" into the search bar: show instantaneous row filtering across records.
  • Click "+ Add New Student" to show the live candidate registration dialog.

Step 4 (Aayush Sharma): Automated Eligibility Verification Engine
  • Switch to the "Apply & Eligibility Checker" tab.
  • Select Drive: "Google (28.5 LPA, Min CGPA 8.5)".
  • Select Student: "Kshitij Jangam (CGPA 9.20, 0 backlogs)".
    -> Notice the green checkmarks: PASS: Meets CGPA cutoff, PASS: No active backlogs,
       PASS: Branch eligible. The "Submit Job Application" button turns active.
  • Select Student: "Karan Malhotra (CGPA 6.80, 1 backlog)".
    -> Notice the red rejection explanations: FAIL: CGPA below cutoff (8.5),
       FAIL: Active backlogs exceed limit (0). The button disables automatically.
  • Explain the "Dream Company 1.5x Rule": If a student is already placed, the system
    allows applying only if the new drive package is >= 1.5x their existing package.

Step 5 (Aayush Sharma): Recruitment Pipeline & Automated Offer Sync
  • Switch to "Application Pipeline" tab.
  • Select an application currently in "TECHNICAL_INTERVIEW".
  • In the dropdown, change the stage to "OFFERED" and click "Update Candidate Stage".
  • Notice the pop-up alert confirming the candidate is placed.
  • Switch back to "Students Directory" and "Dashboard" tabs: the student is now
    automatically marked as "PLACED" with their new company and package, and all
    institutional statistics update in real time without manual re-entry!

Step 6 (Kshitij Jangam): Excel Report Export
  • Switch to "Placement Reports" tab.
  • Click "Export Placed Students to CSV (Excel)".
  • Save the file and open it in Microsoft Excel to demonstrate full compatibility.

================================================================================
5. CORE VIVA VOCE QUESTIONS & ANSWERS
================================================================================

SECTION A: For Kshitij Jangam (Data Layer, Registries & GUI Analytics)
--------------------------------------------------------------------------------
Q1: Why did you choose CSV files rather than MySQL or SQLite?
Answer: In an academic mini-project, Portability and Zero-Configuration are paramount.
Traditional databases (MySQL, PostgreSQL) require local server services, port binding,
and configuration scripts that frequently fail on external examiner laptops.
CSV (Comma Separated Values) flat files provide human-readable, reliable persistence
handled natively by Java standard I/O, ensuring the software runs on any device immediately.

Q2: How does the application prevent duplicate student entries?
Answer: In DataStore.java, student records are indexed by their unique PRN/USN.
During addStudent() execution, the system checks whether a student with the given PRN
already exists. If found, an exception is raised, preventing duplicates.

Q3: How is dynamic table filtering implemented in StudentPanel?
Answer: We use Java Swing's TableRowSorter attached to the DefaultTableModel.
When the user types into the search JTextField, a DocumentListener triggers a regex
filter: sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)). This achieves
instantaneous, case-insensitive row filtering across PRN, name, and branch columns.

Q4: How does the application ensure thread safety in Java Swing?
Answer: All GUI rendering and updates run on the Swing Event Dispatch Thread (EDT)
using SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true)), preventing
threading conflicts and UI freezes.

--------------------------------------------------------------------------------
SECTION B: For Aayush Sharma (Eligibility Engine & Pipeline Automation)
--------------------------------------------------------------------------------
Q5: What is the Dream Company 1.5x Rule and why is it implemented?
Answer: In college recruitment, students who have already secured an offer often
apply for similar low-paying drives, preventing unplaced peers from securing jobs.
Our Dream Company Rule enforces that an already placed candidate can only apply for
a new drive if the new CTC is at least 1.5 times their current compensation:
(New CTC >= 1.5 * Current CTC). This is evaluated in EligibilityService.java.

Q6: What happens if a candidate satisfies CGPA criteria but fails backlog criteria?
Answer: EligibilityService evaluates all rules deterministically. It constructs an
EligibilityResult object containing an overall boolean verdict and an array of individual
test explanations. If any single criterion fails (e.g., active backlogs > maxBacklogs),
the overall verdict returns FALSE, and the GUI renders a clear rejection explanation
while disabling the application submission button.

Q7: What are the sequential stages in your recruitment pipeline?
Answer: The application models the standard engineering recruitment process across
seven distinct stages: APPLIED -> SHORTLISTED -> ONLINE_TEST -> TECHNICAL_INTERVIEW ->
HR_INTERVIEW -> OFFERED (or REJECTED). Candidates move progressively based on results.

Q8: Explain the Automated Offer Synchronization mechanism.
Answer: When a coordinator changes an application's stage to 'OFFERED' in PipelinePanel,
the system triggers an automatic update in DataStore. The candidate's Student object is
retrieved, its placementStatus is updated to 'PLACED', placedCompany is set to the company
name, and placedPackageLpa is recorded. The updated state is immediately saved to
students.csv and reflected on the Dashboard.

================================================================================
END OF HANDOVER README
================================================================================

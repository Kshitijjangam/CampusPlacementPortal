package com.placement.ui;

import com.placement.data.DataStore;
import com.placement.model.Application;
import com.placement.model.CompanyDrive;
import com.placement.model.Student;
import com.placement.service.EligibilityService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Automated Candidate Eligibility Evaluation and Application Panel.
 */
public class EligibilityPanel extends JPanel {

    private final DataStore dataStore;
    private final EligibilityService eligibilityService;

    private JComboBox<CompanyDrive> cmbDrive;
    private JComboBox<Student> cmbStudent;
    private JTextArea txtResults;
    private JButton btnApply;
    private JLabel lblVerdict;

    public EligibilityPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        this.eligibilityService = new EligibilityService();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        reloadCombos();
    }

    private void initComponents() {
        // --- Top Selection Panel ---
        JPanel selectPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        selectPanel.setBorder(new TitledBorder("Select Placement Drive and Candidate"));

        selectPanel.add(new JLabel("Target Company Drive:"));
        cmbDrive = new JComboBox<>();
        cmbDrive.addActionListener(e -> checkEligibility());
        selectPanel.add(cmbDrive);

        selectPanel.add(new JLabel("Candidate Student Profile:"));
        cmbStudent = new JComboBox<>();
        cmbStudent.addActionListener(e -> checkEligibility());
        selectPanel.add(cmbStudent);

        add(selectPanel, BorderLayout.NORTH);

        // --- Center: Verification Results ---
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(new TitledBorder("Automated Verification Results"));

        lblVerdict = new JLabel("STATUS: PENDING SELECTION", SwingConstants.CENTER);
        lblVerdict.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblVerdict.setOpaque(true);
        lblVerdict.setBackground(new Color(236, 240, 241));
        lblVerdict.setBorder(new EmptyBorder(8, 8, 8, 8));
        centerPanel.add(lblVerdict, BorderLayout.NORTH);

        txtResults = new JTextArea();
        txtResults.setEditable(false);
        txtResults.setFont(new Font("Monospaced", Font.PLAIN, 13));
        centerPanel.add(new JScrollPane(txtResults), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Controls ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton btnCheck = new JButton("Re-evaluate Eligibility");
        btnCheck.addActionListener(e -> checkEligibility());
        bottomPanel.add(btnCheck);

        btnApply = new JButton("Submit Job Application");
        btnApply.setEnabled(false);
        btnApply.addActionListener(e -> submitApplication());
        bottomPanel.add(btnApply);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void reloadCombos() {
        cmbDrive.removeAllItems();
        for (CompanyDrive d : dataStore.getDrives()) {
            cmbDrive.addItem(d);
        }

        cmbStudent.removeAllItems();
        for (Student s : dataStore.getStudents()) {
            cmbStudent.addItem(s);
        }

        checkEligibility();
    }

    private void checkEligibility() {
        CompanyDrive drive = (CompanyDrive) cmbDrive.getSelectedItem();
        Student student = (Student) cmbStudent.getSelectedItem();

        if (drive == null || student == null) {
            txtResults.setText("Please select both a company drive and a student.");
            lblVerdict.setText("STATUS: PENDING");
            lblVerdict.setBackground(new Color(236, 240, 241));
            lblVerdict.setForeground(Color.BLACK);
            btnApply.setEnabled(false);
            return;
        }

        EligibilityService.EligibilityResult result = eligibilityService.evaluate(student, drive);

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================================\n");
        sb.append(" CANDIDATE ELIGIBILITY EVALUATION REPORT\n");
        sb.append("==================================================================\n");
        sb.append(String.format("Student: %s (%s) | Branch: %s | CGPA: %.2f | Backlogs: %d\n",
                student.getName(), student.getUsn(), student.getBranch(), student.getCgpa(), student.getActiveBacklogs()));
        sb.append(String.format("Company: %s | Role: %s | Package: %.2f LPA | Min CGPA: %.2f\n",
                drive.getCompanyName(), drive.getJobRole(), drive.getCtcLpa(), drive.getMinCgpa()));
        sb.append("------------------------------------------------------------------\n");

        for (String detail : result.getDetails()) {
            sb.append(detail).append("\n");
        }
        sb.append("------------------------------------------------------------------\n");

        // Check for duplicate application
        boolean alreadyApplied = false;
        for (Application a : dataStore.getApplications()) {
            if (a.getStudentUsn().equalsIgnoreCase(student.getUsn()) &&
                a.getDriveId().equalsIgnoreCase(drive.getDriveId())) {
                alreadyApplied = true;
                break;
            }
        }

        if (alreadyApplied) {
            sb.append("ALERT: Candidate has ALREADY submitted an application for this drive!\n");
            lblVerdict.setText("STATUS: ALREADY APPLIED");
            lblVerdict.setBackground(new Color(243, 156, 18));
            lblVerdict.setForeground(Color.WHITE);
            btnApply.setEnabled(false);
        } else if (result.isEligible()) {
            sb.append("CONCLUSION: Candidate meets all criteria and is ELIGIBLE to apply!\n");
            lblVerdict.setText("STATUS: VERIFIED ELIGIBLE - READY TO APPLY");
            lblVerdict.setBackground(new Color(46, 204, 113));
            lblVerdict.setForeground(Color.WHITE);
            btnApply.setEnabled(true);
        } else {
            sb.append("CONCLUSION: Candidate does NOT meet the required criteria.\n");
            lblVerdict.setText("STATUS: NOT ELIGIBLE");
            lblVerdict.setBackground(new Color(231, 76, 60));
            lblVerdict.setForeground(Color.WHITE);
            btnApply.setEnabled(false);
        }

        txtResults.setText(sb.toString());
    }

    private void submitApplication() {
        CompanyDrive drive = (CompanyDrive) cmbDrive.getSelectedItem();
        Student student = (Student) cmbStudent.getSelectedItem();

        if (drive == null || student == null) return;

        String id = "APP-" + (dataStore.getApplications().size() + 101);
        String today = LocalDate.now().toString();

        Application app = new Application(id, student.getUsn(), student.getName(), drive.getDriveId(),
                drive.getCompanyName(), drive.getJobRole(), today, "APPLIED", "-", "Application submitted and verified.");

        dataStore.addApplication(app);
        JOptionPane.showMessageDialog(this,
                "Application submitted successfully for " + student.getName() + "!\nAdded to candidate recruitment pipeline.",
                "Application Submitted", JOptionPane.INFORMATION_MESSAGE);

        checkEligibility();
    }
}

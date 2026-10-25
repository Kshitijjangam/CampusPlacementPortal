package com.placement.ui;

import com.placement.data.DataStore;
import com.placement.model.Application;
import com.placement.model.CompanyDrive;
import com.placement.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Recruitment Pipeline Panel.
 * Tracks application progress across interview stages and handles automated offer synchronization.
 */
public class PipelinePanel extends JPanel {

    private final DataStore dataStore;
    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<String> cmbStage;
    private JTextField txtInterviewDate;

    public PipelinePanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
        loadApplications();
    }

    private void initComponents() {
        // --- Center: Applications Table ---
        String[] cols = {"App ID", "Student USN", "Candidate Name", "Company", "Job Designation", "Applied Date", "Recruitment Stage", "Interview Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Bottom: Stage Transition Controls ---
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new TitledBorder("Update Selected Candidate Pipeline Stage"));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        controls.add(new JLabel("Move to Stage:"));
        cmbStage = new JComboBox<>(new String[]{
                "APPLIED", "SHORTLISTED", "ONLINE_TEST", "TECHNICAL_INTERVIEW", "HR_INTERVIEW", "OFFERED", "REJECTED"
        });
        controls.add(cmbStage);

        controls.add(new JLabel("Interview Date/Time:"));
        txtInterviewDate = new JTextField("2026-11-15 10:00 AM", 15);
        controls.add(txtInterviewDate);

        JButton btnUpdate = new JButton("Update Candidate Stage");
        btnUpdate.addActionListener(e -> updateSelectedStage());
        controls.add(btnUpdate);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadApplications());
        controls.add(btnRefresh);

        bottomPanel.add(controls, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadApplications() {
        tableModel.setRowCount(0);
        List<Application> list = dataStore.getApplications();
        for (Application a : list) {
            tableModel.addRow(new Object[]{
                    a.getApplicationId(), a.getStudentUsn(), a.getStudentName(),
                    a.getCompanyName(), a.getJobRole(), a.getAppliedDate(),
                    a.getStage(), a.getInterviewDate()
            });
        }
    }

    private void updateSelectedStage() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an application record from the table.");
            return;
        }

        String appId = (String) table.getValueAt(row, 0);
        String newStage = (String) cmbStage.getSelectedItem();
        String date = txtInterviewDate.getText().trim();

        Application app = null;
        for (Application a : dataStore.getApplications()) {
            if (a.getApplicationId().equalsIgnoreCase(appId)) {
                app = a;
                break;
            }
        }

        if (app == null) return;

        app.setStage(newStage);
        if (!date.isEmpty()) {
            app.setInterviewDate(date);
        }
        dataStore.updateApplication(app);

        // Automated Business Logic: If candidate is OFFERED, mark student as PLACED
        if ("OFFERED".equalsIgnoreCase(newStage)) {
            Student s = dataStore.findStudentByUsn(app.getStudentUsn());
            CompanyDrive d = dataStore.findDriveById(app.getDriveId());

            if (s != null && d != null) {
                s.setPlacementStatus("PLACED");
                s.setPlacedCompany(d.getCompanyName());
                s.setPlacedPackageLpa(d.getCtcLpa());
                dataStore.updateStudent(s);

                JOptionPane.showMessageDialog(this,
                        "Offer Accepted!\nStudent " + s.getName() + " (" + s.getUsn() + ") has been automatically updated as PLACED at "
                        + d.getCompanyName() + " (" + d.getCtcLpa() + " LPA)!",
                        "Placement Offer Extended", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Application " + appId + " moved to " + newStage + "!");
        }

        loadApplications();
    }
}

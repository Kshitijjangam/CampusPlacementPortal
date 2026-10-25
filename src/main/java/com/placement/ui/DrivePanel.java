package com.placement.ui;

import com.placement.data.DataStore;
import com.placement.model.CompanyDrive;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Company Placement Drives Management Panel.
 */
public class DrivePanel extends JPanel {

    private final DataStore dataStore;
    private JTable table;
    private DefaultTableModel tableModel;

    public DrivePanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
        loadDrives();
    }

    private void initComponents() {
        // --- Center: Drives Table ---
        String[] cols = {"Drive ID", "Company Name", "Job Designation", "CTC (LPA)", "Min CGPA Cutoff", "Max Backlogs", "Eligible Branches", "Drive Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Bottom: Action Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton btnAdd = new JButton("+ Register Company Drive");
        btnAdd.addActionListener(e -> showAddDriveDialog());
        bottomPanel.add(btnAdd);

        JButton btnDelete = new JButton("Delete Selected Drive");
        btnDelete.addActionListener(e -> deleteSelectedDrive());
        bottomPanel.add(btnDelete);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadDrives());
        bottomPanel.add(btnRefresh);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadDrives() {
        tableModel.setRowCount(0);
        List<CompanyDrive> list = dataStore.getDrives();
        for (CompanyDrive d : list) {
            tableModel.addRow(new Object[]{
                    d.getDriveId(), d.getCompanyName(), d.getJobRole(),
                    String.format(java.util.Locale.US, "%.2f LPA", d.getCtcLpa()),
                    String.format(java.util.Locale.US, "%.2f", d.getMinCgpa()),
                    d.getMaxBacklogs(), d.getEligibleBranches(), d.getDriveDate(), d.getStatus()
            });
        }
    }

    private void showAddDriveDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register Placement Drive", true);
        dlg.setSize(420, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(7, 2, 8, 8));

        JTextField txtComp = new JTextField();
        JTextField txtRole = new JTextField("Software Development Engineer");
        JTextField txtCtc = new JTextField("12.0");
        JTextField txtCgpa = new JTextField("7.5");
        JTextField txtBacklogs = new JTextField("0");
        JTextField txtBranches = new JTextField("CSE,ISE,ECE");
        JTextField txtDate = new JTextField("2026-11-20");

        dlg.add(new JLabel("  Company Name:"));
        dlg.add(txtComp);
        dlg.add(new JLabel("  Job Designation:"));
        dlg.add(txtRole);
        dlg.add(new JLabel("  CTC Package (LPA):"));
        dlg.add(txtCtc);
        dlg.add(new JLabel("  Min CGPA Cutoff:"));
        dlg.add(txtCgpa);
        dlg.add(new JLabel("  Max Backlogs Allowed:"));
        dlg.add(txtBacklogs);
        dlg.add(new JLabel("  Eligible Branches:"));
        dlg.add(txtBranches);
        dlg.add(new JLabel("  Drive Date (YYYY-MM-DD):"));
        dlg.add(txtDate);

        JButton btnSave = new JButton("Publish Drive");
        btnSave.addActionListener(e -> {
            try {
                String comp = txtComp.getText().trim();
                String role = txtRole.getText().trim();
                double ctc = Double.parseDouble(txtCtc.getText().trim());
                double minCgpa = Double.parseDouble(txtCgpa.getText().trim());
                int backlogs = Integer.parseInt(txtBacklogs.getText().trim());
                String branches = txtBranches.getText().trim();
                String date = txtDate.getText().trim();

                if (comp.isEmpty() || role.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Company and Role cannot be empty.");
                    return;
                }

                String id = "DRV-0" + (dataStore.getDrives().size() + 1);
                CompanyDrive d = new CompanyDrive(id, comp, role, ctc, minCgpa, backlogs, branches, date, "OPEN");
                dataStore.addDrive(d);
                dlg.dispose();
                loadDrives();
                JOptionPane.showMessageDialog(this, "Company drive published successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid inputs: " + ex.getMessage());
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dlg.dispose());

        dlg.add(btnSave);
        dlg.add(btnCancel);
        dlg.setVisible(true);
    }

    private void deleteSelectedDrive() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a drive from the table.");
            return;
        }
        String id = (String) table.getValueAt(row, 0);
        int opt = JOptionPane.showConfirmDialog(this, "Delete placement drive " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            dataStore.deleteDrive(id);
            loadDrives();
        }
    }
}

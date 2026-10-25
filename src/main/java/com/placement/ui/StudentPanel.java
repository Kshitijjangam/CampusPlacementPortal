package com.placement.ui;

import com.placement.data.DataStore;
import com.placement.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Student Directory Management Panel.
 * Supports viewing, searching, adding, and deleting student profiles.
 */
public class StudentPanel extends JPanel {

    private final DataStore dataStore;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbBranchFilter;

    public StudentPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
        loadStudents();
    }

    private void initComponents() {
        // --- Top Bar: Search and Filter ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        topPanel.add(new JLabel("Search (Name/USN):"));
        txtSearch = new JTextField(15);
        txtSearch.addActionListener(e -> filterStudents());
        topPanel.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> filterStudents());
        topPanel.add(btnSearch);

        topPanel.add(new JLabel("Branch:"));
        cmbBranchFilter = new JComboBox<>(new String[]{"ALL", "CSE", "ISE", "ECE", "MECH"});
        cmbBranchFilter.addActionListener(e -> filterStudents());
        topPanel.add(cmbBranchFilter);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbBranchFilter.setSelectedIndex(0);
            loadStudents();
        });
        topPanel.add(btnReset);

        add(topPanel, BorderLayout.NORTH);

        // --- Center: Students Table ---
        String[] cols = {"USN / Roll No", "Full Name", "Email", "Branch", "CGPA", "Backlogs", "Skills", "Status", "Placed Company", "Package (LPA)"};
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

        JButton btnAdd = new JButton("+ Add New Student");
        btnAdd.addActionListener(e -> showAddStudentDialog());
        bottomPanel.add(btnAdd);

        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.addActionListener(e -> deleteSelectedStudent());
        bottomPanel.add(btnDelete);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadStudents());
        bottomPanel.add(btnRefresh);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadStudents() {
        tableModel.setRowCount(0);
        List<Student> list = dataStore.getStudents();
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getUsn(), s.getName(), s.getEmail(), s.getBranch(),
                    String.format(java.util.Locale.US, "%.2f", s.getCgpa()),
                    s.getActiveBacklogs(), s.getSkills(), s.getPlacementStatus(),
                    s.getPlacedCompany(),
                    s.getPlacedPackageLpa() > 0 ? String.format(java.util.Locale.US, "%.2f LPA", s.getPlacedPackageLpa()) : "-"
            });
        }
    }

    private void filterStudents() {
        String query = txtSearch.getText().trim().toLowerCase();
        String branch = (String) cmbBranchFilter.getSelectedItem();

        tableModel.setRowCount(0);
        List<Student> list = dataStore.getStudents();
        for (Student s : list) {
            boolean matchQuery = query.isEmpty() ||
                    s.getUsn().toLowerCase().contains(query) ||
                    s.getName().toLowerCase().contains(query);

            boolean matchBranch = "ALL".equalsIgnoreCase(branch) ||
                    s.getBranch().equalsIgnoreCase(branch);

            if (matchQuery && matchBranch) {
                tableModel.addRow(new Object[]{
                        s.getUsn(), s.getName(), s.getEmail(), s.getBranch(),
                        String.format(java.util.Locale.US, "%.2f", s.getCgpa()),
                        s.getActiveBacklogs(), s.getSkills(), s.getPlacementStatus(),
                        s.getPlacedCompany(),
                        s.getPlacedPackageLpa() > 0 ? String.format(java.util.Locale.US, "%.2f LPA", s.getPlacedPackageLpa()) : "-"
                });
            }
        }
    }

    private void showAddStudentDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register Student Profile", true);
        dlg.setSize(400, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(7, 2, 8, 8));

        JTextField txtUsn = new JTextField("1RV22CS0" + (dataStore.getStudents().size() + 10));
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JComboBox<String> cmbDept = new JComboBox<>(new String[]{"CSE", "ISE", "ECE", "MECH"});
        JTextField txtCgpa = new JTextField("8.0");
        JTextField txtBacklogs = new JTextField("0");
        JTextField txtSkills = new JTextField("Java, SQL, DSA");

        dlg.add(new JLabel("  USN / Roll No:"));
        dlg.add(txtUsn);
        dlg.add(new JLabel("  Student Name:"));
        dlg.add(txtName);
        dlg.add(new JLabel("  Email ID:"));
        dlg.add(txtEmail);
        dlg.add(new JLabel("  Department / Branch:"));
        dlg.add(cmbDept);
        dlg.add(new JLabel("  CGPA:"));
        dlg.add(txtCgpa);
        dlg.add(new JLabel("  Active Backlogs:"));
        dlg.add(txtBacklogs);
        dlg.add(new JLabel("  Technical Skills:"));
        dlg.add(txtSkills);

        JButton btnSave = new JButton("Save Candidate");
        btnSave.addActionListener(e -> {
            try {
                String usn = txtUsn.getText().trim();
                String name = txtName.getText().trim();
                String email = txtEmail.getText().trim();
                String dept = (String) cmbDept.getSelectedItem();
                double cgpa = Double.parseDouble(txtCgpa.getText().trim());
                int backlogs = Integer.parseInt(txtBacklogs.getText().trim());
                String skills = txtSkills.getText().trim();

                if (usn.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "USN and Name cannot be empty.");
                    return;
                }

                Student s = new Student(usn, name, email, dept, cgpa, backlogs, skills, "NOT PLACED", "", 0.0);
                dataStore.addStudent(s);
                dlg.dispose();
                loadStudents();
                JOptionPane.showMessageDialog(this, "Student registered successfully!");
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

    private void deleteSelectedStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table.");
            return;
        }
        String usn = (String) table.getValueAt(row, 0);
        int opt = JOptionPane.showConfirmDialog(this, "Delete student record for " + usn + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            dataStore.deleteStudent(usn);
            loadStudents();
        }
    }
}

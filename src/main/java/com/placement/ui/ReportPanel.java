package com.placement.ui;

import com.placement.data.DataStore;
import com.placement.model.Student;
import com.placement.service.AnalyticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * Report Generation and Data Export Panel.
 */
public class ReportPanel extends JPanel {

    private final DataStore dataStore;
    private final AnalyticsService analyticsService;
    private JTextArea txtReport;

    public ReportPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        this.analyticsService = new AnalyticsService();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
        generateReport();
    }

    private void initComponents() {
        txtReport = new JTextArea();
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(txtReport), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton btnGenerate = new JButton("Generate Live Summary");
        btnGenerate.addActionListener(e -> generateReport());
        bottomPanel.add(btnGenerate);

        JButton btnExportCsv = new JButton("Export Placed Students to CSV (Excel)");
        btnExportCsv.addActionListener(e -> exportPlacedToCsv());
        bottomPanel.add(btnExportCsv);

        JButton btnReset = new JButton("Reset Sample Data");
        btnReset.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Reset placement database to default sample data for live demo?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                dataStore.seedSampleData();
                generateReport();
                JOptionPane.showMessageDialog(this, "Sample dataset reloaded successfully!");
            }
        });
        bottomPanel.add(btnReset);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void generateReport() {
        List<Student> students = dataStore.getStudents();
        AnalyticsService.PlacementStats stats = analyticsService.calculate(students);

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                  DEPARTMENT OF TRAINING & PLACEMENT (T&P CELL)                         \n");
        sb.append("                     ANNUAL CAMPUS PLACEMENT SUMMARY REPORT                             \n");
        sb.append("                         Report Generated: ").append(LocalDate.now()).append("\n");
        sb.append("========================================================================================\n\n");

        sb.append(String.format("Total Registered 4th Year Candidates: %d\n", stats.totalStudents));
        sb.append(String.format("Total Candidates Successfully Placed: %d\n", stats.placedStudents));
        sb.append(String.format("Overall Institutional Placement Rate: %.1f%%\n", stats.placementPercentage));
        sb.append(String.format("Highest CTC Package Offered:         %.2f LPA\n", stats.highestPackageLpa));
        sb.append(String.format("Average CTC Package Offered:         %.2f LPA\n\n", stats.averagePackageLpa));

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" BRANCH-WISE PLACEMENT BREAKDOWN:\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-12s | %-16s | %-14s | %-20s\n", "Branch", "Total Students", "Placed Count", "Placement Rate (%)"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (String b : stats.branchTotal.keySet()) {
            int tot = stats.branchTotal.get(b);
            int placed = stats.branchPlaced.getOrDefault(b, 0);
            double pct = tot > 0 ? ((double) placed / tot) * 100.0 : 0.0;
            sb.append(String.format("%-12s | %-16d | %-14d | %-20.1f%%\n", b, tot, placed, pct));
        }

        sb.append("----------------------------------------------------------------------------------------\n\n");
        sb.append(" LIST OF PLACED STUDENTS:\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-14s | %-20s | %-8s | %-16s | %-12s\n", "USN", "Candidate Name", "Branch", "Company Placed", "Package"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (Student s : students) {
            if ("PLACED".equalsIgnoreCase(s.getPlacementStatus())) {
                sb.append(String.format("%-14s | %-20s | %-8s | %-16s | %-12.2f LPA\n",
                        s.getUsn(), s.getName(), s.getBranch(), s.getPlacedCompany(), s.getPlacedPackageLpa()));
            }
        }

        sb.append("========================================================================================\n");
        txtReport.setText(sb.toString());
    }

    private void exportPlacedToCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Placed_Students_Report.csv"));
        int res = chooser.showSaveDialog(this);

        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {
                fw.write("USN,Name,Email,Branch,CGPA,Placed_Company,Package_LPA\n");
                for (Student s : dataStore.getStudents()) {
                    if ("PLACED".equalsIgnoreCase(s.getPlacementStatus())) {
                        fw.write(String.format(java.util.Locale.US, "%s,%s,%s,%s,%.2f,%s,%.2f\n",
                                s.getUsn(), s.getName(), s.getEmail(), s.getBranch(), s.getCgpa(),
                                s.getPlacedCompany(), s.getPlacedPackageLpa()));
                    }
                }
                JOptionPane.showMessageDialog(this,
                        "Successfully exported placed students list to:\n" + file.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

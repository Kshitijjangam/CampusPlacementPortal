package com.placement.ui;

import com.placement.data.DataStore;
import com.placement.service.AnalyticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dashboard Panel displaying placement statistics and branch performance.
 */
public class DashboardPanel extends JPanel {

    private final DataStore dataStore;
    private final AnalyticsService analyticsService;

    private JLabel lblTotalStudents;
    private JLabel lblPlacedStudents;
    private JLabel lblPlacementRate;
    private JLabel lblHighestPackage;
    private JLabel lblAveragePackage;
    private JTable branchTable;
    private DefaultTableModel branchTableModel;

    public DashboardPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        this.analyticsService = new AnalyticsService();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- Top KPI Cards Panel ---
        JPanel kpiPanel = new JPanel(new GridLayout(1, 5, 12, 12));
        kpiPanel.setPreferredSize(new Dimension(800, 100));

        lblTotalStudents = createKpiCard(kpiPanel, "Total Registered", "0", new Color(41, 128, 185));
        lblPlacedStudents = createKpiCard(kpiPanel, "Placed Students", "0", new Color(39, 174, 96));
        lblPlacementRate = createKpiCard(kpiPanel, "Placement Rate", "0%", new Color(46, 204, 113));
        lblHighestPackage = createKpiCard(kpiPanel, "Highest Package", "0.0 LPA", new Color(142, 68, 173));
        lblAveragePackage = createKpiCard(kpiPanel, "Average Package", "0.0 LPA", new Color(230, 126, 34));

        add(kpiPanel, BorderLayout.NORTH);

        // --- Center: Branch-wise Breakdown Table ---
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(new TitledBorder("Department-Wise Placement Statistics"));

        String[] cols = { "Department / Branch", "Total Students", "Placed Count", "Unplaced Count", "Placement Percentage (%)" };
        branchTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        branchTable = new JTable(branchTableModel);
        branchTable.setRowHeight(26);
        branchTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(new JScrollPane(branchTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Controls ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh Dashboard");
        btnRefresh.addActionListener(e -> refreshData());
        bottomPanel.add(btnRefresh);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createKpiCard(JPanel parent, String title, String initialVal, Color color) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitle.setForeground(Color.DARK_GRAY);

        JLabel lblVal = new JLabel(initialVal, SwingConstants.CENTER);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblVal.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        parent.add(card);

        return lblVal;
    }

    public void refreshData() {
        AnalyticsService.PlacementStats stats = analyticsService.calculate(dataStore.getStudents());

        lblTotalStudents.setText(String.valueOf(stats.totalStudents));
        lblPlacedStudents.setText(String.valueOf(stats.placedStudents));
        lblPlacementRate.setText(String.format(java.util.Locale.US, "%.1f%%", stats.placementPercentage));
        lblHighestPackage.setText(String.format(java.util.Locale.US, "%.1f LPA", stats.highestPackageLpa));
        lblAveragePackage.setText(String.format(java.util.Locale.US, "%.1f LPA", stats.averagePackageLpa));

        branchTableModel.setRowCount(0);
        for (String branch : stats.branchTotal.keySet()) {
            int tot = stats.branchTotal.get(branch);
            int placed = stats.branchPlaced.getOrDefault(branch, 0);
            int unplaced = tot - placed;
            double pct = tot > 0 ? ((double) placed / tot) * 100.0 : 0.0;

            branchTableModel.addRow(new Object[]{
                    branch, tot, placed, unplaced, String.format(java.util.Locale.US, "%.1f%%", pct)
            });
        }
    }
}

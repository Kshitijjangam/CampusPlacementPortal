package com.placement.ui;

import com.placement.data.DataStore;

import javax.swing.*;
import java.awt.*;

/**
 * Main Application Window.
 * Contains menu bar, tabbed modules, and status bar.
 */
public class MainFrame extends JFrame {

    private final DataStore dataStore;

    private DashboardPanel dashboardPanel;
    private StudentPanel studentPanel;
    private DrivePanel drivePanel;
    private EligibilityPanel eligibilityPanel;
    private PipelinePanel pipelinePanel;
    private ReportPanel reportPanel;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        super("Campus Placement Drive & Job Application Portal - T&P Cell");
        this.dataStore = new DataStore();

        initComponents();
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        // --- 1. Menu Bar ---
        setJMenuBar(createMenuBar());

        // --- 2. Tabbed Pane ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));

        dashboardPanel = new DashboardPanel(dataStore);
        drivePanel = new DrivePanel(dataStore);
        studentPanel = new StudentPanel(dataStore);
        eligibilityPanel = new EligibilityPanel(dataStore);
        pipelinePanel = new PipelinePanel(dataStore);
        reportPanel = new ReportPanel(dataStore);

        tabbedPane.addTab("  Dashboard & Analytics  ", dashboardPanel);
        tabbedPane.addTab("  Company Drives  ", drivePanel);
        tabbedPane.addTab("  Students Directory  ", studentPanel);
        tabbedPane.addTab("  Apply & Eligibility Checker  ", eligibilityPanel);
        tabbedPane.addTab("  Application Pipeline  ", pipelinePanel);
        tabbedPane.addTab("  Placement Reports  ", reportPanel);

        // Auto-refresh when tab is switched
        tabbedPane.addChangeListener(e -> refreshActiveTab());

        add(tabbedPane, BorderLayout.CENTER);

        // --- 3. Bottom Status Bar ---
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusBar.setBackground(new Color(245, 245, 245));

        JLabel lblStatus = new JLabel("  Status: System Ready | Local CSV DataStore Connected (Offline Mode)");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.add(lblStatus, BorderLayout.WEST);

        JLabel lblTeam = new JLabel("B.Tech CSE Mini Project - Team of 4  ");
        lblTeam.setFont(new Font("SansSerif", Font.ITALIC, 11));
        statusBar.add(lblTeam, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();

        // File Menu
        JMenu menuFile = new JMenu("File");
        JMenuItem itemReset = new JMenuItem("Reset Sample Data");
        itemReset.addActionListener(e -> {
            dataStore.seedSampleData();
            refreshAll();
            JOptionPane.showMessageDialog(this, "Sample data reloaded!");
        });
        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(e -> System.exit(0));
        menuFile.add(itemReset);
        menuFile.addSeparator();
        menuFile.add(itemExit);

        // Tools Menu
        JMenu menuTools = new JMenu("Tools");
        JMenuItem itemRefresh = new JMenuItem("Refresh All Data");
        itemRefresh.addActionListener(e -> refreshAll());
        menuTools.add(itemRefresh);

        // Help Menu
        JMenu menuHelp = new JMenu("Help");
        JMenuItem itemTeam = new JMenuItem("Student Work Breakdown");
        itemTeam.addActionListener(e -> showTeamDialog());
        JMenuItem itemAbout = new JMenuItem("About System");
        itemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "PlaceTrack - Campus Placement Portal\nDeveloped for 4th Year B.Tech CSE Mini Project.\nPlatform: Java Swing Desktop Application.",
                "About PlaceTrack", JOptionPane.INFORMATION_MESSAGE));
        menuHelp.add(itemTeam);
        menuHelp.add(itemAbout);

        mb.add(menuFile);
        mb.add(menuTools);
        mb.add(menuHelp);

        return mb;
    }

    private void refreshActiveTab() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) dashboardPanel.refreshData();
        else if (idx == 1) drivePanel.loadDrives();
        else if (idx == 2) studentPanel.loadStudents();
        else if (idx == 3) eligibilityPanel.reloadCombos();
        else if (idx == 4) pipelinePanel.loadApplications();
        else if (idx == 5) reportPanel.generateReport();
    }

    private void refreshAll() {
        dashboardPanel.refreshData();
        drivePanel.loadDrives();
        studentPanel.loadStudents();
        eligibilityPanel.reloadCombos();
        pipelinePanel.loadApplications();
        reportPanel.generateReport();
    }

    private void showTeamDialog() {
        String msg = "4-STUDENT MODULE DIVISION:\n\n" +
                "• Student 1: Student Profiles & Company Drives Registry (Student.java, CompanyDrive.java, DataStore.java)\n" +
                "• Student 2: Automated Eligibility & Filtering Engine (EligibilityService.java, CGPA/Backlog checks)\n" +
                "• Student 3: Candidate Recruitment Pipeline & Offer Workflow (Application.java, PipelinePanel.java)\n" +
                "• Student 4: Institutional Analytics & GUI Integration (AnalyticsService.java, MainFrame.java)\n";
        JOptionPane.showMessageDialog(this, msg, "Student Work Breakdown", JOptionPane.INFORMATION_MESSAGE);
    }
}

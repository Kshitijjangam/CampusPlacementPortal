package com.placement;

import com.placement.data.DataStore;
import com.placement.server.WebServer;
import com.placement.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Campus Placement Drive & Job Application Portal
 * Mini Project - 4th Year B.Tech Computer Science & Engineering
 * 
 * @author Kshitij Jangam (PRN: 1032230618) - Team Lead
 * @author Aayush Sharma  (PRN: 1032220990)
 * 
 * Entry Point:
 * Initializes DataStore (CSV persistence), launches the embedded
 * College T&P Intranet Web Server at http://localhost:8080, and opens
 * the Java Swing Desktop GUI on the Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        // Initialize DataStore (CSV persistence layer)
        DataStore store = new DataStore();

        // 1. Start Embedded College Intranet Web Server
        int port = WebServer.start(8080, store);

        // 2. Launch Swing Desktop GUI if graphic environment is available
        try {
            if (!GraphicsEnvironment.isHeadless()) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                SwingUtilities.invokeLater(() -> {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                });
            }
        } catch (Exception ignored) {}
    }
}

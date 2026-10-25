package com.placement;

import com.placement.data.DataStore;
import com.placement.server.WebServer;
import com.placement.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Application Entry Point.
 * Starts the embedded Web Server at http://localhost:8080 AND launches the Swing Desktop GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize DataStore (CSV persistence)
        DataStore store = new DataStore();

        // 1. Start Embedded Web Server
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

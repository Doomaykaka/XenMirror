package xenmirror.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import xenmirror.controllers.BackupController;
import xenmirror.utils.Config;
import xenmirror.utils.Constants;
import xenmirror.utils.Logger;
import xenmirror.utils.SupportFunctions;

public class Tray {
    private Image icon;
    private SystemTray tray;
    private TrayIcon trayIcon;

    private BackupController controller;

    private static final String TRAY_ICON_TOOLTIP_NAME = "XenMirror";
    private static final String MANAGE_BACKUPS_BUTTON_NAME = "Manage backups";
    private static final String SETTINGS_BUTTON_NAME = "Settings";
    private static final String EXIT_BUTTON_NAME = "Exit";
    private static final String WELCOME_MESSAGE = "App started!";
    private static final String EXIT_MESSAGE = "Goodbye!";
    private static final boolean IMAGE_IS_AUTOSIZED = true;

    public Tray(BackupController controller) {
        this.controller = controller;

        init();
    }

    private void init() {
        Logger.printApplicationLog("Tray init", "Tray");

        icon = SupportFunctions.getAppIcon();

        JPopupMenu trayMenu = new JPopupMenu();
        addItemsToTrayMenu(trayMenu);

        tray = SystemTray.getSystemTray();

        trayIcon = new TrayIcon(icon, TRAY_ICON_TOOLTIP_NAME);
        trayIcon.setImageAutoSize(IMAGE_IS_AUTOSIZED);

        setTrayIconListener(trayIcon, trayMenu);
    }

    private void addItemsToTrayMenu(JPopupMenu trayMenu) {
        Logger.printApplicationLog("Create tray items", "Tray");

        JMenuItem manageBackups = new JMenuItem(MANAGE_BACKUPS_BUTTON_NAME);
        manageBackups.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageBackups();
            }
        });
        trayMenu.add(manageBackups);

        JMenuItem settings = new JMenuItem(SETTINGS_BUTTON_NAME);
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings();
            }
        });
        trayMenu.add(settings);

        JMenuItem item = new JMenuItem(EXIT_BUTTON_NAME);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeExit();
            }
        });
        trayMenu.add(item);
    }

    private void manageBackups() {
        Logger.printApplicationLog("Manage backups", "Tray");

        ManageWorkspacesWindow manageWorkspacesWindow = new ManageWorkspacesWindow(controller);
        manageWorkspacesWindow.showWindow();
    }

    private void settings() {
        Logger.printApplicationLog("Settings", "Tray");

        OptionsWindow optionsWindow = new OptionsWindow();
        optionsWindow.showWindow();
    }

    private void makeExit() {
        Logger.printApplicationLog("Make exit", "Tray");

        trayIcon.displayMessage(Constants.getAppName(), EXIT_MESSAGE, TrayIcon.MessageType.INFO);
        SupportFunctions.correctExit();
    }

    private void setTrayIconListener(TrayIcon trayIco, JPopupMenu trayMenu) {
        trayIcon.addMouseListener(new MouseAdapter() {
            private volatile boolean isVisible = Constants.getBoolDefault();

            @Override
            public synchronized void mousePressed(MouseEvent e) {
                Logger.printApplicationLog("Tray click", "Tray");

                isVisible = !isVisible;

                trayMenu.setLocation(e.getLocationOnScreen());
                trayMenu.setVisible(isVisible);

                if (isVisible) {
                    Thread waitDelayAndInvisibleTray = createTrayHiderThread(trayMenu);

                    waitDelayAndInvisibleTray.start();
                }
            }
        });
    }

    private Thread createTrayHiderThread(JPopupMenu trayMenu) {
        Thread waitDelayAndInvisibleTray = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Long delay = Long.parseLong(Config.getConfig().getTrayShowTimeMs());
                    Thread.sleep(delay);

                    trayMenu.setVisible(Constants.getBoolDefault());
                } catch (InterruptedException e) {
                    Logger.printApplicationLog("Tray hide wait error", "Tray");
                    Logger.printApplicationLog(e.getMessage(), "Tray");
                } catch (NumberFormatException e) {
                    Logger.printApplicationLog("Bad tray delay", "Tray");
                    Logger.printApplicationLog(e.getMessage(), "Tray");
                }
            }
        });

        return waitDelayAndInvisibleTray;
    }

    public void show() {
        Logger.printApplicationLog("Show tray", "Tray");

        try {
            if (trayIcon != null) {
                tray.add(trayIcon);
            }
        } catch (AWTException e) {
            Logger.printApplicationLog("show tray error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        trayIcon.displayMessage(Constants.getAppName(), WELCOME_MESSAGE, TrayIcon.MessageType.INFO);
    }
}

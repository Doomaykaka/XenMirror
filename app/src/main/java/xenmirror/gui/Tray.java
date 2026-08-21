package xenmirror.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import xenmirror.controllers.BackupController;
import xenmirror.models.Workspace;
import xenmirror.utils.Config;
import xenmirror.utils.Constants;
import xenmirror.utils.Logger;
import xenmirror.utils.SupportFunctions;

public class Tray {
    private Image icon;
    private SystemTray tray;
    private TrayIcon trayIcon;

    private BackupController controller;

    private static final String TRAY_ICON_TOOLTIP_NAME = "ShodanEye";
    private static final String BACKUP_BUTTON_NAME = "Backup";
    private static final String RESTORE_BUTTON_NAME = "Restore";
    private static final String EXIT_BUTTON_NAME = "Exit";
    private static final String WELCOME_MESSAGE = "App started!";
    private static final String EXIT_MESSAGE = "Goodbye!";
    private static final String START_BACKUP_MESSAGE = "Backup started!";
    private static final String END_BACKUP_MESSAGE = "Backup ended!";
    private static final String START_RESTORE_MESSAGE = "Restore started!";
    private static final String END_RESTORE_MESSAGE = "Restore ended!";
    private static final boolean IMAGE_IS_AUTOSIZED = true;

    public Tray(BackupController controller) {
        this.controller = controller;

        init();
    }

    private void init() {
        Logger.printApplicationLog("Tray init", "Tray");

        if (Config.getConfig().isUseLAF()) {
            FlatIntelliJLaf.setup();
            try {
                if (Config.getConfig().isUseDark()) {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } else {
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                }
            } catch (UnsupportedLookAndFeelException e) {
                Logger.printApplicationLog("GUI style setup error", "Tray");
                Logger.printApplicationLog(e.getMessage(), "Tray");
                e.printStackTrace();
            }
        }

        URL appIconUrl = Tray.class.getClassLoader().getResource(Constants.getGuiImageIconResourcePath());

        if (appIconUrl != null) {
            icon = Toolkit.getDefaultToolkit().getImage(appIconUrl);
        }

        JPopupMenu trayMenu = new JPopupMenu();
        addItemsToTrayMenu(trayMenu);

        tray = SystemTray.getSystemTray();

        if (icon != null) {
            trayIcon = new TrayIcon(icon, TRAY_ICON_TOOLTIP_NAME);
            trayIcon.setImageAutoSize(IMAGE_IS_AUTOSIZED);

            setTrayIconListener(trayIcon, trayMenu);
        }
    }

    private void addItemsToTrayMenu(JPopupMenu trayMenu) {
        Logger.printApplicationLog("Create tray items", "Tray");

        JMenuItem backupItem = new JMenuItem(BACKUP_BUTTON_NAME);
        backupItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeBackup();
            }
        });
        trayMenu.add(backupItem);

        JMenuItem restoreItem = new JMenuItem(RESTORE_BUTTON_NAME);
        restoreItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeRestore();
            }
        });
        trayMenu.add(restoreItem);

        JMenuItem item = new JMenuItem(EXIT_BUTTON_NAME);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeExit();
            }
        });
        trayMenu.add(item);
    }

    private void makeBackup() {
        Logger.printApplicationLog("Make backup", "Tray");

        trayIcon.displayMessage(Constants.getAppName(), START_BACKUP_MESSAGE, TrayIcon.MessageType.INFO);

        List<Workspace> workspaces = controller.getWorkspaces();

        for (Workspace workspace : workspaces) {
            controller.createNewBackup(workspace);
        }

        trayIcon.displayMessage(Constants.getAppName(), END_BACKUP_MESSAGE, TrayIcon.MessageType.INFO);
    }

    private void makeRestore() {
        Logger.printApplicationLog("Make restore", "Tray");

        trayIcon.displayMessage(Constants.getAppName(), START_RESTORE_MESSAGE, TrayIcon.MessageType.INFO);

        List<Workspace> workspaces = controller.getWorkspaces();

        for (Workspace workspace : workspaces) {
            controller.restore(workspace);
        }

        trayIcon.displayMessage(Constants.getAppName(), END_RESTORE_MESSAGE, TrayIcon.MessageType.INFO);
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
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    Logger.printApplicationLog("Bad tray delay", "Tray");
                    Logger.printApplicationLog(e.getMessage(), "Tray");
                    e.printStackTrace();
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
            e.printStackTrace();
        }

        trayIcon.displayMessage(Constants.getAppName(), WELCOME_MESSAGE, TrayIcon.MessageType.INFO);
    }
}

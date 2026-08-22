package xenmirror.gui;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import xenmirror.utils.Config;
import xenmirror.utils.SupportFunctions;

public class OptionsWindow extends JFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 475;
    private static final String WINDOW_TITLE = "Settings";

    public OptionsWindow() {
        boolean isResizable = false;

        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(isResizable);

        setIconImage(SupportFunctions.getAppIcon());

        JPanel windowLayer = new JPanel();
        windowLayer.setLayout(new BoxLayout(windowLayer, BoxLayout.Y_AXIS));

        List<JComponent> options = fillWindowOptions(windowLayer);
        List<JButton> controls = fillWindowControls(windowLayer);

        add(windowLayer);

        readOptionsStateAndSetInGUI(options);

        addButtonsActionListeners(controls, options);
    }

    public void showWindow() {
        setVisible(true);
    }

    private List<JComponent> fillWindowOptions(JPanel windowLayer) {
        List<JComponent> options = new ArrayList<>();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JPanel chkLogApp = SupportFunctions.getEntityWindowCheckbox("Log app");
        JPanel chkUseLaf = SupportFunctions.getEntityWindowCheckbox("Use Laf");
        JPanel chkUseDark = SupportFunctions.getEntityWindowCheckbox("Use Dark theme");
        JPanel backupFolderPathInput = SupportFunctions.getEntityWindowJTextfield("Backup folder path");
        JButton backupFolderPathChooserInput = new JButton("Backup folder path");
        JPanel filesCheckDelayMsInput = SupportFunctions.getEntityWindowJTextfield("Files check delay");
        JPanel trayShowTimeMs = SupportFunctions.getEntityWindowJTextfield("Tray show time ms");
        JPanel useRussian = SupportFunctions.getEntityWindowCheckbox("Use Russian language");

        addChooseBackupFolderActionListener(backupFolderPathInput, backupFolderPathChooserInput);

        int gap = 15;
        SupportFunctions.addChildPanelWithGap(panel, chkLogApp, gap);
        SupportFunctions.addChildPanelWithGap(panel, chkUseLaf, gap);
        SupportFunctions.addChildPanelWithGap(panel, chkUseDark, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupFolderPathInput, gap);
        SupportFunctions.addChildWithGap(panel, backupFolderPathChooserInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, filesCheckDelayMsInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, trayShowTimeMs, gap);
        SupportFunctions.addChildPanelWithGap(panel, useRussian, gap);

        options.add(chkLogApp);
        options.add(chkUseLaf);
        options.add(chkUseDark);
        options.add(backupFolderPathInput);
        options.add(filesCheckDelayMsInput);
        options.add(trayShowTimeMs);
        options.add(useRussian);

        windowLayer.add(panel);

        return options;
    }

    private void addChooseBackupFolderActionListener(
            JPanel backupFolderPathInput, JButton backupFolderPathChooserInput) {
        ActionListener listener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "Backup folder path":
                    File backupFolder = SupportFunctions.chooseFolder();

                    SupportFunctions.setEntityWindowJTextfieldValue(
                            backupFolderPathInput, backupFolder.getAbsolutePath());

                    break;
            }
        };

        backupFolderPathChooserInput.addActionListener(listener);
    }

    private List<JButton> fillWindowControls(JPanel windowLayer) {
        List<JButton> controls = new ArrayList<>();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JButton btnSaveOptions = new JButton("Save");
        JButton btnCancelOptions = new JButton("Cancel");

        int gap = 15;
        SupportFunctions.addButtonWithGap(panel, btnSaveOptions, gap);
        SupportFunctions.addButtonWithGap(panel, btnCancelOptions, gap);

        controls.add(btnSaveOptions);
        controls.add(btnCancelOptions);

        windowLayer.add(panel);

        return controls;
    }

    private void readOptionsStateAndSetInGUI(List<JComponent> options) {
        Config appConfig = Config.getConfig();

        JPanel logAppPanel = (JPanel) options.get(0);
        JPanel useLafPanel = (JPanel) options.get(1);
        JPanel useDarkPanel = (JPanel) options.get(2);
        JPanel backupFolderPath = (JPanel) options.get(3);
        JPanel filesCheckDelayMs = (JPanel) options.get(4);
        JPanel trayShowTimeMs = (JPanel) options.get(5);
        JPanel useRussian = (JPanel) options.get(6);

        SupportFunctions.setEntityWindowCheckboxValue(logAppPanel, appConfig.isLogApp());
        SupportFunctions.setEntityWindowCheckboxValue(useLafPanel, appConfig.isUseLAF());
        SupportFunctions.setEntityWindowCheckboxValue(useDarkPanel, appConfig.isUseDark());
        SupportFunctions.setEntityWindowJTextfieldValue(
                backupFolderPath, Config.getConfig().getBackupsFolderPath());
        SupportFunctions.setEntityWindowJTextfieldValue(
                filesCheckDelayMs, Config.getConfig().getFilesCheckDelayMs());
        SupportFunctions.setEntityWindowJTextfieldValue(
                trayShowTimeMs, Config.getConfig().getTrayShowTimeMs());
        SupportFunctions.setEntityWindowCheckboxValue(
                useRussian, Config.getConfig().isUseRussianLanguage());
    }

    private void addButtonsActionListeners(List<JButton> buttons, List<JComponent> options) {
        ActionListener listener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "Save":
                    SupportFunctions.showMessage("Options saved");
                    setupOptionsFromGUI(options);
                    this.dispose();

                    break;
                case "Cancel":
                    SupportFunctions.showMessage("Cancel");
                    this.dispose();

                    break;
            }
        };

        for (JButton button : buttons) {
            button.addActionListener(listener);
        }
    }

    private void setupOptionsFromGUI(List<JComponent> options) {
        Config appConfig = Config.getConfig();

        JPanel logAppPanel = (JPanel) options.get(0);
        JPanel useLafPanel = (JPanel) options.get(1);
        JPanel useDarkPanel = (JPanel) options.get(2);
        JPanel backupFolderPathPanel = (JPanel) options.get(3);
        JPanel filesCheckDelayMsPanel = (JPanel) options.get(4);
        JPanel trayShowTimeMsPanel = (JPanel) options.get(5);
        JPanel useRussianPanel = (JPanel) options.get(6);

        boolean logApp = SupportFunctions.getEntityWindowCheckboxValue(logAppPanel);
        boolean useLaf = SupportFunctions.getEntityWindowCheckboxValue(useLafPanel);
        boolean useDark = SupportFunctions.getEntityWindowCheckboxValue(useDarkPanel);
        String backupFolderPath = SupportFunctions.getEntityWindowJTextfieldValue(backupFolderPathPanel);
        String filesCheckDelayMs = SupportFunctions.getEntityWindowJTextfieldValue(filesCheckDelayMsPanel);
        String trayShowTimeMs = SupportFunctions.getEntityWindowJTextfieldValue(trayShowTimeMsPanel);
        boolean useRussian = SupportFunctions.getEntityWindowCheckboxValue(useRussianPanel);

        appConfig.setLogApp(logApp);
        appConfig.setUseLAF(useLaf);
        appConfig.setUseDark(useDark);
        appConfig.setBackupsFolderPath(backupFolderPath);
        appConfig.setFilesCheckDelayMs(filesCheckDelayMs);
        appConfig.setTrayShowTimeMs(trayShowTimeMs);
        appConfig.setUseRussianLanguage(useRussian);

        try {
            appConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

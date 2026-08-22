package xenmirror.gui;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import xenmirror.models.Workspace;
import xenmirror.utils.Config;
import xenmirror.utils.Constants;
import xenmirror.utils.SupportFunctions;

public class CreateEditWorkspaceWindow extends JFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 675;
    private static final String WINDOW_TITLE_CREATE = "Create workspace";
    private static final String WINDOW_TITLE_EDIT = "Edit workspace";

    private Workspace workspaceToEdit;

    public CreateEditWorkspaceWindow(Workspace workspaceToEdit) {
        boolean isResizable = false;

        setTitle(WINDOW_TITLE_EDIT);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(isResizable);

        setIconImage(SupportFunctions.getAppIcon());

        this.workspaceToEdit = workspaceToEdit;

        JPanel windowLayer = new JPanel();
        windowLayer.setLayout(new BoxLayout(windowLayer, BoxLayout.Y_AXIS));

        List<JComponent> fields = fillWindowFields(windowLayer);
        List<JButton> controls = fillWindowControls(windowLayer);

        add(windowLayer);

        readFieldsStateAndSetInGUI(fields);

        addButtonsActionListeners(controls, fields);
    }

    public CreateEditWorkspaceWindow() {
        boolean isResizable = false;

        setTitle(WINDOW_TITLE_CREATE);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(isResizable);

        setIconImage(SupportFunctions.getAppIcon());

        JPanel windowLayer = new JPanel();
        windowLayer.setLayout(new BoxLayout(windowLayer, BoxLayout.Y_AXIS));

        List<JComponent> fields = fillWindowFields(windowLayer);
        List<JButton> controls = fillWindowControls(windowLayer);

        add(windowLayer);

        addButtonsActionListeners(controls, fields);
    }

    public void showWindow() {
        setVisible(true);
    }

    private List<JComponent> fillWindowFields(JPanel windowLayer) {
        List<JComponent> fields = new ArrayList<>();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JPanel backupDateDiffInput = SupportFunctions.getEntityWindowJTextfield("Backup date diff");
        JPanel backupInArchiveInput = SupportFunctions.getEntityWindowCheckbox("Backup in archive");
        JPanel backupPasswordInput = SupportFunctions.getEntityWindowJTextfield("Backup password");
        JPanel backupPostfixInput = SupportFunctions.getEntityWindowJTextfield("Backup name postfix");
        JPanel backupPrefixInput = SupportFunctions.getEntityWindowJTextfield("Backup name prefix");
        JPanel backupUseTimestampInput = SupportFunctions.getEntityWindowJTextfield("Backup use timestamp");
        JPanel backupUseVersionInput = SupportFunctions.getEntityWindowJTextfield("Backup use version");
        JPanel backupUseManualStrategyInput = SupportFunctions.getEntityWindowCheckbox("Backup use manual strategy");
        JPanel backupUseTimeStrategyInput = SupportFunctions.getEntityWindowCheckbox("Backup use time strategy");
        JPanel backupUseChangeStrategyInput = SupportFunctions.getEntityWindowCheckbox("Backup use change strategy");
        JPanel backupFoldersInput = SupportFunctions.getEntityWindowJTextfield("Backup folders paths");
        JButton backupFoldersChooserInput = new JButton("Backup folders paths");
        JPanel backupFilesInput = SupportFunctions.getEntityWindowJTextfield("Backup folders paths");
        JButton backupFilesChooserInput = new JButton("Backup files paths");
        JPanel backupRotateAfterInput = SupportFunctions.getEntityWindowJTextfield("Backup rotate after N times");

        addChooseFoldersActionListener(backupFoldersInput, backupFoldersChooserInput);
        addChooseFilesActionListener(backupRotateAfterInput, backupFilesChooserInput);

        int gap = 15;
        SupportFunctions.addChildPanelWithGap(panel, backupDateDiffInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupInArchiveInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupPasswordInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupPostfixInput, gap);
        SupportFunctions.addChildWithGap(panel, backupPrefixInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupUseTimestampInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupUseVersionInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupUseManualStrategyInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupUseTimeStrategyInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupUseChangeStrategyInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupFoldersInput, gap);
        SupportFunctions.addChildWithGap(panel, backupFoldersChooserInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupFilesInput, gap);
        SupportFunctions.addChildWithGap(panel, backupFilesChooserInput, gap);
        SupportFunctions.addChildPanelWithGap(panel, backupRotateAfterInput, gap);

        fields.add(backupDateDiffInput);
        fields.add(backupInArchiveInput);
        fields.add(backupPasswordInput);
        fields.add(backupPostfixInput);
        fields.add(backupPrefixInput);
        fields.add(backupUseTimestampInput);
        fields.add(backupUseVersionInput);
        fields.add(backupUseManualStrategyInput);
        fields.add(backupUseTimeStrategyInput);
        fields.add(backupUseChangeStrategyInput);
        fields.add(backupFoldersInput);
        fields.add(backupFoldersChooserInput);
        fields.add(backupFilesInput);
        fields.add(backupFilesChooserInput);
        fields.add(backupRotateAfterInput);

        windowLayer.add(panel);

        return fields;
    }

    private void addChooseFoldersActionListener(JPanel backupFoldersInput, JButton backupFoldersChooserInput) {
        ActionListener listener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "Backup folders paths":
                    File[] folders = SupportFunctions.chooseFolders();

                    String[] paths = new String[folders.length];

                    for (int i = 0; i < folders.length; i++) {
                        paths[i] = folders[i].getAbsolutePath();
                    }

                    String foldersRepresentation = String.join(Constants.getListSeparator(), paths);

                    SupportFunctions.setEntityWindowJTextfieldValue(backupFoldersInput, foldersRepresentation);

                    break;
            }
        };

        backupFoldersChooserInput.addActionListener(listener);
    }

    private void addChooseFilesActionListener(JPanel backupFilesInput, JButton backupFilesChooserInput) {
        ActionListener listener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "Backup files paths":
                    File[] files = SupportFunctions.chooseFiles();

                    String[] paths = new String[files.length];

                    for (int i = 0; i < files.length; i++) {
                        paths[i] = files[i].getAbsolutePath();
                    }

                    String filesRepresentation = String.join(Constants.getListSeparator(), paths);

                    SupportFunctions.setEntityWindowJTextfieldValue(backupFilesInput, filesRepresentation);

                    break;
            }
        };

        backupFilesChooserInput.addActionListener(listener);
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

    private void readFieldsStateAndSetInGUI(List<JComponent> options) {
        JPanel logAppPanel = (JPanel) options.get(0);
        JPanel useLafPanel = (JPanel) options.get(1);
        JPanel useDarkPanel = (JPanel) options.get(2);
        JPanel backupFolderPath = (JPanel) options.get(3);
        JPanel filesCheckDelayMs = (JPanel) options.get(4);
        JPanel trayShowTimeMs = (JPanel) options.get(5);
        JPanel useRussian = (JPanel) options.get(6);

        //        SupportFunctions.setEntityWindowCheckboxValue(logAppPanel, appConfig.isLogApp());
        //        SupportFunctions.setEntityWindowCheckboxValue(useLafPanel, appConfig.isUseLAF());
        //        SupportFunctions.setEntityWindowCheckboxValue(useDarkPanel, appConfig.isUseDark());
        //        SupportFunctions.setEntityWindowJTextfieldValue(
        //                backupFolderPath, Config.getConfig().getBackupsFolderPath());
        //        SupportFunctions.setEntityWindowJTextfieldValue(
        //                filesCheckDelayMs, Config.getConfig().getFilesCheckDelayMs());
        //        SupportFunctions.setEntityWindowJTextfieldValue(
        //                trayShowTimeMs, Config.getConfig().getTrayShowTimeMs());
        //        SupportFunctions.setEntityWindowCheckboxValue(
        //                useRussian, Config.getConfig().isUseRussianLanguage());
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

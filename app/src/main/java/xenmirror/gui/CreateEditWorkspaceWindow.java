package xenmirror.gui;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import xenmirror.controllers.BackupController;
import xenmirror.models.BackupStrategyType;
import xenmirror.models.Workspace;
import xenmirror.models.WorkspaceDescriptor;
import xenmirror.utils.Constants;
import xenmirror.utils.SupportFunctions;

public class CreateEditWorkspaceWindow extends JFrame {
    private static BackupController backupController;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 875;
    private static final String WINDOW_TITLE_CREATE = "Create workspace";
    private static final String WINDOW_TITLE_EDIT = "Edit workspace";

    private Workspace workspaceToEdit;
    private File[] files;
    private File[] folders;

    private Runnable onClose;

    public CreateEditWorkspaceWindow(Workspace workspaceToEdit, BackupController backupController) {
        boolean isResizable = false;

        setTitle(WINDOW_TITLE_EDIT);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(isResizable);

        setIconImage(SupportFunctions.getAppIcon());

        this.workspaceToEdit = workspaceToEdit;
        this.backupController = backupController;

        JPanel windowLayer = new JPanel();
        windowLayer.setLayout(new BoxLayout(windowLayer, BoxLayout.Y_AXIS));

        List<JComponent> fields = fillWindowFields(windowLayer);
        List<JButton> controls = fillWindowControls(windowLayer);

        add(windowLayer);

        readFieldsStateAndSetInGUI(fields);

        addButtonsActionListeners(controls, fields);
    }

    public CreateEditWorkspaceWindow(BackupController backupController) {
        boolean isResizable = false;

        setTitle(WINDOW_TITLE_CREATE);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(isResizable);

        setIconImage(SupportFunctions.getAppIcon());

        this.backupController = backupController;

        JPanel windowLayer = new JPanel();
        windowLayer.setLayout(new BoxLayout(windowLayer, BoxLayout.Y_AXIS));

        List<JComponent> fields = fillWindowFields(windowLayer);

        SupportFunctions.setEntityWindowJTextfieldValue((JPanel) fields.get(1), Constants.getDefaultDateValue());

        List<JButton> controls = fillWindowControls(windowLayer);

        add(windowLayer);

        addButtonsActionListeners(controls, fields);
    }

    public void showWindow(Runnable onClose) {
        this.onClose = onClose;

        setVisible(true);
    }

    private List<JComponent> fillWindowFields(JPanel windowLayer) {
        List<JComponent> fields = new ArrayList<>();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JPanel backupNameInput = SupportFunctions.getEntityWindowJTextfield("Backup name");
        JPanel backupDateDiffInput = SupportFunctions.getEntityWindowJTextfield("Backup date diff");
        JPanel backupInArchiveInput = SupportFunctions.getEntityWindowCheckbox("Backup in archive");
        JPanel backupPasswordInput = SupportFunctions.getEntityWindowJTextfield("Backup password");
        JPanel backupPostfixInput = SupportFunctions.getEntityWindowJTextfield("Backup name postfix");
        JPanel backupPrefixInput = SupportFunctions.getEntityWindowJTextfield("Backup name prefix");
        JPanel backupUseTimestampInput = SupportFunctions.getEntityWindowCheckbox("Backup use timestamp");
        JPanel backupUseVersionInput = SupportFunctions.getEntityWindowCheckbox("Backup use version");
        JPanel backupUseManualStrategyInput = SupportFunctions.getEntityWindowCheckbox("Backup use manual strategy");
        JPanel backupUseTimeStrategyInput = SupportFunctions.getEntityWindowCheckbox("Backup use time strategy");
        JPanel backupUseChangeStrategyInput = SupportFunctions.getEntityWindowCheckbox("Backup use change strategy");
        JPanel backupFoldersInput = SupportFunctions.getEntityWindowJTextfield("Backup folders paths");
        JButton backupFoldersChooserInput = new JButton("Backup folders paths");
        JPanel backupFilesInput = SupportFunctions.getEntityWindowJTextfield("Backup files paths");
        JButton backupFilesChooserInput = new JButton("Backup files paths");
        JPanel backupRotateAfterInput = SupportFunctions.getEntityWindowJTextfield("Backup rotate after N times");

        addChooseFoldersActionListener(backupFoldersInput, backupFoldersChooserInput);
        addChooseFilesActionListener(backupFilesInput, backupFilesChooserInput);

        int gap = 15;
        SupportFunctions.addChildPanelWithGap(panel, backupNameInput, gap);
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

        fields.add(backupNameInput);
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
                    folders = SupportFunctions.chooseFolders();

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
                    files = SupportFunctions.chooseFiles();

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

        JButton btnCreateWorkspace = new JButton("Create");
        JButton btnEditWorkspace = new JButton("Edit");
        JButton btnCancelWorkspace = new JButton("Cancel");

        int gap = 15;

        if (workspaceToEdit == null) {
            SupportFunctions.addButtonWithGap(panel, btnCreateWorkspace, gap);

            controls.add(btnCreateWorkspace);
        } else {
            SupportFunctions.addButtonWithGap(panel, btnEditWorkspace, gap);

            controls.add(btnEditWorkspace);
        }

        SupportFunctions.addButtonWithGap(panel, btnCancelWorkspace, gap);

        controls.add(btnCancelWorkspace);

        windowLayer.add(panel);

        return controls;
    }

    private void readFieldsStateAndSetInGUI(List<JComponent> fields) {
        JPanel backupNameInput = (JPanel) fields.get(0);
        JPanel backupDateDiffInput = (JPanel) fields.get(1);
        JPanel backupInArchiveInput = (JPanel) fields.get(2);
        JPanel backupPasswordInput = (JPanel) fields.get(3);
        JPanel backupPostfixInput = (JPanel) fields.get(4);
        JPanel backupPrefixInput = (JPanel) fields.get(5);
        JPanel backupUseTimestampInput = (JPanel) fields.get(6);
        JPanel backupUseVersionInput = (JPanel) fields.get(7);
        JPanel backupUseManualStrategyInput = (JPanel) fields.get(8);
        JPanel backupUseTimeStrategyInput = (JPanel) fields.get(9);
        JPanel backupUseChangeStrategyInput = (JPanel) fields.get(10);
        JPanel backupFoldersInput = (JPanel) fields.get(11);
        JPanel backupFilesInput = (JPanel) fields.get(13);
        JPanel backupRotateAfterInput = (JPanel) fields.get(15);

        boolean useManual = workspaceToEdit
                .getDescriptor()
                .getBackupsStrategyTypes()
                .contains(BackupStrategyType.MANUAL.toString());
        boolean useTime = workspaceToEdit
                .getDescriptor()
                .getBackupsStrategyTypes()
                .contains(BackupStrategyType.BY_TIME.toString());
        boolean useChange = workspaceToEdit
                .getDescriptor()
                .getBackupsStrategyTypes()
                .contains(BackupStrategyType.ON_CHANGE.toString());
        List<File> foldersValue = workspaceToEdit.getDescriptor().getFoldersToBackup();
        List<String> foldersPaths =
                foldersValue.stream().map(File::getAbsolutePath).toList();
        String foldersPathsValue = String.join(Constants.getListSeparator(), foldersPaths);
        List<File> filesValue = workspaceToEdit.getDescriptor().getFilesToBackup();
        List<String> filesPaths = filesValue.stream().map(File::getAbsolutePath).toList();
        String filesPathsValue = String.join(Constants.getListSeparator(), filesPaths);

        SupportFunctions.setEntityWindowJTextfieldValue(backupNameInput, workspaceToEdit.getName());
        SupportFunctions.setEntityWindowJTextfieldValue(
                backupDateDiffInput, workspaceToEdit.getDescriptor().getBackupDateDiff());
        SupportFunctions.setEntityWindowCheckboxValue(
                backupInArchiveInput, workspaceToEdit.getDescriptor().isBackupInArchive());
        SupportFunctions.setEntityWindowJTextfieldValue(
                backupPasswordInput, workspaceToEdit.getDescriptor().getBackupPassword());
        SupportFunctions.setEntityWindowJTextfieldValue(
                backupPostfixInput, workspaceToEdit.getDescriptor().getBackupPostfix());
        SupportFunctions.setEntityWindowJTextfieldValue(
                backupPrefixInput, workspaceToEdit.getDescriptor().getBackupPreffix());
        SupportFunctions.setEntityWindowCheckboxValue(
                backupUseTimestampInput, workspaceToEdit.getDescriptor().isBackupUseTimestamps());
        SupportFunctions.setEntityWindowCheckboxValue(
                backupUseVersionInput, workspaceToEdit.getDescriptor().isBackupUseVersion());
        SupportFunctions.setEntityWindowCheckboxValue(backupUseManualStrategyInput, useManual);
        SupportFunctions.setEntityWindowCheckboxValue(backupUseTimeStrategyInput, useTime);
        SupportFunctions.setEntityWindowCheckboxValue(backupUseChangeStrategyInput, useChange);
        SupportFunctions.setEntityWindowJTextfieldValue(backupFoldersInput, foldersPathsValue);
        SupportFunctions.setEntityWindowJTextfieldValue(backupFilesInput, filesPathsValue);
        SupportFunctions.setEntityWindowJTextfieldValue(
                backupRotateAfterInput,
                Integer.toString(workspaceToEdit.getDescriptor().getRotateAfter()));
    }

    private void addButtonsActionListeners(List<JButton> buttons, List<JComponent> fields) {
        ActionListener listener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "Create":
                    SupportFunctions.showMessage("Workspace created");
                    createWorkspaceFromGUI(fields);
                    this.dispose();

                    break;
                case "Edit":
                    SupportFunctions.showMessage("Workspace edited");
                    editWorkspaceFromGUI(fields);
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

    private void createWorkspaceFromGUI(List<JComponent> fields) {
        JPanel backupNameInput = (JPanel) fields.get(0);
        JPanel backupDateDiffInput = (JPanel) fields.get(1);
        JPanel backupInArchiveInput = (JPanel) fields.get(2);
        JPanel backupPasswordInput = (JPanel) fields.get(3);
        JPanel backupPostfixInput = (JPanel) fields.get(4);
        JPanel backupPrefixInput = (JPanel) fields.get(5);
        JPanel backupUseTimestampInput = (JPanel) fields.get(6);
        JPanel backupUseVersionInput = (JPanel) fields.get(7);
        JPanel backupUseManualStrategyInput = (JPanel) fields.get(8);
        JPanel backupUseTimeStrategyInput = (JPanel) fields.get(9);
        JPanel backupUseChangeStrategyInput = (JPanel) fields.get(10);
        JPanel backupRotateAfterInput = (JPanel) fields.get(15);

        String name = SupportFunctions.getEntityWindowJTextfieldValue(backupNameInput);
        String dateDiff = SupportFunctions.getEntityWindowJTextfieldValue(backupDateDiffInput);
        boolean inArchive = SupportFunctions.getEntityWindowCheckboxValue(backupInArchiveInput);
        String password = SupportFunctions.getEntityWindowJTextfieldValue(backupPasswordInput);
        String postfix = SupportFunctions.getEntityWindowJTextfieldValue(backupPostfixInput);
        String prefix = SupportFunctions.getEntityWindowJTextfieldValue(backupPrefixInput);
        boolean useTimestamp = SupportFunctions.getEntityWindowCheckboxValue(backupUseTimestampInput);
        boolean useVersion = SupportFunctions.getEntityWindowCheckboxValue(backupUseVersionInput);
        boolean useManualStrategy = SupportFunctions.getEntityWindowCheckboxValue(backupUseManualStrategyInput);
        boolean useTimeStrategy = SupportFunctions.getEntityWindowCheckboxValue(backupUseTimeStrategyInput);
        boolean useChangeStrategy = SupportFunctions.getEntityWindowCheckboxValue(backupUseChangeStrategyInput);
        String rotateAfter = SupportFunctions.getEntityWindowJTextfieldValue(backupRotateAfterInput);

        List<String> strategies = new ArrayList<>();

        if (useManualStrategy) {
            strategies.add(BackupStrategyType.MANUAL.toString());
        }

        if (useTimeStrategy) {
            strategies.add(BackupStrategyType.BY_TIME.toString());
        }

        if (useChangeStrategy) {
            strategies.add(BackupStrategyType.ON_CHANGE.toString());
        }

        String strategyTypesValue = String.join(Constants.getListSeparator(), strategies);

        int rotateAfterValue = 0;

        try {
            rotateAfterValue = Integer.parseInt(rotateAfter);
        } catch (NumberFormatException e) {
            ;
        }

        if (folders == null) {
            folders = new File[] {};
        }

        if (files == null) {
            files = new File[] {};
        }

        if (name == null || (!useManualStrategy && !useTimeStrategy && !useChangeStrategy)) {
            SupportFunctions.showMessage("Workspace bad data");
            return;
        }

        Workspace newWorkspace = new Workspace(name);
        WorkspaceDescriptor newWorkspaceDescriptor = new WorkspaceDescriptor(
                password,
                dateDiff,
                useTimestamp,
                useVersion,
                prefix,
                postfix,
                inArchive,
                List.of(folders),
                List.of(files),
                strategyTypesValue,
                newWorkspace,
                rotateAfterValue);

        newWorkspace.setDescriptor(newWorkspaceDescriptor);

        this.backupController.createNewWorkspace(newWorkspace);

        if (this.onClose != null) {
            this.onClose.run();
        }
    }

    private void editWorkspaceFromGUI(List<JComponent> fields) {
        JPanel backupDateDiffInput = (JPanel) fields.get(1);
        JPanel backupInArchiveInput = (JPanel) fields.get(2);
        JPanel backupPasswordInput = (JPanel) fields.get(3);
        JPanel backupPostfixInput = (JPanel) fields.get(4);
        JPanel backupPrefixInput = (JPanel) fields.get(5);
        JPanel backupUseTimestampInput = (JPanel) fields.get(6);
        JPanel backupUseVersionInput = (JPanel) fields.get(7);
        JPanel backupUseManualStrategyInput = (JPanel) fields.get(8);
        JPanel backupUseTimeStrategyInput = (JPanel) fields.get(9);
        JPanel backupUseChangeStrategyInput = (JPanel) fields.get(10);
        JPanel backupRotateAfterInput = (JPanel) fields.get(15);

        String dateDiff = SupportFunctions.getEntityWindowJTextfieldValue(backupDateDiffInput);
        boolean inArchive = SupportFunctions.getEntityWindowCheckboxValue(backupInArchiveInput);
        String password = SupportFunctions.getEntityWindowJTextfieldValue(backupPasswordInput);
        String postfix = SupportFunctions.getEntityWindowJTextfieldValue(backupPostfixInput);
        String prefix = SupportFunctions.getEntityWindowJTextfieldValue(backupPrefixInput);
        boolean useTimestamp = SupportFunctions.getEntityWindowCheckboxValue(backupUseTimestampInput);
        boolean useVersion = SupportFunctions.getEntityWindowCheckboxValue(backupUseVersionInput);
        boolean useManualStrategy = SupportFunctions.getEntityWindowCheckboxValue(backupUseManualStrategyInput);
        boolean useTimeStrategy = SupportFunctions.getEntityWindowCheckboxValue(backupUseTimeStrategyInput);
        boolean useChangeStrategy = SupportFunctions.getEntityWindowCheckboxValue(backupUseChangeStrategyInput);
        String rotateAfter = SupportFunctions.getEntityWindowJTextfieldValue(backupRotateAfterInput);

        List<String> strategies = new ArrayList<>();

        if (useManualStrategy) {
            strategies.add(BackupStrategyType.MANUAL.toString());
        }

        if (useTimeStrategy) {
            strategies.add(BackupStrategyType.BY_TIME.toString());
        }

        if (useChangeStrategy) {
            strategies.add(BackupStrategyType.ON_CHANGE.toString());
        }

        String strategyTypesValue = String.join(Constants.getListSeparator(), strategies);

        int rotateAfterValue = 0;

        try {
            rotateAfterValue = Integer.parseInt(rotateAfter);
        } catch (NumberFormatException e) {
            ;
        }

        if (!useManualStrategy && !useTimeStrategy && !useChangeStrategy) {
            SupportFunctions.showMessage("Workspace bad data");
            return;
        }

        WorkspaceDescriptor workspaceDescriptorToEdit = workspaceToEdit.getDescriptor();
        workspaceDescriptorToEdit.setBackupPassword(password);
        workspaceDescriptorToEdit.setBackupDateDiff(dateDiff);
        workspaceDescriptorToEdit.setBackupUseTimestamps(useTimestamp);
        workspaceDescriptorToEdit.setBackupUseVersion(useVersion);
        workspaceDescriptorToEdit.setBackupPreffix(prefix);
        workspaceDescriptorToEdit.setBackupPostfix(postfix);
        workspaceDescriptorToEdit.setBackupInArchive(inArchive);
        workspaceDescriptorToEdit.setFoldersToBackup(List.of(folders));
        workspaceDescriptorToEdit.setFilesToBackup(List.of(files));
        workspaceDescriptorToEdit.setBackupsStrategyTypes(strategyTypesValue);
        workspaceDescriptorToEdit.setRotateAfter(rotateAfterValue);

        this.backupController.editWorkspace(workspaceToEdit);

        if (this.onClose != null) {
            this.onClose.run();
        }
    }
}

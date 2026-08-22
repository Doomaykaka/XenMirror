package xenmirror.dao;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import xenmirror.models.Backup;
import xenmirror.models.BackupDescriptor;
import xenmirror.models.BackupStrategy;
import xenmirror.models.Workspace;
import xenmirror.utils.Config;
import xenmirror.utils.Constants;
import xenmirror.utils.Logger;
import xenmirror.utils.SupportFunctions;

public class BackupDAO {
    private BackupStrategy strategy;
    private Workspace workspace;

    public BackupDAO(BackupStrategy strategy, Workspace workspace) {
        this.strategy = strategy;
        this.workspace = workspace;
    }

    public Backup getBackup(String name) {
        Logger.printApplicationLog("Get backup by name", "BackupDAO");

        Backup result = null;

        File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());

        if (!backupsFolder.exists()) {
            backupsFolder.mkdir();
        }

        File workspaceFolder = new File(backupsFolder, this.workspace.getName());

        if (!workspaceFolder.exists()) {
            workspaceFolder.mkdir();
        }

        for (File backupFolder : workspaceFolder.listFiles()) {
            if (backupFolder.getName().equals(name)) {
                result = SupportFunctions.parseBackupFolder(backupFolder, this.workspace);

                if (result == null) {
                    continue;
                }

                readAndConnectBackupDescriptor(backupFolder, result);

                break;
            }
        }

        return result;
    }

    public Backup getBackup(Instant date) {
        Logger.printApplicationLog("Get backup by date", "BackupDAO");

        Backup result = null;

        File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());

        if (!backupsFolder.exists()) {
            backupsFolder.mkdir();
        }

        File workspaceFolder = new File(backupsFolder, this.workspace.getName());

        if (!workspaceFolder.exists()) {
            workspaceFolder.mkdir();
        }

        for (File backupFolder : workspaceFolder.listFiles()) {
            Backup parsed = SupportFunctions.parseBackupFolder(backupFolder, this.workspace);

            if (parsed == null) {
                continue;
            }

            readAndConnectBackupDescriptor(backupFolder, parsed);

            if (parsed.getDescriptor().getChangeStampAsInstant().equals(date)) {
                result = parsed;
                break;
            }
        }

        return result;
    }

    public Backup getBackup(long version) {
        Logger.printApplicationLog("Get backup by version", "BackupDAO");

        Backup result = null;

        File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());

        if (!backupsFolder.exists()) {
            backupsFolder.mkdir();
        }

        File workspaceFolder = new File(backupsFolder, this.workspace.getName());

        if (!workspaceFolder.exists()) {
            workspaceFolder.mkdir();
        }

        for (File backupFolder : workspaceFolder.listFiles()) {
            Backup parsed = SupportFunctions.parseBackupFolder(backupFolder, this.workspace);

            if (parsed == null) {
                continue;
            }

            readAndConnectBackupDescriptor(backupFolder, parsed);

            if (parsed.getDescriptor().getVersion() == version) {
                result = parsed;
                break;
            }
        }

        return result;
    }

    public List<Backup> getAll() {
        Logger.printApplicationLog("Get all backups", "BackupDAO");

        List<Backup> result = new ArrayList<Backup>();

        File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());

        if (!backupsFolder.exists()) {
            backupsFolder.mkdir();
        }

        File workspaceFolder = new File(backupsFolder, this.workspace.getName());

        if (!workspaceFolder.exists()) {
            workspaceFolder.mkdir();
        }

        for (File backupFolder : workspaceFolder.listFiles()) {
            if (backupFolder.getName().equals(Constants.getConfigFilename())) {
                continue;
            }

            try {
                Backup parsed = SupportFunctions.parseBackupFolder(backupFolder, this.workspace);

                if (parsed == null) {
                    continue;
                }

                readAndConnectBackupDescriptor(backupFolder, parsed);

                result.add(parsed);
            } catch (NullPointerException e) {
                ;
            }
        }

        return result;
    }

    public boolean saveBackup(Backup lastBackup) {
        Logger.printApplicationLog("Save backup", "BackupDAO");

        boolean saveIsCorrect = Constants.getBoolDefault();

        updateWorkspace();

        BackupDescriptor lastBackupDescriptor = null;

        if (lastBackup != null) {
            lastBackupDescriptor = lastBackup.getDescriptor();
        }

        try {
            saveIsCorrect = strategy.startBackupProcess(workspace, lastBackupDescriptor) != null;
        } catch (IOException e) {
            Logger.printApplicationLog("backup process error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
            e.printStackTrace();
        }

        return saveIsCorrect;
    }

    public synchronized boolean removeBackup(Backup backupToRemove) {
        Logger.printApplicationLog("Remove backup", "BackupDAO");

        boolean backupIsRemoved = Constants.getBoolDefault();

        updateWorkspace();

        File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());

        if (!backupsFolder.exists()) {
            backupsFolder.mkdir();
        }

        File workspaceFolder = new File(backupsFolder, this.workspace.getName());

        if (!workspaceFolder.exists()) {
            workspaceFolder.mkdir();
        }

        File currentBackupFolder = null;

        for (File backupFolder : workspaceFolder.listFiles()) {
            if (!backupFolder.isDirectory()) {
                continue;
            }

            Backup parsed = SupportFunctions.parseBackupFolder(backupFolder, this.workspace);

            if (parsed == null) {
                continue;
            }

            readAndConnectBackupDescriptor(backupFolder, parsed);

            if (parsed.getDescriptor().getVersion()
                    == backupToRemove.getDescriptor().getVersion()) {
                currentBackupFolder = backupFolder;
                break;
            }
        }

        if (currentBackupFolder == null) {
            return backupIsRemoved;
        }

        SupportFunctions.clearFolder(currentBackupFolder);

        backupIsRemoved = currentBackupFolder.delete();

        return backupIsRemoved;
    }

    private void readAndConnectBackupDescriptor(File backupFolder, Backup backup) {
        File backupDescriptor =
                new File(backupFolder, backupFolder.getName() + Constants.getBackupDescriptionFileExt());

        boolean backupIsCorrupted = backupDescriptor.getParentFile().listFiles().length == 1;

        if (backupDescriptor == null || !backupDescriptor.exists() || backupIsCorrupted) {
            return;
        }

        BackupDescriptor descriptor = SupportFunctions.readBackupDescriptor(backupDescriptor);

        backup.setDescriptor(descriptor);
        descriptor.setBackup(backup);
    }

    private void updateWorkspace() {
        Logger.printApplicationLog("Workspace update", "BackupDAO");

        workspace.setBackups(getAll());
    }
}

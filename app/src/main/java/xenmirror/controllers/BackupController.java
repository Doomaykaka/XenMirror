package xenmirror.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import xenmirror.App;
import xenmirror.dao.BackupDAO;
import xenmirror.models.Backup;
import xenmirror.models.BackupDescriptor;
import xenmirror.models.BackupStrategy;
import xenmirror.models.BackupStrategyType;
import xenmirror.models.Workspace;
import xenmirror.models.WorkspaceDescriptor;
import xenmirror.utils.Config;
import xenmirror.utils.Constants;
import xenmirror.utils.Logger;
import xenmirror.utils.SupportFunctions;

public class BackupController {
    private List<Workspace> workspaces;
    private Map<Workspace, BackupDAO> workspacesDAO;

    public BackupController(List<Workspace> managedWorkspaces) {
        this.workspaces = managedWorkspaces;

        init();
        runBackupCheckerLoop();
    }

    public void init() {
        Logger.printApplicationLog("BackupController init", "BackupController");

        this.workspacesDAO = new HashMap<Workspace, BackupDAO>();

        for (int workspaceIdx = Constants.getStartIndex(); workspaceIdx < this.workspaces.size(); workspaceIdx++) {
            Workspace workspace = this.workspaces.get(workspaceIdx);
            prepareWorkspaceWithDao(workspace);
        }
    }

    private void prepareWorkspaceWithDao(Workspace workspace) {
        WorkspaceDescriptor workspaceDescriptor = null;
        try {
            workspaceDescriptor = SupportFunctions.parseWorkspaceDescriptor(workspace, Config.getConfig());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<BackupStrategyType> backupStartegyTypes =
                SupportFunctions.parseBackupStrategyTypes(workspaceDescriptor.getBackupsStrategyTypes());

        BackupStrategy strategy = new BackupStrategy(backupStartegyTypes);
        BackupDAO newDAO = new BackupDAO(strategy, workspace);
        this.workspacesDAO.put(workspace, newDAO);

        updateWorkspace(workspace);
    }

    private void runBackupCheckerLoop() {
        Logger.printApplicationLog("Run backup checker loop", "BackupController");

        BackupCheckerTask checkerTask = new BackupCheckerTask();
        Thread checkerThread = new Thread(checkerTask);
        checkerThread.start();
    }

    public void createNewBackup(Workspace backupWorkspace) {
        Logger.printApplicationLog("Create new backup", "BackupController");

        updateWorkspace(backupWorkspace);

        BackupDAO dao = workspacesDAO.get(backupWorkspace);

        List<Backup> allBackups = dao.getAll();

        if (allBackups.isEmpty() || SupportFunctions.getLastWorkspaceBackup(backupWorkspace) == null) {
            createFirstBackupWithDescriptor(backupWorkspace, dao);
        } else {
            createLastBackupWithDescriptor(backupWorkspace, dao);
        }
    }

    private void createFirstBackupWithDescriptor(Workspace backupWorkspace, BackupDAO dao) {
        dao.saveBackup(null);
    }

    private void createLastBackupWithDescriptor(Workspace backupWorkspace, BackupDAO dao) {
        WorkspaceDescriptor workspaceDescriptor = null;

        Backup backupToSave = null;

        try {
            workspaceDescriptor = SupportFunctions.parseWorkspaceDescriptor(backupWorkspace, Config.getConfig());
        } catch (IOException e) {
            Logger.printApplicationLog("backup creating error", "BackupController");
            Logger.printApplicationLog(e.getMessage(), "BackupController");
            e.printStackTrace();
        }

        List<String> filesToBackupRerpr = SupportFunctions.listRepresentationToList(
                SupportFunctions.listToReprepsentationsList(workspaceDescriptor.getFilesToBackup()));
        List<String> foldersToBackupRerpr = SupportFunctions.listRepresentationToList(
                SupportFunctions.listToReprepsentationsList(workspaceDescriptor.getFoldersToBackup()));
        List<File> filesToBackup = SupportFunctions.listOfPathsToListOfFiles(filesToBackupRerpr);
        List<File> foldersToBackup = SupportFunctions.listOfPathsToListOfFiles(foldersToBackupRerpr);

        boolean isSecured = workspaceDescriptor.getBackupPassword() != null
                && !workspaceDescriptor.getBackupPassword().isEmpty();

        Backup lastBackup = SupportFunctions.getLastWorkspaceBackup(backupWorkspace);

        boolean needDataCheck = !Constants.getBoolDefault();

        BackupDescriptor descriptor = new BackupDescriptor(foldersToBackup, filesToBackup, isSecured, needDataCheck);

        Backup newBackup = new Backup(null, descriptor);
        newBackup.setWorkspace(backupWorkspace);
        backupWorkspace.addBackup(newBackup);
        descriptor.setBackup(backupToSave);

        dao.saveBackup(lastBackup);
    }

    public void restore(Workspace backupWorkspace) {
        Logger.printApplicationLog("Restore data from backup", "BackupController");

        updateWorkspace(backupWorkspace);

        Backup backup = SupportFunctions.getLastWorkspaceBackup(backupWorkspace);

        WorkspaceDescriptor workspaceDescriptor = null;

        try {
            workspaceDescriptor = SupportFunctions.parseWorkspaceDescriptor(backupWorkspace, Config.getConfig());
        } catch (IOException e) {
            Logger.printApplicationLog("backup restoring error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
            e.printStackTrace();
        }

        List<String> backupFilePaths = backup.getDescriptor().getFilePaths();
        List<String> backupFolderPaths = backup.getDescriptor().getFoldersPaths();

        List<File> backupFiles = SupportFunctions.listOfPathsToListOfFiles(backupFilePaths);
        List<File> backupFolders = SupportFunctions.listOfPathsToListOfFiles(backupFolderPaths);

        for (File file : backupFiles) {
            SupportFunctions.removeFilesAndFolders(file);
        }

        for (File folder : backupFolders) {
            SupportFunctions.removeFilesAndFolders(folder);
        }

        if (workspaceDescriptor.isBackupInArchive()) {
            restoreBackupFromArchive(backup, backupFiles, backupFolders, workspaceDescriptor);
        } else {
            restoreBackupFromFolder(backup, backupFiles, backupFolders);
        }
    }

    private void restoreBackupFromArchive(
            Backup backup, List<File> backupFiles, List<File> backupFolders, WorkspaceDescriptor workspaceDescriptor) {
        File backupZipFile = backup.getData();

        if (backupZipFile == null || !backupZipFile.exists() || backupZipFile.isDirectory()) {
            Logger.printApplicationLog("cant restore backup, bad data zip file", "BackupController");

            return;
        }

        List<File> allData = new ArrayList<>();
        allData.addAll(backupFiles);
        allData.addAll(backupFolders);
        File relativePath = SupportFunctions.getRelativePath(allData);

        String password = workspaceDescriptor.getBackupPassword();

        SupportFunctions.unzipFilesAndFoldersFromArchive(relativePath, backupZipFile, password);
    }

    private void restoreBackupFromFolder(Backup backup, List<File> backupFiles, List<File> backupFolders) {
        File backupDataFolder = backup.getData();

        if (backupDataFolder == null || !backupDataFolder.exists() || !backupDataFolder.isDirectory()) {
            Logger.printApplicationLog("cant restore backup, bad data folder", "BackupController");

            return;
        }

        List<File> allData = new ArrayList<File>();
        allData.addAll(backupFiles);
        allData.addAll(backupFolders);
        File relativePath = SupportFunctions.getRelativePath(allData);

        for (File element : backupDataFolder.listFiles()) {
            SupportFunctions.copyFilesAndFolders(element, relativePath);
        }
    }

    private void updateWorkspace(Workspace workspaceToUpdate) {
        Logger.printApplicationLog("Workspace update", "BackupDAO");

        workspaceToUpdate.setBackups(workspacesDAO.get(workspaceToUpdate).getAll());
    }

    private class BackupCheckerTask implements Runnable {

        @Override
        public void run() {
            Logger.printApplicationLog("Backup checker loop started", "BackupController");

            Thread currentThread = Thread.currentThread();

            tryStartBackup(currentThread);

            for (Workspace workspace : workspaces) {
                Runnable watcherTask = new Runnable() {
                    @Override
                    public void run() {
                        while (!App.isClosed()) {
                            try {
                                createNewBackup(workspace);

                                Long delay = Long.parseLong(Config.getConfig().getFilesCheckDelayMs());
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                Logger.printApplicationLog("waiting error", "BackupController");
                                Logger.printApplicationLog(e.getMessage(), "BackupController");
                                e.printStackTrace();
                            } catch (NumberFormatException e) {
                                Logger.printApplicationLog("Bad backup checker delay", "Tray");
                                Logger.printApplicationLog(e.getMessage(), "Tray");
                                e.printStackTrace();
                            }
                        }
                    }
                };

                Thread watcherThread = new Thread(watcherTask);
                watcherThread.start();
            }
        }

        private void tryStartBackup(Thread currentThread) {
            List<Entry<Workspace, BackupDAO>> daos = List.copyOf(workspacesDAO.entrySet());

            for (Entry<Workspace, BackupDAO> workspaceDAOEntry : daos) {
                createNewBackup(workspaceDAOEntry.getKey());
            }
        }
    }

    public List<Workspace> getWorkspaces() {
        return workspaces;
    }
}

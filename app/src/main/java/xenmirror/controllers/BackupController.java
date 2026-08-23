package xenmirror.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
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

    public void runBackupCheckerLoop() {
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

        rotateBackups(backupWorkspace, allBackups, dao);
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

    private void rotateBackups(Workspace workspaceToRotate, List<Backup> workspaceBackups, BackupDAO dao) {
        WorkspaceDescriptor descriptor = workspaceToRotate.getDescriptor();

        int maxBackups = descriptor.getRotateAfter();

        if (maxBackups == 0) {
            return;
        }

        if (workspaceBackups.size() > maxBackups) {
            workspaceBackups.sort(Comparator.comparing(
                    bd -> bd.getDescriptor() != null ? bd.getDescriptor().getChangeStampAsInstant() : Instant.now()));

            for (int i = 0; i < workspaceBackups.size() - maxBackups; i++) {
                dao.removeBackup(workspaceBackups.get(i));
            }
        }
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
                                WorkspaceDescriptor descriptor = workspace.getDescriptor();

                                boolean needBackupCheck = descriptor
                                                .getBackupsStrategyTypes()
                                                .contains(BackupStrategyType.BY_TIME.toString())
                                        || descriptor
                                                .getBackupsStrategyTypes()
                                                .contains(BackupStrategyType.ON_CHANGE.toString());

                                if (needBackupCheck) {
                                    createNewBackup(workspace);
                                }

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

    public void removeWorkspace(Workspace workspaceToRemove) {
        if (workspaces.remove(workspaceToRemove)) {
            File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());
            File workspaceFolder = new File(backupsFolder, workspaceToRemove.getName());

            if (!workspaceFolder.exists()) {
                return;
            }

            SupportFunctions.clearFolder(workspaceFolder);
            workspaceFolder.delete();
        }
    }

    public void createNewWorkspace(Workspace workspaceToCreate) {
        File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());
        File workspaceFolder = new File(backupsFolder, workspaceToCreate.getName());

        if (!workspaceFolder.exists()) {
            workspaceFolder.mkdir();
        }

        Path pathToDescriptor = Path.of(workspaceFolder
                .toPath()
                .resolve(Constants.getConfigFilename())
                .toFile()
                .getAbsolutePath());

        File descriptorFile = pathToDescriptor.toFile();

        try {
            if (descriptorFile.createNewFile()) {
                writeWorkspaceDescriptor(descriptorFile, workspaceToCreate);

                workspaces.add(workspaceToCreate);
            }
        } catch (IOException e) {
            Logger.printApplicationLog("cant create workspace folder", "BackupController");
            e.printStackTrace();
        }
    }

    private void writeWorkspaceDescriptor(File descriptorFile, Workspace workspaceToCreate) {
        try (FileOutputStream descriptorFOS = new FileOutputStream(descriptorFile.getAbsolutePath())) {
            OutputStreamWriter writer =
                    new OutputStreamWriter(descriptorFOS, Config.getConfig().getSystemEncoding());
            Properties properties = new Properties();

            List<File> files = workspaceToCreate.getDescriptor().getFilesToBackup();
            List<File> folders = workspaceToCreate.getDescriptor().getFoldersToBackup();
            List<String> filesPaths =
                    files.stream().map(file -> file.getAbsolutePath()).toList();
            List<String> foldersPaths =
                    folders.stream().map(folder -> folder.getAbsolutePath()).toList();
            String filesPathsValue = String.join(Constants.getListSeparator(), filesPaths);
            String foldersPathsValue = String.join(Constants.getListSeparator(), foldersPaths);

            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupDateDiff(),
                    workspaceToCreate.getDescriptor().getBackupDateDiff());
            SupportFunctions.setBooleanProperty(
                    properties,
                    Constants.getPropertyNameBackupInArchive(),
                    workspaceToCreate.getDescriptor().isBackupInArchive());
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupPassword(),
                    workspaceToCreate.getDescriptor().getBackupPassword());
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupPostfix(),
                    workspaceToCreate.getDescriptor().getBackupPostfix());
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupPreffix(),
                    workspaceToCreate.getDescriptor().getBackupPreffix());
            SupportFunctions.setBooleanProperty(
                    properties,
                    Constants.getPropertyNameBackupUseTimestamp(),
                    workspaceToCreate.getDescriptor().isBackupUseTimestamps());
            SupportFunctions.setBooleanProperty(
                    properties,
                    Constants.getPropertyNameBackupUseVersion(),
                    workspaceToCreate.getDescriptor().isBackupUseVersion());
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupsStrategyTypes(),
                    workspaceToCreate.getDescriptor().getBackupsStrategyTypes());
            SupportFunctions.setStringProperty(properties, Constants.getPropertyNameFilesToBackup(), filesPathsValue);
            SupportFunctions.setStringProperty(
                    properties, Constants.getPropertyNameFoldersToBackup(), foldersPathsValue);
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameRotateAfterCount(),
                    Integer.toString(workspaceToCreate.getDescriptor().getRotateAfter()));

            properties.store(writer, Constants.getTextDefault());
            descriptorFOS.flush();
        } catch (SecurityException | IOException e) {
            Logger.printApplicationLog("Failed to save workspace descriptor file", "BackupController");
            e.printStackTrace();
        }
    }

    public void editWorkspace(Workspace workspaceToEdit) {
        File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());
        File workspaceFolder = new File(backupsFolder, workspaceToEdit.getName());

        if (!workspaceFolder.exists()) {
            workspaceFolder.mkdir();
        }

        Path pathToDescriptor = Path.of(workspaceFolder
                .toPath()
                .resolve(Constants.getConfigFilename())
                .toFile()
                .getAbsolutePath());

        File descriptorFile = pathToDescriptor.toFile();

        updateWorkspaceDescriptor(descriptorFile, workspaceToEdit);
    }

    private void updateWorkspaceDescriptor(File descriptorFile, Workspace workspaceToUpdate) {
        try (FileOutputStream descriptorFOS = new FileOutputStream(descriptorFile.getAbsolutePath())) {
            OutputStreamWriter writer =
                    new OutputStreamWriter(descriptorFOS, Config.getConfig().getSystemEncoding());
            Properties properties = new Properties();

            List<File> files = workspaceToUpdate.getDescriptor().getFilesToBackup();
            List<File> folders = workspaceToUpdate.getDescriptor().getFoldersToBackup();
            List<String> filesPaths =
                    files.stream().map(file -> file.getAbsolutePath()).toList();
            List<String> foldersPaths =
                    folders.stream().map(folder -> folder.getAbsolutePath()).toList();
            String filesPathsValue = String.join(Constants.getListSeparator(), filesPaths);
            String foldersPathsValue = String.join(Constants.getListSeparator(), foldersPaths);

            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupDateDiff(),
                    workspaceToUpdate.getDescriptor().getBackupDateDiff());
            SupportFunctions.setBooleanProperty(
                    properties,
                    Constants.getPropertyNameBackupInArchive(),
                    workspaceToUpdate.getDescriptor().isBackupInArchive());
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupPassword(),
                    workspaceToUpdate.getDescriptor().getBackupPassword());
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupPostfix(),
                    workspaceToUpdate.getDescriptor().getBackupPostfix());
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupPreffix(),
                    workspaceToUpdate.getDescriptor().getBackupPreffix());
            SupportFunctions.setBooleanProperty(
                    properties,
                    Constants.getPropertyNameBackupUseTimestamp(),
                    workspaceToUpdate.getDescriptor().isBackupUseTimestamps());
            SupportFunctions.setBooleanProperty(
                    properties,
                    Constants.getPropertyNameBackupUseVersion(),
                    workspaceToUpdate.getDescriptor().isBackupUseVersion());
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameBackupsStrategyTypes(),
                    workspaceToUpdate.getDescriptor().getBackupsStrategyTypes());
            SupportFunctions.setStringProperty(properties, Constants.getPropertyNameFilesToBackup(), filesPathsValue);
            SupportFunctions.setStringProperty(
                    properties, Constants.getPropertyNameFoldersToBackup(), foldersPathsValue);
            SupportFunctions.setStringProperty(
                    properties,
                    Constants.getPropertyNameRotateAfterCount(),
                    Integer.toString(workspaceToUpdate.getDescriptor().getRotateAfter()));

            properties.store(writer, Constants.getTextDefault());
            descriptorFOS.flush();
        } catch (SecurityException | IOException e) {
            Logger.printApplicationLog("Failed to update workspace descriptor file", "BackupController");
            e.printStackTrace();
        }
    }

    public void removeBackup(Backup backupToRemove, Workspace parentWorkspace) {
        if (parentWorkspace.getBackups().remove(backupToRemove)) {
            File backupFolder = backupToRemove.getData();

            if (!backupFolder.exists()) {
                return;
            }

            SupportFunctions.clearFolder(backupFolder);
            backupFolder.delete();
        }
    }

    public void restoreToBackup(Workspace workspace, Backup backupToRestore) {
        List<String> backupFilePaths = backupToRestore.getDescriptor().getFilePaths();
        List<String> backupFolderPaths = backupToRestore.getDescriptor().getFoldersPaths();

        List<File> backupFiles = SupportFunctions.listOfPathsToListOfFiles(backupFilePaths);
        List<File> backupFolders = SupportFunctions.listOfPathsToListOfFiles(backupFolderPaths);

        for (File file : backupFiles) {
            SupportFunctions.removeFilesAndFolders(file);
        }

        for (File folder : backupFolders) {
            SupportFunctions.removeFilesAndFolders(folder);
        }

        if (workspace.getDescriptor().isBackupInArchive()) {
            restoreBackupFromArchive(backupToRestore, backupFiles, backupFolders, workspace.getDescriptor());
        } else {
            restoreBackupFromFolder(backupToRestore, backupFiles, backupFolders);
        }
    }
}

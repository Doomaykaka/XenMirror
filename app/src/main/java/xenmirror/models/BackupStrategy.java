package xenmirror.models;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import xenmirror.utils.Config;
import xenmirror.utils.Constants;
import xenmirror.utils.Logger;
import xenmirror.utils.SupportFunctions;

public class BackupStrategy {
    private Set<BackupStrategyType> strategyTypes;

    public BackupStrategy(Set<BackupStrategyType> backupStrategyTypes) {
        strategyTypes = new HashSet<BackupStrategyType>();
        strategyTypes.addAll(backupStrategyTypes);
    }

    public Backup startBackupProcess(Workspace workspace, BackupDescriptor lastBackupDescriptor) throws IOException {
        Logger.printApplicationLog("Start backup process", "BackupStrategy");

        Backup backup = null;

        BackupDescriptor generatedNewBackupDescriptor = generateNewBackupDescriptor(workspace.getDescriptor());

        if (lastBackupDescriptor != null
                && lastBackupDescriptor.getChangeStamp() != null
                && !checkThatBackupNeeded(workspace, lastBackupDescriptor, generatedNewBackupDescriptor)) {
            return backup;
        }

        File backupArchive = packData(generatedNewBackupDescriptor, workspace);
        backup = new Backup(backupArchive, generatedNewBackupDescriptor);

        return backup;
    }

    private boolean checkThatBackupNeeded(
            Workspace workspace, BackupDescriptor lastBackupDescriptor, BackupDescriptor generatedNewBackupDescriptor)
            throws IOException {
        Logger.printApplicationLog("Check that backup needed", "BackupStrategy");

        boolean backupNeeded = Constants.getBoolDefault();

        if (lastBackupDescriptor == null) {
            backupNeeded = !Constants.getBoolDefault();

            return backupNeeded;
        }

        if (strategyTypes.contains(BackupStrategyType.BY_TIME)
                && strategyTypes.contains(BackupStrategyType.ON_CHANGE)) {
            backupNeeded = checkThatBackupNeededByTimeAndOnChange(
                    generatedNewBackupDescriptor, workspace, lastBackupDescriptor);

            return backupNeeded;
        }

        if (strategyTypes.contains(BackupStrategyType.BY_TIME)) {
            backupNeeded = checkThatBackupNeededByTime(workspace, lastBackupDescriptor);

            return backupNeeded;
        }

        if (strategyTypes.contains(BackupStrategyType.ON_CHANGE)) {
            if (lastBackupDescriptor.getChangeStampAsInstant() == null) {
                backupNeeded = true;

                return backupNeeded;
            }

            Instant lastBackupDescriptorStamp = lastBackupDescriptor.getChangeStampAsInstant();
            Instant generatedNewBackupDescriptorStamp = generatedNewBackupDescriptor.getChangeStampAsInstant();

            backupNeeded = lastBackupDescriptorStamp.isBefore(generatedNewBackupDescriptorStamp);

            return backupNeeded;
        }

        if (strategyTypes.contains(BackupStrategyType.MANUAL)) {
            backupNeeded = !Constants.getBoolDefault();

            return backupNeeded;
        }

        return backupNeeded;
    }

    private boolean checkThatBackupNeededByTimeAndOnChange(
            BackupDescriptor backupDescriptor, Workspace workspace, BackupDescriptor lastBackupDescriptor)
            throws IOException {
        boolean backupNeeded = Constants.getBoolDefault();

        Workspace backupWorkspace = workspace;
        WorkspaceDescriptor workspaceDescriptor =
                SupportFunctions.parseWorkspaceDescriptor(backupWorkspace, Config.getConfig());

        Instant lastBackupDate = lastBackupDescriptor.getChangeStampAsInstant();
        Instant current = Instant.now();
        long dateDiffSecs = current.getEpochSecond() - lastBackupDate.getEpochSecond();

        long ddTime = SupportFunctions.dateDiffStringToMilliseconds(workspaceDescriptor.getBackupDateDiff());

        backupNeeded = dateDiffSecs >= ddTime
                && lastBackupDescriptor.getChangeStampAsInstant().isBefore(backupDescriptor.getChangeStampAsInstant());

        return backupNeeded;
    }

    private boolean checkThatBackupNeededByTime(Workspace workspace, BackupDescriptor lastBackupDescriptor)
            throws IOException {
        boolean backupNeeded = Constants.getBoolDefault();

        Workspace backupWorkspace = workspace;
        WorkspaceDescriptor workspaceDescriptor =
                SupportFunctions.parseWorkspaceDescriptor(backupWorkspace, Config.getConfig());

        Instant lastBackupDate = lastBackupDescriptor.getChangeStampAsInstant();
        Instant current = Instant.now();
        long dateDiffSecs = current.getEpochSecond() - lastBackupDate.getEpochSecond();

        long ddTime = SupportFunctions.dateDiffStringToMilliseconds(workspaceDescriptor.getBackupDateDiff()) / 1000;

        backupNeeded = dateDiffSecs >= ddTime;

        return backupNeeded;
    }

    private BackupDescriptor generateNewBackupDescriptor(WorkspaceDescriptor workspaceDescriptor) {
        Logger.printApplicationLog("Generate new backup descriptor", "BackupStrategy");

        BackupDescriptor result = null;

        List<File> files = new ArrayList<File>();
        List<File> folders = new ArrayList<File>();

        for (File file : workspaceDescriptor.getFilesToBackup()) {
            files.add(file);
        }

        for (File folder : workspaceDescriptor.getFoldersToBackup()) {
            folders.add(folder);
        }

        boolean needDataCheck = !Constants.getBoolDefault();

        boolean backupIsSecured =
                workspaceDescriptor.isBackupInArchive() && workspaceDescriptor.getBackupPassword() != null;

        result = new BackupDescriptor(folders, files, backupIsSecured, needDataCheck);
        long newVersion = result.getVersion() + 1;
        result.setVersion(newVersion);
        result.setChangeStamp(result.getCurrentChangeStamp(files, folders).toString());

        return result;
    }

    private File packData(BackupDescriptor backupDescriptor, Workspace workspace) {
        Logger.printApplicationLog("Start pack backup data", "BackupStrategy");

        File result = null;

        WorkspaceDescriptor workspaceDescriptor = null;
        try {
            workspaceDescriptor = SupportFunctions.parseWorkspaceDescriptor(workspace, Config.getConfig());
        } catch (IOException e) {
            Logger.printApplicationLog("getting workspace error", "BackupStrategy");
            Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
            e.printStackTrace();
        }

        result = prepareBackupStructure(backupDescriptor, workspace, workspaceDescriptor);

        return result;
    }

    private File prepareBackupStructure(
            BackupDescriptor backupDescriptor, Workspace workspace, WorkspaceDescriptor workspaceDescriptor) {
        File backup = null;

        if (workspaceDescriptor == null) {
            return backup;
        }

        List<String> filesToBackup = SupportFunctions.listRepresentationToList(
                SupportFunctions.listToReprepsentationsList(workspaceDescriptor.getFilesToBackup()));
        List<String> foldersToBackup = SupportFunctions.listRepresentationToList(
                SupportFunctions.listToReprepsentationsList(workspaceDescriptor.getFoldersToBackup()));

        File backupsFolder = new File(Config.getConfig().getBackupsFolderPath());

        if (!backupsFolder.exists()) {
            backupsFolder.mkdirs();
        }

        File workspaceFolder = new File(backupsFolder, workspace.getName());

        if (!workspaceFolder.exists()) {
            workspaceFolder.mkdir();
        }

        String backupName = null;
        try {
            backupName = getBackupName(backupDescriptor, workspace);
        } catch (IOException e) {
            Logger.printApplicationLog("getting backup name error", "BackupStrategy");
            Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
            e.printStackTrace();
        }

        String backupFolderName = backupName
                .replaceAll(Constants.getSpaceRegexp(), Constants.getFsSeparator())
                .replaceAll(Constants.getListSeparator(), Constants.getFsSeparator())
                .replaceAll(Constants.getTimeSeparator(), Constants.getFsSeparator());
        File currentBackupFolder = new File(workspaceFolder, backupFolderName);

        if (!currentBackupFolder.exists()) {
            try {
                currentBackupFolder.mkdir();
            } catch (SecurityException e) {
                System.out.println(e.getMessage());
                Logger.printApplicationLog("backup folder creating error", "BackupStrategy");
                Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
                e.printStackTrace();
            }
        }

        backup = prepareBackupContent(
                backupDescriptor,
                workspace,
                workspaceDescriptor,
                currentBackupFolder,
                backupFolderName,
                filesToBackup,
                foldersToBackup);

        return backup;
    }

    private File prepareBackupContent(
            BackupDescriptor backupDescriptor,
            Workspace workspace,
            WorkspaceDescriptor workspaceDescriptor,
            File currentBackupFolder,
            String backupFolderName,
            List<String> filesToBackup,
            List<String> foldersToBackup) {
        File backup = null;

        if (workspaceDescriptor.isBackupInArchive()) {
            File backupZipFile = new File(currentBackupFolder, backupFolderName + Constants.getBackupArchiveFileExt());

            if (!backupZipFile.exists()) {
                try {
                    backupZipFile.createNewFile();
                } catch (IOException e) {
                    Logger.printApplicationLog("archive file creating error", "BackupStrategy");
                    Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
                    e.printStackTrace();
                }
            }

            backup = backupZipFile;
        } else {
            File backupFolder = new File(currentBackupFolder, backupFolderName);

            if (!backupFolder.exists()) {
                try {
                    backupFolder.mkdir();
                } catch (SecurityException e) {
                    Logger.printApplicationLog("backup folder creating error", "BackupStrategy");
                    Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
                    e.printStackTrace();
                }
            }

            backup = backupFolder;
        }

        File backupDescriptorFile =
                new File(currentBackupFolder, backupFolderName + Constants.getBackupDescriptionFileExt());

        if (!backupDescriptorFile.exists()) {
            try {
                backupDescriptorFile.createNewFile();
            } catch (IOException e) {
                Logger.printApplicationLog("descriptor file creating error", "BackupStrategy");
                Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
                e.printStackTrace();
            }
        }

        SupportFunctions.writeFilesAndFolders(filesToBackup, foldersToBackup, backup, backupDescriptor, workspace);
        SupportFunctions.writeBackupDescriptor(backupDescriptor, backupDescriptorFile, workspace);

        checkThatBackupIsCorrect(workspaceDescriptor, backup);

        return backup;
    }

    private void checkThatBackupIsCorrect(WorkspaceDescriptor workspaceDescriptor, File backup) {
        if (workspaceDescriptor.isBackupInArchive()
                && (workspaceDescriptor.getBackupPassword() == null
                        || workspaceDescriptor.getBackupPassword().isEmpty())) {
            try (ZipFile zip = new ZipFile(backup)) {
                if (zip.size() == Constants.getIntDefault()) {
                    throw new ZipException("zip generation error");
                }
            } catch (ZipException e) {
                Logger.printApplicationLog("zip file processing error", "BackupStrategy");
                Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
                e.printStackTrace();
            } catch (IOException e) {
                Logger.printApplicationLog("zip file opening error", "BackupStrategy");
                Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
                e.printStackTrace();
            }
        } else if (workspaceDescriptor.isBackupInArchive()
                && workspaceDescriptor.getBackupPassword() != null
                && !workspaceDescriptor.getBackupPassword().isEmpty()) {
            try {
                if (!backup.exists() || backup.isDirectory()) {
                    throw new IOException("zip generation error");
                }
            } catch (IOException e) {
                Logger.printApplicationLog("zip file check error", "BackupStrategy");
                Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
                e.printStackTrace();
            }
        } else {
            try {
                if (!backup.exists() || !backup.isDirectory()) {
                    throw new IOException("backup folder generation error");
                }
            } catch (IOException e) {
                Logger.printApplicationLog("backup folder opening error", "BackupStrategy");
                Logger.printApplicationLog(e.getMessage(), "BackupStrategy");
                e.printStackTrace();
            }
        }
    }

    public String getBackupName(BackupDescriptor backupDescriptor, Workspace workspace) throws IOException {
        Logger.printApplicationLog("Getting backup name", "BackupStrategy");

        Workspace backupWorkspace = workspace;
        WorkspaceDescriptor workspaceConfig =
                SupportFunctions.parseWorkspaceDescriptor(backupWorkspace, Config.getConfig());

        String backupName = Constants.getTextDefault();

        String preffix = workspaceConfig.getBackupPreffix();

        if (preffix != null && !preffix.isEmpty()) {
            backupName += preffix;
        }

        String date = Instant.now().toString();

        if (date != null && !date.isEmpty() && !backupName.isEmpty()) {
            backupName += Constants.getBackupNamePartsSeparator();
        }

        if (date != null && !date.isEmpty()) {
            backupName += date;
        }

        String version = Long.toString(backupDescriptor.getVersion());

        if (version != null && !version.isEmpty() && !backupName.isEmpty()) {
            backupName += Constants.getBackupNamePartsSeparator();
        }

        if (version != null && !version.isEmpty()) {
            backupName += version;
        }

        String postfix = workspaceConfig.getBackupPostfix();

        if (postfix != null && !postfix.isEmpty() && !backupName.isEmpty()) {
            backupName += Constants.getBackupNamePartsSeparator();
        }

        if (postfix != null && !postfix.isEmpty()) {
            backupName += postfix;
        }

        return backupName;
    }
}

package xenmirror.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import xenmirror.utils.Constants;
import xenmirror.utils.Logger;

public class BackupDescriptor implements Cloneable {
    private static final String REPRESENTATION_PART_1 = "BackupDescriptor [filePaths=";
    private static final String REPRESENTATION_PART_2 = ", foldersPaths=";
    private static final String REPRESENTATION_PART_3 = ", changeStamp=";
    private static final String REPRESENTATION_PART_4 = ", filesCount=";
    private static final String REPRESENTATION_PART_5 = ", foldersCount=";
    private static final String REPRESENTATION_PART_6 = ", filesCountTotal=";
    private static final String REPRESENTATION_PART_7 = ", foldersCountTotal=";
    private static final String REPRESENTATION_PART_8 = ", isSecured=";
    private static final String REPRESENTATION_PART_9 = ", version=";
    private static final String REPRESENTATION_PART_10 = "]";

    private List<String> filePaths;
    private List<String> foldersPaths;
    private String changeStamp;
    private int filesCount = Constants.getIntDefault();
    private int foldersCount = Constants.getIntDefault();
    private int filesCountTotal = Constants.getIntDefault();
    private int foldersCountTotal = Constants.getIntDefault();
    private boolean isSecured = Constants.getBoolDefault();
    private long version = Constants.getIntDefault();

    private Backup backup;

    public BackupDescriptor(BackupDescriptor backupDescriptorToClone) {
        this.filePaths = List.copyOf(backupDescriptorToClone.getFilePaths());
        this.foldersPaths = List.copyOf(backupDescriptorToClone.getFilePaths());
        this.changeStamp = new String(backupDescriptorToClone.getChangeStamp());
        this.filesCount = backupDescriptorToClone.getFilesCount();
        this.foldersCount = backupDescriptorToClone.getFoldersCount();
        this.filesCountTotal = backupDescriptorToClone.getFilesCountTotal();
        this.foldersCountTotal = backupDescriptorToClone.getFilesCountTotal();
        this.isSecured = backupDescriptorToClone.isSecured();
        this.version = backupDescriptorToClone.getVersion();
    }

    public BackupDescriptor(
            List<File> foldersToBackup, List<File> filesToBackup, boolean isSecured, boolean checkData) {
        this.isSecured = isSecured;

        init(foldersToBackup, filesToBackup, checkData);
    }

    private void init(List<File> foldersToBackup, List<File> filesToBackup, boolean checkData) {
        Logger.printApplicationLog("Backup descriptor init start", "BackupDescriptor");

        saveFilesDescription(filesToBackup);
        saveFoldersDescription(foldersToBackup);

        if (checkData) {
            calculateChangeStamp(filesToBackup, foldersToBackup);
        }
    }

    private void saveFilesDescription(List<File> filesToBackup) {
        filePaths = new ArrayList<String>();

        if (filesToBackup == null) {
            return;
        }

        for (File fileToBackup : filesToBackup) {
            filePaths.add(fileToBackup.getAbsolutePath());
        }

        filesCount = filesToBackup.size();
        filesCountTotal = filesToBackup.size();
    }

    private void saveFoldersDescription(List<File> foldersToBackup) {
        foldersPaths = new ArrayList<String>();

        if (foldersToBackup == null) {
            return;
        }

        for (File folderToBackup : foldersToBackup) {
            generateFolderDescriptionPart(folderToBackup);
        }

        foldersCount = foldersToBackup.size();
    }

    private void generateFolderDescriptionPart(File folder) {
        if (folder == null) {
            return;
        }

        if (folder.listFiles() == null) {
            foldersCountTotal++;
            foldersPaths.add(folder.getAbsolutePath());

            return;
        }

        for (File folderEntry : folder.listFiles()) {
            if (folderEntry.isFile()) {
                filesCountTotal++;
                filePaths.add(folderEntry.getAbsolutePath());
            } else {
                foldersCountTotal++;
                foldersPaths.add(folderEntry.getAbsolutePath());

                generateFolderDescriptionPart(folderEntry);
            }
        }

        foldersCountTotal++;
        foldersPaths.add(folder.getAbsolutePath());
    }

    private void calculateChangeStamp(List<File> filesToBackup, List<File> foldersToBackup) {
        Instant lastChangeStamp = null;

        if (foldersToBackup != null) {
            for (File folderToBackup : foldersToBackup) {
                FileTime folderTime;
                try {
                    folderTime = Files.getLastModifiedTime(folderToBackup.toPath());
                    Instant changeStamp = folderTime.toInstant();

                    if (lastChangeStamp == null) {
                        lastChangeStamp = changeStamp;
                    } else {
                        if (lastChangeStamp.isBefore(changeStamp)) {
                            lastChangeStamp = changeStamp;
                        }
                    }
                } catch (IOException e) {
                    Logger.printApplicationLog("calculate changestep error", "BackupDescriptor");
                    Logger.printApplicationLog(e.getMessage(), "BackupDescriptor");
                }
            }
        }

        if (filesToBackup != null) {
            for (File fileToBackup : filesToBackup) {
                FileTime fileTime;
                try {
                    fileTime = Files.getLastModifiedTime(fileToBackup.toPath());
                    Instant changeStamp = fileTime.toInstant();

                    if (lastChangeStamp == null) {
                        lastChangeStamp = changeStamp;
                    } else {
                        if (lastChangeStamp.isBefore(changeStamp)) {
                            lastChangeStamp = changeStamp;
                        }
                    }
                } catch (IOException e) {
                    Logger.printApplicationLog("calculate changestep error", "BackupDescriptor");
                    Logger.printApplicationLog(e.getMessage(), "BackupDescriptor");
                }
            }
        }

        if (lastChangeStamp != null) {
            changeStamp = lastChangeStamp.toString();
        } else {
            changeStamp = null;
        }
    }

    public Instant getCurrentChangeStamp(List<File> filesToBackup, List<File> foldersToBackup) {
        Instant lastChangeStamp = null;

        if (foldersToBackup != null) {
            for (File folderToBackup : foldersToBackup) {
                FileTime folderTime;
                try {
                    folderTime = Files.getLastModifiedTime(folderToBackup.toPath());
                    Instant changeStamp = folderTime.toInstant();

                    if (lastChangeStamp == null) {
                        lastChangeStamp = changeStamp;
                    } else {
                        if (lastChangeStamp.isBefore(changeStamp)) {
                            lastChangeStamp = changeStamp;
                        }
                    }
                } catch (IOException e) {
                    Logger.printApplicationLog("get changestep error", "BackupDescriptor");
                    Logger.printApplicationLog(e.getMessage(), "BackupDescriptor");
                }
            }
        }

        if (filesToBackup != null) {
            for (File fileToBackup : filesToBackup) {
                FileTime fileTime;
                try {
                    fileTime = Files.getLastModifiedTime(fileToBackup.toPath());
                    Instant changeStamp = fileTime.toInstant();

                    if (lastChangeStamp == null) {
                        lastChangeStamp = changeStamp;
                    } else {
                        if (lastChangeStamp.isBefore(changeStamp)) {
                            lastChangeStamp = changeStamp;
                        }
                    }
                } catch (IOException e) {
                    Logger.printApplicationLog("get changestep error", "BackupDescriptor");
                    Logger.printApplicationLog(e.getMessage(), "BackupDescriptor");
                }
            }
        }

        return lastChangeStamp;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public List<String> getFoldersPaths() {
        return foldersPaths;
    }

    public String getChangeStamp() {
        return changeStamp;
    }

    public Instant getChangeStampAsInstant() {
        if (changeStamp == null) {
            return null;
        }

        return Instant.parse(changeStamp);
    }

    public int getFilesCount() {
        return filesCount;
    }

    public int getFoldersCount() {
        return foldersCount;
    }

    public int getFilesCountTotal() {
        return filesCountTotal;
    }

    public int getFoldersCountTotal() {
        return foldersCountTotal;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public long getVersion() {
        return version;
    }

    public void setChangeStamp(String changeStamp) {
        this.changeStamp = changeStamp;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }

    public void setFoldersCount(int foldersCount) {
        this.foldersCount = foldersCount;
    }

    public void setFilesCountTotal(int filesCountTotal) {
        this.filesCountTotal = filesCountTotal;
    }

    public void setFoldersCountTotal(int foldersCountTotal) {
        this.foldersCountTotal = foldersCountTotal;
    }

    public Backup getBackup() {
        return backup;
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + Objects.hash(
                        changeStamp,
                        filePaths,
                        filesCount,
                        filesCountTotal,
                        foldersCount,
                        foldersCountTotal,
                        foldersPaths,
                        isSecured,
                        version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BackupDescriptor other = (BackupDescriptor) obj;
        return Objects.equals(changeStamp, other.changeStamp)
                && Objects.equals(filePaths, other.filePaths)
                && filesCount == other.filesCount
                && filesCountTotal == other.filesCountTotal
                && foldersCount == other.foldersCount
                && foldersCountTotal == other.foldersCountTotal
                && Objects.equals(foldersPaths, other.foldersPaths)
                && isSecured == other.isSecured
                && version == other.version;
    }

    @Override
    public String toString() {
        return REPRESENTATION_PART_1
                + filePaths
                + REPRESENTATION_PART_2
                + foldersPaths
                + REPRESENTATION_PART_3
                + changeStamp
                + REPRESENTATION_PART_4
                + filesCount
                + REPRESENTATION_PART_5
                + foldersCount
                + REPRESENTATION_PART_6
                + filesCountTotal
                + REPRESENTATION_PART_7
                + foldersCountTotal
                + REPRESENTATION_PART_8
                + isSecured
                + REPRESENTATION_PART_9
                + version
                + REPRESENTATION_PART_10;
    }
}

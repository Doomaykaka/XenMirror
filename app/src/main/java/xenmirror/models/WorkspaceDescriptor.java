package xenmirror.models;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class WorkspaceDescriptor {
    private static final String REPRESENTATION_PART_1 = "WorkspaceDescriptor [backupPassword=";
    private static final String REPRESENTATION_PART_2 = ", backupDateDiff=";
    private static final String REPRESENTATION_PART_3 = ", backupUseTimestamps=";
    private static final String REPRESENTATION_PART_4 = ", backupUseVersion=";
    private static final String REPRESENTATION_PART_5 = ", backupPreffix=";
    private static final String REPRESENTATION_PART_6 = ", backupPostfix=";
    private static final String REPRESENTATION_PART_7 = ", backupInArchive=";
    private static final String REPRESENTATION_PART_8 = ", filesToBackup=";
    private static final String REPRESENTATION_PART_9 = ", foldersToBackup=";
    private static final String REPRESENTATION_PART_10 = "]";

    private String backupPassword;
    private String backupDateDiff;
    private boolean backupUseTimestamps;
    private boolean backupUseVersion;
    private String backupPreffix;
    private String backupPostfix;
    private boolean backupInArchive;
    private List<File> foldersToBackup;
    private List<File> filesToBackup;

    private String backupsStrategyTypes;

    private Workspace workspace;

    public WorkspaceDescriptor(
            String backupPassword,
            String backupDateDiff,
            boolean backupUseTimestamps,
            boolean backupUseVersion,
            String backupPreffix,
            String backupPostfix,
            boolean backupInArchive,
            List<File> foldersToBackup,
            List<File> filesToBackup,
            String backupsStrategyTypes,
            Workspace workspace) {
        this.backupPassword = backupPassword;
        this.backupDateDiff = backupDateDiff;
        this.backupUseTimestamps = backupUseTimestamps;
        this.backupUseVersion = backupUseVersion;
        this.backupPreffix = backupPreffix;
        this.backupPostfix = backupPostfix;
        this.backupInArchive = backupInArchive;
        this.foldersToBackup = foldersToBackup;
        this.filesToBackup = filesToBackup;
        this.backupsStrategyTypes = backupsStrategyTypes;
        this.workspace = workspace;
    }

    public String getBackupPassword() {
        return backupPassword;
    }

    public void setBackupPassword(String backupPassword) {
        this.backupPassword = backupPassword;
    }

    public String getBackupDateDiff() {
        return backupDateDiff;
    }

    public void setBackupDateDiff(String backupDateDiff) {
        this.backupDateDiff = backupDateDiff;
    }

    public boolean isBackupUseTimestamps() {
        return backupUseTimestamps;
    }

    public void setBackupUseTimestamps(boolean backupUseTimestamps) {
        this.backupUseTimestamps = backupUseTimestamps;
    }

    public boolean isBackupUseVersion() {
        return backupUseVersion;
    }

    public void setBackupUseVersion(boolean backupUseVersion) {
        this.backupUseVersion = backupUseVersion;
    }

    public String getBackupPreffix() {
        return backupPreffix;
    }

    public void setBackupPreffix(String backupPreffix) {
        this.backupPreffix = backupPreffix;
    }

    public String getBackupPostfix() {
        return backupPostfix;
    }

    public void setBackupPostfix(String backupPostfix) {
        this.backupPostfix = backupPostfix;
    }

    public boolean isBackupInArchive() {
        return backupInArchive;
    }

    public void setBackupInArchive(boolean backupInArchive) {
        this.backupInArchive = backupInArchive;
    }

    public List<File> getFoldersToBackup() {
        return foldersToBackup;
    }

    public void setFoldersToBackup(List<File> foldersToBackup) {
        this.foldersToBackup = foldersToBackup;
    }

    public List<File> getFilesToBackup() {
        return filesToBackup;
    }

    public void setFilesToBackup(List<File> filesToBackup) {
        this.filesToBackup = filesToBackup;
    }

    public String getBackupsStrategyTypes() {
        return backupsStrategyTypes;
    }

    public void setBackupsStrategyTypes(String backupsStrategyTypes) {
        this.backupsStrategyTypes = backupsStrategyTypes;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                backupDateDiff,
                backupInArchive,
                backupPassword,
                backupPostfix,
                backupPreffix,
                backupUseTimestamps,
                backupUseVersion,
                backupsStrategyTypes,
                filesToBackup,
                foldersToBackup,
                workspace);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        WorkspaceDescriptor other = (WorkspaceDescriptor) obj;
        return Objects.equals(backupDateDiff, other.backupDateDiff)
                && backupInArchive == other.backupInArchive
                && Objects.equals(backupPassword, other.backupPassword)
                && Objects.equals(backupPostfix, other.backupPostfix)
                && Objects.equals(backupPreffix, other.backupPreffix)
                && backupUseTimestamps == other.backupUseTimestamps
                && backupUseVersion == other.backupUseVersion
                && Objects.equals(backupsStrategyTypes, other.backupsStrategyTypes)
                && Objects.equals(filesToBackup, other.filesToBackup)
                && Objects.equals(foldersToBackup, other.foldersToBackup)
                && Objects.equals(workspace, other.workspace);
    }

    @Override
    public String toString() {
        return REPRESENTATION_PART_1
                + backupPassword
                + REPRESENTATION_PART_2
                + backupDateDiff
                + REPRESENTATION_PART_3
                + backupUseTimestamps
                + REPRESENTATION_PART_4
                + backupUseVersion
                + REPRESENTATION_PART_5
                + backupPreffix
                + REPRESENTATION_PART_6
                + backupPostfix
                + REPRESENTATION_PART_7
                + backupInArchive
                + REPRESENTATION_PART_8
                + filesToBackup.size()
                + REPRESENTATION_PART_9
                + foldersToBackup.size()
                + REPRESENTATION_PART_10;
    }
}

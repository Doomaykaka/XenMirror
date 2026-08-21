package xenmirror.models;

import java.io.File;
import java.util.Objects;

public class Backup {
    private static final String REPRESENTATION_FIRST_PART = "Backup [data=";
    private static final String REPRESENTATION_SECOND_PART = ", descriptor=";
    private static final String REPRESENTATION_THIRD_PART = "]";

    private File data;
    private BackupDescriptor descriptor;

    private Workspace workspace;

    public Backup(File data, BackupDescriptor descriptor) {
        this.data = data;
        this.descriptor = descriptor;

        if (descriptor != null) {
            descriptor.setBackup(this);
        }
    }

    public File getData() {
        return data;
    }

    public void setData(File data) {
        this.data = data;
    }

    public BackupDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(BackupDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, descriptor);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Backup other = (Backup) obj;
        return Objects.equals(data, other.data) && Objects.equals(descriptor, other.descriptor);
    }

    @Override
    public String toString() {
        return REPRESENTATION_FIRST_PART + data + REPRESENTATION_SECOND_PART + descriptor + REPRESENTATION_THIRD_PART;
    }
}

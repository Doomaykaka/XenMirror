package xenmirror.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Workspace {
    private static final String REPRESENTATION_FIRST_PART = "Workspace [backups=";
    private static final String REPRESENTATION_SECOND_PART = ", name=";
    private static final String REPRESENTATION_THIRD_PART = "]";

    private List<Backup> backups;
    private String name;
    private WorkspaceDescriptor descriptor;

    public Workspace(String name) {
        this.backups = new ArrayList<Backup>();
        this.name = name;
    }

    public List<Backup> getBackups() {
        return backups;
    }

    public void setBackups(List<Backup> backups) {
        this.backups = backups;
    }

    public void addBackup(Backup backup) {
        this.backups.add(backup);
        backup.setWorkspace(this);
    }

    public String getName() {
        return name;
    }

    public WorkspaceDescriptor getDescriptor() {
        return descriptor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescriptor(WorkspaceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public void setWorkspaceDescriptor(WorkspaceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Workspace other = (Workspace) obj;
        return Objects.equals(backups, other.backups) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return REPRESENTATION_FIRST_PART + backups + REPRESENTATION_SECOND_PART + name + REPRESENTATION_THIRD_PART;
    }
}

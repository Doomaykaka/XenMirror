package xenmirror.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import xenmirror.controllers.BackupController;
import xenmirror.models.Backup;
import xenmirror.models.Workspace;
import xenmirror.utils.SupportFunctions;

public class WorkspaceBackupsWindow extends JFrame {
    private BackupController backupController;
    private final JTable table;
    private final DefaultTableModel tableModel;

    private final Workspace backupsWorkspace;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 450;
    private static final String WINDOW_TITLE = "Manage workspace backups";

    public WorkspaceBackupsWindow(BackupController backupController, Workspace backupsWorkspace) {
        boolean isResizable = false;

        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(isResizable);

        setLayout(new BorderLayout());

        setIconImage(SupportFunctions.getAppIcon());

        this.backupController = backupController;
        this.backupsWorkspace = backupsWorkspace;

        String[] columnNames = {"Backup name", "Restore", "Delete"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        table.setFillsViewportHeight(true);
        table.setRowHeight(45);
        table.setAutoCreateRowSorter(true);

        table.getColumnModel().getColumn(1).setCellRenderer(getTableCellRenderer("Restore"));
        table.getColumnModel().getColumn(2).setCellRenderer(getTableCellRenderer("Delete"));

        table.addMouseListener(getTableMouseListener());

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadBackups();
    }

    private TableCellRenderer getTableCellRenderer(String text) {
        return new TableCellRenderer() {
            private JButton button;

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (button == null) {
                    button = new JButton();
                    button.setPreferredSize(new Dimension(100, 35));
                }

                button.setText(text);

                return button;
            }
        };
    }

    private MouseListener getTableMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Point p = e.getPoint();

                    int viewRow = table.rowAtPoint(p);
                    if (viewRow < 0) return;

                    int viewColumn = table.columnAtPoint(p);
                    if (viewColumn < 0) return;

                    int modelRow = table.convertRowIndexToModel(viewRow);
                    int modelColumn = table.convertColumnIndexToModel(viewColumn);

                    switch (modelColumn) {
                        case 1:
                            restore(modelRow);
                            break;
                        case 2:
                            removeBackup(modelRow);
                            break;
                    }
                }
            }
        };
    }

    private void restore(int row) {
        Backup backup = (Backup) table.getModel().getValueAt(row, 2);

        backupController.restoreToBackup(backup.getWorkspace(), backup);
    }

    private void removeBackup(int row) {
        Backup backup = (Backup) table.getModel().getValueAt(row, 2);

        removeBackup(backup);
    }

    private void removeBackup(Backup backup) {
        Object[] options = {"Remove", "Cancel"};

        SwingUtilities.invokeLater(() -> {
            long answer = JOptionPane.showOptionDialog(
                    null,
                    "Remove backup?",
                    "Backup remove",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (answer == 0) {
                backupController.removeBackup(backup, backupsWorkspace);

                SupportFunctions.showMessage("Backup removed");
            }

            refreshTable();
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        loadBackups();
    }

    private void loadBackups() {
        for (Backup slot : backupsWorkspace.getBackups()) {
            addBackupSlot(slot);
        }
    }

    private void addBackupSlot(Backup slot) {
        StringBuilder builder = new StringBuilder();

        slot.setWorkspace(backupsWorkspace);
        builder.append(slot.getData().getName());

        tableModel.addRow(new Object[] {builder.toString(), slot, slot, slot});
    }

    public void showWindow() {
        setVisible(true);
    }
}

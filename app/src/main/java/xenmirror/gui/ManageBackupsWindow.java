package xenmirror.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import xenmirror.controllers.BackupController;
import xenmirror.models.Workspace;
import xenmirror.utils.SupportFunctions;

public class ManageBackupsWindow extends JFrame {
    private static BackupController backupController;
    private final JTable table;
    private final DefaultTableModel tableModel;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 450;
    private static final String WINDOW_TITLE = "Manage backups";

    public ManageBackupsWindow(BackupController backupController) {
        boolean isResizable = false;

        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(isResizable);

        setLayout(new BorderLayout());

        this.backupController = backupController;

        String[] columnNames = {"Workspace name", "View backups", "Backup", "Edit", "Delete"};

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

        table.getColumnModel().getColumn(1).setCellRenderer(getTableCellRenderer("View backups"));
        table.getColumnModel().getColumn(2).setCellRenderer(getTableCellRenderer("Backup"));
        table.getColumnModel().getColumn(3).setCellRenderer(getTableCellRenderer("Edit"));
        table.getColumnModel().getColumn(4).setCellRenderer(getTableCellRenderer("Delete"));

        table.addMouseListener(getTableMouseListener());

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomRightPanel = new JPanel();
        bottomRightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomRightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCreateNewWorkspace = new JButton("Create new workspace");
        btnCreateNewWorkspace.addActionListener(e -> createNewWorkspace());
        bottomRightPanel.add(btnCreateNewWorkspace);

        add(bottomRightPanel, BorderLayout.SOUTH);

        loadWorkspaces();
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
                            viewBackups(modelRow);
                            break;
                        case 2:
                            backup(modelRow);
                            break;
                        case 3:
                            editWorkspace(modelRow);
                            break;
                        case 4:
                            removeWorkspace(modelRow);
                            break;
                    }
                }
            }
        };
    }

    private void viewBackups(int row) {}

    private void backup(int row) {
        Workspace workspace = (Workspace) table.getModel().getValueAt(row, 2);

        backupController.createNewBackup(workspace);
    }

    private void editWorkspace(int row) {
        Workspace workspace = (Workspace) table.getModel().getValueAt(row, 3);

        editWorkspace(workspace);
    }

    private void removeWorkspace(int row) {
        Workspace workspace = (Workspace) table.getModel().getValueAt(row, 4);

        removeWorkspace(workspace);
    }

    private void editWorkspace(Workspace workspace) {
        CreateEditWorkspaceWindow createEditWorkspaceWindow =
                new CreateEditWorkspaceWindow(workspace, backupController);
        createEditWorkspaceWindow.showWindow(() -> {
            SwingUtilities.invokeLater(() -> {
                refreshTable();
            });
        });
    }

    private void removeWorkspace(Workspace workspace) {
        Object[] options = {"Remove", "Cancel"};

        SwingUtilities.invokeLater(() -> {
            long answer = JOptionPane.showOptionDialog(
                    null,
                    "Remove workspace?",
                    "Workspace remove",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (answer == 0) {
                backupController.removeWorkspace(workspace);

                SupportFunctions.showMessage("Character removed");
            }

            refreshTable();
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        loadWorkspaces();
    }

    private void loadWorkspaces() {
        for (Workspace slot : backupController.getWorkspaces()) {
            addWorkspaceSlot(slot);
        }
    }

    private void addWorkspaceSlot(Workspace workspace) {
        StringBuilder builder = new StringBuilder();

        builder.append(workspace.getName());

        tableModel.addRow(new Object[] {builder.toString(), workspace, workspace, workspace, workspace});
    }

    private void createNewWorkspace() {
        CreateEditWorkspaceWindow createEditWorkspaceWindow = new CreateEditWorkspaceWindow(backupController);
        createEditWorkspaceWindow.showWindow(() -> {
            SwingUtilities.invokeLater(() -> {
                refreshTable();
            });
        });
    }

    public void showWindow() {
        setVisible(true);
    }
}

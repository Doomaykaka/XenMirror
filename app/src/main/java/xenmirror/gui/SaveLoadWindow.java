package xenmirror.gui;

import java.awt.*;
import javax.swing.*;

public class SaveLoadWindow extends JFrame {
    //    private static GameOperationsController gameOperationsController;
    //    private final boolean runInStatisticMode;
    //    private final JTable table;
    //    private final DefaultTableModel tableModel;
    //
    //    private static volatile dark_shell.models.database.Character currentCharacter;
    //
    //    private static final int WIDTH = 600;
    //    private static final int HEIGHT = 450;
    //    private static final String WINDOW_TITLE = "Load game";
    //
    //    public SaveLoadWindow(boolean runInStatisticMode) {
    //        this.runInStatisticMode = runInStatisticMode;
    //
    //        boolean isResizable = false;
    //
    //        setTitle(WINDOW_TITLE);
    //        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    //        setSize(WIDTH, HEIGHT);
    //        setLocationRelativeTo(null);
    //        setResizable(isResizable);
    //
    //        setLayout(new BorderLayout());
    //
    //        String[] columnNames = {"Character name", "Load", "Delete"};
    //
    //        if (runInStatisticMode) {
    //            columnNames = new String[] {"Character name", "View statistic"};
    //        }
    //
    //        tableModel = new DefaultTableModel(columnNames, 0) {
    //            @Override
    //            public boolean isCellEditable(int row, int column) {
    //                return false;
    //            }
    //        };
    //        table = new JTable(tableModel);
    //
    //        table.setFillsViewportHeight(true);
    //        table.setRowHeight(45);
    //        table.setAutoCreateRowSorter(true);
    //
    //        if (runInStatisticMode) {
    //            table.getColumnModel().getColumn(1).setCellRenderer(getTableCellRenderer("View statistic"));
    //        } else {
    //            table.getColumnModel().getColumn(1).setCellRenderer(getTableCellRenderer("Load"));
    //            table.getColumnModel().getColumn(2).setCellRenderer(getTableCellRenderer("Delete"));
    //        }
    //
    //        table.getSelectionModel().addListSelectionListener(getTableListSelectionListener());
    //
    //        JScrollPane scrollPane = new JScrollPane(table);
    //        add(scrollPane, BorderLayout.CENTER);
    //
    //        JPanel bottomRightPanel = new JPanel();
    //        bottomRightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    //        bottomRightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    //
    //        JButton btnLoadFromFile = new JButton("Load save from file");
    //        btnLoadFromFile.addActionListener(e -> loadCharacterFromFile());
    //        bottomRightPanel.add(btnLoadFromFile);
    //
    //        if (!runInStatisticMode) {
    //            add(bottomRightPanel, BorderLayout.SOUTH);
    //        }
    //
    //        initController();
    //        loadCharacters();
    //    }
    //
    //    private TableCellRenderer getTableCellRenderer(String text) {
    //        return new TableCellRenderer() {
    //            private JButton button;
    //
    //            @Override
    //            public Component getTableCellRendererComponent(
    //                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    //                if (button == null) {
    //                    button = new JButton();
    //                    button.setPreferredSize(new Dimension(100, 35));
    //                }
    //
    //                button.setText(text);
    //
    //                return button;
    //            }
    //        };
    //    }
    //
    //    private ListSelectionListener getTableListSelectionListener() {
    //        return e -> {
    //            if (!e.getValueIsAdjusting()) {
    //                int viewRow = table.getSelectedRow();
    //                if (viewRow < 0) return;
    //
    //                int viewColumn = table.getSelectedColumn();
    //                if (viewColumn < 0) return;
    //
    //                int modelRow = table.convertRowIndexToModel(viewRow);
    //                int modelColumn = table.convertColumnIndexToModel(viewColumn);
    //
    //                Character character = (Character) tableModel.getValueAt(modelRow, 1);
    //
    //                if (runInStatisticMode) {
    //                    viewStatsButtonClick(modelColumn, character);
    //                } else {
    //                    loadRemoveButtonsClick(modelColumn, character);
    //                }
    //            }
    //        };
    //    }
    //
    //    private void loadRemoveButtonsClick(int modelColumn, Character character) {
    //        if (modelColumn == 1) {
    //            setCurrentCharacter(character);
    //
    //            SupportFunctions.showMessage("Character loaded");
    //
    //            AdditionalGameWindows.selectGameSetting(character);
    //
    //            this.dispose();
    //        } else if (modelColumn == 2) {
    //            removeCharacter(character);
    //        }
    //    }
    //
    //    private void viewStatsButtonClick(int modelColumn, Character character) {
    //        if (modelColumn == 1) {
    //            Statistic statistic = character.getStatistic();
    //
    //            SupportFunctions.showMessage("Character stats loaded");
    //
    //            StringBuilder builder = new StringBuilder();
    //
    //            builder.append("Character ");
    //            builder.append(character.getName());
    //            builder.append(":\n");
    //
    //            builder.append("last play date - ");
    //            builder.append(statistic.getLastPlayDate());
    //            builder.append("\n");
    //            builder.append("days in game - ");
    //            builder.append(statistic.getDaysInGame());
    //            builder.append("\n");
    //            builder.append("events conducted - ");
    //            builder.append(statistic.getEventsConducted());
    //            builder.append("\n");
    //            builder.append("number of successful events - ");
    //            builder.append(statistic.getNumberOfSuccessfulEvents());
    //            builder.append("\n");
    //            builder.append("max level - ");
    //            builder.append(statistic.getMaxLevel());
    //            builder.append("\n");
    //            builder.append("max experience - ");
    //            builder.append(statistic.getMaxExperience());
    //            builder.append("\n");
    //
    //            SupportFunctions.showMessage(builder.toString());
    //        }
    //    }
    //
    //    private void removeCharacter(Character character) {
    //        Object[] options = {"Remove", "Cancel"};
    //
    //        SwingUtilities.invokeLater(() -> {
    //            long answer = JOptionPane.showOptionDialog(
    //                    null,
    //                    "Remove character?",
    //                    "Character remove",
    //                    JOptionPane.DEFAULT_OPTION,
    //                    JOptionPane.QUESTION_MESSAGE,
    //                    null,
    //                    options,
    //                    options[0]);
    //
    //            if (answer == 0) {
    //                gameOperationsController.removeCharacter(character.getId());
    //
    //                SupportFunctions.showMessage("Character removed");
    //            }
    //
    //            refreshTable();
    //        });
    //    }
    //
    //    private void refreshTable() {
    //        tableModel.setRowCount(0);
    //        loadCharacters();
    //    }
    //
    //    private void initController() {
    //        CharactersDAO charactersDAO = new CharactersDAO(HibernateConfiguration.getEntityManagerFactory());
    //        CyberpunkCharacteristicsDAO cyberpunkCharacteristicsDAO =
    //                new CyberpunkCharacteristicsDAO(HibernateConfiguration.getEntityManagerFactory());
    //        FantasyCharacteristicsDAO fantasyCharacteristicsDAO =
    //                new FantasyCharacteristicsDAO(HibernateConfiguration.getEntityManagerFactory());
    //        PostApocalypseCharacteristicsDAO postApocalypseCharacteristicsDAO =
    //                new PostApocalypseCharacteristicsDAO(HibernateConfiguration.getEntityManagerFactory());
    //        StatisticsDAO statisticsDAO = new StatisticsDAO(HibernateConfiguration.getEntityManagerFactory());
    //
    //        gameOperationsController = new GameOperationsController(
    //                charactersDAO,
    //                cyberpunkCharacteristicsDAO,
    //                fantasyCharacteristicsDAO,
    //                postApocalypseCharacteristicsDAO,
    //                statisticsDAO);
    //    }
    //
    //    private void loadCharacters() {
    //        java.util.List<Character> characters = gameOperationsController.getAllCharacters();
    //        for (Character slot : characters) {
    //            addLoadSlot(slot);
    //        }
    //    }
    //
    //    private void addLoadSlot(Character character) {
    //        StringBuilder builder = new StringBuilder();
    //
    //        builder.append(character.getName());
    //        builder.append(" [");
    //        builder.append("cyb - ");
    //        builder.append(character.getCyberpunkCharacteristics().getLevel());
    //        builder.append(" lvl ,");
    //        builder.append("fan - ");
    //        builder.append(character.getFantasyCharacteristics().getLevel());
    //        builder.append(" lvl ,");
    //        builder.append("apoc - ");
    //        builder.append(character.getPostApocalypseCharacteristics().getLevel());
    //        builder.append(" lvl ");
    //        builder.append("]");
    //
    //        if (runInStatisticMode) {
    //            tableModel.addRow(new Object[] {builder.toString(), character});
    //        } else {
    //            tableModel.addRow(new Object[] {builder.toString(), character, character});
    //        }
    //    }
    //
    //    private void loadCharacterFromFile() {
    //        File saveFile = SupportFunctions.chooseFile();
    //        if (saveFile == null) return;
    //
    //        Character loadedCharacter = gameOperationsController.importCharacter(saveFile);
    //        if (loadedCharacter != null) {
    //            gameOperationsController.saveCharacter(loadedCharacter);
    //            refreshTable();
    //            SupportFunctions.showMessage("Character imported from file");
    //        } else {
    //            SupportFunctions.showMessage("Failed to import character");
    //        }
    //    }
    //
    //    public void showWindow() {
    //        setVisible(true);
    //    }
    //
    //    public static void setCurrentCharacter(dark_shell.models.database.Character character) {
    //        currentCharacter = character;
    //    }
    //
    //    public static dark_shell.models.database.Character getCurrentCharacter() {
    //        return currentCharacter;
    //    }
    //
    //    public static void saveCharacter() {
    //        if (getCurrentCharacter() == null || gameOperationsController == null) {
    //            SupportFunctions.showMessage("Load game before save");
    //            return;
    //        }
    //
    //        File saveFile = SupportFunctions.saveFile();
    //
    //        if (saveFile == null) {
    //            SupportFunctions.showMessage("Bad save file");
    //            return;
    //        }
    //
    //        gameOperationsController.exportCharacter(getCurrentCharacter(), saveFile);
    //
    //        SupportFunctions.showMessage("Character exported to file");
    //    }
}

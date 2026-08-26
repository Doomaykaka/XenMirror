package xenmirror.utils;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import xenmirror.models.Backup;
import xenmirror.models.BackupDescriptor;
import xenmirror.models.BackupStrategyType;
import xenmirror.models.Workspace;
import xenmirror.models.WorkspaceDescriptor;

public class SupportFunctions {
    private static final boolean ENCRYPTION_DEFAULT_PARAM = true;

    public static Image getAppIcon() {
        Image appIcon = null;

        URL appIconUrl = SupportFunctions.class.getResource(Constants.getGuiImageIconResourcePath());
        appIcon = Toolkit.getDefaultToolkit().getImage(appIconUrl);

        return appIcon;
    }

    public static JPanel getEntityWindowCheckbox(String label) {
        JPanel result = null;

        result = new JPanel();
        BoxLayout layout = new BoxLayout(result, BoxLayout.X_AXIS);
        result.setLayout(layout);

        JCheckBox input = new JCheckBox();

        result.add(new JLabel(label));
        result.add(new JLabel(Constants.getSpace()));
        result.add(input);

        return result;
    }

    public static void addChildPanelWithGap(JPanel panel, JPanel child, int gap) {
        panel.add(child);
        panel.add(Box.createRigidArea(new Dimension(gap, gap)));
    }

    public static void addButtonWithGap(JPanel panel, JButton button, int gap) {
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(gap, gap)));
    }

    public static void setEntityWindowCheckboxValue(JPanel panel, boolean value) {
        int checkboxIndex = 2;

        JCheckBox checkbox = (JCheckBox) panel.getComponent(checkboxIndex);
        checkbox.getModel().setSelected(value);
    }

    public static void setEntityWindowJTextfieldValue(JPanel panel, String value) {
        int jtextfieldIndex = 2;

        JTextField input = (JTextField) panel.getComponent(jtextfieldIndex);
        input.setText(value);
    }

    public static File chooseFolder() {
        File selectedFile = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int state = fileChooser.showOpenDialog(null);

        if (state == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }

        return selectedFile;
    }

    public static File[] chooseFolders() {
        File[] selectedFolders = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int state = fileChooser.showOpenDialog(null);

        if (state == JFileChooser.APPROVE_OPTION) {
            selectedFolders = fileChooser.getSelectedFiles();

            if (selectedFolders.length == 0) {
                selectedFolders = new File[] {fileChooser.getSelectedFile()};
            }
        }

        return selectedFolders;
    }

    public static File[] chooseFiles() {
        File[] selectedFiles = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int state = fileChooser.showOpenDialog(null);

        if (state == JFileChooser.APPROVE_OPTION) {
            selectedFiles = fileChooser.getSelectedFiles();

            if (selectedFiles.length == 0) {
                selectedFiles = new File[] {fileChooser.getSelectedFile()};
            }
        }

        return selectedFiles;
    }

    public static boolean getEntityWindowCheckboxValue(JPanel panel) {
        int checkboxIndex = 2;

        JCheckBox checkbox = (JCheckBox) panel.getComponent(checkboxIndex);
        return checkbox.isSelected();
    }

    public static String getEntityWindowJTextfieldValue(JPanel panel) {
        int jtextfieldIndex = 2;

        JTextField input = (JTextField) panel.getComponent(jtextfieldIndex);
        return input.getText();
    }

    public static JPanel getEntityWindowJTextfield(String label) {
        JPanel result = null;

        result = new JPanel();
        BoxLayout layout = new BoxLayout(result, BoxLayout.X_AXIS);
        result.setLayout(layout);

        JTextField input = new JTextField();

        result.add(new JLabel(label));
        result.add(new JLabel(Constants.getSpace()));
        result.add(input);

        return result;
    }

    public static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void addChildWithGap(JPanel panel, JComponent child, int gap) {
        panel.add(child);
        panel.add(Box.createRigidArea(new Dimension(gap, gap)));
    }

    public static void correctExit() {
        Logger.printApplicationLog("Correct exit", "SupportFunctions");

        System.exit(Constants.getCorrectExitCode());
    }

    public static void nonCorrectExit() {
        Logger.printApplicationLog("Non correct exit", "SupportFunctions");

        System.exit(Constants.getNonCorrectExitCode());
    }

    public static List<String> readFileContent(File fileToRead) {
        String defaultEncoding = Config.getConfig().getSystemEncoding();

        return readFileContent(fileToRead, defaultEncoding);
    }

    public static List<String> readFileContent(File fileToRead, String encoding) {
        Logger.printApplicationLog("File start reading", "SupportFunctions");

        List<String> result = new ArrayList<String>();

        if (fileToRead == null || !fileToRead.exists() || !fileToRead.canRead()) {
            return result;
        }

        try {
            Scanner scanner = null;

            if (encoding == null) {
                scanner = new Scanner(new FileInputStream(fileToRead));
            } else {
                scanner = new Scanner(new FileInputStream(fileToRead), encoding);
            }

            while (scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            Logger.printApplicationLog("read file error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        return result;
    }

    public static boolean writeFilesAndFolders(
            List<String> filepaths,
            List<String> folderpaths,
            File archiveFileOrFolder,
            BackupDescriptor descriptor,
            Workspace workspace) {
        Logger.printApplicationLog("Start files and folders writing", "SupportFunctions");

        WorkspaceDescriptor workspaceDescriptor = null;
        try {
            workspaceDescriptor = parseWorkspaceDescriptor(workspace, Config.getConfig());
        } catch (IOException e) {
            Logger.printApplicationLog("getting workspace error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        if (!workspaceDescriptor.isBackupInArchive()) {
            Logger.printApplicationLog("Write files and folders without archive", "SupportFunctions");

            return writeFilesAndFoldersWithoutArchive(filepaths, folderpaths, archiveFileOrFolder);
        }

        if (workspaceDescriptor.getBackupPassword() != null
                && !workspaceDescriptor.getBackupPassword().isEmpty()) {
            Logger.printApplicationLog("Write files and folders in archive with password", "SupportFunctions");

            return writeFilesAndFoldersInArchiveWithPassword(
                    filepaths, folderpaths, archiveFileOrFolder, descriptor, workspace);
        } else {
            Logger.printApplicationLog("Write files and folders in archive without password", "SupportFunctions");

            return writeFilesAndFoldersInArchiveWithoutPassword(filepaths, folderpaths, archiveFileOrFolder);
        }
    }

    public static boolean writeFilesAndFoldersWithoutArchive(
            List<String> filepaths, List<String> folderpaths, File folder) {
        boolean success = Constants.getBoolDefault();

        List<File> filepathsParsed = listOfPathsToListOfFiles(filepaths);
        List<File> folderpathsParsed = listOfPathsToListOfFiles(folderpaths);

        for (File file : filepathsParsed) {
            copyFilesAndFolders(file, folder);
        }

        for (File folderParsed : folderpathsParsed) {
            copyFilesAndFolders(folderParsed, folder);
        }

        return success;
    }

    public static boolean writeFilesAndFoldersInArchiveWithPassword(
            List<String> filepaths,
            List<String> folderpaths,
            File archive,
            BackupDescriptor descriptor,
            Workspace workspace) {
        boolean success = Constants.getBoolDefault();

        WorkspaceDescriptor workspaceDescriptor = null;
        try {
            workspaceDescriptor = parseWorkspaceDescriptor(workspace, Config.getConfig());
        } catch (IOException e) {
            Logger.printApplicationLog("getting workspace error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        ZipParameters zipFileSettings = new ZipParameters();
        zipFileSettings.setEncryptFiles(ENCRYPTION_DEFAULT_PARAM);
        zipFileSettings.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

        ZipFile zipFile =
                new ZipFile(archive, workspaceDescriptor.getBackupPassword().toCharArray());
        zipFile.setCharset(Charset.forName(Config.getConfig().getSystemEncoding()));

        List<File> filepathsParsed = listOfPathsToListOfFiles(filepaths);
        List<File> folderpathsParsed = listOfPathsToListOfFiles(folderpaths);

        writeFilesIntoZip(filepathsParsed, zipFile, zipFileSettings);
        writeFoldersIntoZip(folderpathsParsed, zipFile, zipFileSettings);

        closeZipFile(zipFile);

        success = !Constants.getBoolDefault();

        return success;
    }

    private static void writeFilesIntoZip(List<File> filepathsParsed, ZipFile zipFile, ZipParameters zipFileSettings) {
        Logger.printApplicationLog("Write files in archive", "SupportFunctions");

        for (File file : filepathsParsed) {
            try {
                zipFile.addFile(file, zipFileSettings);
            } catch (ZipException e) {
                Logger.printApplicationLog("zip file work with files error", "SupportFunctions");
                Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
            }
        }
    }

    private static void writeFoldersIntoZip(
            List<File> folderpathsParsed, ZipFile zipFile, ZipParameters zipFileSettings) {
        Logger.printApplicationLog("Write folders in archive", "SupportFunctions");

        for (File folder : folderpathsParsed) {
            try {
                zipFile.addFolder(folder, zipFileSettings);
            } catch (ZipException e) {
                Logger.printApplicationLog("zip file work with folders error", "SupportFunctions");
                Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
            }
        }
    }

    private static void closeZipFile(ZipFile zipFile) {
        Logger.printApplicationLog("Archive closing", "SupportFunctions");

        try {
            zipFile.close();
        } catch (IOException e) {
            Logger.printApplicationLog("zip file close error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }
    }

    public static boolean writeFilesAndFoldersInArchiveWithoutPassword(
            List<String> filepaths, List<String> folderpaths, File archive) {
        boolean success = Constants.getBoolDefault();

        List<File> filepathsParsed = listOfPathsToListOfFiles(filepaths);
        List<File> folderpathsParsed = listOfPathsToListOfFiles(folderpaths);

        List<File> allPaths = new ArrayList<File>(filepathsParsed);
        allPaths.addAll(folderpathsParsed);

        File relativePathFile = getRelativePath(allPaths);
        Path relativePath = Paths.get(relativePathFile.toURI());

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(
                new FileOutputStream(archive),
                Charset.forName(Config.getConfig().getSystemEncoding()))) {
            putFoldersIntoZip(folderpathsParsed, relativePath, zipOutputStream);
            putFilesIntoZip(filepathsParsed, relativePath, zipOutputStream);
        } catch (Exception e) {
            Logger.printApplicationLog("zipping files error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        success = !Constants.getBoolDefault();

        return success;
    }

    private static void putFoldersIntoZip(
            List<File> folderpathsParsed, Path relativePath, ZipOutputStream zipOutputStream) throws Exception {
        Logger.printApplicationLog("Put folders into zip", "SupportFunctions");

        for (File folder : folderpathsParsed) {
            if (folder.isDirectory()) {
                putFoldersIntoZip(Arrays.asList(folder.listFiles()), relativePath, zipOutputStream);
            } else {
                Path filePath = Paths.get(folder.toURI());
                ZipEntry zipEntry =
                        new ZipEntry(relativePath.relativize(filePath).toString());

                zipOutputStream.putNextEntry(zipEntry);
                Files.copy(filePath, zipOutputStream);
                zipOutputStream.closeEntry();
            }
        }
    }

    private static void putFilesIntoZip(List<File> filepathsParsed, Path relativePath, ZipOutputStream zipOutputStream)
            throws Exception {
        Logger.printApplicationLog("Put files into zip", "SupportFunctions");

        for (File file : filepathsParsed) {
            Path filePath = Paths.get(file.toURI());
            ZipEntry zipEntry = new ZipEntry(relativePath.relativize(filePath).toString());

            zipOutputStream.putNextEntry(zipEntry);
            Files.copy(filePath, zipOutputStream);
            zipOutputStream.closeEntry();
        }
    }

    public static boolean unzipFilesAndFoldersFromArchive(File targetFolder, File archive, String password) {
        boolean success = Constants.getBoolDefault();

        try (ZipFile fileToUnzip = new ZipFile(archive, password.toCharArray())) {
            fileToUnzip.setCharset(Charset.forName(Config.getConfig().getSystemEncoding()));

            fileToUnzip.extractAll(targetFolder.getAbsolutePath());
            success = !Constants.getBoolDefault();
        } catch (IOException e) {
            Logger.printApplicationLog("zip file extract error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        return success;
    }

    public static List<File> listOfPathsToListOfFiles(List<String> paths) {
        List<File> result = new ArrayList<File>();

        for (String path : paths) {
            result.add(new File(path));
        }

        return result;
    }

    public static File getRelativePath(List<File> paths) {
        File relativePath = null;

        File firstPath = paths.getFirst();
        List<File> firstPathParts = new ArrayList<File>();

        while (firstPath != null) {
            firstPathParts.add(firstPath);
            firstPath = firstPath.getParentFile();
        }

        List<File> filtredPathParts = new ArrayList<File>(firstPathParts);

        for (File pathToCheck : paths) {
            for (File firstPathPart : firstPathParts) {
                if (!pathToCheck.getAbsolutePath().contains(firstPathPart.getAbsolutePath())) {
                    filtredPathParts.remove(firstPathPart);
                }
            }
        }

        File maxLengthPath = filtredPathParts.getFirst();

        for (File pathToCheck : filtredPathParts) {
            if (pathToCheck.toString().length() > maxLengthPath.toString().length()) {
                maxLengthPath = pathToCheck;
            }
        }

        relativePath = maxLengthPath;

        return relativePath;
    }

    public static boolean writeBackupDescriptor(
            BackupDescriptor descriptor, File backupDescriptorFile, Workspace workspace) {
        Logger.printApplicationLog("Start writing backup descriptor", "SupportFunctions");

        boolean success = Constants.getBoolDefault();

        if (backupDescriptorFile == null || descriptor == null || !backupDescriptorFile.exists()) {
            return success;
        }

        Properties properties = new Properties();

        String filepaths = stringsListToString(descriptor.getFilePaths());
        String folderpaths = stringsListToString(descriptor.getFoldersPaths());
        String changeStamp = descriptor.getChangeStamp();
        String filesCount = Integer.toString(descriptor.getFilesCount());
        String foldersCount = Integer.toString(descriptor.getFoldersCount());
        String filesCountTotal = Integer.toString(descriptor.getFilesCountTotal());
        String foldersCountTotal = Integer.toString(descriptor.getFoldersCountTotal());
        boolean isSecured = descriptor.isSecured();
        String version = Long.toString(descriptor.getVersion());

        try (FileOutputStream configFOS = new FileOutputStream(backupDescriptorFile)) {
            OutputStreamWriter writer =
                    new OutputStreamWriter(configFOS, Config.getConfig().getSystemEncoding());

            SupportFunctions.setStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFilepaths(), filepaths);
            SupportFunctions.setStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFolderpaths(), folderpaths);
            SupportFunctions.setStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameChangeStamp(), changeStamp);
            SupportFunctions.setStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFilesCount(), filesCount);
            SupportFunctions.setStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFoldersCount(), foldersCount);
            SupportFunctions.setStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFilesCountTotal(), filesCountTotal);
            SupportFunctions.setStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFoldersCountTotal(), foldersCountTotal);
            SupportFunctions.setBooleanProperty(
                    properties, Constants.getBackupDescriptorPropertyNameIsSecured(), isSecured);
            SupportFunctions.setStringProperty(properties, Constants.getBackupDescriptorPropertyNameVersion(), version);

            properties.store(writer, Constants.getTextDefault());
            configFOS.flush();
        } catch (FileNotFoundException e) {
            Logger.printApplicationLog("descriptor file search error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        } catch (IOException e) {
            Logger.printApplicationLog("descriptor file write error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        success = !Constants.getBoolDefault();

        return success;
    }

    public static String byteArrayToString(byte[] bytes) {
        String result = Constants.getTextDefault();

        StringBuilder sb = new StringBuilder();

        for (byte chunk : bytes) {
            sb.append(Byte.toString(chunk));
            sb.append(Constants.getSpace());
        }

        result = sb.toString();
        result = result.substring(Constants.getStartIndex(), result.length() + Constants.getSizeToIndexOffset());

        return result;
    }

    public static byte[] bytesRepresentationToArray(String representation) {
        byte[] result = new byte[] {};

        List<Byte> resultList = new ArrayList<Byte>();
        String[] chunks = representation.split(Constants.getSpaceRegexp());

        for (String chunk : chunks) {
            resultList.add(Byte.parseByte(chunk));
        }

        result = new byte[resultList.size()];

        for (int i = 0; i < resultList.size(); i++) {
            result[i] = resultList.get(i);
        }

        return result;
    }

    public static String stringsListToString(List<String> strings) {
        String result = Constants.getTextDefault();

        if (strings == null || strings.isEmpty()) {
            return result;
        }

        StringBuilder sb = new StringBuilder();

        for (String row : strings) {
            sb.append(row);
            sb.append(Constants.getListSeparator());
        }

        result = sb.toString();
        result = result.substring(
                Constants.getStartIndex(),
                result.length() - Constants.getListSeparator().length());

        return result;
    }

    public static List<String> listRepresentationToList(String representation) {
        List<String> result = new ArrayList<String>();

        String reprCopy = new String(representation);

        while (reprCopy.contains(Constants.getListSeparator())) {
            String element =
                    reprCopy.substring(Constants.getStartIndex(), reprCopy.indexOf(Constants.getListSeparator()));

            reprCopy = reprCopy.substring(reprCopy.indexOf(Constants.getListSeparator())
                    + Constants.getListSeparator().length());
            result.add(element);
        }

        if (!reprCopy.isEmpty()) {
            result.add(reprCopy);
        }

        return result;
    }

    public static String listToReprepsentationsList(List<File> files) {
        String result = Constants.getTextDefault();

        if (files == null || files.isEmpty()) {
            return result;
        }

        StringBuilder sb = new StringBuilder();

        for (File file : files) {
            sb.append(file.getAbsolutePath());
            sb.append(Constants.getListSeparator());
        }

        result = sb.toString();
        result = result.substring(
                Constants.getStartIndex(),
                result.length() - Constants.getListSeparator().length());

        return result;
    }

    public static String getStringProperty(Properties property, String propertyName) {
        if (property.getProperty(propertyName) == null) {
            return Constants.getConfigDefaultStringPropertiesValues();
        }

        String propertyRepresentation = property.getProperty(propertyName);

        return propertyRepresentation;
    }

    public static void setStringProperty(Properties property, String propertyName, String value) {
        if (property == null) {
            return;
        }

        property.setProperty(propertyName, value);
    }

    public static boolean getBooleanProperty(Properties property, String propertyName) {
        if (property.getProperty(propertyName) == null) {
            return Constants.getConfigDefaultBooleanPropertiesValues();
        }

        String propertyRepresentation = property.getProperty(propertyName);

        return Boolean.parseBoolean(propertyRepresentation);
    }

    public static void setBooleanProperty(Properties property, String propertyName, Boolean value) {
        if (property == null) {
            return;
        }

        String propertyValueRepresentation = Boolean.toString(value);

        property.setProperty(propertyName, propertyValueRepresentation);
    }

    public static synchronized void clearFolder(File folder) {
        for (File entry : folder.listFiles()) {
            if (entry.isDirectory()) {
                clearFolder(entry);
            }

            entry.delete();
        }
    }

    public static Backup parseBackupFolder(File folder, Workspace workspace) {
        Logger.printApplicationLog("Start backup folder parsing", "SupportFunctions");

        Backup result = null;

        if (folder == null
                || !folder.exists()
                || !folder.isDirectory()
                || workspace == null
                || folder.listFiles().length == 0) {
            Logger.printApplicationLog("Bad backup folder parsing input", "SupportFunctions");
            return result;
        }

        WorkspaceDescriptor config = null;
        try {
            config = parseWorkspaceDescriptor(workspace, Config.getConfig());
        } catch (IOException e) {
            Logger.printApplicationLog("backup config creating error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        File[] folderElements = folder.listFiles();

        File descriptorFile = null;
        File archiveFile = null;

        final int NORMAL_FOLDER_SIZE = 2;

        if (folderElements.length < NORMAL_FOLDER_SIZE) {
            return result;
        }

        for (File element : folderElements) {
            if (descriptorFile == null) {
                descriptorFile = checkThatFileIsDescriptorAndReturn(element);
            }

            if (archiveFile == null) {
                archiveFile = checkThatFileIsArchiveAndReturn(element, config);
            }

            if (archiveFile == null) {
                archiveFile = checkThatFileIsDirectoryAndReturn(element, config);
            }
        }

        if (descriptorFile == null || archiveFile == null) {
            Logger.printApplicationLog("backup is corrupted", "SupportFunctions");

            return result;
        }

        BackupDescriptor descriptor = readBackupDescriptor(descriptorFile);

        return buildBackup(config, archiveFile, descriptor);
    }

    private static File checkThatFileIsDescriptorAndReturn(File file) {
        File result = null;

        if (file.isFile() && !file.isDirectory() && file.getName().endsWith(Constants.getBackupDescriptionFileExt())) {
            result = file;
        }

        return result;
    }

    private static File checkThatFileIsArchiveAndReturn(File file, WorkspaceDescriptor workspaceDescriptor) {
        File result = null;

        if (file.isFile()
                && workspaceDescriptor.isBackupInArchive()
                && !file.isDirectory()
                && file.getName().endsWith(Constants.getBackupArchiveFileExt())) {
            result = file;
        }

        return result;
    }

    private static File checkThatFileIsDirectoryAndReturn(File file, WorkspaceDescriptor workspaceDescriptor) {
        File result = null;

        if (!workspaceDescriptor.isBackupInArchive() && file.isDirectory()) {
            result = file;
        }

        return result;
    }

    private static Backup buildBackup(
            WorkspaceDescriptor workspaceDescriptor, File archiveFile, BackupDescriptor descriptor) {
        Backup result = null;

        if (!workspaceDescriptor.isBackupInArchive()) {
            File backupFolder = archiveFile;

            try {
                if (!archiveFile.exists()) {
                    throw new SecurityException("not found");
                }
            } catch (SecurityException e) {
                Logger.printApplicationLog("can't access to backup folder", "SupportFunctions");
                Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
            }

            result = new Backup(backupFolder, descriptor);

            return result;
        } else if (workspaceDescriptor.isBackupInArchive()
                && (workspaceDescriptor.getBackupPassword() == null
                        || workspaceDescriptor.getBackupPassword().isEmpty())) {
            try (java.util.zip.ZipFile archiveFileZip = new java.util.zip.ZipFile(archiveFile)) {
                if (archiveFileZip.size() == Constants.getIntDefault()) {
                    throw new ZipException("empty zip");
                }
            } catch (java.util.zip.ZipException e) {
                Logger.printApplicationLog("archive file read error", "SupportFunctions");
                Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
            } catch (IOException e) {
                Logger.printApplicationLog("archive file read error", "SupportFunctions");
                Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
            }

            result = new Backup(archiveFile, descriptor);

            return result;
        }

        try (ZipFile archiveFileZip = new ZipFile(archiveFile)) {
            if (!archiveFileZip.isValidZipFile()) {
                throw new ZipException("bad encrypted zip");
            }
        } catch (ZipException e) {
            Logger.printApplicationLog("encrypted archive file read error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        } catch (IOException e) {
            Logger.printApplicationLog("encrypted archive file read error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        result = new Backup(archiveFile, descriptor);

        return result;
    }

    public static BackupDescriptor readBackupDescriptor(File descriptorFile) {
        Logger.printApplicationLog("Start backup descriptor reading", "SupportFunctions");

        BackupDescriptor result = null;

        boolean backupIsCorrupted = descriptorFile.getParentFile().listFiles().length == 1;

        if (descriptorFile == null || !descriptorFile.exists() || backupIsCorrupted) {
            return result;
        }

        String filepaths = Constants.getTextDefault();
        String folderpaths = Constants.getTextDefault();
        String changestamp = Constants.getTextDefault();
        String filesCount = Constants.getTextDefault();
        boolean isSecured = Constants.getBoolDefault();
        String version = Constants.getTextDefault();

        try (FileInputStream descriptorFIS = new FileInputStream(descriptorFile)) {
            InputStreamReader reader =
                    new InputStreamReader(descriptorFIS, Config.getConfig().getSystemEncoding());
            Properties properties = new Properties();
            properties.load(reader);

            filepaths = SupportFunctions.getStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFilepaths());
            folderpaths = SupportFunctions.getStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFolderpaths());
            changestamp = SupportFunctions.getStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameChangeStamp());
            filesCount = SupportFunctions.getStringProperty(
                    properties, Constants.getBackupDescriptorPropertyNameFilesCount());
            isSecured = SupportFunctions.getBooleanProperty(
                    properties, Constants.getBackupDescriptorPropertyNameIsSecured());
            version =
                    SupportFunctions.getStringProperty(properties, Constants.getBackupDescriptorPropertyNameVersion());
        } catch (FileNotFoundException e) {
            Logger.printApplicationLog("descriptor file not found error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        } catch (IOException e) {
            Logger.printApplicationLog("descriptor file read error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        if (filepaths.isEmpty() && folderpaths.isEmpty()) {
            return result;
        }

        List<File> filesToBackup = listOfPathsToListOfFiles(listRepresentationToList(filepaths));
        List<File> foldersToBackup = listOfPathsToListOfFiles(listRepresentationToList(folderpaths));

        boolean needDataCheck = Constants.getBoolDefault();

        result = new BackupDescriptor(foldersToBackup, filesToBackup, isSecured, needDataCheck);

        long versionValue = 0;

        try {
            versionValue = Long.parseLong(version);
        } catch (NumberFormatException e) {
            ;
        }

        result.setVersion(versionValue);
        result.setChangeStamp(changestamp);

        try {
            result.setFilesCount(Integer.parseInt(filesCount));
        } catch (NumberFormatException e) {
            ;
        }

        return result;
    }

    public static void copyFilesAndFolders(File source, File destination) {
        Logger.printApplicationLog("Copy files and folders", "SupportFunctions");

        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyDirectory(File source, File destination) {
        File directoryInDestination = new File(destination.getAbsolutePath(), source.getName());

        if (!directoryInDestination.exists() || !directoryInDestination.isDirectory()) {
            directoryInDestination.mkdir();
        }

        for (File sourcePart : source.listFiles()) {
            copyFilesAndFolders(sourcePart, directoryInDestination);
        }
    }

    private static void copyFile(File source, File destination) {
        File fileInDestination = null;

        if (destination != null && destination.exists() && destination.isDirectory()) {
            fileInDestination = new File(destination.getAbsolutePath(), source.getName());
        } else {
            fileInDestination = new File(destination.getAbsolutePath());
        }

        RandomAccessFile fileInDestinationW = null;

        try {
            fileInDestinationW = new RandomAccessFile(fileInDestination, Constants.getWriteFileFlags());
        } catch (FileNotFoundException e) {
            Logger.printApplicationLog("file open error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        if (fileInDestination.exists()) {
            try {
                fileInDestination.createNewFile();

                try (FileInputStream srcStream = new FileInputStream(source)) {
                    FileChannel src = srcStream.getChannel();
                    FileChannel dest = fileInDestinationW.getChannel();

                    dest.transferFrom(src, Constants.getStartIndex(), src.size());
                } catch (IOException e) {
                    Logger.printApplicationLog("file copy error", "SupportFunctions");
                    Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
                }

                fileInDestinationW.close();
            } catch (IOException e) {
                Logger.printApplicationLog("file copy error", "SupportFunctions");
                Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
            }
        }
    }

    public static Set<BackupStrategyType> parseBackupStrategyTypes(String rawData) {
        Set<BackupStrategyType> result = new HashSet<BackupStrategyType>();

        if (rawData == null || rawData.isEmpty()) {
            Logger.printApplicationLog("empty input data", "SupportFunctions");
            return result;
        }

        String[] chunks = rawData.split(Constants.getListSeparator());

        for (String chunk : chunks) {
            result.add(BackupStrategyType.valueOf(BackupStrategyType.class, chunk));
        }

        return result;
    }

    public static Backup getLastWorkspaceBackup(Workspace workspace) {
        Logger.printApplicationLog("Get last workspace backup", "SupportFunctions");

        Backup last = null;

        List<Backup> allBackups = workspace.getBackups();

        for (Backup workspaceBackup : allBackups) {
            if (last == null) {
                last = workspaceBackup;
                continue;
            }

            if (last.getDescriptor() == null || workspaceBackup.getDescriptor() == null) {
                continue;
            }

            File lastDataFolder = last.getDescriptor().getBackup().getData();
            File currentDataFolder = workspaceBackup.getDescriptor().getBackup().getData();

            Instant lastDate = null;
            Instant currentDate = null;

            try {
                FileTime fileTime = Files.getLastModifiedTime(lastDataFolder.toPath());
                lastDate = fileTime.toInstant();
                FileTime fileTimeCurrent = Files.getLastModifiedTime(currentDataFolder.toPath());
                currentDate = fileTimeCurrent.toInstant();
            } catch (IOException e) {
                Logger.printApplicationLog("Cant parse backup edit time", "BackupStrategy");

                continue;
            }

            if (currentDate.isAfter(lastDate)) {
                last = workspaceBackup;
            }
        }

        return last;
    }

    public static boolean removeFilesAndFolders(File toRemove) {
        Logger.printApplicationLog("Remove files and folders", "SupportFunctions");

        boolean isRemoved = Constants.getBoolDefault();

        if (!toRemove.exists()) {
            return isRemoved;
        }

        if (!toRemove.isDirectory()) {
            isRemoved = toRemove.delete();
        } else {
            for (File childToRemove : toRemove.listFiles()) {
                if (!removeFilesAndFolders(childToRemove)) {
                    Logger.printApplicationLog("cant remove " + childToRemove.getAbsolutePath(), "SupportFunctions");
                    return isRemoved;
                }
            }

            isRemoved = !Constants.getBoolDefault();
        }

        return isRemoved;
    }

    public static long dateDiffStringToMilliseconds(String dateDiff) {
        Logger.printApplicationLog("Calculate date difference to milliseconds", "SupportFunctions");

        long dateDiffMs = Constants.getIntDefaultAlt();

        if (dateDiff == null || dateDiff.isEmpty()) {
            Logger.printApplicationLog("empty date difference", "SupportFunctions");
            return dateDiffMs;
        }

        String datePart = dateDiff.split(Constants.getSpaceRegexp())[0];
        String timePart = dateDiff.split(Constants.getSpaceRegexp())[1];

        String years = datePart.split(Constants.getChronoUnitsSeparator())[0];
        String months = datePart.split(Constants.getChronoUnitsSeparator())[1];
        String days = datePart.split(Constants.getChronoUnitsSeparator())[2];

        String timeDef = timePart.split(Constants.getTimeUnitsMillisSeparator())[0];

        String hours = timeDef.split(Constants.getTimeSeparator())[0];
        String minutes = timeDef.split(Constants.getTimeSeparator())[1];
        String seconds = timeDef.split(Constants.getTimeSeparator())[2];

        Logger.printApplicationLog(timePart, "DEBUG");

        String millis = "0";

        if (timePart.split(Constants.getTimeUnitsMillisSeparator()).length > 1) {
            millis = timePart.split(Constants.getTimeUnitsMillisSeparator())[1];
        }

        long yearsMilliseconds =
                Long.parseLong(years) * ChronoUnit.YEARS.getDuration().toMillis();
        long monthsMilliseconds =
                Long.parseLong(months) * ChronoUnit.MONTHS.getDuration().toMillis();
        long daysMilliseconds =
                Long.parseLong(days) * ChronoUnit.DAYS.getDuration().toMillis();
        long hoursMilliseconds = TimeUnit.HOURS.toMillis(Long.parseLong(hours));
        long minutesMilliseconds = TimeUnit.MINUTES.toMillis(Long.parseLong(minutes));
        long secondsMilliseconds = TimeUnit.SECONDS.toMillis(Long.parseLong(seconds));
        long milliseconds = Long.parseLong(millis);

        dateDiffMs = yearsMilliseconds
                + monthsMilliseconds
                + daysMilliseconds
                + hoursMilliseconds
                + minutesMilliseconds
                + secondsMilliseconds
                + milliseconds;

        return dateDiffMs;
    }

    public static BackupDescriptor createNewBackupDescriptor(Workspace workspace) {
        Logger.printApplicationLog("Create new backup descriptor", "SupportFunctions");

        BackupDescriptor result = null;

        WorkspaceDescriptor descriptor = null;
        try {
            descriptor = parseWorkspaceDescriptor(workspace, Config.getConfig());
        } catch (IOException e) {
            Logger.printApplicationLog("backup descriptor creating error", "SupportFunctions");
            Logger.printApplicationLog(e.getMessage(), "SupportFunctions");
        }

        List<File> filesToBackup = descriptor.getFilesToBackup();
        List<File> foldersToBackup = descriptor.getFoldersToBackup();
        boolean isSecured = descriptor.getBackupPassword() != null
                && !descriptor.getBackupPassword().isEmpty();

        boolean needDataCheck = !Constants.getBoolDefault();

        result = new BackupDescriptor(foldersToBackup, filesToBackup, isSecured, needDataCheck);

        return result;
    }

    public static List<Workspace> findWorkspaces(Config config) {
        Logger.printApplicationLog("Search workspaces", "SupportFunctions");

        List<Workspace> result = new ArrayList<Workspace>();

        if (config.getBackupsFolderPath() == null
                || config.getBackupsFolderPath().isEmpty()) {
            Logger.printApplicationLog("cant find workspaces to process", "SupportFunctions");
            nonCorrectExit();
        }

        File backupsFolder = new File(config.getBackupsFolderPath());

        if (!backupsFolder.exists()) {
            Logger.printApplicationLog("cant find backups folder", "SupportFunctions");
            nonCorrectExit();
        }

        for (File workspaceFolder : backupsFolder.listFiles()) {
            if (workspaceFolder.exists() && workspaceFolder.isDirectory()) {
                Workspace newWorkspace = new Workspace(workspaceFolder.getName());
                try {
                    WorkspaceDescriptor workspaceDescriptor =
                            parseWorkspaceDescriptor(newWorkspace, Config.getConfig());

                    if (workspaceDescriptor == null) {
                        continue;
                    }

                    newWorkspace.setWorkspaceDescriptor(workspaceDescriptor);
                    workspaceDescriptor.setWorkspace(newWorkspace);
                } catch (IOException e) {
                    ;
                }

                result.add(newWorkspace);
            }
        }

        return result;
    }

    public static WorkspaceDescriptor parseWorkspaceDescriptor(Workspace workspace, Config config) throws IOException {
        File backupsFolder = new File(config.getBackupsFolderPath());

        if (!backupsFolder.exists()) {
            Logger.printApplicationLog("cant find backups folder", "SupportFunctions");
            nonCorrectExit();
        }

        File foundedWorkspaceFolder = null;

        for (File workspaceFolder : backupsFolder.listFiles()) {
            if (workspaceFolder.exists()
                    && workspaceFolder.isDirectory()
                    && workspaceFolder.getName().equals(workspace.getName())) {
                foundedWorkspaceFolder = workspaceFolder;
            }
        }

        if (foundedWorkspaceFolder == null) {
            Logger.printApplicationLog("cant find workspace folder", "SupportFunctions");
            nonCorrectExit();
        }

        return parseWorkspaceDescriptorFile(foundedWorkspaceFolder, workspace);
    }

    private static WorkspaceDescriptor parseWorkspaceDescriptorFile(File workspaceFolder, Workspace workspace)
            throws IOException {
        WorkspaceDescriptor parsedDescriptor = null;

        Path pathToDescriptor = Path.of(workspaceFolder
                .toPath()
                .resolve(Constants.getConfigFilename())
                .toFile()
                .getAbsolutePath());

        if (!pathToDescriptor.toFile().exists()) {
            workspaceFolder.delete();
            return parsedDescriptor;
        }

        Properties properties = null;

        try (FileInputStream descripterFIS = new FileInputStream(pathToDescriptor.toString())) {
            InputStreamReader reader =
                    new InputStreamReader(descripterFIS, Config.getConfig().getSystemEncoding());
            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            Logger.printApplicationLog("cant read workspace descripter", "SupportFunctions");
        }

        String backupPassword =
                SupportFunctions.getStringProperty(properties, Constants.getPropertyNameBackupPassword());
        String backupDateDiff =
                SupportFunctions.getStringProperty(properties, Constants.getPropertyNameBackupDateDiff());
        boolean backupUseTimestamps =
                SupportFunctions.getBooleanProperty(properties, Constants.getPropertyNameBackupUseTimestamp());
        boolean backupUseVersion =
                SupportFunctions.getBooleanProperty(properties, Constants.getPropertyNameBackupUseVersion());
        String backupPreffix = SupportFunctions.getStringProperty(properties, Constants.getPropertyNameBackupPreffix());
        String backupPostfix = SupportFunctions.getStringProperty(properties, Constants.getPropertyNameBackupPostfix());
        boolean backupInArchive =
                SupportFunctions.getBooleanProperty(properties, Constants.getPropertyNameBackupInArchive());
        String foldersToBackup =
                SupportFunctions.getStringProperty(properties, Constants.getPropertyNameFoldersToBackup());
        String filesToBackup = SupportFunctions.getStringProperty(properties, Constants.getPropertyNameFilesToBackup());
        String rotateAfterValue =
                SupportFunctions.getStringProperty(properties, Constants.getPropertyNameRotateAfterCount());

        int rotateAfter = 0;

        try {
            rotateAfter = Integer.parseInt(rotateAfterValue);
        } catch (NumberFormatException e) {
            ;
        }

        List<File> folders = listOfPathsToListOfFiles(listRepresentationToList(foldersToBackup));
        List<File> files = listOfPathsToListOfFiles(listRepresentationToList(filesToBackup));

        String backupsStrategyTypes =
                SupportFunctions.getStringProperty(properties, Constants.getPropertyNameBackupsStrategyTypes());

        parsedDescriptor = new WorkspaceDescriptor(
                backupPassword,
                backupDateDiff,
                backupUseTimestamps,
                backupUseVersion,
                backupPreffix,
                backupPostfix,
                backupInArchive,
                folders,
                files,
                backupsStrategyTypes,
                workspace,
                rotateAfter);

        return parsedDescriptor;
    }
}

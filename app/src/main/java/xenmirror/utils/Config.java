package xenmirror.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
    private boolean logApp;
    private boolean useLAF;
    private boolean useDark;
    private String encoding;
    private String filesCheckDelayMs;
    private String trayShowTimeMs;

    private String backupsFolderPath;

    private String systemEncoding;

    private Path pathToConfig;

    private static volatile Config instance;

    private Config() throws IOException, FileNotFoundException {
        this.systemEncoding = System.getProperty(Constants.getSystemEncodingProperty());

        String configParentFolder = System.getProperty(Constants.getConfigParentFolderName());

        this.pathToConfig = Path.of(Path.of(configParentFolder, Constants.getConfigFilename())
                .toFile()
                .getAbsolutePath());

        if (!this.pathToConfig.toFile().exists()) {
            throw new FileNotFoundException(Constants.getConfigNotFoundMessage() + this.pathToConfig.toString());
        }

        FileInputStream configFIS = new FileInputStream(this.pathToConfig.toString());
        InputStreamReader reader = new InputStreamReader(configFIS, this.systemEncoding);
        Properties properties = new Properties();
        properties.load(reader);

        this.filesCheckDelayMs =
                SupportFunctions.getStringProperty(properties, Constants.getPropertyNameFilesCheckDelay());
        this.trayShowTimeMs = SupportFunctions.getStringProperty(properties, Constants.getPropertyNameTrayShowTimeMs());
        this.encoding = SupportFunctions.getStringProperty(properties, Constants.getPropertyNameEncoding());
        this.logApp = SupportFunctions.getBooleanProperty(properties, Constants.getPropertyNameLogApp());
        this.useLAF = SupportFunctions.getBooleanProperty(properties, Constants.getPropertyNameLafIsNeeded());
        this.useDark = SupportFunctions.getBooleanProperty(properties, Constants.getPropertyNameDarkThemeIsNeeded());
        this.backupsFolderPath =
                SupportFunctions.getStringProperty(properties, Constants.getPropertyNameBackupFolderPath());

        if (this.encoding != null && !this.encoding.isEmpty()) {
            this.systemEncoding = encoding;
        }

        if (this.filesCheckDelayMs == null || this.filesCheckDelayMs.isEmpty()) {
            this.filesCheckDelayMs = Long.toString(Constants.getBackupCheckerDelayMs());
        }

        if (this.trayShowTimeMs == null || this.trayShowTimeMs.isEmpty()) {
            this.trayShowTimeMs = Long.toString(Constants.getTrayHideDelay());
        }
    }

    public static Config getConfig() {
        if (instance == null) {
            try {
                instance = new Config();
            } catch (IOException e) {
                System.out.println("SupportFunctions: config initialize error");
                System.out.println(e.getMessage());
            }
        }

        return instance;
    }

    public void save() throws IOException {
        String configParentFolder = System.getProperty(Constants.getConfigParentFolderName());

        this.pathToConfig = Path.of(Path.of(configParentFolder, Constants.getConfigFilename())
                .toFile()
                .getAbsolutePath());

        if (!this.pathToConfig.toFile().exists()) {
            throw new FileNotFoundException(Constants.getConfigNotFoundMessage() + this.pathToConfig.toString());
        }

        FileOutputStream configFOS = new FileOutputStream(this.pathToConfig.toString());
        OutputStreamWriter writer = new OutputStreamWriter(configFOS, this.systemEncoding);
        Properties properties = new Properties();

        SupportFunctions.setStringProperty(properties, Constants.getPropertyNameFilesCheckDelay(), filesCheckDelayMs);
        SupportFunctions.setStringProperty(properties, Constants.getPropertyNameTrayShowTimeMs(), trayShowTimeMs);
        SupportFunctions.setStringProperty(properties, Constants.getPropertyNameEncoding(), encoding);
        SupportFunctions.setBooleanProperty(properties, Constants.getPropertyNameLogApp(), logApp);
        SupportFunctions.setBooleanProperty(properties, Constants.getPropertyNameLafIsNeeded(), useLAF);
        SupportFunctions.setBooleanProperty(properties, Constants.getPropertyNameDarkThemeIsNeeded(), useDark);
        SupportFunctions.setStringProperty(properties, Constants.getPropertyNameBackupFolderPath(), backupsFolderPath);

        properties.store(writer, Constants.getTextDefault());
        configFOS.flush();
        configFOS.close();
    }

    public boolean isLogApp() {
        return this.logApp;
    }

    public boolean isUseLAF() {
        return this.useLAF;
    }

    public boolean isUseDark() {
        return this.useDark;
    }

    public void setLogApp(boolean logApp) {
        this.logApp = logApp;
    }

    public void setUseLAF(boolean useLAF) {
        this.useLAF = useLAF;
    }

    public void setUseDark(boolean useDark) {
        this.useDark = useDark;
    }

    public String getBackupsFolderPath() {
        return this.backupsFolderPath;
    }

    public void setBackupsFolderPath(String backupsFolderPath) {
        this.backupsFolderPath = backupsFolderPath;
    }

    public String getSystemEncoding() {
        return this.systemEncoding;
    }

    public String getFilesCheckDelayMs() {
        return filesCheckDelayMs;
    }

    public void setFilesCheckDelayMs(String filesCheckDelayMs) {
        this.filesCheckDelayMs = filesCheckDelayMs;
    }

    public String getTrayShowTimeMs() {
        return trayShowTimeMs;
    }

    public void setTrayShowTimeMs(String trayShowTimeMs) {
        this.trayShowTimeMs = trayShowTimeMs;
    }
}

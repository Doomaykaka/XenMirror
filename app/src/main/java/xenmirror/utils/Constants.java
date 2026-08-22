package xenmirror.utils;

public class Constants {
    // App
    private static final String APP_NAME = "XenMirror";
    private static final String TEXT_DEFAULT = "";
    private static final String SPACE = " ";
    private static final String SPACE_REGEXP = "\s";
    private static final Boolean BOOL_DEFAULT = false;
    private static final int INT_DEFAULT = -1;
    private static final int INT_DEFAULT_ALT = 0;
    private static final int START_INDEX = 0;
    private static final int SIZE_TO_INDEX_OFFSET = -1;
    private static final String LOG_FILE_PARENT_FOLDER_NAME = "user.dir";
    private static final String LOG_FILENAME = "application.log";
    private static final String NEW_LINE = "\n";
    private static final String NEW_LINE_OLD_SEPARATOR = "/nl";
    private static final String LOG_FILES_DELIMETER = "-------------------------------------";
    private static final String BACKUP_DESCRIPTION_FILE_EXT = ".desc";
    private static final String BACKUP_ARCHIVE_FILE_EXT = ".zip";
    private static final String LIST_SEPARATOR = ";";
    private static final String TIME_SEPARATOR = ":";
    private static final String FS_SEPARATOR = "-";
    private static final int CORRECT_EXIT_CODE = 0;
    private static final int NON_CORRECT_EXIT_CODE = 0;
    private static final String CHRONO_UNITS_SEPARATOR = "-";
    private static final String TIME_UNITS_MILLIS_SEPARATOR = "\\.";
    private static final String WRITE_FILE_FLAGS = "rw";
    private static final String SYSTEM_ENCODING_PROPERTY = "file.encoding";

    // Config
    private static final String CONFIG_PARENT_FOLDER_NAME = "user.dir";
    private static final String CONFIG_FILENAME = "settings.conf";
    private static final String CONFIG_NOT_FOUND_MESSAGE = "Config not found. Please create config file with path:";
    private static final String CONFIG_PARSE_EXCEPTION_MESSAGE_PREFIX = "Param ";
    private static final String CONFIG_PARSE_EXCEPTION_MESSAGE_POSTFIX = " not parsed";
    private static final String PROPERTY_NAME_FILES_CHECK_DELAY = "files-check-delay";
    private static final String PROPERTY_NAME_TRAY_SHOW_TIME_MS = "tray-show-time-ms";
    private static final String PROPERTY_NAME_ENCODING = "encoding";
    private static final String PROPERTY_NAME_LOG_APP = "log-app";
    private static final String PROPERTY_NAME_LAF_IS_NEEDED = "laf-is-needed";
    private static final String PROPERTY_NAME_DARK_THEME_IS_NEEDED = "dark-is-needed";
    private static final String PROPERTY_NAME_RUSSIAN_LANGUAGE_IS_NEEDED = "russian-is-needed";
    private static final String PROPERTY_NAME_BACKUP_PASSWORD = "backup-password";
    private static final String PROPERTY_NAME_BACKUP_DATE_DIFF = "backup-date-diff";
    private static final String PROPERTY_NAME_BACKUP_FOLDER_PATH = "backup-folder-path";
    private static final String PROPERTY_NAME_BACKUP_USE_TIMESTAMP = "backup-use-timestamp";
    private static final String PROPERTY_NAME_BACKUP_USE_VERSION = "backup-use-version";
    private static final String PROPERTY_NAME_BACKUP_PREFFIX = "backup-prefix";
    private static final String PROPERTY_NAME_BACKUP_POSTFIX = "backup-postfix";
    private static final String PROPERTY_NAME_BACKUP_IN_ARCHIVE = "backup-in-archive";
    private static final String PROPERTY_NAME_BACKUPS_STRATEGY_TYPES = "backups-strategy-types";
    private static final String PROPERTY_NAME_FILES_TO_BACKUP = "files-to-backup";
    private static final String PROPERTY_NAME_FOLDERS_TO_BACKUP = "folders-to-backup";
    private static final String PROPERTY_NAME_ROTATE_AFTER_COUNT = "rotate-after";
    private static final String CONFIG_DEFAULT_STRING_PROPERTIES_VALUES = "";
    private static final Boolean CONFIG_DEFAULT_BOOLEAN_PROPERTIES_VALUES = false;
    private static final int CONFIG_DEFAULT_ROTATE_AFTER_VALUE = 100;
    private static final String CONFIG_FOLDER_NOT_FOUND_MESSAGE =
            "Config folder not found. Please create config folder with path:";
    private static final String CONFIG_DATE_FORMAT_DEFAULT = "EEE MMM dd HH:mm:ss zzz yyyy";

    // Backup
    private static final String BACKUP_NAME_PARTS_SEPARATOR = "-";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_FILEPATHS = "descriptor-filepaths";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_FOLDERPATHS = "descriptor-folderpaths";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_CHANGESTAMP = "descriptor-changestamp";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_FILES_COUNT = "descriptor-files-count";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_FOLDERS_COUNT = "descriptor-folders-count";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_FILES_COUNT_TOTAL = "descriptor-files-count-total";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_FOLDERS_COUNT_TOTAL = "descriptor-folders-count-total";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_IS_SECURED = "descriptor-is-secured";
    private static final String BACKUP_DESCRIPTOR_PROPERTY_NAME_VERSION = "descriptor-version";

    private static final String WORKSPACE_DESCRIPTER_NOT_FOUND_MESSAGE =
            "Workspace descripter not found. Please create descripter file with path:";

    // Controller
    private static final long BACKUP_CHECKER_DELAY_MS = 10000;

    // GUI
    private static final String GUI_IMAGE_ICON_RESOURCE_PATH = "/images/app_icon.jpg";
    private static final long TRAY_HIDE_DELAY = 4000;
    private static final String DEFAULT_DATE_VALUE = "0-0-0 00\\:00\\:00";

    public static String getAppName() {
        return APP_NAME;
    }

    public static String getTextDefault() {
        return TEXT_DEFAULT;
    }

    public static String getSpace() {
        return SPACE;
    }

    public static Boolean getBoolDefault() {
        return BOOL_DEFAULT;
    }

    public static int getIntDefault() {
        return INT_DEFAULT;
    }

    public static int getIntDefaultAlt() {
        return INT_DEFAULT_ALT;
    }

    public static int getStartIndex() {
        return START_INDEX;
    }

    public static int getSizeToIndexOffset() {
        return SIZE_TO_INDEX_OFFSET;
    }

    public static String getLogFileParentFolderName() {
        return LOG_FILE_PARENT_FOLDER_NAME;
    }

    public static String getLogFilename() {
        return LOG_FILENAME;
    }

    public static String getNewLine() {
        return NEW_LINE;
    }

    public static String getNewLineOldSeparator() {
        return NEW_LINE_OLD_SEPARATOR;
    }

    public static String getLogFilesDelimeter() {
        return LOG_FILES_DELIMETER;
    }

    public static String getConfigParentFolderName() {
        return CONFIG_PARENT_FOLDER_NAME;
    }

    public static String getConfigFilename() {
        return CONFIG_FILENAME;
    }

    public static String getConfigNotFoundMessage() {
        return CONFIG_NOT_FOUND_MESSAGE;
    }

    public static String getConfigParseExceptionMessagePrefix() {
        return CONFIG_PARSE_EXCEPTION_MESSAGE_PREFIX;
    }

    public static String getConfigParseExceptionMessagePostfix() {
        return CONFIG_PARSE_EXCEPTION_MESSAGE_POSTFIX;
    }

    public static String getPropertyNameLogApp() {
        return PROPERTY_NAME_LOG_APP;
    }

    public static String getPropertyNameLafIsNeeded() {
        return PROPERTY_NAME_LAF_IS_NEEDED;
    }

    public static String getPropertyNameDarkThemeIsNeeded() {
        return PROPERTY_NAME_DARK_THEME_IS_NEEDED;
    }

    public static String getPropertyNameRussianLanguageIsNeeded() {
        return PROPERTY_NAME_RUSSIAN_LANGUAGE_IS_NEEDED;
    }

    public static String getConfigDefaultStringPropertiesValues() {
        return CONFIG_DEFAULT_STRING_PROPERTIES_VALUES;
    }

    public static Boolean getConfigDefaultBooleanPropertiesValues() {
        return CONFIG_DEFAULT_BOOLEAN_PROPERTIES_VALUES;
    }

    public static String getPropertyNameBackupPassword() {
        return PROPERTY_NAME_BACKUP_PASSWORD;
    }

    public static String getPropertyNameBackupDateDiff() {
        return PROPERTY_NAME_BACKUP_DATE_DIFF;
    }

    public static String getPropertyNameBackupFolderPath() {
        return PROPERTY_NAME_BACKUP_FOLDER_PATH;
    }

    public static String getPropertyNameBackupUseTimestamp() {
        return PROPERTY_NAME_BACKUP_USE_TIMESTAMP;
    }

    public static String getPropertyNameBackupUseVersion() {
        return PROPERTY_NAME_BACKUP_USE_VERSION;
    }

    public static String getPropertyNameBackupPreffix() {
        return PROPERTY_NAME_BACKUP_PREFFIX;
    }

    public static String getPropertyNameBackupPostfix() {
        return PROPERTY_NAME_BACKUP_POSTFIX;
    }

    public static String getBackupNamePartsSeparator() {
        return BACKUP_NAME_PARTS_SEPARATOR;
    }

    public static String getBackupDescriptionFileExt() {
        return BACKUP_DESCRIPTION_FILE_EXT;
    }

    public static String getBackupArchiveFileExt() {
        return BACKUP_ARCHIVE_FILE_EXT;
    }

    public static String getPropertyNameBackupInArchive() {
        return PROPERTY_NAME_BACKUP_IN_ARCHIVE;
    }

    public static String getBackupDescriptorPropertyNameFilepaths() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_FILEPATHS;
    }

    public static String getBackupDescriptorPropertyNameFolderpaths() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_FOLDERPATHS;
    }

    public static String getBackupDescriptorPropertyNameChangeStamp() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_CHANGESTAMP;
    }

    public static String getBackupDescriptorPropertyNameFilesCount() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_FILES_COUNT;
    }

    public static String getBackupDescriptorPropertyNameFoldersCount() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_FOLDERS_COUNT;
    }

    public static String getBackupDescriptorPropertyNameFilesCountTotal() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_FILES_COUNT_TOTAL;
    }

    public static String getBackupDescriptorPropertyNameFoldersCountTotal() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_FOLDERS_COUNT_TOTAL;
    }

    public static String getBackupDescriptorPropertyNameIsSecured() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_IS_SECURED;
    }

    public static String getBackupDescriptorPropertyNameVersion() {
        return BACKUP_DESCRIPTOR_PROPERTY_NAME_VERSION;
    }

    public static String getListSeparator() {
        return LIST_SEPARATOR;
    }

    public static String getSpaceRegexp() {
        return SPACE_REGEXP;
    }

    public static int getCorrectExitCode() {
        return CORRECT_EXIT_CODE;
    }

    public static int getNonCorrectExitCode() {
        return NON_CORRECT_EXIT_CODE;
    }

    public static String getPropertyNameBackupsStrategyTypes() {
        return PROPERTY_NAME_BACKUPS_STRATEGY_TYPES;
    }

    public static long getBackupCheckerDelayMs() {
        return BACKUP_CHECKER_DELAY_MS;
    }

    public static String getConfigFolderNotFoundMessage() {
        return CONFIG_FOLDER_NOT_FOUND_MESSAGE;
    }

    public static String getPropertyNameFilesToBackup() {
        return PROPERTY_NAME_FILES_TO_BACKUP;
    }

    public static String getPropertyNameFoldersToBackup() {
        return PROPERTY_NAME_FOLDERS_TO_BACKUP;
    }

    public static String getTimeSeparator() {
        return TIME_SEPARATOR;
    }

    public static String getFsSeparator() {
        return FS_SEPARATOR;
    }

    public static String getConfigDateFormatDefault() {
        return CONFIG_DATE_FORMAT_DEFAULT;
    }

    public static String getChronoUnitsSeparator() {
        return CHRONO_UNITS_SEPARATOR;
    }

    public static String getTimeUnitsMillisSeparator() {
        return TIME_UNITS_MILLIS_SEPARATOR;
    }

    public static String getWriteFileFlags() {
        return WRITE_FILE_FLAGS;
    }

    public static String getGuiImageIconResourcePath() {
        return GUI_IMAGE_ICON_RESOURCE_PATH;
    }

    public static String getSystemEncodingProperty() {
        return SYSTEM_ENCODING_PROPERTY;
    }

    public static String getPropertyNameEncoding() {
        return PROPERTY_NAME_ENCODING;
    }

    public static long getTrayHideDelay() {
        return TRAY_HIDE_DELAY;
    }

    public static String getPropertyNameFilesCheckDelay() {
        return PROPERTY_NAME_FILES_CHECK_DELAY;
    }

    public static String getPropertyNameTrayShowTimeMs() {
        return PROPERTY_NAME_TRAY_SHOW_TIME_MS;
    }

    public static String getWorkspaceDescripterNotFoundMessage() {
        return WORKSPACE_DESCRIPTER_NOT_FOUND_MESSAGE;
    }

    public static String getPropertyNameRotateAfterCount() {
        return PROPERTY_NAME_ROTATE_AFTER_COUNT;
    }

    public static int getConfigDefaultRotateAfterValue() {
        return CONFIG_DEFAULT_ROTATE_AFTER_VALUE;
    }

    public static String getDefaultDateValue() {
        return DEFAULT_DATE_VALUE;
    }
}

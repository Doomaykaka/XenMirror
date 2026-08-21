package xenmirror.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Logger {
    private static final String LOG_MESSAGE_PREFFIX = "<";
    private static final String LOG_MESSAGE_POSTFIX = "> - ";
    private static final String LOG_START_MESSAGE_POSTFIX_MODIFIER = "Log started";
    private static final String LOG_SOURCE_MESSAGE_POSTFIX_START_MODIFIER = "[";
    private static final String LOG_SOURCE_MESSAGE_POSTFIX_END_MODIFIER = "] ";

    private static volatile String appLogPath;

    public static synchronized void printApplicationLog(String message, String source) {
        if (!Config.getConfig().isLogApp()) {
            return;
        }

        PrintWriter printWriter = null;
        boolean isNew = false;

        isNew = appLogPath == null;

        if (isNew) {
            appLogPath = prepareAppLogPath();
        }

        if (appLogPath != null) {
            try {
                boolean append = true;
                printWriter = new PrintWriter(new FileOutputStream(appLogPath, append));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (printWriter != null && isNew) {
            printWriter.write(Constants.getLogFilesDelimeter() + Constants.getNewLine());
            printWriter.write(LOG_MESSAGE_PREFFIX
                    + LocalDateTime.now()
                    + LOG_MESSAGE_POSTFIX
                    + LOG_START_MESSAGE_POSTFIX_MODIFIER
                    + Constants.getNewLine());
        }

        if (printWriter != null && message != null && source != null) {
            printWriter.write(LOG_MESSAGE_PREFFIX
                    + LocalDateTime.now()
                    + LOG_MESSAGE_POSTFIX
                    + LOG_SOURCE_MESSAGE_POSTFIX_START_MODIFIER
                    + source
                    + LOG_SOURCE_MESSAGE_POSTFIX_END_MODIFIER
                    + message
                    + Constants.getNewLine());
        }

        if (printWriter != null && message != null && source == null) {
            printWriter.write(
                    LOG_MESSAGE_PREFFIX + LocalDateTime.now() + LOG_MESSAGE_POSTFIX + message + Constants.getNewLine());
        }

        printWriter.close();
        printWriter = null;
    }

    private static synchronized String prepareAppLogPath() {
        String pathToAppLogFile = Constants.getTextDefault();

        File userDirectory = new File(System.getProperty(Constants.getLogFileParentFolderName()));
        Path logFilePathObj = Path.of(userDirectory.getAbsolutePath().toString(), Constants.getLogFilename());

        File appLogFile = logFilePathObj.toFile();

        if (!appLogFile.exists()) {
            try {
                appLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pathToAppLogFile = appLogFile.getAbsolutePath();

        return pathToAppLogFile;
    }
}

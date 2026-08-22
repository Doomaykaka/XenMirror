package xenmirror;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import xenmirror.controllers.BackupController;
import xenmirror.gui.Tray;
import xenmirror.models.Workspace;
import xenmirror.utils.Config;
import xenmirror.utils.Constants;
import xenmirror.utils.Logger;
import xenmirror.utils.SupportFunctions;

public class App {
    private static boolean isClosed = Constants.getBoolDefault();

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        Logger.printApplicationLog("App started", "App");

        Config appConfig = Config.getConfig();

        initAppGUI();

        List<Workspace> workspaces = SupportFunctions.findWorkspaces(appConfig);

        BackupController controller = new BackupController(workspaces);
        controller.runBackupCheckerLoop();

        Tray tray = new Tray(controller);
        tray.show();
    }

    private static void initAppGUI() {
        if (Config.getConfig().isUseLAF()) {
            FlatIntelliJLaf.setup();
            try {
                if (Config.getConfig().isUseDark()) {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } else {
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                }
            } catch (UnsupportedLookAndFeelException e) {
                Logger.printApplicationLog("GUI style setup error", "Tray");
                Logger.printApplicationLog(e.getMessage(), "Tray");
                e.printStackTrace();
            }
        }
    }

    public static boolean isClosed() {
        return isClosed;
    }

    public static void setIsClosed(boolean isClosed) {
        App.isClosed = isClosed;
    }
}

package xenmirror;

import java.util.List;
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
        List<Workspace> workspaces = SupportFunctions.findWorkspaces(appConfig);

        BackupController controller = new BackupController(workspaces);

        Tray tray = new Tray(controller);
        tray.show();
    }

    public static boolean isClosed() {
        return isClosed;
    }

    public static void setIsClosed(boolean isClosed) {
        App.isClosed = isClosed;
    }
}

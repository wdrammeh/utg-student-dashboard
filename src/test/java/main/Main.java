package main;

import core.Board;
import core.utils.App;
import core.utils.Globals;
import core.utils.MDate;
import utg.Dashboard;

public class Main {
    // Should never equal Dashboard.getDefaultPath
    private static final String TEST_PATH = Globals.joinPaths(Globals.userHome(), "dashboard-test");


    public static void main(String[] args) {
        System.out.println(MDate.formatNow());
        run(TEST_PATH, true);
    }

    private static void run(String path, boolean ser) {
        App.silenceInfo("Dashboard running on test mode; where path = '"+path+"'.");
        Dashboard.main(new String[]{path});
        if (!ser) {
            Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_HOOK);
            App.silenceWarning("No changes will be saved for this session.");
        }
    }

}

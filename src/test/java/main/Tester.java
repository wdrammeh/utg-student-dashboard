package main;

import core.Board;
import core.utils.App;
import core.utils.Globals;
import core.utils.MDate;
import utg.Dashboard;

public class Tester {
    // Should not equal Dashboard.getDefaultPath
    private static final String TEST_PATH = Globals.joinPaths(Globals.userHome(), "utgsd-test");


    public static void main(String[] args) {
        System.out.println(MDate.formatNow());
        run(TEST_PATH, true);
    }

    private static void run(String path, boolean ser) {
        App.silenceInfo("Dashboard is running on test mode; where path = '"+path+"'.");
        Dashboard.main(new String[]{path});
        if (!ser) {
            Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_HOOK);
            App.silenceWarning("No changes will be saved for this session.");
        }
    }

}

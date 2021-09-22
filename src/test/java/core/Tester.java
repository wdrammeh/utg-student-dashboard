package core;

import core.utils.App;
import core.utils.Globals;
import core.utils.MDate;
import utg.Dashboard;

public class Tester {

    public static void main(String[] args) {
        System.out.println(MDate.formatNow());
        launch(Globals.joinPaths(Globals.userHome(), "dashboard"), true);
    }

    private static void launch(String path, boolean ser){
        App.silenceInfo("Dashboard running on test mode; where path = '"+path+"'.");
        Dashboard.main(new String[]{path});
        if (!ser) {
            Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_HOOK);
            App.silenceWarning("No changes will be saved for this session.");
        }
    }

}

package core;

import core.utils.App;
import core.utils.MDate;
import utg.Dashboard;

public class Tester {

    public static void main(String[] args) {
        System.out.println(MDate.formatNow());
    }

    /**
     * Run Dashboard without the "Collapse Sequence".
     */
    private static void run(boolean save) {
        Dashboard.main(null);
        if (!save) {
            Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_HOOK);
            App.silenceWarning("Running in Safe Mode? No changes will be saved for this run.");
        }
    }

}

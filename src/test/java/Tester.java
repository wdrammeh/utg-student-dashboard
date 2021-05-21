import core.Board;
import utg.Dashboard;

public class Tester {

    public static void main(String[] args) {
        Dashboard.storeConfigs();
        System.exit(0);
    }

    /**
     * Run Dashboard without the "Collapse Sequence".
     */
    private static void run(boolean save) {
        Dashboard.main(null);
        if (!save) {
            Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_HOOK);
        }
    }

}

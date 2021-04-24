import core.Board;
import core.News;
import core.Portal;
import core.alert.Notification;
import core.module.ModuleHandler;
import core.module.SemesterActivity;
import core.setting.Settings;
import core.task.TaskActivity;
import core.user.Student;
import utg.Dashboard;

import java.util.Date;

public class Tester {

    public static void main(String[] args) {
//        Student.initialize();
    }

    private static void run(boolean save){
        Dashboard.main(null);
        if (save) {
            Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_HOOK);
        }
    }

}

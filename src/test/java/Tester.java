import core.News;
import core.Portal;
import core.alert.Notification;
import core.first.Welcome;
import core.module.ModuleHandler;
import core.module.RunningCourseActivity;
import core.setting.Settings;
import core.task.TaskActivity;
import core.user.Student;

import java.util.Date;

public class Tester {

    public static void main(String[] args) {
        System.out.println(new Date());
        new Welcome().setVisible(true);
    }

    /**
     * Loads up all serializable matter from the disk.
     * Might be significantly slow.
     */
    public static void loadEverything(){
        Student.initialize();
        new ModuleHandler();
        Settings.deserialize();
        Portal.deSerialize();
        RunningCourseActivity.deserializeModules();
        TaskActivity.deSerializeAll();
        Notification.deSerialize();
        new News();
    }

}

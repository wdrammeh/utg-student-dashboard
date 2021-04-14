import core.News;
import core.Portal;
import core.alert.Notification;
import core.module.ModuleHandler;
import core.module.RunningCourseActivity;
import core.setting.Settings;
import core.task.TaskActivity;
import core.user.Student;

import java.util.Date;

public class Tester {

    public static void main(String[] args) {
        System.out.println(new Date());

    }

    /**
     * Loads up all serializable matter from the disk.
     */
    public static void loadEverything(){
        Student.initialize();
        final ModuleHandler moduleHandler = new ModuleHandler();
        Settings.deserialize();
        Portal.deSerialize();
        RunningCourseActivity.deserializeModules();
        TaskActivity.deSerializeAll();
        Notification.deSerialize();
        final News news = new News();
    }

}

import core.News;
import core.Portal;
import core.alert.Notification;
import core.module.ModuleHandler;
import core.module.RunningCourseActivity;
import core.setting.Settings;
import core.task.TaskSelf;
import core.user.Student;

import java.util.Date;

/**
 * This type is intended for testing a specific component / functionality of the project.
 * This class becomes useful when testing: for instance, how accurate are some computations?
 * Or how exactly are some components rendered before loading them up with the entire project.
 * E.g., to see how the Preview window looks like, use something like:
 *  SwingUtilities.invokeLater(()-> new Preview(null).setVisible(true));
 *
 * In whatever case, developer is assumed to be working on that particular side of the project.
 */
public class Tester {

    public static void main(String[] args) {
        System.out.println(new Date());
//        loadContent();


    }

    public static void loadContent(){
        Student.initialize();
        new ModuleHandler();
        Settings.deserialize();
        Portal.deSerialize();
        RunningCourseActivity.deserializeModules();
        TaskSelf.deSerializeAll();
        Notification.deSerialize();
        new News();
    }

}

import core.user.Student;

import java.util.Date;

public class Tester {

    public static void main(String[] args) {
        System.out.println(new Date());
        loadContent();
        System.out.println(Student.getTelephones());
    }

    public static void loadContent(){
        Student.initialize();
//        new ModuleHandler();
//        Settings.deserialize();
//        Portal.deSerialize();
//        RunningCourseActivity.deserializeModules();
//        TaskCentral.deSerializeAll();
//        Notification.deSerialize();
//        new News();
    }

}

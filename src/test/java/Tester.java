import core.Board;
import core.module.SemesterActivity;
import utg.Dashboard;

public class Tester {

    public static void main(String[] args) {
        new SemesterActivity.RegisteredCourseAdder().setVisible(true);
        new ModuleHandler.ModuleAdder("2016/2017", Student.FIRST_SEMESTER).setVisible(true);
    }

    private static void run(boolean save){
        Dashboard.main(null);
        if (!save) {
            Runtime.getRuntime().removeShutdownHook(Board.SHUT_DOWN_HOOK);
        }
    }

}

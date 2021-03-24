import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import core.Course;
import core.ModuleHandler;
import core.Student;

public class XStreamTest {

    public static void main(String[] args) {
        final DomDriver driver = new DomDriver("UTF-8");
        final XStream xStream = new XStream(driver);
        xStream.addPermission(AnyTypePermission.ANY);

        Student.initialize();
        new ModuleHandler();

        final Course course = ModuleHandler.getMonitor().get(0);
        final String xml = xStream.toXML(course);
//        System.out.println(xml);

        final String fullXml = String.join(System.getProperty("line.separator"),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", xml);
        System.out.println(fullXml);

        final Course course1 = (Course) xStream.fromXML(fullXml);
        System.out.println(xStream.toXML(course1));
    }

}

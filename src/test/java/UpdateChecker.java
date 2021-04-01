import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class UpdateChecker {

    public static void main(String[] args) {
        final Document doc;
        try {
            doc = Jsoup.connect("https://github.com/w-drammeh/utg-dashboard").get();
            final Element version = doc.selectFirst(".markdown-body > p:nth-child(2) > code:nth-child(1)");
            System.out.println(version.text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

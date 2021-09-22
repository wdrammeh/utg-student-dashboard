package core;

import core.utils.Globals;
import core.utils.FontFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static core.utils.Globals.reference;

public class Help implements Activity {
    private boolean isFirstView = true;
    private KScrollPane tipPane, faqPane;


    public Help(){
        final CardLayout helpCard = new CardLayout();
        final KPanel centerPanel = new KPanel(helpCard);

        final KLabel showingLabel = new KLabel("Dashboard Tips", FontFactory.BODY_HEAD_FONT);
        final KComboBox<String> helpBox = new KComboBox<>(new String[] {"Dashboard Tips", "UTG FAQs"});
        helpBox.setFocusable(false);
        helpBox.addActionListener(e-> {
            final int selectionInt = helpBox.getSelectedIndex();
            if (selectionInt == 0) {
                helpCard.show(centerPanel, "tips");
                showingLabel.setText("Dashboard Tips");
            } else if (selectionInt == 1) {
                helpCard.show(centerPanel, "faqs");
                showingLabel.setText("UTG FAQs & Answers");
            }
        });

        final KPanel northPanel = new KPanel(new BorderLayout());
        northPanel.add(new KPanel(showingLabel), BorderLayout.CENTER);
        northPanel.add(new KPanel(helpBox), BorderLayout.EAST);

        helpCard.addLayoutComponent(centerPanel.add(getTips()), "tips");
        helpCard.addLayoutComponent(centerPanel.add(getFAQs()), "faqs");

        final KPanel helpActivity = new KPanel(new BorderLayout());
        helpActivity.add(northPanel, BorderLayout.NORTH);
        helpActivity.add(centerPanel, BorderLayout.CENTER);

        Board.addCard(helpActivity, "Help");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Help");
        if (isFirstView) {
            SwingUtilities.invokeLater(()-> {
                tipPane.toTop();
                faqPane.toTop();
            });
            isFirstView = false;
        }
    }

    private Component getTips(){
        final String tipText = heading("Running Courses") +
                "Dashboard provides a mechanism for you to effectively keep track of the courses you " +
                "register for every semester. Go to <i>"+reference("Home", "This Semester")+"</i>. " +
                "Temporarily, you can add courses to the table which you should <b>Verify</b> later." +
                "However, it is recommended that you register all your courses on the portal first, " +
                "and let Dashboard synchronize them. To achieve this, click <b>More Options</b> " +
                "and select <b>Match Portal</b>. For more options, right-click a course on the table. " +
                "Courses your register can automatically be imported when you create a Task, or an Assignment." +
                subHeading("Checkout") +
                "If a course is <i>not verified</i>, Dashboard will indicate '"+ Globals.UNKNOWN+"' " +
                "on its status column. This means Dashboard does not know whether you've registered " +
                "it or not. In this case, you have to <i>check it out</i>. To do that, right-click on " +
                "the course and select <b>Checkout</b> from the menu. " +
                "Once a course is <b>verified</b> you can only change its schedule. For more info, " +
                "See Module Verification." +
                subHeading("Match") +
                "This process lets you match your local registration table with your Portal by bringing all " +
                "the courses you've registered this semester. Click 'More options > Match Portal'."+
                heading("Module Collection") +
                "At "+reference("Home", "Module Collection")+", Dashboard does not miss a course " +
                "you've treated! The courses are arranged according to their respective years and semesters, " +
                "and so are they added to the tables in the same manner. " +
                "Again, you can add your own courses here but they'll <b>not</b> be included in your Analysis " +
                "(and Transcript) until they're confirmed on the Portal. " +
                "To add a course, right-click on the respective table and select <i>Add Course</i> from the menu." +
                subHeading("Synchronization") +
                "Timely, you can try checking for new added courses on your Portal by clicking " +
                reference("Home", "Module Collection", "Sync")+". " +
                "While importing newly added courses from your Portal, this action may verify " +
                "unverified courses as well. In a nutshell, for every course on the Portal, " +
                "if it corresponds to a local course and the local course is verified, nothing is done; " +
                "but if the local course is not verified, it'll be substituted with the one on the portal; " +
                "otherwise the course is added directly if it corresponds to no local course." +
                subHeading("Verification") +
                "To launch verification for a course that you've added locally, right-click on it and select " +
                "<i>Verify</i> from the Popup Menu. Among the details you provide, Dashboard uses only the " +
                "<i>Course Code</i> in the verification process. If no course on your Portal resembles the " +
                "code then the operation is unsuccessful, otherwise the course is confirmed-set. " +
                "If the course is confirmed, it'll get substituted with the one found on the Portal " +
                "thus re-writing all its details. This may cause Dashboard to throw the course to the " +
                "appropriate table if it was not as according to its year and semester, since every " +
                "semester has its own table. Finally, you'll not be able to change core details of the course " +
                "except the schedule." +
                subHeading("Summer") +
                "The courses that you've done during summer, should appear in this table under at " +
                reference("Home", "Module Collection", "Summer")+"." +
                subHeading("Miscellaneous Modules") +
                "The <b>Miscellaneous</b> Table is not intended to hold modules for <b>Undergraduate</b> Level " +
                "students. Like to all the other tables, you can add miscellaneous courses but only outside " +
                "your four-years specification at undergraduate level. If a bachelor student sees courses on " +
                "the Misc. Table automatically, then one or more of the following problems hold:" +
                "<ol><li>There's a <b>conflict</b> between the <i>Year of Admission</i> of the student and " +
                "the courses he/she is doing</li>" +
                "<li>All the eight(8) tables refused to accommodate a particular course as per the " +
                "inconsistency of its semester with the precise level of the student backed by the " +
                "admission month - usually, September or February</li>" +
                "<li>Any other discrepant detail given by the Portal can potentially raise this issue</li></ol>" +
                heading("Privacy & Settings") +
                "To change the default settings of the Dashboard including the Look & Feel, go to " +
                reference("Home", "Privacy & Settings", "Customize Dashboard")+"." +
                subHeading("Image Icon") +
                "You can quickly change the image icon by right-clicking on it." +
                subHeading("Major Code") +
                "Your <b>Major-code</b> is used by Dashboard to determine which courses are your majors. " +
                "To re-set your major-code, go to "+reference("Home", "Privacy & Settings", "Customize Profile")+". " +
                "Care must be taken regarding the code you provide: if it's incorrect, then you've certainly " +
                "provided yourself with wrong analysis!" +
                subHeading("Minor Code") +
                "Like your major-code, the <b>Minor-code</b> too is used by Dashboard analogously to detect " +
                "your minor courses. The minor-code is only available if you're doing a minor program." +
                subHeading("Custom Detail") +
                "You can also add <b>Customized Details</b>. These are your own additional details; " +
                "for instance <b>Nickname, High school, Hobby</b> etc. To add a custom detail, go to " +
                reference("Home", "Privacy & Settings", "Customize Profile")+". Set the <b>Key</b>; " +
                "example High School. And then, the <b>Value</b>; example Nusrat Senior Secondary School." +
                heading("Transcript") +
                "At "+reference("Home", "My Transcript")+" Activity, presented to you is your transcript " +
                "different from the one printable. The printable transcript is currently over 90% forge of the " +
                "official UTG transcript, and it's not to be used for official reasons. Did you know, " +
                "you can simply <b>double-click</b> on any row to display the full contents of the course " +
                "at that particular row?" +
                subHeading("Printing") +
                "To export / save your transcript, click "+reference("Home", "My Transcript", "Export")+". " +
                "Then feel fre to share it with family and friends!" +
                heading("Analysis") +
                "In the <b>Analysis Center</b>, "+reference("Home", "Analysis")+", Dashboard presents " +
                "analysis based on your modules, semesters, and academic-years. Do your part by providing the " +
                "right information - i.e setting the major [and, or the minor-code] correctly. " +
                "It should however be noted that the accuracy of the analysis is partly based on the consistency " +
                "of the data given to Dashboard by your portal." +
                heading("Other Tips & Universal Access") +
                subHeading("Go Portal") +
                "The Dashboard philosophy is that you should only visit your Portal when there is need to write it. " +
                "Even though, Dashboard provides a convenient way of opening your Portal without you going " +
                "through the process of entering your credentials. Click the <i>Go Portal</i> Button to " +
                "quickly jump right into your Portal." +
                subHeading("Come Home") +
                "To quickly show the home page of your Dashboard, press <b>Alt+H</b>. This is responsive from " +
                "every activity. The component handler of this key-combination is added to the rootPane of " +
                "the Dashboard and hence invisible. And it is always seeking focus. So in activities where " +
                "no visible component is having focus, simply clicking the <b>space-bar</b> will do this job." +
                subHeading("About UTG") +
                "You can visit the UTG official site to learn more about the University of the Gambia. " +
                "Click the <i>About UTG</i> Button appearing at the top-right of your Dashboard. <br/>";

        final KTextPane textPane = KTextPane.htmlFormattedPane(tipText);
        tipPane = new KScrollPane(textPane);
        tipPane.setBorder(null);
        return tipPane;
    }

    private String heading(String head){
        return "<h1>"+head+"</h1>";
    }

    private String subHeading(String subHead){
        return "<h2>"+subHead+"</h2>";
    }

    private Component getFAQs(){
        final Yaml faqsYaml = new Yaml(new Constructor(ArrayList.class));
        final ArrayList<HashMap<String, String>> faqs;
        faqs = faqsYaml.load(getClass().getResourceAsStream("/faqs.yml"));
        final StringBuilder faqsBuilder = new StringBuilder();
        for (HashMap<String, String> map : faqs) {
            faqsBuilder.append("<div style='border: thin solid blue; border-radius: 20px;" +
                    "padding: 0px 5px 5px 5px; margin: 5px 0px 5px 0px;'>").
                    append("<p>").append("<b>Question:</b> ").append(map.get("question")).append("</p>").
                    append("<p>").append("<b>Answer:</b> ").append(map.get("answer")).append("</p>").
                    append("<p>").append("<b>Author:</b> ").append(map.get("author")).append("</p>").
                    append("</div>");
        }
        final KTextPane textPane = KTextPane.htmlFormattedPane(faqsBuilder.toString());
        faqPane = new KScrollPane(textPane);
        faqPane.setBorder(null);
        return faqPane;
    }

}

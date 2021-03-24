package core;

import proto.*;

import javax.swing.*;
import java.awt.*;

/**
 * Encompasses both the 'FAQs' and 'Dashboard Tips'.
 */
public class Tips implements Activity {
    private CardLayout helpCard;
    private KScrollPane tipsPane, faqsPane;
    private boolean isFirstView;


    public Tips(){
        isFirstView = true;
        helpCard = new CardLayout();
        final KPanel centerPanel = new KPanel(helpCard);
        final KLabel showingLabel = new KLabel("Dashboard Tips", KFontFactory.BODY_HEAD_FONT);
        final KComboBox<String> helpBox = new KComboBox<String>(new String[] {"Dashboard Tips", "UTG FAQs"}) {
            @Override
            public JToolTip createToolTip() {
                return MComponent.preferredTip();
            }
        };
        helpBox.addActionListener(e-> {
            if (helpBox.getSelectedIndex() == 0) {
                helpCard.show(centerPanel,"tips");
                showingLabel.setText("Dashboard Tips");
            } else if (helpBox.getSelectedIndex() == 1) {
                helpCard.show(centerPanel,"faqs");
                showingLabel.setText("UTG FAQs & Answers");
            }
        });

        final KPanel northPanel = new KPanel(new BorderLayout());
        northPanel.add(new KPanel(showingLabel), BorderLayout.WEST);
        northPanel.add(new KPanel(helpBox), BorderLayout.EAST);

        final KPanel tipActivity = new KPanel(new BorderLayout());
        tipActivity.add(northPanel, BorderLayout.NORTH);
        tipActivity.add(centerPanel, BorderLayout.CENTER);

        generateTips();
        generateFAQs();

        helpCard.addLayoutComponent(centerPanel.add(tipsPane),"tips");
        helpCard.addLayoutComponent(centerPanel.add(faqsPane),"faqs");

        Board.addCard(tipActivity, "Faqs & Help");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Faqs & Help");
        if (isFirstView) {
            SwingUtilities.invokeLater(()-> {
                tipsPane.toTop();
                faqsPane.toTop();
            });
            isFirstView = false;
        }
    }

//    Dashboard tips
    private void generateTips(){
        final String runningTip = "Dashboard provides a mechanism for you to effectively keep track of the courses you " +
                "register for every ongoing semester. Go to 'Home | This Semester'. Temporarily, you can add courses to " +
                "the table which you should <i>Verify</i> later. However, it is recommended that you register all your courses on " +
                "the portal first, and let Dashboard synchronize them. To achieve this, click <b>More Options</b> and select <i>Match Portal</i>. " +
                "This is very important: for instance, it allows you to automatically import their names in creating tasks, assignments, or events.";

        final String verificationTip = "If adding courses locally and verifying them later is what you prefer, then there's " +
                "no problem as long as you apply the convention that the code of the course you're adding must be the " +
                "same as the one registered on the portal. Dashboard will go through the table of the courses of the ongoing " +
                "semester (if there's any), and if a course is found with exactly the same code, the consequence is that " +
                "the one you added will be substituted with the one found on the portal. It will also be marked verified which " +
                "shall mean that you'll not be able to tamper with the details except the schedules. To verify a course that you " +
                "already added locally, select it and click the <i>Confirm Button</i> therein.";

        final String matchingTip = "If you want to see a photocopy of the Running table on your portal, just click " +
                "<b>More Options</b> and select <i>Match Portal</i>. " +
                "Use this after you register all your courses. This is simpler and will bring all your courses.";

        final String modulesTip = "Under the 'Home | Modules Collection' activity, Dashboard does not miss a course you've treated! " +
                "The courses are arranged according to their respective years and semesters, and so are they added to the tables in the same manner. " +
                "Again, you can add your own courses here but will not be included in your analysis (and transcript) until they're confirmed on the portal. " +
                "To add a course, right-click on the " +
                "respective table and select Add. You can also get the details of a course, delete it, confirm, etc. by selecting the course first " +
                "right-clicking on it and choosing the right option from the Popup.";

        final String syncingTip = "Timely, you can try checking for new added courses on your portal by clicking 'Home | Modules Collection " +
                "| Sync'. While importing newly added courses from your portal, this action may verify unverified courses as well. In a nutshell, " +
                "for every course on the portal, if it corresponds to a local course and the local course is verified, nothing is done; else if " +
                "the local course is not verified, it'll be substituted with the one on the portal; otherwise the course is added directly if it " +
                "corresponds to no local course.";

        final String confirmationTip = "To launch verification for a course that you've added locally, right-click on it and select " +
                "<i>Verify</i> from the Popup shown to you. Among the details you provide, Dashboard uses only the code in the verification process. " +
                "If no course on your portal resembles the code then the operation is unsuccessful, otherwise the course is " +
                "confirmed. If the course is confirmed, it'll get substituted with the one found on the portal thus re-writing all " +
                "its details. This may cause Dashboard to throw the course to the appropriate table if it was not as according to its " +
                "year and semester, since every semester has its own table. Finally, you'll not be able to change core details " +
                "of the course save the schedule.";

        final String summerTip = "The courses that you've done during summer, should appear in this table under 'Home | Module Collection | Summer'.";

        final String miscTableTip = "The <b>Miscellaneous</b> table is not intended to hold modules for <b>Undergraduate Level</b> students. " +
                "Like to all the other tables, you can add miscellaneous courses but only outside your four-years specification at undergraduate level. " +
                "If a bachelor student sees courses on the Misc. table automatically, then one or more of the following problems hold:" +
                "<ol><li>There's a <b>conflict</b> between the <i>Year of Admission</i> of the student and the courses he/she is doing</li>" +
                "<li>All the eight(8) tables refused to accommodate a particular course as per the inconsistency of its semester with the precise level of the student " +
                "backed by the admission month - usually, September or February</li>" +
                "<li>Any other discrepant detail given by the portal can potentially raise this issue</li></ol>";

        final String settingsTip = "To change the default settings of the Dashboard including the Look & Feel, go to 'Home | Privacy & Settings | Customize Dashboard'.";

        final String iconTip = "Your optional image icon which is always shown at the top-left of the Dashboard is the same instance shown at 'Home | " +
                "Privacy & Settings | About-Me' tab. You can quickly change it by right-clicking the one at the top-left.";

        final String majCodeTip = "Your <b>Major-code</b> is used by Dashboard to determine which courses are your majors. To re-set your major-code, go to 'Home | " +
                "Privacy & Settings | Customize Profile'. <b>You have to be very careful regarding the code you provide</b>: if it's " +
                "incorrect, then you've certainly provided yourself with wrong analysis!";

        final String minCodeTip = "Like your major-code, the <b>Minor-code</b> too is used by Dashboard analogously to detect your minor courses. " +
                "The minor-code is only available if you're doing a minor program.";

        final String customDTip = "You can also add <b>Customized Details</b>. These are your own additional details; for instance <b>Nickname, High school, Hobby</b> etc." +
                "To add a custom detail, go to 'Home | Privacy & Settings | Customize Profile'. Set the <b>Key</b>; example High School. And then, the <b>Value</b>; example Nusrat " +
                "Senior Secondary School.";

        final String transTip = "Under 'Home | My Transcript' activity, presented to you is your transcript different from " +
                "the one printable. The printable transcript is currently about 80% forge of the official UTG transcript, " +
                "and it's not to be used for official reasons.<br>" +
                "Did you know you can simply <b>double-click</b> on any row to display the full contents of the course " +
                "at that particular row?";

        final String printTip = "To export your transcript to a file in you directory, go to 'Home | My Transcript' and click " +
                "<b>Export Transcript</b>, a button at the top-right. Then feel fre to share it with family and friends! ";

        final String analysisTip = "In the <b>Analysis Center</b>, 'Home | Analysis', Dashboard presents analysis based on your modules, " +
                "semesters, and academic-years. Do your part by providing the right information - i.e setting the major [and, or the minor-code] correctly. " +
                "It should however be noted that the accuracy of the analysis is partly based on the consistency of the data given to " +
                "Dashboard by your portal.";

        final String goPortalTip = "The Dashboard philosophy is that you should only visit your portal when there is need to write it. " +
                "Even though, Dashboard provides a convenient way of opening your portal without you going through the process of entering " +
                "your credentials. Click the Go Portal Button to quickly jump right into your portal. This, to some degree, will " +
                "disclose to you how Dashboard uses <i>Selenium Web Driver</i> to automate the Portal.";

        final String comeHomeTip = "To quickly show the home page of your Dashboard, press <b>Alt+H</b>. This is responsive from every activity. " +
                "The component handler of this key-combination is added to the rootPane of the Dashboard and hence invisible. And " +
                "it is always seeking focus. So in activities where no visible component is having focus, simply clicking the <b>space-bar</b> " +
                "will do this job.";

        final String utgTip = "You can visit the UTG official site to learn more about the University of the Gambia. To do this, click the About UTG " +
                "Button appearing at the top-right of your Dashboard.";

        final KPanel tipsPlace = new KPanel(975, 2_200);
        tipsPlace.setLayout(new BoxLayout(tipsPlace, BoxLayout.Y_AXIS));
        tipsPlace.addAll(head("Running Courses"), write(runningTip), subHead("Verification"),
                write(verificationTip), subHead("Matching"), write(matchingTip),
                head("Module Collection"), write(modulesTip), subHead("Modules Synchronization"),
                write(syncingTip), subHead("Course Verification"), write(confirmationTip),
                subHead("Summer"), write(summerTip), subHead("Miscellaneous Table"), write(miscTableTip),
                head("Privacy & Settings"), write(settingsTip), subHead("User Icon"),
                write(iconTip), subHead("Major Code"), write(majCodeTip), subHead("Minor Code"),
                write(minCodeTip), subHead("Custom Detail"), write(customDTip),
                head("My Transcript"), write(transTip), subHead("Printing"), write(printTip),
                head("Analysis"), write(analysisTip),
                head("Other Tips and Universal Access"), subHead("Go Portal"), write(goPortalTip),
                subHead("Come Home"), write(comeHomeTip), subHead("About UTG"), write(utgTip));
        tipsPane = new KScrollPane(new KPanel(new FlowLayout(FlowLayout.CENTER, 0, 0), tipsPlace));
    }

    private KPanel head(String topic) {
        final KLabel headLabel = new KLabel(topic, KFontFactory.createBoldFont(18), Color.BLUE);
        final KPanel headPanel = new KPanel(headLabel);
        headPanel.setBackground(Color.WHITE);
        headPanel.setReflectTheme(false);
        return headPanel;
    }

    private KPanel subHead(String subTopic) {
        final KLabel subHeadLabel = new KLabel(subTopic, KFontFactory.createPlainFont(17), Color.BLUE);
        final KPanel subHeadPanel =  new KPanel(new FlowLayout(FlowLayout.LEFT), subHeadLabel);
        subHeadPanel.setBackground(Color.WHITE);
        subHeadPanel.setReflectTheme(false);
        return subHeadPanel;
    }

    private KTextPane write(String note) {
        return KTextPane.htmlFormattedPane(note);
    }

//    UTG Faqs
    private void generateFAQs(){
        //declaring the q, a strings
        final String mailQ = "How do I get to discover my Student Mail address?";
        final String mailA = "Every enrolled-student is assigned a unique email address known as the <b>student mail</b>. " +
                "It is a mean by which <b>UTG</b> reaches out to the students, and updates them with the latest campus news. Unfortunately, " +
                "most students will only know about this overdue. So, your email is a combination of the first letter of your First Name and " +
                "Last Name, respectively, plus your matriculation number. Discard all the initials (if there's any), and lowercase the letters. " +
                "The password is, by default, your Matriculation Number.";

        final String changePortalMailQ = "Do I really need to change my Portal Email and password?";
        final String changePortalMailA = "Just as it is widely know to almost everyone, by default, everyone's email is their " +
                "Mat. Number followed by @utg.edu.gm, whilst the password is simply the Mat. Number. This is quite predictable and vulnerable, " +
                "so it is strongly recommended that you change it. Visit the faulty of your department for this changes.";

        final KPanel faqsPlace = new KPanel();
        faqsPlace.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        faqsPlace.setLayout(new BoxLayout(faqsPlace, BoxLayout.Y_AXIS));
        faqsPlace.addAll(provideAnswerToFaq(mailQ, mailA, 150), Box.createVerticalStrut(10),
                provideAnswerToFaq(changePortalMailQ, changePortalMailA, 125));//to be continued...
        faqsPane = new KScrollPane(new KPanel(new FlowLayout(FlowLayout.CENTER, 0, 0), faqsPlace));
    }

    private KPanel provideAnswerToFaq(String problem, String answer, int height){
        final Font hintFont = KFontFactory.createBoldFont(15);

        final KPanel quesPanel = new KPanel(new BorderLayout());
        final KPanel quesLabelPanel = new KPanel(new KLabel("Question:", hintFont));
        quesLabelPanel.setReflectTheme(false);
        quesLabelPanel.setBackground(Color.WHITE);
        quesPanel.add(quesLabelPanel, BorderLayout.WEST);
        quesPanel.add(write(problem), BorderLayout.CENTER);

        final KPanel ansPanel = new KPanel(new BorderLayout());
        final KPanel ansLabelPanel = new KPanel(new KLabel("Answer:", hintFont));
        ansLabelPanel.setReflectTheme(false);
        ansLabelPanel.setBackground(Color.WHITE);
        ansPanel.add(ansLabelPanel, BorderLayout.WEST);
        ansPanel.add(write(answer), BorderLayout.CENTER);

        final KPanel onePanel = new KPanel(975, height);
        onePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE,2, true));
        onePanel.setLayout(new BoxLayout(onePanel, BoxLayout.Y_AXIS));
        onePanel.addAll(quesPanel, ansPanel);
        return onePanel;
    }

}

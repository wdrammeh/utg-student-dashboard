package core.first;

import core.user.Setup;
import proto.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class Welcome extends KDialog {
    private CardLayout cardLayout;
    private KPanel welcomeActivity;
    private KScrollPane scrollPane;
    private static final int PREFERRED_WIDTH = 675;


    public Welcome() {
        super("Welcome");
        setSize(PREFERRED_WIDTH + 50, 575);
        cardLayout = new CardLayout();
        welcomeActivity = new KPanel(cardLayout);
        addWelcomeActivity();
        addSelectionActivity();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setContentPane(welcomeActivity);
        setLocationRelativeTo(null);
    }

    private void addWelcomeActivity(){
        final KPanel topPanel = new KPanel(new KLabel("UTG-Student Dashboard (v0.0.1-SNAPSHOT)", KFontFactory.createBoldFont(20)));
        topPanel.setBackground(Color.WHITE);

        final String broughtString = "Proudly brought to you by the <b>Dashboard Project</b>. Dashboard comes with solutions long-anticipated by the UTG Students, " +
                "so use it to organize yourself! Before proceeding, we vehemently recommend that you go through the disclaimer below." +
                "<p>Please note that this copy of Dashboard is a <b>SNAPSHOT</b> release which means that it is still under active development. " +
                "SNAPSHOT releases are intended for Feedback purposes only; and hence, this will be replaced with a stable release. " +
                "Also, configuration data serialized by this version won't be compatible with future releases.</p>";

        final String dedicationText = "Dashboard is developed by the Students for the Students. Whether you're an <b>Undergraduate</b>, <b>Postgraduate</b>, or a student <b>deferring</b>, Dashboard got you covered. " +
                "Dashboard grasp your fundamental details at every successful login - so you don't " +
                "need to be manually specifying your level, or status on the go.";

        final String requirementText = "Dashboard, as a project, is written completely in the Java (<i>platform-independent</i>) Language, allowing it to run gently on virtually all operating systems. " +
                "The system-dependent compilations are as a result of the <b>Selenium Web-Driver Specification</b> across platforms." +
                "<p>Dashboard uses the traditional <b>Firefox Browser</b> to get the better of your Portal. " +
                "This is wholly background, and does no way interfere with the normal usage of your browser.</p>" +
                "<p>For uniformity reasons, Dashboard is not <b>tested compatible</b> with any other browser. " +
                "So if Firefox is not installed, please make sure it is installed and ready before start.</p>";

        final String securityText = "Whatever happens in your Dashboard, stays in your Dashboard. However, keep the following points in mind as long as the Portal is concerned:" +
                "<h3 style='font-size: 14px;'>What Dashboard cannot do</h3>" +
                "<b>Dashboard does not write your portal</b>! Dashboard is not legalized to do so just yet, therefore it cannot <i>register</i> or <i>drop</i> courses, neither can it <i>apply deferment</i>, nor can it be used as a <i>mean of application</i> for non-enrolled students." +
                "<h3 style='font-size: 14px;'>What Dashboard can do</h3>" +
                "Just a glimpse of what Dashboard is capable of carving out for you:" +
                "<ul>" +
                "<li>You'll be able to <b>Print / Export your transcript</b> in a Portable Document Format</li>" +
                "<li>Dashboard provides powerful analyzation system of filtering your courses based on grades, scores, requirements, etc; your attended-lecturers, CGPAs earned, to mention a few</li>" +
                "<li>Sketches your semester-to-semester performance with respect to the CGPA earned per semester</li>" +
                "<li>Dashboard possesses rich customization property, letting you to keep track of even <b>non-academic related details</b>. Customizations include changing the <b>Look & Feel</b> " +
                "to your native system's Look</li>" +
                "<li>Enjoy the carefully spelled-out answers to some <b>FAQs</b> of UTG</li>" +
                "<li>Dashboard keeps you up to date with the News from UTG's official news site in a more presentable manner, and offline reading capability</li>" +
                "<li>Dashboard effectively organizes your <b>personal tasks, group works, assignments, and other student-related cares</b>, all at your fingertips</li>" +
                "<li>Dashboard reads, if necessary, every last detail of your portal which you can access anytime offline!</li></ul>";

        final String importantText = "The analysis provided to you by Dashboard is entirely <b>Portal-independent</b>.<br>" +
                "But analysis is based on data, and their is no better source of your data than your portal. Therefore, unexpected details from therein can induce <i>misbehavior of your Dashboard</i>!" +
                "<p>We urged every student victim of <b>wrong</b>, or <b>incomplete details</b> from their portals to refer to their respective departments " +
                "for help before or anytime soon after proceeding.</p>" +
                "<p>We however handle, gracefully, the common issue of <b>missing-grades</b>, but cannot afford to lose core details like your <b>name or matriculation number</b>. " +
                "Dashboard may halt build, if such details are missing, or not readable somehow.</p>" +
                "<p>Besides missing details, some students have <b>conflicting information</b> in their portals. " +
                "This can let you have the <b>worst possible experience</b> from your usage of Dashboard! For instance, a student admitted in 2016 may have his/her year-of-admission 2019 in the portal. " +
                "To mention a few consequences of this is that, obviously, a wrong computation will be returned when Dashboard is asked to predict the <b>expected year of graduation</b>, or the </b>current level</b> of the student. " +
                "Plus, mis-indexing of modules' years will occur which, in turn, will cause <i>analysis-by-year</i> problems, and addition of modules to the inappropriate tables.</p>" +
                "<p>The good news is: all these, if occurred, can be fixed at any point in time even after build - as Dashboard effortlessly <b>re-indexes</b> your resources after every successful login.</p>";

        final String nextText = "To continue, acknowledge adherence to these terms by selecting the <b>Checkbox</b> below.";

        final KPanel separatorPanel = new KPanel(new KSeparator(new Dimension(PREFERRED_WIDTH, 1), Color.RED));
        separatorPanel.setBackground(Color.WHITE);

        final KButton exitButton = new KButton("Exit");
        exitButton.setFont(KFontFactory.createPlainFont(15));
        exitButton.addActionListener(e-> System.exit(0));

        final KButton nextButton = new KButton("Continue");
        nextButton.setFont(KFontFactory.createPlainFont(15));
        nextButton.addActionListener(e-> cardLayout.show(welcomeActivity, "Choose"));
        nextButton.setEnabled(false);

        final KCheckBox nextCheckBox = new KCheckBox("I hereby read, understood, and consent to these terms.");
        nextCheckBox.setFont(KFontFactory.createPlainFont(15));
//        nextCheckBox.setForeground(Color.RED);
        nextCheckBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextCheckBox.addItemListener(e-> nextButton.setEnabled(e.getStateChange() == ItemEvent.SELECTED));

        final KPanel bottomPanel = new KPanel(new BorderLayout());
        bottomPanel.add(new KPanel(nextCheckBox), BorderLayout.WEST);
        bottomPanel.add(new KPanel(new FlowLayout(FlowLayout.CENTER, 10, 5), exitButton, nextButton),
                BorderLayout.EAST);

        final KPanel welcomePanel = new KPanel();
        welcomePanel.setLayout(new BorderLayout(5, 10));
        final KPanel textPanel = new KPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.addAll(topPanel,
                write(broughtString, 165),
                head("Dedication"), write(dedicationText, 90),
                head("System Requirement"), write(requirementText, 185),
                head("Portal & Privacy"), write(securityText, 550),
                head("Important"), write(importantText, 425),
                separatorPanel, write(nextText, 50));
        scrollPane = new KScrollPane(textPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        welcomePanel.add(scrollPane, BorderLayout.CENTER);
        welcomePanel.add(bottomPanel, BorderLayout.SOUTH);
        cardLayout.addLayoutComponent(welcomeActivity.add(welcomePanel), "Welcome");
    }

    private KPanel head(String head){
        final KPanel headerPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(new KLabel(head, KFontFactory.createBoldFont(17), Color.BLUE));
        return headerPanel;
    }

    private static KTextPane write(String manyText, int tHeight){
        final KTextPane textPane = KTextPane.htmlFormattedPane(manyText);
        textPane.setBackground(Color.WHITE);
        textPane.setPreferredSize(new Dimension(PREFERRED_WIDTH, tHeight));
        return textPane;
    }

    public KScrollPane getScrollPane(){
        return scrollPane;
    }

    private void addSelectionActivity(){
        final KPanel hintPanel = new KPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.add(new KPanel(new KLabel("Select User Type", KFontFactory.createBoldFont(25))));
        hintPanel.add(new KPanel(new KLabel("How do you want to use Dashboard?",
                KFontFactory.createPlainFont(20), Color.GRAY)));

        final Dimension optionPanelDimension = new Dimension(200, 165);
        final Border selectedBorder = BorderFactory.createLineBorder(Color.BLUE, 2, true);
        final Border unselectedBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true);

        final KPanel studentOptionPanel = new KPanel(new BorderLayout(0, 5), optionPanelDimension);

        final JRadioButton studentOption = new JRadioButton("UTG Student", true);
        studentOption.setFont(KFontFactory.createPlainFont(17));
        studentOption.setFocusable(false);
        studentOption.addItemListener(e-> studentOptionPanel.setBorder(e.getStateChange() == ItemEvent.SELECTED ?
                selectedBorder : unselectedBorder));

        studentOptionPanel.setBorder(selectedBorder);
        studentOptionPanel.add(studentOption, BorderLayout.NORTH);
        studentOptionPanel.add(new KPanel(KLabel.createIcon("UTGLogo.gif", 100, 100)),
                BorderLayout.CENTER);
        studentOptionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                studentOption.doClick();
            }
        });

        final String studentText = "<b>Login</b> as a UTG Student:" +
                "<ul>" +
                "<li>Full functionality</li>" +
                "<li>Enhanced customization</li>" +
                "<li>Explore UTG Portal</li>" +
                "<li>Track Semesters on the go</li>" +
                "<li>Modules collection, organization</li>" +
                "<li>Transcript exportation</li>" +
                "<li>Analysis & presentation</li>" +
                "<li>Tips, FAQs, Feedback</li>" +
                "<li>TODO, Tasks, Projects</li>" +
                "<li>News, Notification</li>" +
                "</ul>";
        final KTextPane studentTextPane = KTextPane.htmlFormattedPane(studentText);
        studentTextPane.setBackground(null);
        studentTextPane.setOpaque(false);

        final KPanel studentPanel = new KPanel(new BorderLayout());
        studentPanel.add(new KPanel(new FlowLayout(FlowLayout.LEFT, 10, 5), studentOptionPanel),
                BorderLayout.NORTH);
        studentPanel.add(new KPanel(studentTextPane), BorderLayout.CENTER);

        final KPanel trialOptionPanel = new KPanel(new BorderLayout(0, 5), optionPanelDimension);

        final JRadioButton trylOption = new JRadioButton("Non-UTG Student");
        trylOption.setFont(KFontFactory.createPlainFont(17));
        trylOption.setFocusable(false);
        trylOption.addItemListener(e-> trialOptionPanel.setBorder(e.getStateChange() == ItemEvent.SELECTED ?
                selectedBorder : unselectedBorder));

        trialOptionPanel.setBorder(unselectedBorder);
        trialOptionPanel.add(trylOption, BorderLayout.NORTH);
        trialOptionPanel.add(new KPanel(KLabel.createIcon("personal.png", 100, 100)),
                BorderLayout.CENTER);
        trialOptionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                trylOption.doClick();
            }
        });

        final String trialText = "Having troubles logging in? <b>Try Dashboard</b>:" +
                "<ul>" +
                "<li>Limited functionality</li>" +
                "<li>Customization</li>" +
                "<li>Explore UTG</li>" +
                "<li>FAQs & Help</li>" +
                "<li>Tasks</li>" +
                "<li>News</li>" +
                "<li>Login anytime</li>" +
                "</ul>";
        final KTextPane trialTextPane = KTextPane.htmlFormattedPane(trialText);
        trialTextPane.setBackground(null);
        trialTextPane.setOpaque(false);

        final KPanel trialPanel = new KPanel(new BorderLayout());
        trialPanel.add(new KPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5), trialOptionPanel),
                BorderLayout.NORTH);
        trialPanel.add(new KPanel(trialTextPane), BorderLayout.CENTER);

        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(studentOption);
        buttonGroup.add(trylOption);

        final KButton nextButton = new KButton("Continue");
        nextButton.setFont(KFontFactory.createPlainFont(15));
        nextButton.setFocusable(true);
        nextButton.addActionListener(e-> {
            setVisible(false);
            if (studentOption.isSelected()) {
                SwingUtilities.invokeLater(()-> new Login(this).setVisible(true));
            } else if (trylOption.isSelected()) {
                SwingUtilities.invokeLater(()-> new Setup(this).setVisible(true));
            }
        });

        final KButton backButton = new KButton("Back");
        backButton.setFont(KFontFactory.createPlainFont(15));
        backButton.addActionListener(e-> cardLayout.show(welcomeActivity, "Welcome"));

        final KPanel choosePanel = new KPanel(new BorderLayout());
        choosePanel.add(hintPanel, BorderLayout.NORTH);
        choosePanel.add(studentPanel, BorderLayout.WEST);
        choosePanel.add(trialPanel, BorderLayout.EAST);
        choosePanel.add(new KPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5), backButton, nextButton),
                BorderLayout.SOUTH);
        cardLayout.addLayoutComponent(welcomeActivity.add(choosePanel), "Choose");
    }

}

package core.first;

import core.user.Guest;
import core.utils.FontFactory;
import proto.*;
import utg.Dashboard;

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
        final KPanel topPanel = new KPanel(new KLabel("UTG STUDENT DASHBOARD", FontFactory.createBoldFont(20)));
        topPanel.setBackground(Color.WHITE);

        final String broughtString = "Proudly brought to you by the <b>Dashboard Project</b>. Dashboard comes with solutions long-anticipated by the UTG Students, " +
                "so use it to organize yourself! Before proceeding, we vehemently recommend that you go through the disclaimer below.";

        final String dedicationText = "Dashboard is developed by the Students for the Students. Whether you're an <b>Undergraduate</b>, <b>Postgraduate</b>, or a student <b>deferring</b>, Dashboard got you covered. " +
                "Dashboard grasp your fundamental details at every successful login - so you don't " +
                "need to be manually specifying your level, or status on the go.";

        final String requirementText = "Dashboard, as a project, is written completely in the Java (<i>platform-independent</i>) Language, allowing it to run gently on virtually all operating systems. " +
                "The system-dependent compilations are as a result of the <b>Selenium Web Driver Specification</b> across platforms." +
                "<p>Dashboard uses the traditional <b>Firefox Browser</b> to get the better of your Portal. " +
                "This is wholly background, and does no way interfere with the normal usage of your browser.</p>" +
                "<p>For uniformity reasons, Dashboard is not <b>tested compatible</b> with any other browser. " +
                "So if Firefox is not installed, please make sure it is installed and ready before start.</p>";

        final String importantText = "Whatever happens in your Dashboard, stays in your Dashboard. However, keep the following points in mind as long as the ERP System is concerned:<br>" +
                "The <b>analysis</b> provided to you by Dashboard is entirely <i>Portal-independent</i>. " +
                "But analysis is based on data, and their is no better source of your data than the Portal. Therefore, unexpected details from therein can induce <i>misbehavior of your Dashboard</i>!" +
                "<p>We urged every student victim of <b>wrong</b>, or <b>incomplete details</b> from their portals to refer to their respective departments " +
                "for help before, or anytime soon after, proceeding.</p>" +
                "<p>We however handle, gracefully, the common issue of <b>missing-grades</b>, but cannot afford to lose core details like your <b>name or matriculation number</b>. " +
                "Dashboard may halt build, if such details are missing, or not readable somehow.</p>";

        final String nextText = "<br>To continue, acknowledge adherence to these terms by selecting the <b>Checkbox</b> below.";

        final KButton exitButton = new KButton("Exit");
        exitButton.setFont(FontFactory.createPlainFont(15));
        exitButton.addActionListener(e-> System.exit(0));

        final KButton nextButton = new KButton("Continue");
        nextButton.setFont(FontFactory.createPlainFont(15));
        nextButton.addActionListener(e-> cardLayout.show(welcomeActivity, "Choose"));
        nextButton.setEnabled(false);

        final KCheckBox nextCheckBox = new KCheckBox("I hereby READ, UNDERSTOOD, and CONSENT to these terms.");
        nextCheckBox.setFont(FontFactory.createBoldFont(15));
        nextCheckBox.setForeground(Color.DARK_GRAY);
        nextCheckBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextCheckBox.setFocusable(false);
        nextCheckBox.addItemListener(e-> nextButton.setEnabled(e.getStateChange() == ItemEvent.SELECTED));

        final KPanel bottomPanel = new KPanel(new BorderLayout());
        bottomPanel.add(new KPanel(new FlowLayout(FlowLayout.CENTER, 5, 10), nextCheckBox), BorderLayout.WEST);
        bottomPanel.add(new KPanel(new FlowLayout(FlowLayout.CENTER, 5, 10), exitButton, nextButton),
                BorderLayout.EAST);

        final KPanel welcomePanel = new KPanel();
        welcomePanel.setLayout(new BorderLayout(5, 10));
        final KPanel textPanel = new KPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.addAll(topPanel,
                head(String.valueOf(Dashboard.VERSION)),
                write(broughtString, 70),
                head("Dedication"), write(dedicationText, 90),
                head("System Requirement"), write(requirementText, 190),
                head("Portal & Privacy"), write(importantText, 225),
                write(nextText, 65));
        scrollPane = new KScrollPane(textPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        welcomePanel.add(scrollPane, BorderLayout.CENTER);
        welcomePanel.add(bottomPanel, BorderLayout.SOUTH);
        cardLayout.addLayoutComponent(welcomeActivity.add(welcomePanel), "Welcome");
    }

    private KPanel head(String head){
        final KPanel headerPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(new KLabel(head, FontFactory.createBoldFont(17), Color.BLUE));
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
        hintPanel.add(new KPanel(new KLabel("Select User Type", FontFactory.createBoldFont(25))));
        hintPanel.add(new KPanel(new KLabel("How do you want to use Dashboard?",
                FontFactory.createPlainFont(20), Color.GRAY)));

        final Dimension optionPanelDimension = new Dimension(200, 165);
        final Border selectedBorder = BorderFactory.createLineBorder(Color.BLUE, 2, true);
        final Border unselectedBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true);

        final KPanel studentOptionPanel = new KPanel(new BorderLayout(0, 5), optionPanelDimension);

        final JRadioButton studentOption = new JRadioButton("UTG Student", true);
        studentOption.setFont(FontFactory.createPlainFont(17));
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
        trylOption.setFont(FontFactory.createPlainFont(17));
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
        nextButton.setFont(FontFactory.createPlainFont(15));
        nextButton.setFocusable(true);
        nextButton.addActionListener(e-> {
            setVisible(false);
            if (studentOption.isSelected()) {
                SwingUtilities.invokeLater(()-> new Login(this).setVisible(true));
            } else if (trylOption.isSelected()) {
                SwingUtilities.invokeLater(()-> new Guest(this).setVisible(true));
            }
        });

        final KButton backButton = new KButton("Back");
        backButton.setFont(FontFactory.createPlainFont(15));
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

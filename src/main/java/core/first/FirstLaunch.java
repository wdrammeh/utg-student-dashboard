package core.first;

import core.Board;
import core.alert.Notification;
import core.setting.SettingsActivity;
import core.user.Student;
import core.utils.*;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * First and for all; this type requests the initial details from the student.
 */
public class FirstLaunch extends KDialog {
    private KPanel contentPanel;
    private Font bigFont = FontFactory.createBoldFont(18);
    private CardLayout layout = new CardLayout(){
        @Override
        public void show(Container parent, String name) {
            super.show(parent, name);
            FirstLaunch.this.setTitle("Startup Settings - "+name);
        }
    };


    public FirstLaunch() {
        super("Startup Settings - Major Code");
        setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
        setDefaultCloseOperation(KDialog.DO_NOTHING_ON_CLOSE);
        contentPanel = new KPanel(layout);
        setContentPane(contentPanel);
        layout.addLayoutComponent(contentPanel.add(majorCodeComponent()), "Major Code");
        layout.addLayoutComponent(contentPanel.add(minorComponent()), "Minor Code");
        layout.addLayoutComponent(contentPanel.add(imageComponent()), "Image Icon");
        setPreferredSize(new Dimension(600, 500));
        pack();
        setLocationRelativeTo(Board.getRoot());
    }

    private Component majorCodeComponent(){
        final String majorCodeText = "<p>Yes, your major code. In short, it means the 3-letter prefix of the course-codes of your major courses.</p>" +
                "<p>Dashboard performs analysis on you from a variety of angles. One of the most important is performing personalized " +
                "analysis on your <b>major courses</b>, and for that it uses this code to auto-index and filter out the courses " +
                "that are your majors.</p>" +
                "<p><b>If you are not sure, do not write anything in the field below!</b> Changes can always be made in Settings.</p>" +
                "<p>For example, the known major-code for Mathematics program is <b>MTH</b>; Computer, <b>CPS</b>; Economics, <b>ECO</b>; " +
                "Chemistry, <b>CHM</b>; Biology, <b>BIO</b>, etc.</p>";
        final KTextPane textPane = KTextPane.htmlFormattedPane(majorCodeText);
        textPane.setBackground(Color.WHITE);

        final KButton nextButton = new KButton("Next");
        final KTextField majorCodeField = KTextField.rangeControlField(3);
        majorCodeField.setPreferredSize(new Dimension(150, 35));
        majorCodeField.setFont(bigFont);
        majorCodeField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        majorCodeField.addActionListener(e-> nextButton.doClick());

        nextButton.setFont(FontFactory.createPlainFont(15));
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e-> {
            final String majorCode = majorCodeField.getText().toUpperCase();
            Student.setMajorCode(majorCode);
            layout.show(contentPanel, "Minor Code");
        });

        final KPanel majorPanel = new KPanel();
        majorPanel.setLayout(new BoxLayout(majorPanel, BoxLayout.Y_AXIS));
        majorPanel.addAll(new KPanel(new KLabel("What's Your Major Code?", bigFont)), textPane,
                new KPanel(majorCodeField), MComponent.contentBottomGap(),
                new KPanel(new FlowLayout(FlowLayout.RIGHT), nextButton));
        return majorPanel;
    }

    private Component minorComponent(){
        final String minorText = "<p>If you are also doing a minor, then Dashboard has even better ways of organizing your modules.</p>" +
                "<p>Write, in the fields below, the <b>name</b> and <b>course-code</b> of the program you are minoring.<br>" +
                "<i>Remember, the minor-code must also match the minor program in a way as specified earlier for the major-code.</i></p>" +
                "<p>If you are not minoring a program, select the corresponding button below and continue.</p>";
        final KTextPane textPane = KTextPane.htmlFormattedPane(minorText);
        textPane.setBackground(Color.WHITE);

        final KTextField minorCodeField = KTextField.rangeControlField(3);
        final KButton nextButton = new KButton("Next");
        final KTextField minorNameField = new KTextField();
        minorNameField.setPreferredSize(new Dimension(350, 30));
        minorNameField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        minorNameField.setEditable(false);
        minorNameField.addActionListener(e-> minorCodeField.requestFocusInWindow());

        minorCodeField.setPreferredSize(new Dimension(125, 30));
        minorCodeField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        minorCodeField.setEditable(false);
        minorCodeField.addActionListener(e-> {
            if (minorNameField.hasText()) {
                nextButton.doClick();
            } else {
                minorNameField.requestFocusInWindow();
            }
        });

        final JRadioButton iDoButton = new JRadioButton("Am doing a minor");
        iDoButton.setFont(FontFactory.createPlainFont(15));
        iDoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iDoButton.addItemListener(e -> {
            minorNameField.setEditable(e.getStateChange() == ItemEvent.SELECTED);
            minorCodeField.setEditable(e.getStateChange() == ItemEvent.SELECTED);
        });
        final JRadioButton iDontButton = new JRadioButton("Am not doing a minor", true);
        iDontButton.setFont(FontFactory.createPlainFont(15));
        iDontButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final ButtonGroup choicesGroup = new ButtonGroup();
        choicesGroup.add(iDoButton);
        choicesGroup.add(iDontButton);

        final KPanel kPanel = new KPanel();
        kPanel.setLayout(new BoxLayout(kPanel, BoxLayout.Y_AXIS));
        kPanel.addAll(textPane, new KPanel(iDoButton, iDontButton),
                new KPanel(new KLabel("Minor Program: ", FontFactory.createBoldFont(16)), minorNameField),
                new KPanel(new KLabel("Code: ", FontFactory.createBoldFont(16)), minorCodeField));

        final KButton prevButton = new KButton("Back");
        prevButton.setFont(FontFactory.createPlainFont(15));
        prevButton.addActionListener(e-> layout.show(contentPanel, "Major Code"));

        nextButton.setFont(FontFactory.createPlainFont(15));
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e-> {
            if (iDoButton.isSelected()) {
                final String minorName = minorNameField.getText();
                final String minorCode = minorCodeField.getText().toUpperCase();
                if (Globals.hasNoText(minorName)) {
                    App.reportError(rootPane,"No Minor", "Please enter the name of your minor program.");
                    return;
                } else if (Globals.hasNoText(minorCode)) {
                    App.reportWarning(rootPane,"Warning", "You have not set the code for your minor program: "+minorName+".\n" +
                            "Set this later in the Settings for Dashboard to detect your minor courses.");
                }
                Student.setMinor(minorName);
                Student.setMinorCode(minorCode);
            } else {
                Student.setMinor("");
            }
            layout.show(contentPanel, "Image Icon");
        });

        final KPanel minorPanel = new KPanel();
        minorPanel.setLayout(new BoxLayout(minorPanel, BoxLayout.Y_AXIS));
        minorPanel.addAll(new KPanel(new KLabel("Do You Minor a Program?", bigFont)), kPanel,
                MComponent.contentBottomGap(), new KPanel(new FlowLayout(FlowLayout.RIGHT), prevButton, nextButton));
        return minorPanel;
    }

    private Component imageComponent(){
        final String imgText = "With such a nice look, you cannot wait to behold your glittering face right at the top-left " +
                "of your dashboard." +
                "<p>Set an optional image icon now to get started with your <b>Personal Dashboard</b>, or anytime later " +
                "in Settings.</p>";
        final KTextPane textPane = KTextPane.htmlFormattedPane(imgText);
        textPane.setBackground(Color.WHITE);

        final KLabel iconLabel = new KLabel(Student.getIcon());

        final KButton setButton = new KButton("Set Now");
        setButton.setFocusable(true);
        setButton.setFont(FontFactory.createPlainFont(14));
        setButton.setForeground(Color.BLUE);
        setButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setButton.addActionListener(e-> {
            Student.startSettingImage(getRootPane());
            iconLabel.setIcon(Student.getIcon());
        });

        final KPanel nicePanel = new KPanel();
        nicePanel.setLayout(new BoxLayout(nicePanel, BoxLayout.Y_AXIS));
        nicePanel.addAll(textPane, new KPanel(iconLabel), new KPanel(setButton));

        final KButton prevButton = new KButton("Back");
        prevButton.setFont(FontFactory.createPlainFont(15));
        prevButton.addActionListener(e-> layout.show(contentPanel, "Minor Code"));
        final KButton finishButton = new KButton("Finish");
        finishButton.setFont(FontFactory.createPlainFont(15));
        finishButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        finishButton.addActionListener(e-> {
            new Thread(FirstLaunch::mountDataPlus).start();
            dispose();
        });

        final KPanel imgPanel = new KPanel();
        imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.Y_AXIS));
        imgPanel.addAll(new KPanel(new KLabel("You Look Nice!", bigFont)), nicePanel,
                MComponent.contentBottomGap(), new KPanel(new FlowLayout(FlowLayout.RIGHT), prevButton, finishButton));
        return imgPanel;
    }

    private static void mountDataPlus(){
        // initialize an About Me
        final String aboutMe = "My name is "+Student.getFullNamePostOrder()+"\n" +
                "The University of the Gambia\n" +
                "School of "+Student.getSchool()+"\n" +
                "Division of "+Student.getDivision()+"\n" +
                Student.getMajor()+" Program\n" +
                Student.getYearOfAdmission()+" - "+Student.getExpectedYearOfGraduation();
        Student.setAbout(aboutMe);
        SettingsActivity.descriptionArea.setText(aboutMe);
        Serializer.mountUserData();

        // create a welcome message notification
        final String welcomeMessage = "Dear "+Student.getFirstName()+"," +
                "<p>Regards from the <b>Dashboard Team</b>. This is a personal management " +
                "tool for all your academic-related stuff at the UTG.</p>" +
                "<p>Dashboard is a '<b>FOSS</b>' (Free and Open-Source Software). This means " +
                "that everyone can contribute to its development, including you!</p>" +
                "<p>Yes, even if you are not a programmer, you can provide an answer to a " +
                "Frequently Asked Question (<b>FAQ</b>), and solve problems of your brothers and sisters.</p>" +
                "<p>If you are interested in improving this project, we can't wait to receive your contributions! " +
                "Simply follow the following link to get started:</p>" +
                "<a href='"+Internet.REPO_URL+"'><i style='text-align: center;'>"+ Internet.REPO_URL +"</i></a>";
        Notification.create("Welcome",
                "Welcome to Dashboard, "+Student.getFullNamePostOrder(), welcomeMessage);
    }

}

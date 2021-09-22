package core;

import core.user.Student;
import core.utils.*;
import proto.*;
import utg.Dashboard;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class About extends KDialog {
    private CardLayout midCard;
    private KPanel midLayer;


    public About(){
        super("About");
        setModalityType(DEFAULT_MODALITY_TYPE);

        midCard = new CardLayout();
        midLayer = new KPanel(midCard);
        midCard.addLayoutComponent(midLayer.add(getAboutCard()), "about");
        midCard.addLayoutComponent(midLayer.add(getCreditsCard()), "credits");
        midCard.addLayoutComponent(midLayer.add(new KScrollPane(getFeedbackCard(),
                new Dimension(680, 500))), "feedback");
        midCard.addLayoutComponent(midLayer.add(getDonateCard()), "donate");
        midCard.addLayoutComponent(midLayer.add(getTermsCard()), "terms");

        final KPanel hindLayer = new KPanel();
        hindLayer.addAll(newCardButton("About", "About", "about"),
                newCardButton("Credits", "Credits", "credits"),
                newCardButton("Feedback", "Feedback", "feedback"),
                newCardButton("Donate", "Donate", "donate"),
                newCardButton("Terms", "Terms & Conditions", "terms"));

        final KPanel contentPanel = new KPanel(new BorderLayout());
        contentPanel.add(midLayer, BorderLayout.CENTER);
        contentPanel.add(hindLayer, BorderLayout.SOUTH);
        setContentPane(contentPanel);
        pack();
        setLocationRelativeTo(Board.getRoot());
    }

    @Override
    public void setTitle(String title) {
        super.setTitle("Dashboard - "+title);
    }

    /**
     * Creates a button with the specified buttonText.
     * Buttons created with this call are specifically meant for shifting among
     * the activities herein.
     * Its action is that the card-layout shows the component, and the dialog
     * sets its title to activityTitle.
     */
    private KButton newCardButton(String buttonText, String activityTitle, String component){
        final KButton cardButton = new KButton(buttonText);
        cardButton.setFont(FontFactory.createPlainFont(15));
        cardButton.addActionListener(e-> {
            midCard.show(midLayer, component);
            setTitle(activityTitle); // which is hereby overridden
        });
        return cardButton;
    }

    private KPanel getAboutCard(){
        final KPanel dashboardLayer = new KPanel(new BorderLayout());
        dashboardLayer.add(KLabel.createIcon("dashboard.png", 150, 135), BorderLayout.CENTER);
        dashboardLayer.add(new KPanel(new KLabel("A flexible and elegant student management system for the UTG",
                FontFactory.createPlainFont(16))), BorderLayout.SOUTH);

        final KPanel javaLayer = new KPanel(new BorderLayout());
        javaLayer.add(new KLabel(new ImageIcon(App.getIconURL("splash.gif"))), BorderLayout.CENTER);

        final KPanel iconsLayer = new KPanel();
        iconsLayer.setLayout(new BoxLayout(iconsLayer, BoxLayout.Y_AXIS));
        iconsLayer.addAll(dashboardLayer, javaLayer, Box.createVerticalStrut(50));

        final KPanel bottomLayer = new KPanel();
        bottomLayer.setLayout(new BoxLayout(bottomLayer, BoxLayout.Y_AXIS));
        bottomLayer.addAll(new KLabel("Release: "+ Dashboard.VERSION, FontFactory.createPlainFont(15)),
                new KLabel("Email: "+ Mailer.DEVELOPER_MAIL, FontFactory.createPlainFont(15)),
                new KLabel("Contact: +220 3413910", FontFactory.createPlainFont(15)));

        final KPanel aboutCard = new KPanel(new BorderLayout());
        aboutCard.add(new KPanel(new KLabel("University Student Dashboard", FontFactory.createBoldFont(18))),
                BorderLayout.NORTH);
        aboutCard.add(iconsLayer, BorderLayout.CENTER);
        aboutCard.add(bottomLayer, BorderLayout.SOUTH);
        return aboutCard;
    }

    private KPanel getCreditsCard(){
        final KLabel authorIconLabel = KLabel.createIcon("author.jpg", 200, 175);

        final KPanel authorNamesPanel = new KPanel();
        authorNamesPanel.setLayout(new BoxLayout(authorNamesPanel,BoxLayout.Y_AXIS));
        authorNamesPanel.addAll(new KLabel("MUHAMMED W. DRAMMEH", FontFactory.createBoldFont(16)),
                new KLabel("(B.Sc) Mathematics & Computer Science", FontFactory.createPlainFont(15)),
                new KLabel("The University of The Gambia (2016 - 2020)", FontFactory.createPlainFont(15)));

        final KLabel readLabel = new KLabel("Read more...", FontFactory.createPlainFont(15), Color.BLUE);
        readLabel.underline(false);
        readLabel.setCursor(MComponent.HAND_CURSOR);
        readLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AuthorDialog().setVisible(true);
            }
        });

        final KPanel authorLayer = new KPanel();
        authorLayer.addAll(new KPanel(authorIconLabel), authorNamesPanel);

        final Font specialNameFont = FontFactory.createPlainFont(15);
        final KPanel specialNamesLayer = new KPanel();
        specialNamesLayer.setLayout(new BoxLayout(specialNamesLayer, BoxLayout.Y_AXIS));
        specialNamesLayer.addAll(new KLabel("Mr. Fred Sangol Uche  [Lecturer, UTG]", specialNameFont),
                new KLabel("Mahmud S Jallow  [Student, UTG]", specialNameFont),
                new KLabel("Alieu Ceesay  [Student, UTG]", specialNameFont));

        final KPanel respectLayer = new KPanel();
        respectLayer.setLayout(new BoxLayout(respectLayer, BoxLayout.Y_AXIS));
        respectLayer.add(new KPanel(new FlowLayout(FlowLayout.LEFT),
                new KLabel("Special thanks to:", FontFactory.createBoldFont(15))));
        respectLayer.add(new KPanel(specialNamesLayer));
        respectLayer.add(new KPanel(new KLabel("And all the students whose details were used during the \"Testing\"",
                FontFactory.createPlainFont(14), Color.GRAY)));

        final KPanel creditsCard = new KPanel();
        creditsCard.setLayout(new BoxLayout(creditsCard, BoxLayout.Y_AXIS));
        creditsCard.addAll(authorLayer, new KPanel(new FlowLayout(FlowLayout.RIGHT, 25, 5), readLabel),
                MComponent.contentBottomGap(), respectLayer);
        return creditsCard;
    }

    private KPanel getFeedbackCard(){
        final String bettermentText = "<b>Help make Dashboard better by giving the developers a Review</b>." +
                "<p>You may <b>Report a Bug</b> to be fixed, make a <b>Suggestion</b> to be implemented in a future release, " +
                "or provide an <b>Answer</b> to a Frequently Asked Question. By clicking Send, your review shall be delivered " +
                "to the developers' mail address: <b>"+Mailer.DEVELOPER_MAIL +"</b>. Your student-mail might be used for this purpose.</p>";

        final Border lineBorder = BorderFactory.createLineBorder(Color.BLUE, 1,true);
        final Border spaceBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        final Font feedHeadFont = FontFactory.createBoldFont(16);

        final KTextArea reviewTextArea = KTextArea.getLimitedEntryArea(500);
        final KScrollPane reviewTextAreaScroll = reviewTextArea.outerScrollPane(new Dimension(500, 100));
        reviewTextAreaScroll.setBorder(spaceBorder);

        final String reviewString = "If you have any review, you can send write it to the developers in the text-area below. " +
                "The review must exclude any kind of greetings, or introductions.";

        final KButton reviewSender = newReviewSender();
        reviewSender.addActionListener(e-> new Thread(()-> {
            if (Globals.hasNoText(reviewTextArea.getText())) {
                reportBlankReview(reviewTextArea);
            } else {
                MComponent.toggleEnabled(reviewTextArea, reviewSender);
                reviewSender.setText("Sending...");
                if (Internet.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | Review | "+ Student.getFullNamePostOrder(),
                            reviewTextArea.getText());
                    if (gMailer.send()) {
                        reviewTextArea.setText(null);
                    }
                } else {
                    reportNoConnection();
                }
                MComponent.toggleEnabled(reviewTextArea, reviewSender);
                reviewSender.setText("Send");
            }
        }).start());

        final KPanel reviewLayer = new KPanel();
        reviewLayer.setBorder(lineBorder);
        reviewLayer.setLayout(new BoxLayout(reviewLayer, BoxLayout.Y_AXIS));
        reviewLayer.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Give a Review", feedHeadFont)),
                newNotePane(reviewString,50), reviewTextAreaScroll,
                new KPanel(new FlowLayout(FlowLayout.RIGHT), reviewSender));

        final KTextArea suggestionTextArea = KTextArea.getLimitedEntryArea(500);
        final KScrollPane suggestionTextAreaScroll = suggestionTextArea.outerScrollPane(new Dimension(500, 100));
        suggestionTextAreaScroll.setBorder(spaceBorder);

        final String suggestionString = "In no more than 500 characters, briefly state, in the text-area below, a feature " +
                "you'd like to use in a future release of Dashboard.";

        final KButton suggestionSender = newReviewSender();
        suggestionSender.addActionListener(e-> new Thread(()-> {
            if (Globals.hasNoText(suggestionTextArea.getText())) {
                reportBlankReview(suggestionTextArea);
            } else {
                MComponent.toggleEnabled(suggestionTextArea,suggestionSender);
                suggestionSender.setText("Sending...");
                if (Internet.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | Suggestion |"+
                            Student.getFullNamePostOrder(),
                            suggestionTextArea.getText());
                    if (gMailer.send()) {
                        suggestionTextArea.setText(null);
                    }
                } else {
                    reportNoConnection();
                }
                MComponent.toggleEnabled(suggestionTextArea,suggestionSender);
                suggestionSender.setText("Send");
            }
        }).start());

        final KPanel suggestionLayer = new KPanel();
        suggestionLayer.setBorder(lineBorder);
        suggestionLayer.setLayout(new BoxLayout(suggestionLayer, BoxLayout.Y_AXIS));
        suggestionLayer.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT),
                        new KLabel("Suggest a Feature", feedHeadFont)), newNotePane(suggestionString,50),
                suggestionTextAreaScroll, new KPanel(new FlowLayout(FlowLayout.RIGHT), suggestionSender));

        final KTextField answerTitleField = KTextField.rangeControlField(100);
        answerTitleField.setPreferredSize(new Dimension(550, 30));

        final KTextArea answerTextArea = KTextArea.getLimitedEntryArea(500);
        final KScrollPane answerTextAreaScroll = answerTextArea.outerScrollPane(new Dimension(500, 100));
        answerTextAreaScroll.setBorder(spaceBorder);
        final String answerString = "Answer a problem faced by students at UTG and benefit your brothers and sisters! " +
                "Please refer to 'Home | FAQs & Help | UTG FAQs' to ensure your question is not already answered.";

        final KButton answerSender = newReviewSender();
        answerSender.addActionListener(e-> {
            if (Globals.hasNoText(answerTitleField.getText())) {
                App.reportError(getRootPane(),"No Question",
                        "Please provide the question by filling out the Text Field.");
                answerTitleField.requestFocusInWindow();
                return;
            }

            if (Globals.hasNoText(answerTextArea.getText())) {
                reportBlankReview(answerTextArea);
                return;
            }

            new Thread(()-> {
                MComponent.toggleEnabled(answerTitleField, answerTextArea, answerSender);
                answerSender.setText("Sending...");
                if (Internet.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | FAQ & Answer"+
                            Student.getFullNamePostOrder(),
                            "Question: "+answerTitleField.getText()+"\nAnswer: "+answerTextArea.getText());
                    if (gMailer.send()) {
                        answerTitleField.setText(null);
                        answerTextArea.setText(null);
                    }
                } else {
                    reportNoConnection();
                }
                MComponent.toggleEnabled(answerTitleField,answerTextArea,answerSender);
                answerSender.setText("Send");
            }).start();
        });

        final KPanel titleSubstance = new KPanel(new BorderLayout());
        titleSubstance.add(new KPanel(new KLabel("Question:", FontFactory.createPlainFont(15))),
                BorderLayout.WEST);
        titleSubstance.add(new KPanel(answerTitleField), BorderLayout.CENTER);

        final KPanel bodySubstance = new KPanel(new BorderLayout());
        bodySubstance.add(new KPanel(new KLabel("Answer:", FontFactory.createPlainFont(15))),
                BorderLayout.WEST);
        bodySubstance.add(answerTextAreaScroll, BorderLayout.CENTER);

        final KPanel answerSubstance = new KPanel();
        answerSubstance.setLayout(new BoxLayout(answerSubstance, BoxLayout.Y_AXIS));
        answerSubstance.addAll(titleSubstance, Box.createVerticalStrut(10), bodySubstance);

        final KPanel answerLayer = new KPanel();
        answerLayer.setBorder(lineBorder);
        answerLayer.setLayout(new BoxLayout(answerLayer, BoxLayout.Y_AXIS));
        answerLayer.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Answer a FAQ", feedHeadFont)),
                newNotePane(answerString,75), answerSubstance,
                new KPanel(new FlowLayout(FlowLayout.RIGHT), answerSender));

        final KTextArea bugTextArea = KTextArea.getLimitedEntryArea(500);
        final JScrollPane bugTextAreaScroll = bugTextArea.outerScrollPane(new Dimension(500,100));
        bugTextAreaScroll.setBorder(spaceBorder);
        final String bugString = "In no more than 500 characters, " +
                "kindly describe a problem (if there is any) you encountered while using Dashboard.";

        final KButton bugSender = newReviewSender();
        bugSender.addActionListener(e-> new Thread(()-> {
            if (Globals.hasNoText(bugTextArea.getText())) {
                reportBlankReview(bugTextArea);
            } else {
                MComponent.toggleEnabled(bugTextArea,bugSender);
                bugSender.setText("Sending...");
                if (Internet.isInternetAvailable()) {
                    final Mailer gMailer = new Mailer("Dashboard Feedback | A Bug Report | "+
                            Student.getFullNamePostOrder(), bugTextArea.getText());
                    if (gMailer.send()) {
                        bugTextArea.setText(null);
                    }
                    bugTextArea.setEditable(true);
                } else {
                    reportNoConnection();
                }
                MComponent.toggleEnabled(bugTextArea,bugSender);
                bugSender.setText("Send");
            }
        }).start());

        final KPanel bugLayer = new KPanel();
        bugLayer.setBorder(lineBorder);
        bugLayer.setLayout(new BoxLayout(bugLayer, BoxLayout.Y_AXIS));
        bugLayer.addAll(new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Report a Problem", feedHeadFont)),
                newNotePane(bugString,50), bugTextAreaScroll,
                new KPanel(new FlowLayout(FlowLayout.RIGHT), bugSender));

        final KPanel feedbackCard = new KPanel();
        feedbackCard.setBorder(spaceBorder);
        feedbackCard.setLayout(new BoxLayout(feedbackCard, BoxLayout.Y_AXIS));
        feedbackCard.addAll(newNotePane(bettermentText, 130), Box.createVerticalStrut(25),
                reviewLayer, Box.createVerticalStrut(20), suggestionLayer, Box.createVerticalStrut(20),
                answerLayer, Box.createVerticalStrut(20), bugLayer, Box.createVerticalStrut(10));
        return feedbackCard;
    }

    /**
     * Creates a sender button.
     */
    private KButton newReviewSender(){
        final KButton button = new KButton("Send");
        button.setStyle(FontFactory.createPlainFont(14), Color.BLUE);
        return button;
    }

    private void reportBlankReview(KTextArea textArea){
        App.reportError(getRootPane(), "Blank", "Cannot send a blank review. Fill out the Text Area first.");
        textArea.requestFocusInWindow();
    }

    private void reportNoConnection(){
        App.reportError(getRootPane(), "No Internet", "Internet connection is required to send a Review.\n" +
                "Please connect and try again.");
    }

    private KPanel getDonateCard(){
        final String donationText = "Dashboard is not currently backed, funded, or sponsored by any institution or organization. " +
                "Therefore, to guarantee our long term of service, and continuous update, we humbly welcome all kinds of " +
                "donations. (Every Dalasi will count!)";

        final KPanel donationCard = new KPanel();
        donationCard.setLayout(new BoxLayout(donationCard, BoxLayout.Y_AXIS));
        donationCard.addAll(newNotePane(donationText, 100));
        return donationCard;
    }

    private KPanel getTermsCard(){
        final String termsString = "Your usage of the <b>Personal Dashboard</b> is subject to the following terms and conditions:" +
                "<p>It is strictly out of bounds that any part of this tool be used, directly or indirectly, " +
                "in your course work-projects like Junior Project, Senior Project, etc. Such an act will lead to a serious " +
                "academic penalty. In a nutshell, no part of this project may be modified or reproduced without the prior " +
                "written permission of the authors. This product contains sensitive mechanisms, " +
                "thus tampering with any can be detrimental to the students, or even burden the servers of the portal.</p>" +
                "<p style='text-align: right;'><b>__Muhammed W. Drammeh</b></p>";

        final KPanel termsCard = new KPanel();
        termsCard.setLayout(new BoxLayout(termsCard, BoxLayout.Y_AXIS));
        termsCard.addAll(newNotePane(termsString, 200));
        return termsCard;
    }

    /**
     * Returns a text-pane with the given text, and height.
     * The text is loaded as html.
     */
    private KTextPane newNotePane(String text, int height){
        final KTextPane textPane = KTextPane.htmlFormattedPane(text);
        textPane.setPreferredSize(new Dimension(getPreferredSize().width, height));
        textPane.setOpaque(false);
        return textPane;
    }


    private static class AuthorDialog extends KDialog {

        private AuthorDialog(){
            super("Author");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final Font hintFont = FontFactory.createBoldFont(15);
            final Font valueFont = FontFactory.createPlainFont(16);

            final KPanel firstNamePanel = new KPanel(new BorderLayout());
            firstNamePanel.add(new KPanel(new KLabel("First Name:", hintFont)), BorderLayout.WEST);
            firstNamePanel.add(new KPanel(new KLabel("Drammeh", valueFont)), BorderLayout.CENTER);

            final KPanel lastNamePanel = new KPanel(new BorderLayout());
            lastNamePanel.add(new KPanel(new KLabel("Last Name:", hintFont)), BorderLayout.WEST);
            lastNamePanel.add(new KPanel(new KLabel("Muhammed W.", valueFont)), BorderLayout.CENTER);

            final KPanel dobPanel = new KPanel(new BorderLayout());
            dobPanel.add(new KPanel(new KLabel("Date of Birth:", hintFont)), BorderLayout.WEST);
            dobPanel.add(new KPanel(new KLabel("Jan 01, 1999", valueFont)), BorderLayout.CENTER);

            final KPanel pobPanel = new KPanel(new BorderLayout());
            pobPanel.add(new KPanel(new KLabel("Place of Birth:", hintFont)), BorderLayout.WEST);
            pobPanel.add(new KPanel(new KLabel("Diabugu Batapa, Sandu District, URR", valueFont)),
                    BorderLayout.CENTER);

            final KPanel addressPanel = new KPanel(new BorderLayout());
            addressPanel.add(new KPanel(new KLabel("Address:", hintFont)), BorderLayout.WEST);
            addressPanel.add(new KPanel(new KLabel("Sukuta, Kombo North", valueFont)), BorderLayout.CENTER);

            final KPanel telephonePanel = new KPanel(new BorderLayout());
            telephonePanel.add(new KPanel(new KLabel("Telephone:", hintFont)), BorderLayout.WEST);
            telephonePanel.add(new KPanel(new KLabel("+220 3413910", valueFont)), BorderLayout.CENTER);

            final KPanel emailPanel = new KPanel(new BorderLayout());
            emailPanel.add(new KPanel(new KLabel("Email Address:", hintFont)), BorderLayout.WEST);
            emailPanel.add(new KPanel(new KLabel("wakadrammeh@gmail.com", valueFont)), BorderLayout.CENTER);

            final KPanel nationalityPanel = new KPanel(new BorderLayout());
            nationalityPanel.add(new KPanel(new KLabel("Nationality:", hintFont)), BorderLayout.WEST);
            nationalityPanel.add(new KPanel(new KLabel("Gambian", valueFont)), BorderLayout.CENTER);

            final KButton closeButton = new KButton("Close");
            closeButton.addActionListener(e-> dispose());

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(firstNamePanel, lastNamePanel, dobPanel, pobPanel, addressPanel, telephonePanel,
                    emailPanel, nationalityPanel, MComponent.contentBottomGap(),
                    new KPanel(new FlowLayout(FlowLayout.RIGHT), closeButton));

            getRootPane().setDefaultButton(closeButton);
            setContentPane(contentPanel);
            pack();
            setLocationRelativeTo(getRootPane());
        }
    }

}

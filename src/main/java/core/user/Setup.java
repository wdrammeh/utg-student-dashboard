package core.user;

import core.Board;
import core.first.Welcome;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;

/**
 * For setting up the user for a trial only.
 */
public class Setup extends KDialog {


    public Setup(Welcome welcome){
        super("Try Dashboard");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        final KPanel hintPanel = new KPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.add(new KPanel(new KLabel("Welcome!", KFontFactory.createBoldFont(25))));
        hintPanel.add(new KPanel(new KLabel("Kindly fill out the following fields to get started",
                KFontFactory.createPlainFont(20), Color.GRAY)));
        hintPanel.add(Box.createRigidArea(new Dimension(450, 25)));

        final Dimension fieldDim = new Dimension(235, 30);

        final KTextField firstNameField = new KTextField(fieldDim);
        final KPanel firstNameLayer = new KPanel(new BorderLayout());
        firstNameLayer.add(new KPanel(new FlowLayout(FlowLayout.RIGHT), newHintLabel("First Name:")),
                BorderLayout.WEST);
        firstNameLayer.add(new KPanel(firstNameField), BorderLayout.CENTER);

        final KTextField lastNameField = new KTextField(fieldDim);
        final KPanel lastNameLayer = new KPanel(new BorderLayout());
        lastNameLayer.add(new KPanel(newHintLabel("Last Name:")), BorderLayout.WEST);
        lastNameLayer.add(new KPanel(lastNameField), BorderLayout.CENTER);

        final KTextField nationalityField = new KTextField(fieldDim);
        final KPanel nationalityLayer = new KPanel(new BorderLayout());
        nationalityLayer.add(new KPanel(newHintLabel("Nationality:")), BorderLayout.WEST);
        nationalityLayer.add(new KPanel(nationalityField), BorderLayout.CENTER);

        final KTextField addressField = new KTextField(fieldDim);
        final KPanel addressLayer = new KPanel(new BorderLayout());
        addressLayer.add(new KPanel(newHintLabel("Address:")), BorderLayout.WEST);
        addressLayer.add(new KPanel(addressField), BorderLayout.CENTER);

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(hintPanel, firstNameLayer, lastNameLayer, nationalityLayer, addressLayer,
                MComponent.contentBottomGap());

        final KButton startButton = new KButton("Start Dashboard");
        startButton.setFont(KFontFactory.createPlainFont(15));
        startButton.addActionListener(e-> {
            if (!Globals.hasText(firstNameField.getText())) {
                signalMissingInfo("First Name", firstNameField);
            } else if (!Globals.hasText(lastNameField.getText())) {
                signalMissingInfo("Last Name", lastNameField);
            } else if (!Globals.hasText(nationalityField.getText())) {
                signalMissingInfo("Nationality", nationalityField);
            } else if (!Globals.hasText(addressField.getText())) {
                signalMissingInfo("Address", addressField);
            } else {
                dispose();
                Student.setupTrial(new String[]{firstNameField.getText(), lastNameField.getText(),
                        nationalityField.getText(), addressField.getText()});
                new Board().setVisible(true);
            }
        });

        final KButton backButton = new KButton("Cancel");
        backButton.setFont(KFontFactory.createPlainFont(15));
        backButton.addActionListener(e-> {
            dispose();
            welcome.setVisible(true);
        });

        rootPane.setDefaultButton(startButton);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        getContentPane().add(new KPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5), backButton, startButton),
                BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private static KLabel newHintLabel(String string) {
        return new KLabel(string, KFontFactory.createBoldFont(16));
    }

    private void signalMissingInfo(String info, Component c){
        App.reportError(getRootPane(), "Error", String.format("Please enter your %s.", info));
        c.requestFocusInWindow();
    }

}

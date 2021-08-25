package core.task.creator;

import core.Board;
import core.task.handler.ProjectHandler;
import core.task.self.ProjectSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.KComponent;
import core.utils.KFontFactory;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

import static core.task.creator.TodoCreator.DESCRIPTION_LIMIT;

public class ProjectCreator extends KDialog {
    private KTextField nameField;
    private KComboBox<Object> durationBox;
    private KComboBox<String> typeBox;
    private KButton createButton;

    public ProjectCreator(){
        super("Create Project");
        setModalityType(TodoCreator.DEFAULT_MODALITY_TYPE);
        setResizable(true);

        final Font labelsFont = KFontFactory.createBoldFont(16);
        final Font boxFont = KFontFactory.createPlainFont(15);
        final Dimension panelsDimension = new Dimension(465,35);

        nameField = KTextField.rangeControlField(DESCRIPTION_LIMIT);
        nameField.setPreferredSize(new Dimension(310, 30));
        final KPanel namePanelPlus = new KPanel(new BorderLayout(), panelsDimension);
        namePanelPlus.add(new KPanel(new KLabel("Project Name:", labelsFont)), BorderLayout.WEST);
        namePanelPlus.add(new KPanel(nameField), BorderLayout.CENTER);

        typeBox = new KComboBox<>(new String[]{"Java", "Python", "C/C++", "C#", "Database", "Web", "Other"});
        typeBox.setFont(boxFont);
        typeBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final KPanel typePanelPlus = new KPanel(new BorderLayout(),panelsDimension);
        typePanelPlus.add(new KPanel(new KLabel("Project Type:", labelsFont)), BorderLayout.WEST);
        typePanelPlus.add(new KPanel(typeBox), BorderLayout.CENTER);

        durationBox = new KComboBox<>(new Object[] {"Five Days", "One Week", "Two Weeks", "Three Weeks", "One Month", "Two Months", "Three Months", "Six Months"});
        durationBox.setFont(boxFont);
        durationBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final KPanel durationPanelPlus = new KPanel(new BorderLayout(),panelsDimension);
        durationPanelPlus.add(new KPanel(new KLabel("Specified Duration:",labelsFont)),BorderLayout.WEST);
        durationPanelPlus.add(new KPanel(durationBox),BorderLayout.CENTER);

        final KButton cancelButton = new KButton("Cancel");
        cancelButton.addActionListener(e-> dispose());
        createButton = new KButton("Create");
        createButton.addActionListener(listener());
        rootPane.setDefaultButton(createButton);

        final KPanel contentPlate = new KPanel();
        contentPlate.setLayout(new BoxLayout(contentPlate,BoxLayout.Y_AXIS));
        contentPlate.addAll(namePanelPlus,typePanelPlus, durationPanelPlus, KComponent.createRigidArea(400, 25),
                new KPanel(new FlowLayout(FlowLayout.RIGHT), cancelButton, createButton));
        setContentPane(contentPlate);
        pack();
        setMinimumSize(this.getPreferredSize());
        setLocationRelativeTo(Board.getRoot());
    }

    private ActionListener listener(){
        return e -> {
            final String name = nameField.getText();
            int givenDays = 0;
            if (Globals.hasNoText(name)) {
                App.reportError("No Name","Please specify a name for the project.");
                nameField.requestFocusInWindow();
            } else if (name.length() > DESCRIPTION_LIMIT) {
                App.reportError("Error", "Sorry, name of a project must be at most "+
                        DESCRIPTION_LIMIT +" characters.");
            } else {
                final String dDuration = durationBox.getSelectionText();
                if (Objects.equals(dDuration, "Five Days")) {
                    givenDays = 5;
                } else if (Objects.equals(dDuration, "One Week")) {
                    givenDays = 7;
                } else if (Objects.equals(dDuration, "Two Weeks")) {
                    givenDays = 14;
                } else if (Objects.equals(dDuration, "Three Weeks")) {
                    givenDays = 21;
                } else if (Objects.equals(dDuration, "One Month")) {
                    givenDays = 30;
                } else if (Objects.equals(dDuration, "Two Months")) {
                    givenDays = 60;
                } else if (Objects.equals(dDuration, "Three Months")) {
                    givenDays = 90;
                } else if (Objects.equals(dDuration, "Six Months")) {
                    givenDays = 180;
                }

                ProjectHandler.newIncoming(new ProjectSelf(name, typeBox.getSelectionText(), givenDays));
                dispose();
            }
        };
    }

}

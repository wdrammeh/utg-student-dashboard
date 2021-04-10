package core.task.creator;

import core.Board;
import core.task.handler.ProjectHandler;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;

import static core.task.creator.TodoCreator.DESCRIPTION_LIMIT;

public class ProjectCreator extends KDialog {
    private KTextField nameField;
    private JComboBox<Object> durationBox;
    private JComboBox<String> typeBox;
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

        typeBox = new JComboBox<>(new String[]{"Java", "Python", "C/C++", "C#", "Database", "Web", "Other"});
        typeBox.setFont(boxFont);
        typeBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final KPanel typePanelPlus = new KPanel(new BorderLayout(),panelsDimension);
        typePanelPlus.add(new KPanel(new KLabel("Project Type:", labelsFont)), BorderLayout.WEST);
        typePanelPlus.add(new KPanel(typeBox), BorderLayout.CENTER);

        durationBox = new JComboBox<>(new Object[] {"Five Days", "One Week", "Two Weeks", "Three Weeks", "One Month", "Two Months", "Three Months", "Six Months"});
        durationBox.setFont(boxFont);
        durationBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final KPanel durationPanelPlus = new KPanel(new BorderLayout(),panelsDimension);
        durationPanelPlus.add(new KPanel(new KLabel("Specified Duration:",labelsFont)),BorderLayout.WEST);
        durationPanelPlus.add(new KPanel(durationBox),BorderLayout.CENTER);

        final KButton cancelButton = new KButton("Cancel");
        cancelButton.addActionListener(e-> dispose());
        createButton = new KButton("Create");
        createButton.addActionListener(ProjectHandler.additionWaiter());
        rootPane.setDefaultButton(createButton);

        final KPanel contentPlate = new KPanel();
        contentPlate.setLayout(new BoxLayout(contentPlate,BoxLayout.Y_AXIS));
        contentPlate.addAll(namePanelPlus,typePanelPlus, durationPanelPlus, MComponent.createRigidArea(400, 25),
                new KPanel(new FlowLayout(FlowLayout.RIGHT), cancelButton, createButton));
        setContentPane(contentPlate);
        pack();
        setMinimumSize(this.getPreferredSize());
        setLocationRelativeTo(Board.getRoot());
    }

    public KTextField getNameField(){
        return nameField;
    }

    public String getTheType(){
        return String.valueOf(typeBox.getSelectedItem());
    }

    public String getTheDuration(){
        return String.valueOf(durationBox.getSelectedItem());
    }

}

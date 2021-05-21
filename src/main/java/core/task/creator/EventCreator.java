package core.task.creator;

import core.Board;
import core.module.SemesterActivity;
import core.task.handler.EventHandler;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;

import javax.swing.*;
import java.awt.*;

import static core.task.creator.TodoCreator.DESCRIPTION_LIMIT;

public class EventCreator extends KDialog {
    public static final int TEST = 0;
    public static final int EXAM = 1;
    public static final int OTHER = 2;
    private String eventType;
    private KTextField descriptionField;
    private KTextField dayField, monthField, yearField;

    public EventCreator(int eType){
        setModalityType(TodoCreator.DEFAULT_MODALITY_TYPE);
        setResizable(true);
        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        final KLabel typeLabel = new KLabel();
        typeLabel.setFont(KFontFactory.createBoldFont(16));
        final KLabel dateLabel = new KLabel();
        dateLabel.setFont(KFontFactory.createBoldFont(16));

        final KPanel importPanel = new KPanel();
        final String[] activeNames = SemesterActivity.names();
        if (activeNames.length >= 1) {
            final KComboBox<String> importBox = new KComboBox<>(activeNames);
            importBox.setFont(KFontFactory.createPlainFont(15));
            importBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            importBox.setFocusable(false);
            importBox.setToolTipText("Import");
            importBox.addActionListener(e -> {
                descriptionField.setText(String.valueOf(importBox.getSelectedItem()));
                dayField.requestFocusInWindow();
            });
            importPanel.add(importBox);
        }

        if (eType == TEST) {
            setTitle("Upcoming Test");
            typeLabel.setText("Course name:");
            dateLabel.setText("Test Date:");
            eventType = "Test";
            contentPanel.add(importPanel);
        } else if (eType == EXAM) {
            setTitle("Upcoming Exam");
            typeLabel.setText("Course name:");
            dateLabel.setText("Exam Date:");
            eventType = "Exam";
            contentPanel.add(importPanel);
        } else if (eType == OTHER) {
            setTitle("Upcoming Event");
            typeLabel.setText("Event title:");
            dateLabel.setText("Date:");
            eventType = "Event";
        }

        final Font labelsFont = KFontFactory.createPlainFont(16);
        final Dimension typicalPanelsDimension = new Dimension(465,35);

        descriptionField = KTextField.rangeControlField(DESCRIPTION_LIMIT);
        descriptionField.setPreferredSize(new Dimension(310,30));
        final KPanel titleLayer = new KPanel(new BorderLayout(),typicalPanelsDimension);
        titleLayer.add(new KPanel(typeLabel), BorderLayout.WEST);
        titleLayer.add(new KPanel(descriptionField), BorderLayout.CENTER);

        dayField = KTextField.dayField();
        monthField = KTextField.monthField();
        yearField = KTextField.yearField();

        final KPanel dateFieldsPanel = new KPanel();
        dateFieldsPanel.addAll(new KLabel("Day",labelsFont),dayField, MComponent.createRigidArea(10, 20),
                new KLabel("Month",labelsFont),monthField, MComponent.createRigidArea(10, 20),new KLabel("Year",labelsFont),yearField);
        final KPanel datesLayer = new KPanel(new BorderLayout(),typicalPanelsDimension);
        datesLayer.add(new KPanel(dateLabel),BorderLayout.WEST);
        datesLayer.add(dateFieldsPanel,BorderLayout.CENTER);

        final KButton cancelButton = new KButton("Cancel");
        cancelButton.addActionListener(e -> this.dispose());

        final KButton addButton = new KButton("Add");
        addButton.addActionListener(EventHandler.newListener());//No fear - if value was not one of the specified 3, compiler won't reach this line

        rootPane.setDefaultButton(addButton);
        contentPanel.addAll(titleLayer, datesLayer, MComponent.contentBottomGap(),
                new KPanel(new FlowLayout(FlowLayout.RIGHT), cancelButton, addButton));
        setContentPane(contentPanel);
        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(Board.getRoot());
    }

    public KTextField getDescriptionField(){
        return descriptionField;
    }

    public String getProvidedDate() {
        if(Globals.hasNoText(dayField.getText()) || Globals.hasNoText(monthField.getText()) || Globals.hasNoText(yearField.getText())) {
            return "";
        }
        final String sep = MDate.VAL_SEP;
        return dayField.getText()+sep+monthField.getText()+sep+yearField.getText();
    }

    public String type(){
        return eventType;
    }

}
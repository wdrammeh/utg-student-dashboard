package core.task.creator;

import core.Board;
import core.task.handler.TodoHandler;
import core.task.self.TodoSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.KComponent;
import core.utils.KFontFactory;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

public class TodoCreator extends KDialog {
    private KTextField descriptionField;
    private KComboBox<Object> durationBox;
    public static final int DESCRIPTION_LIMIT = 50; // Note that this should be increased.


    public TodoCreator(){
        super("Create Task");
        setModalityType(TodoCreator.DEFAULT_MODALITY_TYPE);
        setResizable(true);

        final Dimension platesDimension = new Dimension(475, 35);
        final Dimension fieldsDimension = new Dimension(310, 30);
        final Font labelsFont = KFontFactory.createBoldFont(16);

        descriptionField = KTextField.rangeControlField(DESCRIPTION_LIMIT);
        descriptionField.setPreferredSize(fieldsDimension);
        final KPanel namePlate = new KPanel(new BorderLayout(), platesDimension);
        namePlate.add(new KPanel(new KLabel("Task Description:", labelsFont)), BorderLayout.WEST);
        namePlate.add(new KPanel(descriptionField), BorderLayout.CENTER);

        durationBox = new KComboBox<>(new Object[] {"Five Days", "One Week", "Two Weeks", "Three Weeks", "One Month"});
        durationBox.setFont(KFontFactory.createPlainFont(15));
        durationBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final KPanel durationPlate = new KPanel(new BorderLayout(), platesDimension);
        durationPlate.add(new KPanel(new KLabel("To be completed in:", labelsFont)), BorderLayout.WEST);
        durationPlate.add(new KPanel(durationBox), BorderLayout.CENTER);

        final KButton quitButton = new KButton("Cancel");
        quitButton.addActionListener(e -> dispose());
        //The button which returns 'true' signaling that user provides all required inputs for a task to be joined
        final KButton createButton = new KButton("Create");
        createButton.setFocusable(true);
        createButton.addActionListener(listener());

        rootPane.setDefaultButton(createButton);
        final KPanel contentPlate = new KPanel();
        contentPlate.setLayout(new BoxLayout(contentPlate,BoxLayout.Y_AXIS));
        contentPlate.addAll(namePlate,durationPlate, KComponent.contentBottomGap(),
                new KPanel(new FlowLayout(FlowLayout.RIGHT), quitButton, createButton));
        setContentPane(contentPlate);
        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(Board.getRoot());
    }

    private ActionListener listener(){
        return e -> {
            final String name = descriptionField.getText();
            int givenDays = 0;
            if (Globals.hasNoText(name)) {
                App.reportError(getRootPane(), "No Name", "Please specify a name for the task.");
                descriptionField.requestFocusInWindow();
            } else if (name.length() > DESCRIPTION_LIMIT) {
                App.reportError("Error", "Sorry, description of a task must be at most "+
                        DESCRIPTION_LIMIT +" characters.");
            } else {
                final String span = durationBox.getSelectionText();
                if (Objects.equals(span, "Five Days")) {
                    givenDays = 5;
                } else if (Objects.equals(span, "One Week")) {
                    givenDays = 7;
                } else if (Objects.equals(span, "Two Weeks")) {
                    givenDays = 14;
                } else if (Objects.equals(span, "Three Weeks")) {
                    givenDays = 21;
                } else if (Objects.equals(span, "One Month")) {
                    givenDays = 30;
                }
                TodoHandler.newIncoming(new TodoSelf(name, givenDays));
                dispose();
            }
        };
    }

}

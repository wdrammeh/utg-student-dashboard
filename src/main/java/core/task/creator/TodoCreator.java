package core.task.creator;

import core.Board;
import core.task.handler.TodoHandler;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;

public class TodoCreator extends KDialog {
    private KTextField descriptionField;
    private JComboBox<Object> durationBox;
    private KButton createButton;//The button which returns 'true' signaling that user provides all required inputs for a task to be joined
    public static final int DESCRIPTION_LIMIT = 50; // Note that this should be increased.
    // It's only small because of irregular component arrangement, and fixed-layouts.


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

        durationBox = new JComboBox<>(new Object[] {"Five Days", "One Week", "Two Weeks", "Three Weeks", "One Month"});
        durationBox.setFont(KFontFactory.createPlainFont(15));
        durationBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final KPanel durationPlate = new KPanel(new BorderLayout(), platesDimension);
        durationPlate.add(new KPanel(new KLabel("To be completed in:", labelsFont)), BorderLayout.WEST);
        durationPlate.add(new KPanel(durationBox), BorderLayout.CENTER);

        final KButton quitButton = new KButton("Cancel");
        quitButton.addActionListener(e -> dispose());
        createButton = new KButton("Create");
        createButton.setFocusable(true);
        createButton.addActionListener(TodoHandler.additionWaiter());

        rootPane.setDefaultButton(createButton);
        final KPanel contentPlate = new KPanel();
        contentPlate.setLayout(new BoxLayout(contentPlate,BoxLayout.Y_AXIS));
        contentPlate.addAll(namePlate,durationPlate, MComponent.contentBottomGap(),
                new KPanel(new FlowLayout(FlowLayout.RIGHT), quitButton, createButton));
        setContentPane(contentPlate);
        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(Board.getRoot());
    }

    public KTextField getDescriptionField(){
        return descriptionField;
    }

    public String getDuration(){
        return String.valueOf(durationBox.getSelectedItem());
    }

}

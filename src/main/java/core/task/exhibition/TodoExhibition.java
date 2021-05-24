package core.task.exhibition;

import core.Board;
import core.task.handler.TodoHandler;
import core.task.self.TodoSelf;
import core.utils.Globals;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;

public class TodoExhibition extends KDialog {

    public TodoExhibition(TodoSelf theTask){
        super("Task");
        setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
        setResizable(true);

        final KPanel contentPanel = new KPanel();//To be used... contentPane!
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        final KPanel nameLayer = new KPanel(new BorderLayout());
        nameLayer.add(new KPanel(newHintLabel("Task name:")),BorderLayout.WEST);
        nameLayer.add(new KPanel(newValueLabel(theTask.getDescription())),BorderLayout.CENTER);

        final KPanel stateLayer = new KPanel(new BorderLayout());
        stateLayer.add(new KPanel(newHintLabel("Current state:")),BorderLayout.WEST);
        stateLayer.add(new KPanel(newValueLabel(theTask.isActive() ? "In progress" : "Completed")),BorderLayout.CENTER);

        final KPanel durationLayer = new KPanel(new BorderLayout());
        durationLayer.add(new KPanel(newHintLabel("Specified duration:")),BorderLayout.WEST);
        durationLayer.add(new KPanel(newValueLabel(theTask.getSpecifiedDuration()+" days")),BorderLayout.CENTER);

        final KPanel addDateLayer = new KPanel(new BorderLayout());
        addDateLayer.add(new KPanel(newHintLabel("Date Initiated:")),BorderLayout.WEST);
        addDateLayer.add(new KPanel(newValueLabel(theTask.getStartDate())),BorderLayout.CENTER);

        final KPanel completeDateLayer = new KPanel(new BorderLayout());
        completeDateLayer.add(new KPanel(newHintLabel("Expected to complete:")),BorderLayout.WEST);
        completeDateLayer.add(new KPanel(newValueLabel(theTask.getDateExpectedToComplete())),BorderLayout.CENTER);

        final KPanel soFarLayer = new KPanel(new BorderLayout());
        soFarLayer.add(new KPanel(newHintLabel("Time taken so far:")),BorderLayout.WEST);
        soFarLayer.add(new KPanel(newValueLabel(theTask.getDaysTaken() == 0 ? "Less than a day" :
                Globals.checkPlurality(theTask.getDaysTaken(), "days"))), BorderLayout.CENTER);

        final KPanel remainingLayer = new KPanel(new BorderLayout());
        remainingLayer.add(new KPanel(newHintLabel("Time remaining:")),BorderLayout.WEST);
        remainingLayer.add(new KPanel(newValueLabel(theTask.getDaysLeft() == 1 ? "Less than a day" :
                theTask.getDaysLeft()+" days")),BorderLayout.CENTER);

        final KPanel completedLayer = new KPanel(new BorderLayout());
        completedLayer.add(new KPanel(newHintLabel("Date completed:")),BorderLayout.WEST);
        completedLayer.add(new KPanel(newValueLabel(theTask.getDateCompleted())),BorderLayout.CENTER);

        final KPanel consumedLayer = new KPanel(new BorderLayout());
        consumedLayer.add(new KPanel(newHintLabel("Time consumed:")),BorderLayout.WEST);
        consumedLayer.add(new KPanel(newValueLabel(theTask.getTotalTimeConsumed() == 0 ? "Less than a day" :
                theTask.getTotalTimeConsumed() == theTask.getSpecifiedDuration() ?
                        " Period specified ("+theTask.getSpecifiedDuration()+" days)" :
                        Globals.checkPlurality(theTask.getTotalTimeConsumed(), "days"))),BorderLayout.CENTER);

        if (theTask.isActive()) {
            contentPanel.addAll(nameLayer, stateLayer, addDateLayer, durationLayer, completeDateLayer,
                    soFarLayer, remainingLayer);
        } else {
            contentPanel.addAll(nameLayer, stateLayer, durationLayer, addDateLayer, completedLayer, consumedLayer);
        }

        final KButton doneButton = new KButton("Mark as Done");
        doneButton.setForeground(Color.BLUE);
        doneButton.addActionListener(e -> TodoHandler.transferTask(theTask,this,false));

        final KButton removeButton = new KButton("Remove");
        removeButton.setForeground(Color.RED);
        removeButton.addActionListener(TodoHandler.removalWaiter(theTask,this));

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e -> this.dispose());

        final KPanel buttonsContainer = new KPanel(new FlowLayout(FlowLayout.CENTER));
        if (theTask.isActive()) {
            buttonsContainer.addAll(removeButton,doneButton,closeButton);
        } else {
            buttonsContainer.addAll(removeButton,closeButton);
        }

        contentPanel.addAll(MComponent.contentBottomGap(), buttonsContainer);
        getRootPane().setDefaultButton(closeButton);
        setContentPane(contentPanel);
        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(Board.getRoot());
        SwingUtilities.invokeLater(()-> setVisible(true));
    }

    private static KLabel newHintLabel(String text){
        return new KLabel(text, KFontFactory.createBoldFont(16));
    }

    private static KLabel newValueLabel(String text){
        return new KLabel(text, KFontFactory.createPlainFont(16));
    }

}
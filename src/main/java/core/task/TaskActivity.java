package core.task;

import core.Activity;
import core.Board;
import core.task.handler.AssignmentHandler;
import core.task.handler.EventHandler;
import core.task.handler.ProjectHandler;
import core.task.handler.TodoHandler;
import core.utils.MComponent;
import proto.KButton;
import proto.KFontFactory;
import proto.KLabel;
import proto.KPanel;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * This class packages all its data onto a panel, which is also added to the cardBoard Layout
 * in Board to make it come to sight at the corresponding big-button click.
 * It serves as the intermediary between all the so-called task helpers, like self, creator,
 * exhibitor, etc. and the Board.
 */
public class TaskActivity implements Activity {
    private CardLayout cardLayout;
    private KPanel inPanel;
    private static KButton todoBigButton;
    private static KButton projectBigButton;
    private static KButton assignmentBigButton;
    private static KButton eventBigButton;
    private static KLabel hintLabel;
    public static final Font TASK_HEADERS_FONT = KFontFactory.createPlainFont(20);
    public static final Font TASK_BUTTONS_FONT = KFontFactory.createPlainFont(15);
    public static final int CONTENTS_POSITION = FlowLayout.LEFT;


    public TaskActivity(){
        hintLabel = KLabel.getPredefinedLabel("My Tasks", SwingConstants.LEFT);
        hintLabel.setStyle(KFontFactory.BODY_HEAD_FONT, Color.BLUE);

        cardLayout = new CardLayout();
        inPanel = new KPanel(cardLayout);

        final KPanel tasksHome = new KPanel(new FlowLayout(FlowLayout.LEFT));
        cardLayout.addLayoutComponent(inPanel.add(tasksHome), "Home");
        tasksHome.addAll(giveTodoButton(), Box.createHorizontalStrut(25),
                giveProjectsButton(), Box.createHorizontalStrut(25),
                giveAssignmentsButton(), Box.createHorizontalStrut(25),
                giveEventsButton());

        final KButton returnButton = new KButton("Return");
        returnButton.setFont(TASK_BUTTONS_FONT);
        returnButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Home");
            hintLabel.setText("");
        });
        returnButton.setMnemonic(KeyEvent.VK_BACK_SPACE);
        returnButton.setToolTipText("Back (Alt+Back_Space)");

        final KPanel upperPanel = new KPanel(new BorderLayout());
        upperPanel.add(new KPanel(hintLabel), BorderLayout.WEST);
        upperPanel.add(new KPanel(returnButton), BorderLayout.EAST);

        final KPanel activityPanel = new KPanel(new BorderLayout());
        activityPanel.add(upperPanel, BorderLayout.NORTH);
        activityPanel.add(inPanel);
        if (!Dashboard.isFirst()) {
            TaskCentral.deSerializeAll();
        }
        Board.addCard(activityPanel, "Tasks");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Tasks");
    }

    /**
     * Basically, returns a big-button that'll be added to the home. And also configures
     * its on-click action by adding its succeeding component to the card.
     * The rest perform the same.
     */
    private KButton giveTodoButton(){
        todoBigButton = newBigButton("TODO", TodoHandler.getActiveCount());
        final TodoHandler todoHandler = new TodoHandler(todoBigButton);
        cardLayout.addLayoutComponent(inPanel.add(todoHandler.getComponent()),"TODO");
        todoBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"TODO");
            hintLabel.setText(" > TODO List");
        });
        return todoBigButton;
    }

    private KButton giveProjectsButton(){
        projectBigButton = newBigButton("Projects", ProjectHandler.getLiveCount());
        final ProjectHandler projectHandler = new ProjectHandler(projectBigButton);
        cardLayout.addLayoutComponent(inPanel.add(projectHandler.getComponent()),"Projects");
        projectBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Projects");
            hintLabel.setText(" > Projects");
        });
        return projectBigButton;
    }

    private KButton giveAssignmentsButton(){
        assignmentBigButton = newBigButton("Assignments", AssignmentHandler.getDoingCount());
        final AssignmentHandler assignmentHandler = new AssignmentHandler(assignmentBigButton);
        cardLayout.addLayoutComponent(inPanel.add(assignmentHandler.getComponent()),"Assignments");
        assignmentBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Assignments");
            hintLabel.setText(" > Assignments");
        });
        return assignmentBigButton;
    }

    private KButton giveEventsButton(){
        eventBigButton = newBigButton("Upcoming", EventHandler.getUpcomingCount());
        final EventHandler eventHandler = new EventHandler(eventBigButton);
        eventBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Events");
            hintLabel.setText(" > Upcoming Events");
        });
        cardLayout.addLayoutComponent(inPanel.add(eventHandler.getComponent()),"Events");
        return eventBigButton;
    }

    /**
     * Provides the shared look of these big-buttons.
     * Note that the setText function of these buttons are forwarded to their inner-label
     * numberText, which indicates the number of tasks running on each.
     */
    private KButton newBigButton(String label, int number){
        final KLabel lText = new KLabel(label, KFontFactory.createBoldFont(17));
        lText.setPreferredSize(new Dimension(175, 30));
        lText.setHorizontalAlignment(KLabel.CENTER);

        final KLabel numberText = new KLabel(Integer.toString(number), KFontFactory.createBoldFont(50));
        numberText.setHorizontalAlignment(KLabel.CENTER);

        final KButton lookButton = new KButton(){
            @Override
            public void setText(String text) {
                numberText.setText(text);
                MComponent.ready(numberText);
            }
        };
        lookButton.setPreferredSize(new Dimension(175, 150));
        lookButton.setLayout(new BorderLayout(0, 0));
        lookButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lookButton.add((lText), BorderLayout.NORTH);
        lookButton.add(numberText, BorderLayout.CENTER);
        return lookButton;
    }

}

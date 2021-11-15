package core.task;

import core.Activity;
import core.Board;
import core.task.handler.AssignmentHandler;
import core.task.handler.EventHandler;
import core.task.handler.ProjectHandler;
import core.task.handler.TodoHandler;
import core.task.self.AssignmentSelf;
import core.task.self.EventSelf;
import core.task.self.ProjectSelf;
import core.task.self.TodoSelf;
import core.utils.App;
import core.utils.FontFactory;
import core.utils.Globals;
import core.utils.Serializer;
import proto.KButton;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * This class packages all its data onto a panel, which is also added to the cardBoard Layout
 * in Board to make it come to sight at the corresponding big-button click.
 * It serves as an intermediary between all the so-called task helpers, like self, creator,
 * exhibitor, etc. and the Board.
 */
public class TaskActivity implements Activity {
    private final KLabel hintLabel;
    private KPanel inPanel;
    private CardLayout cardLayout;
    public static final Font TASK_HEADERS_FONT = FontFactory.createBoldFont(16);
    public static final Font TASK_BUTTONS_FONT = FontFactory.createPlainFont(15);
    public static final int CONTENTS_POSITION = FlowLayout.CENTER;


    public TaskActivity(){
        hintLabel = KLabel.getPredefinedLabel("My Tasks", SwingConstants.LEFT);
        hintLabel.setStyle(FontFactory.BODY_HEAD_FONT, Color.BLUE);

        final KButton returnButton = new KButton("Task Menu");
        returnButton.setFont(TASK_BUTTONS_FONT);
        returnButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Home");
            hintLabel.setText("");
        });
        returnButton.setEnabled(false);

        cardLayout = new CardLayout(){
            @Override
            public void show(Container parent, String name) {
                super.show(parent, name);
                returnButton.setEnabled(!"Home".equals(name));
            }
        };
        inPanel = new KPanel(cardLayout);

        final KPanel tasksHome = new KPanel(new FlowLayout(FlowLayout.LEFT));
        cardLayout.addLayoutComponent(inPanel.add(tasksHome), "Home");
        tasksHome.addAll(giveTodoButton(), Box.createHorizontalStrut(25),
                giveProjectsButton(), Box.createHorizontalStrut(25),
                giveAssignmentsButton(), Box.createHorizontalStrut(25),
                giveEventsButton());

        final KPanel upperPanel = new KPanel(new BorderLayout());
        upperPanel.add(new KPanel(hintLabel), BorderLayout.WEST);
        upperPanel.add(new KPanel(returnButton), BorderLayout.EAST);

        final KPanel activityPanel = new KPanel(new BorderLayout());
        activityPanel.add(upperPanel, BorderLayout.NORTH);
        activityPanel.add(inPanel, BorderLayout.CENTER);
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
        final KButton todoBigButton = newBigButton("TODO");
        TodoHandler.initHandle(todoBigButton);
        cardLayout.addLayoutComponent(inPanel.add(TodoHandler.getComponent()),"TODO");
        todoBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"TODO");
            hintLabel.setText(" > TODO List");
        });
        return todoBigButton;
    }

    private KButton giveProjectsButton(){
        final KButton projectBigButton = newBigButton("Projects");
        ProjectHandler.initHandle(projectBigButton);
        cardLayout.addLayoutComponent(inPanel.add(ProjectHandler.getComponent()),"Projects");
        projectBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Projects");
            hintLabel.setText(" > Projects");
        });
        return projectBigButton;
    }

    private KButton giveAssignmentsButton(){
        final KButton assignmentBigButton = newBigButton("Assignments");
        AssignmentHandler.initHandle(assignmentBigButton);
        cardLayout.addLayoutComponent(inPanel.add(AssignmentHandler.getComponent()),"Assignments");
        assignmentBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Assignments");
            hintLabel.setText(" > Assignments");
        });
        return assignmentBigButton;
    }

    private KButton giveEventsButton(){
        final KButton eventBigButton = newBigButton("Events");
        EventHandler.initHandle(eventBigButton);
        cardLayout.addLayoutComponent(inPanel.add(EventHandler.getComponent()),"Events");
        eventBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Events");
            hintLabel.setText(" > Upcoming Events");
        });
        return eventBigButton;
    }

    /**
     * Provides the shared look of these big-buttons.
     * Note that the setText function of these buttons are forwarded to their inner-label
     * numberText, which indicates the number of tasks running on each.
     */
    private KButton newBigButton(String label){
        final KLabel lText = new KLabel(label, FontFactory.createBoldFont(17));
        lText.setPreferredSize(new Dimension(175, 30));
        lText.setHorizontalAlignment(KLabel.CENTER);

        final KLabel numberLabel = new KLabel("0", FontFactory.createBoldFont(50));
        numberLabel.setHorizontalAlignment(KLabel.CENTER);

        final KButton lookButton = new KButton(){
            @Override
            public void setText(String text) {
                numberLabel.setText(text);
            }
        };
        lookButton.setPreferredSize(new Dimension(175, 150));
        lookButton.setLayout(new BorderLayout(0, 0));
        lookButton.add((lText), BorderLayout.NORTH);
        lookButton.add(numberLabel, BorderLayout.CENTER);
        return lookButton;
    }

    public static void serializeAll(){
        try {
            final ArrayList<TodoSelf> todoList = TodoHandler.TODOS;
            final String[] todos = new String[todoList.size()];
            for (int i = 0; i < todoList.size(); i++) {
                todos[i] = todoList.get(i).export();
            }
            Serializer.toDisk(todos, Serializer.inPath("tasks", "todos.ser"));
        } catch (Exception e) {
            App.silenceException(e);
        }

        try {
            final ArrayList<ProjectSelf> projectList = ProjectHandler.PROJECTS;
            final String[] projects = new String[projectList.size()];
            for(int i = 0; i < projectList.size(); i++){
                projects[i] = projectList.get(i).export();
            }
            Serializer.toDisk(projects, Serializer.inPath("tasks", "projects.ser"));
        } catch (Exception e) {
            App.silenceException(e);
        }

        try {
            final ArrayList<AssignmentSelf> assignmentList = AssignmentHandler.ASSIGNMENTS;
            final String[] assignments = new String[assignmentList.size()];
            final String[] questions = new String[assignments.length];
            final ArrayList<Integer> groupIndexes = new ArrayList<>();
            for (int i = 0; i < assignments.length; i++) {
                final AssignmentSelf assignment = assignmentList.get(i);
                assignments[i] = assignment.export();
                questions[i] = assignment.getQuestion();
                if (assignment.isGroup()) {
                    groupIndexes.add(i);
                }
            }
            Serializer.toDisk(assignments, Serializer.inPath("tasks", "assignments.ser"));
            Serializer.toDisk(questions, Serializer.inPath("tasks", "assignments.questions.ser"));
            final String[] groupsMembers = new String[groupIndexes.size()];
            int j = 0;
            for (int index : groupIndexes) {
                final AssignmentSelf assignment = assignmentList.get(index);
                groupsMembers[j] = Globals.joinLines(assignment.members.toArray());
                j++;
            }
            Serializer.toDisk(groupsMembers, Serializer.inPath("tasks", "assignments.groups.members.ser"));
        } catch (Exception e) {
            App.silenceException(e);
        }

        try {
            final ArrayList<EventSelf> eventsList = EventHandler.EVENTS;
            final String[] events = new String[eventsList.size()];
            for (int i = 0; i < eventsList.size(); i++) {
                events[i] = eventsList.get(i).export();
            }
            Serializer.toDisk(events, Serializer.inPath("tasks", "events.ser"));
        } catch (Exception e) {
            App.silenceException(e);
        }
    }

}

package core.task;

import core.Activity;
import core.Board;
import core.serial.Serializer;
import core.task.handler.AssignmentHandler;
import core.task.handler.EventHandler;
import core.task.handler.ProjectHandler;
import core.task.handler.TodoHandler;
import core.task.self.AssignmentSelf;
import core.task.self.EventSelf;
import core.task.self.ProjectSelf;
import core.task.self.TodoSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.KButton;
import proto.KFontFactory;
import proto.KLabel;
import proto.KPanel;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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

        final KButton returnButton = new KButton("Menu");
        returnButton.setFont(TASK_BUTTONS_FONT);
        returnButton.setToolTipText("Tasks Menu");
        returnButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Home");
            hintLabel.setText("");
        });

        final KPanel upperPanel = new KPanel(new BorderLayout());
        upperPanel.add(new KPanel(hintLabel), BorderLayout.WEST);
        upperPanel.add(new KPanel(returnButton), BorderLayout.EAST);

        final KPanel activityPanel = new KPanel(new BorderLayout());
        activityPanel.add(upperPanel, BorderLayout.NORTH);
        activityPanel.add(inPanel);
        if (!Dashboard.isFirst()) {
            TaskActivity.deSerializeAll();
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


    public static void serializeAll(){
        final ArrayList<TodoSelf> todoList = TodoHandler.TODOS;
        final String[] todos = new String[todoList.size()];
        for(int i = 0; i < todoList.size(); i++){
            todos[i] = todoList.get(i).export();
        }
        Serializer.toDisk(todos, Serializer.inPath("tasks", "todos.ser"));

        final ArrayList<ProjectSelf> projectList = ProjectHandler.PROJECTS;
        final String[] projects = new String[projectList.size()];
        for(int i = 0; i < projectList.size(); i++){
            projects[i] = projectList.get(i).export();
        }
        Serializer.toDisk(projects, Serializer.inPath("tasks", "projects.ser"));

        final ArrayList<AssignmentSelf> assignmentList = AssignmentHandler.ASSIGNMENTS;
        final String[] assignments = new String[assignmentList.size()];
        final String[] questions = new String[assignments.length];
        final ArrayList<Integer> groupIndexes = new ArrayList<>();
        for(int i = 0; i < assignments.length; i++){
            final AssignmentSelf assignment = assignmentList.get(i);
            assignments[i] = assignment.export();
            questions[i] = assignment.getQuestion();
            if (assignment.isGroup()) {
                groupIndexes.add(i);
            }
        }
        Serializer.toDisk(assignments, Serializer.inPath("tasks", "assignments.ser"));
        Serializer.toDisk(questions, Serializer.inPath("tasks", "questions.ser"));
        final String[] groupsMembers = new String[groupIndexes.size()];
        int j = 0;
        for (int index : groupIndexes){
            final AssignmentSelf assignment = assignmentList.get(index);
            groupsMembers[j] = Globals.joinLines(assignment.members.toArray());
            j++;
        }
        Serializer.toDisk(groupsMembers, Serializer.inPath("tasks", "groups.members.ser"));

        final ArrayList<EventSelf> eventsList = EventHandler.EVENTS;
        final String[] events = new String[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i++){
            events[i] = eventsList.get(i).export();
        }
        Serializer.toDisk(events, Serializer.inPath("tasks", "events.ser"));
    }

    public static void deSerializeAll(){
        final Object todoObj = Serializer.fromDisk(Serializer.inPath("tasks", "todos.ser"));
        if (todoObj == null) {
            App.silenceException("Failed to read TODO Tasks.");
        } else {
            final String[] todos = (String[]) todoObj;
            for (String data : todos) {
                final String[] lines = Globals.splitLines(data);
                final TodoSelf todoSelf = new TodoSelf(lines[0], Integer.parseInt(lines[2]),
                        lines[1], Boolean.parseBoolean(lines[4]));
                todoSelf.setTotalTimeConsumed(Integer.parseInt(lines[3]));
                todoSelf.setDateCompleted(lines[5]);
                todoSelf.eveIsAlerted = Boolean.parseBoolean(lines[6]);
                todoSelf.doneIsAlerted = Boolean.parseBoolean(lines[7]);
                if (todoSelf.isActive()) { // This only means it slept alive - we're to check then if it's to wake alive or not
                    if (new Date().before(MDate.parse(todoSelf.getDateExpectedToComplete()))) {
                        todoSelf.wakeAlive();
                    } else {
                        todoSelf.wakeDead();
                    }
                }
                todoSelf.setUpUI();
                TodoHandler.receiveFromSerials(todoSelf);
            }
        }

        final Object projectObj = Serializer.fromDisk(Serializer.inPath("tasks", "projects.ser"));
        if (projectObj == null) {
            App.silenceException("Failed to read Projects.");
        } else {
            final String[] projects = (String[]) projectObj;
            for (String data : projects) {
                final String[] lines = Globals.splitLines(data);
                final ProjectSelf projectSelf = new ProjectSelf(lines[0], lines[1], lines[2],
                        Integer.parseInt(lines[3]), Boolean.parseBoolean(lines[5]));
                projectSelf.setTotalTimeConsumed(Integer.parseInt(lines[4]));
                projectSelf.setDateCompleted(lines[6]);
                projectSelf.eveIsAlerted = Boolean.parseBoolean(lines[7]);
                projectSelf.completionIsAlerted = Boolean.parseBoolean(lines[8]);
                if (projectSelf.isLive()) {
                    if (new Date().before(MDate.parse(projectSelf.getDateExpectedToComplete()))) {
                        projectSelf.wakeLive();
                        projectSelf.initializeUI();
                    } else {
                        projectSelf.wakeDead();
                        projectSelf.setUpDoneUI();
                    }
                } else {
                    projectSelf.setUpDoneUI();
                }
                ProjectHandler.receiveFromSerials(projectSelf);
            }
        }

        final Object assignObj = Serializer.fromDisk(Serializer.inPath("tasks", "assignments.ser"));
        final Object groupsMembersObj = Serializer.fromDisk(Serializer.inPath("tasks", "groups.members.ser"));
        final Object questionsObj = Serializer.fromDisk(Serializer.inPath("tasks", "questions.ser"));
        if (assignObj == null || groupsMembersObj == null || questionsObj == null) {
            App.silenceException("Failed to read assignments.");
        } else {
            final String[] assignments = (String[]) assignObj;
            final String[] groupsMembers = (String[]) groupsMembersObj;
            final String[] questions = (String[]) questionsObj;
            for (int i = 0, j = 0; i < assignments.length; i++) {
                final String[] lines = Globals.splitLines(assignments[i]);
                final AssignmentSelf assignmentSelf = new AssignmentSelf(lines[0], lines[1], questions[i],
                        Boolean.parseBoolean(lines[2]), lines[3], lines[4], Boolean.parseBoolean(lines[5]));
                assignmentSelf.setSubmissionDate(lines[6]);
                assignmentSelf.eveIsAlerted = Boolean.parseBoolean(lines[7]);
                assignmentSelf.submissionIsAlerted = Boolean.parseBoolean(lines[8]);
                assignmentSelf.setUpUI(); // Todo consider recall
                if (assignmentSelf.isGroup()) {
                    final String[] memberLines = Globals.splitLines(groupsMembers[j]);
                    Collections.addAll(assignmentSelf.members, memberLines);
                    j++;
                }
                if (assignmentSelf.isOn()) {
                    if (MDate.parse(MDate.formatDateOnly(new Date())+" 0:0:0").
                            before(MDate.parse(assignmentSelf.getDeadLine()+" 0:0:0"))) {
                        assignmentSelf.wakeAlive();
                    } else {
                        assignmentSelf.wakeDead();
                    }
                }
                assignmentSelf.setUpUI();
                AssignmentHandler.receiveFromSerials(assignmentSelf);
            }
        }

        final Object eventsObj = Serializer.fromDisk(Serializer.inPath("tasks", "events.ser"));
        if (eventsObj == null) {
            App.silenceException("Failed to read Events.");
        } else {
            final String[] events = (String[]) eventsObj;
            for (String data : events){
                final String[] lines = Globals.splitLines(data);
                final EventSelf eventSelf = new EventSelf(lines[0], lines[1], Boolean.parseBoolean(lines[2]));
                eventSelf.eveIsAlerted = Boolean.parseBoolean(lines[3]);
                eventSelf.timeupIsAlerted = Boolean.parseBoolean(lines[4]);
                eventSelf.setUpUI(); // Todo consider recall
                if (eventSelf.isPending()) {
                    if (MDate.parse(MDate.formatDateOnly(new Date()) + " 0:0:0").
                            before(MDate.parse(eventSelf.getDateDue() + " 0:0:0"))) {
                        eventSelf.wakeAlive();
                    } else {
                        eventSelf.endState();
                    }
                }
                eventSelf.setUpUI();
                EventHandler.receiveFromSerials(eventSelf);
            }
        }
    }

}

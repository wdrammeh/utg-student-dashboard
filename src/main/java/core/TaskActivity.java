package core;

import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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
    private static final Font TASK_BUTTONS_FONT = KFontFactory.createPlainFont(15);
    private static final Font TASK_HEADERS_FONT = KFontFactory.createPlainFont(20);
    private static final int CONTENTS_POSITION = FlowLayout.LEFT;


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
            TaskSelf.deSerializeAll();
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
        todoBigButton = newBigButton("TODO", TodoHandler.activeCount);
        todoBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"TODO");
            hintLabel.setText(" > TODO List");
        });
        cardLayout.addLayoutComponent(inPanel.add(new TodoHandler().todoComponent()),"TODO");
        return todoBigButton;
    }

    private KButton giveProjectsButton(){
        projectBigButton = newBigButton("Projects", ProjectsHandler.liveCount);
        projectBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Projects");
            hintLabel.setText(" > Projects");
        });
        cardLayout.addLayoutComponent(inPanel.add(new ProjectsHandler().projectComponent()),"Projects");
        return projectBigButton;
    }

    private KButton giveAssignmentsButton(){
        assignmentBigButton = newBigButton("Assignments", AssignmentsHandler.doingCount);
        assignmentBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Assignments");
            hintLabel.setText(" > Assignments");
        });
        cardLayout.addLayoutComponent(inPanel.add(new AssignmentsHandler().assignmentsComponent()),"Assignments");
        return assignmentBigButton;
    }

    private KButton giveEventsButton(){
        eventBigButton = newBigButton("Upcoming", EventsHandler.upcomingCount);
        eventBigButton.addActionListener(e-> {
            cardLayout.show(inPanel,"Events");
            hintLabel.setText(" > Upcoming Events");
        });
        cardLayout.addLayoutComponent(inPanel.add(new EventsHandler().eventsComponent()),"Events");
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


    /*
        What do these helper-classes do?
        Like their immediate mother-class, they serve as the intermediary between their respective task-types
        and the Board. They provide UI and behavior as well.
     */

    public static class TodoHandler {
        public static final ArrayList<TaskSelf.TodoSelf> TODOS = new ArrayList<>();
        private static int activeCount, dormantCount;
        private static KPanel activeContainer, dormantContainer;
        private static TaskCreator.TodoCreator todoCreator;

        public TodoHandler(){
            activeContainer = new KPanel(){
                @Override
                public Component add(Component comp) {
                    activeContainer.setPreferredSize(new Dimension(activeContainer.getPreferredSize().width,
                            activeContainer.getPreferredSize().height+40));
                    renewCount(1);
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    activeContainer.setPreferredSize(new Dimension(activeContainer.getPreferredSize().width,
                            activeContainer.getPreferredSize().height-40));
                    renewCount(-1);
                }
            };
            activeContainer.setLayout(new FlowLayout(CONTENTS_POSITION));

            dormantContainer = new KPanel(){
                @Override
                public Component add(Component comp) {
                    dormantContainer.setPreferredSize(new Dimension(dormantContainer.getPreferredSize().width,
                            dormantContainer.getPreferredSize().height+40));
                    dormantCount++;
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    dormantContainer.setPreferredSize(new Dimension(dormantContainer.getPreferredSize().width,
                            dormantContainer.getPreferredSize().height-40));
                    dormantCount--;
                }
            };
            dormantContainer.setLayout(new FlowLayout(CONTENTS_POSITION));
        }

        public static ActionListener additionWaiter(){
            return e -> {
                final String name = todoCreator.getDescriptionField().getText();
                int givenDays = 0;
                if (Globals.hasNoText(name)) {
                    App.reportError(todoCreator.getRootPane(), "No Name", "Please specify a name for the task");
                    todoCreator.getDescriptionField().requestFocusInWindow();
                } else if (name.length() > TaskCreator.TASKS_DESCRIPTION_LIMIT) {
                    App.reportError("Error", "Sorry, description of a task must be at most "+
                            TaskCreator.TASKS_DESCRIPTION_LIMIT +" characters.");
                } else {
                    final String span = todoCreator.getDuration();
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

                    if (App.showYesNoCancelDialog(todoCreator.getRootPane(), "Confirm",
                            "Do you wish to add the following task?\n-\n" +
                            "Name:  " + name + "\n" +
                            "To be completed in:  " + span)) {
                        final TaskSelf.TodoSelf incomingTodo = new TaskSelf.TodoSelf(name, givenDays);
                        TODOS.add(incomingTodo);
                        activeContainer.add(incomingTodo.getLayer());
                        MComponent.ready(activeContainer);
                        todoCreator.dispose();
                    }
                }
            };
        }

        public static void transferTask(TaskSelf.TodoSelf oldSelf, KDialog dialog, boolean timeDue){
            if (timeDue) {
                oldSelf.setTotalTimeConsumed(oldSelf.getSpecifiedDuration());
            	finalizeTransfer(oldSelf);
            } else {
                if (App.showYesNoCancelDialog(dialog.getRootPane(), "Confirm",
                        "Are you sure you've completed this task? It will be marked as completed.")) {
                    oldSelf.setTotalTimeConsumed(oldSelf.getDaysTaken());
                	finalizeTransfer(oldSelf);
                    dialog.dispose();
                }
            }
        }

        private static void finalizeTransfer(TaskSelf.TodoSelf oldSelf) {
        	oldSelf.setDateCompleted(MDate.now());
            oldSelf.getTogoLabel().setText("Completed "+oldSelf.getDateCompleted());//Which is that
            oldSelf.getTogoLabel().setForeground(Color.BLUE);
            oldSelf.setActive(false);

            final KPanel oldPanel = oldSelf.getLayer();
            activeContainer.remove(oldPanel);
            dormantContainer.add(oldPanel);
            MComponent.ready(activeContainer, dormantContainer);
        }

        public static ActionListener removalWaiter(TaskSelf.TodoSelf task, KDialog dialog){
            return e -> {
                if (App.showYesNoCancelDialog(dialog.getRootPane(),"Confirm",
                        "Do you really want to remove this task?")) {
                    final KPanel outgoingPanel = task.getLayer();
                    if (task.isActive()) {
                        activeContainer.remove(outgoingPanel);
                    } else {
                        dormantContainer.remove(outgoingPanel);
                    }
                    TODOS.remove(task);
                    MComponent.ready(activeContainer, dormantContainer);
                    task.setActive(false);
                    dialog.dispose();
                }
            };
        }

        private static void renewCount(int valueEffected){
            activeCount += valueEffected;
            todoBigButton.setText(activeCount);
        }
        
        public static void receiveFromSerials(TaskSelf.TodoSelf dTodo){
            if (dTodo.isActive()) {
                activeContainer.add(dTodo.getLayer());
            } else {
                dormantContainer.add(dTodo.getLayer());
            }
            TODOS.add(dTodo);
        }

        private JComponent todoComponent(){
            final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, runningTasks(), completedTasks());
            splitPane.setContinuousLayout(true);
            splitPane.setDividerLocation(200);
            return new KPanel(new BorderLayout(), splitPane);
        }

        private JComponent runningTasks(){
            final KButton addButton = new KButton("New Task");
            addButton.setFont(TASK_BUTTONS_FONT);
            addButton.setMnemonic(KeyEvent.VK_T);
            addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addButton.setToolTipText("Create Task (Alt+T)");
            addButton.addActionListener(e-> {
                todoCreator = new TaskCreator.TodoCreator();
                todoCreator.setVisible(true);
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(addButton, BorderLayout.WEST);
            labelPanelPlus.add(new KPanel(new KLabel("Active Tasks", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel runningPanel = new KPanel(new BorderLayout());
            runningPanel.add(labelPanelPlus, BorderLayout.NORTH);
            runningPanel.add(new KScrollPane(activeContainer), BorderLayout.CENTER);
            return runningPanel;
        }

        private JComponent completedTasks(){
            final KButton clearButton = new KButton("Clear List");
            clearButton.setFont(TASK_BUTTONS_FONT);
            clearButton.setToolTipText("Remove All (Alt+C)");
            clearButton.setMnemonic(KeyEvent.VK_C);
            clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            clearButton.addActionListener(e-> {
                if (dormantContainer.getComponentCount() > 0) {
                    if (App.showYesNoCancelDialog("Confirm", "Do you want to remove all the completed tasks.")) {
                        for (Component c : dormantContainer.getComponents()) {
                            dormantContainer.remove(c);
                        }
                        TODOS.removeIf(t -> !t.isActive());
                        MComponent.ready(dormantContainer);
                    }
                }
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(clearButton, BorderLayout.WEST);
            labelPanelPlus.add(new KPanel(new KLabel("Completed Tasks", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel completedTasksPanel = new KPanel(new BorderLayout());
            completedTasksPanel.add(labelPanelPlus, BorderLayout.NORTH);
            completedTasksPanel.add(new KScrollPane(dormantContainer), BorderLayout.CENTER);
            return completedTasksPanel;
        }

        private int getTotalCount(){
            return activeCount + dormantCount;
        }
    }


    public static class ProjectsHandler {
        public static final ArrayList<TaskSelf.ProjectSelf> PROJECTS = new ArrayList<>();
        private static int liveCount, completeCount;//count - number of all, liveCount - number currently running
        private static KPanel projectsReside;
        private static TaskCreator.ProjectCreator projectCreator;

        public ProjectsHandler(){
            projectsReside = new KPanel(){
                @Override
                public Component add(Component comp) {
                    projectsReside.setPreferredSize(new Dimension(projectsReside.getPreferredSize().width,
                            projectsReside.getPreferredSize().height+40));
                    return super.add(comp);
                }
                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    projectsReside.setPreferredSize(new Dimension(projectsReside.getPreferredSize().width,
                            projectsReside.getPreferredSize().height-40));
                }
            };
            projectsReside.setLayout(new FlowLayout(CONTENTS_POSITION));
        }

        public static ActionListener additionWaiter(){
            return e -> {
                final String name = projectCreator.getNameField().getText();
                int givenDays = 0;
                if (Globals.hasNoText(name)) {
                    App.reportError("No Name","Please specify a name for the project");
                    projectCreator.getNameField().requestFocusInWindow();
                } else if (name.length() > TaskCreator.TASKS_DESCRIPTION_LIMIT) {
                    App.reportError("Error", "Sorry, name of a project must be at most "+
                            TaskCreator.TASKS_DESCRIPTION_LIMIT +" characters.");
                } else {
                    final String dDuration = projectCreator.getTheDuration();
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

                    if (App.showYesNoCancelDialog(projectCreator.getRootPane(), "Confirm",
                            "Do you wish to add the following project?\n-\n" +
                            "Name:  " + name + "\n" +
                            "Type:  " + projectCreator.getTheType() + " Project" + "\n" +
                            "Duration:  " + dDuration)) {
                        final TaskSelf.ProjectSelf incomingProject = new TaskSelf.ProjectSelf(name,
                                projectCreator.getTheType(), givenDays);
                        PROJECTS.add(incomingProject);
                        projectsReside.add(incomingProject.getLayer());
                        projectCreator.dispose();
                        MComponent.ready(projectsReside);
                        renewCount(1);
                    }
                }
            };
        }

        public static void performIComplete(TaskSelf.ProjectSelf project, boolean timeDue){
        	if (timeDue) {
        	    project.setTotalTimeConsumed(project.getSpecifiedDuration());
        		finalizeCompletion(project);
                renewCount(-1);
                completeCount++;
        	} else {
        		if (App.showYesNoCancelDialog("Confirm",
                        "Are you sure you've completed this project before the specified time?")) {
                    project.setTotalTimeConsumed(project.getDaysTaken());
        			finalizeCompletion(project);
                    renewCount(-1);
                    completeCount++;
        		}
        	}

        }

        private static void finalizeCompletion(TaskSelf.ProjectSelf project){
            project.setDateCompleted(MDate.now());
            project.setLive(false);
            project.setUpDoneUI();
            //Respect that order of sorting... since the project generator does not use clear-cut separator
            projectsReside.remove(project.getLayer());
            projectsReside.add(project.getLayer());
            MComponent.ready(projectsReside);
        }

        public static ActionListener removalListener(TaskSelf.ProjectSelf project){
            return e -> {
                if (App.showYesNoCancelDialog("Confirm","Are you sure you want to remove this project?.")) {
                    if (project.isLive()) {
                        renewCount(-1);
                    } else {
                        completeCount--;
                    }
                    project.setLive(false);
                    projectsReside.remove(project.getLayer());
                    PROJECTS.remove(project);
                    MComponent.ready(projectsReside);
                }
            };
        }

        public static void renewCount(int value){
            liveCount += value;
            projectBigButton.setText(liveCount);
        }

        public static void receiveFromSerials(TaskSelf.ProjectSelf dProject){
            projectsReside.add(dProject.getLayer());
            PROJECTS.add(dProject);
            if (dProject.isLive()) {
                renewCount(1);
            } else {
                completeCount++;
            }
        }

        private JComponent projectComponent(){
            final KButton addButton = new KButton("New Project");
            addButton.setFont(TASK_BUTTONS_FONT);
            addButton.setMnemonic(KeyEvent.VK_P);
            addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addButton.setToolTipText("Create Project (Alt+P)");
            addButton.addActionListener(e-> {
                projectCreator = new TaskCreator.ProjectCreator();
                projectCreator.setVisible(true);
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(addButton, BorderLayout.WEST);
            labelPanelPlus.add(new KPanel(new KLabel("My Projects", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel projectComponent = new KPanel(new BorderLayout());
            projectComponent.add(labelPanelPlus, BorderLayout.NORTH);
            projectComponent.add(new KScrollPane(projectsReside), BorderLayout.CENTER);
            return projectComponent;
        }

        private int getTotalCount(){
            return liveCount + completeCount;
        }
    }


    public static class AssignmentsHandler {
        public static final ArrayList<TaskSelf.AssignmentSelf> ASSIGNMENTS = new ArrayList<>();
		private static int doingCount, doneCount;
        private static KPanel activeReside, doneReside;
        private static TaskCreator.AssignmentCreator assignmentCreator;

        public AssignmentsHandler(){
        	activeReside = new KPanel(){
                @Override
                public Component add(Component comp) {
                    activeReside.setPreferredSize(new Dimension(activeReside.getPreferredSize().width,
                            activeReside.getPreferredSize().height+40));
                    renewCount(1);
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    activeReside.setPreferredSize(new Dimension(activeReside.getPreferredSize().width,
                            activeReside.getPreferredSize().height-40));
                    renewCount(-1);
                }
            };
            activeReside.setLayout(new FlowLayout(CONTENTS_POSITION));

            doneReside = new KPanel(){
                @Override
                public Component add(Component comp) {
                    doneReside.setPreferredSize(new Dimension(doneReside.getPreferredSize().width,
                            doneReside.getPreferredSize().height+40));
                    doneCount++;
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    doneCount--;
                    doneReside.setPreferredSize(new Dimension(doneReside.getPreferredSize().width,
                            doneReside.getPreferredSize().height-40));
                }
            };
            doneReside.setLayout(new FlowLayout(CONTENTS_POSITION));
        }

        public static ActionListener additionListener(){
            return e -> {
                final String name = assignmentCreator.getNameField().getText();
                if (Globals.hasNoText(name)) {
                    App.reportError(assignmentCreator.getRootPane(), "No Name",
                            "Please provide the name of the course");
                    assignmentCreator.getNameField().requestFocusInWindow();
                } else if (name.length() > TaskCreator.TASKS_DESCRIPTION_LIMIT) {
                    App.reportError(assignmentCreator.getRootPane(), "Error",
                            "Sorry, the subject name cannot exceed "+
                            TaskCreator.TASKS_DESCRIPTION_LIMIT +" characters.");
                    assignmentCreator.getNameField().requestFocusInWindow();
                } else if (Globals.hasNoText(assignmentCreator.getProvidedDeadLine())) {
                    App.reportError(assignmentCreator.getRootPane(), "Deadline Error",
                            "Please fill out all the fields for the deadline. You can change them later.");
                } else {
                    final String type = assignmentCreator.isGroup() ? "Group Assignment" : "Individual Assignment";
                    final String question = assignmentCreator.getQuestion();
                    final Date givenDate = Objects.requireNonNull(MDate.parse(assignmentCreator.getProvidedDeadLine()+" 0:0:0"));
                    if (givenDate.before(new Date())) {
                        App.reportError(assignmentCreator.getRootPane(), "Invalid Deadline",
                                "That deadline is already past. Enter a valid deadline.");
                        return;
                    }
                    final String deadline = MDate.formatDateOnly(givenDate);
                    final String preMean = String.valueOf(assignmentCreator.getSelectedMode());
                    String mean;
                    if (preMean.contains("hard")) {
                        mean = "A Hard Copy";
                    } else if (preMean.contains("soft")) {
                        mean = "A Soft Copy";
                    } else if (preMean.contains("email")) {
                        mean = "An Email Address - " + assignmentCreator.getMeanValue();
                    } else if (preMean.contains("web")) {
                        mean = "A Webpage - " + assignmentCreator.getMeanValue();
                    } else {
                        mean = "Other Means";
                    }
                    if (App.showYesNoCancelDialog(assignmentCreator.getRootPane(), "Confirm",
                            "Do you wish to add the following assignment?\n-\n" +
                            "Subject:  " + name + "\n" +
                            "Type:  " + type + "\n" +
                            "Submission:  "+deadline+ "\n" +
                            "Through:  "+mean)) {
                        final TaskSelf.AssignmentSelf incomingAssignment = new TaskSelf.AssignmentSelf(name, deadline,
                                question, assignmentCreator.isGroup(), mean);
                        ASSIGNMENTS.add(incomingAssignment);
                        activeReside.add(incomingAssignment.getLayer());
                        MComponent.ready(activeReside);
                        assignmentCreator.dispose();
                    }
                }
            };
        }

        public static void transferAssignment(TaskSelf.AssignmentSelf assignmentSelf,
                                              TaskExhibition.AssignmentExhibition assignmentExhibition, boolean isTime) {
            if (isTime) {
                assignmentSelf.setSubmissionDate(assignmentSelf.getDeadLine());
                completeTransfer(assignmentSelf);
            } else {
                if (App.showYesNoCancelDialog(assignmentExhibition.getRootPane(),"Confirm",
                        "Are you sure you have submitted this assignment?")) {
                    assignmentSelf.setSubmissionDate(MDate.formatDateOnly(new Date()));
                    completeTransfer(assignmentSelf);
                    assignmentExhibition.dispose();
                }
            }
        }
        
        private static void completeTransfer(TaskSelf.AssignmentSelf aSelf){
            aSelf.getDeadlineIndicator().setText("Submitted: "+aSelf.getSubmissionDate());
            aSelf.getDeadlineIndicator().setStyle(KFontFactory.createPlainFont(16), Color.BLUE);
            aSelf.getDeadlineIndicator().setCursor(null);
            for (MouseListener l : aSelf.getDeadlineIndicator().getMouseListeners()) {
                aSelf.getDeadlineIndicator().removeMouseListener(l);
            }
            aSelf.setOn(false);
            activeReside.remove(aSelf.getLayer());
            doneReside.add(aSelf.getLayer());//come on... will only adding it in the 2nd not remove it from the first first?
            MComponent.ready(activeReside,doneReside);
        }
        
        public static ActionListener removalListener(TaskSelf.AssignmentSelf assignmentSelf,
                                                     TaskExhibition.AssignmentExhibition eDialog){
            return e -> {
                if (App.showYesNoCancelDialog(eDialog.getRootPane(), "Confirm",
                        "Are you sure you want to remove this assignment?")) {
                    if (assignmentSelf.isOn()) {
                        activeReside.remove(assignmentSelf.getLayer());
                    } else {
                        doneReside.remove(assignmentSelf.getLayer());
                    }
                    MComponent.ready(activeReside, doneReside);
                    ASSIGNMENTS.remove(assignmentSelf);
                    assignmentSelf.setOn(false);
                    eDialog.dispose();
                }
            };
        }
        
        private static int getTotalCount(){
            return doingCount + doneCount;
        }

        private static void renewCount(int effect){
            doingCount += effect;
            assignmentBigButton.setText(doingCount);
        }

        public static void receiveFromSerials(TaskSelf.AssignmentSelf aSelf){
            if (aSelf.isOn()) {
                activeReside.add(aSelf.getLayer());
            } else {
                doneReside.add(aSelf.getLayer());
            }
            ASSIGNMENTS.add(aSelf);
        }

        private JComponent assignmentsComponent(){
        	final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, activeAssignments(), doneAssignments());
            splitPane.setContinuousLayout(true);
            splitPane.setDividerLocation(200);
            return new KPanel(new BorderLayout(), splitPane);
        }

        private JComponent activeAssignments() {
        	final KButton createButton = new KButton("New Assignment");
            createButton.setFont(TASK_BUTTONS_FONT);
            createButton.setMnemonic(KeyEvent.VK_A);
            createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            createButton.setToolTipText("Add Assignment (Alt+A)");
            createButton.addActionListener(e-> {
                assignmentCreator = new TaskCreator.AssignmentCreator();
                assignmentCreator.setVisible(true);
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(createButton, BorderLayout.WEST);
            labelPanelPlus.add(new KPanel(new KLabel("Assignments", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel upperReside = new KPanel(new BorderLayout());
            upperReside.add(labelPanelPlus, BorderLayout.NORTH);
            upperReside.add(new KScrollPane(activeReside), BorderLayout.CENTER);
            return upperReside;
        }

        private JComponent doneAssignments() {
        	final KButton clearButton = new KButton("Remove all");
            clearButton.setFont(TASK_BUTTONS_FONT);
            clearButton.setToolTipText("Remove All (Alt + R)");
            clearButton.setMnemonic(KeyEvent.VK_R);
            clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            clearButton.addActionListener(e -> {
                if (doneReside.getComponentCount() > 0) {
                    if (App.showYesNoCancelDialog("Confirm",
                            "Are you sure you want to remove all the submitted assignments?")) {
                        for (Component c : doneReside.getComponents()) {
                            doneReside.remove(c);
                        }
                        MComponent.ready(doneReside);
                        ASSIGNMENTS.removeIf(a -> !a.isOn());
                    }
                }
            });

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(clearButton, BorderLayout.WEST);
            labelPanelPlus.add(new KPanel(new KLabel("Submitted Assignments", TASK_HEADERS_FONT)),
                    BorderLayout.CENTER);

            final KPanel lowerReside = new KPanel(new BorderLayout());
            lowerReside.add(labelPanelPlus, BorderLayout.NORTH);
            lowerReside.add(new KScrollPane(doneReside), BorderLayout.CENTER);
            return lowerReside;
        }
    }


    public static class EventsHandler {
        public static final ArrayList<TaskSelf.EventSelf> EVENTS = new ArrayList<>();
        private static int upcomingCount;
        private static KPanel eventsReside;
        private static TaskCreator.EventCreator testCreator, examCreator, othersCreator;

        public EventsHandler(){
            eventsReside = new KPanel(){
                @Override
                public Component add(Component comp) {
                    eventsReside.setPreferredSize(new Dimension(eventsReside.getPreferredSize().width,
                            eventsReside.getPreferredSize().height+35));
                    return super.add(comp);
                }

                @Override
                public void remove(Component comp) {
                    super.remove(comp);
                    eventsReside.setPreferredSize(new Dimension(eventsReside.getPreferredSize().width,
                            eventsReside.getPreferredSize().height-35));
                }
            };
            eventsReside.setLayout(new FlowLayout(CONTENTS_POSITION));
        }

        public static ActionListener newListener(){
            return e -> {
                final TaskCreator.EventCreator requiredCreator = getShowingCreator();
                if (requiredCreator == null) {
                    return;
                }
                String tName = requiredCreator.getDescriptionField().getText();
                if (Globals.hasNoText(tName)) {
                    App.reportError(requiredCreator.getRootPane(), "No Name",
                            "Please specify a name for the event.");
                    requiredCreator.getDescriptionField().requestFocusInWindow();
                } else if (tName.length() > TaskCreator.TASKS_DESCRIPTION_LIMIT) {
                    App.reportError(requiredCreator.getRootPane(), "Error",
                            "Sorry, the event's name should be at most "+
                            TaskCreator.TASKS_DESCRIPTION_LIMIT+" characters.");
                    requiredCreator.getDescriptionField().requestFocusInWindow();
                } else if (Globals.hasNoText(requiredCreator.getProvidedDate())) {
                    App.reportError(requiredCreator.getRootPane(), "Error",
                            "Please provide all the fields for the date of the "+
                            (requiredCreator.type()));
                } else {
                    final Date date = MDate.parse(requiredCreator.getProvidedDate() + " 0:0:0");
                    if (date == null) {
                        return;
                    }
                    if (date.before(new Date())) {
                        App.reportError(requiredCreator.getRootPane(),"Invalid Deadline",
                                "Please consider the deadline. It's already past.");
                        return;
                    }
                    if (requiredCreator.getTitle().contains("Test")) {
                        tName = tName + " Test";
                    } else if (requiredCreator.getTitle().contains("Exam")) {
                        tName = tName + " Examination";
                    }
                    final String dateString = MDate.formatDateOnly(date);
                    if (App.showYesNoCancelDialog(requiredCreator.getRootPane(),"Confirm",
                            "Do you wish to add the following event?\n-\n" +
                            "Title:  "+tName+"\n" +
                            "Date:  "+dateString)) {
                        final TaskSelf.EventSelf incomingEvent = new TaskSelf.EventSelf(tName, dateString);
                        EVENTS.add(incomingEvent);
                        eventsReside.add(incomingEvent.getEventLayer());
                        MComponent.ready(eventsReside);
                        requiredCreator.dispose();
                        renewCount(1);
                    }
                }
            };
        }

        private static TaskCreator.EventCreator getShowingCreator(){
            if (testCreator != null && testCreator.isShowing()) {
                return testCreator;
            } else if (examCreator != null && examCreator.isShowing()) {
                return examCreator;
            } else if (othersCreator != null && othersCreator.isShowing()) {
                return othersCreator;
            } else {
                return null;
            }
        }

        public static void deleteEvent(TaskSelf.EventSelf event){
            EVENTS.remove(event);
            eventsReside.remove(event.getEventLayer());
            MComponent.ready(eventsReside);
            if (event.isPending()) {
                TaskActivity.EventsHandler.renewCount(-1);
            }
        }

        public static void renewCount(int value){
            upcomingCount += value;
            eventBigButton.setText(upcomingCount);
        }

        public static void receiveFromSerials(TaskSelf.EventSelf eventSelf) {
            eventsReside.add(eventSelf.getEventLayer());
            if (eventSelf.isPending()) {
                renewCount(1);
            }
            EVENTS.add(eventSelf);
        }

        private JComponent eventsComponent(){
            final KMenuItem testItem = new KMenuItem("Upcoming Test", e-> {
                testCreator = new TaskCreator.EventCreator(TaskCreator.EventCreator.TEST);
                testCreator.setVisible(true);
            });

            final KMenuItem examItem = new KMenuItem("Upcoming Exam", e-> {
                examCreator = new TaskCreator.EventCreator(TaskCreator.EventCreator.EXAM);
                examCreator.setVisible(true);
            });

            final KMenuItem otherItem = new KMenuItem("Other", e-> {
                othersCreator = new TaskCreator.EventCreator(TaskCreator.EventCreator.OTHER);
                othersCreator.setVisible(true);
            });

            final JPopupMenu jPopup = new JPopupMenu();
            jPopup.add(testItem);
            jPopup.add(examItem);
            jPopup.add(otherItem);

            final KButton popUpButton = new KButton("New Event");
            popUpButton.setToolTipText("Create Event (Alt+V)");
            popUpButton.setMnemonic(KeyEvent.VK_V);
            popUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            popUpButton.setFont(TASK_BUTTONS_FONT);
            popUpButton.addActionListener(e-> jPopup.show(popUpButton, popUpButton.getX(), popUpButton.getY() +
                    (popUpButton.getPreferredSize().height)));

            final KPanel labelPanelPlus = new KPanel(new BorderLayout());
            labelPanelPlus.add(popUpButton, BorderLayout.WEST);
            labelPanelPlus.add(new KPanel(new KLabel("Events", TASK_HEADERS_FONT)), BorderLayout.CENTER);

            final KPanel eventsComponent = new KPanel(new BorderLayout());
            eventsComponent.add(labelPanelPlus, BorderLayout.NORTH);
            eventsComponent.add(new KScrollPane(eventsReside), BorderLayout.CENTER);
            return eventsComponent;
        }
    }

}

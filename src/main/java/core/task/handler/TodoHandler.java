package core.task.handler;

import core.task.creator.TodoCreator;
import core.task.self.TodoSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import static core.task.TaskActivity.*;
import static core.task.creator.TodoCreator.DESCRIPTION_LIMIT;

public class TodoHandler {
    public static final ArrayList<TodoSelf> TODOS = new ArrayList<>();
    private static int activeCount, dormantCount;
    private static KPanel activeContainer, dormantContainer;
    private static TodoCreator todoCreator;
    private static KButton bigButton;


    public TodoHandler(KButton bigButton){
        TodoHandler.bigButton = bigButton;
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
        activeContainer.setLayout(new FlowLayout(CONTENTS_POSITION, 10, 10));

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
        dormantContainer.setLayout(new FlowLayout(CONTENTS_POSITION, 10, 10));
    }

    public static ActionListener additionWaiter(){
        return e -> {
            final String name = todoCreator.getDescriptionField().getText();
            int givenDays = 0;
            if (Globals.hasNoText(name)) {
                App.reportError(todoCreator.getRootPane(), "No Name", "Please specify a name for the task.");
                todoCreator.getDescriptionField().requestFocusInWindow();
            } else if (name.length() > DESCRIPTION_LIMIT) {
                App.reportError("Error", "Sorry, description of a task must be at most "+
                        DESCRIPTION_LIMIT +" characters.");
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
                final TodoSelf incomingTodo = new TodoSelf(name, givenDays);
                TODOS.add(incomingTodo);
                activeContainer.add(incomingTodo.getLayer());
                MComponent.ready(activeContainer);
                todoCreator.dispose();
            }
        };
    }

    public static void transferTask(TodoSelf oldSelf, KDialog dialog, boolean timeDue){
        if (timeDue) {
            oldSelf.setTotalTimeConsumed(oldSelf.getSpecifiedDuration());
            finalizeTransfer(oldSelf);
        } else {
            if (App.showYesNoCancelDialog(dialog.getRootPane(), "Confirm",
                    "Are you sure you've completed this task?")) {
                oldSelf.setTotalTimeConsumed(oldSelf.getDaysTaken());
                finalizeTransfer(oldSelf);
                dialog.dispose();
            }
        }
    }

    private static void finalizeTransfer(TodoSelf oldSelf) {
        oldSelf.setDateCompleted(MDate.now());
        oldSelf.getTogoLabel().setText("Completed "+oldSelf.getDateCompleted());//Which is that
        oldSelf.getTogoLabel().setForeground(Color.BLUE);
        oldSelf.setActive(false);

        final KPanel oldPanel = oldSelf.getLayer();
        activeContainer.remove(oldPanel);
        dormantContainer.add(oldPanel);
        MComponent.ready(activeContainer, dormantContainer);
    }

    public static ActionListener removalWaiter(TodoSelf task, KDialog dialog){
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
        bigButton.setText(activeCount);
    }

    public static void receiveFromSerials(TodoSelf dTodo){
        if (dTodo.isActive()) {
            activeContainer.add(dTodo.getLayer());
        } else {
            dormantContainer.add(dTodo.getLayer());
        }
        TODOS.add(dTodo);
    }

    public JComponent getComponent(){
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                runningTasks(), completedTasks());
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(275);
        return new KPanel(new BorderLayout(), splitPane);
    }

    private JComponent runningTasks(){
        final KButton addButton = new KButton("New Task");
        addButton.setFont(TASK_BUTTONS_FONT);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e-> {
            todoCreator = new TodoCreator();
            todoCreator.setVisible(true);
        });

        final KPanel labelPanelPlus = new KPanel(new BorderLayout());
        labelPanelPlus.add(addButton, BorderLayout.WEST);
        labelPanelPlus.add(new KPanel(new KLabel("Active Tasks", TASK_HEADERS_FONT)), BorderLayout.CENTER);

        final KPanel runningPanel = new KPanel(new BorderLayout());
        runningPanel.add(labelPanelPlus, BorderLayout.NORTH);

        final KScrollPane scrollPane = new KScrollPane(activeContainer);
        scrollPane.setBorder(null);
        runningPanel.add(scrollPane, BorderLayout.CENTER);
        return runningPanel;
    }

    private JComponent completedTasks(){
        final KButton clearButton = new KButton("Clear List");
        clearButton.setFont(TASK_BUTTONS_FONT);
        clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e-> {
            if (dormantContainer.getComponentCount() > 0) {
                if (App.showYesNoCancelDialog("Confirm", "Do you want to remove all the completed tasks?")) {
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
        final KScrollPane scrollPane = new KScrollPane(dormantContainer);
        scrollPane.setBorder(null);
        completedTasksPanel.add(scrollPane, BorderLayout.CENTER);
        return completedTasksPanel;
    }

    public static int getTotalCount(){
        return activeCount + dormantCount;
    }

    public static int getActiveCount(){
        return activeCount;
    }

}

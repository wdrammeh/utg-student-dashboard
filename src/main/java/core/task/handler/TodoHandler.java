package core.task.handler;

import core.serial.Serializer;
import core.task.creator.TodoCreator;
import core.task.self.TodoSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import static core.task.TaskActivity.*;

/**
 * Provides activity interface, and general functionality for the TODO Task types.
 */
public class TodoHandler {
    private static KPanel activeContainer;
    private static KPanel dormantContainer;
    private static int activeCount;
    private static int dormantCount;
    private static KButton portalButton;
    public static final ArrayList<TodoSelf> TODOS = new ArrayList<>();


    public static void initHandle(KButton portalButton){
        TodoHandler.portalButton = portalButton;
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
        if (!Dashboard.isFirst()) {
            deserialize();
        }
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
        oldSelf.getTogoLabel().setText("Completed "+oldSelf.getDateCompleted()); // Which is that
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
        portalButton.setText(activeCount);
    }

    public static void newIncoming(TodoSelf todo){
        TODOS.add(todo);
        activeContainer.add(todo.getLayer());
        MComponent.ready(activeContainer);
    }

    public static void receiveFromSerials(TodoSelf dTodo){
        if (dTodo.isActive()) {
            activeContainer.add(dTodo.getLayer());
        } else {
            dormantContainer.add(dTodo.getLayer());
        }
        TODOS.add(dTodo);
    }

    public static JComponent getComponent(){
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                runningTasksComponent(), completedTasksComponent());
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(275);
        return new KPanel(new BorderLayout(), splitPane);
    }

    private static JComponent runningTasksComponent(){
        final KButton addButton = new KButton("New Task");
        addButton.setFont(TASK_BUTTONS_FONT);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e-> {
            new TodoCreator().setVisible(true);
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

    private static JComponent completedTasksComponent(){
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

    private static void deserialize(){
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
                receiveFromSerials(todoSelf);
            }
        }
    }

}

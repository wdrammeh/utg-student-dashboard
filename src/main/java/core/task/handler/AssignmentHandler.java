package core.task.handler;

import core.serial.Serializer;
import core.task.creator.AssignmentCreator;
import core.task.exhibition.AssignmentExhibition;
import core.task.self.AssignmentSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static core.task.TaskActivity.*;

public class AssignmentHandler {
    private static int doingCount;
    private static int doneCount;
    private static KPanel activeReside;
    private static KPanel doneReside;
    private static KButton portalButton;
    public static final ArrayList<AssignmentSelf> ASSIGNMENTS = new ArrayList<>();


    public static void initHandle(KButton portalButton){
        AssignmentHandler.portalButton = portalButton;
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
        activeReside.setLayout(new FlowLayout(CONTENTS_POSITION, 10, 10));

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
        doneReside.setLayout(new FlowLayout(CONTENTS_POSITION, 10, 10));
        if (!Dashboard.isFirst()) {
            deserialize();
        }
    }

    public static void transferAssignment(AssignmentSelf assignmentSelf,
                                          AssignmentExhibition assignmentExhibition, boolean isTime) {
        if (isTime) {
            assignmentSelf.setSubmissionDate(assignmentSelf.getDeadLine());
            completeTransfer(assignmentSelf);
        } else {
            if (App.showYesNoCancelDialog(assignmentExhibition.getRootPane(),"Confirm",
                    "Are you sure you have submitted this assignment already?")) {
                assignmentSelf.setSubmissionDate(MDate.formatDateOnly(new Date()));
                completeTransfer(assignmentSelf);
                assignmentExhibition.dispose();
            }
        }
    }

    private static void completeTransfer(AssignmentSelf aSelf){
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

    public static ActionListener removalListener(AssignmentSelf assignmentSelf,
                                                 AssignmentExhibition eDialog){
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
        portalButton.setText(doingCount);
    }

    public static void newIncoming(AssignmentSelf assignment){
        ASSIGNMENTS.add(assignment);
        activeReside.add(assignment.getLayer());
        MComponent.ready(activeReside);
    }

    public static void receiveFromSerials(AssignmentSelf aSelf){
        if (aSelf.isOn()) {
            activeReside.add(aSelf.getLayer());
        } else {
            doneReside.add(aSelf.getLayer());
        }
        ASSIGNMENTS.add(aSelf);
    }

    public static JComponent getComponent(){
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, activeAssignments(), doneAssignments());
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(275); // mathematically should be 3/4 or 75% of the consumable area
        return new KPanel(new BorderLayout(), splitPane);
    }

    private static JComponent activeAssignments() {
        final KButton createButton = new KButton("New Assignment");
        createButton.setFont(TASK_BUTTONS_FONT);
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.addActionListener(e-> {
            new AssignmentCreator().setVisible(true);
        });

        final KPanel labelPanelPlus = new KPanel(new BorderLayout());
        labelPanelPlus.add(createButton, BorderLayout.WEST);
        labelPanelPlus.add(new KPanel(new KLabel("Assignments", TASK_HEADERS_FONT)), BorderLayout.CENTER);

        final KPanel upperReside = new KPanel(new BorderLayout());
        upperReside.add(labelPanelPlus, BorderLayout.NORTH);
        upperReside.add(new KScrollPane(activeReside), BorderLayout.CENTER);
        return upperReside;
    }

    private static JComponent doneAssignments() {
        final KButton clearButton = new KButton("Clear");
        clearButton.setFont(TASK_BUTTONS_FONT);
        clearButton.setToolTipText("Remove all submissions");
        clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> {
            if (doneReside.getComponentCount() > 0) {
                if (App.showYesNoCancelDialog("Confirm",
                        "Are you sure you want to remove all submitted assignments?")) {
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
        labelPanelPlus.add(new KPanel(new KLabel("Submitted", TASK_HEADERS_FONT)),
                BorderLayout.CENTER);

        final KPanel lowerReside = new KPanel(new BorderLayout());
        lowerReside.add(labelPanelPlus, BorderLayout.NORTH);
        lowerReside.add(new KScrollPane(doneReside), BorderLayout.CENTER);
        return lowerReside;
    }

    public static int getDoingCount(){
        return doingCount;
    }

    public static int getDoneCount(){
        return doneCount;
    }

    private static void deserialize(){
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
                receiveFromSerials(assignmentSelf);
            }
        }
    }

}

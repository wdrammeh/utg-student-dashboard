package core.task.handler;

import core.task.creator.AssignmentCreator;
import core.task.exhibition.AssignmentExhibition;
import core.task.self.AssignmentSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static core.task.TaskActivity.*;
import static core.task.creator.TodoCreator.DESCRIPTION_LIMIT;

public class AssignmentHandler {
    public static final ArrayList<AssignmentSelf> ASSIGNMENTS = new ArrayList<>();
    private static int doingCount, doneCount;
    private static KPanel activeReside, doneReside;
    private static AssignmentCreator assignmentCreator;
    private static KButton bigButton;


    public AssignmentHandler(KButton bigButton){
        AssignmentHandler.bigButton = bigButton;
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
                        "Please provide the name of the course.");
                assignmentCreator.getNameField().requestFocusInWindow();
            } else if (name.length() > DESCRIPTION_LIMIT) {
                App.reportError(assignmentCreator.getRootPane(), "Error",
                        "Sorry, the subject name cannot exceed "+
                                DESCRIPTION_LIMIT +" characters.");
                assignmentCreator.getNameField().requestFocusInWindow();
            } else if (Globals.hasNoText(assignmentCreator.getProvidedDeadLine())) {
                App.reportError(assignmentCreator.getRootPane(), "Deadline Error",
                        "Please fill out all the fields for the deadline. You can change them later.");
            } else {
                final String type = assignmentCreator.isGroup() ? "Group Assignment" : "Individual Assignment";
                final String question = assignmentCreator.getQuestion();
                final Date givenDate = Objects.requireNonNull(MDate.parse(assignmentCreator.getProvidedDeadLine()+" 0:0:0"));
                if (givenDate.before(new Date())) {
                    App.reportError(assignmentCreator.getRootPane(), "Past Deadline",
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
                    final AssignmentSelf incomingAssignment = new AssignmentSelf(name, deadline,
                            question, assignmentCreator.isGroup(), mean);
                    ASSIGNMENTS.add(incomingAssignment);
                    activeReside.add(incomingAssignment.getLayer());
                    MComponent.ready(activeReside);
                    assignmentCreator.dispose();
                }
            }
        };
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
        bigButton.setText(doingCount);
    }

    public static void receiveFromSerials(AssignmentSelf aSelf){
        if (aSelf.isOn()) {
            activeReside.add(aSelf.getLayer());
        } else {
            doneReside.add(aSelf.getLayer());
        }
        ASSIGNMENTS.add(aSelf);
    }

    public JComponent getComponent(){
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, activeAssignments(), doneAssignments());
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(200);
        return new KPanel(new BorderLayout(), splitPane);
    }

    private JComponent activeAssignments() {
        final KButton createButton = new KButton("New Assignment");
        createButton.setFont(TASK_BUTTONS_FONT);
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.setToolTipText("Add Assignment");
        createButton.addActionListener(e-> {
            assignmentCreator = new AssignmentCreator();
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
        final KButton clearButton = new KButton("Clear");
        clearButton.setFont(TASK_BUTTONS_FONT);
        clearButton.setToolTipText("Remove all submissions");
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

    public static int getDoingCount(){
        return doingCount;
    }

    public static int getDoneCount(){
        return doneCount;
    }

}

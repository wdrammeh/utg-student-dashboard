package core;

import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Like its co., TaskExhibition too has subclasses for all the separate task types,
 * and it's for exhibiting them.
 * The event task type has no Exhibitor.
 * TaskExhibitions are made visible at time of creation.
 */
public class TaskExhibition {


    private static KLabel hintLabel(String text){
        return new KLabel(text, KFontFactory.createBoldFont(16));
    }

    private static KLabel valueLabel(String text){
        return new KLabel(text, KFontFactory.createPlainFont(16));
    }


    public static class TodoExhibition extends KDialog {

        public TodoExhibition(TaskSelf.TodoSelf theTask){
            super("Task");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final KPanel contentPanel = new KPanel();//To be used... contentPane!
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            final KPanel nameLayer = new KPanel(new BorderLayout());
            nameLayer.add(new KPanel(hintLabel("Task name:")),BorderLayout.WEST);
            nameLayer.add(new KPanel(valueLabel(theTask.getDescription())),BorderLayout.CENTER);

            final KPanel stateLayer = new KPanel(new BorderLayout());
            stateLayer.add(new KPanel(hintLabel("Current state:")),BorderLayout.WEST);
            stateLayer.add(new KPanel(valueLabel(theTask.isActive() ? "In progress" : "Completed")),BorderLayout.CENTER);

            final KPanel durationLayer = new KPanel(new BorderLayout());
            durationLayer.add(new KPanel(hintLabel("Specified duration:")),BorderLayout.WEST);
            durationLayer.add(new KPanel(valueLabel(theTask.getSpecifiedDuration()+" days")),BorderLayout.CENTER);

            final KPanel addDateLayer = new KPanel(new BorderLayout());
            addDateLayer.add(new KPanel(hintLabel("Date Initiated:")),BorderLayout.WEST);
            addDateLayer.add(new KPanel(valueLabel(theTask.getStartDate())),BorderLayout.CENTER);

            final KPanel completeDateLayer = new KPanel(new BorderLayout());
            completeDateLayer.add(new KPanel(hintLabel("Expected to complete:")),BorderLayout.WEST);
            completeDateLayer.add(new KPanel(valueLabel(theTask.getDateExpectedToComplete())),BorderLayout.CENTER);

            final KPanel soFarLayer = new KPanel(new BorderLayout());
            soFarLayer.add(new KPanel(hintLabel("Time taken so far:")),BorderLayout.WEST);
            soFarLayer.add(new KPanel(valueLabel(theTask.getDaysTaken() == 0 ? "Less than a day" :
                    Globals.checkPlurality(theTask.getDaysTaken(), "days"))), BorderLayout.CENTER);

            final KPanel remainingLayer = new KPanel(new BorderLayout());
            remainingLayer.add(new KPanel(hintLabel("Time remaining:")),BorderLayout.WEST);
            remainingLayer.add(new KPanel(valueLabel(theTask.getDaysLeft() == 1 ? "Less than a day" :
                    theTask.getDaysLeft()+" days")),BorderLayout.CENTER);

            final KPanel completedLayer = new KPanel(new BorderLayout());
            completedLayer.add(new KPanel(hintLabel("Date completed:")),BorderLayout.WEST);
            completedLayer.add(new KPanel(valueLabel(theTask.getDateCompleted())),BorderLayout.CENTER);

            final KPanel consumedLayer = new KPanel(new BorderLayout());
            consumedLayer.add(new KPanel(hintLabel("Time consumed:")),BorderLayout.WEST);
            consumedLayer.add(new KPanel(valueLabel(theTask.getTotalTimeConsumed() == 0 ? "Less than a day" :
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
            doneButton.addActionListener(e -> TaskActivity.TodoHandler.transferTask(theTask,this,false));

            final KButton removeButton = new KButton("Remove");
            removeButton.setForeground(Color.RED);
            removeButton.addActionListener(TaskActivity.TodoHandler.removalWaiter(theTask,this));

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
    }


    public static class ProjectExhibition extends KDialog{

        public ProjectExhibition(TaskSelf.ProjectSelf theProject){
            setTitle("Project");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final KPanel exhibitionPane = new KPanel();
            exhibitionPane.setLayout(new BoxLayout(exhibitionPane, BoxLayout.Y_AXIS));

            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KPanel(hintLabel("Project name:")),BorderLayout.WEST);
            namePanel.add(new KPanel(valueLabel(theProject.getProjectName())),BorderLayout.CENTER);

            final KPanel typePanel = new KPanel(new BorderLayout());
            typePanel.add(new KPanel(hintLabel("Type:")),BorderLayout.WEST);
            typePanel.add(new KPanel(valueLabel(theProject.getType())),BorderLayout.CENTER);

            final KPanel statusPanel = new KPanel(new BorderLayout());
            statusPanel.add(new KPanel(hintLabel("Status:")),BorderLayout.WEST);
            statusPanel.add(new KPanel(valueLabel(theProject.isLive() ? "Running" : "Completed")),BorderLayout.CENTER);

            final KPanel startedPanel = new KPanel(new BorderLayout());
            startedPanel.add(new KPanel(hintLabel("Started:")),BorderLayout.WEST);
            startedPanel.add(new KPanel(valueLabel(theProject.getStartDate())),BorderLayout.CENTER);

            final KPanel expectedPanel = new KPanel(new BorderLayout());
            expectedPanel.add(new KPanel(hintLabel("Expected to complete:")),BorderLayout.WEST);
            expectedPanel.add(new KPanel(valueLabel(theProject.getDateExpectedToComplete())), BorderLayout.CENTER);

            final KPanel soFarPanel = new KPanel(new BorderLayout());
            soFarPanel.add(new KPanel(hintLabel("Time taken so far:")),BorderLayout.WEST);
            soFarPanel.add(new KPanel(valueLabel(theProject.getDaysTaken() == 0 ? "Less than a day" :
                    Globals.checkPlurality(theProject.getDaysTaken(), "days"))),BorderLayout.CENTER);

            final KPanel remPanel = new KPanel(new BorderLayout());
            remPanel.add(new KPanel(hintLabel("Time remaining:")),BorderLayout.WEST);
            remPanel.add(new KPanel(valueLabel(theProject.getDaysLeft() == 1 ? "Less than a day" :
                    theProject.getDaysLeft()+" days")), BorderLayout.CENTER);

            final KPanel completedPanel = new KPanel(new BorderLayout());
            completedPanel.add(new KPanel(hintLabel("Completed:")),BorderLayout.WEST);
            completedPanel.add(new KPanel(valueLabel(theProject.getDateCompleted())), BorderLayout.CENTER);

            final KPanel consumedPanel = new KPanel(new BorderLayout());
            consumedPanel.add(new KPanel(hintLabel("Time Consumed:")),BorderLayout.WEST);
            consumedPanel.add(new KPanel(valueLabel(theProject.getTotalTimeConsumed() == 0 ? "Less than a day" :
                    theProject.getDaysTaken() == theProject.getSpecifiedDuration() ?
                            "Specified period ("+theProject.getSpecifiedDuration()+" days)" :
                            theProject.getDaysTaken() +" days")), BorderLayout.CENTER);

            if (theProject.isLive()) {
                exhibitionPane.addAll(namePanel, typePanel, statusPanel, startedPanel, expectedPanel,
                        soFarPanel, remPanel);
            } else {
                exhibitionPane.addAll(namePanel, typePanel, statusPanel, startedPanel, completedPanel, consumedPanel);
            }

            final KButton closeButton = new KButton("Close");
            closeButton.addActionListener(e-> dispose());

            exhibitionPane.add(Box.createVerticalStrut(25));
            exhibitionPane.add(new KPanel(closeButton));

            rootPane.setDefaultButton(closeButton);
            setContentPane(exhibitionPane);
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
            SwingUtilities.invokeLater(()-> setVisible(true));
        }
    }


    public static class AssignmentExhibition extends KDialog {

        public AssignmentExhibition(TaskSelf.AssignmentSelf assignment){
            setTitle(assignment.getCourseName()+" - "+(assignment.isGroup() ? "Group Assignment" : "Personal Assignment"));
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final KPanel subjectPanel = new KPanel(new BorderLayout());
            subjectPanel.add(new KPanel(hintLabel("Subject:")),BorderLayout.WEST);
            subjectPanel.add(new KPanel(valueLabel(assignment.getCourseName())),BorderLayout.CENTER);

            final KPanel statusPanel = new KPanel(new BorderLayout());
            statusPanel.add(new KPanel(hintLabel("Status:")),BorderLayout.WEST);
            statusPanel.add(new KPanel(valueLabel(assignment.isOn() ? "In-progress / Un-submitted" : "Submitted")),
                    BorderLayout.CENTER);

            final KPanel startPanel = new KPanel(new BorderLayout());
            startPanel.add(new KPanel(hintLabel("Date Initiated:")),BorderLayout.WEST);
            startPanel.add(new KPanel(valueLabel(assignment.getStartDate())),BorderLayout.CENTER);

            final KPanel submittedPanel = new KPanel(new BorderLayout());
            submittedPanel.add(new KPanel(hintLabel("Date Submitted:")),BorderLayout.WEST);
            submittedPanel.add(new KPanel(valueLabel(assignment.getSubmissionDate())),BorderLayout.CENTER);

            final KPanel deadlinePanel = new KPanel(new BorderLayout());
            deadlinePanel.add(new KPanel(hintLabel("Deadline Given:")),BorderLayout.WEST);
            deadlinePanel.add(new KPanel(valueLabel(assignment.getDeadLine())),BorderLayout.CENTER);

            final KPanel remainPanel = new KPanel(new BorderLayout());
            remainPanel.add(new KPanel(hintLabel("Time Remaining:")),BorderLayout.WEST);
            remainPanel.add(new KPanel(valueLabel((Globals.checkPlurality(assignment.getTimeRemaining(),"days")) +
                    " to submit")),BorderLayout.CENTER);

            final KPanel modePanel = new KPanel(new BorderLayout());
            modePanel.add(new KPanel(hintLabel("Mode of submission:")), BorderLayout.WEST);
            modePanel.add(new KPanel(valueLabel(assignment.getModeOfSubmission())), BorderLayout.CENTER);

            final KPanel questionPanel = new KPanel(new BorderLayout());
            questionPanel.add(new KPanel(hintLabel("Question(s):")), BorderLayout.WEST);
            final KTextArea questionArea = new KTextArea();
            questionArea.setText(assignment.getQuestion());
            final KScrollPane scrollPane = questionArea.outerScrollPane(new Dimension(475,150));
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY,1,false));
            questionPanel.add(scrollPane, BorderLayout.CENTER);

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            if (assignment.isOn()) {
                AssignmentExhibition.this.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent e) {
                        assignment.setQuestion(questionArea.getText());
                    }
                });
                questionPanel.add(new KLabel("Question(s) wont be editable after assignments are submitted.",
                        KFontFactory.createPlainFont(15), Color.RED), BorderLayout.SOUTH);
                contentPanel.addAll(subjectPanel, statusPanel, startPanel, deadlinePanel, remainPanel, modePanel,
                        questionPanel);
            } else {
                questionArea.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        e.consume();
                    }
                });
                contentPanel.addAll(subjectPanel, statusPanel, startPanel, submittedPanel, deadlinePanel,
                        modePanel, questionPanel);
            }

            final KButton submitButton = new KButton("Mark as Submit");
            submitButton.setForeground(Color.BLUE);
            submitButton.addActionListener(e->
                    TaskActivity.AssignmentsHandler.transferAssignment(assignment,this,false));

            final KButton removeButton = new KButton("Remove");
            removeButton.setForeground(Color.RED);
            removeButton.addActionListener(TaskActivity.AssignmentsHandler.removalListener(assignment, this));

            final KButton closeButton = new KButton("Close");
            closeButton.setFocusable(true);
            closeButton.addActionListener(e-> dispose());

            final KPanel buttonsContainer = new KPanel(new FlowLayout(FlowLayout.CENTER));
            if (assignment.isOn()) {
                buttonsContainer.addAll(removeButton, submitButton, closeButton);
            } else {
                buttonsContainer.addAll(removeButton, closeButton);
            }

            contentPanel.addAll(MComponent.contentBottomGap(), buttonsContainer);

            rootPane.setDefaultButton(closeButton);
            setContentPane(contentPanel);
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
            SwingUtilities.invokeLater(()-> setVisible(true));
        }
    }

}

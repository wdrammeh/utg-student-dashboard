package core.task.exhibition;

import core.Board;
import core.task.handler.AssignmentHandler;
import core.task.self.AssignmentSelf;
import core.utils.Globals;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AssignmentExhibition extends KDialog {

    public AssignmentExhibition(AssignmentSelf assignment){
        setTitle(assignment.getCourseName()+" - "+(assignment.isGroup() ? "Group Assignment" : "Personal Assignment"));
        setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
        setResizable(true);

        final KPanel subjectPanel = new KPanel(new BorderLayout());
        subjectPanel.add(new KPanel(newHintLabel("Subject:")),BorderLayout.WEST);
        subjectPanel.add(new KPanel(newValueLabel(assignment.getCourseName())),BorderLayout.CENTER);

        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(newHintLabel("Status:")),BorderLayout.WEST);
        statusPanel.add(new KPanel(newValueLabel(assignment.isOn() ? "In-progress / Un-submitted" : "Submitted")),
                BorderLayout.CENTER);

        final KPanel startPanel = new KPanel(new BorderLayout());
        startPanel.add(new KPanel(newHintLabel("Date Initiated:")),BorderLayout.WEST);
        startPanel.add(new KPanel(newValueLabel(assignment.getStartDate())),BorderLayout.CENTER);

        final KPanel submittedPanel = new KPanel(new BorderLayout());
        submittedPanel.add(new KPanel(newHintLabel("Date Submitted:")),BorderLayout.WEST);
        submittedPanel.add(new KPanel(newValueLabel(assignment.getSubmissionDate())),BorderLayout.CENTER);

        final KPanel deadlinePanel = new KPanel(new BorderLayout());
        deadlinePanel.add(new KPanel(newHintLabel("Deadline Given:")),BorderLayout.WEST);
        deadlinePanel.add(new KPanel(newValueLabel(assignment.getDeadLine())),BorderLayout.CENTER);

        final KPanel remainPanel = new KPanel(new BorderLayout());
        remainPanel.add(new KPanel(newHintLabel("Time Remaining:")),BorderLayout.WEST);
        remainPanel.add(new KPanel(newValueLabel((Globals.checkPlurality(assignment.getTimeRemaining(),"days")) +
                " to submit")),BorderLayout.CENTER);

        final KPanel modePanel = new KPanel(new BorderLayout());
        modePanel.add(new KPanel(newHintLabel("Mode of submission:")), BorderLayout.WEST);
        modePanel.add(new KPanel(newValueLabel(assignment.getModeOfSubmission())), BorderLayout.CENTER);

        final KPanel questionPanel = new KPanel(new BorderLayout());
        questionPanel.add(new KPanel(newHintLabel("Question(s):")), BorderLayout.WEST);
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
            questionPanel.add(new KLabel("Question(s) won't be editable after assignments are submitted.",
                    KFontFactory.createPlainFont(15), Color.RED), BorderLayout.SOUTH);
            contentPanel.addAll(subjectPanel, statusPanel, startPanel, deadlinePanel, remainPanel, modePanel,
                    questionPanel);
        } else {
            questionArea.setEditable(false);
            contentPanel.addAll(subjectPanel, statusPanel, startPanel, submittedPanel, deadlinePanel,
                    modePanel, questionPanel);
        }

        final KButton submitButton = new KButton("Mark as Submit");
        submitButton.setForeground(Color.BLUE);
        submitButton.addActionListener(e->
                AssignmentHandler.transferAssignment(assignment,this,false));

        final KButton removeButton = new KButton("Remove");
        removeButton.setForeground(Color.RED);
        removeButton.addActionListener(AssignmentHandler.removalListener(assignment, this));

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

    private static KLabel newHintLabel(String text){
        return new KLabel(text, KFontFactory.createBoldFont(16));
    }

    private static KLabel newValueLabel(String text){
        return new KLabel(text, KFontFactory.createPlainFont(16));
    }

}
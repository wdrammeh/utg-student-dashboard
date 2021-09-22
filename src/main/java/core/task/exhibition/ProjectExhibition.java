package core.task.exhibition;

import core.Board;
import core.task.self.ProjectSelf;
import core.utils.FontFactory;
import core.utils.Globals;
import proto.KButton;
import proto.KDialog;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;

public class ProjectExhibition extends KDialog {

    public ProjectExhibition(ProjectSelf theProject){
        setTitle("Project");
        setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
        setResizable(true);

        final KPanel exhibitionPane = new KPanel();
        exhibitionPane.setLayout(new BoxLayout(exhibitionPane, BoxLayout.Y_AXIS));

        final KPanel namePanel = new KPanel(new BorderLayout());
        namePanel.add(new KPanel(newHintLabel("Project name:")),BorderLayout.WEST);
        namePanel.add(new KPanel(newValueLabel(theProject.getProjectName())),BorderLayout.CENTER);

        final KPanel typePanel = new KPanel(new BorderLayout());
        typePanel.add(new KPanel(newHintLabel("Type:")),BorderLayout.WEST);
        typePanel.add(new KPanel(newValueLabel(theProject.getType())),BorderLayout.CENTER);

        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(newHintLabel("Status:")),BorderLayout.WEST);
        statusPanel.add(new KPanel(newValueLabel(theProject.isLive() ? "Running" : "Completed")),BorderLayout.CENTER);

        final KPanel startedPanel = new KPanel(new BorderLayout());
        startedPanel.add(new KPanel(newHintLabel("Started:")),BorderLayout.WEST);
        startedPanel.add(new KPanel(newValueLabel(theProject.getStartDate())),BorderLayout.CENTER);

        final KPanel expectedPanel = new KPanel(new BorderLayout());
        expectedPanel.add(new KPanel(newHintLabel("Expected to complete:")),BorderLayout.WEST);
        expectedPanel.add(new KPanel(newValueLabel(theProject.getDateExpectedToComplete())), BorderLayout.CENTER);

        final KPanel soFarPanel = new KPanel(new BorderLayout());
        soFarPanel.add(new KPanel(newHintLabel("Time taken so far:")),BorderLayout.WEST);
        soFarPanel.add(new KPanel(newValueLabel(theProject.getDaysTaken() == 0 ? "Less than a day" :
                Globals.checkPlurality(theProject.getDaysTaken(), "days"))),BorderLayout.CENTER);

        final KPanel remPanel = new KPanel(new BorderLayout());
        remPanel.add(new KPanel(newHintLabel("Time remaining:")),BorderLayout.WEST);
        remPanel.add(new KPanel(newValueLabel(theProject.getDaysLeft() == 1 ? "Less than a day" :
                theProject.getDaysLeft()+" days")), BorderLayout.CENTER);

        final KPanel completedPanel = new KPanel(new BorderLayout());
        completedPanel.add(new KPanel(newHintLabel("Completed:")),BorderLayout.WEST);
        completedPanel.add(new KPanel(newValueLabel(theProject.getDateCompleted())), BorderLayout.CENTER);

        final KPanel consumedPanel = new KPanel(new BorderLayout());
        consumedPanel.add(new KPanel(newHintLabel("Time Consumed:")),BorderLayout.WEST);
        consumedPanel.add(new KPanel(newValueLabel(theProject.getTotalTimeConsumed() == 0 ? "Less than a day" :
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

    private static KLabel newHintLabel(String text){
        return new KLabel(text, FontFactory.createBoldFont(16));
    }

    private static KLabel newValueLabel(String text){
        return new KLabel(text, FontFactory.createPlainFont(16));
    }

}

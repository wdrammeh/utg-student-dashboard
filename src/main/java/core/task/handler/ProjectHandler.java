package core.task.handler;

import core.task.creator.ProjectCreator;
import core.task.self.ProjectSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.KButton;
import proto.KLabel;
import proto.KPanel;
import proto.KScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import static core.task.TaskActivity.*;
import static core.task.creator.TodoCreator.DESCRIPTION_LIMIT;

public class ProjectHandler {
    public static final ArrayList<ProjectSelf> PROJECTS = new ArrayList<>();
    private static int liveCount, completeCount; // count - number of all, liveCount - number currently running
    private static KPanel projectsReside;
    private static ProjectCreator projectCreator;
    private static KButton bigButton;


    public ProjectHandler(KButton bigButton){
        ProjectHandler.bigButton = bigButton;
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
                App.reportError("No Name","Please specify a name for the project.");
                projectCreator.getNameField().requestFocusInWindow();
            } else if (name.length() > DESCRIPTION_LIMIT) {
                App.reportError("Error", "Sorry, name of a project must be at most "+
                        DESCRIPTION_LIMIT +" characters.");
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
                    final ProjectSelf incomingProject = new ProjectSelf(name,
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

    public static void performIComplete(ProjectSelf project, boolean timeDue){
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

    private static void finalizeCompletion(ProjectSelf project){
        project.setDateCompleted(MDate.now());
        project.setLive(false);
        project.setUpDoneUI();
        //Respect that order of sorting... since the project generator does not use clear-cut separator
        projectsReside.remove(project.getLayer());
        projectsReside.add(project.getLayer());
        MComponent.ready(projectsReside);
    }

    public static ActionListener removalListener(ProjectSelf project){
        return e -> {
            if (App.showYesNoCancelDialog("Confirm","Are you sure you want to remove this project?")) {
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
        bigButton.setText(liveCount);
    }

    public static void receiveFromSerials(ProjectSelf dProject){
        projectsReside.add(dProject.getLayer());
        PROJECTS.add(dProject);
        if (dProject.isLive()) {
            renewCount(1);
        } else {
            completeCount++;
        }
    }

    public JComponent getComponent(){
        final KButton addButton = new KButton("New Project");
        addButton.setFont(TASK_BUTTONS_FONT);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setToolTipText("Create Project");
        addButton.addActionListener(e-> {
            projectCreator = new ProjectCreator();
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

    private static int getTotalCount(){
        return liveCount + completeCount;
    }

    public static int getLiveCount(){
        return liveCount;
    }

}

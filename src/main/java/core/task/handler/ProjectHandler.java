package core.task.handler;

import core.serial.Serializer;
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
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import static core.task.TaskActivity.*;

public class ProjectHandler {
    private static int liveCount; // count - number of all, liveCount - number currently running
    private static int completeCount;
    private static KPanel projectsReside;
    private static KButton portalButton;
    public static final ArrayList<ProjectSelf> PROJECTS = new ArrayList<>();


    public static void initHandle(KButton portalButton){
        ProjectHandler.portalButton = portalButton;
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
        projectsReside.setLayout(new FlowLayout(CONTENTS_POSITION, 10, 10));
        if (!Dashboard.isFirst()) {
            deserialize();
        }
    }

    public static void performIComplete(ProjectSelf project, boolean timeDue){
        if (timeDue) {
            project.setTotalTimeConsumed(project.getSpecifiedDuration());
            finalizeCompletion(project);
            renewCount(-1);
            completeCount++;
        } else {
            if (App.showYesNoCancelDialog("Confirm",
                    "Are you sure you've completed this project?")) {
                project.setTotalTimeConsumed(project.getDaysTaken());
                finalizeCompletion(project);
                renewCount(-1);
                completeCount++;
            }
        }

    }

    private static void finalizeCompletion(ProjectSelf project){
        project.setDateCompleted(MDate.formatNow());
        project.setLive(false);
        project.setUpDoneUI();
        // Respect that order of sorting... since the project generator does not use clear-cut separator
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
        portalButton.setText(liveCount);
    }

    public static void newIncoming(ProjectSelf project){
        PROJECTS.add(project);
        projectsReside.add(project.getLayer());
        MComponent.ready(projectsReside);
        renewCount(1);
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

    public static JComponent getComponent(){
        final KButton addButton = new KButton("New Project");
        addButton.setFont(TASK_BUTTONS_FONT);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setToolTipText("Create Project");
        addButton.addActionListener(e-> {
            new ProjectCreator().setVisible(true);
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

    private static void deserialize(){
        final Object projectObj = Serializer.fromDisk(Serializer.inPath("tasks", "projects.ser"));
        if (projectObj == null) {
            App.silenceException("Failed to read Projects.");
        } else {
            final String[] projects = (String[]) projectObj;
            for (String data : projects) {
                final String[] lines = Globals.splitLines(data);
                final ProjectSelf projectSelf = new ProjectSelf(lines[0], lines[1],
                        MDate.formatDayTime(MDate.fromSerial(lines[2])), Integer.parseInt(lines[3]),
                        Boolean.parseBoolean(lines[5]));
                projectSelf.setTotalTimeConsumed(Integer.parseInt(lines[4]));
                projectSelf.setDateCompleted(MDate.formatDayTime(MDate.fromSerial(lines[6])));
                projectSelf.eveIsAlerted = Boolean.parseBoolean(lines[7]);
                projectSelf.completionIsAlerted = Boolean.parseBoolean(lines[8]);
                if (projectSelf.isLive()) {
                    if (new Date().before(MDate.parseDayTime(projectSelf.getDateExpectedToComplete()))) {
                        projectSelf.wakeLive();
                        projectSelf.initializeUI();
                    } else {
                        projectSelf.wakeDead();
                        projectSelf.setUpDoneUI();
                    }
                } else {
                    projectSelf.setUpDoneUI();
                }
                receiveFromSerials(projectSelf);
            }
        }
    }

}

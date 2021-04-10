package core.task;

import core.serial.Serializer;
import core.task.handler.AssignmentHandler;
import core.task.handler.EventHandler;
import core.task.handler.ProjectHandler;
import core.task.handler.TodoHandler;
import core.task.self.AssignmentSelf;
import core.task.self.EventSelf;
import core.task.self.ProjectSelf;
import core.task.self.TodoSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * It specifies the task itself - provides description, dates, etc.
 * A TaskSelf must support serialization, but not necessarily for itself.
 */
public class TaskCentral {

    public static void serializeAll(){
        final ArrayList<TodoSelf> todoList = TodoHandler.TODOS;
        final String[] todos = new String[todoList.size()];
        for(int i = 0; i < todoList.size(); i++){
            todos[i] = todoList.get(i).export();
        }
        Serializer.toDisk(todos, Serializer.inPath("tasks", "todos.ser"));

        final ArrayList<ProjectSelf> projectList = ProjectHandler.PROJECTS;
        final String[] projects = new String[projectList.size()];
        for(int i = 0; i < projectList.size(); i++){
            projects[i] = projectList.get(i).export();
        }
        Serializer.toDisk(projects, Serializer.inPath("tasks", "projects.ser"));

        final ArrayList<AssignmentSelf> assignmentList = AssignmentHandler.ASSIGNMENTS;
        final String[] assignments = new String[assignmentList.size()];
        final String[] questions = new String[assignments.length];
        final ArrayList<Integer> groupIndexes = new ArrayList<>();
        for(int i = 0; i < assignments.length; i++){
            final AssignmentSelf assignment = assignmentList.get(i);
            assignments[i] = assignment.export();
            questions[i] = assignment.getQuestion();
            if (assignment.isGroup()) {
                groupIndexes.add(i);
            }
        }
        Serializer.toDisk(assignments, Serializer.inPath("tasks", "assignments.ser"));
        Serializer.toDisk(questions, Serializer.inPath("tasks", "questions.ser"));
        final String[] groupsMembers = new String[groupIndexes.size()];
        int j = 0;
        for (int index : groupIndexes){
            final AssignmentSelf assignment = assignmentList.get(index);
            groupsMembers[j] = Globals.joinLines(assignment.members.toArray());
            j++;
        }
        Serializer.toDisk(groupsMembers, Serializer.inPath("tasks", "groups.members.ser"));

        final ArrayList<EventSelf> eventsList = EventHandler.EVENTS;
        final String[] events = new String[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i++){
            events[i] = eventsList.get(i).export();
        }
        Serializer.toDisk(events, Serializer.inPath("tasks", "events.ser"));
    }

    public static void deSerializeAll(){
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
                TodoHandler.receiveFromSerials(todoSelf);
            }
        }

        final Object projectObj = Serializer.fromDisk(Serializer.inPath("tasks", "projects.ser"));
        if (projectObj == null) {
            App.silenceException("Failed to read Projects.");
        } else {
            final String[] projects = (String[]) projectObj;
            for (String data : projects) {
                final String[] lines = Globals.splitLines(data);
                final ProjectSelf projectSelf = new ProjectSelf(lines[0], lines[1], lines[2],
                        Integer.parseInt(lines[3]), Boolean.parseBoolean(lines[5]));
                projectSelf.setTotalTimeConsumed(Integer.parseInt(lines[4]));
                projectSelf.setDateCompleted(lines[6]);
                projectSelf.eveIsAlerted = Boolean.parseBoolean(lines[7]);
                projectSelf.completionIsAlerted = Boolean.parseBoolean(lines[8]);
                if (projectSelf.isLive()) {
                    if (new Date().before(MDate.parse(projectSelf.getDateExpectedToComplete()))) {
                        projectSelf.wakeLive();
                        projectSelf.initializeUI();
                    } else {
                        projectSelf.wakeDead();
                        projectSelf.setUpDoneUI();
                    }
                } else {
                    projectSelf.setUpDoneUI();
                }
                ProjectHandler.receiveFromSerials(projectSelf);
            }
        }

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
                AssignmentHandler.receiveFromSerials(assignmentSelf);
            }
        }

        final Object eventsObj = Serializer.fromDisk(Serializer.inPath("tasks", "events.ser"));
        if (eventsObj == null) {
            App.silenceException("Failed to read Events.");
        } else {
            final String[] events = (String[]) eventsObj;
            for (String data : events){
                final String[] lines = Globals.splitLines(data);
                final EventSelf eventSelf = new EventSelf(lines[0], lines[1], Boolean.parseBoolean(lines[2]));
                eventSelf.eveIsAlerted = Boolean.parseBoolean(lines[3]);
                eventSelf.timeupIsAlerted = Boolean.parseBoolean(lines[4]);
                eventSelf.setUpUI(); // Todo consider recall
                if (eventSelf.isPending()) {
                    if (MDate.parse(MDate.formatDateOnly(new Date()) + " 0:0:0").
                            before(MDate.parse(eventSelf.getDateDue() + " 0:0:0"))) {
                        eventSelf.wakeAlive();
                    } else {
                        eventSelf.endState();
                    }
                }
                eventSelf.setUpUI();
                EventHandler.receiveFromSerials(eventSelf);
            }
        }
    }

}

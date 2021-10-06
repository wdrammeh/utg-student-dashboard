package core.module;

import core.Activity;
import core.Board;
import core.user.Student;
import core.utils.FontFactory;
import core.utils.Globals;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringJoiner;

/**
 * The Analysis type is stand-alone: it does the analysis, presents the analysis.
 * By that, it subclasses its collaborators like the performance-sketch and present-dialogues.
 */
public class ModuleAnalysis implements Activity {
    private KLabel APlusLabel, ANeutralLabel, AMinusLabel, BPlusLabel, BNeutralLabel, BMinusLabel,
            CPlusLabel, CNeutralLabel, CMinusLabel, DLabel, FLabel;

    private KLabel highestScoreLabel, lowestScoreLabel, highestMajorScoreLabel, lowestMajorScoreLabel,
            highestMinorScoreLabel, lowestMinorScoreLabel, highestDERScoreLabel, lowestDERScoreLabel,
            highestGERScoreLabel, lowestGERScoreLabel;

    private KLabel majorsLabel, minorsLabel, DERsLabel, GERsLabel, unclassifiedListLabel;
    private KLabel allModulesLabel;

    private ArrayList<Course> APlusList, ANeutralList, AMinusList, BPlusList, BNeutralList, BMinusList,
            CPlusList, CNeutralList, CMinusList, DList, FList;

    private ArrayList<Course> majorsList, minorsList, DERList, GERList, unclassifiedList;

    private Course highestScoreCourse, lowestScoreCourse, highestMajorScoreCourse, lowestMajorScoreCourse,
            highestMinorScoreCourse, lowestMinorScoreCourse, highestDERScoreCourse, lowestDERScoreCourse,
            highestGERScoreCourse, lowestGERScoreCourse;

    private static ArrayList<String> semestersList, yearsList;
    private static ArrayList<Double> semesterScores;

    private CardLayout cardLayout;
    private KPanel modulesBasement, semestersBasement, yearsBasement;
    private static final Font HINT_FONT = FontFactory.createBoldFont(16);
    private static final Font VALUE_FONT = FontFactory.createPlainFont(15);
    private static final Font FOCUS_FONT = FontFactory.createPlainFont(18);
    private static final Cursor FOCUS_CURSOR = MComponent.HAND_CURSOR;


    public ModuleAnalysis(){
        final KPanel analysisActivity = new KPanel(new BorderLayout());
        if (Student.isGuest()) {
            analysisActivity.add(MComponent.createUnavailableActivity("Analysis"), BorderLayout.CENTER);
        } else {
            cardLayout = new CardLayout();
            final KPanel analysisContents = new KPanel(cardLayout);
            cardLayout.addLayoutComponent(analysisContents.add(getModulesBasement()), "courses");
            cardLayout.addLayoutComponent(analysisContents.add(getSemestersBasement()), "semesters");
            cardLayout.addLayoutComponent(analysisContents.add(getYearsBasement()),"years");

            final KComboBox<String> optionsCombo = new KComboBox<>(new String[]
                    {"My Courses", "Semesters", "Academic Years"});
            optionsCombo.setToolTipText("Change Analysis");
            optionsCombo.addActionListener(e-> {
                if (optionsCombo.getSelectedIndex() == 0) {
                    cardLayout.show(analysisContents, "courses");
                } else if (optionsCombo.getSelectedIndex() == 1) {
                    cardLayout.show(analysisContents, "semesters");
                } else if (optionsCombo.getSelectedIndex() == 2) {
                    cardLayout.show(analysisContents, "years");
                }
            });

            final KPanel sensitivePanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            sensitivePanel.addAll(new KLabel("Showing analysis based on:",
                            FontFactory.createPlainFont(15)), optionsCombo);

            final KPanel northPanel = new KPanel(new BorderLayout());
            northPanel.add(new KPanel(new KLabel("Analysis Center", FontFactory.BODY_HEAD_FONT)),
                    BorderLayout.WEST);
            northPanel.add(sensitivePanel, BorderLayout.EAST);

            analysisActivity.add(northPanel, BorderLayout.NORTH);
            analysisActivity.add(analysisContents, BorderLayout.CENTER);
        }

        Board.addCard(analysisActivity, "Analysis");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Analysis");
        if (!Student.isGuest()) {
            SwingUtilities.invokeLater(()-> {
                resetLists();
                completeModulesBasement();
                completeSemestersBasement();
                completeYearsBasement();
                resetCourses();
                resetLabels();
            });
        }
    }

    /**
     * Returns the component onto which the {@link #modulesBasement} is placed.
     * Firstly, all relevant labels are initialized.
     */
    private Component getModulesBasement(){
//        by grade
        APlusLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("A+ Grades", APlusList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        ANeutralLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("A Grades", ANeutralList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        AMinusLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("A- Grades", AMinusList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        BPlusLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("B+ Grades", BPlusList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        BNeutralLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("B Grades", BNeutralList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        BMinusLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("B- Grades", BMinusList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        CPlusLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("C+ Grades", CPlusList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        CNeutralLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("C Grades", CNeutralList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        CMinusLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("C- Grades", CMinusList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        DLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("D Grades", DList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        FLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("F Grades", FList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

//        by score
        highestScoreLabel = newSingletonLabel(()-> highestScoreCourse.exhibit());
        lowestScoreLabel = newSingletonLabel(()-> lowestScoreCourse.exhibit());
        highestMajorScoreLabel = newSingletonLabel(()-> highestMajorScoreCourse.exhibit());
        lowestMajorScoreLabel = newSingletonLabel(()-> lowestMajorScoreCourse.exhibit());
        highestMinorScoreLabel = newSingletonLabel(()-> highestMinorScoreCourse.exhibit());
        lowestMinorScoreLabel = newSingletonLabel(()-> lowestMinorScoreCourse.exhibit());
        highestDERScoreLabel = newSingletonLabel(()-> highestDERScoreCourse.exhibit());
        lowestDERScoreLabel = newSingletonLabel(()-> lowestDERScoreCourse.exhibit());
        highestGERScoreLabel = newSingletonLabel(()-> highestGERScoreCourse.exhibit());
        lowestGERScoreLabel = newSingletonLabel(()-> lowestGERScoreCourse.exhibit());

//        by requirement
        majorsLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("My Majors", majorsList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        minorsLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("My Minors", minorsList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        DERsLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("Divisional Educational Requirements", DERList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        GERsLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("General Education Requirements", GERList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        unclassifiedListLabel = newValueLabel(()-> {
            final GlassPrompt glassPrompt = new GlassPrompt("Unknown Requirements", unclassifiedList);
            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
        });

        allModulesLabel = new KLabel("", VALUE_FONT, Color.BLUE);
        allModulesLabel.underline(true);
        allModulesLabel.setCursor(FOCUS_CURSOR);
        allModulesLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final GlassPrompt modulesPrompt = new GlassPrompt("My Modules", ModuleMemory.getList());
                SwingUtilities.invokeLater(()-> modulesPrompt.setVisible(true));
            }
        });

        modulesBasement = new KPanel();
        modulesBasement.setLayout(new BoxLayout(modulesBasement, BoxLayout.Y_AXIS));
        return new KScrollPane(modulesBasement);
    }

    /**
     * Creates a label suitable for the {@link #modulesBasement}.
     * Such a label has no text, initially, until setText(String) is invoked on it
     * when this type is answering activity.
     * The given runnable is typically assigned the task of  displaying the corresponding
     * dialog provided the current text of the label is not "None" which
     * signifies that it's pointing to an empty list.
     * These labels are attachActiveOnFocus set with the "None" value since they reference
     * lists, and might be empty.
     * @see #attachActiveOnFocus(KLabel, String)
     * @see GlassPrompt
     */
    private KLabel newValueLabel(Runnable r) {
        final KLabel label = new KLabel("", VALUE_FONT, Color.BLUE) {
            @Override
            public void setText(String text) {
                super.setText(text);
                setCursor(text.equals("None") ? null : FOCUS_CURSOR);
            }
        };
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!label.getText().equals("None")) {
                    r.run();
                }
            }
        });
        attachActiveOnFocus(label, "None");
        return label;
    }

    /**
     * Creates a value-like label that points to a single course rather than a list.
     * @see #newValueLabel(Runnable)
     */
    private KLabel newSingletonLabel(Runnable r){
        final KLabel label = new KLabel("", VALUE_FONT, Color.BLUE){
            @Override
            public void setText(String text) {
                super.setText(text);
                if (!text.equals("...")) {
                    setCursor(FOCUS_CURSOR);
                }
            }
        };
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!label.getText().equals("...")) {
                    r.run();
                }
            }
        });
        attachActiveOnFocus(label, "...");
        return label;
    }

    /**
     * Called at answering activity to make sure the analysis is up to date.
     * This lets the labels assume their appropriate texts, and interactivity.
     * Like other complete calls, the basement is emptied before any addition.
     * @see #completeSemestersBasement()
     * @see #completeYearsBasement()
     */
    private void completeModulesBasement(){
        MComponent.clear(modulesBasement);
        if (ModuleMemory.getList().isEmpty()) {
            modulesBasement.addAll(createNoAnalysisPanel());
        } else {
            modulesBasement.addAll(newAnalysisHeader("By Grade"),
                    newAnalysisPlate("A+", APlusLabel),
                    newAnalysisPlate("A", ANeutralLabel),
                    newAnalysisPlate("A-", AMinusLabel),
                    newAnalysisPlate("B+", BPlusLabel),
                    newAnalysisPlate("B", BNeutralLabel),
                    newAnalysisPlate("B-", BMinusLabel),
                    newAnalysisPlate("C+", CPlusLabel),
                    newAnalysisPlate("C", CNeutralLabel),
                    newAnalysisPlate("C-", CMinusLabel),
                    newAnalysisPlate("D", DLabel),
                    newAnalysisPlate("F", FLabel),
                    newAnalysisHeader("By Score"),
                    newAnalysisPlate("Best Score Overall", highestScoreLabel),
                    newAnalysisPlate("Worst Score Overall", lowestScoreLabel),
                    newAnalysisPlate("Best Major Score", highestMajorScoreLabel),
                    newAnalysisPlate("Worst Major Score", lowestMajorScoreLabel),
                    newAnalysisPlate("Best Minor Score", highestMinorScoreLabel),
                    newAnalysisPlate("Worst Minor Score", lowestMinorScoreLabel),
                    newAnalysisPlate("Best DER Score", highestDERScoreLabel),
                    newAnalysisPlate("Worst DER Score", lowestDERScoreLabel),
                    newAnalysisPlate("Best GER Score", highestGERScoreLabel),
                    newAnalysisPlate("Worst GER Score", lowestGERScoreLabel),
                    newAnalysisHeader("By Requirement"),
                    newAnalysisPlate("Majors", majorsLabel),
                    newAnalysisPlate("Minors", minorsLabel),
                    newAnalysisPlate("DERs", DERsLabel),
                    newAnalysisPlate("GERs", GERsLabel),
                    newAnalysisPlate("Unclassified", unclassifiedListLabel),
                    newAnalysisPlate("All together", allModulesLabel));
            final String totalCheck = Globals.checkPlurality(ModuleMemory.getList().size(),"Courses");
            final String majorsCheck = Globals.checkPlurality(majorsList.size(),"Majors");
            final String minorsCheck = Globals.checkPlurality(minorsList.size(), "Minors");
            final String DERsCheck = Globals.checkPlurality(DERList.size(),"DERs");
            final String GERsCheck = Globals.checkPlurality(GERList.size(), "GERs");
            final String unknownsCheck = Globals.checkPlurality(unclassifiedList.size(),"Un-classifications");
            final StringJoiner joiner = new StringJoiner(" : ","[", "]");
            joiner.add(majorsCheck).add(minorsCheck).add(DERsCheck).add(GERsCheck).add(unknownsCheck);
            allModulesLabel.setText(totalCheck+" "+joiner);
        }
        MComponent.ready(modulesBasement);
    }

    /**
     * Initializes the {@link #semestersBasement} and returns a container
     * onto which it is placed.
     */
    private Component getSemestersBasement(){
        semestersBasement = new KPanel();
        semestersBasement.setLayout(new BoxLayout(semestersBasement, BoxLayout.Y_AXIS));
        return new KScrollPane(semestersBasement);
    }

    /**
     * Completes setting up the {@link #semestersBasement}.
     * @see #completeModulesBasement()
     */
    private void completeSemestersBasement(){
        semesterScores = new ArrayList<>();
        MComponent.clear(semestersBasement);
        if (semestersList.isEmpty()) {
            semestersBasement.addAll(createNoAnalysisPanel());
        } else {
            for (String semTex : semestersList) {
                final ArrayList<Course> fractionalSem = ModuleMemory.getFractionBySemester(semTex);
                final KLabel promptLabel = new KLabel("", VALUE_FONT, Color.BLUE);
                promptLabel.setCursor(FOCUS_CURSOR);
                promptLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        final GlassPrompt glassPrompt = new GlassPrompt(semTex, fractionalSem);
                        SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
                    }
                });
                if (fractionalSem.size() == 1) {
                    promptLabel.setText(fractionalSem.get(0).getAbsoluteName());
                } else {
                    final int i = fractionalSem.size();
                    final StringJoiner joiner = new StringJoiner(" : ", "[", "]");
                    joiner.add(Globals.checkPlurality(ModuleMemory.getMajorsBySemester(semTex).size(),"Majors"));
                    joiner.add(Globals.checkPlurality(ModuleMemory.getMinorsBySemester(semTex).size(),"Minors"));
                    joiner.add(Globals.checkPlurality(ModuleMemory.getDERsBySemester(semTex).size(),"DERs"));
                    joiner.add(Globals.checkPlurality(ModuleMemory.getGERsBySemester(semTex).size(),"GERs"));
                    joiner.add(Globals.checkPlurality(ModuleMemory.getUnknownsBySemester(semTex).size(),"Un-classifications"));
                    promptLabel.setText(Globals.checkPlurality(i, "Courses")+" "+joiner);
                }
                attachActiveOnFocus(promptLabel, null);

                final KLabel CGLabel = new KLabel(toFourth(ModuleMemory.getCGPABySemester(semTex)),
                        VALUE_FONT, Color.BLUE);
                semesterScores.add(Double.valueOf(CGLabel.getText()));

                semestersBasement.addAll(newAnalysisHeader(semTex), newAnalysisPlate("Courses Registered",
                        promptLabel), newAnalysisPlate("CGPA Earned", CGLabel));
            }

            final KLabel totalLabel = new KLabel(Globals.checkPlurality(semestersList.size(),"Semesters"),
                    VALUE_FONT, Color.BLUE);
            totalLabel.underline(true);
            semestersBasement.addAll(newAnalysisHeader("Overall"),
                    newAnalysisPlate("All together", totalLabel),
                    newAnalysisPlate("Best Semester", new KLabel(ModuleMemory.getBestSemester(),
                            VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Worst Semester", new KLabel(ModuleMemory.getWorstSemester(),
                            VALUE_FONT, Color.BLUE)),
                    newAnalysisHeader("Performance Sketch"), new Sketch());
        }
        MComponent.ready(semestersBasement);
    }

    /**
     * Returns the first 6-digits of the given double value,
     * for convenience, as a String.
     * This is intended for Dashboard computed CGPAs only.
     */
    public static String toFourth(double d){
        final String t = String.valueOf(d);
        return t.length() <= 6 ? t : String.valueOf(Globals.round(d, 4));
    }

    private JComponent getYearsBasement(){
        yearsBasement = new KPanel();
        yearsBasement.setLayout(new BoxLayout(yearsBasement, BoxLayout.Y_AXIS));
        return new KScrollPane(yearsBasement);
    }

    private void completeYearsBasement(){
        MComponent.clear(yearsBasement);
        if (yearsList.isEmpty()) {
            yearsBasement.addAll(createNoAnalysisPanel());
        } else {
            for (String yearTex : yearsList) {
                final ArrayList<Course> fractionalYear = ModuleMemory.getFractionByYear(yearTex);
                final KLabel allRegisteredLabel = new KLabel("", VALUE_FONT, Color.BLUE);
                allRegisteredLabel.setCursor(FOCUS_CURSOR);
                allRegisteredLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SwingUtilities.invokeLater(()-> new GlassPrompt(yearTex, fractionalYear).setVisible(true));
                    }
                });
                final ArrayList<Course> yMajors = ModuleMemory.getMajorsByYear(yearTex);
                final ArrayList<Course> yMinors = ModuleMemory.getMinorsByYear(yearTex);
                final ArrayList<Course> yDERs = ModuleMemory.getDERsByYear(yearTex);
                final ArrayList<Course> yGERs = ModuleMemory.getGERsByYear(yearTex);
                final ArrayList<Course> yNons = ModuleMemory.getUnknownsByYear(yearTex);
                if (fractionalYear.size() == 1) {
                    final Course onlyCourse = fractionalYear.get(0);
                    allRegisteredLabel.setText(onlyCourse.getAbsoluteName()+" ["+onlyCourse.getSemester()+"]");
                } else {
                    final int i = fractionalYear.size();
                    final StringJoiner joiner = new StringJoiner(" : ", "[", "]");
                    joiner.add(Globals.checkPlurality(yMajors.size(), "Majors"));
                    joiner.add(Globals.checkPlurality(yMinors.size(), "Minors"));
                    joiner.add(Globals.checkPlurality(yDERs.size(), "DERs"));
                    joiner.add(Globals.checkPlurality(yGERs.size(), "GERs"));
                    joiner.add(Globals.checkPlurality(yNons.size(), "Un-classifications"));
                    allRegisteredLabel.setText(Globals.checkPlurality(i, "Courses")+" "+joiner);
                }
                attachActiveOnFocus(allRegisteredLabel, null);

                final ArrayList<String> yLects = ModuleMemory.getLecturersByYear(yearTex);
                final KLabel tutorsLabel = new KLabel(Globals.checkPlurality(yLects.size(),"Lecturers"),
                        VALUE_FONT, Color.BLUE);
                tutorsLabel.setCursor(tutorsLabel.getText().equals("No Lecturers") ? null : FOCUS_CURSOR);
                attachActiveOnFocus(tutorsLabel, "No Lecturers");
                tutorsLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!tutorsLabel.getText().equals("No Lecturers")) {
                            final GlassPrompt glassPrompt = new GlassPrompt(yLects, yearTex);
                            SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
                        }
                    }
                });
                yearsBasement.addAll(newAnalysisHeader(yearTex),
                        newAnalysisPlate("Courses Registered", allRegisteredLabel),
                        newAnalysisPlate("Majors", specificYearLabel("Majors", yMajors, yearTex)),
                        newAnalysisPlate("Minors", specificYearLabel("Minors", yMinors, yearTex)),
                        newAnalysisPlate("DERs", specificYearLabel("DERs", yDERs, yearTex)),
                        newAnalysisPlate("GERs", specificYearLabel("GERs", yGERs, yearTex)),
                        newAnalysisPlate("Unclassified", specificYearLabel("Unknowns", yNons, yearTex)),
                        newAnalysisPlate("Lecturers", tutorsLabel),
                        newAnalysisPlate("CGPA Earned", new KLabel(toFourth(ModuleMemory.getCGPAByYear(yearTex)),
                                VALUE_FONT, Color.BLUE)));
            }

            final ArrayList<String> tutorsList = ModuleMemory.getLecturers();
            final KLabel totalTutorsLabel = new KLabel(Globals.checkPlurality(tutorsList.size(),
                    "distinguished lecturers"), VALUE_FONT, Color.BLUE);
            totalTutorsLabel.setCursor(totalTutorsLabel.getText().equals("No distinguished lecturers") ? null :
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            attachActiveOnFocus(totalTutorsLabel, "No distinguished lecturers");
            totalTutorsLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!totalTutorsLabel.getText().equals("No distinguished lecturers")) {
                        SwingUtilities.invokeLater(()-> new GlassPrompt(tutorsList).setVisible(true));
                    }
                }
            });

            final KLabel totalLabel = new KLabel(Globals.checkPlurality(yearsList.size(),"Academic years"),
                    VALUE_FONT, Color.BLUE);
            totalLabel.underline(true);
            yearsBasement.addAll(newAnalysisHeader("Overall"),
                    newAnalysisPlate("All together", totalLabel),
                    newAnalysisPlate("My Tutors", totalTutorsLabel),
                    newAnalysisPlate("Best Year", new KLabel(ModuleMemory.getBestYear(),
                            VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Worst Year", new KLabel(ModuleMemory.getWorstYear(),
                            VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Current CGPA", new KLabel(Student.getCGPA() +
                            "    ["+Student.upperClassDivision()+"]", VALUE_FONT, Color.BLUE)));
        }
        MComponent.ready(yearsBasement);
    }

    /**
     * Returns a value-like label that is supposed to be pre-hinted.
     * This label points to a specific list of courses in a particular year;
     * E.g majors, minors, etc.; the given yearName specifies such a year.
     * The label will assume the given text based on what the {@link #getProperValueText(ArrayList)}
     * returns with the given list as the parameter.
     * The label will remove the yearName from its text.
     * Notice the dialogue prompted by this label reset its title as specified herein.
     * @see #getProperValueText(ArrayList)
     */
    private KLabel specificYearLabel(String text, ArrayList<Course> list, String yearName) {
        final KLabel kLabel = new KLabel(getProperValueText(list), VALUE_FONT, Color.BLUE);
        kLabel.setText(kLabel.getText().replace(yearName+" ", ""));
        kLabel.setCursor(kLabel.getText().equals("None") ? null : FOCUS_CURSOR);
        kLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!kLabel.getText().equals("None")) {
                    final GlassPrompt glassPrompt = new GlassPrompt("", list);
                    glassPrompt.setTitle(yearName+" ["+text+": "+list.size()+"]");
                    SwingUtilities.invokeLater(()-> glassPrompt.setVisible(true));
                }
            }
        });
        attachActiveOnFocus(kLabel, "None");
        return kLabel;
    }

    /**
     * Returns a Dashboard specific text to describe
     * the number of elements in the given list.
     * Returns "None" if list is empty...
     */
    private String getProperValueText(ArrayList<Course> list){
        if (list.isEmpty()) {
            return "None";
        } else if (list.size() == 1) {
            return list.get(0).getName()+" ["+list.get(0).getAbsoluteSemesterName()+"]";
        } else if (list.size() == 2) {
            return list.get(0).getName() + " & " + list.get(1).getName();
        } else if (list.size() == 3) {
            return list.get(0).getName() + ", and 2 others...";
        } else {
            return list.get(0).getName()+", "+list.get(1).getName()+", and "+(list.size() - 2)+" others...";
        }
    }

    /**
     * Returns a Dashboard specific text to describe
     * the situation of the module the target-label is pointing.
     * Returns "..." if there's no such course...
     */
    private String getProperValueText(Course course){
        return course == null ? "..." : course.getName()+" ["+course.getAbsoluteSemesterName()+"] : "+course.getScore()+"%";
    }

    private KPanel newAnalysisHeader(String headerText){
        return new KPanel(new KLabel(headerText, FontFactory.createBoldFont(18), Color.RED));
    }

    /**
     * Returns a panel for placing horizontal hint-value pair analysis data.
     * A label, added to the left, is created with the given hint as its text
     * and assumes the {@link #HINT_FONT}.
     * The given label is placed next to it, and is usually no ordinary label.
     */
    private KPanel newAnalysisPlate(String hint, KLabel label){
        final KPanel panel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        panel.addAll(new KPanel(new KLabel(hint, HINT_FONT)), Box.createHorizontalStrut(20), label);
        return panel;
    }

    /**
     * Adds mouse-enter event to this label such that it responds with the
     * {@link #FOCUS_FONT} unless its current text is the given exception.
     */
    private void attachActiveOnFocus(KLabel label, String except) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!label.getText().equals(except)) {
                    label.setFont(FOCUS_FONT);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setFont(VALUE_FONT);
            }
        });
    }

    /**
     * Invoked during answering activity to
     * reset the lists in order to ensure atomicity in data,
     * and points to the latest {@link ModuleMemory} updates.
     */
    private void resetLists(){
        APlusList =  ModuleMemory.getFractionByGrade("A+");
        ANeutralList = ModuleMemory.getFractionByGrade("A");
        AMinusList = ModuleMemory.getFractionByGrade("A-");
        BPlusList = ModuleMemory.getFractionByGrade("B+");
        BNeutralList = ModuleMemory.getFractionByGrade("B");
        BMinusList = ModuleMemory.getFractionByGrade("B-");
        CPlusList = ModuleMemory.getFractionByGrade("C+");
        CNeutralList = ModuleMemory.getFractionByGrade("C");
        CMinusList = ModuleMemory.getFractionByGrade("C-");
        DList = ModuleMemory.getFractionByGrade("D");
        FList = ModuleMemory.getFractionByGrade("F");

        majorsList = ModuleMemory.getMajors();
        minorsList = ModuleMemory.getMinors();
        DERList = ModuleMemory.getDERs();
        GERList = ModuleMemory.getGERs();
        unclassifiedList = ModuleMemory.getUnknowns();

        semestersList = ModuleMemory.getSemesters();
        yearsList = ModuleMemory.getAcademicYears();
    }

    /**
     * Resets the course fields of this class to be pointing
     * to the latest {@link ModuleMemory} values.
     * Call this on answering activity.
     */
    private void resetCourses(){
        highestScoreCourse = ModuleMemory.getHighestScore();
        lowestScoreCourse = ModuleMemory.getLowestScore();
        highestMajorScoreCourse = ModuleMemory.getHighestMajorScore();
        lowestMajorScoreCourse = ModuleMemory.getLowestMajorScore();
        highestMinorScoreCourse = ModuleMemory.getHighestMinorScore();
        lowestMinorScoreCourse = ModuleMemory.getLowestMinorScore();
        highestDERScoreCourse = ModuleMemory.getHighestDERScore();
        lowestDERScoreCourse = ModuleMemory.getLowestDERScore();
        highestGERScoreCourse = ModuleMemory.getHighestGERScore();
        lowestGERScoreCourse = ModuleMemory.getLowestGERScore();
    }

    /**
     * Resets the label fields to their latest values.
     * Call this on answering activity.
     * @see ModuleMemory
     */
    private void resetLabels(){
        APlusLabel.setText(getProperValueText(APlusList));
        ANeutralLabel.setText(getProperValueText(ANeutralList));
        AMinusLabel.setText(getProperValueText(AMinusList));
        BPlusLabel.setText(getProperValueText(BPlusList));
        BNeutralLabel.setText(getProperValueText(BNeutralList));
        BMinusLabel.setText(getProperValueText(BMinusList));
        CPlusLabel.setText(getProperValueText(CPlusList));
        CNeutralLabel.setText(getProperValueText(CNeutralList));
        CMinusLabel.setText(getProperValueText(CMinusList));
        DLabel.setText(getProperValueText(DList));
        FLabel.setText(getProperValueText(FList));

        highestScoreLabel.setText(getProperValueText(highestScoreCourse));
        lowestScoreLabel.setText(getProperValueText(lowestScoreCourse));
        highestMajorScoreLabel.setText(getProperValueText(highestMajorScoreCourse));
        lowestMajorScoreLabel.setText(getProperValueText(lowestMajorScoreCourse));
        highestMinorScoreLabel.setText(getProperValueText(highestMinorScoreCourse));
        lowestMinorScoreLabel.setText(getProperValueText(lowestMinorScoreCourse));
        highestDERScoreLabel.setText(getProperValueText(highestDERScoreCourse));
        lowestDERScoreLabel.setText(getProperValueText(lowestDERScoreCourse));
        highestGERScoreLabel.setText(getProperValueText(highestGERScoreCourse));
        lowestGERScoreLabel.setText(getProperValueText(lowestGERScoreCourse));

        majorsLabel.setText(getProperValueText(majorsList));
        minorsLabel.setText(getProperValueText(minorsList));
        DERsLabel.setText(getProperValueText(DERList));
        GERsLabel.setText(getProperValueText(GERList));
        unclassifiedListLabel.setText(getProperValueText(unclassifiedList));
    }

    /**
     * Created and added in place of the analysis-basements
     * to signify that analysis is not available.
     * Notice only verified courses are included in the analysis.
     * This may include an icon in a future release.
     */
    private KPanel createNoAnalysisPanel() {
        final KLabel label1 = new KLabel("Analysis unavailable", FontFactory.createBoldFont(30));
        final KLabel label2 = new KLabel("No verified courses detected for analysis",
                FontFactory.createPlainFont(20), Color.DARK_GRAY);

        final KPanel innerPanel = new KPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.addAll(new KPanel(label1), new KPanel(label2));

        final KPanel outerPanel = new KPanel(new BorderLayout());
        outerPanel.add(Box.createVerticalStrut(100), BorderLayout.NORTH);
        outerPanel.add(innerPanel, BorderLayout.CENTER);
        outerPanel.add(Box.createVerticalStrut(150), BorderLayout.SOUTH);
        return outerPanel;
    }


    /**
     * The GlassPrompt inner-type is responsible for prompting up
     * the contents of an analysis-list.
     * Series of constructors, some specific, some generic, but
     * most all receive a list to be prompted with.
     */
    private static class GlassPrompt extends KDialog {
        public KPanel substancePanel;

        /**
         * Constructs a {@link GlassPrompt} of courses with the given title
         * on this root component [or Board.getRoot() if null].
         * The title is initialized herein, but some callers do need to change it.
         */
        private GlassPrompt(String title, ArrayList<Course> courses, Component root) {
            super(title);
            final int listSize = courses.size();
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);
            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            courses.sort(Comparator.comparing(Course::getName));
            for (int i = 0; i < listSize; i++) {
                join(i + 1, courses.get(i));
            }
            final KScrollPane kScrollPane = new KScrollPane(substancePanel);
            if (listSize > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width + 25,525));
            }
            setContentPane(new KPanel(new BorderLayout(), kScrollPane));
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(root == null ? Board.getRoot() : root);
        }

        /**
         * Constructs a {@link GlassPrompt} of courses with the given label
         * pre-attached to the description to produce the title.
         * This will be placed on the Dashboard's instance.
         * @see #GlassPrompt(String, ArrayList, Component)
         */
        private GlassPrompt(String label, ArrayList<Course> courses){
            this(label+" ["+ Globals.checkPlurality(courses.size(), "Courses")+"]",
                    courses, Board.getRoot());
        }

        /**
         * Constructs a {@link GlassPrompt} with the given title and list.
         * This is exclusive to lecturer names of a specific academic year.
         */
        private GlassPrompt(ArrayList<String> tutors, String year) {
            super(year+" ["+Globals.checkPlurality(tutors.size(), "Lecturers")+"]");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);
            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            Collections.sort(tutors);
            for (int i = 0; i < tutors.size(); i++) {
                join(i + 1, tutors.get(i), year);
            }
            final KScrollPane kScrollPane = new KScrollPane(substancePanel);
            if (tutors.size() > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width + 25, 525));
            }
            setContentPane(new KPanel(new BorderLayout(), kScrollPane));
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
        }

        /**
         * Constructs a {@link GlassPrompt} with the given title and list.
         * This is for all the lecturer names.
         */
        private GlassPrompt(ArrayList<String> lecturers) {
            super("My Tutors ["+lecturers.size()+"]");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);
            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            Collections.sort(lecturers);
            for (int i = 0; i < lecturers.size(); i++) {
                join(i + 1, lecturers.get(i));
            }
            final KScrollPane kScrollPane = new KScrollPane(substancePanel);
            if (lecturers.size() > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width + 25, 525));
            }
            setContentPane(new KPanel(new BorderLayout(), kScrollPane));
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
        }

        /**
         * Adds the given course to the substancePanel.
         */
        public void join(int row, Course c) {
            final KLabel numericLabel = new KLabel(row+".", FontFactory.createPlainFont(15));

            final KLabel nameLabel = new KLabel(c.getName(), FontFactory.createPlainFont(15));
            nameLabel.underline(false);
            nameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    c.exhibit(getRootPane());
                }
            });
            nameLabel.setCursor(MComponent.HAND_CURSOR);

            final KPanel linearPanel = new KPanel(new FlowLayout(FlowLayout.LEFT, 10, 5),
                    numericLabel, nameLabel);
            substancePanel.add(linearPanel);
        }

        /**
         * Adds the given tutor-name to the substancePanel.
         * This is year-bound as specified by the year-name.
         */
        private void join(int row, String tutorName, String year) {
            final KLabel numericLabel = new KLabel(row+".", FontFactory.createPlainFont(15));

            final KLabel nameLabel = new KLabel(tutorName, FontFactory.createPlainFont(15));
            nameLabel.underline(false);
            nameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    final ArrayList<Course> list = ModuleMemory.getFractionByLecturer(tutorName,year);
                    final String title = tutorName+" ["+ year +": "+list.size()+"]";
                    final GlassPrompt prompt = new GlassPrompt(title, list, getRootPane());
                    prompt.setVisible(true);
                }
            });
            nameLabel.setCursor(MComponent.HAND_CURSOR);

            final KPanel linearPanel = new KPanel(new FlowLayout(FlowLayout.LEFT, 10, 5),
                    numericLabel, nameLabel);
            substancePanel.add(linearPanel);
        }

        /**
         * Adds the given lecturer-name to the substancePanel.
         */
        private void join(int row, String tutorName) {
            final KLabel numericLabel = new KLabel(row+".", FontFactory.createPlainFont(15));

            final KLabel nameLabel = new KLabel(tutorName, FontFactory.createPlainFont(15));
            nameLabel.underline(false);
            nameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    final ArrayList<Course> list = ModuleMemory.getFractionByLecturer(tutorName);
                    final String title = tutorName+" ["+ Globals.checkPlurality(list.size(),"Courses")+"]";
                    new GlassPrompt(title, list, getRootPane()).setVisible(true);
                }
            });
            nameLabel.setCursor(MComponent.HAND_CURSOR);

            final KPanel linearPanel = new KPanel(new FlowLayout(FlowLayout.LEFT, 10, 5),
                    numericLabel, nameLabel);
            substancePanel.add(linearPanel);
        }
    }


    /**
     * For sketching the semester to semester performance.
     */
    private static class Sketch extends KPanel {
        private static final int PADDING = 50;
        private static final int POINTS_WIDTH = 10;
        private static final int Y_COUNT = 20;
        private static final double Y_MAX = 4.3;

        private Sketch(){
            setPreferredSize(new Dimension(900, 430));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(FontFactory.createPlainFont(12));

            final double xScale = ((double) (getWidth() - (2 * PADDING)) / (semesterScores.size() - 1));
            final double yScale = ((double) (getHeight() - (2 * PADDING)) / (Y_MAX - 1));

            final ArrayList<Point> points = new ArrayList<>();
            for (int i = 0; i < semesterScores.size(); i++) {
                int x1 = (int) (i * xScale + PADDING);
                int y1 = (int) ((Y_MAX - semesterScores.get(i)) * yScale + PADDING);
                points.add(new Point(x1, y1));
            }

            g2.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING);
            g2.drawLine(PADDING, getHeight() - PADDING, PADDING, PADDING);

            for (int i = 0; i < semesterScores.size(); i++) {
                int x0 = i * (getWidth() - (PADDING * 2)) / (semesterScores.size() - 1) + PADDING;
                int x1 = x0;
                int y0 = this.getHeight() - PADDING;
                int y1 = y0 - POINTS_WIDTH;
                g2.drawLine(x0, y0, x1, y1);

                final String[] nameParts = semestersList.get(i).split(" ");
                g2.drawString(nameParts[0], x0 - 35, y0 + 20);
                g2.drawString(nameParts[1] + " " + nameParts[2], x0 - 50, y0 + 35);
            }

            for (int i = 0; i < Y_COUNT; i++) {
                int x0 = PADDING;
                int x1 = POINTS_WIDTH + PADDING;
                int y0 = getHeight() - (((i + 1) * (getHeight() - PADDING * 2)) / Y_COUNT + PADDING);
                int y1 = y0;
                g2.drawLine(x0, y0, x1, y1);

                if (i == Y_COUNT - 1) {
                    g2.setFont(FontFactory.createBoldFont(14));
                    g2.drawString(String.valueOf(Y_MAX),25,y0 + 5);
                }
            }

            g2.setFont(FontFactory.createPlainFont(12));
            for (int i = 0; i < points.size() - 1; i++) {
                int x1 = points.get(i).x;
                int y1 = points.get(i).y;
                int x2 = points.get(i + 1).x;
                int y2 = points.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.setColor(Color.BLUE);
            g2.setFont(FontFactory.createPlainFont(14));
            for (int i = 0; i < points.size(); i++) {
                int x = points.get(i).x - POINTS_WIDTH / 2;
                int y = points.get(i).y - POINTS_WIDTH / 2;
                int ovalW = POINTS_WIDTH;
                int ovalH = POINTS_WIDTH;
                g2.fillOval(x, y, ovalW, ovalH);
                g2.drawString(String.valueOf(semesterScores.get(i)),x - 10,y - 10);
            }
        }
    }

}

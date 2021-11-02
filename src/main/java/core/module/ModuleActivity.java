package core.module;

import core.Activity;
import core.Board;
import core.user.Student;
import core.utils.App;
import core.utils.MComponent;
import core.utils.FontFactory;
import proto.*;

import javax.swing.*;
import java.awt.*;

/**
 * An activity end for the courses.
 * ModuleActivity is for courses as SemesterActivity is for RegisteredCourses.
 */
public class ModuleActivity implements Activity {
    private KButton onButton;
    private KLabel indicator;
    private CardLayout residentLayout;
    private KButton refreshButton;


    public ModuleActivity() {
        final KPanel modulesActivity = new KPanel(new BorderLayout());
        if (Student.isGuest()) {
            modulesActivity.add(MComponent.createUnavailableActivity("Modules"));
        } else {
            indicator = new KLabel("First Year: "+Student.firstAcademicYear(), FontFactory.BODY_HEAD_FONT);

            refreshButton = new KButton("Sync");
            refreshButton.setFont(FontFactory.createPlainFont(15));
            refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            refreshButton.setToolTipText("Synchronize Courses");
            refreshButton.addActionListener(e-> ModuleHandler.launchThoroughSync(true, refreshButton));

            final KPanel headerPanel = new KPanel(new BorderLayout());
            headerPanel.add(new KPanel(indicator), BorderLayout.WEST);
            headerPanel.add(new KPanel(refreshButton), BorderLayout.EAST);

            final SummerHandler summerHandler = new SummerHandler();
            final MiscHandler miscHandler = new MiscHandler();
            final ModuleHandler handler = new ModuleHandler();

            residentLayout = new CardLayout();
            final KPanel residentPanel = new KPanel(residentLayout) {
                @Override
                public Component add(Component comp) {
                    final KScrollPane scrollPane = new KScrollPane(comp);
                    scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
                    return super.add(scrollPane);
                }
            };
            final KButton y1Button = getControlButton("Year One");
            y1Button.redress();
            onButton = y1Button;
            y1Button.addActionListener(e-> {
                setOnButton(y1Button);
                indicator.setText("First Year: "+Student.firstAcademicYear());
                residentLayout.show(residentPanel,"Year1");
            });

            final KButton y2Button = getControlButton("Year Two");
            y2Button.addActionListener(e-> {
                setOnButton(y2Button);
                indicator.setText("Second Year: " + Student.secondAcademicYear());
                residentLayout.show(residentPanel, "Year2");
            });

            final KButton y3Button = getControlButton("Year Three");
            y3Button.addActionListener(e-> {
                setOnButton(y3Button);
                indicator.setText("Third Year: " + Student.thirdAcademicYear());
                residentLayout.show(residentPanel, "Year3");
            });

            final KButton y4Button = getControlButton("Year Four");
            y4Button.addActionListener(e-> {
                setOnButton(y4Button);
                indicator.setText("Final Year: " + Student.fourthAcademicYear());
                residentLayout.show(residentPanel, "Year4");
            });

            final KButton summerButton = getControlButton("Summer");
            summerButton.addActionListener(e-> {
                setOnButton(summerButton);
                indicator.setText("Summer");
                residentLayout.show(residentPanel,"Summer");
            });

            final KButton miscButton = getControlButton("Misc.");
            miscButton.addActionListener(e-> {
                setOnButton(miscButton);
                indicator.setText("Miscellaneous");
                residentLayout.show(residentPanel,"misc");
            });

            final KPanel controlPanel = new KPanel(150, 400);
            controlPanel.add(Box.createRigidArea(new Dimension(150, 50)));
            controlPanel.addAll(y1Button, y2Button, y3Button, y4Button, new KSeparator(new Dimension(125,1)),
                    summerButton, miscButton);

            residentLayout.addLayoutComponent(residentPanel.add(handler.yearOnePresent()),"Year1");
            residentLayout.addLayoutComponent(residentPanel.add(handler.yearTwoPresent()),"Year2");
            residentLayout.addLayoutComponent(residentPanel.add(handler.yearThreePresent()),"Year3");
            residentLayout.addLayoutComponent(residentPanel.add(handler.yearFourPresent()),"Year4");
            residentLayout.addLayoutComponent(residentPanel.add(summerHandler.getPresent()),"Summer");
            residentLayout.addLayoutComponent(residentPanel.add(miscHandler.getPresent()),"misc");

            modulesActivity.add(headerPanel, BorderLayout.NORTH);
            modulesActivity.add(controlPanel, BorderLayout.WEST);
            modulesActivity.add(residentPanel, BorderLayout.CENTER);
        }

        Board.addCard(modulesActivity, "Modules Collection");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Modules Collection");
    }

    private KButton getControlButton(String text) {
        final KButton button = new KButton(text);
        button.setStyle(FontFactory.createPlainFont(15), Color.BLUE);
        button.setPreferredSize(new Dimension(150,30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.undress();
        return button;
    }

    private void setOnButton(KButton button){
        if (onButton != button) {
            button.redress();
            onButton.undress();
            onButton = button;
        }
    }

}


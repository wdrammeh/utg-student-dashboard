package core.transcript;

import com.itextpdf.text.DocumentException;
import core.Activity;
import core.Board;
import core.module.Course;
import core.module.ModuleHandler;
import core.user.Student;
import core.utils.App;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * Dashboard does not make any effort(s) to calculate the CGPA.
 * The cgpa, the upperDivision, as well as other generated resources of this type
 * should be renewed anytime it answer activity.
 * And the cgpa is always set before the upperDivision is re-checked!
 * Feel free of what this class presents, because what's printed is independent of it.
 */
public class TranscriptActivity implements Activity {
    /**
     * Holds the logo, labels, as well as the details.
     */
    private static KPanel detailPanel;
    private static KTable table;
    private static KLabel CGPALabel;
    private static KLabel minorLabel;
    private static KLabel classificationLabel;
    public static final String[] HEADS = {"COURSE CODE", "COURSE DESCRIPTION", "CREDIT VALUE", "GRADE", "QUALITY POINT"};
    public static final KTableModel TRANSCRIPT_MODEL = new KTableModel(HEADS);


    public TranscriptActivity(){
        final KPanel activityPanel = new KPanel(new BorderLayout());
        if (Student.isTrial()) {
            activityPanel.add(MComponent.createUnavailableActivity("Transcript"), BorderLayout.CENTER);
        } else {
            minorLabel = new KLabel("", KFontFactory.createBoldFont(15));
            CGPALabel = new KLabel("", KFontFactory.createBoldFont(16));
            classificationLabel = new KLabel("", KFontFactory.createPlainFont(15));

            buildDetailsPanelPlus();

            TRANSCRIPT_MODEL.setColumnIdentifiers(HEADS);

            table = new KTable(TRANSCRIPT_MODEL);
            table.setRowHeight(30);
            table.setFont(KFontFactory.createBoldFont(15));
            table.getColumnModel().getColumn(1).setPreferredWidth(350);
            table.getColumnModel().getColumn(3).setPreferredWidth(40);
            table.getTableHeader().setFont(KFontFactory.createBoldFont(16));
            table.getTableHeader().setPreferredSize(new Dimension(table.getPreferredSize().width,35));
            table.centerAlignColumns(2, 3, 4);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        final String code = String.valueOf(TRANSCRIPT_MODEL.getValueAt(table.getSelectedRow(), 0));
                        final Course c = ModuleHandler.getModuleByCode(code);
                        if (c != null) {
                            c.exhibit();
                        }
                    }
                }
            });

            final KPanel midLiner = new KPanel();
            midLiner.setLayout(new BoxLayout(midLiner, BoxLayout.Y_AXIS));
            midLiner.setBorder(BorderFactory.createEmptyBorder(5,5,15,5));
            midLiner.addAll(detailPanel, Box.createVerticalStrut(20),
                    table.sizeMatchingScrollPane(), Box.createVerticalStrut(10), getPointPanel(),
                    Box.createVerticalStrut(10), new KPanel(classificationLabel));

            activityPanel.add(topLayer(), BorderLayout.NORTH);
            activityPanel.add(new KScrollPane(midLiner), BorderLayout.CENTER);
        }

        Board.addCard(activityPanel, "Transcript");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Transcript");
        if (!Student.isTrial()) {
            minorLabel.setText(Student.isDoingMinor() ? Student.getMinor().toUpperCase() : "NONE");
            CGPALabel.setText(Double.toString(Student.getCGPA()));
            classificationLabel.setText(Student.upperClassDivision());
            if (classificationLabel.getText().equals("None")) {
                classificationLabel.getParent().setVisible(false);
            }
        }
    }

//    all the components needed at the top. may be a label to the left, buttons to the right
    private static KPanel topLayer() {
        final KCheckBox detailsCheck = new KCheckBox("Show Details", false);
        detailsCheck.setFont(KFontFactory.createPlainFont(15));
        detailsCheck.setFocusable(false);
        detailsCheck.addItemListener(itemEvent -> detailPanel.setVisible(itemEvent.getStateChange() == ItemEvent.SELECTED));

        final KLabel exportLabel = new KLabel("Export", KFontFactory.createBoldFont(16), Color.BLUE);
        exportLabel.underline(false);
        exportLabel.setCursor(MComponent.HAND_CURSOR);
        exportLabel.setToolTipText("Export Transcript");
        exportLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    new TranscriptExporter().exportNow();
                } catch (IOException | DocumentException ex) {
                    App.reportError(ex);
                }
            }
        });

        final KPanel topPanel = new KPanel(new BorderLayout());
        topPanel.add(new KPanel(new KLabel("My Transcript", KFontFactory.BODY_HEAD_FONT)), BorderLayout.WEST);
        topPanel.add(new KPanel(new FlowLayout(FlowLayout.CENTER, 20, 5), detailsCheck, exportLabel),
                BorderLayout.EAST);
        return topPanel;
    }

    private static void buildDetailsPanelPlus(){
        final KLabel l1 = new KLabel("THE UNIVERSITY OF THE GAMBIA", KFontFactory.createBoldFont(25));
        final KLabel l2 = new KLabel("STUDENT ACADEMIC RECORDS", l1.getFont());

        final KPanel l1l2Panel = new KPanel(new Dimension(500, 100), l1, l2);

        final KPanel labelsPanel = new KPanel(KLabel.createIcon("UTGLogo.gif", 125,  125),
                Box.createRigidArea(new Dimension(50, 100)), l1l2Panel);

        detailPanel = new KPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.addAll(labelsPanel, Box.createVerticalStrut(30), getDetailPanels());
        detailPanel.setVisible(false);
    }

//    returns a panel containing all the details in rectangles
    private static KPanel getDetailPanels(){
        final KPanel leftSide = new KPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.add(newRectangularPanel(150,30,"STUDENT NAME", Student.getFullNamePostOrder()));
        leftSide.add(Box.createVerticalStrut(10));
        leftSide.add(newRectangularPanel(150,60,"YEAR","ENROLLED",
                Integer.toString(Student.getYearOfAdmission())));
        leftSide.add(Box.createVerticalStrut(10));
        leftSide.add(newRectangularPanel(150,30,"MINOR", ""));

        final KPanel rightSide = new KPanel();
        rightSide.setLayout(new BoxLayout(rightSide,BoxLayout.Y_AXIS));
        rightSide.add(newRectangularPanel(165,30,"STUDENT NUMBER", Student.getMatNumber()));
        rightSide.add(Box.createVerticalStrut(10));
        rightSide.add(newRectangularPanel(165,60,"YEAR EXPECTED", "TO GRADUATE",
                Student.getExpectedYearOfGraduation()+""));
        rightSide.add(Box.createVerticalStrut(10));
        rightSide.add(newRectangularPanel(165,30,"MAJOR", Student.getMajor()));

        final KPanel returningPanel = new KPanel(new BorderLayout());
        returningPanel.add(leftSide, BorderLayout.WEST);
        returningPanel.add(rightSide, BorderLayout.EAST);
        return returningPanel;
    }

    private static KPanel newRectangularPanel(int leftMostWidth, int leftMostHeight, String... parts){
        final Font hereFont = KFontFactory.createBoldFont(15);

        final KPanel leftMost = new KPanel(leftMostWidth, leftMostHeight);
        final KPanel rightMost = new KPanel(275, leftMostHeight);

        if (parts.length == 2) {
            leftMost.add(new KLabel(parts[0], hereFont));
            rightMost.add(Objects.equals(parts[0], "MINOR") ? minorLabel : new KLabel(parts[1].toUpperCase(), hereFont));
        } else if (parts.length == 3) {
            leftMost.setLayout(new BoxLayout(leftMost, BoxLayout.Y_AXIS));
            leftMost.add(new KPanel(new KLabel(parts[0], hereFont)));
            leftMost.add(new KPanel(new KLabel(parts[1].toUpperCase(), hereFont)));

            rightMost.setLayout(new BoxLayout(rightMost, BoxLayout.X_AXIS));
            rightMost.addAll(new KPanel(), new KLabel(parts[2].toUpperCase(), hereFont), new KPanel());
        }

        final KPanel whole = new KPanel(new BorderLayout());
        whole.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        whole.add(leftMost, BorderLayout.WEST);
        whole.add(new KSeparator(KSeparator.VERTICAL), BorderLayout.CENTER);
        whole.add(rightMost, BorderLayout.EAST);
        return whole;
    }

    private static KPanel getPointPanel(){
        final KPanel leftHand = new KPanel();
        leftHand.setLayout(new BoxLayout(leftHand, BoxLayout.Y_AXIS));
        leftHand.addAll(new KPanel(new KLabel("AVERAGE", KFontFactory.createBoldFont(15))),
                new KPanel(new KLabel("QUALITY POINT", KFontFactory.createBoldFont(15))));

        final KPanel avgPanel = new KPanel(255,55);
        avgPanel.setLayout(new BoxLayout(avgPanel, BoxLayout.X_AXIS));
        avgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1,false));
        avgPanel.add(leftHand);
        avgPanel.add(new KSeparator(KSeparator.VERTICAL));
        final KPanel numberHolder = new KPanel();
        numberHolder.setLayout(new BoxLayout(numberHolder, BoxLayout.X_AXIS));
        numberHolder.addAll(new KPanel(), CGPALabel, new KPanel());
        avgPanel.add(numberHolder);

        final KPanel kPanel = new KPanel(new BorderLayout());
        kPanel.add(avgPanel, BorderLayout.EAST);
        return kPanel;
    }

}

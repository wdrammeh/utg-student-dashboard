package core;

import core.alert.NotificationActivity;
import core.driver.MDriver;
import core.first.FirstLaunch;
import core.module.Analysis;
import core.module.ModuleActivity;
import core.module.ModuleHandler;
import core.module.SemesterActivity;
import core.serial.Serializer;
import core.setting.Settings;
import core.setting.SettingsUI;
import core.task.TaskActivity;
import core.transcript.TranscriptActivity;
import core.user.Student;
import core.utils.App;
import core.utils.Globals;
import core.utils.Internet;
import core.utils.MComponent;
import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author Muhammed W. Drammeh <md21712494@utg.edu.gm>
 *
 * The ultimate class of UI.
 * When using Dashboard, the user actually interacts with an instance of this
 * child of KFrame, an hier to JFrame.
 *
 * I'll assume a height of about 20 - 30 pixels is consumed by the decoration of the platform.
 * thoraxPanel shall cover, at least, 230 pixels of height
 * bodyLayer (Panel), 450
 * Thus, the total dimension is roughly 1_000 x (680 - 700)
 *
 * It is important to explore the {@link Activity} interface, and implementing classes..
 */
public final class Board extends KFrame {
    /**
     * The container onto which the 2 layers are placed.
     * The contentPane is set to this.
     */
    private static KPanel contentPanel;
    /**
     * Has the cardLayout and responsible for bringing and discarding the main activities.
     * it has a height of 450.
     */
    private static KPanel bodyLayer;
    /**
     * The layout which shifts between the main activities:
     * includes the outline buttons activities - 'Home', 'News', 'Tasks', 'Notifications';
     * as well as the home-panel activities - 'Semester', 'Collection', 'Personalization',
     * 'Transcript', 'Analysis', 'FAQ', 'About'
     *
     * Activities within the main activities are generally referred to as presents.
     */
    private static CardLayout cardLayout;
    /**
     * The runtime instance of Dashboard. Used for static access.
     */
    private static Board instance;
    /**
     * Determines whether the current instance is ready, mostly for visual modifications.
     * This is only set to true after the Dashboard is done building.
     * @see #completeBuild()
     */
    private static boolean isReady;
    private static KLabel imageLabel;
    private static KLabel levelLabel;
    private static KLabel statusLabel;
    private static KLabel nameLabel;
    private static KLabel semesterLabel;
    private static KButton notificationButton;
//    Collaborators declaration. The order in which these will be initialized does matter!
    private SemesterActivity semesterActivity;
    private ModuleActivity moduleActivity;
    private SettingsUI settingsUI;
    private TranscriptActivity transcriptActivity;
    private Analysis analysisActivity;
    private Help helpActivity;
    private About about;
    private TaskActivity taskActivity;
    private News newsPresent;
    private NotificationActivity alertActivity;
    public static final Thread SHUT_DOWN_HOOK = new Thread(Serializer::mountUserData);
    /**
     * Some processes cannot be applied while Dashboard is building,
     * so they're packed here, and triggered simultaneously as soon as
     * Dashboard is done building.
     * Do not misuse this by adding processes that can be done during build.
     */
    public static final ArrayList<Runnable> POST_PROCESSES = new ArrayList<Runnable>() {
        @Override
        public boolean add(Runnable runnable) {
            if (isReady) {
                runnable.run();
                return false;
            }
            return super.add(runnable);
        }
    };


    public Board() {
        super("UTG Student Dashboard");
        instance = Board.this;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!Settings.confirmExit || App.showYesNoCancelDialog("Confirm Exit",
                        "Do you want to close the Dashboard?")) {
                    setVisible(false);
                }
            }
        });

        Settings.allLooksInfo = UIManager.getInstalledLookAndFeels();
        if (!Dashboard.isFirst()) {
            Settings.deserialize();
            for (UIManager.LookAndFeelInfo lookAndFeelInfo : Settings.allLooksInfo) {
                if (lookAndFeelInfo.getName().equals(Settings.lookName)) {
                    try {
                        UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    } catch (Exception e) {
                        POST_PROCESSES.add(()-> App.reportError(e));
                    }
                    break;
                }
            }
            POST_PROCESSES.add(()-> {
                if (Internet.isInternetAvailable()) {
                    MDriver.setup();
                }
            });
        }

        contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        setContentPane(contentPanel);
        setUpThorax();
        setUpBody();

        if (!(Dashboard.isFirst() || Student.isGuest())) {
            Portal.deSerialize();
        }
        semesterActivity = new SemesterActivity();
        moduleActivity = new ModuleActivity();
        settingsUI = new SettingsUI();
        transcriptActivity = new TranscriptActivity();
        analysisActivity = new Analysis();
        helpActivity = new Help();
        about = new About();
//        outlined / big buttons
        alertActivity = new NotificationActivity();
        taskActivity = new TaskActivity();
        newsPresent = new News();

        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(null);
        attachListeners();
        completeBuild();
    }

    /**
     * Sets up the thorax-region of the Dashboard, and adds it to the contentPanel.
     * Total height is between 230 to 250, and  30 is reserved for the outline-buttons.
     * This is horizontally partitioned into 3 sections with widths as follows:
     * imagePart = 275
     * midPart is unset, and
     * detailsPart = 375
     */
    private void setUpThorax() {
        final KMenuItem resetOption = new KMenuItem("Set Default", e-> Student.fireIconReset());

        final JPopupMenu imageOptionsPop = new JPopupMenu();
        imageOptionsPop.add(new KMenuItem("Select", e-> Student.startSettingImage()));
        imageOptionsPop.add(resetOption);

        imageLabel = new KLabel(Student.getIcon());
        final KPanel imagePart = new KPanel(new BorderLayout(), new Dimension(275,200));
        imagePart.add(imageLabel);
        imagePart.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) { // For unix-based systems
                    resetOption.setEnabled(!Student.isDefaultIconSet());
                    imageOptionsPop.show(imagePart, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) { // Windows systems
                mousePressed(e);
            }
        });

        levelLabel = new KLabel(Student.isGuest() ? "" : Student.getLevel().toUpperCase(),
                KFontFactory.createPlainFont(15), Color.BLUE);
        final KPanel levelPanel = new KPanel(new FlowLayout(FlowLayout.LEFT), new Dimension(325,25));
        if (!Student.isGuest()) {
            levelPanel.addAll(new KLabel("Level:", KFontFactory.createPlainFont(15)), levelLabel);
        }

        nameLabel = new KLabel(Student.requiredNameForFormat().toUpperCase(), KFontFactory.createBoldFont(20));
        if (!Student.isGuest()) {
            nameLabel.setToolTipText(Student.getMajor()+" Major");
        }

        final KLabel programLabel = new KLabel(Student.isGuest() ? "" : Student.getProgram(),
                KFontFactory.createPlainFont(17));

        final KButton toPortalButton = KButton.createIconifiedButton("go-arrow.png",25,25);
        toPortalButton.setText("Go Portal");
        toPortalButton.setMaximumSize(new Dimension(145, 35));
        toPortalButton.setFont(KFontFactory.createBoldItalic(15));
        toPortalButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toPortalButton.setToolTipText(String.format("Visit %s Portal", Student.isGuest() ? "UTG" : "your"));
        toPortalButton.addActionListener(actionEvent-> new Thread(()-> Portal.openPortal(toPortalButton)).start());

        final KPanel midPart = new KPanel(300, 200);
        midPart.setLayout(new GridLayout(7, 1, 5, 0));
        midPart.addAll(levelPanel, Box.createRigidArea(new Dimension(100, 10)),
                new KPanel(nameLabel), new KPanel(programLabel),
                Box.createRigidArea(new Dimension(100, 10)));
        final KPanel horizontalWrapper = new KPanel();
        horizontalWrapper.setLayout(new BoxLayout(horizontalWrapper, BoxLayout.X_AXIS));
        horizontalWrapper.addAll(new KPanel(), toPortalButton, new KPanel());
        midPart.add(horizontalWrapper);//notice how the last space is automatically left blank.
        //besides, the height and the spaces do not seem to count

        final KLabel aboutUTGLabel = new KLabel("About UTG", KFontFactory.createBoldFont(15), Color.BLUE);
        aboutUTGLabel.setToolTipText("Learn more about UTG");
        aboutUTGLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aboutUTGLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(()-> {
                    aboutUTGLabel.setEnabled(false);
                    try {
                        Internet.visit(News.HOME_SITE);
                    } catch (Exception ex) {
                        App.reportError(ex);
                    } finally {
                        aboutUTGLabel.setEnabled(true);
                    }
                }).start();
            }
        });

        statusLabel = new KLabel(Student.isGuest() ? "" : Student.getStatus().toUpperCase(),
                KFontFactory.createBoldFont(15));
        statusLabel.setForeground(Color.GRAY);

        final KPanel statePanel = new KPanel();
        if (!Student.isGuest()) {
            statePanel.addAll(new KLabel("Status:", KFontFactory.createPlainFont(15)), statusLabel);
        }

        final KPanel upperDetails = new KPanel(new BorderLayout());
        upperDetails.add(statePanel, BorderLayout.WEST);
        upperDetails.add(new KPanel(aboutUTGLabel), BorderLayout.EAST);

        final KLabel utgIcon = KLabel.createIcon("UTGLogo.gif", 125, 85);

        final KLabel schoolLabel = createLabelFor("School of", Student.getSchool());
        final KLabel divLabel = createLabelFor("Division of", Student.getDivision());
        semesterLabel = new KLabel(Student.isGuest() ? "" : Student.getSemester(),
                KFontFactory.createBoldFont(17));

        final KPanel moreDetails = new KPanel();
        moreDetails.setLayout(new BoxLayout(moreDetails, BoxLayout.Y_AXIS));
        moreDetails.addAll(schoolLabel, divLabel, semesterLabel);

        final KPanel detailsPart = new KPanel(375, 200);
        detailsPart.setLayout(new BorderLayout());
        detailsPart.add(upperDetails, BorderLayout.NORTH);
        detailsPart.add(utgIcon, BorderLayout.CENTER);
        detailsPart.add(moreDetails, BorderLayout.SOUTH);

        final int outlinesWidth = 215;
        final Font outlinesFont = KFontFactory.createBoldFont(15);

        final KButton toHome = new KButton("HOME");
        toHome.setFont(outlinesFont);
        toHome.setPreferredSize(new Dimension(outlinesWidth, toHome.getPreferredSize().height));
        toHome.addActionListener(e-> showCard("Home"));

        final KButton toTasks = new KButton("MY TASKS+");
        toTasks.setFont(outlinesFont);
        toTasks.setPreferredSize(new Dimension(outlinesWidth, toTasks.getPreferredSize().height));
        toTasks.addActionListener(e-> taskActivity.answerActivity());

        final KButton toNews = new KButton("NEWS");
        toNews.setFont(outlinesFont);
        toNews.setPreferredSize(new Dimension(outlinesWidth, toNews.getPreferredSize().height));
        toNews.addActionListener(e-> newsPresent.answerActivity());

        notificationButton = new KButton("NOTIFICATIONS"){
            @Override
            public void setToolTipText(String text) {
                super.setToolTipText(text);
                if (text == null) {
                    super.setForeground(null);
                    super.setCursor(null);
                } else {
                    super.setForeground(Color.RED);
                    super.setCursor(MComponent.HAND_CURSOR);
//                    Todo signal a desktop notification here
                }
            }
        };
        notificationButton.setFont(outlinesFont);
        notificationButton.setPreferredSize(new Dimension(outlinesWidth,
                notificationButton.getPreferredSize().height));
        notificationButton.addActionListener(e-> alertActivity.answerActivity());

        final KPanel bigButtonsPanel = new KPanel(new FlowLayout(FlowLayout.CENTER, 10, 5),
                new Dimension(1_000, 30));
        bigButtonsPanel.addAll(toHome, toTasks, toNews, notificationButton);

        final KPanel thoraxPanel = new KPanel(1_000,230);
        thoraxPanel.setLayout(new BorderLayout());
        thoraxPanel.add(imagePart, BorderLayout.WEST);
        thoraxPanel.add(midPart, BorderLayout.CENTER);
        thoraxPanel.add(detailsPart, BorderLayout.EAST);
        thoraxPanel.add(bigButtonsPanel, BorderLayout.SOUTH);
        contentPanel.add(thoraxPanel, BorderLayout.NORTH);
    }

    /**
     * Specifically used to create labels for the school, and the department.
     * It tackles both the issue of missing data, or trial users.
     * If t has no text, as defined by #{@link Globals#hasNoText(String)},
     * the empty string is assigned; t is assigned if it contains Unknonw -
     * e.g Unknown School, Unknown Department / Division;
     * otherwise, the header is attached to the value, separated by a whitespace.
     * @see Student#getDivision()
     * @see Student#getSchool()
     * @see Globals#hasNoText(String)
     */
    private KLabel createLabelFor(String h, String t){
        final String text = Globals.hasNoText(t) ? "" : t.contains("Unknown") ? t : h+" "+t;
        return new KLabel(text, KFontFactory.createBoldFont(17));
    }

    /**
     * Sets up the body region of the Dashboard, and adds it to the contentPanel.
     * Notice the cardLayout is initialized with this setup and also adds the
     * home-page as the first card.
     */
    private void setUpBody() {
        cardLayout = new CardLayout();
        bodyLayer = new KPanel(cardLayout);
        bodyLayer.setPreferredSize(new Dimension(1_000, 450));
        cardLayout.addLayoutComponent(bodyLayer.add(generateHomePage()),"Home");
        contentPanel.add(new KScrollPane(bodyLayer), BorderLayout.CENTER);
    }

    /**
     * Attaches universal action-, and key-listeners to the Dashboard.
     * Since typical Dashboard buttons do not seek focus,
     * the keys are typically focus-hungry.
     * In a future release, some activities will define or
     * have their own key-bindings as well.
     * @see KButton
     */
    private void attachListeners(){
        final KButton homeButton = KButton.createRootPaneButton(KeyEvent.VK_H,
                e-> cardLayout.show(bodyLayer, "Home"));
        homeButton.setFocusable(true);
        rootPane.add(homeButton);

        final Timer onlineTimer = new Timer(5 * Globals.SECOND, null);
        onlineTimer.addActionListener(e-> new Thread(()-> {
            if (Internet.isInternetAvailable()) {
                Internet.checkForUpdate(false);
                if (Portal.isAutoSynced() && !Student.isGuest()) {
                    syncAll();
                }
                onlineTimer.stop();
            }
        }).start());
        POST_PROCESSES.add(onlineTimer::start);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            super.setVisible(true);
            if (Dashboard.isFirst() && !Student.isGuest()) {
                final FirstLaunch firstLaunch = new FirstLaunch();
                SwingUtilities.invokeLater(()-> firstLaunch.setVisible(true));
            }
            for (Runnable r : POST_PROCESSES) {
                new Thread(r).start();
            }
            POST_PROCESSES.clear();
        } else {
            collapse();
        }
    }

    /**
     * Collapses this instance bu disposing it off first, followed
     * by terminating the VM.
     * Do not call this directly! It must be triggered by {@link #setVisible(boolean)}
     */
    private void collapse(){
        dispose();
        System.exit(0);
    }

    /**
     * Completes building the Dashboard.
     * By the time this call is made, all components must have been loaded
     * except those waiting in the {@link #POST_PROCESSES}.
     * Most collaborators use the {@link #POST_PROCESSES} to postpone their
     * actions.
     */
    private void completeBuild() {
        if (Dashboard.isFirst()) {
            SettingsUI.loadDefaults();
        }
        Runtime.getRuntime().addShutdownHook(SHUT_DOWN_HOOK);
        isReady = true;
    }

    /**
     * Generates the home-page (of the bodyLayer).
     * Consists of a series of panels referred to as the "home-panels";
     * each of which provokes a main-activity.
     * @see #newHomePanel(String, String, int, int)
     */
    private JComponent generateHomePage(){
        final KPanel semesterPanel = newHomePanel("This Semester","current.png",200,170);
        semesterPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                semesterActivity.answerActivity();
            }
        });

        final KPanel collectionPanel = newHomePanel("Module Collection","collection.png",
                200,170);
        collectionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                moduleActivity.answerActivity();
            }
        });

        final KPanel settingsPanel = newHomePanel("Privacy & Settings","personalization.png",
                200,170);
        settingsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                settingsUI.answerActivity();
            }
        });

        final KPanel transcriptPanel = newHomePanel("My Transcript","transcript.png",
                190,155);
        transcriptPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                transcriptActivity.answerActivity();
            }
        });

        final KPanel analysisPanel = newHomePanel("Analysis","analysis.png",200,190);
        analysisPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                analysisActivity.answerActivity();
            }
        });

        final KPanel helpPanel = newHomePanel("FAQ & Help","help.png",200,170);
        helpPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                helpActivity.answerActivity();
            }
        });

        final KPanel aboutPanel = newHomePanel("About","about.png",200,170);
        aboutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                about.setVisible(true);
            }
        });

        final KPanel homePage = new KPanel(new GridLayout(2, 4, 30, 15));
        homePage.addAll(semesterPanel, collectionPanel, settingsPanel, transcriptPanel, analysisPanel,
                helpPanel, aboutPanel);
        return homePage;
    }

    /**
     * Creates and returns a home-panel with the specified text as its title.
     * It uses the given iconName and scales it to iWidth and iHeight.
     */
    private static KPanel newHomePanel(String text, String iconName, int iWidth, int iHeight){
        final KLabel label = new KLabel(text, KFontFactory.createPlainFont(16));
        final KPanel panel = new KPanel(new BorderLayout());
        panel.add(new KPanel(new Dimension(225,30), label), BorderLayout.NORTH);
        panel.add(KLabel.createIcon(iconName, iWidth, iHeight), BorderLayout.CENTER);
        panel.setCursor(MComponent.HAND_CURSOR);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setFont(KFontFactory.createBoldFont(17));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setFont(KFontFactory.createPlainFont(16));
            }
        });
        return panel;
    }

    /**
     * Adds the specified component to the cardLayout with the given name.
     * The name will be used when requesting such a card.
     * @see #showCard(String)
     */
    public static void addCard(Component component, String name){
        cardLayout.addLayoutComponent(bodyLayer.add(component), name);
    }

    /**
     * Requests to show a main-activity which on the bodyLayer with the given name.
     * A component with the specified name must have been added  already.
     * @see #addCard(Component, String)
     */
    public static void showCard(String name){
        cardLayout.show(bodyLayer, name);
    }

    /**
     * Returns the rootPane of the current instance of the Dashboard.
     * A shorthand way of calling Board.getInstance().getRootPane()
     *
     * If the instance is null, returns null.
     */
    public static JRootPane getRoot(){
        return instance == null ? null : instance.rootPane;
    }

    public static Board getInstance(){
        return instance;
    }

    public static boolean isReady(){
        return isReady;
    }

    public static void setReady(boolean ready){
        isReady = ready;
    }

    /**
     * Returns the outlined notification button.
     * Used by NotificationActivity to adjust the toolTip on the go.
     */
    public static KButton getNotificationButton() {
        return notificationButton;
    }

    /**
     * Effects the image icon changes.
     * Called by the Student type to signal that a user has just changed icon.
     * So the imageLabel will change its icon accordingly.
     */
    public static void effectIconChanges(){
       imageLabel.setIcon(Student.getIcon());
    }

    public static void effectSemesterUpgrade() {
        final String semester = Student.getSemester();
        POST_PROCESSES.add(()-> {
            semesterLabel.setText(semester);
            SemesterActivity.semesterBigLabel.setText(semester);
        });
    }

    public static void effectLevelUpgrade() {
        POST_PROCESSES.add(()-> levelLabel.setText(Student.getLevel().toUpperCase()));
    }

    public static void effectNameFormatChanges(){
        final String requiredName = Student.requiredNameForFormat();
        POST_PROCESSES.add(()-> nameLabel.setText(requiredName.toUpperCase()));
    }

    public static void effectStatusUpgrade(){
        POST_PROCESSES.add(()-> statusLabel.setText(Student.getStatus().toUpperCase()));
    }

    private static void syncAll() {
        SemesterActivity.startMatching(false);
        ModuleHandler.launchThoroughSync(false, null);
        NotificationActivity.updateNotices(false);
        instance.newsPresent.packAll(false);
    }

}

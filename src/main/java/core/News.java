package core;

import core.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class News implements Activity {
    private KPanel present;
    private KScrollPane scrollPane;
    private KButton refreshButton;
    private KLabel accessLabel;
    private KPanel accessResident;
    private boolean isFirstView;
    private static String accessTime;
    private static final ArrayList<NewsSavior> NEWS_DATA = new ArrayList<>() {
        @Override
        public boolean contains(Object o) {
            for (NewsSavior savior : NEWS_DATA) {
                if (savior.equals(o)) {
                    return true;
                }
            }
            return false;
        }
    }; // unlike many of its kind, this does not explicitly delete.
    public static final String HOME_SITE = "https://www.utg.edu.gm/";
    public static final String NEWS_SITE = "https://www.utg.edu.gm/category/news/";


    public News() {
        refreshButton = new KButton("Refresh");
        refreshButton.setFont(FontFactory.createPlainFont(15));
        refreshButton.setCursor(MComponent.HAND_CURSOR);
        refreshButton.addActionListener(e-> new Thread(()-> packAll(true)).start());

        final KPanel northPanel = new KPanel(new BorderLayout());
        northPanel.add(new KPanel(new KLabel("News Feeds", FontFactory.BODY_HEAD_FONT)), BorderLayout.WEST);
        northPanel.add(new KPanel(refreshButton), BorderLayout.EAST);

        accessTime = "News Feeds will be shown here... Refresh now to get updates.";
        accessLabel = new KLabel(accessTime, FontFactory.createPlainFont(14), Color.GRAY);

        accessResident = new KPanel(new FlowLayout(FlowLayout.CENTER, 5, 20), accessLabel);

        present = new KPanel();
        present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
        present.add(accessResident);

        isFirstView = true;
        scrollPane = new KScrollPane(present);

        final KPanel activityPanel = new KPanel(new BorderLayout());
        activityPanel.add(northPanel, BorderLayout.NORTH);
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        Board.addCard(activityPanel, "News");

        if (Dashboard.isFirst()) {
            NEWS_DATA.clear(); // somewhat necessary... e.g. from "trial" to login
            new Thread(()-> packAll(false)).start();
        } else {
            Board.POST_PROCESSES.add(this::deserialize);
        }
    }

    @Override
    public void answerActivity() {
        Board.showCard("News");
        if (isFirstView) {
            SwingUtilities.invokeLater(()-> scrollPane.toTop());
            isFirstView = false;
        }
    }

    public void packAll(boolean userRequest) {
        refreshButton.setEnabled(false);
        try {
            final Document doc = Jsoup.connect(NEWS_SITE).get();
            final List<Element> elements = doc.getElementsByTag("article");
            final boolean wasEmpty = NEWS_DATA.isEmpty();
            for (final Element element : elements) {
                final String head = element.select("h2.entry-title").text();
                final String body = element.getElementsByTag("p").text();
                final String link = element.getElementsByTag("a").attr("href");
                final NewsSavior savior = new NewsSavior(head, body, link, null);
                if (!NEWS_DATA.contains(savior)) {
                    if (wasEmpty) {
                        NEWS_DATA.add(savior);
                        present.add(packNews(head, body, link, null));
                    } else {
                        NEWS_DATA.add(0, savior);
                        present.add(packNews(head, body, link, null), 0);
                    }
                    MComponent.ready(present);
                }
            }
            accessTime = "Accessed: "+ MDate.formatNow();
            accessLabel.setText(accessTime);
            present.add(accessResident); // will be sent to the bottom while trying to change its parent
            if (userRequest) {
                App.reportInfo("News", "News feeds refreshed successfully.");
            }
        } catch (IOException e) {
            if (userRequest) {
                App.reportError("Error",
                        "We're unable to access the server at 'utg.edu.gm'.\n" +
                                "Please try again later.");
            }
        } finally {
            refreshButton.setEnabled(true);
            MComponent.ready(present);
        }
    }

    /**
     * Organizes a news in a panel.
     */
    private KPanel packNews(String header, String body, String link, String allContent) {
        final KLabel hLabel = new KLabel(header, FontFactory.createBoldFont(18), Color.BLUE);
        final KTextPane textPane = KTextPane.htmlFormattedPane(body.substring(0, body.length() - (header.length() + 13)));
        final NewsDialog newsDialog = new NewsDialog(header, body, link, allContent);

        final KButton extendedReader = new KButton();
        extendedReader.setFont(FontFactory.createPlainFont(15));
        extendedReader.setCursor(MComponent.HAND_CURSOR);
        if (Globals.hasNoText(allContent)) {
            extendedReader.setText("Get full news");
            extendedReader.addActionListener(e-> newsDialog.primaryClick(extendedReader));
        } else {
            extendedReader.setText("Continue reading");
            extendedReader.setForeground(Color.BLUE);
            extendedReader.addActionListener(e-> newsDialog.setVisible(true));
        }

        final KPanel readerWrap = new KPanel(new FlowLayout(FlowLayout.RIGHT), extendedReader);

        final KPanel niceBox = new KPanel(new BorderLayout());
        niceBox.setBackground(Color.WHITE);
        niceBox.setPreferredSize(new Dimension(975, 160));
        niceBox.add(new KPanel(new FlowLayout(FlowLayout.LEFT), hLabel), BorderLayout.NORTH);
        niceBox.add(textPane, BorderLayout.CENTER);
        niceBox.add(readerWrap, BorderLayout.SOUTH);
        return new KPanel(new FlowLayout(FlowLayout.CENTER, 5, 5), niceBox);
    }


    private static class NewsDialog extends KDialog {
        private String keyContent;
        private String bodyContent;
        private String associateLink;
        private String allContent;
        private KTextPane textPane;

        private NewsDialog(String heading, String body, String link, String allNews){
            super(heading);
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);
            keyContent = heading;
            bodyContent = body;
            associateLink = link;
            allContent = allNews;
            textPane = KTextPane.htmlFormattedPane(allContent);
            textPane.setPreferredSize(new Dimension(665, 465));
            KScrollPane newsScrollPane = new KScrollPane(textPane);

            final KButton visitButton = new KButton("Visit site");
            visitButton.addActionListener(e-> new Thread(()-> {
                visitButton.setEnabled(false);
                dispose();
                try {
                    Internet.visit(NEWS_SITE);
                } catch (Exception ex) {
                    App.reportError(ex);
                }
                visitButton.setEnabled(true);
            }).start());

            final KButton closeButton = new KButton("Close");
            closeButton.addActionListener(e-> dispose());

            final KPanel contentPanel = new KPanel(new BorderLayout());
            contentPanel.add(newsScrollPane, BorderLayout.CENTER);
            contentPanel.add(new KPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10), visitButton, closeButton),
                    BorderLayout.SOUTH);
            setContentPane(contentPanel);
            getRootPane().setDefaultButton(closeButton);
            pack();
            setLocationRelativeTo(Board.getRoot());
        }

        private void primaryClick(KButton primaryButton){
            new Thread(()-> {
                primaryButton.setEnabled(false);
                try {
                    final Document specificDocument = Jsoup.connect(associateLink).get();
                    allContent = specificDocument.select(".entry-content").outerHtml();
                    textPane.setText(allContent);
                    primaryButton.setText("Continue reading");
                    primaryButton.setForeground(Color.BLUE);
                    primaryButton.removeActionListener(primaryButton.getActionListeners()[0]);
                    primaryButton.addActionListener(e-> setVisible(true));
                    final NewsSavior updatedNews = new NewsSavior(keyContent, bodyContent, associateLink, allContent);
                    int i = 0;
                    for (; i < NEWS_DATA.size(); i++) {
                        if (updatedNews.equals(NEWS_DATA.get(i))) {
                            break;
                        }
                    }
                    NEWS_DATA.set(i, updatedNews);
                    setVisible(true);
                } catch (IOException ioe) {
                    App.reportError("Error",
                            "Failed to retrieve the contents of the news \"" + keyContent + "\"\n" +
                            "Please check back later.");
                } catch (Exception e) {
                    App.silenceException(e);
                } finally {
                    primaryButton.setEnabled(true);
                }
            }).start();
        }
    }


    private static final class NewsSavior {
        private String heading;
        private String body;
        private String link;
        private String content;

        private NewsSavior(String heading, String body, String link, String content){
            this.heading = heading;
            this.body = body;
            this.link = link;
            this.content = content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o){
                return true;
            } else if (o instanceof NewsSavior) {
                return this.heading.equals(((NewsSavior) o).heading);
            } else {
                return false;
            }
        }

    }


    public static void serialize() {
        final int length = NEWS_DATA.size();
        final String[] heads = new String[length];
        final String[] bodies = new String[length];
        final String[] links = new String[length];
        final String[] contents = new String[length];
        for (int i = 0; i < length; i++){
            final NewsSavior savior = NEWS_DATA.get(i);
            heads[i] = savior.heading;
            bodies[i] = savior.body;
            links[i] = savior.link;
            contents[i] = savior.content;
        }
        Serializer.toDisk(heads, Serializer.inPath("news", "heads.ser"));
        Serializer.toDisk(bodies, Serializer.inPath("news", "bodies.ser"));
        Serializer.toDisk(links, Serializer.inPath("news", "links.ser"));
        Serializer.toDisk(contents, Serializer.inPath("news", "contents.ser"));
        Serializer.toDisk(accessTime, Serializer.inPath("news", "access.time.ser"));
    }

    private void deserialize() {
        final Object headsObj = Serializer.fromDisk(Serializer.inPath("news", "heads.ser"));
        final Object bodiesObj = Serializer.fromDisk(Serializer.inPath("news", "bodies.ser"));
        final Object linksObj = Serializer.fromDisk(Serializer.inPath("news", "links.ser"));
        final Object contentsObj = Serializer.fromDisk(Serializer.inPath("news", "contents.ser"));
        if (headsObj != null && bodiesObj != null && linksObj != null && contentsObj != null) {
            try {
                final String[] heads = (String[]) headsObj;
                final String[] bodies = (String[]) bodiesObj;
                final String[] links = (String[]) linksObj;
                final String[] contents = (String[]) contentsObj;
                final int length = heads.length;
                for (int i = 0; i < length; i++){
                    final NewsSavior savior = new NewsSavior(heads[i], bodies[i], links[i], contents[i]);
                    NEWS_DATA.add(savior);
                }
                for (final NewsSavior news : NEWS_DATA) {
                    present.add(packNews(news.heading, news.body, news.link, news.content));
                }
                present.add(accessResident);
                MComponent.ready(present);
                final Object accessObj = Serializer.fromDisk(Serializer.inPath("news", "access.time.ser"));
                accessTime = String.valueOf(accessObj);
                accessLabel.setText(accessTime);
            } catch (Exception e) {
                App.silenceException(e);
            }
        }
    }

}

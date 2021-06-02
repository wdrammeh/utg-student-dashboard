package core.utils;

import core.alert.Notification;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import utg.Dashboard;
import utg.Version;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Date;

public class Internet {
    public static final String REPO_URL = "https://github.com/w-drammeh/utg-student-dashboard";
    public static final String DOWNLOAD_URL = REPO_URL+"#install"; // Todo check these out


    /**
     * Attempts a desktop browse to the given site.
     * The site will be visited to, making no internet availability checks.
     */
    public static void visit(String site) throws Exception {
        Desktop.getDesktop().browse(URI.create(site));
    }

    /**
     * Returns true if Dashboard is able to connect to the internet.
     * Calling this on the main thread can put Dashboard in a serious waiting state!
     *
     * @see #isHostAvailable(String)
     */
    public static boolean isInternetAvailable(){
        return isHostAvailable("google.com") || isHostAvailable("github.com") ||
                isHostAvailable("facebook.com");
    }

    /**
     * Returns true if a socket connection could be established with the given hostName;
     * false otherwise. Call this on a separate thread. This method is self-silent.
     *
     * @see #isInternetAvailable()
     */
    public static boolean isHostAvailable(String hostName) {
        try (final Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostName, 80), 10 * Globals.SECOND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void checkForUpdate(boolean requested){
        Element verElement = null;
        try {
            final Document document = Jsoup.connect(REPO_URL).get();
            verElement = document.selectFirst(".markdown-body > p:nth-child(2) > code:nth-child(1)");
        } catch (HttpStatusException httpStatusError) {
            Dashboard.setAuthentic(false);
            Dashboard.reportAuthenticationError();
        } catch (Exception e) {
            if (requested) {
                App.reportError(e);
            } else {
                App.silenceException(e);
            }
            return;
        }

        if (verElement == null) {
            return;
        }

        final Version latestVersion = new Version(verElement.text());
        final int comparison = Dashboard.VERSION.compare(latestVersion);
        if (comparison == Version.LESS) {
            final Date lastDeprecateTime = Dashboard.VERSION.getDeprecateTime();
            final String deprecateTime;
            if (lastDeprecateTime == null) {
                deprecateTime = MDate.daysAfter(new Date(), Version.MAX_DEPRECATE_TIME);
                Notification.create("Update", "A new update is available",
                        "<p>There is a new Dashboard release. Please <a href=" + DOWNLOAD_URL + ">download</a> it now.</p>" +
                                "<p><b>Please Note:</b> Your Dashboard will be outdated by '" + deprecateTime + "'.</p>");
            } else {
                deprecateTime = MDate.format(lastDeprecateTime);
            }

            final boolean updateNow = App.showYesNoCancelDialog("Update Available",
                    "A new version is available: '"+latestVersion+"'. Do you want to update now?");
            if (updateNow) {
                try {
                    visit(DOWNLOAD_URL);
                } catch (Exception e) {
                    App.reportError(e);
                }
            } else {
                App.reportWarning("Update Warning",
                        "Please note that you must update to the latest version on, or before '"+deprecateTime+"'.");
            }
            Dashboard.VERSION.setDeprecateTime(MDate.parse(deprecateTime));
        } else if (comparison == Version.EQUAL) {
            if (requested) {
                App.reportInfo("Up to Date", "Your Dashboard is up to date. Check back later for updates.");
            } else {
                App.silenceInfo("Dashboard is up to date.");
            }
        } else if (comparison == Version.GREATER) { // how possible?
            App.reportWarning("Unexpected Version", "Your Dashboard Version - '"+Dashboard.VERSION+"' - " +
                    "is over the latest version - '"+latestVersion+"'.\n" +
                    "Please contact the developers for this issue.");
        }
    }

}

package core.utils;

import core.Board;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

public class Internet {


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
     * @see #isHostAvailable(String)
     */
    public static boolean isInternetAvailable(){
        try {
            final boolean available = isHostAvailable("google.com")
                    || isHostAvailable("github.com") || isHostAvailable("facebook.com");
            if (available && Board.isReady()) {
                new Thread(Board::online).start(); // this should not delay the return
            }
            return available;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns true if a socket connection could be established with the given hostName;
     * false otherwise. Do not forget to this this on a separate thread.
     * Dashboard's internet check availability is based on this call.
     * @see #isInternetAvailable()
     */
    public static boolean isHostAvailable(String hostName) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(hostName, 80), 10_000);
            return true;
        } catch (UnknownHostException e) {
            App.silenceException(e);
            return false;
        } finally {
            socket.close();
        }
    }

}

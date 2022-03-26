package utg;

import core.utils.App;
import core.utils.Mailer;

public class ErrorMessage {
        public static void reportAuthenticationError() {
        App.reportWarning(null, "Authentication Error",
                "This program is either not verified, or no longer supported.\n" +
                        "Contact the developers: '"+ Mailer.DEVELOPER_MAIL +"'.");
        System.exit(0);
    }
}

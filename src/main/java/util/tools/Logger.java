package util.tools;

// Maximal professioneller Logger
public final class Logger {

    private static final boolean enabled = true;

    private Logger() {}

    public static void log(String message) {
        if (enabled) {
            System.out.println(message);
        }
    }

    public static void logNullable(String message) {
        if (message != null) {
            log(message);
        }
    }

    public static void logIfTrue(boolean pred, String message) {
        if (pred) {
            log(message);
        }
    }
}

package util;

// Maximal professioneller Logger
public final class Logger {

    private static final boolean enabled = true;

    private Logger() {}

    public static void log(String message) {
        if (enabled) {
            System.out.println(message);
        }
    }

    public static void logNullable(String message, String nullable) {
        if (nullable != null && !nullable.isBlank() && !nullable.isEmpty() && !"null".equals(nullable)) {
            log(message);
        }
    }

    public static void logIfTrue(boolean pred, String message) {
        if (pred) {
            log(message);
        }
    }
}

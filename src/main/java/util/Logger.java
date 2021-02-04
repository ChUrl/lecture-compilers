package util;

import java.util.Map;
import java.util.function.Supplier;

// Maximal professioneller Logger
public final class Logger {

    private static final boolean LOG_ENABLED = true;
    private static final boolean LOG_EXCEPTIONS = false;
    private static final int LOG_LEVEL = 1; // 0 = ERROR, 1 = DEBUG, 2 = INFO

    private static final Map<String, Boolean> packages;

    static {
        packages = Map.ofEntries(Map.entry("parser.grammar", true),
                                 Map.entry("parser", true),
                                 Map.entry("parser.ast", true),
                                 Map.entry("typechecker", true),
                                 Map.entry("codegen.flowgraph", true),
                                 Map.entry("codegen.analysis", true),
                                 Map.entry("codegen.analysis.dataflow", true),
                                 Map.entry("codegen.analysis.liveness", true),
                                 Map.entry("codegen", false));
    }

    private Logger() {}

    private static void log(String message, Class clazz) {
        if (LOG_ENABLED
            && packages.containsKey(clazz.getPackageName()) && packages.get(clazz.getPackageName()).equals(true)) {
            System.out.printf("%-75s\t(%s)%n", message, clazz.getName());

        } else if (LOG_ENABLED && !packages.containsKey(clazz.getPackageName())) {
            System.out.println("Failed Logging attempt from " + clazz.getName() + ": " + clazz.getPackageName());
        }
    }

    public static void logException(String message, Class clazz) {
        if (LOG_EXCEPTIONS) {
            log("EXCEP - " + message, clazz);
        }
    }

    public static void logError(String message, Class clazz) {
        if (LOG_LEVEL >= 0) {
            log("ERROR - " + message, clazz);
        }
    }

    public static void logDebug(String message, Class clazz) {
        if (LOG_LEVEL >= 1) {
            log("DEBUG - " + message, clazz);
        }
    }

    public static void logInfo(String message, Class clazz) {
        if (LOG_LEVEL >= 2) {
            log("INFO  - " + message, clazz);
        }
    }

    public static void logErrorSupplier(Supplier<String> call, Class clazz) {
        if (LOG_ENABLED && LOG_LEVEL >= 0) {
            logError(call.get(), clazz);
        }
    }

    public static void logDebugSupplier(Supplier<String> call, Class clazz) {
        if (LOG_ENABLED && LOG_LEVEL >= 1) {
            logDebug(call.get(), clazz);
        }
    }

    public static void logInfoSupplier(Supplier<String> call, Class clazz) {
        if (LOG_ENABLED && LOG_LEVEL >= 2) {
            logInfo(call.get(), clazz);
        }
    }

    // TODO: Flipped nullble and message
    public static void logErrorNullable(String nullable, String message, Class clazz) {
        if (nullable != null && !nullable.isEmpty() && !"null".equals(nullable)) {
            logError(message, clazz);
        }
    }

    public static void logDebugNullable(String nullable, String message, Class clazz) {
        if (nullable != null && !nullable.isEmpty() && !"null".equals(nullable)) {
            logDebug(message, clazz);
        }
    }

    public static void logInfoNullable(String nullable, String message, Class clazz) {
        if (nullable != null && !nullable.isEmpty() && !"null".equals(nullable)) {
            logInfo(message, clazz);
        }
    }

    public static void logErrorIfTrue(boolean pred, String message, Class clazz) {
        if (pred) {
            logError(message, clazz);
        }
    }

    public static void logDebugIfTrue(boolean pred, String message, Class clazz) {
        if (pred) {
            logDebug(message, clazz);
        }
    }

    public static void logInfoIfTrue(boolean pred, String message, Class clazz) {
        if (pred) {
            logInfo(message, clazz);
        }
    }
}

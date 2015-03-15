package jp.saka1029.cspj.problem.old;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static final Logger logger;

    static {
        logger = Logger.getLogger("saka1029.dcsp");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tFT%1$tT.%1$tL %4$s %5$s %6$s%n");
    }
    
    private static String[] format(String format, Object... args) {
        String s = String.format(format, args);
        return s.split("\\r?\\n");
    }

    public static boolean isInfoLoggable() { return logger.isLoggable(Level.INFO); }
    public static void info(String format, Object... args) {
        if (!logger.isLoggable(Level.INFO)) return;
        for (String s : format(format, args))
            logger.info(s);
    }
    public static void info(Throwable t) {
        logger.log(Level.INFO, "", t);
    }
    public static void info(Object obj) {
        if (!logger.isLoggable(Level.INFO)) return;
        info("%s", obj);
    }

    public static boolean isFineLoggable() { return logger.isLoggable(Level.FINE); }
    public static void fine(String format, Object... args) {
        if (!logger.isLoggable(Level.FINE)) return;
        for (String s : format(format, args))
            logger.fine(s);
    }

    public static boolean isWarnLoggable() { return logger.isLoggable(Level.WARNING); }
    public static void warn(String format, Object... args) {
        for (String s : format(format, args))
            logger.warning(s);
    }

    public static boolean isErrorLoggable() { return logger.isLoggable(Level.SEVERE); }
    public static void error(String format, Object... args) {
        for (String s : format(format, args))
            logger.severe(s);
    }
    
    public static void methodName() {
        StackTraceElement s = Thread.currentThread().getStackTrace()[2];
        Log.info("<<<<< %s#%s >>>>>", s.getClassName(), s.getMethodName());
    }
}

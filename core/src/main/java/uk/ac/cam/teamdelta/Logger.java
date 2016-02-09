package uk.ac.cam.teamdelta;

public class Logger {

    private static final boolean DEBUG = true;

    public static void debug(String s) {
        if (DEBUG) {
            System.out.println("DEBUG: " + s);
        }
    }
}

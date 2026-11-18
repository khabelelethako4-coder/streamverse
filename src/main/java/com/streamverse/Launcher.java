package com.streamverse;

/**
 * Plain entry point. Needed when the app is started from a fat JAR or from an
 * IDE that is not configured with --add-modules, because a class that extends
 * javafx.application.Application cannot be the main class on the classpath.
 */
public final class Launcher {
    private Launcher() { }

    public static void main(String[] args) {
        App.main(args);
    }
}

package cmanager.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Utility class for handling logging. */
public class LoggingUtil {

    /** The environment variable to pass the desired log level. */
    // Example usage: `CMANAGER_LOG_LEVEL=ALL gradle run`
    private static final String LOG_LEVEL_ENVIRONMENT_VARIABLE = "CMANAGER_LOG_LEVEL";

    /** The specified log level. */
    private static Level logLevel = null;

    /** Retrieve the log level from the environment variable. */
    private static void retrieveLevel() {
        // The log level is already set.
        if (logLevel != null) {
            return;
        }

        // Get the value of the environment variable.
        final String environmentVariable = System.getenv(LOG_LEVEL_ENVIRONMENT_VARIABLE);

        // Try to parse the log level.
        try {
            // Use the specified log level as it is valid.
            logLevel = Level.parse(environmentVariable);
        } catch (NullPointerException | IllegalArgumentException exception) {
            // The environment is either not set or the level is invalid, so disable logging.
            logLevel = Level.OFF;
        }
    }

    /**
     * Get the logger for the corresponding class.
     *
     * @param clazz The class to get the logger for.
     * @return The logger for the specified class, with the correct log level being set.
     */
    public static Logger getLogger(final Class clazz) {
        // Get the default logger for the class and create a default handler.
        final Logger logger = Logger.getLogger(clazz.getName());
        final Handler handler = new ConsoleHandler();

        // Set the log level for the handler.
        retrieveLevel();
        handler.setLevel(logLevel);

        // Add the handler to the logger and set the log level for the logger.
        logger.addHandler(handler);
        logger.setLevel(logLevel);

        // Return the configured logger itself.
        return logger;
    }
}

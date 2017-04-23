package edu.uwf.cs.logging;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import static org.apache.log4j.Priority.*;

public class LoggingAppender extends ConsoleAppender {

    /* keep commented out colors just in case someone wants to change to them */
    private static final int NORMAL = 0;
//    private static final int BRIGHT = 1;
//    private static final int FOREGROUND_RED = 31;
//    private static final int FOREGROUND_GREEN = 32;
    private static final int FOREGROUND_YELLOW = 33;
//    private static final int FOREGROUND_BLUE = 34;
    private static final int FOREGROUND_CYAN = 36;

    private static final String PREFIX = "\u001b[";
    private static final String SUFFIX = "m";
    private static final char SEPARATOR = ';';
    private static final String END_COLOUR = PREFIX + SUFFIX;

    private static final String WARN_COLOUR = PREFIX
            + NORMAL + SEPARATOR + FOREGROUND_CYAN + SUFFIX;
    private static final String INFO_COLOUR = PREFIX
            + NORMAL + SEPARATOR + FOREGROUND_YELLOW + SUFFIX;

    /**
     * Wraps the ANSI control characters around the
     * output from the super-class Appender.
     */
    protected void subAppend(LoggingEvent event) {
        this.qw.write(getColour(event.getLevel()));
        super.subAppend(event);
        this.qw.write(END_COLOUR);

        if (this.immediateFlush) {
            this.qw.flush();
        }
    }

    /**
     * Get the appropriate control characters to change
     * the colour for the specified logging level.
     */
    private String getColour(Level level) {
        switch (level.toInt()) {
            case WARN_INT:
                return WARN_COLOUR;
            case INFO_INT:
                return INFO_COLOUR;
            default:
                return INFO_COLOUR;
        }
    }
}
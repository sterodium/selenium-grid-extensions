package io.sterodium.extensions.log;

import io.sterodium.extensions.spi.GridConfigurator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * Configures log output and log level from command line arguments if logging is not configured by
 * log4j.properties. If no log file is specified then logs to console.
 *
 * -log {file_path}  sets log file
 * -debug            sets log level to DEBUG (default is INFO)
 *
 * @author Vladimir Ilyin ilyin371@gmail.com
 *         Date: 20/11/2015
 */
public class LoggingConfigurator implements GridConfigurator {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LoggingConfigurator.class);

    private static final String LOG_FILE_PARAM = "-log";
    private static final String LOG_FILE_PROPERTY = "selenium.LOGGER";
    private static final String LOG_LEVEL_PROPERTY = "selenium.LOGGER.level";

    private static final String PATTERN = "%d{ISO8601} [%t] [%p] [%c] %m%n";
    private static final String MAX_FILE_SIZE = "10MB";
    private static final int MAX_BACKUPS = 1;

    private Level logLevel;

    @Override
    public String[] configure(String[] args) {

        boolean isLog4jConfigured = Logger.getRootLogger().getAllAppenders().hasMoreElements();
        if (!isLog4jConfigured) {
            logLevel = Arrays.asList(args).contains("-debug")
                    ? Level.DEBUG
                    : getDefaultLogLevel();

            String logFilename = getLogFilename(args);
            if (logFilename != null && !logFilename.isEmpty()) {
                installFileAppender(logFilename);
            } else {
                installConsoleAppender();
                LOG.info("No logging configuration found, logging to console");
            }
        }
        return clearLogFilenameParam(args);
    }


    private void installConsoleAppender() {
        ConsoleAppender console = new ConsoleAppender();
        console.setName("Console");
        console.setThreshold(logLevel);
        console.setLayout(new PatternLayout(PATTERN));
        console.setFollow(true);
        console.activateOptions();

        Logger.getRootLogger().addAppender(console);
    }

    private void installFileAppender(String logFilename) {
        RollingFileAppender file = new RollingFileAppender();
        file.setName("FileAppender");
        file.setThreshold(logLevel);
        file.setFile(new File(logFilename).getAbsolutePath());
        file.setMaxFileSize(MAX_FILE_SIZE);
        file.setMaxBackupIndex(MAX_BACKUPS);
        file.setLayout(new PatternLayout(PATTERN));
        file.setAppend(true);
        file.activateOptions();

        Logger.getRootLogger().addAppender(file);
    }

    private static String getLogFilename(String[] args) {
        String logFilename;
        int idx = Arrays.asList(args).indexOf(LOG_FILE_PARAM);
        if (-1 != idx) {
            logFilename = args[idx + 1];
        } else {
            logFilename = getLogFilenameFromSystemProperty();
        }
        return logFilename;
    }

    private static String[] clearLogFilenameParam(String[] args) {
        System.clearProperty(LOG_FILE_PROPERTY);
        int idx = Arrays.asList(args).indexOf(LOG_FILE_PARAM);
        if (-1 != idx) {
            if (args.length > idx + 1)
                args = ArrayUtils.remove(args, idx + 1); // value
            args = ArrayUtils.remove(args, idx); // name
        }
        return args;
    }

    private static String getLogFilenameFromSystemProperty() {
        final String logFilename;

        logFilename = System.getProperty(LOG_FILE_PROPERTY);
        if (null == logFilename) {
            return null;
        } else {
            return new File(logFilename).getAbsolutePath();
        }
    }

    private static Level getDefaultLogLevel() {
        final String logLevelProperty = System.getProperty(LOG_LEVEL_PROPERTY);
        if (null == logLevelProperty) {
            return Level.INFO;
        } else {
            return Level.toLevel(logLevelProperty);
        }
    }
}

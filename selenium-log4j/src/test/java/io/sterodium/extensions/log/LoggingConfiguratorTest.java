package io.sterodium.extensions.log;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


public class LoggingConfiguratorTest {

    private static final String LOG_FILENAME = "selenium.log";
    private static final String[] NO_ARGS = new String[0];
    private List<Appender> appenders;
    private LoggingConfigurator loggingConfigurator;

    @Before
    public void setUp() throws Exception {
        loggingConfigurator = new LoggingConfigurator();
        appenders = new LinkedList<>();
        Enumeration e = Logger.getRootLogger().getAllAppenders();
        while (e.hasMoreElements()) {
            Object appender = e.nextElement();
            if (appender instanceof Appender) {
                appenders.add((Appender) appender);
            }
        }
    }

    @Test
    public void configure_shouldInstallConsoleAppender() throws Exception {
        loggingConfigurator.configure(NO_ARGS);

        Appender appender = Logger.getRootLogger().getAppender("Console");
        assertNotNull(appender);
        assertThat(appender, instanceOf(ConsoleAppender.class));
    }

    @Test
    public void configure_shouldInstallFileAppenderFromArguments() throws Exception {

        String[] arguments = loggingConfigurator.configure(new String[]{"-log", "target/" + LOG_FILENAME});
        assertEquals(0, arguments.length);
        Appender appender = Logger.getRootLogger().getAppender("FileAppender");
        assertNotNull(appender);
        assertThat(appender, instanceOf(RollingFileAppender.class));
        assertThat(((RollingFileAppender) appender).getFile(), endsWith(LOG_FILENAME));
        assertThat(arguments.length, is(0));
    }

    @After
    public void tearDown() throws Exception {
        // restore
        Logger.getRootLogger().removeAllAppenders();
        for (Appender appender : appenders) {
            Logger.getRootLogger().addAppender(appender);
        }

        SLF4JBridgeHandler.uninstall();
    }

}
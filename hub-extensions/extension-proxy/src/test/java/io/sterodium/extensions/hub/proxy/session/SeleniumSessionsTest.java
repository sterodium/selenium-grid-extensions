package io.sterodium.extensions.hub.proxy.session;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 22/09/2015
 *         <p/>
 */
public class SeleniumSessionsTest {

    @Test(expected = IllegalArgumentException.class)
    public void getSessionIdExceptional() {
        SeleniumSessions.getSessionIdFromPath("/sessionId/");
    }

    @Test
    public void getSessionIdFromPath() {
        assertEquals("sessionId", SeleniumSessions.getSessionIdFromPath("/session/sessionId/"));
        assertEquals("sessionId", SeleniumSessions.getSessionIdFromPath("/session/sessionId/getCurrentWindow"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void trimSessionPathExceptional() {
        SeleniumSessions.trimSessionPath("/sessionId/");
    }

    @Test
    public void trimSessionPath() {
        assertEquals("", SeleniumSessions.trimSessionPath("/session/sessionId"));
        assertEquals("/request", SeleniumSessions.trimSessionPath("/session/id/request"));
        assertEquals("/another/one", SeleniumSessions.trimSessionPath("/session/id/another/one"));
    }
}

package io.sterodium.extensions.hub.proxy.session;

import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 21/09/2015
 */
public class SeleniumSessions {

    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("/session/([^/]+).*");

    private final Registry registry;

    public SeleniumSessions(Registry registry) {
        this.registry = registry;
    }

    public URL getRemoteHostForSession(String sessionId) {
        for (TestSession activeSession : registry.getActiveSessions()) {
            if (sessionId.equals(activeSession.getExternalKey().getKey())) {
                return activeSession.getSlot().getProxy().getRemoteHost();
            }
        }
        throw new IllegalArgumentException("Invalid sessionId. No active session is present for id:" + sessionId);
    }

    public void refreshTimeout(String sessionId) {
        for (TestSession activeSession : registry.getActiveSessions()) {
            if (sessionId.equals(activeSession.getExternalKey().getKey())) {
                refreshTimeout(activeSession);
            }
        }
    }

    private void refreshTimeout(TestSession activeSession) {
        if (activeSession.getInactivityTime() != 0) {
            activeSession.setIgnoreTimeout(false);
        }
    }

    public static String getSessionIdFromPath(String pathInfo) {
        Matcher matcher = SESSION_ID_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid request. Session Id is not present");
    }

    public static String trimSessionPath(String pathInfo) {
        return pathInfo.replaceFirst("/session/" + getSessionIdFromPath(pathInfo), "");
    }

}

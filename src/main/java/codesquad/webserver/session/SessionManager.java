package codesquad.webserver.session;

import codesquad.webserver.model.User;
import java.util.Map;

public interface SessionManager {
    Session createSession(User user);
    Session getSession(String sessionId);
    Map<String, Session> getSessions();
    void removeSession(String sessionId);
    void invalidateExpiredSessions();
    void clearAllSessions();
    void invalidateSession(String sessionId);
}

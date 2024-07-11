package codesquad.webserver.filter;

import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import codesquad.webserver.session.InMemorySessionManager;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@codesquad.webserver.annotation.Filter
public class SessionCheckFilter implements Filter {

    private static final String SESSION_KEY = "SID";
    private static final Logger logger = LoggerFactory.getLogger(SessionCheckFilter.class);

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) {
        if (isProtectedPath(request.requestLine().path()) && !hasValidSession(request)) {
            HttpResponse forbiddenResponse = HttpResponseBuilder.buildForbiddenFromFile();
            chain.setResponse(forbiddenResponse);
            return;
        }
    }

    private boolean isProtectedPath(String path) {
        return false; //TODO: 접근 거부 경로 추가
    }

    private boolean hasValidSession(HttpRequest request) {
        List<String> cookie = request.headers().get("Cookie");
        if (cookie == null || cookie.isEmpty()) {
            return false;
        }

        Optional<String> sessionId = cookie.stream()
                .flatMap(c -> List.of(c.split(";")).stream())
                .map(String::trim)
                .filter(c -> c.startsWith(SESSION_KEY + "="))
                .map(c -> c.substring((SESSION_KEY + "=").length()))
                .findFirst();

        if (sessionId.isEmpty()) {
            return false;
        }

        SessionManager sessionManager = InMemorySessionManager.getInstance();
        Session session = sessionManager.getSession(sessionId.get());
        return session != null;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

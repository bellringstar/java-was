package codesquad.webserver.dispatcher.requesthandler;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Component;
import codesquad.webserver.db.user.UserDatabase;
import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import codesquad.webserver.model.User;
import codesquad.webserver.parser.QueryStringParser;
import codesquad.webserver.session.Session;
import codesquad.webserver.session.SessionManager;
import codesquad.webserver.session.cookie.HttpCookie;
import codesquad.webserver.session.cookie.HttpCookie.SameSite;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LogoutRequestHandler extends AbstractRequestHandler {

    private static final String REDIRECT_PATH = "/";
    private static final String COOKIE_NAME = "SID";

    private static final Logger logger = LoggerFactory.getLogger(LogoutRequestHandler.class);

    private final SessionManager sessionManager;

    @Autowired
    public LogoutRequestHandler(FileReader fileReader,SessionManager sessionManager) {
        super(fileReader);
        this.sessionManager = sessionManager;
    }

    @Override
    protected HttpResponse handlePost(HttpRequest request) {
        try {
            String sessionId = request.getSessionIdFromRequest();
            sessionManager.invalidateSession(sessionId);

            return HttpResponseBuilder.redirect(REDIRECT_PATH)
                    .cookie(new HttpCookie(COOKIE_NAME, "")
                            .setMaxAge(0)
                            .setHttpOnly(true))
                    .build();
        } catch (IllegalStateException e) {
            logger.error("Session not found", e);
            return HttpResponseBuilder.buildNotFoundFromFile();
        }
    }
}
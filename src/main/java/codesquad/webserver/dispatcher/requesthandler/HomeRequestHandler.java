package codesquad.webserver.dispatcher.requesthandler;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Component;
import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import codesquad.webserver.model.User;
import codesquad.webserver.session.SessionManager;
import codesquad.webserver.template.TemplateEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HomeRequestHandler extends AbstractRequestHandler {

    private static final String FILE_PATH = "/index.html";
    private static final Logger logger = LoggerFactory.getLogger(HomeRequestHandler.class);

    private final TemplateEngine templateEngine;
    private final SessionManager sessionManager;

    @Autowired
    public HomeRequestHandler(FileReader fileReader, TemplateEngine templateEngine, SessionManager sessionManager) {
        super(fileReader);
        this.templateEngine = templateEngine;
        this.sessionManager = sessionManager;
    }

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        try {
            FileReader.FileResource file = fileReader.read(FILE_PATH);
            String fileContent = readFileContent(file);
            Map<String, Object> model = new HashMap<>();

            String sessionId = null;
            try {
                sessionId = request.getSessionIdFromRequest();
            } catch (IllegalStateException e) {
                logger.info("세션ID를 찾을 수 없는 요청입니다.: {}", e.getMessage());
            }

            User user = null;
            if (sessionId != null) {
                user = Optional.ofNullable(sessionManager.getSession(sessionId)).map(session -> session.getUser())
                        .orElse(null);
            }

            if (user != null) {
                model.put("isLoggedIn", true);
                model.put("username", user.getName());
            } else {
                model.put("isLoggedIn", false);
            }

            String content = templateEngine.render(fileContent, model);
            return HttpResponseBuilder.ok().body(content.getBytes()).build();
        } catch (IOException e) {
            logger.error("Error reading file: {}", e.getMessage());
            return HttpResponseBuilder.notFound().build();
        } catch (Exception e) {
            logger.error("Unhandled exception: {}", e.getMessage());
            return HttpResponseBuilder.serverError().build();
        }
    }

    private String readFileContent(FileReader.FileResource file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}

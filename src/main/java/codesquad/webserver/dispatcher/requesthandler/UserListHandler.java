package codesquad.webserver.dispatcher.requesthandler;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Component;
import codesquad.webserver.db.user.UserDatabase;
import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import codesquad.webserver.model.User;
import codesquad.webserver.session.SessionManager;
import codesquad.webserver.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserListHandler extends AbstractRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserListHandler.class);

    private final TemplateEngine templateEngine;
    private final SessionManager sessionManager;
    private final UserDatabase userDatabase;

    @Autowired
    public UserListHandler(FileReader fileReader, TemplateEngine templateEngine, SessionManager sessionManager, UserDatabase userDatabase) {
        super(fileReader);
        this.templateEngine = templateEngine;
        this.sessionManager = sessionManager;
        this.userDatabase = userDatabase;
    }

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        // 필터에서 세션 체크 이미 끝난 상태
        List<User> users = userDatabase.findAllUsers();
        Map<String, Object> model = new HashMap<>();
        model.put("users", users);
        String content = templateEngine.render(loadUserListTemplate(), model);
        return HttpResponseBuilder.ok().body(content.getBytes()).build();
    }

    private String loadUserListTemplate() {
        return "<h1>User List</h1>" +
                "<ul>" +
                "{{#for user in users}}" +
                "  <li>{{user.name}}</li>" +
                "{{/for}}" +
                "</ul>";
    }
}

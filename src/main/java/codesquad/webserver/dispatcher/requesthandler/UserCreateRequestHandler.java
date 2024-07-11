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
import java.util.Map;

@Component
public class UserCreateRequestHandler extends AbstractRequestHandler {

    private static final String HOME_PATH = "/";

    private final UserDatabase userDatabase;

    @Autowired
    public UserCreateRequestHandler(FileReader fileReader, UserDatabase userDatabase) {
        super(fileReader);
        this.userDatabase = userDatabase;
    }

    @Override
    protected HttpResponse handlePost(HttpRequest request) {
        Map<String, String> params = QueryStringParser.parse(request.body());
        User user = User.of(params);

        userDatabase.save(user);
        return HttpResponseBuilder.redirect(HOME_PATH).build();
    }
}

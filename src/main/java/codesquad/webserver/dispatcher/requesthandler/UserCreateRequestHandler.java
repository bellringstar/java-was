package codesquad.webserver.dispatcher.requesthandler;

import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.db.UserDatabase;
import codesquad.webserver.db.UserDatabaseFactory;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import codesquad.webserver.model.User;
import codesquad.webserver.parser.QueryStringParser;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCreateRequestHandler extends AbstractRequestHandler{

    private static final String HOME_PATH = "/index.html";
    private static final Logger logger = LoggerFactory.getLogger(UserCreateRequestHandler.class);

    public UserCreateRequestHandler(FileReader fileReader) {
        super(fileReader);
    }

    @Override
    protected HttpResponse handlePost(HttpRequest request) {
        UserDatabase userDatabase = UserDatabaseFactory.getInstance();
        logger.info("body: {}", request.body());
        Map<String, String> params = QueryStringParser.parse(request.body());
        logger.info("params: {}", params);
        User user = User.of(params);

        userDatabase.save(user);
        logger.debug("사용자 저장 : {}", user);
        return HttpResponseBuilder.buildRedirectResponse(HOME_PATH);
    }
}

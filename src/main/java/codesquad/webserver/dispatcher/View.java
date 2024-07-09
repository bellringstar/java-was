package codesquad.webserver.dispatcher;

import codesquad.webserver.httpresponse.HttpResponse;
import java.util.Map;

public interface View {
    HttpResponse render(Map<String, ?> model);
}

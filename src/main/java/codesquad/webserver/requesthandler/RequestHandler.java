package codesquad.webserver.requesthandler;

import codesquad.webserver.HttpRequest;
import codesquad.webserver.HttpResponse;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request);
}

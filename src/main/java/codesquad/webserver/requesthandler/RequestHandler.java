package codesquad.webserver.requesthandler;

import codesquad.webserver.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request);
}

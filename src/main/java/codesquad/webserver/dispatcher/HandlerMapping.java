package codesquad.webserver.dispatcher;

import codesquad.webserver.httprequest.HttpRequest;


public interface HandlerMapping {

    Object getHandler(HttpRequest request);
}

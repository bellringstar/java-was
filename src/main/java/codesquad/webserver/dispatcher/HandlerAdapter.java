package codesquad.webserver.dispatcher;

import codesquad.webserver.httprequest.HttpRequest;

public interface HandlerAdapter {
    boolean supports(Object handler);

    ModelAndView handle(HttpRequest request, Object handler);
}

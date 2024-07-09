package codesquad.webserver.dispatcher;

import codesquad.webserver.httprequest.HttpRequest;

public interface ViewResolver {
    View resolveView(ModelAndView modelAndView, HttpRequest request);
}

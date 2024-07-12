package codesquad.webserver.dispatcher.handler.mapping;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Component;
import codesquad.webserver.httprequest.HttpRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class SimpleHandlerMapping implements HandlerMapping {

    private final Map<String, Object> handlers = new HashMap<>();
    private final HandlerConfig handlerConfig;

    @Autowired
    public SimpleHandlerMapping(HandlerConfig handlerConfig) {
        this.handlerConfig = handlerConfig;
        this.handlerConfig.initHandlers(this);
    }

    public void addHandler(String path, Object handler) {
        handlers.put(path, handler);
    }

    @Override
    public Object getHandler(HttpRequest request) {
        return handlers.get(request.getRequestLine().path());
    }
}
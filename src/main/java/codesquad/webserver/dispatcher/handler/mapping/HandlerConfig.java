package codesquad.webserver.dispatcher.handler.mapping;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Component;
import codesquad.webserver.dispatcher.requesthandler.*;

@Component
public class HandlerConfig {

    private final UserListHandler userListHandler;
    private final UserCreateRequestHandler userCreateRequestHandler;
    private final HomeRequestHandler homeRequestHandler;
    private final LoginRequestHandler loginRequestHandler;
    private final RegisterRequestHandler registerRequestHandler;
    private final LogoutRequestHandler logoutRequestHandler;
    private final LoginFailHandler loginFailHandler;

    @Autowired
    public HandlerConfig(UserListHandler userListHandler, UserCreateRequestHandler userCreateRequestHandler,
                         HomeRequestHandler homeRequestHandler, LoginRequestHandler loginRequestHandler,
                         RegisterRequestHandler registerRequestHandler, LogoutRequestHandler logoutRequestHandler, LoginFailHandler loginFailHandler) {
        this.userListHandler = userListHandler;
        this.userCreateRequestHandler = userCreateRequestHandler;
        this.homeRequestHandler = homeRequestHandler;
        this.loginRequestHandler = loginRequestHandler;
        this.registerRequestHandler = registerRequestHandler;
        this.logoutRequestHandler = logoutRequestHandler;
        this.loginFailHandler = loginFailHandler;
    }


    public void initHandlers(SimpleHandlerMapping handlerMapping) {
        handlerMapping.addHandler("", homeRequestHandler);
        handlerMapping.addHandler("/", homeRequestHandler);
        handlerMapping.addHandler("/index.html", homeRequestHandler);
        handlerMapping.addHandler("/register", registerRequestHandler);
        handlerMapping.addHandler("/create", userCreateRequestHandler);
        handlerMapping.addHandler("/login", loginRequestHandler);
        handlerMapping.addHandler("/login/login_failed.html", loginFailHandler);
        handlerMapping.addHandler("/logout", logoutRequestHandler);
        handlerMapping.addHandler("/user/list", userListHandler);
    }
}
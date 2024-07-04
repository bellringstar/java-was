package codesquad;

import codesquad.webserver.FileReader;
import codesquad.webserver.WebServer;
import codesquad.webserver.dispatcher.HttpRequestDispatcher;
import codesquad.webserver.httpresponse.HttpResponseWriter;
import codesquad.webserver.parser.BodyParser;
import codesquad.webserver.parser.HeaderParser;
import codesquad.webserver.parser.HttpParser;
import codesquad.webserver.parser.QueryStringParser;
import codesquad.webserver.parser.RequestLineParser;
import codesquad.webserver.requesthandler.HomeRequestHandler;
import codesquad.webserver.requesthandler.RegisterRequestHandler;
import codesquad.webserver.requesthandler.StaticFileHandler;
import codesquad.webserver.requesthandler.UserCreateRequestHandler;
import codesquad.webserver.router.Router;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;


public class Main {

    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        init();
        WebServer webServer = new WebServer(PORT,
                THREAD_POOL_SIZE,
                new HttpRequestDispatcher(new HttpResponseWriter()));
        webServer.start();
    }

    private static void init() {
        Router router = Router.getInstance();
        FileReader reader = new FileReader();
        StaticFileHandler staticFileHandler = new StaticFileHandler(reader);

        router.addRoute("*.css", staticFileHandler);

        router.addRoute("*.js", staticFileHandler);

        router.addRoute("*.png", staticFileHandler);
        router.addRoute("*.jpg", staticFileHandler);
        router.addRoute("*.gif", staticFileHandler);

        router.addRoute("/img/*", staticFileHandler);
        router.addRoute("/favicon.ico", staticFileHandler);

        router.addRoute("/index.html", new HomeRequestHandler(reader));
        router.addRoute("/register.html", new RegisterRequestHandler(reader));
        router.addRoute("/create", new UserCreateRequestHandler(reader));
    }
}

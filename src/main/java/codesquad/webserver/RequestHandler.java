package codesquad.webserver;

import codesquad.webserver.db.UserDatabase;
import codesquad.webserver.db.UserDatabaseFactory;
import codesquad.webserver.model.User;
import codesquad.webserver.parser.BodyParser;
import codesquad.webserver.parser.HeaderParser;
import codesquad.webserver.parser.HttpParser;
import codesquad.webserver.parser.QueryStringParser;
import codesquad.webserver.parser.RequestLineParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final HttpParser httpParser;
    private final FileReader fileReader;
    private final HttpResponseBuilder responseBuilder;

    public RequestHandler() {
        this.httpParser = new HttpParser(new RequestLineParser(), new HeaderParser(), new QueryStringParser(), new BodyParser());
        this.fileReader = new FileReader();
        this.responseBuilder = new HttpResponseBuilder();
    }

    public void handle(Socket clientSocket) throws IOException {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            try {
                HttpRequest request = httpParser.parse(in);
                logger.debug("Received request: " + request.requestLine().method() + " " + request.requestLine().path());

                // create로 시작하는 경로 -> 파라미터에 있는 정보로 유저 객체 생성 후 메인 페이지로 send
                if (request.requestLine().path().startsWith("/create")) {
                    saveUser(request);
                    HttpResponse response = responseBuilder.buildRedirectResponse("/");
                    sendResponse(out, response);
                    return;
                }

                File file = fileReader.read(request.requestLine().path());
                HttpResponse response = responseBuilder.build(file);

                sendResponse(out, response);
            } catch (FileNotFoundException e) {
                logger.error("File not found: " + e.getMessage());
                HttpResponse response = responseBuilder.buildNotFondResponse();

                sendResponse(out, response);
            }

        }
    }

    private void sendResponse(OutputStream out, HttpResponse response) throws IOException {
        out.write(response.getByte());
        out.flush();
    }

    private void saveUser(HttpRequest request) {
        User user = User.of(request.params());
        UserDatabase instance = UserDatabaseFactory.getInstance();
        instance.save(user);
    }
}

package codesquad.webserver;

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
}

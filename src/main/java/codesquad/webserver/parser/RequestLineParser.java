package codesquad.webserver.parser;

import codesquad.webserver.parser.enums.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLineParser {

    private static final Logger logger = LoggerFactory.getLogger(RequestLineParser.class);

    public RequestLine parse(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null) {
            throw new IOException("Invalid request line");
        }

        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        HttpMethod method = HttpMethod.find(parts[0]);
        String path = parts[1];
        String httpVersion = parts[2];

        logger.debug("Request Line : {} {} {}", method, path, httpVersion);

        return new RequestLine(method, path, httpVersion);
    }
}

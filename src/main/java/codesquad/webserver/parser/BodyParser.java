package codesquad.webserver.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BodyParser {

    private static final Logger logger = LoggerFactory.getLogger(BodyParser.class);

    public String parse(BufferedReader in, Map<String, String> headers) throws IOException {
        StringBuilder body = new StringBuilder();
        if (headers.containsKey("Content-Type")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            int read = in.read(buffer, 0, contentLength);
            body.append(buffer, 0, read);
            logger.debug("Body : {}", body);
        }
        return body.toString();
    }
}

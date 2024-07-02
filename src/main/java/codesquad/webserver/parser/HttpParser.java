package codesquad.webserver.parser;

import codesquad.webserver.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpParser.class);
    private final RequestLineParser requestLineParser;
    private final HeaderParser headerParser;
    private final QueryStringParser queryStringParser;
    private final BodyParser bodyParser;

    public HttpParser(RequestLineParser requestLineParser, HeaderParser headerParser,
                      QueryStringParser queryStringParser,
                      BodyParser bodyParser) {
        this.requestLineParser = requestLineParser;
        this.headerParser = headerParser;
        this.queryStringParser = queryStringParser;
        this.bodyParser = bodyParser;
    }

    public HttpRequest parse(BufferedReader in) throws IOException {

        RequestLine requestLine = requestLineParser.parse(in);
        Map<String, String> headers = headerParser.parse(in);
        Map<String, String> params = queryStringParser.parse(requestLine.path());
        String body = bodyParser.parse(in, headers);

        return new HttpRequest(requestLine, headers, body);
    }
}

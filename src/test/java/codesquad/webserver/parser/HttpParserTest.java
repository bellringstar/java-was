package codesquad.webserver.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import codesquad.webserver.HttpRequest;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

public class HttpParserTest {

    private final RequestLineParser requestLineParser = new RequestLineParser();
    private final HeaderParser headerParser = new HeaderParser();
    private final QueryStringParser queryStringParser = new QueryStringParser();
    private final BodyParser bodyParser = new BodyParser();
    private final HttpParser httpParser = new HttpParser(requestLineParser, headerParser, queryStringParser, bodyParser);

    @Test
    @DisplayName("정상적인 GET 요청을 파싱해야 한다")
    public void testParseValidGetRequest() throws Exception {
        // Given
        String request = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Connection: keep-alive\r\n\r\n";
        BufferedReader in = new BufferedReader(new StringReader(request));

        // When
        HttpRequest httpRequest = httpParser.parse(in);

        // Then
        assertEquals("GET", httpRequest.requestLine().method());
        assertEquals("/index.html", httpRequest.requestLine().path());
        assertEquals("HTTP/1.1", httpRequest.requestLine().httpVersion());
        assertEquals("localhost", httpRequest.headers().get("Host"));
        assertEquals("keep-alive", httpRequest.headers().get("Connection"));
        assertEquals(Map.of(), httpRequest.params());
        assertEquals("", httpRequest.body());
    }

    @Test
    @DisplayName("정상적인 GET 요청을 파싱해야 한다 - Query String 포함")
    public void testParseValidGetRequestWithQueryString() throws Exception {
        // Given
        String request = "GET /search?query=java HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Connection: keep-alive\r\n\r\n";
        BufferedReader in = new BufferedReader(new StringReader(request));

        // When
        HttpRequest httpRequest = httpParser.parse(in);

        // Then
        assertEquals("GET", httpRequest.requestLine().method());
        assertEquals("/search?query=java", httpRequest.requestLine().path());
        assertEquals("HTTP/1.1", httpRequest.requestLine().httpVersion());
        assertEquals("localhost", httpRequest.headers().get("Host"));
        assertEquals("keep-alive", httpRequest.headers().get("Connection"));
        assertEquals(Map.of("query", "java"), httpRequest.params());
        assertEquals("", httpRequest.body());
    }

    @Test
    @DisplayName("잘못된 요청 라인을 파싱할 때 IOException을 발생시켜야 한다")
    public void testParseInvalidRequestLine() {
        // Given
        String request = "INVALID_REQUEST_LINE\r\n" +
                "Host: localhost\r\n\r\n";
        BufferedReader in = new BufferedReader(new StringReader(request));

        // When & Then
        IOException exception = org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> {
            httpParser.parse(in);
        });

        assertEquals("Invalid request line: INVALID_REQUEST_LINE", exception.getMessage());
    }
}

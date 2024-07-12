package codesquad.webserver.httprequest;


import codesquad.webserver.parser.RequestLine;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, List<String>> headers;
    private final Map<String, String> params;
    private final String body;

    public HttpRequest(RequestLine requestLine, Map<String, List<String>> headers, Map<String, String> params,
                       String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.params = params;
        this.body = body;
    }

    private static final String SESSION_KEY = "SID";

    public String getSessionIdFromRequest() {
        List<String> cookie = this.headers.get("Cookie");
        if (cookie == null || cookie.isEmpty()) {
            return "";
        }

        Optional<String> sessionId = cookie.stream()
                .flatMap(c -> List.of(c.split(";")).stream())
                .map(String::trim)
                .filter(c -> c.startsWith(SESSION_KEY + "="))
                .map(c -> c.substring((SESSION_KEY + "=").length()))
                .findFirst();

        if (sessionId.isEmpty()) {
            return "";
        }

        return sessionId.get();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        System.out.println("리퀘스트 헤더" + headers);
        StringBuilder httpRequestString = new StringBuilder();

        httpRequestString.append(requestLine.toString()).append("\r\n");

        headers.forEach((key, values) -> values.forEach(
                value -> httpRequestString.append(key).append(": ").append(value).append("\r\n")));

        httpRequestString.append("\r\n");

        if (body != null && !body.isEmpty()) {
            httpRequestString.append(body);
        }

        return httpRequestString.toString();
    }
}

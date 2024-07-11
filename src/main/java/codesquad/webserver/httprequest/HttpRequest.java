package codesquad.webserver.httprequest;

import codesquad.webserver.parser.RequestLine;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record HttpRequest(RequestLine requestLine, Map<String, List<String>> headers, Map<String, String> params, String body) {

    @Override
    public String toString() {
        StringBuilder httpRequestString = new StringBuilder();

        httpRequestString.append(requestLine.toString()).append("\r\n");

        headers.forEach((key, values) -> values.forEach(value -> httpRequestString.append(key).append(": ").append(value).append("\r\n")));

        httpRequestString.append("\r\n");

        if (body != null && !body.isEmpty()) {
            httpRequestString.append(body);
        }

        return httpRequestString.toString();
    }
}

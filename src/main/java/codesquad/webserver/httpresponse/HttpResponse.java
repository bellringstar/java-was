package codesquad.webserver.httpresponse;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final byte[] body;

    // TODO: 헤더와 바디를 분리해 처리
    public HttpResponse(int statusCode, String statusMessage, Map<String, String> headers, byte[] body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>(headers);
        this.body = body;
    }

    public HttpResponse(int statusCode, String statusMessage) {
        this(statusCode, statusMessage, new HashMap<>(), new byte[0]);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public byte[] getBody() {
        return body != null ? body.clone() : null;
    }

    public byte[] generateHttpResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        responseBuilder.append("\r\n");

        byte[] headerBytes = responseBuilder.toString().getBytes();
        byte[] responseBytes = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
        System.arraycopy(body, 0, responseBytes, headerBytes.length, body.length);

        return responseBytes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int statusCode = 200;
        private String statusMessage = "OK";
        private final Map<String, String> headers = new HashMap<>();
        private byte[] body;

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusMessage(String statusMessage) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusCode, statusMessage, headers, body);
        }
    }
}

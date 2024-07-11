package codesquad.webserver.httpresponse;

import codesquad.webserver.db.cookie.HttpCookie;
import codesquad.webserver.filereader.FileReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseBuilder {
    private static final Logger log = LoggerFactory.getLogger(HttpResponseBuilder.class);
    public static final Map<String, String> MIME_TYPES;

    static {
        Map<String, String> mimeTypes = new HashMap<>();
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("ico", "image/x-icon");
        MIME_TYPES = Collections.unmodifiableMap(mimeTypes);
    }

    private int statusCode;
    private String statusMessage;
    private final Map<String, String> headers;
    private final List<HttpCookie> cookies;
    private byte[] body;

    public HttpResponseBuilder() {
        this.statusCode = 200;
        this.statusMessage = "OK";
        this.headers = new HashMap<>();
        this.cookies = new ArrayList<>();
        this.body = new byte[0];
    }

    public HttpResponseBuilder statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponseBuilder statusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public HttpResponseBuilder header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public HttpResponseBuilder cookie(HttpCookie cookie) {
        this.cookies.add(cookie);
        return this;
    }

    public HttpResponseBuilder cookies(List<HttpCookie> cookies) {
        this.cookies.addAll(cookies);
        return this;
    }

    public HttpResponseBuilder body(byte[] body) {
        this.body = body;
        return this;
    }

    public HttpResponse build() {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(statusCode);
        response.setStatusMessage(statusMessage);
        headers.forEach(response::addHeader);
        cookies.forEach(response::addCookie);
        response.setBody(body);
        return response;
    }

    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder().statusCode(200).statusMessage("OK");
    }

    public static HttpResponseBuilder notFound() {
        return new HttpResponseBuilder().statusCode(404).statusMessage("Not Found");
    }

    public static HttpResponseBuilder redirect(String location) {
        return new HttpResponseBuilder()
                .statusCode(302)
                .statusMessage("Found")
                .header("Location", location);
    }

    public static HttpResponseBuilder methodNotAllowed() {
        return new HttpResponseBuilder().statusCode(405).statusMessage("Method Not Allowed");
    }

    public static HttpResponseBuilder serverError() {
        return new HttpResponseBuilder().statusCode(500).statusMessage("Internal Server Error");
    }

    public static HttpResponse buildFromFile(FileReader.FileResource resource) throws IOException {
        byte[] body = readAllBytes(resource.getInputStream());
        String contentType = getContentType(resource.getFileName());

        return ok()
                .header("Content-Type", contentType)
                .header("Content-Length", String.valueOf(body.length))
                .body(body)
                .build();
    }

    private static String getContentType(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1).toLowerCase();
            return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
        }
        return "application/octet-stream";
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
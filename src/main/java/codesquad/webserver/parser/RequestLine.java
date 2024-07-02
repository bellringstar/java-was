package codesquad.webserver.parser;

public record RequestLine(String method, String path, String httpVersion) {

}

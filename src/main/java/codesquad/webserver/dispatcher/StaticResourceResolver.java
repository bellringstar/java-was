package codesquad.webserver.dispatcher;

public interface StaticResourceResolver {

    boolean isStaticResource(String path);
}

package codesquad.webserver;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.webserver.dispatcher.DispatcherServlet;
import codesquad.webserver.dispatcher.handler.adater.SimpleHandlerAdapter;
import codesquad.webserver.dispatcher.handler.mapping.SimpleHandlerMapping;
import codesquad.webserver.dispatcher.view.resolver.DefaultViewResolver;
import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.parser.RequestLine;
import codesquad.webserver.parser.enums.HttpMethod;
import codesquad.webserver.staticresouce.DefaultStaticResourceResolver;
import codesquad.webserver.staticresouce.StaticResourceHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserRegistrationIntegrationTest {

    private DispatcherServlet dispatcherServlet;

    @BeforeEach
    void setUp() {
        dispatcherServlet = new DispatcherServlet(new SimpleHandlerMapping(
                new StaticResourceHandler(new FileReader(), new DefaultStaticResourceResolver()), new FileReader()),
                new SimpleHandlerAdapter(), new DefaultViewResolver());
    }

    @Test
    @DisplayName("post 로 성공적으로 회원가입")
    void testSuccessfulPostRegistration() throws Exception {
        // Given
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/x-www-form-urlencoded"));

        String body = "userId=testuser&password=password123&name=Test User";
        RequestLine requestLine = new RequestLine(HttpMethod.POST, "/create", "/create", "HTTP/1.1");
        HttpRequest request = new HttpRequest(requestLine, headers, new HashMap<>(), body);

        // When
        HttpResponse response = dispatcherServlet.service(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(302);
        assertThat(response.getHeaderValue("Location")).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("GET으로 회원가입 실패")
    void testFailedGetRegistration() throws Exception {
        // Given
        RequestLine requestLine = new RequestLine(HttpMethod.GET, "/create",
                "/create?userId=testuser&password=password123&name=Test User", "HTTP/1.1");
        HttpRequest request = new HttpRequest(requestLine, new HashMap<>(), new HashMap<>(), "");

        // When
        HttpResponse response = dispatcherServlet.service(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(405); // Method Not Allowed
    }
}
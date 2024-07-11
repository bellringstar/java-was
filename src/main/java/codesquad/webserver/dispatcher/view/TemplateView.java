package codesquad.webserver.dispatcher.view;

import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import codesquad.webserver.session.cookie.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateView implements View {

    private static final Logger logger = LoggerFactory.getLogger(TemplateView.class);
    private final String viewName;
    private final Map<String, Object> model;

    public TemplateView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    @Override
    public HttpResponse render(Map<String, ?> model) {
        logger.info("템플릿 뷰 동작");

        // 상태 코드 추출
        int statusCode = model.containsKey("statusCode") ? (int) model.get("statusCode") : 200;

        // 헤더 추출
        List<HttpResponse.Header> headers =
                model.containsKey("headers") ? (List<HttpResponse.Header>) model.get("headers") : new ArrayList<>();

        // 쿠키 추출
        List<HttpCookie> cookies =
                model.containsKey("cookies") ? (List<HttpCookie>) model.get("cookies") : new ArrayList<>();

        // 바디 추출
        byte[] body = model.containsKey("body") ? (byte[]) model.get("body") : new byte[0];

//        // 템플릿 엔진을 사용하여 뷰 렌더링
//        String renderedContent = renderTemplate(viewName, this.model);
//
//        // 렌더링된 내용을 바디에 설정
//        body = renderedContent.getBytes();

        HttpResponseBuilder responseBuilder = HttpResponseBuilder.ok()
                .statusCode(statusCode)
                .cookies(cookies)
                .body(body);

        headers.forEach(header -> responseBuilder.header(header.getName(), header.getValue()));

        return responseBuilder.build();
    }

    private String renderTemplate(String viewName, Map<String, Object> model) {

        return "Rendered content for view: " + viewName + " with model: " + model;
    }
}

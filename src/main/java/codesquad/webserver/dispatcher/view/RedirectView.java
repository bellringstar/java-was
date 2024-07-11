package codesquad.webserver.dispatcher.view;

import codesquad.webserver.session.cookie.HttpCookie;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedirectView implements View {

    private final String url;
    private final Map<String, Object> model;

    public RedirectView(String url, Map<String, Object> model) {
        this.url = url;
        this.model = model;
    }

    @Override
    public HttpResponse render(Map<String, ?> model) {
        List<HttpCookie> cookies = extractCookies(model);
        return cookies != null && !cookies.isEmpty() ? HttpResponseBuilder.redirect(url).cookies(cookies).build()
                : HttpResponseBuilder.redirect(url).build();
    }

    private List<HttpCookie> extractCookies(Map<String, ?> model) {
        List<HttpCookie> cookies = new ArrayList<>();
        if (model.containsKey("cookies")) {
            Object cookiesObj = model.get("cookies");
            if (cookiesObj instanceof List) {
                for (Object cookie : (List) cookiesObj) {
                    if (cookie instanceof HttpCookie) {
                        cookies.add((HttpCookie) cookie);
                    }
                }
            }
        }
        return cookies;
    }


}

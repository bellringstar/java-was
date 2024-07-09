package codesquad.webserver.dispatcher;

import codesquad.webserver.annotation.Component;
import codesquad.webserver.httprequest.HttpRequest;
import java.util.Map;

@Component
public class DefaultViewResolver implements ViewResolver {
    @Override
    public View resolveView(ModelAndView modelAndView, HttpRequest request) {
        String viewName = modelAndView.getViewName();
        Map<String, Object> model = modelAndView.getModel();

        Map<String, String> headers = request.headers();

        if (viewName.startsWith("redirect:")) {
            return new RedirectView(viewName.substring(9));
        } else if (viewName.equals("jsonView")) {
            return new JsonView(model);
        } else {
            return new TemplateView(viewName, model);
        }
    }
}

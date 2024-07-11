package codesquad.webserver.dispatcher.view.resolver;

import codesquad.webserver.annotation.Component;
import codesquad.webserver.dispatcher.view.JsonView;
import codesquad.webserver.dispatcher.view.ExceptionView;
import codesquad.webserver.dispatcher.view.ModelAndView;
import codesquad.webserver.dispatcher.view.RedirectView;
import codesquad.webserver.dispatcher.view.TemplateView;
import codesquad.webserver.dispatcher.view.View;
import codesquad.webserver.httprequest.HttpRequest;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DefaultViewResolver implements ViewResolver {
    private static final Logger log = LoggerFactory.getLogger(DefaultViewResolver.class);

    @Override
    public View resolveView(ModelAndView modelAndView, HttpRequest request) {
        String viewName = modelAndView.getViewName();
        Map<String, Object> model = modelAndView.getModel();

        Map<String, List<String>> headers = request.headers();
        if (viewName.startsWith("redirect:")) {
            return new RedirectView(viewName.substring(9), model);
        } else if (viewName.equals("jsonView")) {
            return new JsonView(model);
        } else if (viewName.equals("templateView")){
            return new TemplateView(viewName, model);
        } else {
            return new ExceptionView(viewName, model);
        }
    }
}

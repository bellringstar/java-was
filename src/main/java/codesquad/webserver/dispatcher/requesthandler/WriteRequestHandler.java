package codesquad.webserver.dispatcher.requesthandler;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.dispatcher.view.ModelAndView;
import codesquad.webserver.dispatcher.view.ModelKey;
import codesquad.webserver.dispatcher.view.ViewName;
import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.filereader.FileReader.FileResource;
import codesquad.webserver.httprequest.HttpRequest;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(path = "/write")
public class WriteRequestHandler extends AbstractRequestHandler {

    private static final String PATH = "/article/write.html";
    private static final Logger logger = LoggerFactory.getLogger(WriteRequestHandler.class);

    @Autowired
    public WriteRequestHandler(FileReader fileReader) {
        super(fileReader);
    }

    @Override
    protected ModelAndView handleGet(HttpRequest request) {
        try {
            FileResource fileResource = fileReader.read(PATH);
            String content = fileResource.readFileContent();
            ModelAndView mv = new ModelAndView(ViewName.TEMPLATE_VIEW);
            mv.addAttribute(ModelKey.CONTENT, content);
            return mv;

        } catch (IOException e) {
            logger.error("Error reading file: {}", e.getMessage());
            return new ModelAndView(ViewName.EXCEPTION_VIEW)
                    .addAttribute(ModelKey.STATUS_CODE, 404)
                    .addAttribute(ModelKey.ERROR_MESSAGE, "File not found");

        } catch (Exception e) {
            logger.error("Unhandled exception: {}", e.getMessage());
            return new ModelAndView(ViewName.EXCEPTION_VIEW)
                    .addAttribute(ModelKey.STATUS_CODE, 500)
                    .addAttribute(ModelKey.ERROR_MESSAGE, "Internal server error");
        }
    }

    @Override
    protected ModelAndView handlePost(HttpRequest request) {
        return super.handlePost(request);
    }
}

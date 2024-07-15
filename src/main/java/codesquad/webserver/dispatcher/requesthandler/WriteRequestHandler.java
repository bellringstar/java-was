package codesquad.webserver.dispatcher.requesthandler;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.db.article.Article;
import codesquad.webserver.db.article.ArticleDatabase;
import codesquad.webserver.db.article.Image;
import codesquad.webserver.dispatcher.view.ModelAndView;
import codesquad.webserver.dispatcher.view.ModelKey;
import codesquad.webserver.dispatcher.view.ViewName;
import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.filereader.FileReader.FileResource;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httprequest.HttpRequest.FileItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(path = "/write")
public class WriteRequestHandler extends AbstractRequestHandler {

    private static final String PATH = "/article/write.html";
    private static final Logger logger = LoggerFactory.getLogger(WriteRequestHandler.class);
    private static final String UPLOAD_DIR_NAME = "uploads";
    private final String uploadDir;

    private final ArticleDatabase articleDatabase;

    @Autowired
    public WriteRequestHandler(FileReader fileReader, ArticleDatabase articleDatabase) {
        super(fileReader);
        this.articleDatabase = articleDatabase;
        this.uploadDir = getProjectUploadPath();
        logger.info("Upload directory set to: {}", this.uploadDir);
    }

    private String getProjectUploadPath() {
        String projectDir = System.getProperty("user.dir");
        return Paths.get(projectDir, UPLOAD_DIR_NAME).toString();
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
        Map<String, String> multipartFields = request.getMultipartFields();
        Map<String, FileItem> multipartFiles = request.getMultipartFiles();

        String content = multipartFields.get("content");
        FileItem imageFile = multipartFiles.get("image");

        try {
            Article article = new Article(content);

            if (imageFile != null) {
                String imagePath = saveImageToLocal(imageFile);
                Image image = new Image(null, imagePath, null);
                article.addImage(image);
            }

            articleDatabase.save(article);

            ModelAndView mv = new ModelAndView(ViewName.REDIRECT_VIEW);
            mv.addAttribute(ModelKey.REDIRECT_URL, "/");
            return mv;

        } catch (IOException e) {
            logger.error("Error saving image: {}", e.getMessage());
            return new ModelAndView(ViewName.EXCEPTION_VIEW)
                    .addAttribute(ModelKey.STATUS_CODE, 500)
                    .addAttribute(ModelKey.ERROR_MESSAGE, "Error saving image");
        } catch (Exception e) {
            logger.error("Error saving article: {}", e.getMessage());
            return new ModelAndView(ViewName.EXCEPTION_VIEW)
                    .addAttribute(ModelKey.STATUS_CODE, 500)
                    .addAttribute(ModelKey.ERROR_MESSAGE, "Error saving article");
        }
    }

    private String saveImageToLocal(FileItem imageFile) throws IOException {
        String fileName = UUID.randomUUID() + "_" + imageFile.getFilename();
        File uploadDirFile = new File(uploadDir);

        if (!uploadDirFile.exists()) {
            if (!uploadDirFile.mkdirs()) {
                logger.error("Failed to create upload directory: {}", uploadDir);
                throw new IOException("Failed to create upload directory: " + uploadDir);
            }
        }

        File file = new File(uploadDirFile, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageFile.getContent());
        } catch (IOException e) {
            logger.error("Error writing file: {}", e.getMessage(), e);
            throw e;
        }

        return file.getAbsolutePath();
    }
}
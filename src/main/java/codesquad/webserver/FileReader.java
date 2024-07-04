package codesquad.webserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader {
    private static Logger logger = LoggerFactory.getLogger(FileReader.class);
    private static final String STATIC_DIRECTORY = "static";
    private static final String BASE_FILE = "index.html";

    public File read(String requestPath) throws FileNotFoundException {
        String fileName = requestPath.equals("/") ? "index.html" : requestPath.substring(1);
        URL resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + "/" + fileName);

        logger.info("파일명 : " + fileName);
        if (resource == null) {
            logger.error("File not found: " + fileName);
            throw new FileNotFoundException("File not found: " + fileName);
        }

        File file = new File(resource.getFile());

        if (!file.isFile()) {
            file = new File(resource.getPath() + "/" + BASE_FILE);
        }

        if (!file.exists()) {
            // todo: REST API요청일지도?
            logger.error("File not found: " + fileName);
            throw new FileNotFoundException("File not found: " + fileName);
        }

        logger.info("File found: " + file.getPath());
        return file;
    }
}

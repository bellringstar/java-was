package codesquad.webserver.dispatcher.requesthandler;

import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import java.io.IOException;

public class RegisterRequestHandler extends AbstractRequestHandler {

    private static final String FILE_PATH = "/registration/index.html";

    public RegisterRequestHandler(FileReader fileReader) {
        super(fileReader);
    }

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        try {
            FileReader.FileResource file = fileReader.read(FILE_PATH);
            return HttpResponseBuilder.buildFromFile(file);
        } catch (IOException e) {
            return HttpResponseBuilder.notFound().build();
        }
    }
}

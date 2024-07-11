package codesquad.webserver.dispatcher.requesthandler;

import codesquad.webserver.filereader.FileReader;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import java.io.IOException;

public class StaticFileHandler extends AbstractRequestHandler {

    public StaticFileHandler(FileReader fileReader) {
        super(fileReader);
    }

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        try {
            FileReader.FileResource file = fileReader.read(request.requestLine().path());
            return HttpResponseBuilder.buildFromFile(file);
        } catch (IOException e) {
            return HttpResponseBuilder.notFound().build();
        }
    }
}

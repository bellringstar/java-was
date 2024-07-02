package codesquad.webserver.parser;

import java.util.HashMap;
import java.util.Map;

public class QueryStringParser {

    public Map<String, String> parse(String path) {
        String[] urlParams = path.split("\\?");
        if (urlParams.length == 2) {
            String param = path.split("\\?")[1];
            Map<String, String> params = new HashMap<>();
            for (String info : param.split("&")) {
                String key = info.split("=")[0].trim();
                String value = info.split("=")[1].trim();
                params.put(key, value);
            }
            return params;
        }
        return null;
    }
}

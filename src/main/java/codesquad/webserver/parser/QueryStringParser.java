package codesquad.webserver.parser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QueryStringParser {

    public Map<String, String> parse(String path) {
        String[] urlParams = path.split("\\?");
        if (urlParams.length == 2) {
            String param = path.split("\\?")[1];
            Map<String, String> params = new HashMap<>();
            for (String info : param.split("&")) {
                String[] keyValue = info.split("=", 2);
                if (keyValue.length == 2) {
                    String key = URLDecoder.decode(keyValue[0].trim(), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1].trim(), StandardCharsets.UTF_8);
                    params.put(key, value);
                }
            }
            return params;
        }
        return Map.of();
    }
}

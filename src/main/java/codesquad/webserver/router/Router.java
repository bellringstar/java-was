package codesquad.webserver.router;

import codesquad.webserver.requesthandler.RequestHandler;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Router {

    private static final Map<String, RequestHandler> ROUTES = new ConcurrentHashMap<>();

    private Router() {}

    private static class Holder {
        private static final Router INSTANCE = new Router();
    }

    public static Router getInstance() {
        return Holder.INSTANCE;
    }

    public void addRoute(String path, RequestHandler handler) {
        //TODO: path, hadler null 체크 추가
        if (ROUTES.putIfAbsent(path, handler) != null) {
            throw new IllegalStateException("이미 존재하는 경로입니다.: " + path);
        }
    }

    public RequestHandler getHandler(String path) {
        // 이것만 스레드 안정성 고려 필요한 메서드
        return Optional.ofNullable(ROUTES.get(path))
                .orElseThrow(() -> new IllegalArgumentException("해당 경로의 핸들러가 존재하지 않습니다."));
    }

    public void removeRoute(String path) {
        ROUTES.remove(path);
    }
}

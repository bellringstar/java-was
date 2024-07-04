package codesquad.webserver;

import codesquad.webserver.dispatcher.HttpRequestDispatcher;
import codesquad.webserver.parser.HttpParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private final ExecutorService threadPool;
    private final int port;
    private final HttpRequestDispatcher dispatcher;
    private final HttpParser parser;

    public WebServer(int port, int poolSize, HttpRequestDispatcher dispatcher, HttpParser parser) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.port = port;
        this.dispatcher = dispatcher;
        this.parser = parser;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true); // 작업 중 껐다 켰다 하는 상황에 발생하는 포트 사용중 발생을 피하기 위해 임시 작성
            logger.info("Starting web server on port {}", port);
            while (!Thread.currentThread().isInterrupted()) {
                Socket client = serverSocket.accept();
                logger.info("연결");
                threadPool.execute(() -> handleConnection(client));
            }
        } catch (IOException e) {
            logger.error("Error while starting web server", e);
        }
    }

    private void handleConnection(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream outputStream = client.getOutputStream()) {
            HttpRequest request = parser.parse(in);
            dispatcher.dispatch(request, outputStream);
        } catch (Exception e) {
            logger.error("Error while handling request", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("Error while closing connection", e);
            }
        }
    }
}

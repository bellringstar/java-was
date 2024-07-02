package codesquad;

import codesquad.webserver.WebServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {

    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) throws IOException {
        WebServer webServer = new WebServer(PORT, THREAD_POOL_SIZE);
        webServer.start();
    }
}

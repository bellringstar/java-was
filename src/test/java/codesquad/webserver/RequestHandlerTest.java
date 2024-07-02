package codesquad.webserver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RequestHandlerTest {

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private int port = 8080;

    @BeforeEach
    public void setUp() throws IOException {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(10);
    }

    @AfterEach
    public void tearDown() throws IOException {
        serverSocket.close();
        executorService.shutdown();
    }

    @Test
    public void testHandleCreateUserRequest() throws IOException {
        // Start a thread to handle the server socket
        executorService.submit(() -> {
            try {
                Socket clientSocket = serverSocket.accept();
                new RequestHandler().handle(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Create a client socket to send a request
        try (Socket socket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send a request to create a user
            out.println("GET /create?userId=testUser&password=testPass&name=TestName&email=test@example.com HTTP/1.1");
            out.println("Host: localhost");
            out.println();
            out.flush();

            // Read the response
            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine).append("\n");
                if (responseLine.isEmpty()) {
                    break;
                }
            }

            // Validate the response
            assertTrue(response.toString().contains("HTTP/1.1 302 Found"));
            assertTrue(response.toString().contains("Location: /"));
        }
    }

    @Test
    public void testHandleFileNotFound() throws IOException {
        // Start a thread to handle the server socket
        executorService.submit(() -> {
            try {
                Socket clientSocket = serverSocket.accept();
                new RequestHandler().handle(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Create a client socket to send a request
        try (Socket socket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send a request for a non-existent file
            out.println("GET /nonexistentfile HTTP/1.1");
            out.println("Host: localhost");
            out.println();
            out.flush();

            // Read the response
            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine).append("\n");
                if (responseLine.isEmpty()) {
                    break;
                }
            }

            // Validate the response
            assertTrue(response.toString().contains("HTTP/1.1 404 Not Found"));
        }
    }
}

package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoryHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(HistoryHandler.class.getName());
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleGet(exchange);
            } else {
                logger.warning("Unsupported HTTP method: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error handling request", e);
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistory();

        if (history.isEmpty()) {
            logger.info("No history available, returning 204 No Content");
            exchange.sendResponseHeaders(204, -1);
        } else {
            String response = gson.toJson(history);
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
                logger.info("History sent successfully with " + responseBytes.length + " bytes");
            }
        }
    }
}

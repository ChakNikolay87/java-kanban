package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.NoSuchElementException;

public class EpicHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(EpicHandler.class.getName());
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            logger.info("Received request: " + method + " " + path);

            try (exchange) {
                switch (method) {
                    case "GET" -> handleGet(exchange);
                    case "POST" -> handlePost(exchange);
                    case "DELETE" -> handleDelete(exchange, path);
                    default -> sendNotFound(exchange);
                }
            } catch (Exception e) {
                logger.severe("Error handling request: " + e.getMessage());
                sendInternalError(exchange, e.getMessage());
            }
        }
    }


    private void handleGet(HttpExchange exchange) throws IOException {
        logger.info("Handling GET request for epics");
        sendText(exchange, gson.toJson(taskManager.getEpics()));
        logger.info("Epics sent successfully.");
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(isr, Epic.class);

        logger.info("Handling POST request for creating epic");
        taskManager.addEpic(epic);
        logger.info("Epic created successfully with ID: " + epic.getId());
        exchange.sendResponseHeaders(201, -1);
        exchange.close();
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (!path.startsWith("/epics/") || pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        String epicIdStr = pathParts[2];

        if (!epicIdStr.matches("\\d+")) {
            sendInternalError(exchange, "Invalid epicId: %s".formatted(epicIdStr));
            return;
        }

        int epicId = Integer.parseInt(epicIdStr);

        taskManager.getEpic(epicId)
                .orElseThrow(() -> new NoSuchElementException("Epic with ID " + epicId + " not found"));

        taskManager.deleteEpic(epicId);
        logger.info("Epic deleted successfully: ID " + epicId);

        exchange.sendResponseHeaders(200, -1);
        exchange.close();
    }

}

package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Logger;

public class EpicHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(EpicHandler.class.getName());
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = GsonUtil.createGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            logger.info("Received request: " + method + " " + path);

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

    private void handleGet(HttpExchange exchange) throws IOException {
        // Возвращаем все эпики
        logger.info("Handling GET request for epics");
        sendText(exchange, gson.toJson(taskManager.getEpics()));
        logger.info("Epics sent successfully.");
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        // Чтение тела запроса
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(isr, Epic.class);

        // Добавление эпика
        logger.info("Handling POST request for creating epic");
        taskManager.addEpic(epic);
        logger.info("Epic created successfully with ID: " + epic.getId());
        exchange.sendResponseHeaders(201, -1); // Статус 201 Created
        exchange.close();
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        // Извлечение ID эпика из пути
        if (path.startsWith("/epics/")) {
            String[] pathParts = path.split("/");
            if (pathParts.length == 3) {
                try {
                    int epicId = Integer.parseInt(pathParts[2]);

                    // Удаление эпика, если он существует
                    Optional<Epic> epic = taskManager.getEpic(epicId);
                    if (epic.isPresent()) {
                        taskManager.deleteEpic(epicId);
                        logger.info("Epic deleted successfully: ID " + epicId);
                        exchange.sendResponseHeaders(200, -1); // Статус 200 OK
                    } else {
                        logger.warning("Epic with ID " + epicId + " not found");
                        sendNotFound(exchange); // Если эпик не найден
                    }
                } catch (NumberFormatException e) {
                    logger.severe("Invalid epic ID format: " + e.getMessage());
                    sendInternalError(exchange, "Invalid epic ID format");
                }
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
        exchange.close();
    }
}

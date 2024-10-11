package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubtaskHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(SubtaskHandler.class.getName());
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = GsonUtil.createGson(); // Используем кастомный Gson с адаптерами для Duration и LocalDateTime
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        logger.info("Received request: " + method + " " + path);

        try {
            switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange, path);
                default -> {
                    logger.warning("Invalid method: " + method);
                    sendNotFound(exchange);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing request", e);
            sendInternalError(exchange, "Internal server error: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            logger.info("Handling GET request for subtasks");
            String response = gson.toJson(taskManager.getSubtasks());
            sendText(exchange, response);
            logger.info("Subtasks sent successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while handling GET request", e);
            sendInternalError(exchange, e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        logger.info("Handling POST request for creating subtask");
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Subtask subtask = gson.fromJson(isr, Subtask.class);

            logger.info("Subtask received: " + subtask);

            // Проверка существования эпика, к которому принадлежит подзадача
            Optional<Epic> epic = taskManager.getEpic(subtask.getSubtasksEpicId());
            if (epic.isEmpty()) {
                String errorMessage = "Epic with ID " + subtask.getSubtasksEpicId() + " not found";
                logger.warning(errorMessage);
                sendInternalError(exchange, errorMessage);
                return;
            }

            // Добавление подзадачи
            taskManager.addSubtask(subtask);
            exchange.sendResponseHeaders(201, -1); // Статус 201 Created
            logger.info("Subtask created successfully with ID: " + subtask.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while handling POST request", e);
            sendInternalError(exchange, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        logger.info("Handling DELETE request for subtasks");
        if (path.startsWith("/subtasks/")) {
            String[] pathParts = path.split("/");
            if (pathParts.length == 3) {
                try {
                    int subtaskId = Integer.parseInt(pathParts[2]);
                    logger.info("Attempting to delete subtask with ID: " + subtaskId);

                    // Удаление подзадачи, если она существует
                    Optional<Subtask> subtask = taskManager.getSubtaskById(subtaskId);
                    if (subtask.isPresent()) {
                        taskManager.deleteSubtask(subtaskId);
                        exchange.sendResponseHeaders(200, -1); // Статус 200 OK
                        logger.info("Subtask deleted successfully: ID " + subtaskId);
                    } else {
                        logger.warning("Subtask with ID " + subtaskId + " not found");
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Invalid subtask ID format", e);
                    sendInternalError(exchange, "Invalid subtask ID format");
                }
            } else {
                logger.warning("Invalid DELETE request format");
                sendNotFound(exchange);
            }
        } else {
            logger.warning("Invalid DELETE request path: " + path);
            sendNotFound(exchange);
        }
        exchange.close();
    }
}

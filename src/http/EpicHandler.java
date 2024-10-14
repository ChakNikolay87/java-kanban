package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
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
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        logger.info("Received request: " + method + " " + path);

        switch (method) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "DELETE" -> handleDeleteEpic(exchange, path);
            default -> sendNotFound(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        logger.info("Handling GET request for epics");
        sendText(exchange, gson.toJson(taskManager.getEpics()));
        logger.info("Epics sent successfully.");
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        logger.info("Handling POST request for creating epic");
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Epic epic = gson.fromJson(isr, Epic.class);

            if (epic == null || epic.getName() == null || epic.getDescription() == null) {
                logger.warning("Invalid epic format or missing required fields");
                sendInternalError(exchange, "Invalid epic format or missing required fields");
                return;
            }

            taskManager.addEpic(epic);
            logger.info("Epic created successfully with ID: " + epic.getId());
            exchange.sendResponseHeaders(201, -1); // Возвращаем статус 201 Created
        } catch (Exception e) {
            logger.severe("Error while processing epic creation: " + e.getMessage());
            sendInternalError(exchange, "Error while processing epic creation: " + e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, String path) throws IOException {
        handleDelete(exchange, path,
                taskManager::getEpic,
                taskManager::deleteEpic);
    }

    private void handleDelete(HttpExchange exchange, String path,
                              Function<Integer, Optional<?>> getTaskById,
                              Consumer<Integer> deleteTask) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length != 3 || !pathParts[2].matches("\\d+")) {
            logger.warning("Invalid epic ID format or request path");
            sendNotFound(exchange);
            return;
        }

        int taskId = Integer.parseInt(pathParts[2]);

        try {
            getTaskById.apply(taskId)
                    .orElseThrow(() -> new NoSuchElementException("Epic with ID " + taskId + " not found"));

            deleteTask.accept(taskId);
            logger.info("Epic deleted successfully: ID " + taskId);
            exchange.sendResponseHeaders(200, -1);
        } catch (NoSuchElementException e) {
            logger.warning(e.getMessage());
            sendNotFound(exchange);
        } catch (Exception e) {
            logger.severe("Error deleting epic: " + e.getMessage());
            sendInternalError(exchange, e.getMessage());
        } finally {
            exchange.close();
        }
    }
}

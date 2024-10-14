package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(TaskHandler.class.getName());
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = GsonUtil.createGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        logger.info("Received request: " + method + " " + path);

        try {
            switch (method) {
                case "GET" -> handleGet(exchange, path);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDeleteTask(exchange, path);
                default -> {
                    logger.warning("Unsupported method: " + method);
                    sendNotFound(exchange);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error handling request", e);
            sendInternalError(exchange, "Internal server error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            logger.info("Handling GET request for tasks");
            sendText(exchange, gson.toJson(taskManager.getTasks()));
            logger.info("Tasks sent successfully.");
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        logger.info("Handling POST request for creating task");
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Task task = gson.fromJson(isr, Task.class);

            if (task == null || task.getName() == null || task.getDescription() == null) {
                logger.warning("Invalid task format or missing required fields");
                sendInternalError(exchange, "Invalid task format or missing required fields");
                return;
            }

            if (isOverlapping(task)) {
                String errorMessage = "Task time conflicts with existing tasks.";
                logger.warning(errorMessage);
                exchange.sendResponseHeaders(406, -1);
                return;
            }

            taskManager.addTask(task);
            logger.info("Task created successfully with ID: " + task.getId());
            exchange.sendResponseHeaders(201, -1);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while processing task creation", e);
            sendInternalError(exchange, "Error while processing task creation: " + e.getMessage());
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String path) throws IOException {
        handleDelete(exchange, path,
                taskManager::getTask,
                taskManager::deleteTask);
    }

    private void handleDelete(HttpExchange exchange, String path,
                              Function<Integer, Optional<?>> getTaskById,
                              Consumer<Integer> deleteTask) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length != 3 || !pathParts[2].matches("\\d+")) {
            logger.warning("Invalid task ID format or request path");
            sendNotFound(exchange);
            return;
        }

        int taskId = Integer.parseInt(pathParts[2]);

        try {
            getTaskById.apply(taskId)
                    .orElseThrow(() -> new NoSuchElementException("Task with ID " + taskId + " not found"));

            deleteTask.accept(taskId);
            logger.info("Task deleted successfully: ID " + taskId);
            exchange.sendResponseHeaders(200, -1);
        } catch (NoSuchElementException e) {
            logger.warning(e.getMessage());
            sendNotFound(exchange);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting task", e);
            sendInternalError(exchange, "Error deleting task: " + e.getMessage());
        }
    }

    private boolean isOverlapping(Task newTask) {
        return taskManager.getPrioritizedTasks().stream()
                .anyMatch(existingTask ->
                        existingTask.getStartTime() != null && existingTask.getEndTime() != null &&
                                newTask.getStartTime() != null && newTask.getEndTime() != null &&
                                !(existingTask.getEndTime().isBefore(newTask.getStartTime()) ||
                                        existingTask.getStartTime().isAfter(newTask.getEndTime()))
                );
    }
}

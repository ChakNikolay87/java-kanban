package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import http.BaseHttpHandler;
import http.GsonUtil;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = GsonUtil.createGson(); // Используем кастомный Gson с адаптерами
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET" -> handleGet(exchange, path);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange, path);
                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Логируем полное сообщение об ошибке
            sendInternalError(exchange, "Internal server error: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            sendText(exchange, gson.toJson(taskManager.getTasks()));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Task task;
        try {
            task = gson.fromJson(isr, Task.class);

            // Проверяем, что задача не null и имеет необходимые поля
            if (task == null || task.getName() == null || task.getDescription() == null) {
                sendInternalError(exchange, "Invalid task format or missing required fields");
                return;
            }

            taskManager.addTask(task);
            exchange.sendResponseHeaders(201, -1); // Возвращаем статус 201 Created
        } catch (Exception e) {
            e.printStackTrace(); // Логируем ошибку
            sendInternalError(exchange, "Error while processing task creation: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/tasks/")) {
            String[] pathParts = path.split("/");
            if (pathParts.length == 3) {
                try {
                    int taskId = Integer.parseInt(pathParts[2]);
                    Optional<Task> task = taskManager.getTask(taskId);
                    if (task.isPresent()) {
                        taskManager.deleteTask(taskId);
                        exchange.sendResponseHeaders(200, -1); // Успешное удаление
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    sendInternalError(exchange, "Invalid task ID format");
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

package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import http.BaseHttpHandler;
import http.GsonUtil;
import managers.TaskManager;

import java.io.IOException;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = GsonUtil.createGson();  // Use the custom Gson with adapters
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, response);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange, "Internal server error: " + e.getMessage());
        }
    }
}

package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(PrioritizedTasksHandler.class.getName());
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = GsonUtil.createGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                logger.info("Received GET request for prioritized tasks");

                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, response);

                logger.info("Prioritized tasks sent successfully");
            } else {
                logger.warning("Unsupported HTTP method: " + exchange.getRequestMethod());
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error handling request", e);
            sendInternalError(exchange, "Internal server error: " + e.getMessage());
        }
    }
}

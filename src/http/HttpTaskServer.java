package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final TaskManager taskManager;
    private final HttpServer server;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = GsonUtil.createGson(); // Инициализация Gson с адаптерами
        server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Инициализация контекстов для различных обработчиков
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));

        // Другие контексты можно добавить аналогичным образом
    }

    public void start() {
        server.start();
        System.out.println("Server started on port 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }



    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }
}

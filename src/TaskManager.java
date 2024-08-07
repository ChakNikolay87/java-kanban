import java.util.List;

public interface TaskManager {
    Task createTask(String name, String description);

    Epic createEpic(String name, String description);

    Subtask createSubtask(String name, String description, int epicId);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void deleteAllTasks();

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();
}

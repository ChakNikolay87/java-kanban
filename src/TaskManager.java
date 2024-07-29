import java.util.*;

public class TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    public Task createTask(String name, String description) {
        Task task = new Task(idCounter++, name, description);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(idCounter++, name, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(String name, String description, int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        Subtask subtask = new Subtask(idCounter++, name, description, epicId);
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return true;
        } else if (subtasks.containsKey(task.getId())) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                epic.addSubtask(subtask);
            }
            return true;
        } else if (epics.containsKey(task.getId())) {
            Epic epic = (Epic) task;
            epics.put(epic.getId(), epic);
            return true;
        }
        return false;
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            if (subtask != null) {
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.removeSubtask(subtask);
                }
            }
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            if (epic != null) {
                for (Subtask subtask : epic.getSubtasks()) {
                    subtasks.remove(subtask.getId());
                }
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }
        return epic.getSubtasks();
    }
}

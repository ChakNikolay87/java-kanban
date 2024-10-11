package managers;

import history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private static int nextId = 1;
    protected final Map<Integer, Task> tasksMap = new HashMap<>();
    protected final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));

    @Override
    public Task addTask(Task task) {
        if (isOverlapping(task)) {
            throw new IllegalArgumentException("Задачи пересекаются по времени выполнения.");
        }
        task.setId(nextId);
        nextId++;
        tasksMap.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epicsMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Epic epic = epicsMap.get(subtask.getSubtasksEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с ID " + subtask.getSubtasksEpicId() + " не найден.");
        }
        if (isOverlapping(subtask)) {
            throw new IllegalArgumentException("Задачи пересекаются по времени выполнения.");
        }
        subtask.setId(nextId);
        nextId++;
        subtasksMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Task updateTask(Task taskToReplace) {
        // Проверяем, существует ли задача с таким ID
        if (tasksMap.containsKey(taskToReplace.getId())) {
            // Получаем текущую версию задачи
            Task existingTask = tasksMap.get(taskToReplace.getId());

            // Временно удаляем задачу из набора приоритетных задач,
            // чтобы предотвратить конфликт при проверке на пересечение со временем самой себя
            prioritizedTasks.remove(existingTask);

            // Проверяем, пересекается ли обновляемая задача по времени с другими задачами
            if (isOverlapping(taskToReplace)) {
                // Если время пересекается, возвращаем оригинальную задачу в набор приоритетных задач
                prioritizedTasks.add(existingTask);
                // Выбрасываем исключение с описанием конфликта времени
                throw new IllegalArgumentException("Task time conflicts with existing tasks.");
            }

            // Если конфликтов нет, обновляем задачу в карте задач
            tasksMap.replace(taskToReplace.getId(), taskToReplace);
            // Добавляем обновлённую задачу обратно в набор приоритетных задач
            prioritizedTasks.add(taskToReplace);
        }
        // Возвращаем обновлённую задачу
        return taskToReplace;
    }




    @Override
    public Subtask updateSubtask(Subtask subtaskToReplace) {
        // Получаем ID обновляемой подзадачи
        var updatingSubtaskId = subtaskToReplace.getId();

        // Проверяем, существует ли подзадача с таким ID
        if (!subtasksMap.containsKey(updatingSubtaskId)) {
            // Если нет, выбрасываем исключение
            throw new IllegalArgumentException("Subtask by id=%s not found".formatted(updatingSubtaskId));
        }

        // Получаем текущую версию подзадачи
        Subtask existingSubtask = subtasksMap.get(updatingSubtaskId);

        // Временно удаляем подзадачу из набора приоритетных задач
        prioritizedTasks.remove(existingSubtask);

        // Проверяем, пересекается ли обновляемая подзадача по времени с другими задачами
        if (isOverlapping(subtaskToReplace)) {
            // Если пересекается, возвращаем оригинальную подзадачу обратно в набор
            prioritizedTasks.add(existingSubtask);
            // Выбрасываем исключение с описанием конфликта времени
            throw new IllegalArgumentException("Subtask time conflicts with existing tasks.");
        }

        // Обновляем подзадачу в карте подзадач
        subtasksMap.replace(updatingSubtaskId, subtaskToReplace);

        // Получаем эпик, к которому принадлежит подзадача
        var epic = epicsMap.get(subtaskToReplace.getSubtasksEpicId());

        // Если эпик существует, обновляем в нём информацию о подзадаче
        if (epic != null) {
            epic.updateSubtask(subtaskToReplace);
        }

        // Добавляем обновлённую подзадачу обратно в набор приоритетных задач
        prioritizedTasks.add(subtaskToReplace);

        // Возвращаем обновлённую подзадачу
        return subtaskToReplace;
    }



    @Override
    public Epic updateEpic(Epic epicToReplace) {
        Epic existingEpic = epicsMap.get(epicToReplace.getId());
        if (existingEpic != null) {
            for (Subtask subtask : existingEpic.getSubtasks()) {
                epicToReplace.addSubtask(subtask);
            }
            epicsMap.replace(epicToReplace.getId(), epicToReplace);
            epicToReplace.updateEpicTime();
        }
        return epicToReplace;
    }

    @Override
    public void clearTasks() {
        for (Integer taskKey : tasksMap.keySet()) {
            inMemoryHistoryManager.remove(taskKey);
        }
        tasksMap.clear();
    }

    @Override
    public void clearEpics() {
        for (Integer epicKey : epicsMap.keySet()) {
            inMemoryHistoryManager.remove(epicKey);
        }
        for (Integer subtaskKey : subtasksMap.keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        for (Epic epic : epicsMap.values()) {
            epic.clear();
        }
        epicsMap.clear();
        subtasksMap.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Integer subtaskKey : subtasksMap.keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.clear();
        }
    }

    @Override
    public void deleteTask(int id) {
        inMemoryHistoryManager.remove(id);
        tasksMap.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicsMap.get(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                inMemoryHistoryManager.remove(subtask.getId());
                subtasksMap.remove(subtask.getId());
            }
            inMemoryHistoryManager.remove(id);
            epicsMap.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasksMap.remove(id);
        if (subtask != null) {
            inMemoryHistoryManager.remove(id);
            Epic epic = epicsMap.get(subtask.getSubtasksEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
            }
        }
    }

    @Override
    public Optional<Task> getTask(int id) {
        Optional<Task> findTask = tasksMap.values().stream()
                .filter(task -> id == task.getId())
                .findFirst();
        findTask.ifPresent(task -> inMemoryHistoryManager.add(task));
        return findTask;
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        Optional<Epic> findEpic = epicsMap.values().stream()
                .filter(epic -> id == epic.getId())
                .findFirst();
        findEpic.ifPresent(epic -> inMemoryHistoryManager.add(epic));
        return findEpic;
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Optional<Subtask> findSubtask = subtasksMap.values().stream()
                .filter(subtask -> subtask.getId() == id)
                .findFirst();
        findSubtask.ifPresent(subtask -> inMemoryHistoryManager.add(subtask));
        return findSubtask;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }



    public void setNextId(int nextId) {
        InMemoryTaskManager.nextId = nextId;
    }

    private boolean isOverlapping(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask ->
                        existingTask.getStartTime() != null &&
                                existingTask.getEndTime() != null &&
                                !(existingTask.getEndTime().isBefore(newTask.getStartTime()) ||
                                        existingTask.getStartTime().isAfter(newTask.getEndTime()))
                );
    }
}

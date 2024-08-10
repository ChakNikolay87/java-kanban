import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task1);

        Task task2 = new Task(0, "Task 2", "Description 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Subtask 1", "Description 1", epic1.getId());
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(0, "Subtask 2", "Description 2", epic1.getId());
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic(0, "Epic 2", "Description 2");
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask(0, "Subtask 3", "Description 3", epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println("All Tasks: " + taskManager.getTasks());
        System.out.println("All Epics: " + taskManager.getEpics());
        System.out.println("All Subtasks: " + taskManager.getSubtasks());

        task1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        System.out.println("Updated Tasks: " + taskManager.getTasks());
        System.out.println("Updated Epics: " + taskManager.getEpics());
        System.out.println("Updated Subtasks: " + taskManager.getSubtasks());

        System.out.println("History of viewed tasks: " + taskManager.getHistory());

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());

        System.out.println("After Deletion - All Tasks: " + taskManager.getTasks());
        System.out.println("After Deletion - All Epics: " + taskManager.getEpics());
        System.out.println("After Deletion - All Subtasks: " + taskManager.getSubtasks());
        System.out.println("History of viewed tasks after deletion: " + taskManager.getHistory());
    }
}

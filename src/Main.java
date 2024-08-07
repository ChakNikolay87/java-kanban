public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.createTask("Task 1", "Description 1");
        Task task2 = taskManager.createTask("Task 2", "Description 2");

        Epic epic1 = taskManager.createEpic("Epic 1", "Description 1");
        Subtask subtask1 = taskManager.createSubtask("Subtask 1", "Description 1", epic1.getId());
        Subtask subtask2 = taskManager.createSubtask("Subtask 2", "Description 2", epic1.getId());

        Epic epic2 = taskManager.createEpic("Epic 2", "Description 2");
        Subtask subtask3 = taskManager.createSubtask("Subtask 3", "Description 3", epic2.getId());

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

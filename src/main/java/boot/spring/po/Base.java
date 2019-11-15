package boot.spring.po;

import org.activiti.engine.task.Task;

public class Base {

    protected Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}

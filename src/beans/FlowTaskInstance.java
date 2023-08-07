package beans;

public class FlowTaskInstance {
    TasksBean task;
    String status;

    public TasksBean getTask() {
        return task;
    }

    public void setTask(TasksBean task) {
        this.task = task;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

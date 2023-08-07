package beans;

public class ActionMatrixBean {
    private String currentNodeStatus, succeedingNodeStatus, availableAction, taskType;

    public String getCurrentNodeStatus() {
        return currentNodeStatus;
    }

    public void setCurrentNodeStatus(String currentNodeStatus) {
        this.currentNodeStatus = currentNodeStatus;
    }

    public String getSucceedingNodeStatus() {
        return succeedingNodeStatus;
    }

    public void setSucceedingNodeStatus(String succeedingNodeStatus) {
        this.succeedingNodeStatus = succeedingNodeStatus;
    }

    public String getAvailableAction() {
        return availableAction;
    }

    public void setAvailableAction(String availableAction) {
        this.availableAction = availableAction;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}

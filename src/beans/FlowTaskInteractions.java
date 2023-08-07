package beans;

public class FlowTaskInteractions {
Long fromFlowTask,toFlowTask;

    public Long getFromFlowTask() {
        return fromFlowTask;
    }

    public void setFromFlowTask(Long fromFlowTask) {
        this.fromFlowTask = fromFlowTask;
    }

    public Long getToFlowTask() {
        return toFlowTask;
    }

    public void setToFlowTask(Long toFlowTask) {
        this.toFlowTask = toFlowTask;
    }


    public String toString(){
        String returnString ="";
        returnString += "FromFlowTaskId:" + fromFlowTask +"\n";
        returnString += "ToFlowTaskIf  :" + toFlowTask +"\n";
        return returnString;
    }
}

package beans;

public class TasksBean {
    String checklistName,baseTaskName, status;
    boolean isPayrollTask,verificationFlag= false;
    Long flowTaskId,baseFlowTaskId;

    public String getChecklistName() {

        return checklistName;

    }

    public String getStatus() {
        return status;
    }

    public boolean getVerificationFlag() {
        return verificationFlag;
    }

    public void setVerificationFlag(boolean verificationFlag) {
        this.verificationFlag = verificationFlag;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setChecklistName(String checklistName) {
        this.checklistName = checklistName;
    }

    public boolean isPayrollTask() {
        return isPayrollTask;
    }

    public void setPayrollTask(boolean payrollTask) {
        isPayrollTask = payrollTask;
    }

    public String getBaseTaskName() {
        return baseTaskName;
    }

    public void setBaseTaskName(String baseTaskName) {
        this.baseTaskName = baseTaskName;
    }

    public Long getFlowTaskId() {
        return flowTaskId;
    }

    public void setFlowTaskId(Long flowTaskId) {
        this.flowTaskId = flowTaskId;
    }

    public Long getBaseFlowTaskId() {
        return baseFlowTaskId;
    }

    public void setBaseFlowTaskId(Long baseTaskId) {
        this.baseFlowTaskId = baseTaskId;
    }

    public String toString(){
        String returnString ="";
        returnString += "FlowTaskId        :" + flowTaskId +"\n";
        returnString += "BaseFlowTaskId    :" + baseFlowTaskId +"\n";
        returnString += "Checklist Name    :" + checklistName +"\n";
        returnString += "Base Task Name    :" + baseTaskName +"\n";
        returnString += "Payroll Flag      :" + isPayrollTask +"\n";
        returnString += "Status            :" + status +"\n";
        returnString += "Verification      :" + verificationFlag +"\n";
        return returnString;
    }


}

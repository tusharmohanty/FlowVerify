package beans;

public class TasksBean {
    String checklistName,baseTaskName, status, taskType;
    boolean isPayrollTask,verificationFlag= false;
    Long flowTaskId,baseFlowTaskId,flowTaskInstanceId,ldgId;

    public String getChecklistName() {

        return checklistName;

    }

    public String getStatus() {
        return status;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public boolean isVerificationFlag() {
        return verificationFlag;
    }

    public Long getFlowTaskInstanceId() {
        return flowTaskInstanceId;
    }

    public void setFlowTaskInstanceId(Long flowTaskInstanceId) {
        this.flowTaskInstanceId = flowTaskInstanceId;
    }

    public Long getLdgId() {
        return ldgId;
    }

    public void setLdgId(Long ldgId) {
        this.ldgId = ldgId;
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

    public boolean isNodeComplete(){
        boolean returnFlag = false;
        if(this.status.equals("COMPLETED")){
            returnFlag = true;
        }
        return returnFlag;
    }

    // A task in Completed or Skipped status is considered functionally complete from prereq status check perspective
    public boolean isTaskFunctionallyComplete(boolean isPayrollProcessingEnabled){
        boolean returnObj = false;

        if(isPayrollTask()){  // a payroll task is considered functionally complete if , its status is functionally complete && its verified
            if(getStatus().equals("COMPLETED") || getStatus().equals("SKIPPED")){
                if(isPayrollProcessingEnabled) { // If payroll processing is enabled
                    if (isVerificationFlag()) {
                        returnObj = true;
                    }
                }
                else{
                    returnObj = true;
                }
            }
        }
        else{ // if its not a payroll task , just look at status
            if(getStatus().equals("COMPLETED") || getStatus().equals("SKIPPED")) {
                returnObj = true;
            }
        }

        return returnObj;
    }

    // A task in IN_PROGRESS, PUBLISHED, CHILD_JOB_SUBMITTED, CHILD_JOB_COMPLETED or post processing flag is set
    public boolean isTaskFunctionallyInProgress(){
        boolean returnObj = false;
        if(getStatus().equals("IN_PROGRESS") || getStatus().equals("PUBLISHED")) {
            returnObj = true;
        }
        return returnObj;
    }

    // A task in SKIPPED
    public boolean isTaskSkipped(){
        boolean returnObj = false;
        if(getStatus().equals("SKIPPED")) {
            returnObj = true;
        }
        return returnObj;
    }

    public boolean isStartFlowTaskInstance(){
        boolean returnObj = false;
        if("START_FLOW".equals(getTaskType())){
            returnObj = true;
        }
        return returnObj;
    }

    public boolean isEndFlowTaskInstance(){
        boolean returnObj = false;
        if("END_FLOW".equals(getTaskType())){
            returnObj = true;
        }
        return returnObj;
    }

}

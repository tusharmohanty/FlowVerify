import beans.FlowTaskInteractions;
import beans.TasksBean;

import java.util.ArrayList;
import java.util.List;

public class ControllerEngine {

    private List<TasksBean> taskList;
    private List<FlowTaskInteractions> flowTaskInteractionsList;
    private boolean isPayrollProcessingEnabled;
    public ControllerEngine(List<TasksBean> taskList,
                            List<FlowTaskInteractions> flowTaskInteractionsList,
                            boolean payrollprocessingFlag){
        this.taskList = taskList;
        this.flowTaskInteractionsList = flowTaskInteractionsList;
        this.isPayrollProcessingEnabled = payrollprocessingFlag;

    }

    public boolean isPayrollProcessingEnabled() {
        return isPayrollProcessingEnabled;
    }

    public void setPayrollProcessingEnabled(boolean payrollProcessingEnabled) {
        isPayrollProcessingEnabled = payrollProcessingEnabled;
    }

    // method returns the startTask FlowTaskInstance object
    private  TasksBean getStartTask(){
        TasksBean returnObj = new TasksBean();
        for(int tempCount=0;tempCount < taskList.size();tempCount ++){
            if((taskList.get(tempCount)).getBaseTaskName().equals("START_FLOW")){
                returnObj = taskList.get(tempCount);
            }
        }
        return returnObj;
    }
    // Given a base flow task Id , method returns a flow task instance object
    public  TasksBean getTask(Long baseFlowTaskId){
        TasksBean returnObj = new TasksBean();
        for(int tempCount=0;tempCount < taskList.size();tempCount ++){
            if((taskList.get(tempCount)).getBaseFlowTaskId().equals(baseFlowTaskId)){
                returnObj = taskList.get(tempCount);
            }
        }
        return returnObj;
    }
    //Given a flow task instance , method returns true if its an end task
    private  boolean isEndTask(TasksBean node){
        boolean returnObj = false;
        if(node.getBaseTaskName().equals("END_FLOW")){
            returnObj = true;
        }
        return  returnObj;
    }


    //Given a flow task instance , method returns true if its a start task
    private  boolean isStartTask(TasksBean node){
        boolean returnObj = false;
        if(node.getBaseTaskName().equals("START_FLOW")){
            returnObj = true;
        }
        return  returnObj;
    }

    //Given a  a current flow task instance , method returns the next flow task instance
    private   List<TasksBean> getNextNodes(TasksBean currentNode , boolean forwardFlag){
        List<TasksBean> returnObj = new ArrayList<TasksBean>();
        if(forwardFlag) {
            for (int tempCount = 0; tempCount < flowTaskInteractionsList.size(); tempCount++) {
                FlowTaskInteractions beanObj = flowTaskInteractionsList.get(tempCount);
                if (beanObj.getFromFlowTask().equals(currentNode.getBaseFlowTaskId())) {
                    TasksBean nextNode = getTask(beanObj.getToFlowTask());
                    returnObj.add(nextNode);
                    System.out.println(nextNode);
                }
            }
        }
        else{
            for (int tempCount = 0; tempCount < flowTaskInteractionsList.size(); tempCount++) {
                FlowTaskInteractions beanObj = flowTaskInteractionsList.get(tempCount);
                if (beanObj.getToFlowTask().equals(currentNode.getBaseFlowTaskId())) {
                    TasksBean nextNode = getTask(beanObj.getFromFlowTask());
                    returnObj.add(nextNode);
                    System.out.println(nextNode);
                }
            }
        }
        return returnObj;
    }

    // Given a task list , flow task interactions list and a current node , method will traverse the flow task
    // interations till it encounters an end task.
    public  void traverse(TasksBean currentNode, boolean forwardFlag ){
        List<TasksBean> nextNodes = getNextNodes(currentNode,forwardFlag);
        for(int tempCount=0;tempCount < nextNodes.size();tempCount ++) {
            TasksBean node = nextNodes.get(tempCount);
            if (forwardFlag){
                if (!isEndTask(node)) { // if forward flag , stop when you encounter end flow
                    traverse(node, forwardFlag);
                }
            }
            else{ //else stop when you encounter start flow
                if (!isStartTask(node)) {
                    traverse(node, forwardFlag);
                }
            }
        }
    }

    public  void traverse(boolean forwardFlag){
        traverse(getStartTask(),forwardFlag);
    }


    // A task in Completed or Skipped status is considered functionally complete from prereq status check perspective
    private boolean isTaskFunctionallyComplete(TasksBean node){
        boolean returnObj = false;

        if(node.isPayrollTask()){  // a payroll task is considered functionally complete if , its status is functionally complete && its verified
            if(node.getStatus().equals("COMPLETED") || node.getStatus().equals("SKIPPED")){
                if(isPayrollProcessingEnabled) { // If payroll processing is enabled
                    if (node.getVerificationFlag()) {
                        returnObj = true;
                    }
                }
                else{
                    returnObj = true;
                }
            }
        }
        else{ // if its not a payroll task , just look at status
            if(node.getStatus().equals("COMPLETED") || node.getStatus().equals("SKIPPED")) {
                returnObj = true;
            }
        }

        return returnObj;
    }


    // A task in IN_PROGRESS, PUBLISHED, CHILD_JOB_SUBMITTED, CHILD_JOB_COMPLETED or post processing flag is set
    private boolean isTaskFunctionallyInProgress(TasksBean node){
        boolean returnObj = false;
            if(node.getStatus().equals("IN_PROGRESS") || node.getStatus().equals("PUBLISHED")) {
                returnObj = true;
            }
        return returnObj;
    }


    private boolean isAnyPrereqPayrollTaskVerificationPending(TasksBean currentNode){
        boolean returnObj = false;
        List<TasksBean> nextNodes = getNextNodes(currentNode,false);
        for(int tempCount=0;tempCount < nextNodes.size();tempCount ++){
            TasksBean node =nextNodes.get(tempCount);
            if(isTaskFunctionallyComplete(node)){ // if its functionally complete , keep looking
                returnObj = isAnyPrereqPayrollTaskVerificationPending(node);
                if(returnObj){
                    break;
                }
            }
            else{    // if its not functionally complete no need to look further
                returnObj = true;
                break;
            }
        }
        return returnObj;
    }

    //function returns true when verify action can be performed
    public boolean isVerifyActionAvailable(TasksBean currentNode){
        boolean returnObj = false;
        if(currentNode.isPayrollTask() && isPayrollProcessingEnabled && !currentNode.getVerificationFlag()) {
            if (currentNode.getStatus().equals("COMPLETED") || currentNode.getStatus().equals("FUNCTIONAL_ERROR") || currentNode.getStatus().equals("SKIPPED")) {
                returnObj = true;
            }
        }
        return  returnObj;
    }

    private void getNextPayrollTask(TasksBean currentNode, List<TasksBean> nodeList, boolean mode){
        List<TasksBean> nextNodes = getNextNodes(currentNode,mode);
        for(int tempCount =0; tempCount < nextNodes.size();tempCount ++){
            TasksBean node = nextNodes.get(tempCount);
            if(node.isPayrollTask()){
                nodeList.add(node);
            }
            else{
                getNextPayrollTask(node,nodeList,mode);
            }
        }
    }

    public List<String> getAvailableActions(TasksBean currentNode){
        List<String> returnObj = new ArrayList<String>();
        List<TasksBean> nextNodes;
        if(currentNode.isPayrollTask()){ // if the current node is a payroll task then look for the next payroll task and then only compute the action matrix
            nextNodes= new ArrayList<TasksBean>();
            getNextPayrollTask(currentNode,nextNodes,true);
        }
        else{
            nextNodes = getNextNodes(currentNode,true);
        }
        for(int tempCount=0; tempCount < nextNodes.size();tempCount ++){
            System.out.println("Evaluating next " + nextNodes.get(tempCount).getChecklistName());
        }
        return  returnObj;
    }
    private void getNextEligibleTaskToStart(TasksBean currentNode, boolean forwardMode, List<TasksBean> nodeList){
        List<TasksBean> nextNodes = getNextNodes(currentNode,forwardMode);
        for(int tempCount=0; tempCount < nextNodes.size();tempCount ++){
            TasksBean node = nextNodes.get(tempCount);
            if(forwardMode) { // look for tasks in Not Started status
                if (node.getStatus().equals("NOT_STARTED")) {
                    nodeList.add(node);
                }
                else if (isTaskFunctionallyInProgress(node)){
                    return;
                }
                else {
                    getNextEligibleTaskToStart(node, forwardMode, nodeList);
                }
            }
        }
    }


    public boolean performVerifyAction(TasksBean currentNode){
        boolean returnObj = false;
        List<TasksBean> nodeList = new ArrayList<TasksBean>();
        if(isVerifyActionAvailable(currentNode)){
            getNextEligibleTaskToStart(currentNode, true,nodeList);
            for(int tempCount=0; tempCount < nodeList.size(); tempCount ++){
                System.out.println("Starting task " + nodeList.get(tempCount).getChecklistName());
            }
            returnObj= true;
        }
        return returnObj;
    }

    public boolean isPrereqComplete(TasksBean currentNode){
        boolean returnObj = false;
        if(isPayrollProcessingEnabled){ // for now assume this is a flow level property , instead of a task level property
           if(currentNode.isPayrollTask()){  // if its a payroll task // traverse the tree backwards to see if all the payroll tasks are verified
               if(isAnyPrereqPayrollTaskVerificationPending(currentNode)){
                   returnObj = false;
               }
               else{
                   returnObj = true;
               }
           }
           else{ //if its not a payroll task , just check the previous task for prereq completeness
               returnObj = isTaskFunctionallyComplete(currentNode);
           }
        }
        else{
            //if its not a payroll task , just check the previous task for prereq completeness
            returnObj = isTaskFunctionallyComplete(currentNode);

        }
        return  returnObj;
    }

}

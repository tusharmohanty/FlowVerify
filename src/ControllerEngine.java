import beans.ActionMatrixBean;
import beans.FlowTaskInteractions;
import beans.TasksBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerEngine {

    private List<TasksBean> taskList;
    private List<FlowTaskInteractions> flowTaskInteractionsList;

    private Map<String,List<ActionMatrixBean>> actionMatrix = HashMap.newHashMap(5);
    private boolean isPayrollProcessingEnabled;
    public ControllerEngine(List<TasksBean> taskList,
                            List<FlowTaskInteractions> flowTaskInteractionsList,
                            boolean payrollprocessingFlag){
        this.taskList = taskList;
        this.flowTaskInteractionsList = flowTaskInteractionsList;
        this.isPayrollProcessingEnabled = payrollprocessingFlag;
        LoadData.loadActionMatrix(actionMatrix);

    }



    // Production code constructor
    public ControllerEngine(Long baseFlowTaskId,
                            Long baseFlowId,
                            Long flowTaskInstanceId,
                            Long ldgId){
        this.isPayrollProcessingEnabled = true;
        flowTaskInteractionsList = new ArrayList<FlowTaskInteractions>();
        taskList = new ArrayList<TasksBean>();
        //populate the data structures from DB
    }

    public ControllerEngine (){
        this.isPayrollProcessingEnabled = true;
        // load Action Matrix
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


    /**
     * Function returns the next non skipped task in the execution chain
     * **/
    public ArrayList<TasksBean> getNonSkippedNextNodes(TasksBean currentNode, boolean forwardFlag){
           ArrayList<TasksBean> returnNodeList = new ArrayList<TasksBean>();
           ArrayList<TasksBean> nextNodeList = (ArrayList<TasksBean>) getNextNodes(currentNode,forwardFlag);
           for(int tempCount=0;tempCount < nextNodeList.size();tempCount++){
               TasksBean nextNode =nextNodeList.get(tempCount);
               if(nextNode.getStatus().equals("SKIPPED")){
                   returnNodeList.addAll(getNonSkippedNextNodes(nextNode,forwardFlag));
               }
               else{
                   returnNodeList.add(nextNode);
               }
           }
           return  returnNodeList;
    }

    /**
     * Function returns the next non skipped, non incomplete task in the execution chain
     * **/
    public ArrayList<TasksBean> getNonSkippedNonIncompleteNextNodes(TasksBean currentNode, boolean forwardFlag){
        ArrayList<TasksBean> returnNodeList = new ArrayList<TasksBean>();
        ArrayList<TasksBean> nextNodeList = (ArrayList<TasksBean>) getNextNodes(currentNode,forwardFlag);
        for(int tempCount=0;tempCount < nextNodeList.size();tempCount++){
            TasksBean nextNode =nextNodeList.get(tempCount);
            if(nextNode.getStatus().equals("SKIPPED") || nextNode.getStatus().equals("INCOMPLETE")){
                returnNodeList.addAll(getNonSkippedNextNodes(nextNode,forwardFlag));
            }
            else{
                returnNodeList.add(nextNode);
            }
        }
        return  returnNodeList;
    }

    /**
     * If Flow Instance status = COMPLETED , can perform bulk rollback = true
     * else there are certain status which if present , bulk rollback cannot be performed
     * if these checks say that bulk rollback can be performed and fi.status is not completed
     * else we execute a VO with flow instance id and if the VO doesnt have data return false*/
    public boolean canPerformBulkRollback(){
        return true;
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
                if (!node.isStartFlowTaskInstance()) {
                    traverse(node, forwardFlag);
                }
            }
        }
    }


    public  List<TasksBean> getStartNodes(boolean forwardFlag){
        List<TasksBean> returnList = new ArrayList<TasksBean>();
        for(int tempCount=0;tempCount < taskList.size();tempCount ++) {
            TasksBean node = taskList.get(tempCount);
            if (forwardFlag){
                if (node.isEndFlowTaskInstance()) {
                    returnList.add(node);
                }
            }
            else{ //else stop when you encounter start flow
                if (node.isStartFlowTaskInstance()) {
                    returnList.add(node);
                }
            }
        }
        return returnList;
    }

    public  void traverse(boolean forwardFlag){
        traverse(getStartTask(),forwardFlag);
    }








    private boolean isAnyPrereqPayrollTaskVerificationPending(TasksBean currentNode){
        boolean returnObj = false;
        List<TasksBean> nextNodes = getNextNodes(currentNode,false);
        for(int tempCount=0;tempCount < nextNodes.size();tempCount ++){
            TasksBean node =nextNodes.get(tempCount);
            if(node.isTaskFunctionallyComplete(isPayrollProcessingEnabled)){ // if its functionally complete , keep looking
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
        if(currentNode.isPayrollTask() && isPayrollProcessingEnabled && !currentNode.isVerificationFlag()) {
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

    private String getAvailableActionFromActionMatrix(TasksBean task, String nextTaskStatus){
            String returnObj = "";
            List<ActionMatrixBean> actionMatrixList = actionMatrix.get(task.getTaskType());
            for(int tempCount=0;actionMatrixList != null && tempCount < actionMatrixList.size();tempCount ++){
               ActionMatrixBean beanObj = actionMatrixList.get(tempCount);
               if(beanObj.getCurrentNodeStatus().equals(task.getStatus()) && beanObj.getSucceedingNodeStatus().equals(nextTaskStatus)){
                   returnObj= beanObj.getAvailableAction();
               }
            }
            return returnObj;
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
            returnObj.add(getAvailableActionFromActionMatrix(currentNode,nextNodes.get(tempCount).getStatus()));
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
                else if (node.isTaskFunctionallyInProgress()){
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
               returnObj = currentNode.isTaskFunctionallyComplete(isPayrollProcessingEnabled);
           }
        }
        else{
            //if its not a payroll task , just check the previous task for prereq completeness
            returnObj = currentNode.isTaskFunctionallyComplete(isPayrollProcessingEnabled);

        }
        return  returnObj;
    }

    public void setBaseFlowTaskId(long baseFlowTaskId, Long ldgId, String lc){
        //load interaction data
    }


}

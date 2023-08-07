import beans.FlowTaskInstance;
import beans.FlowTaskInteractions;
import beans.TasksBean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.w3c.dom.Document;
public class MainTestClass {

    public static void main(String args[]){
        List<TasksBean> taskList = new ArrayList<TasksBean>();
        List<FlowTaskInteractions> flowTaskInteractionsList = new ArrayList<FlowTaskInteractions>();
        List<FlowTaskInstance> flowTaskInstancesList = new ArrayList<FlowTaskInstance>();
        LoadData.loadTaskData(taskList);
        LoadData.loadTaskInteractionsData(flowTaskInteractionsList);
        ControllerEngine controllerObj = new ControllerEngine(taskList,flowTaskInteractionsList,true);
        controllerObj.traverse(true);
//        System.out.println(controllerObj.isPrereqComplete(controllerObj.getTask(005l)));
        controllerObj.performVerifyAction(controllerObj.getTask(002l));

    }









}

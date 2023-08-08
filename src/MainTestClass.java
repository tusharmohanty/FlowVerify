import beans.ActionMatrixBean;
import beans.FlowTaskInstance;
import beans.FlowTaskInteractions;
import beans.TasksBean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.w3c.dom.Document;
public class MainTestClass {

    public static void main(String args[]){
        List<TasksBean> tasksBeanList = new ArrayList<>();
        List<FlowTaskInteractions> flowTaskInteractionsList = new ArrayList<>();
        LoadData.loadTaskInteractionsData(flowTaskInteractionsList,"src/data/FlowTaskInteractions5.xml");
        LoadData.loadTaskData(tasksBeanList,"src/data/TaskList5.xml");
        ControllerEngine controllerEngine = new ControllerEngine(tasksBeanList,flowTaskInteractionsList,true);
        System.out.println(controllerEngine.getAvailableActions(controllerEngine.getTask(5l)));
    }









}

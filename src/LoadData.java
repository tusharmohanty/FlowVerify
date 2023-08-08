import beans.ActionMatrixBean;
import beans.FlowTaskInteractions;
import beans.TasksBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.Map;

public class LoadData {

    public static void loadTaskData(List<TasksBean> tasksBeanList, String path){
        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("FlowTaskInstance");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                TasksBean beanObj = new TasksBean();
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    beanObj.setFlowTaskId(Long.parseLong(eElement.getElementsByTagName("FlowTaskId")
                            .item(0).getTextContent()));
                    beanObj.setBaseFlowTaskId(Long.parseLong(eElement.getElementsByTagName("BaseFlowTaskId")
                            .item(0).getTextContent()));
                    beanObj.setChecklistName(eElement.getElementsByTagName("ChecklistName")
                            .item(0).getTextContent());
                    beanObj.setBaseTaskName(eElement.getElementsByTagName("BaseTaskName")
                            .item(0).getTextContent());
                    beanObj.setTaskType(eElement.getElementsByTagName("TaskType")
                            .item(0).getTextContent());
                    beanObj.setPayrollTask(((eElement.getElementsByTagName("PayrollProcessingFlag")
                            .item(0).getTextContent()).equals("Y") ? true : false));
                    beanObj.setStatus(eElement.getElementsByTagName("Status")
                            .item(0).getTextContent());
                    if(eElement.getElementsByTagName("VerificationFlag").getLength() != 0) {
                        beanObj.setVerificationFlag(((eElement.getElementsByTagName("VerificationFlag")
                                .item(0).getTextContent()).equals("Y") ? true : false));
                    }
                    if(eElement.getElementsByTagName("FlowTaskInstanceId").getLength() != 0) {
                        beanObj.setFlowTaskInstanceId(Long.parseLong(eElement.getElementsByTagName("FlowTaskInstanceId")
                                .item(0).getTextContent()));
                    }
                    if(eElement.getElementsByTagName("LegislativeDataGroupId").getLength() != 0) {
                        beanObj.setFlowTaskInstanceId(Long.parseLong(eElement.getElementsByTagName("LegislativeDataGroupId")
                                .item(0).getTextContent()));
                    }
                }
                tasksBeanList.add(beanObj);
            }
        }
        catch(ParserConfigurationException e1){
            e1.printStackTrace();
        }
        catch(SAXException e2){
            e2.printStackTrace();
        }
        catch(IOException e3){
            e3.printStackTrace();
        }

    }



    public static void loadTaskInteractionsData(List<FlowTaskInteractions> flowTaskInteractionsList, String path){
        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("FlowTaskInteraction");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                FlowTaskInteractions beanObj = new FlowTaskInteractions();
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    beanObj.setFromFlowTask(Long.parseLong(eElement.getElementsByTagName("FromFlowTaskId")
                            .item(0).getTextContent()));
                    beanObj.setToFlowTask(Long.parseLong(eElement.getElementsByTagName("ToFlowTaskId")
                            .item(0).getTextContent()));
                }
                flowTaskInteractionsList.add(beanObj);
            }
        }
        catch(ParserConfigurationException e1){
            e1.printStackTrace();
        }
        catch(SAXException e2){
            e2.printStackTrace();
        }
        catch(IOException e3){
            e3.printStackTrace();
        }

    }

    public static void loadActionMatrix(Map<String,List<ActionMatrixBean>> actionMatrix){
        try {
            File fXmlFile = new File("src/ActionMatrix.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Entry");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                ActionMatrixBean beanObj = new ActionMatrixBean();
                Node nNode = nList.item(temp);
                String taskType ="";
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    taskType = eElement.getElementsByTagName("TaskType").item(0).getTextContent();
                    beanObj.setTaskType(taskType);
                    beanObj.setCurrentNodeStatus(eElement.getElementsByTagName("CurrentNodeStatus")
                            .item(0).getTextContent());
                    beanObj.setSucceedingNodeStatus(eElement.getElementsByTagName("SucceedingNodeStatus")
                            .item(0).getTextContent());
                    beanObj.setAvailableAction(eElement.getElementsByTagName("AvailableAction")
                            .item(0).getTextContent());
                }
                if(actionMatrix.containsKey(taskType)){
                    actionMatrix.get(taskType).add(beanObj);
                }
                else{
                    List<ActionMatrixBean> actionMatrixList = new ArrayList<>();
                    actionMatrixList.add(beanObj);
                    actionMatrix.put(taskType,actionMatrixList);
                }
            }
        }
        catch(ParserConfigurationException e1){
            e1.printStackTrace();
        }
        catch(SAXException e2){
            e2.printStackTrace();
        }
        catch(IOException e3){
            e3.printStackTrace();
        }

    }


}

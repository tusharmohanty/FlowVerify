public class Node {
    String  status;
    Node previousTask;
    Node succeedingTask;

    public Node(String status) {
        this.status = status;
    }
}
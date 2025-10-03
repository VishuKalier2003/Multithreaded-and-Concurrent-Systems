public class ArrayNode {
    private final int snap;
    private final String task;
    private final int workID;

    public ArrayNode(int snap, String task, int wID) {
        this.snap = snap;
        this.task = task;
        this.workID = wID;
    }

    public ArrayNode(int snap) {
        this.snap = snap;
        this.task = "dummy";
        this.workID = -1;
    }

    public int snap() {return snap;}
    public int workID() {return workID;}
    public String task() {return task;}
}
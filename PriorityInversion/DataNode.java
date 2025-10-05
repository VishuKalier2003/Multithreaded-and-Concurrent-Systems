
import java.util.ArrayList;
import java.util.List;

public class DataNode {
    public final String task;
    public final int taskID;
    public int priority;
    public final List<DataNode> prerequisites;

    public DataNode(String task, int uuid, int priority) {
        this.task = task;
        this.taskID = uuid;
        this.priority = priority;
        this.prerequisites = new ArrayList<>();
    }

    public void addPrerequisites(DataNode data) {
        prerequisites.add(data);
    }

    public int priority() {return priority;}
    public int taskID() {return taskID;}
    public String task() {return task;}
}

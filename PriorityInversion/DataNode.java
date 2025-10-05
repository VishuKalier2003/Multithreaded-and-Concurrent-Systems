import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataNode {
    public final String taskName;
    public final int taskID;
    public final List<DataNode> dependents;  // tasks that wait for this
    public final List<DataNode> prerequisites; // tasks this depends on
    public final AtomicInteger effectivePriority; // for donation
    public int basePriority;

    public DataNode(String taskName, int taskID, int basePriority) {
        this.taskName = taskName;
        this.taskID = taskID;
        this.basePriority = basePriority;
        this.effectivePriority = new AtomicInteger(basePriority);
        this.dependents = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
    }

    public void addPrerequisite(DataNode prerequisite) {
        this.prerequisites.add(prerequisite);
        prerequisite.dependents.add(this);
    }

    public int getPriority() { return effectivePriority.get(); }

    public void donatePriority(int newPriority) {
        effectivePriority.updateAndGet(curr -> Math.max(curr, newPriority));
    }
}

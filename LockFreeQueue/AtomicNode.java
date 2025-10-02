
import java.util.concurrent.atomic.AtomicReference;

public class AtomicNode {

    // next pointer as atomic reference
    public final AtomicReference<AtomicNode> next = new AtomicReference<>(null);
    public String data;
    public int nodeID;

    public AtomicNode() {
        this.data = "empty";
        this.nodeID = -1;
    }

    public AtomicNode(String data, int nodeID) {
        this.data = data;
        this.nodeID = nodeID;
    }
}

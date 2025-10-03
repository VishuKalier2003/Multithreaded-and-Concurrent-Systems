import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentArray {
    public final ConcurrentLinkedDeque<ArrayNode>[] array;
    public final AtomicInteger snapCounter = new AtomicInteger(0);

    @SuppressWarnings("unchecked")
    public ConcurrentArray(int size) {
        array = new ConcurrentLinkedDeque[size];
        for (int i = 0; i < size; i++) {
            array[i] = new ConcurrentLinkedDeque<>();
            // Initialize each index with snapshot 0 (base state)
            array[i].addLast(new ArrayNode(0));
        }
    }

    public void update(int idx, String data, int value) throws InterruptedException {
        // Always use global snapshot counter
        int newSnap = snapCounter.incrementAndGet();
        // Simulate processing delay
        Thread.sleep(200);
        array[idx].addLast(new ArrayNode(newSnap, data, value));
        System.out.println("Updated index " + idx + " with data: " + data +
                " value: " + value + " at snapshot " + newSnap);
    }

    public void traverse() throws InterruptedException {
        // Take a stable snapshot ID for consistent read
        int readSnap = snapCounter.get();
        Thread.sleep(100);
        System.out.println("=========== Traverse at Snapshot " + readSnap + " ===========");
        for (int i = 0; i < array.length; i++) {
            ArrayNode persistent = new ArrayNode(0);
            for (ArrayNode node : array[i]) {
                if (node.snap() <= readSnap) {
                    persistent = node;
                } else {
                    break;
                }
            }
            System.out.println("Index: " + i +
                    " | Snap: " + persistent.snap() +
                    " | Data: " + persistent.task() +
                    " | WorkID: " + persistent.workID());
        }
        System.out.println("=================================================");
    }
}

import java.util.*;
import java.util.concurrent.*;

public class PriorityPool {
    public final int poolRank;
    private final ExecutorService executor;
    private final PriorityBlockingQueue<DataNode> queue;

    public PriorityPool(int poolRank) {
        this.poolRank = poolRank;
        this.executor = Executors.newFixedThreadPool(2);
        this.queue = new PriorityBlockingQueue<>(10, Comparator.comparingInt(DataNode::getPriority).reversed());
    }

    public void addTask(DataNode node) {
        queue.add(node);
    }

    public CompletableFuture<Void> startWorker() {
        return CompletableFuture.runAsync(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    DataNode node = queue.take();

                    // --- Handle donation to prerequisites ---
                    for (DataNode prereq : node.prerequisites) {
                        if (prereq.getPriority() < node.getPriority()) {
                            System.out.printf("Donation: %s -> %s%n", node.taskName, prereq.taskName);
                            prereq.donatePriority(node.getPriority());
                            queue.add(prereq); // re-queue after donation
                        }
                    }

                    process(node);

                    // --- Trigger dependents ---
                    for (DataNode dep : node.dependents) {
                        if (!queue.contains(dep)) queue.add(dep);
                    }
                }
            } catch (InterruptedException ignored) { }
        }, executor);
    }

    private void process(DataNode node) throws InterruptedException {
        System.out.printf("Executing [%s] with effective priority %d%n", node.taskName, node.getPriority());
        Thread.sleep(500);
        System.out.printf("Completed [%s]%n", node.taskName);
    }

    public void shutdown() { executor.shutdownNow(); }
}

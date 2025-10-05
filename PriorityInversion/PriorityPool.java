import java.util.concurrent.*;

public class PriorityPool {
    public final int POOL_RANK;
    private final ExecutorService executor;
    public final LinkedBlockingQueue<DataNode> queue = new LinkedBlockingQueue<>();
    public final ConcurrentMap<Integer, DataNode> map = new ConcurrentHashMap<>();

    public PriorityPool(int poolRank) {
        this.POOL_RANK = poolRank;
        this.executor = Executors.newFixedThreadPool(2);
    }

    public void insert(int taskID, DataNode node) {
        map.put(taskID, node);
    }

    public void activate(int taskID) {
        DataNode node = map.remove(taskID);
        if (node != null)
            queue.add(node);
    }

    public void upgrade(int taskID, int newPriority) {
        DataNode node = map.remove(taskID);
        if (node != null) {
            node.priority = newPriority;
            InversionScheduler.poolMap
                    .computeIfAbsent(newPriority, PriorityPool::new)
                    .insert(taskID, node);
        }
    }

    /**
     * Non-blocking asynchronous execution
     */
    public CompletableFuture<String> executeAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DataNode node = queue.take();

                // --- Detect inversion and trigger donation ---
                for (DataNode child : node.prerequisites) {
                    if (child.priority < node.priority) {
                        System.out.printf("Priority Inversion detected: child %d < parent %d → upgrading...%n",
                                child.priority, node.priority);
                        InversionScheduler.poolMap.get(child.priority)
                                .upgrade(child.taskID, node.priority);
                        InversionScheduler.poolMap.get(child.priority)
                                .activate(child.taskID);
                    }
                }

                process(node);
                return "Task " + node.taskID + " completed at priority " + POOL_RANK;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Interrupted: " + e.getMessage();
            }
        }, executor);
    }

    private void process(DataNode node) throws InterruptedException {
        System.out.printf("→ Executing [%s] (Priority: %d)%n", node.task(), node.priority);
        Thread.sleep(1000);
        System.out.printf("✓ Completed [%s]%n", node.task());
    }

    public void shutdown() {
        executor.shutdown();
    }
}

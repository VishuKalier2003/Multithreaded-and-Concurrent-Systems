import java.util.*;
import java.util.concurrent.*;

public class InversionScheduler {

    public static final ConcurrentMap<Integer, PriorityPool> poolMap = new ConcurrentHashMap<>();
    private final List<CompletableFuture<String>> futures = new ArrayList<>();

    public void createPool(int rank) {
        poolMap.putIfAbsent(rank, new PriorityPool(rank));
    }

    public void insertIntoPool(DataNode node, int rank) {
        poolMap.computeIfAbsent(rank, PriorityPool::new).insert(node.taskID, node);
    }

    public void connect(DataNode parent, DataNode child) {
        parent.addPrerequisites(child);
    }

    public void submitAll() {
        poolMap.values().forEach(pool -> futures.add(pool.executeAsync()));
    }

    public void awaitCompletion() {
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
            .thenRun(() -> System.out.println("\n=== All tasks completed ==="))
            .join();
    }

    public void shutdownAll() {
        poolMap.values().forEach(PriorityPool::shutdown);
    }
}

import java.util.*;
import java.util.concurrent.*;

public class InversionScheduler {
    private final Map<Integer, PriorityPool> pools = new HashMap<>();
    private final List<CompletableFuture<Void>> futures = new ArrayList<>();

    public void createPool(int rank) {
        pools.put(rank, new PriorityPool(rank));
    }

    public void addTask(DataNode node, int poolRank) {
        pools.computeIfAbsent(poolRank, PriorityPool::new).addTask(node);
    }

    public void startAll() {
        pools.values().forEach(pool -> futures.add(pool.startWorker()));
    }

    public void awaitCompletion(long timeoutMs) {
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
            .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .exceptionally(_ -> { return null; })
            .join();
    }

    public void shutdownAll() {
        pools.values().forEach(PriorityPool::shutdown);
    }
}

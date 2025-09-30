import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class PriorityTask implements Runnable {
    private final PriorityBlockingQueue<Node> taskQueue = new PriorityBlockingQueue<>();
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final ExecutorService executor;

    public PriorityTask(int numThreads) {
        this.executor = Executors.newFixedThreadPool(numThreads);
    }

    public void process(Node node) {
        taskQueue.add(node);
    }

    public boolean isDone() {
        return taskQueue.isEmpty() && activeTasks.get() == 0;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() || !isDone()) {
                Node node = taskQueue.poll(1, TimeUnit.SECONDS); // avoid blocking forever
                if (node != null) {
                    activeTasks.incrementAndGet();
                    executor.submit(() -> execute(node));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void execute(Node node) {
        try {
            System.out.println("Task: " + node.getData() + " started | Delay: " + delay(node) + "s");
            Thread.sleep(node.getTime() * 1000L);
            System.out.println("Task: " + node.getData() + " completed!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            activeTasks.decrementAndGet();
        }
    }

    private long delay(Node node) {
        return (System.currentTimeMillis() - node.getEntry()) / 1000;
    }

    public void snapshot() {
        long now = System.currentTimeMillis();
        System.out.println("=== Snapshot ===");
        for (Node node : taskQueue) {
            node.setDelay((now - node.getEntry()) / 1000);
            node.showData();
        }
        System.out.println("Active tasks: " + activeTasks.get());
        System.out.println("================");
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
public class Test {
    public static void main(String[] args) throws InterruptedException {
        TaskQueue taskQueue = new TaskQueue(3);

        // Submit tasks
        for (int i = 1; i <= 10; i++) {
            taskQueue.distributeLoad("Task number-" + i);
        }

        // Start a monitor thread for live load snapshots
        Thread monitor = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("---- Load Snapshot ----");
                    taskQueue.printLoads();

                    // Stop if all consumers are idle
                    boolean allIdle = taskQueue.consumers.stream().allMatch(c -> c.queue.isEmpty() && c.getLoad() == 0);
                    if (allIdle) break;
                    Thread.sleep(1000); // snapshot every second
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        monitor.start();

        // Wait for monitor to finish (i.e., all tasks done)
        monitor.join();

        // Now shutdown the executor
        taskQueue.shutdown();
        System.out.println("All tasks completed!");
    }
}

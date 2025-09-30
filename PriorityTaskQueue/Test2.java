public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        PriorityTask ptask = new PriorityTask(2); // 2 executor threads
        Thread dispatcher = new Thread(ptask);
        dispatcher.start();

        // Submit tasks
        for (int i = 1; i <= 10; i++) {
            int randomTime = 1 + (int)(Math.random() * 5); // 1-5 sec execution
            ptask.process(new Node("task" + i, randomTime));
            Thread.sleep(400);
        }

        // Monitor snapshots
        Thread monitor = new Thread(() -> {
            try {
                while (!ptask.isDone()) {
                    ptask.snapshot();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        monitor.start();

        // Wait for monitor and dispatcher
        monitor.join();
        dispatcher.interrupt(); // stop dispatcher loop
        ptask.shutdown();
        System.out.println("All tasks finished. Exiting.");
    }
}
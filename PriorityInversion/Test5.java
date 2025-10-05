public class Test5 {
    public static void main(String[] args) throws Exception {
        InversionScheduler scheduler = new InversionScheduler();

        // --- Create pools ---
        scheduler.createPool(1);
        scheduler.createPool(5);

        // --- Create tasks ---
        DataNode low = new DataNode("LowPriorityTask", 101, 1);
        DataNode high = new DataNode("HighPriorityTask", 202, 5);

        // --- Connect dependency (high depends on low) ---
        high.addPrerequisite(low);

        // --- Add tasks to pools ---
        scheduler.addTask(low, low.basePriority);
        scheduler.addTask(high, high.basePriority);

        // --- Start scheduler ---
        scheduler.startAll();
        scheduler.awaitCompletion(3000);

        // --- Assert donation occurred ---
        assertTrue(low.getPriority() >= high.getPriority(),
                "Low priority task should have received donation from high priority dependent.");

        scheduler.shutdownAll();
        System.out.println("✅ Priority inversion resolved dynamically!");
    }

    public static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }
}

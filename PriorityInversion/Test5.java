
public class Test5 {
    public static void main(String[] args) throws Exception {
        InversionScheduler scheduler = new InversionScheduler();
        // --- Step 1: Define pools ---
        scheduler.createPool(1);
        scheduler.createPool(5);
        // --- Step 2: Create tasks ---
        DataNode low = new DataNode("LowPriorityTask", 101, 1);
        DataNode high = new DataNode("HighPriorityTask", 202, 5);
        // --- Step 3: Connect dependency (high depends on low) ---
        scheduler.connect(high, low);
        // --- Step 4: Insert tasks ---
        scheduler.insertIntoPool(low, low.priority);
        scheduler.insertIntoPool(high, high.priority);
        InversionScheduler.poolMap.get(1).activate(low.taskID);
        InversionScheduler.poolMap.get(5).activate(high.taskID);
        // --- Step 5: Submit and run ---
        scheduler.submitAll();
        scheduler.awaitCompletion();
        // --- Step 6: Assert that inversion correction occurred ---
        assertTrue(low.priority >= high.priority,
                "Low priority task should have received donation from high priority dependent.");
        scheduler.shutdownAll();
        System.out.println("\nAssertion successful: Priority inversion resolved dynamically.");
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition)
            throw new AssertionError("Assertion failed: " + message);
    }

}

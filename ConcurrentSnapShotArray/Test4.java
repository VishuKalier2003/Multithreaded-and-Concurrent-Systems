public class Test4 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== TEST SUITE START ==========");

        testSingleThreadUpdate();
        testMultipleUpdatesSameIndex();
        testConcurrentUpdates();
        testConsistencyBetweenSnapshots();
        testStress();

        System.out.println("========== ALL TESTS PASSED ==========");
    }

    // --- Test 1 ---
    private static void testSingleThreadUpdate() throws InterruptedException {
        ConcurrentArray arr = new ConcurrentArray(3);
        arr.update(0, "TaskA", 100);
        arr.update(1, "TaskB", 200);
        arr.traverse();

        assertTrue(arr.snapCounter.get() == 2, "SingleThreadUpdate: Snap counter should be 2");
        System.out.println("[PASSED] testSingleThreadUpdate");
    }

    // --- Test 2 ---
    private static void testMultipleUpdatesSameIndex() throws InterruptedException {
        ConcurrentArray arr = new ConcurrentArray(2);
        arr.update(0, "First", 1);
        arr.update(0, "Second", 2);
        arr.update(0, "Third", 3);
        arr.traverse();

        assertTrue(arr.snapCounter.get() == 3, "MultipleUpdatesSameIndex: Snap counter should be 3");
        System.out.println("[PASSED] testMultipleUpdatesSameIndex");
    }

    // --- Test 3 ---
    private static void testConcurrentUpdates() throws InterruptedException {
        ConcurrentArray arr = new ConcurrentArray(4);

        Thread t1 = new Thread(() -> {
            try { arr.update(0, "T1", 10); } catch (InterruptedException ignored) {}
        });
        Thread t2 = new Thread(() -> {
            try { arr.update(1, "T2", 20); } catch (InterruptedException ignored) {}
        });
        Thread t3 = new Thread(() -> {
            try { arr.update(2, "T3", 30); } catch (InterruptedException ignored) {}
        });

        t1.start(); t2.start(); t3.start();
        t1.join(); t2.join(); t3.join();

        arr.traverse();

        assertTrue(arr.snapCounter.get() >= 3, "ConcurrentUpdates: At least 3 snapshots expected");
        System.out.println("[PASSED] testConcurrentUpdates");
    }

    // --- Test 4 ---
    private static void testConsistencyBetweenSnapshots() throws InterruptedException {
        ConcurrentArray arr = new ConcurrentArray(2);
        arr.update(0, "A", 11);
        int snapBefore = arr.snapCounter.get();

        arr.update(1, "B", 22);
        arr.traverse();

        assertTrue(arr.snapCounter.get() >= snapBefore, 
            "Consistency: Snapshot counter should not go backwards");
        System.out.println("[PASSED] testConsistencyBetweenSnapshots");
    }

    // --- Test 5 ---
    private static void testStress() throws InterruptedException {
        ConcurrentArray arr = new ConcurrentArray(5);

        for (int i = 0; i < 20; i++) {
            int index = i % 5;
            arr.update(index, "Stress-" + i, i);
        }
        arr.traverse();

        assertTrue(arr.snapCounter.get() == 20, "Stress: Should have exactly 20 snapshots");
        System.out.println("[PASSED] testStress");
    }

    // --- Helper for assertions ---
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("[FAILED] " + message);
        }
    }
}

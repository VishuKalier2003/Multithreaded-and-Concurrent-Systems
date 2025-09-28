
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskQueue {
    public final PriorityQueue<Consumer> consumers;
    public final ExecutorService executor;

    public TaskQueue(int n) {
        executor = Executors.newFixedThreadPool(n);
        consumers = new PriorityQueue<>((a, b) -> Integer.compare(a.getLoad(), b.getLoad()));
        for(int i = 1; i <= n; i++) {
            Consumer c = new Consumer("Consumer "+i, 4000*i);
            consumers.offer(c);
            executor.submit(c);
        }
    }

    public void distributeLoad(String task) {
        Consumer c = consumers.poll();
        c.submitTask(task);
        consumers.offer(c);
    }

    public void printLoads() {
        consumers.forEach(c -> 
            System.out.println(c.name + " load = " + c.getLoad()));
    }

    public void shutdown() {
        executor.shutdownNow();
    }
    
}
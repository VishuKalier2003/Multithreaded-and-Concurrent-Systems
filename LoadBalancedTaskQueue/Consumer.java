
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {
    public final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public final AtomicInteger load = new AtomicInteger(0);
    public final String name;
    public final int waiting;

    public Consumer(String nm, int waiting) {
        this.name = nm;
        this.waiting = waiting;
    }

    public void submitTask(String task) {
        load.incrementAndGet();
        queue.add(task);
    }

    @Override
    public void run() {
        try {
            while(true) {
                String task = queue.take();
                processTask(task);
                load.decrementAndGet();
            }
        } catch(InterruptedException e) 
        {e.getLocalizedMessage();} 
    }

    public int getLoad() {
        return load.intValue();
    }

    public void processTask(String task) throws InterruptedException {
        log("Start: Task Thread : "+name+" task : "+task);
        Thread.sleep(waiting);
        log("End: Task Thread : "+name+" task : "+task);
    }

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.println("[" + timestamp + "] " + message);
    }
}
public class Test3 {
    public static void main(String[] args) throws InterruptedException {
        final LockQueue lq = new LockQueue();       // Lock queue defined
        // Producer I
        Thread producer1 = new Thread(() -> {
            for(int i = 1; i <= 3; i++) {
                try {
                    AtomicNode aNode = new AtomicNode("producer 1 node", i);
                    System.out.println("Producer 1 produces node of nodeID "+i);
                    Thread.sleep(600);
                    lq.enqueue(aNode);
                } catch(InterruptedException e) {
                    e.getLocalizedMessage();
                }
            }
        });
        // Producer II
        Thread producer2 = new Thread(() -> {
            for(int i = 1; i <= 5; i++) {
                try {
                    AtomicNode aNode = new AtomicNode("producer 2 node", i);
                    System.out.println("Producer 2 produces node of nodeID "+i);
                    Thread.sleep(1000);
                    lq.enqueue(aNode);
                } catch(InterruptedException e) {
                    e.getLocalizedMessage();
                }
            }
        });
        // Consumer I
        Thread consumer = new Thread(() -> {
            int c = 8;
            while(c != 0) {
                if(!lq.isEmpty()) {
                    c--;
                    try {
                        Thread.sleep(800);
                        AtomicNode node = lq.dequeue();
                        System.out.println("Consumer 1 consumes node : "+node.data+" "+node.nodeID);
                    } catch(InterruptedException e) {
                        e.getLocalizedMessage();
                    }
                }   
            }
        });
        producer1.start();
        producer2.start();
        consumer.start();
        // Synchronisation
        producer1.join();
        producer2.join();
        consumer.join();
    }
}

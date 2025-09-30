
import java.util.concurrent.TimeUnit;

public class Node implements Comparable<Node> {
    private final String data;
    private final int time;
    private final long entry;
    private long delay;

    public Node(String data, int t) {
        this.data = data;
        this.time = t;
        this.entry = System.currentTimeMillis();
        this.delay = 0l;
    }

    public String getData() {return data;}
    public int getTime() {return time;}
    public long getEntry() {return entry;}
    public long getDelay() {return delay;}

    public void setDelay(long value) {this.delay = value;}

    @Override
    public int compareTo(Node node) {
        long now = System.currentTimeMillis();
        long thisDelay = TimeUnit.MILLISECONDS.toSeconds(now - this.entry);
        long otherDelay = TimeUnit.MILLISECONDS.toSeconds(now - node.entry);
        return Long.compare(otherDelay, thisDelay);
    }

    public void showData() {
        System.out.println("Node | Data : "+data+" time : "+time+" delay : "+delay+"s");
    }
}

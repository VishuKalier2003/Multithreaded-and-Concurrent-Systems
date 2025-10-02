
import java.util.concurrent.atomic.AtomicReference;

public class LockQueue {
    // atomic references used where the referencing is atomic (thread-safe)
    private final AtomicReference<AtomicNode> head, tail;

    public LockQueue() {        // constructor
        AtomicNode dummy = new AtomicNode();
        // using dummy node since dequeuing becomes easier
        this.head = new AtomicReference<>(dummy);
        this.tail = new AtomicReference<>(dummy);
    }

    public void enqueue(AtomicNode newNode) {
        // running the thread
        while(true) {
            // atomic tail reference extracted
            AtomicNode currTail = tail.get();
            // tail reference extracted
            AtomicNode nextTail = currTail.next.get();
            // If the atomic tail reference is lagging behind, push it to the end
            if(nextTail != null) {
                tail.compareAndSet(currTail, nextTail);
            }
            else {
                // Else check if next of next of tail is null if yes replace it with the newNode provided
                currTail.next.compareAndSet(null, newNode);
                // update the tail pointer to the new Node when atomic reference tail matches the current tail pointer
                tail.compareAndSet(currTail, newNode);
                return;
            }
        }
    }

    // In dequeue we do not actually remove the nodes, we simply update the atomic reference pointers
    public AtomicNode dequeue() {
        // running the thread
        while(true) {
            // atomic head reference extracted
            AtomicNode currHead = head.get();
            // atomic next head reference
            AtomicNode nextHead = currHead.next.get();
            if(nextHead == null)
                return null;    // If next head null, nothing beyond dummy node
            // Else if head is same as currHead (which will always be), update tail to next head
            if(head.compareAndSet(currHead, nextHead))
                // return the nextHead directly
                return nextHead;
        }
    }

    // check if queue empty
    public boolean isEmpty() {
        // If next node of head is empty, then empty queue
        return head.get().next.get() == null;
    }
}

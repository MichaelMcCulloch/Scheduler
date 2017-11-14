import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * WorkQueue
 * 
 * Purpose is to reduce contention on the work queue, 
 * Searchers will produce more nodes then they consume, 
 * and we don't want the Searcher to block when adding nodes. 
 */
public class WorkQueue<T> {
    private PriorityBlockingQueue<T> workQueue;
    private ConcurrentLinkedQueue<T> buffQueue; //wait-free
    private Thread qManager;

    public WorkQueue(){
        workQueue = new PriorityBlockingQueue<>();
        buffQueue = new ConcurrentLinkedQueue<>();
        qManager = new Thread(new QueueManager<T>());
        qManager.start();
    }

    /**
     * Add nodes to the buffer, these will make their way to the work queue eventually
     */
    public void add(Collection<T> objs){
        buffQueue.addAll(objs);
    }
    /**
     * Remove a node from the work queue
     */
    public synchronized T remove(){

        qManager.wait();
        T retVal = workQueue.remove();
        qManager.notify();

        return workQueue.remove();
    }

    /**
     * QueueManager
     */
    private class QueueManager<W> implements Runnable {

        /**
         * Continuously move elements from the buffer into the work queue.
         * yield on remove;
         */
        @Override
        public void run() {
            
        }
    }
    
}
import java.util.concurrent.PriorityBlockingQueue;

public class Model {
    protected static PriorityBlockingQueue<Node> queue;

    public Model(){
        queue = new PriorityBlockingQueue<>();
        
    }
    
}
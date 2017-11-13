import java.util.concurrent.PriorityBlockingQueue;

public class Model {
    public PriorityBlockingQueue<Node<Prob>> queue;

    public Model(Node<Prob> root){
        queue = new PriorityBlockingQueue<>();
        queue.add(root);
    }

    public static void main(String[] args) {
        Model m = new Model(null);
    }
    
}
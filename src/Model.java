import java.util.concurrent.PriorityBlockingQueue;

public class Model {
    public static PriorityBlockingQueue<Node> queue;

    public Model(Node root){
        queue = new PriorityBlockingQueue<>();
        queue.add(new Node<Integer>(null, 5));
        queue.add(new Node<Integer>(null, 1));
        queue.add(new Node<Integer>(null, 6));
        queue.add(new Node<Integer>(null, 2));
        queue.add(new Node<Integer>(null, 7));
        queue.add(new Node<Integer>(null, 3));
        queue.add(new Node<Integer>(null, 8));
        queue.add(new Node<Integer>(null, 4));
        
        
        
        

        while (!queue.isEmpty()){
            System.out.println(queue.remove().getInstance());
        }
    }
    
}
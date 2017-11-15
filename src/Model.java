import java.util.concurrent.PriorityBlockingQueue;

/**
 * Responsible for the creation of threads. Since all the peices of the tree exist here in the queue, this might as well be the model
 */

public class Model {

    public static Node<Prob> bestNode;

    public PriorityBlockingQueue<Node<Prob>> queue;

    public Model(Node<Prob> root) {
        queue = new PriorityBlockingQueue<>();
        queue.add(root);
    }

    public static void newBest(Node<Prob> candidate) {
        if (bestNode == null || candidate.compareTo(bestNode) > 0) {
            bestNode = candidate;
        }
    }

    public static void main(String[] args) {
        Node<Prob> n = new Node<Prob>(null, new Prob());
        Model m = new Model(n);
    }

}
public class Node<T extends Comparable<T>> implements Comparable<Node<T>> {

    private T instance; //This is an instance of PROB
    private Node<T> parent; //to propagate (un)solvedness to parent, (parent == null) == isRootNode;

    /**
     * Creates a new node
     * @param parent The parent of this node
     * @param instance The particular instance of Prob
     */
    public Node(Node<T> parent, T instance) {
        this.instance = instance;
        this.parent = parent;
    }

    /**
     * A node is better if it evaluates to a lower value, or if it is solved or unsolvable;
     */
    public int compareTo(Node<T> other) {
        return (instance.compareTo(other.instance));
    }

    /**
     * TODO: Evaluate the instance
     * @param instance The problem to be evaluated
     */
    private int fLEAF(T instance) {
        return 0;
        //return (Integer) instance;
    }
}
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.function.Function;

public class Node<T extends Comparable<? super T>> implements Comparable<Node<T>> {

    private T instance; //This is an instance of PROB
    private Node<T> parent; //to propagate (un)solvedness to parent, (parent == null) == isRootNode;
    private ArrayList<Node<T>> children;//childNodes of this one.
    private PriorityQueue<Division<T>> divisions; //List of devisions not yet tried.

    public Node(Node<T> parent, T instance, ArrayList<Node<T>> children, ArrayList<Function<T, ArrayList<T>>> funs) {
        this.instance = instance;
        this.parent = parent;
        this.children = children;
        // Construct Divs and rank them against this instance. 
        for (Function<T, ArrayList<T>> fun : funs) {
            Division<T> k = new Division<T>(fun, instance);
            divisions.add(k);
        }
    }

    /**
     * Creates a new node
     * @param parent The parent of this node
     * @param instance The particular instance of Prob
     */
    public Node(Node<T> parent, T instance) {
        this(parent, instance, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Add children to this node
     * @param children The new nodes (created by DIV) which you wish to add
     */
    public void addChild(ArrayList<Node<T>> children) {
        this.children.addAll(children);
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
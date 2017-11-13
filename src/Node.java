import java.util.ArrayList;
import java.util.PriorityQueue;

public class Node<T> implements Comparable<Node<T>> {

    private T instance;                 //This is an instance of PROB
    private Boolean solved;             //null = undecided. true = yes, false = unsolvable
    protected Integer score;            //only need to evaluate once
    private Node<T> parent;             //to propagate (un)solvedness to parent;
    private ArrayList<Node<T>> children;//childNodes of this one.
    private PriorityQueue divisions;    //List of devisions not yet tried.


    public Node(Node<T> parent, T instance, ArrayList<Node<T>> children, ArrayList<Division> divisions){
        this.instance = instance;
        this.parent = parent;
        this.children = children;
        this.score = fLEAF(instance);
        this.solved = null;
        this.divisions = new PriorityQueue<>(divisions);
    }

    /**
     * Creates a new node, solution yet unknown
     */
    public Node(Node<T> parent, T instance) {
        this(parent, instance, new ArrayList<>(), new ArrayList<>());
    }

    public void isSolved(boolean solved) {
        this.solved = solved;
    }

    public void addChild(Node<T> child) {
        children.add(child);
    }

    /**
     * A node is better if it evaluates to a lower value, or if it is solved or unsolvable;
     */
    public int compareTo(Node<T> other) {
        if (solved != null) return -1;
        return (this.score < other.score) ? -1 : 1;
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
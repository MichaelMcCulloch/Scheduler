import java.util.ArrayList;
import java.util.PriorityQueue;

public class Node<T extends Comparable<? super T>> implements Comparable<Node<T>> {

    private T instance; //This is a problem Instance
    private Boolean solved; //null = undecided. true = yes, false = unsolvable
    protected Integer score; //only need to evaluate once
    private Node<T> parent; //to propagate (un)solvedness to parent;
    private ArrayList<Node<T>> children; //childNodes of this one.
    private PriorityQueue divisions; //List of devisions not yet tried.

    /**
     * Creates a new node, solution yet unknown
     */
    public Node(Node<T> parent, T instance) {
        this.instance = instance;
        this.parent = parent;
        this.children = new ArrayList<>();
        divisions = new PriorityQueue<>(); //TODO: add default divisions list;
        solved = null;
        score = null;

    }

    /**
     * Creates a node with a known solution
     */
    public Node(Node<T> parent, T instance, boolean solved) {
        this.instance = instance;
        this.solved = solved;
        this.parent = parent;
        this.children = new ArrayList<>();
        score = null;

    }

    public void isSolved(boolean solved) {
        this.solved = solved;
    }

    public void addChild(Node<T> child) {
        children.add(child);
    }

    public T getInstance() {
        return instance;
    }
    

    // A better node is one with a lower (or better) fWert Value
    public int compareTo(Node<T> other) {
        if (this.solved != null) { //priority to solved or unsolved problems
            return 1; 
        } else {
            //only evaluate fLEAF once
            int myScore, otherScore;
            if (this.score != null){
                myScore = this.score;
            } else {
                myScore = fLEAF(this.instance);
                this.score = myScore;
            }
            if (other.score != null){
                otherScore = other.score;
            } else {
                otherScore = fLEAF(other.instance);
                other.score = otherScore;
            }
            int diff = myScore - otherScore; //positive if SELF better, negative if OTHER better;
            return diff;
        }
    }

    /**
     * TODO: Evaluate the instance
     * @param instance The problem to be evaluated
     */
    private int fLEAF(T instance) {
        return (Integer) instance;
    }
}
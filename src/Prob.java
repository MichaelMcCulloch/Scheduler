/**
 * Prob implements Comparable<Prob>
 */
public class Prob implements Comparable<Prob> {

    private int score; // so it can be ranked in a priority queue
    private Boolean solved;

    public Prob() {
        // Only want to eval fLEAF once. (and the searcher should be the one to do it)
        score = fLEAF();
        solved = null;
    }

    /**
     * Lower score wins, IE 0 Is a perfect score;
     * How nodes are scored against eachother, without respect to depth in the tree. If you want to also score by depth in the tree, do it in NODE.compareTo()
     */
    @Override
    public int compareTo(Prob other) {
        return (this.score < other.score) ? -1 : 1;
    }

    /**
    * TODO: Evaluate the instance
    * Used to rank a prob in the work queue
    * @param instance The problem to be evaluated
    */
    private int fLEAF() {
        return 0;

    }

}
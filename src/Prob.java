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

    @Override
    public int compareTo(Prob other) {
        if (solved != null)
            return -1; // priority to solved or unsolvable nodes (Really though if a prob is solved, it doesn't belong in the work queue)
        else
            return (this.score < other.score) ? -1 : 1;
    }

    /**
    * TODO: Evaluate the instance
    * @param instance The problem to be evaluated
    */
    private int fLEAF() {
        return 0;

    }

}
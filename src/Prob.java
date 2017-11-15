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

        return (this.score < other.score) ? -1 : 1;
        //if this changes, will need to change model.newBest()
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
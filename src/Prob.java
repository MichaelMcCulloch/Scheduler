/**
 * Prob implements Comparable<Prob>
 */
public class Prob implements Comparable<Prob>{

    private int score;
    private Boolean solved;

    public Prob(){
        // Only want to eval fLEAF once. (and the searcher should be the one to do it)
        score = fLEAF();
        solved = null;
    }

    @Override
    public int compareTo(Prob other) {
        if (solved != null) return -1;
        else return (this.score < other.score) ? -1 : 1;
    }


     /**
     * TODO: Evaluate the instance
     * @param instance The problem to be evaluated
     */
    private int fLEAF() {
        return 0;
        
    }
    
}
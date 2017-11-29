package scheduler;
/**
 * Pair
 */
public class Pair<X, Y> {

    private X first;
    private Y secnd;

    public Pair(X fst, Y snd) {
        this.first = fst;
        this.secnd = snd;
    }

    public X fst() {
        return first;
    }

    public Y snd() {
        return secnd;
    }
}
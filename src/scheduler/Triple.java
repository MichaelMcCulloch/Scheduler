package scheduler;
/**
 * Triple
 */
public class Triple<X, Y, Z> {

    private X first;
    private Y secnd;
    private Z third;

    public Triple(X fst, Y snd, Z trd) {
        this.first = fst;
        this.secnd = snd;
        this.third = trd;

    }

    public X fst() {
        return first;
    }

    public Y snd() {
        return secnd;
    }

    public Z trd() {
        return third;
    }
}
import java.util.ArrayList;

/**
 * Division. Still not sure where this should go. But it should be attached in some way to a node
 */
public class Division<T> implements Comparable{

    /**
     *
     */
    @Override
    public int compareTo(Object o) {
        return 0;
    }

    /**
     * the quality of this transition is somewhat dependant on the instance. to be used in compareTo
     */
    private int fTRANS(T instance){
        return 0;
    }

    /**
     * Create children out of this division
     */
    public ArrayList<T> divide(T instance){
        return new ArrayList<>();
    }
}
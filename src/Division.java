
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Division<T> extendFunction<T,T>  
 * T could be a Node or directly a Prob.
 */
public class Division<T> implements Function<T, ArrayList<T>>, Comparable<Division<T>> {

    private Function<T, ArrayList<T>> div; //pass in a function you would like to use to compute DIV
    private int score; // so it can be ranked in a priority queue

    public Division(Function<T, ArrayList<T>> fun, T instance) {
        this.div = fun;
        this.score = fTRANS(instance);

    }

    /**
     * this would be the DIV function
     */
    @Override
    public ArrayList<T> apply(T t) {
        return div.apply(t);
    }

    /**
     * where does this belong in its queue
     */
    @Override
    public int compareTo(Division<T> other) {
        return (this.score < other.score) ? -1 : 1;
    }

    /**
     * Valuation of this division with regard to an instance of a problem
     */
    private int fTRANS(T p) {
        Prob pr = (Prob) p;
        //do something with pr
        return 0;
    }

}
import java.util.*;
import java.util.concurrent.*;

/**
 * Main
 */
public class Main {

    public static List<Node<Prob>>[] makeWorkQueues(int poolSize,Node<Prob> root) {
        
        // Div it until there are more nodes than searchers
        Queue<Node<Prob>> startingNodes = new LinkedList<>();
        startingNodes.add(root);
        while (startingNodes.size() < poolSize){
            startingNodes.addAll(Searcher.div(startingNodes.remove()));
        }

        
        // Round robin add them to the work queues of the searcher.
        List<Node<Prob>>[] workQueues = new List[poolSize];
        for (int i = 0; i < startingNodes.size(); i++){
            if ((workQueues[i % poolSize]) == null)
                workQueues[i % poolSize] = new ArrayList<>();
            workQueues[i * poolSize].add(startingNodes.remove());
        }
        return workQueues;
    }

    public static void main(String[] args) {
        int poolSize = Runtime.getRuntime().availableProcessors();
        /**
         * TODO: 
         * Generate root node from input file
         */
        Node<Prob> root = null;

       
        List<Node<Prob>>[] workQueues = makeWorkQueues(poolSize, root);
        
        
        // create worker threads 
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        Searcher[] searchers = new Searcher[poolSize];
        for (int i = 0; i < poolSize; i++) {
            searchers[i] = new Searcher(workQueues[i]);
            pool.execute(searchers[i]);
        }

        //TODO: replace this with termination condition
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            //TODO: handle exception
        }

        Model.shutdownSignal = true;

        pool.shutdown();

        /**
         * TODO: Printout Model.best;
         */
    }
}
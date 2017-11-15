import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Main
 */
public class Main {

    public static ArrayList<Node<Prob>>[] makeWorkQueues(int poolSize,Node<Prob> root) {
        /**
         * Div it untill there are more nodes than searchers
         */
        Queue<Node<Prob>> startingNodes = new LinkedList<>();
        startingNodes.add(root);
        while (startingNodes.size() < poolSize){
            startingNodes.addAll(Model.div(startingNodes.remove()));
        }

        /**
         * Round robin add them to the work queues of the searcher.
         */
        ArrayList<Node<Prob>>[] workQueues = new ArrayList[poolSize];
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
         * Generate root node from input file
         */
        Node<Prob> root = null;

       
        ArrayList<Node<Prob>>[] workQueues = makeWorkQueues(poolSize, root);
        
        /**
         * create worker threads
         */
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        Searcher[] searchers = new Searcher[poolSize];
        for (int i = 0; i < poolSize; i++) {
            searchers[i] = new Searcher(workQueues[i]);
            pool.execute(searchers[i]);
        }

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            //TODO: handle exception
        }

        Model.shutdownSignal = true;

        pool.shutdown();
    }
}
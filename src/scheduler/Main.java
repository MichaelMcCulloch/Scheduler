package scheduler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Maintest
 */
public class Main {

    public static List<Schedule>[] makeWorkQueues(int poolSize,Schedule root) {
        
        // Div it until there are more nodes than searchers
        Queue<Schedule> startingNodes = new LinkedList<>();
        startingNodes.add(root);
        try {
	        while (startingNodes.size() < poolSize){
	        	//System.out.println(startingNodes);
	        	Schedule A =startingNodes.remove();
	        	//System.out.println(A.div(Model.getInstance().checkBest));
	            startingNodes.addAll(A.div(Model.getInstance().checkBest, Model.getInstance().checkBound));
	        }
        } catch (NoSuchElementException e) {
        	System.out.println("oh noes!");
        }

        
        // Round robin add them to the work queues of the searcher.
        List<Schedule>[] workQueues = new List[poolSize];
        int startingNodeSize = startingNodes.size();
        for (int i = 0; i < startingNodeSize; i++){
            if ((workQueues[i % poolSize]) == null)
                workQueues[i % poolSize] = new ArrayList<>();
            workQueues[i % poolSize].add(startingNodes.remove());
        }
        return workQueues;
    }


    

    public static void main(String[] args) {
        int poolSize = Runtime.getRuntime().availableProcessors();
        
        File f;
        Parser p;
        try {
            Scanner user = new Scanner(System.in);
            System.out.println("Point me to the input file:");
            String filename = user.nextLine();
            user.close();
            f = new File(filename);
            p = new Parser(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        Schedule root = p.getInitialInstance();
        List<Schedule> single = new ArrayList<>();
        single.add(root);
        
        
        
        ExecutorService pool;
        if (Model.getInstance().getCourses().size() < 16) {
        	pool = Executors.newFixedThreadPool(1);
        	Searcher searcher = new Searcher(single);
        	pool.execute(searcher);
        } else {
        	List<Schedule>[] workQueues = makeWorkQueues(poolSize, root);
            
            
            // create worker threads 
            pool = Executors.newFixedThreadPool(poolSize);
            Searcher[] searchers = new Searcher[poolSize];
            for (int i = 0; i < poolSize; i++) {
            	if (workQueues[i] == null || workQueues[i].isEmpty()) continue;
                searchers[i] = new Searcher(workQueues[i]);
                pool.execute(searchers[i]);
            }
        }
        
        

        //TODO: replace this with termination condition
        
        try {
            Thread.sleep(6000);
        } catch (Exception e) {
            //TODO: handle exception
        }
        
        
        Searcher.stop();
        pool.shutdown();
        
        Schedule best = Model.getInstance().getBest();
        
        System.out.println(best.prettyPrint());
        /**
         * TODO: Printout Model.best;
         */
    }
}
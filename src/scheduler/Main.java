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
        
        String filename;   
        int weightMin;
        int weightPref;
        int weightSectDiff;
        int weightTogether;
        if (args.length < 5){
            System.out.println("USAGE: input.txt wMinFilled wPref wSecDiff wPair" );
            return;
        } else {
            filename = args[0];
            weightMin = Integer.parseInt(args[1]);
            weightPref = Integer.parseInt(args[2]);
            weightSectDiff = Integer.parseInt(args[3]);
            weightTogether = Integer.parseInt(args[4]);
        }
        File f;
        Parser p;
        try {
            f = new File(filename);
            p = new Parser(f, weightMin, weightPref, weightSectDiff, weightTogether);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        Schedule root = p.getInitialInstance();

       
        List<Schedule>[] workQueues = makeWorkQueues(poolSize, root);
        
        
        // create worker threads 
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        Searcher[] searchers = new Searcher[poolSize];
        for (int i = 0; i < poolSize; i++) {
        	if (workQueues[i] == null || workQueues[i].isEmpty()) continue;
            searchers[i] = new Searcher(workQueues[i]);
            pool.execute(searchers[i]);
        }
        
        pool.shutdown();
        Scanner user = new Scanner(System.in);
        System.out.println("Enter \"quit\" to quit");
        try{
            Thread.sleep(1000);
        } catch (Exception e) {}
        while( !pool.isTerminated() && !user.hasNextLine()){
            if (user.hasNextLine()){
                if (user.nextLine().equals("quit")){
                    searchers[0].stop();
                }
            }
        };
        
        
        Schedule best = Model.getInstance().getBest();
        
        System.out.println(best.prettyPrint());
        
        /**
         * TODO: Printout Model.best;
         */
    }
}
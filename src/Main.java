import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main
 */
public class Main {

    public static List<Schedule>[] makeWorkQueues(int poolSize,Schedule root) {
        
        // Div it until there are more nodes than searchers
        Queue<Schedule> startingNodes = new LinkedList<>();
        startingNodes.add(root);
        
        //might produce fewer than poolSize nodes
        while (!startingNodes.isEmpty() && startingNodes.size() < poolSize){
        	Schedule next = startingNodes.remove();
        	List<Schedule> offspring = next.div(Model.getInstance().checkBest);
        	if (offspring.isEmpty()) continue;
            startingNodes.addAll(offspring);
        }

        if (startingNodes.isEmpty()) return null;
        
        List<Schedule>[] workQueues = new List[poolSize];
        int items = startingNodes.size();
        for (int i = 0; i < items; i++){
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
            System.err.println("Enter the file name:");
            String filename = user.nextLine();
            f = new File(filename);
            p = new Parser(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        Schedule root = p.getInitialInstance();

        Model model = Model.getInstance();
        
        List<Schedule>[] workQueues = makeWorkQueues(poolSize, root);
        
        if (workQueues == null) {
        	if (model.getBest() == null) {
        		System.out.println("No solution found");
        		return;
        	} else {
        		System.out.println(model.getBest().prettyPrint());
        		return;
        	}
        } else if (workQueues.length < poolSize) {
        	poolSize = workQueues.length; //small jobs don't get full parallelism. oh well.
        }
        
        // create worker threads 
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        Searcher[] searchers = new Searcher[poolSize];
        for (int i = 0; i < poolSize; i++) {
            searchers[i] = new Searcher(workQueues[i]);
            pool.execute(searchers[i]);
        }

        /*
        //TODO: replace this with termination condition
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            //TODO: handle exception
        }
         */
        
        Scanner keyboard = new Scanner(System.in);
        while ( !keyboard.next().equals("quit") );
        
        Searcher.stop();
        pool.shutdown();

        if (model.getBest() == null) {
    		System.out.println("\nNo solution found");
    		return;
    	} else {
    		System.out.println(model.getBest().prettyPrint());
    		return;
    	}
    }
}
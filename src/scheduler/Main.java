package scheduler;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import scheduler.Model.Penalty;
import scheduler.Model.Weight;

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
	            startingNodes.addAll(A.div(Model.getInstance().checkBest, Searcher.bound));
	        }
        } catch (NoSuchElementException e) {
        	
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
        int poolSize = Model.getInstance().numThreads;
        
        String filename;
        Map<Model.Weight, Integer> weights = new HashMap<>();
        Map<Model.Penalty,Integer> penalties = new HashMap<>();
        if (args.length < 9){
            System.out.println("USAGE: input.txt wMinFilled wPref wSecDiff wPair pen_coursemin pen_labmin pen_section pen_notpaired" );
            return;
        } else {
            filename = args[0];
            
            weights.put(Weight.MinFilled, Integer.parseInt(args[1]));
            weights.put(Weight.Preference, Integer.parseInt(args[2]));
            weights.put(Weight.SectionDifference, Integer.parseInt(args[3]));
            weights.put(Weight.Paired, Integer.parseInt(args[4]));

            penalties.put(Penalty.CourseMin, Integer.parseInt(args[5]));
            penalties.put(Penalty.LabMin, Integer.parseInt(args[6]));
            penalties.put(Penalty.SectionDifference, Integer.parseInt(args[7]));
            penalties.put(Penalty.Pair, Integer.parseInt(args[8]));
        }
        File f;
        Parser p;
        try {
            f = new File(filename);
            p = new Parser(f, weights, penalties);
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
        
        System.out.println("Press \'Return\' key to quit");
        
         
        Reader s = new Reader();
		Thread t = new Thread(s);
		t.start();
        
        while(!Searcher.finished() && !s.input){
        };
        
        Schedule best = Model.getInstance().getBest();
        if (best==null) System.out.println("No schedule found!");
        else {
            String bestStr = best.prettyPrint();
            System.out.println(bestStr);
            File record = new File(System.getProperty("user.dir") + File.separator + "best.txt");
            try {
                FileWriter fw = new FileWriter(record);
                fw.write(bestStr);
                fw.close();
			} catch (IOException e) {
                //whoops oh well, can't stop nop
            }
        }
        
        System.exit(0);
        
    }
}
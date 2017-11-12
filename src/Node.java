public class Node<Prob> {

    private Prob instance;
    private Boolean solved;

    public Node(Prob pr){
        instance = pr;
        solved = null;
    }
    public Node(Prob pr, boolean sg){
        instance = pr;
        solved = sg; 
    }

}
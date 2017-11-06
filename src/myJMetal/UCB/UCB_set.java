package myJMetal.UCB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main object for use Upper Confidence Bound component
 *  1. Create this with your parameter or without parameter (default)
 *  2. You need to define a set and a tag of something to adjust
 *     2.1 then use <i>addSelector</i>
 *  3. At run (main loop of MOEA)
 *     3.1 Select operator if ucbSet is working
 *     3.2 Adjust Sliding window and calculate credit assignment is ucbSet is working
 * 
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public class UCB_set {
    
    public Integer initDefault;
    public Integer endDefault ;
    
    public Integer maxStep ;
    public Integer step;
    
    public Double C;    //Coefficient to balance exploration and exploitation
    public Double D;    //Decaying Factor
    
    private int WS;                     //Sliding Window Size
    //private Map<String, UCB> selector;  //label and UCB correspondent
    private Map<String, UCBInterface> selector;  //label and UCB correspondent
    private List<Double> SW_reward;     //value calculate with fitness function
    
    private Double x, y;//for fitness value of vector and its child

    public UCB_set() {
        this.initDefault = 0;
        this.endDefault  = 0;
        this.maxStep = 1000;
        this.step = 0 ;
        this.C = 5.0;
        this.D = 1.0;
        
        WS = 100;
        selector = new HashMap<>();
        SW_reward = new ArrayList<>();
    }
    
    
    
    /**
     * Create UCB object
     * @param popSize Population of algorithm to use this
     * @param initDefault number of evaluation to initialize this
     * @param endDefault number of evaluation to finish this
     */
    public UCB_set(int WS, int initDefault, int endDefault) {
        this.WS = WS;
        this.initDefault = initDefault;
        this.endDefault  = endDefault;
        this.maxStep = 1000;
        this.step = 0 ;
        this.C = 5.0;
        this.D = 1.0;
       
        selector = new HashMap<>();
        SW_reward = new ArrayList<>();
    }
    
    
     public UCB_set(int WS, int initDefault, int endDefault, int times, Double C, Double D) {
         this.WS = WS;
        this.initDefault = initDefault;
        this.endDefault  = endDefault;
        this.maxStep = times;
        this.step = 0 ;
        this.C = C;
        this.D = D;
        
        
        selector = new HashMap<>();
        SW_reward = new ArrayList<>();
    }
     
    
    /**
     * Add an UCB
     * @param name
     * @param ucb 
     */
    public void addSelector(String name, UCBInterface ucb){
        selector.put(name, ucb);
        ucb.setSetUCB(this);
    }
    
    public void addSelector(String name, Object[] operator_pool){
        UCBInterface ucb = new UCB(operator_pool);
        selector.put(name, ucb);
        ucb.setSetUCB(this);
    }
    
    
    
    public void selectOperators(){
        for (Map.Entry<String, UCBInterface> entrySet : selector.entrySet()) {
            UCBInterface value = entrySet.getValue();
            value.selectOperator();   
        }
    }
    
    public Object getOperator(String name){
        return selector.get(name).getOperator();
    }
    
    public Integer[] getHistory(String name){
        return selector.get(name).getHistory_using();
    }
    
    //x - > vector n
    //y - > vector child
    public void adjustSlidingWindow(){
        if (SW_reward.size() == WS ) {
            SW_reward.remove(0);
        }
        Double v = (x - y) / x;
        SW_reward.add(v);
        for (Map.Entry<String, UCBInterface> entrySet : selector.entrySet()) {
            entrySet.getValue().adjustSlidingWindow();
        }
    }
    
    public void creditAssignment(){
        for (Map.Entry<String, UCBInterface> entrySet : selector.entrySet()) {
            entrySet.getValue().creditAssignment();
        }
    }

    public int getWS() {
        return WS;
    }

    public List<Double> getSW_reward() {
        return SW_reward;
    }
    
    public boolean isWSfull(){return (WS <= SW_reward.size());}
    
    
    @Override
    public String toString(){
        String str="Operators = {";
        int n=1;
        for (Map.Entry<String, UCBInterface> entrySet : selector.entrySet()) {
            String key = entrySet.getKey();
            UCBInterface value = entrySet.getValue();
            
            str += key+":"+value.getOperator();
            if(n<selector.size()){str+=", ";}
            n++;
        }
        str+="}";
        return str;
    }
    
    
    public String info(){
        String str = "UCB{init="+initDefault+", end="+endDefault+", "
                + "C="+C+", D="+D+", MaxStep="+maxStep+", ws="+WS+"}";
        
        return str;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getInitDefault() {
        return initDefault;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
    
    
    
    public boolean isWorking(int evaluations, int maxEvaluations){
        return (evaluations > initDefault) //more than initial value
                && (evaluations + endDefault < maxEvaluations);//less than end value
    }
    
    public void printHistory(String tag){
        UCBInterface ucb = selector.get(tag);
        Integer[] hist = ucb.getHistory_using();
        Object[] pool = ucb.getOperatorPool();        
        for (int i = 0; i < hist.length; i++) {
            System.out.println( pool[i] +" = "+hist[i]);
        }
    }
    
    public void nextStep(){
        if(step+1 >= maxStep){
            step=0;
        }else{
            step++;
        }
    }
}

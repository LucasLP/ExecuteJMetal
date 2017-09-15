package ic.moeadHH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lucas
 */
public class UCB_set {
    
    public Integer initDefault=0;
    public Integer times = 1000;
    public Double C = 5.0;//5    //Coefficient to balance exploration and exploitation
    public Double D = 1.0;//1    //Decaying Factor
    
    private int WS;                     //Sliding Window Size
    private Map<String, UCB> selector;  //label and UCB correspondent
    private List<Double> SW_reward;     //value calculate with fitness function
    
    public UCB_set(int popSize) {
        WS = (int)(0.5 * popSize );//0.5
        selector = new HashMap<>();
        SW_reward = new ArrayList<>();
    }
    
    
    
    public void addSelector(String name, UCB ucb){
        selector.put(name, ucb);
        ucb.setSetUCB(this);
    }
    
    public void selectOperators(){
        for (Map.Entry<String, UCB> entrySet : selector.entrySet()) {
            UCB value = entrySet.getValue();
            value.selectOperator();   
        }
    }
    
    public Object getOperator(String name){
        return selector.get(name).getOperator();
    }
    
    //x - > vector n
    //y - > vector child
    public void adjustSlidingWindow(Double x, Double y){
        if (SW_reward.size() == WS ) {
            SW_reward.remove(0);
        }
        Double v = (x - y) / x;
        SW_reward.add(v);
        for (Map.Entry<String, UCB> entrySet : selector.entrySet()) {
            entrySet.getValue().adjustSlidingWindow();
        }
    }
    
    public void creditAssignment(){
        for (Map.Entry<String, UCB> entrySet : selector.entrySet()) {
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
        for (Map.Entry<String, UCB> entrySet : selector.entrySet()) {
            String key = entrySet.getKey();
            UCB value = entrySet.getValue();
            
            str += key+":"+value.getOperator();
            if(n<selector.size()){str+=", ";}
            n++;
        }
        str+="}";
        return str;
    }
}

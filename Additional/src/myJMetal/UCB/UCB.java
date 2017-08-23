package myJMetal.UCB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author lucas
 */
public class UCB {
    private UCB_set setUCB;

    private Object K_operators[];
    private Integer n[];
    private Double FRR[];
    private List<Integer> SW_operator;//list of sliding window of operators

    
    private Double Reward[], DecayedReward[] ;//optimization of memory alocation
    
    private int lastOperator;//index of last operator choose
    
    private Integer[] history_using;

    public UCB(Object operator_pool[]){
        SW_operator = new ArrayList<>();
        K_operators = operator_pool;
        n = new Integer[K_operators.length];
        FRR = new Double[K_operators.length];
        
        Reward = new Double[K_operators.length];
        DecayedReward = new Double[K_operators.length];
        history_using = new Integer[K_operators.length];
        
        for (int i = 0; i < K_operators.length; i++) {
            n[i] = 0;
            FRR[i] = 0.0;
            history_using[i] = 0;
        }
        lastOperator=0;//-1
    }

    /**
     * Select operator and put in 'lastOperator' variable
     */
    public void selectOperator() {
        Integer op;
        if(isSWsizeMax()){
            Double SelectedOperator;//value
            Double sum, aux;
            //----First value
            sum = 0.0;
            for (int j = 0; j < K_operators.length; j++) {
                sum += n[j];
            }
            sum = 2 * Math.log(sum);
            SelectedOperator = FRR[0] + (setUCB.C * Math.sqrt(sum / n[0]));
            op = 0;
            //----
            for (int j = 1; j < K_operators.length; j++) {
                aux = FRR[j] + (setUCB.C * Math.sqrt(sum / n[j]));
                if (aux > SelectedOperator) {
                    SelectedOperator = aux;
                    op = j;
                }
            }
        }else{
            op = (new Random()).nextInt(K_operators.length);//PseudoRandom.randInt(0, K_operators.length-1);
        }
        lastOperator = op;
        history_using[lastOperator]++;
    }

 
    public void adjustSlidingWindow(){
        if (SW_operator.size() == setUCB.getWS() ) {
            SW_operator.remove(0);
        }
        SW_operator.add(lastOperator);
       // lastOperator=-1;
    }

    public void creditAssignment() {
        if(isSWsizeMax()){
            Integer op;
            
            for (int j = 0; j < K_operators.length; j++) {
                Reward[j] = 0.0;
                n[j] = 0;
            }
            for (int j = 0; j < setUCB.getWS(); j++) {
                op = SW_operator.get(j);
                Reward[op] += setUCB.getSW_reward().get(j);
                n[op]++;
            }
            Integer rank[] = findRank(Reward);//rank reward in descending order
            Double TotalDecayedReward = 0.0;
            for (int j = 0; j < K_operators.length; j++) {//compute decayedReward
                DecayedReward[j] = Math.pow(setUCB.D, rank[j]) * Reward[j] ;
                TotalDecayedReward += DecayedReward[j];//compute totaldecayedReward
                //n[j] = 0;
            }
            for (int j = 0; j < K_operators.length; j++) {//compute FRR
                FRR[j] = DecayedReward[j] / TotalDecayedReward;
            }
        }
    }

    
    private static Integer[] findRank(Double[] x) {
        List<Double> lst = new ArrayList<Double>();
        Integer[] rank = new Integer[x.length]; // maximum length for already unique array
        for (double d : x) {
            if (lst.indexOf(d) == -1) //only unique elements in list
            {
                lst.add(d);
            }
        }
        Collections.sort(lst);
        for (int i = 0; i < x.length; i++) {
            rank[i] = lst.indexOf(x[i]);
        }
        return rank;
    }

    
    
    public boolean isSWsizeMax() {
        return (SW_operator.size() == setUCB.getWS());
    }

    public Integer getK() {
        return K_operators.length;
    }

    public Object getOperator() {
        return K_operators[lastOperator];
    }

   public void setSetUCB(UCB_set setUCB) {
        this.setUCB = setUCB;
    }

    public Integer[] getHistory_using() {
        return history_using;
    }
   
    public Object[] getOperatorPool(){
        return K_operators;
    }
   
}

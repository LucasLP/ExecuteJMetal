package myJMetal.UCB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public class UCB implements UCBInterface{
    
    private WeightFunction function;
    
    private UCB_set setUCB;

    private Object K_operators[];
    private Integer n[];
    private Double FRR[];
    private List<Integer> SW_operator;//list of sliding window of operators

    
    private Double Reward[], DecayedReward[] ;//optimization of memory alocation
    
    private int lastOperator;//index of last operator choose
    
    private Integer[] history_using;
    private List<Object> operatorUsed;

	//Don't use any WeightFunction
    public UCB(Object operator_pool[]){
        operatorUsed = new ArrayList<>();
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
        function = WeightFunction.Null;
    }

    public UCB(Object operator_pool[], WeightFunction function){
        operatorUsed = new ArrayList<>();
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
        this.function = function;
    }
    /**
     * Select operator and put in 'lastOperator' variable
     */
    @Override
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
        operatorUsed.add(getOperator());
    }

 
    @Override
    public void adjustSlidingWindow(){
        if (SW_operator.size() == setUCB.getWS() ) {
            SW_operator.remove(0);
        }
        SW_operator.add(lastOperator);
        
       // lastOperator=-1;
    }

    @Override
    public void creditAssignment() {
        if(isSWsizeMax()){
            Integer op;
            
            for (int j = 0; j < K_operators.length; j++) {
                Reward[j] = 0.0;
                n[j] = 0;
            }
            for (int j = 0; j < setUCB.getWS(); j++) {
                op = SW_operator.get(j);
                //Reward[op] += setUCB.getSW_reward().get(j);
                
                //The Sliding window is inverted, the last element is the last use
                //UCB3f Reward[op] += setUCB.getSW_reward().get(j)  *  function.decayFactorFunction(setUCB.getWS()-j,setUCB.getWS()+1 );
                Reward[op] += setUCB.getSW_reward().get(j)  *  function.decayFactorFunction((double)j+1, (double)setUCB.getWS() );
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
        List<Double> lst = new ArrayList<>();
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

    
    
    @Override
    public boolean isSWsizeMax() {
        return (SW_operator.size() == setUCB.getWS());
    }

    @Override
    public Integer getK() {
        return K_operators.length;
    }

    @Override
    public Object getOperator() {
        return K_operators[lastOperator];
    }

    @Override
   public void setSetUCB(UCB_set setUCB) {
        this.setUCB = setUCB;
    }

    @Override
    public Integer[] getHistory_using() {
        return history_using;
    }
   
    @Override
    public Object[] getOperatorPool(){
        return K_operators;
    }
   
    
    
    /**
     * Will save operator used append in new line
     * @param nameAlgorithm for file names
     */
    public void printOperatorsUsed(String nameAlgorithm, String problem){
        String outputDir = "experiment/MyExperiments/ucbHistory/"+nameAlgorithm+"/";
        File f = new File(outputDir);
        if(!f.exists()){
            f.mkdirs();
        }
        File file = new File(outputDir+"operators_"+problem+".dat");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(UCB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String str ="";
        int i = 0;
        for (Object op : operatorUsed) {
            if(i>=60){
                str += op +" ";
            }
            i++;
        }
        
        try {
            FileWriter fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(str);
            pw.flush();
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(UCB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

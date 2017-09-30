
package myJMetal.Chart;

import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.DoubleSolution;

/**
 *
 * @author lucas
 */
public interface HistoricAlgorithm {
    public HistoryData getHistory(String indicator);
    public void setRunNumber(Integer runsNumber);
    
    
    
    public static boolean testToCalculate(Integer evaluations, Integer maxEvaluations){
        return (evaluations%(maxEvaluations/100)==0); 
    }
    
    public static void calculateIndicators(Integer evaluations, Integer maxEvaluations, String problemName, List<DoubleSolution> population, Map<String, HistoryData> history) {
        int timeIndex = (int) (evaluations / (maxEvaluations / 100)) - 1;
        Map<String, Double> indicators = HistoryData.calculateQualityIndicator(population, problemName, history.keySet());
        for (String str : history.keySet()) {//for each indicator in history
            history.get(str).addData(indicators.get(str), timeIndex);//add data
        }
    }
}

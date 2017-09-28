package myJMetal.Chart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

/**
 *
 * @author lucas
 */
public class HistoryData {
    private Integer testId;
    
    private Integer numberOfData;
    private Integer numberOfTest;
    
    private Double[][] history;
   

    public HistoryData(Integer numberOfTest) {
        this.testId = -1;
        this.numberOfData = 100;
        this.numberOfTest = numberOfTest;
        history = new Double[this.numberOfData][this.numberOfTest];

        for (int i = 0; i < this.numberOfData; i++) {
            for (int j = 0; j < this.numberOfTest; j++) {
                history[i][j] = 0.0;
            }
        }
        
    }
    
    public void addData(Double data, Integer timeIndex){
        if(timeIndex==0)testId++;
        history[timeIndex][testId] = data;
    }
    
    /**
     * Will save each line how an test
     * @param outputDir will create the directory if doesn't exist
     * @param indicator for file name
     * @param nameAlgorithm for file name
     * @param problemName  for file name
     */
    public void printAllHistory(String outputDir, String indicator, String nameAlgorithm, String problemName){
        File f = new File(outputDir);
        if(!f.exists()){
            f.mkdirs();
        }
        try (OutputStream os = new FileOutputStream(outputDir+"data_"+indicator+"_"+nameAlgorithm+"_"+problemName+".dat")) {
            PrintStream ps = new PrintStream(os);
            for (int data = 0; data < numberOfData; data++) {
                ps.print(data);
                if(data<numberOfData-1){
                    ps.print("\t");
                }
            }
            ps.print("\n");
            for (int test = 0; test < numberOfTest; test++) {
                for (int data = 0; data < numberOfData; data++) {
                    ps.print(history[data][test]);
                    if(data<numberOfData-1){
                        ps.print("\t");
                    }
                }
                ps.print("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(GenerateEvolutionChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static Map<String, Double> calculateQualityIndicator(List<DoubleSolution> population, String problemName) {
        Front referenceFront;
        Map<String, Double> indicators = new HashMap();
        try {
            referenceFront = new ArrayFront("/resources/pareto_fronts/" + problemName + ".pf");
            FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
            Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
            Front normalizedFront = frontNormalizer.normalize(new ArrayFront(population));
            List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront);
            
            indicators.put("HV", new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
            indicators.put("Epsilon", new Epsilon<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
            indicators.put("IGD", new InvertedGenerationalDistancePlus<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
            indicators.put("Spread", new Spread<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
        } catch (FileNotFoundException ex) { 
            Logger.getLogger(GenerateEvolutionChart.class.getName()).log(Level.SEVERE, null, ex);
        }
        return indicators;
    }
}

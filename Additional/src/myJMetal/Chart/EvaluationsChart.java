
package myJMetal.Chart;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import myJMetal.Configuration;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDRAUCBv4;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

/**
 *
 * @author Lucas Prestes
 */
public class EvaluationsChart {
    Configuration configuration;
    int algorithmNum ;
    
    int lastIndex;
    
    String[] names;
    List<Double>[] values;
    
    public EvaluationsChart(Configuration configuration, int algorithmNum, List<String> tags) {
        this.configuration = configuration;
        this.algorithmNum = algorithmNum;
        names = new String[this.algorithmNum];
        int n =0;
        for (String tag : tags) {
            names[n] = tag;
            n++;
        }
        
        values = new ArrayList[this.algorithmNum];
        for (int i = 0; i < values.length; i++) {
            values[i]=new ArrayList<>();
        }
        lastIndex = -1;
    }

    public int getNextIndex() {
        lastIndex++;
        return lastIndex;
    }

    
    public void printQualityIndicator(int algorithmIndex, List<DoubleSolution> population, int evaluation){
        if(configuration.generateChart){
            if( evaluation%(configuration.MaxEvaluations/10)==0){
            Front referenceFront;
            try {
                referenceFront = new ArrayFront("/resources/pareto_fronts/"+configuration.parameters.get("--problem")+".pf");
                FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;
                Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;
                Front normalizedFront = frontNormalizer.normalize(new ArrayFront(population)) ;
                List<PointSolution> normalizedPopulation = FrontUtils
                    .convertFrontToSolutionList(normalizedFront) ;
                Double hv = new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation);
                
                
                values[algorithmIndex].add(BigDecimal.valueOf(hv).setScale(3, RoundingMode.HALF_UP).doubleValue());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MOEADDRAUCBv4.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }
  }
    
    
    public void generate(String problem){//configuration.baseDirectory+"/"+configuration.experimentName+"/
         try (OutputStream os = new FileOutputStream("data_"+problem+".dat")) {
            PrintStream ps = new PrintStream(os);
            ps.println(generateDataFile());
        } catch (IOException ex) {
            Logger.getLogger(EvaluationsChart.class.getName()).log(Level.SEVERE, null, ex);
        } 
         
        try (OutputStream os = new FileOutputStream("script_"+problem+".R")) {
            PrintStream ps = new PrintStream(os);
            ps.println(generateRChart(problem));
        } catch (IOException ex) {
            Logger.getLogger(EvaluationsChart.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    
    private String generateDataFile(){
        String str = "";
        for (int i = 0; i < names.length-1; i++) {
            str += names[i]+"\t";
        }
        str+=names[names.length-1];
        for (int index = 0; index < values[0].size(); index++) {
            str += "\n";
            for (int i = 0; i < algorithmNum-1 ; i++) {
                str+=values[i].get(index)+"\t";
            }
            str+=values[algorithmNum-1].get(index);
        }
        return str;
    }
    
    private String generateRChart(String problem){
        String str = 
           "# Read car and truck values from tab-delimited autos.dat\n"
                + "algorithms <- read.table(\"data_"+problem+".dat\", header=T, sep=\"\\t\") \n"
                + "\n"
                + "# Compute the max and min y \n"
                + "max_y <- max(algorithms)\n"
                + "min_y <- min(algorithms)\n"
                + "\n"
                + "# Define colors to be used for alg1, alg2, alg3\n"
                + "plot_colors <- c(\"blue\",\"red\",\"forestgreen\", \"black\",\"yellow\", \"brown\", \"darkorange\")\n"
                + "plot(algorithms$"+names[0]+", type=\"o\", col=plot_colors[1],  ylim=c(min_y,max_y), axes=FALSE, ann=FALSE)\n"
                + "\n"
                + "# Make x,y axis \n"
                + "axis(1, at=1:10,lab=c(10,20,30,40,50,60,70,80,90,100))\n"
                + "axis(2, at=seq(min_y, max_y, 0.05))\n"
                + "\n"
                + "# Create box around plot\n"
                + "box()\n"
                + "\n";
                for (int i = 1; i < algorithmNum; i++) {
                    str += "lines(algorithms$"+names[i]+", type=\"o\", pch="+i+", lty=2, col=plot_colors["+(i+1)+"])\n";
                }
                str += "\n"
                + "# Create a title bold/italic font\n"
                + "title(main=\"Quality Indicator Evolution for "+problem+"\", font.main=4)\n"
                + "\n"
                + "# Label the x and y axes\n"
                + "title(xlab= \"% of Evolutions\")\n"
                + "title(ylab= \"Indicator value\")\n"
                + "\n"
                + "# Create a legend\n"
                + "legend(\"bottomright\", names(algorithms), cex=0.8, col=plot_colors, pch=21:23, lty=1:3);\n";
        
        return str;
    }
    
    
    
}

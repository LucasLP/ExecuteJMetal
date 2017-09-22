
package myJMetal.Chart;

import java.util.List;
import myJMetal.Configuration;

/**
 *
 * @author lucas
 */
public interface EvaluationsAlgorithm {
    
    public void setConfigurations(Configuration configuration);
    
    public List<Double> getIndicatorValues();
    
}

//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package JMetalMain;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMException;

/**
 * Class for configuring and running the MOEA/D algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es> (initial ideia and version)
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> (reorganize and create configuration class)
 */
public class MyRunner extends AbstractAlgorithmRunner {
    
    private Configuration configuration ;

    public MyRunner(Configuration configuration) {
        this.configuration = configuration;
    }
    
    
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.MOEADRunner problemName [referenceFront]
   */
  public void execute(String[] args) throws FileNotFoundException, JMException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;

    String problemName ;
    String referenceParetoFront = "";
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F2";
      referenceParetoFront = "/resources/pareto_fronts/LZ09_F2.pf";
    }

    problem = (DoubleProblem)ProblemUtils.<DoubleSolution> loadProblem(problemName);
    for (String alg : configuration.NameList) {
        System.out.println("Executing: "+alg);
      
        algorithm = configuration.create(problem,alg);//null);

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

        List<DoubleSolution> population = algorithm.getResult() ;
        long computingTime = algorithmRunner.getComputingTime() ;

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
          printQualityIndicators(population, referenceParetoFront) ;
        }
       // System.out.println("================================");
    }
  }
}

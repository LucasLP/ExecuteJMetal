//  MOEAD_DRA_Settings.java 
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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




import jmetal.experiments.settings.*;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;
import oldMethod.ConfigurationExperiment;

/**
 * Settings class of algorithm MOEA/D-DRA
 */
public class MOEAD_DRA_UCBv1_Settings extends Settings {
  public double CR_ ;
  public double F_  ;
  public String DE_VARIANT_;
  public int populationSize_ ;
  public int maxEvaluations_ ;
  public int finalSize_      ;

  public double mutationProbability_          ;
  public double mutationDistributionIndex_ ;

  public int T_        ;
  public double delta_ ;
  public int nr_    ;

  public String dataDirectory_  ;

  /**
   * Constructor
     * @param problem
   */
  public MOEAD_DRA_UCBv1_Settings(String problem) {
    super(problem);
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    e.printStackTrace();
    }      

    // Default experiments.settings
    CR_ =1.0 ;
    F_  = 0.5 ;
    DE_VARIANT_ = "rand/1/bin";
    
    //populationSize_ = 600;
    if(problem_.getNumberOfObjectives() == 2)
        populationSize_ = 600;
    else
        populationSize_ = 1000;
    //maxEvaluations_ = 300000;
    maxEvaluations_ = 300000;
    
    finalSize_ = populationSize_ ;
   
   
    mutationProbability_ =  1.0 /problem_.getNumberOfVariables() ;
    mutationDistributionIndex_ =  20;

    /*
    T_ = (int) (0.1 * populationSize_);
    delta_ = 0.9;
    nr_ = (int) (0.01 * populationSize_);
    */
    T_ = 20;
    delta_ = 0.9;
    nr_ = 2;

    // Directory with the files containing the weight vectors used in 
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
    //dataDirectory_ =  "/Users/antonio/Softw/pruebas/data/MOEAD_parameters/Weight" ;
    //dataDirectory_ =  "/Users/antelverde/Dropbox/jMetal/data/MOEAD_parameters/Weight" ;
    dataDirectory_ = "weight";
  } // MOEAD_DRA_Settings

  /**
   * Configure the algorithm with the specified parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;

    HashMap  parameters ; // Operator parameters

    // Creating the problem
    algorithm = new MOEAD_DRA_UCBv1(problem_);
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_) ;
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_) ;
    algorithm.setInputParameter("dataDirectory", dataDirectory_)   ;
    algorithm.setInputParameter("finalSize", finalSize_)           ;

    algorithm.setInputParameter("T", T_) ;
    algorithm.setInputParameter("delta", delta_) ;
    algorithm.setInputParameter("nr", nr_) ;
    
    algorithm.setInputParameter("functionType", "_TCHE1") ;
    

    // Crossover operator 
    parameters = new HashMap() ;
    parameters.put("CR", CR_) ;
    parameters.put("F", F_) ;
    parameters.put("DE_VARIANT", DE_VARIANT_);
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);                   
    
    // Mutation operator
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);         
    
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);

    return algorithm;
  } // configure

  /**
   * Configure MOEAD_DRA with user-defined parameter experiments.settings
   * @return A MOEAD_DRA algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    Algorithm algorithm ;
    Selection selection ;
    Crossover crossover ;
    Mutation mutation  ;

    HashMap  parameters ; // Operator parameters

    // Creating the algorithm.
    algorithm = new MOEAD_DRA_UCBv1(problem_) ;

    // Algorithm parameters
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
    finalSize_  = Integer.parseInt(configuration.getProperty("finalSize",String.valueOf(finalSize_)));
    dataDirectory_  = configuration.getProperty("dataDirectory", dataDirectory_);
    delta_ = Double.parseDouble(configuration.getProperty("delta", String.valueOf(delta_)));
    T_ = Integer.parseInt(configuration.getProperty("T", String.valueOf(T_)));
    nr_ = Integer.parseInt(configuration.getProperty("nr", String.valueOf(nr_)));

    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);
    algorithm.setInputParameter("dataDirectory",dataDirectory_)  ;
    algorithm.setInputParameter("finalSize", finalSize_)         ;

    algorithm.setInputParameter("T", T_) ;
    algorithm.setInputParameter("delta", delta_) ;
    algorithm.setInputParameter("nr", nr_) ;

    // Crossover operator
    CR_ = Double.parseDouble(configuration.getProperty("CR",String.valueOf(CR_)));
    F_ = Double.parseDouble(configuration.getProperty("F",String.valueOf(F_)));
    parameters = new HashMap() ;
    parameters.put("CR", CR_) ;
    parameters.put("F", F_) ;
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

    // Mutation parameters
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex",String.valueOf(mutationDistributionIndex_)));
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);

    return algorithm ;
  }
} // MOEAD_DRA_Settings

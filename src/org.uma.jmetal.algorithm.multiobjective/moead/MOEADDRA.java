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

package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.util.experiment.component.GenerateEvolutionChart;
import org.uma.jmetal.util.experiment.component.EvolutionChart.HistoricAlgorithm;
import org.uma.jmetal.util.experiment.component.EvolutionChart.HistoryData;
import myJMetal.Configuration;

/**
 * Class implementing the MOEA/D-DRA algorithm described in :
 * Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of
 * MOEA/D on CEC09 Unconstrained MOP Test Instances, Working Report CES-491,
 * School of CS & EE, University of Essex, 02/2009
 *
 * @author Juan J. Durillo
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MOEADDRA extends AbstractMOEAD<DoubleSolution> implements HistoricAlgorithm{
    String name = "";

    
    @Override
    public HistoryData getHistory(String indicator) {
        return history.get(indicator);
    }
    
    @Override
    public void setRunNumber(Integer runsNumber) {
        history = new HashMap<>();
        history.put("HV", new HistoryData(runsNumber));
        history.put("IGD", new HistoryData(runsNumber));
        history.put("Epsilon", new HistoryData(runsNumber));
        history.put("Spread", new HistoryData(runsNumber)); 
    }
    
    Map<String, HistoryData> history;
    
    
    
  protected DifferentialEvolutionCrossover differentialEvolutionCrossover ;

  protected DoubleSolution[] savedValues;
  protected double[] utility;
  protected int[] frequency;
  
  protected int draTime;

  JMetalRandom randomGenerator ;
  
  
 

  public MOEADDRA(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations,
      MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, FunctionType functionType,
      String dataDirectory, double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
            dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
            neighborSize);

        differentialEvolutionCrossover = (DifferentialEvolutionCrossover)crossoverOperator ;

        savedValues = new DoubleSolution[populationSize];
        utility = new double[populationSize];
        frequency = new int[populationSize];
        for (int i = 0; i < utility.length; i++) {
          utility[i] = 1.0;
          frequency[i] = 0;
        }

        randomGenerator = JMetalRandom.getInstance() ;
    }
  
  private void setConfig(Double delta, Integer nr,Double Cr, Double F, String variant) {
        neighborhoodSelectionProbability = delta;
        maximumNumberOfReplacedSolutions = nr;

        differentialEvolutionCrossover.setCr(Cr);
        differentialEvolutionCrossover.setF(F);
        differentialEvolutionCrossover.setVariant(variant);
    }

  @Override public void run() {
    initializePopulation() ;
    initializeUniformWeight();
    initializeNeighborhood();
    initializeIdealPoint() ;

    int generation = 0 ;
    evaluations = populationSize ;
    //setConfig(0.16, 4, 0.53, 0.08, "rand/1/bin");//600//300k
    do {
      int[] permutation = new int[populationSize];
      MOEADUtils.randomPermutation(permutation, populationSize);

      for (int i = 0; i < populationSize; i++) {
        int subProblemId = permutation[i];
        frequency[subProblemId]++;

        NeighborType neighborType = chooseNeighborType() ;
        //List<DoubleSolution> parents = parentSelection(subProblemId, neighborType) ;
        List<DoubleSolution> parents = parentSelection(subProblemId, neighborType, differentialEvolutionCrossover.getVariant()) ; 

        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

        DoubleSolution child = children.get(0) ;
        mutationOperator.execute(child);
        problem.evaluate(child);

        evaluations++;

        updateIdealPoint(child);
        updateNeighborhood(child, subProblemId, neighborType);
        
        if(HistoricAlgorithm.testToCalculate(evaluations,maxEvaluations)){
            HistoricAlgorithm.calculateIndicators(evaluations, maxEvaluations, problem.getName(), population,history);
        }
        
      }

      generation++;
      if (generation % draTime == 0) {
        utilityFunction();
      }

    } while (evaluations < maxEvaluations);
    /*System.out.println("\n\nhv <- c(");
    int i=0;
      Double[] hd = getHistory("HV").getDataTest(0);
      for (Double d : hd) {
          if(i>100) {
            System.out.println("");
            i=0;
          }
          System.out.print(d+", ");
          i++;
      }
      System.out.println(")");/**/
  }
  
   protected List<DoubleSolution> parentSelection(int subProblemId, NeighborType neighborType, String variant) {
        List<DoubleSolution> parents = null;
        if (variant.equals("rand/1/bin") || variant.equals("rand/1/exp") 
                || variant.equals("current-to-rand/1/bin")){//|| variant.equals("best/1/bin") || variant.equals("best/1/exp")) {
            List<Integer> matingPool = matingSelection(subProblemId, 3, neighborType);
            parents = new ArrayList<>(3);
            parents.add(population.get(matingPool.get(0)));
            parents.add(population.get(matingPool.get(1)));
            parents.add(population.get(matingPool.get(2)));
        }else if (variant.equals("rand/2/bin") ){
            List<Integer> matingPool = matingSelection(subProblemId, 4, neighborType);
            parents = new ArrayList<>(4);
            parents.add(population.get(matingPool.get(0)));
            parents.add(population.get(matingPool.get(1)));
            parents.add(population.get(matingPool.get(2)));
            parents.add(population.get(matingPool.get(3)));
        } else if(variant.equals("current-to-rand/2/bin")) {
            List<Integer> matingPool = matingSelection(subProblemId, 5, neighborType);
            parents = new ArrayList<>(5);
            parents.add(population.get(matingPool.get(0)));
            parents.add(population.get(matingPool.get(1)));
            parents.add(population.get(matingPool.get(2)));
            parents.add(population.get(matingPool.get(3)));
            parents.add(population.get(matingPool.get(4)));
        } 
        return parents;
    }

  protected void initializePopulation() {
    population.clear();
    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newSolution = (DoubleSolution)problem.createSolution();

      problem.evaluate(newSolution);
      population.add(newSolution);
      savedValues[i] = (DoubleSolution) newSolution.copy();
    }
  }

  public void utilityFunction() throws JMetalException {
    double f1, f2, uti, delta;
    for (int n = 0; n < populationSize; n++) {
      f1 = fitnessFunction(population.get(n), lambda[n]);
      f2 = fitnessFunction(savedValues[n], lambda[n]);
      delta = f2 - f1;
      if (delta > 0.001) {
        utility[n] = 1.0;
      } else {
        uti = (0.95 + (0.05 * delta / 0.001)) * utility[n];
        utility[n] = uti < 1.0 ? uti : 1.0;
      }
      savedValues[n] = (DoubleSolution) population.get(n).copy();
    }
  }

  public List<Integer> tourSelection(int depth) {
    List<Integer> selected = new ArrayList<Integer>();
    List<Integer> candidate = new ArrayList<Integer>();

    for (int k = 0; k < problem.getNumberOfObjectives(); k++) {
      // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU Et AL (NOT SORTED!!!!)
      selected.add(k);
    }

    for (int n = problem.getNumberOfObjectives(); n < populationSize; n++) {
      // set of unselected weights
      candidate.add(n);
    }

    while (selected.size() < (int) (populationSize / 5.0)) {
      int best_idd = (int) (randomGenerator.nextDouble() * candidate.size());
      int i2;
      int best_sub = candidate.get(best_idd);
      int s2;
      for (int i = 1; i < depth; i++) {
        i2 = (int) (randomGenerator.nextDouble() * candidate.size());
        s2 = candidate.get(i2);
        if (utility[s2] > utility[best_sub]) {
          best_idd = i2;
          best_sub = s2;
        }
      }
      selected.add(best_sub);
      candidate.remove(best_idd);
    }
    return selected;
  }

    public int getDraTime() {
        return draTime;
    }

    public void setDraTime(int draTime) {
        this.draTime = draTime;
    }
  
    
  

  @Override public String getName() {
    return "MOEADDRA" ;
  }

  @Override public String getDescription() {
    return "Multi-Objective Evolutionary Algorithm based on Decomposition. Version with Dynamic Resource Allocation" ;
  }
}

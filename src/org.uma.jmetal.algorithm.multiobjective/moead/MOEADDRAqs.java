package org.uma.jmetal.algorithm.multiobjective.moead;

import myJMetal.QuasiSimplex.QuasiSimplex;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * 
 * @author lucas
 */
public class MOEADDRAqs extends MOEADDRA{
    String name = "";

    @Override
    public String getName() {
        return name;
    }

    public void setName(String str) {
        name = str;
    }

    public MOEADDRAqs(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations, MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
    }
    
    
    @Override
    public void run() {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        initializeIdealPoint();
        
        QuasiSimplex quasiSimplex = new QuasiSimplex(this, (AbstractDoubleProblem) problem, populationSize);
        quasiSimplex.configurate(QuasiSimplex.Implementation.Coello, 
                                 QuasiSimplex.MarkType.mark, 
                                 NeighborType.NEIGHBOR, 
                                 15001);
        /*UCB_set hh = new UCB_set((int)(0.1*populationSize), //Window Size
                             75000,                     //Init
                             0,                     //End
                             1000,                      //Step  
                             5.0,                       //C
                             1.0);                      //D
        hh.addSelector("set", new Integer[]{1,2,3,4,5,6,7});//,8,9,10});
        System.out.println(hh.info());
        ucb_configuration2(1);/**/

        int generation = 0;
        evaluations = populationSize;
        do {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            for (int i = 0; i < populationSize; i++) {
                int subProblemId = permutation[i];
                frequency[subProblemId]++;
                
          /*      if(hh.isWorking(evaluations, maxEvaluations)&&(!hh.isWSfull() || (evaluations-populationSize)%hh.maxStep==0)){
                    hh.selectOperators();
                   ucb_configuration2((Integer)hh.getOperator("set"));
                }/**/
                
                NeighborType neighborType = chooseNeighborType();
                List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);
                //List<DoubleSolution> parents = parentSelection(subProblemId, neighborType, differentialEvolutionCrossover.getVariant()) ; 
                
                differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
                //hh.setX(fitnessFunction(population.get(subProblemId), lambda[subProblemId]));
                List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);
                
        
                DoubleSolution child = children.get(0);
                
                mutationOperator.execute(child);
                problem.evaluate(child);
                
                /*if(hh.isWorking(evaluations, maxEvaluations)&& (!hh.isWSfull() || (evaluations-populationSize)%hh.maxStep==0)){
                    hh.setY(fitnessFunction(child, lambda[subProblemId]));//qnto menor melhor
                    hh.adjustSlidingWindow();//Adjust the sliding window
                    hh.creditAssignment();//Calculate the credit assignment useing a Decaying Factor
                }/**/
                
                evaluations++;

                updateIdealPoint(child);
                updateNeighborhood(child, subProblemId, neighborType);
                
                //begin quasi simplex
                 if ((evaluations % quasiSimplex.getFrequency()) == 0) {
                    List<DoubleSolution> qs = null;
                     //System.out.print(evaluations + " apply QS");
                    for (int l = 0; l < lambda.length; l++) {
                        if ( quasiSimplex.isAvaible(l)){
                            if (quasiSimplex.getScope().equals(NeighborType.POPULATION)) {
                                qs = quasiSimplex.execute(population,lambda[l]);
                            } else {
                                List<DoubleSolution> neighbors = new ArrayList<>(neighborSize);
                                for (int j = 0; j < neighborSize; j++) {
                                    neighbors.add((DoubleSolution) population.get(neighborhood[l][j]).copy());
                                }
                                qs = quasiSimplex.execute(neighbors, lambda[l]);
                            }
                            evaluations += quasiSimplex.getEvaluations();

                            if (qs==null || qs.isEmpty()) {//qs.size()<3)){
                                quasiSimplex.setMark(l, false);
                            } else {
                                quasiSimplex.setMark(l, true);
                                for (DoubleSolution q : qs) {
                                    updateIdealPoint(q);
                                    updateNeighborhood(q, l, quasiSimplex.getScope());
                                }
                            }
                        }
                        if (evaluations > maxEvaluations) {
                            break;
                        }
                     }
                     /*int count = 0;
                     for (boolean b : quasiSimplex.getMark()) {
                         if (b) {
                             count++;
                         }
                     }
                     System.out.println(" count = " + count);*/
                 }// end quasi simplex/**/
                
            }

            generation++;
            if (generation % draTime == 0) {
                utilityFunction();
            }

        } while (evaluations < maxEvaluations);
        
        

    }


    
  private void ucb_configuration2(Integer i){
      switch(i){
            case 1://DEFAULT CONFIGURATION
                neighborhoodSelectionProbability = 0.9;
                maximumNumberOfReplacedSolutions = 2;
                
                differentialEvolutionCrossover.setCr(1.0);
                differentialEvolutionCrossover.setF(0.5);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 2://concave
                neighborhoodSelectionProbability = 0.57;
                maximumNumberOfReplacedSolutions = 6;
                
                differentialEvolutionCrossover.setCr(0.5);
                differentialEvolutionCrossover.setF(0.56);
                differentialEvolutionCrossover.setVariant("rand/2/bin");
                break;
            case 3://convex
                neighborhoodSelectionProbability = 0.72;
                maximumNumberOfReplacedSolutions = 1;
                
                differentialEvolutionCrossover.setCr(0.97);
                differentialEvolutionCrossover.setF(0.03);
                differentialEvolutionCrossover.setVariant("current-to-rand/2/bin");
                break;
            case 4://linear
                neighborhoodSelectionProbability = 0.45;
                maximumNumberOfReplacedSolutions = 1;
                
                differentialEvolutionCrossover.setCr(0.91);
                differentialEvolutionCrossover.setF(0.95);
                differentialEvolutionCrossover.setVariant("rand/2/bin");
                break;
            case 5://disconnect
                neighborhoodSelectionProbability = 0.08;
                maximumNumberOfReplacedSolutions = 1;
                
                differentialEvolutionCrossover.setCr(0.85);
                differentialEvolutionCrossover.setF(0.05);
                differentialEvolutionCrossover.setVariant("current-to-rand/2/bin");
                break;
            case 6://2f
                neighborhoodSelectionProbability = 0.46;
                maximumNumberOfReplacedSolutions = 1;
                
                differentialEvolutionCrossover.setCr(0.67);
                differentialEvolutionCrossover.setF(0.85);
                differentialEvolutionCrossover.setVariant("current-to-rand/2/bin");
                break;
            case 7://3f
                neighborhoodSelectionProbability = 0.71;
                maximumNumberOfReplacedSolutions = 1;
                
                differentialEvolutionCrossover.setCr(0.67);
                differentialEvolutionCrossover.setF(0.78);
                differentialEvolutionCrossover.setVariant("current-to-rand/2/bin");
                break;
            default:
                System.err.println("Configuration Doesn't exist!");
                break;
                
      }
  }
  
  
  
    @Override
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
        } else{
            System.err.println("Error variant not found!");
        }
        return parents;
    }
}

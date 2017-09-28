package org.uma.jmetal.algorithm.multiobjective.moead;

import pesquisajmetalcode.QuasiSImplex.QuasiSimplex;
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
       
        int generation = 0;
        evaluations = populationSize;
        do {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            for (int i = 0; i < populationSize; i++) {
                int subProblemId = permutation[i];
                frequency[subProblemId]++;
                NeighborType neighborType = chooseNeighborType();
                List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

                differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
                List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);
                
                DoubleSolution child = children.get(0);
                
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNeighborhood(child, subProblemId, neighborType);
                
                //begin quasi simplex
                 if ((evaluations % quasiSimplex.getFrequency()) == 0) {
                    List<DoubleSolution> qs = null;
                    for (int l = 0; l < lambda.length; l++) {
                        if ( quasiSimplex.isAvaible(l)){
                            if (quasiSimplex.getScope().equals(NeighborType.POPULATION)) {
                                qs = quasiSimplex.execute(population,lambda[l]);
                            } else {
                                List<DoubleSolution> pop = new ArrayList<>(neighborSize);
                                for (int j = 0; j < neighborSize; j++) {
                                    pop.add((DoubleSolution) population.get(neighborhood[l][j]).copy());
                                }
                                qs = quasiSimplex.execute(pop, lambda[l]);
                            }
                            evaluations += quasiSimplex.getEvaluations();

                            if (qs==null || qs.isEmpty()) {//qs.size()<3){
                                quasiSimplex.setMark(l, false);
                            } else {
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
                }// end quasi simplex
                
            }

            generation++;
            if (generation % 30 == 0) {
                utilityFunction();
            }

        } while (evaluations < maxEvaluations);
        
        int count=0;
        for (boolean b : quasiSimplex.getMark()) {
            if(b)count++;
        }
        System.out.println("count = "+count);

    }
}

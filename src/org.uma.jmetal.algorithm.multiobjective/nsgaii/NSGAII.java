package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.component.EvolutionChart.HistoricAlgorithm;
import org.uma.jmetal.util.experiment.component.EvolutionChart.HistoryData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NSGAII<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> implements HistoricAlgorithm{
  protected final int maxEvaluations;

  protected final SolutionListEvaluator<S> evaluator;

  protected int evaluations;

  
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
    
    @Override public void run() {
    List<S> offspringPopulation;
    List<S> matingPopulation;

    population = createInitialPopulation();
    population = evaluatePopulation(population);
    initProgress();
    while (!isStoppingConditionReached()) {
      matingPopulation = selection(population);
      offspringPopulation = reproduction(matingPopulation);
      offspringPopulation = evaluatePopulation(offspringPopulation);
      population = replacement(population, offspringPopulation);
      updateProgress();
      if(HistoricAlgorithm.testToCalculate(evaluations,maxEvaluations)){
        HistoricAlgorithm.calculateIndicators(evaluations, maxEvaluations, problem.getName(), (List<DoubleSolution>) population,history);
      }
    }
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
      System.out.println(")");*/
  }
    
    
  /**
   * Constructor
   */
  public NSGAII(Problem<S> problem, int maxEvaluations, int populationSize,
      CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
    super(problem);
    this.maxEvaluations = maxEvaluations;
    setMaxPopulationSize(populationSize); ;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    this.evaluator = evaluator;
  }

  @Override protected void initProgress() {
    evaluations = getMaxPopulationSize();
  }

  @Override protected void updateProgress() {
    evaluations += getMaxPopulationSize() ;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, getProblem());

    return population;
  }

  @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    Ranking<S> ranking = computeRanking(jointPopulation);

    return crowdingDistanceSelection(ranking);
  }

  @Override public List<S> getResult() {
    return getNonDominatedSolutions(getPopulation());
  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new DominanceRanking<S>();
    ranking.computeRanking(solutionList);

    return ranking;
  }

  protected List<S> crowdingDistanceSelection(Ranking<S> ranking) {
    CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
    List<S> population = new ArrayList<>(getMaxPopulationSize());
    int rankingIndex = 0;
    while (populationIsNotFull(population)) {
      if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
        addRankedSolutionsToPopulation(ranking, rankingIndex, population);
        rankingIndex++;
      } else {
        crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
        addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
      }
    }

    return population;
  }

  protected boolean populationIsNotFull(List<S> population) {
    return population.size() < getMaxPopulationSize();
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, List<S> population) {
    return ranking.getSubfront(rank).size() < (getMaxPopulationSize() - population.size());
  }

  protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    List<S> front;

    front = ranking.getSubfront(rank);

    for (S solution : front) {
      population.add(solution);
    }
  }

  protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    List<S> currentRankedFront = ranking.getSubfront(rank);

    Collections.sort(currentRankedFront, new CrowdingDistanceComparator<S>());

    int i = 0;
    while (population.size() < getMaxPopulationSize()) {
      population.add(currentRankedFront.get(i));
      i++;
    }
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList);
  }

  @Override public String getName() {
    return "NSGAII" ;
  }

  @Override public String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version II" ;
  }
}

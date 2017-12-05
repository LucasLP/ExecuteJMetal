package myJMetal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMException;
import org.uma.jmetal.util.experiment.component.GenerateEvolutionChart;
import org.uma.jmetal.util.experiment.component.EvolutionChart.HistoryData;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR;
import org.uma.jmetal.util.experiment.component.GenerateFriedmanTestTables;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateScatterPoints;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;


/**
 * This Experiment class was created to be generic 
 * and execute different types of algorithms and experiment components
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com>
 */
public class ExecuteExperiment {
    private List<ExperimentProblem<DoubleSolution>> problemList;
    private List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList;

    private Configuration configuration;


    public ExecuteExperiment(Configuration configuration) {
        this.configuration = configuration;
        algorithmList = new ArrayList<>();
    }
  
  
    public void execute() throws IOException {
        problemList = configuration.setProblemList();
        configureAlgorithmList();
        List<String> referenceFrontFileNames = configuration.getParameter("paretoFront", configuration.parameters.get("--problem"));

        Experiment<DoubleSolution, List<DoubleSolution>> experiment
                = new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>(configuration.experimentName)
                .setAlgorithmList(algorithmList)
                .setProblemList(problemList)
                .setReferenceFrontDirectory(configuration.paretoFrontDirectory)
                .setReferenceFrontFileNames(referenceFrontFileNames)
                .setExperimentBaseDirectory(configuration.baseDirectory)
                .setOutputParetoFrontFileName("FUN")
                .setOutputParetoSetFileName("VAR")
                .setIndicatorList(Arrays.asList(
                        new Epsilon<DoubleSolution>(),
                        new Spread<DoubleSolution>(),
                        //new GenerationalDistance<DoubleSolution>(),
                        new PISAHypervolume<DoubleSolution>(),
                        new InvertedGenerationalDistance<DoubleSolution>()))//,
                //new InvertedGenerationalDistancePlus<DoubleSolution>()))/**/
                .setIndependentRuns(configuration.Runs)
                .setNumberOfCores(configuration.cores)
                .build();

        if (configuration.executeNewAlgorithm) {
            System.out.println("Executing Algorithms...");
            new ExecuteAlgorithms(experiment).run();
            HistoryData.printAllDataInstances(experiment, configuration.indicators);
        }
        if (configuration.executeQualityIndicators) {
            System.out.println("Executing Quality Indicators...");
            new ComputeQualityIndicators<>(experiment).run();
        }
        if (configuration.executeTablesComparative) {
            System.out.println("Executing Comparatives...");
            new GenerateEvolutionChart(experiment, configuration.indicators).run();
            new GenerateLatexTablesWithStatistics(experiment).run();//remove duplicates is here
            new GenerateWilcoxonTestTablesWithR<>(experiment).run();
            new GenerateFriedmanTestTables<>(experiment).run();
            new GenerateBoxplotsWithR<>(experiment).setRows(4).setColumns(3).setDisplayNotch().run();
            new GenerateScatterPoints(experiment).run();
        }
    }

    private void configureAlgorithmList() {
        System.out.println("N. of Algorithms: " + configuration.NameList.size());
        System.out.println("N. Problems     : " + problemList.size());
        for (int indexAlgorithm = 0; indexAlgorithm < configuration.NameList.size(); indexAlgorithm++) {
            try {
                for (int indexProblem = 0; indexProblem < problemList.size(); indexProblem++) {
                    Algorithm<List<DoubleSolution>> algorithm = configuration.create((DoubleProblem) problemList.get(indexProblem).getProblem(), configuration.NameList.get(indexAlgorithm));
                    algorithmList.add(new ExperimentAlgorithm<>(algorithm, configuration.NameTagList.get(indexAlgorithm), problemList.get(indexProblem).getTag()));
                }
            } catch (JMException | IllegalArgumentException | IllegalAccessException | ClassNotFoundException ex) {
                Logger.getLogger(ExecuteExperiment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("N. of instance of algorithms: " + algorithmList.size());
    }
}

package myJMetal;

import myJMetal.Chart.GenerateEvolutionChart;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.management.JMException;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDRA;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDRAUCB;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDRAUCBIrace;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDRAUCBv1;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDRAUCBv4;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDRAqs;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.UF.UF1;
import org.uma.jmetal.problem.multiobjective.UF.UF10;
import org.uma.jmetal.problem.multiobjective.UF.UF2;
import org.uma.jmetal.problem.multiobjective.UF.UF3;
import org.uma.jmetal.problem.multiobjective.UF.UF4;
import org.uma.jmetal.problem.multiobjective.UF.UF5;
import org.uma.jmetal.problem.multiobjective.UF.UF6;
import org.uma.jmetal.problem.multiobjective.UF.UF7;
import org.uma.jmetal.problem.multiobjective.UF.UF8;
import org.uma.jmetal.problem.multiobjective.UF.UF9;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ6;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7;
import org.uma.jmetal.problem.multiobjective.glt.GLT1;
import org.uma.jmetal.problem.multiobjective.glt.GLT2;
import org.uma.jmetal.problem.multiobjective.glt.GLT3;
import org.uma.jmetal.problem.multiobjective.glt.GLT4;
import org.uma.jmetal.problem.multiobjective.glt.GLT5;
import org.uma.jmetal.problem.multiobjective.glt.GLT6;
import org.uma.jmetal.problem.multiobjective.mop.MOP1;
import org.uma.jmetal.problem.multiobjective.mop.MOP2;
import org.uma.jmetal.problem.multiobjective.mop.MOP3;
import org.uma.jmetal.problem.multiobjective.mop.MOP4;
import org.uma.jmetal.problem.multiobjective.mop.MOP5;
import org.uma.jmetal.problem.multiobjective.mop.MOP6;
import org.uma.jmetal.problem.multiobjective.mop.MOP7;
import org.uma.jmetal.problem.multiobjective.wfg.WFG1;
import org.uma.jmetal.problem.multiobjective.wfg.WFG2;
import org.uma.jmetal.problem.multiobjective.wfg.WFG3;
import org.uma.jmetal.problem.multiobjective.wfg.WFG4;
import org.uma.jmetal.problem.multiobjective.wfg.WFG5;
import org.uma.jmetal.problem.multiobjective.wfg.WFG6;
import org.uma.jmetal.problem.multiobjective.wfg.WFG7;
import org.uma.jmetal.problem.multiobjective.wfg.WFG8;
import org.uma.jmetal.problem.multiobjective.wfg.WFG9;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT2;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT3;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT6;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

/**
 * Configure Single runner and statistical tests and comparative
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com>
 */
public class Configuration {
    public int      MaxEvaluations ;
    public int      Runs ;
    public int      cores ;
    
    public Map<String, String> parameters;//parameters of algorithms
    
    public boolean  executeNewAlgorithm ;
    public boolean  executeQualityIndicators ;
    public boolean  executeTablesComparative ;
    
    
    public List<String> NameList, NameTagList;   
    public int      newAlgorithms ;//Max of this number is NameList
    public String paretoFrontDirectory ;
    public String baseDirectory;
    public String experimentName;
    public List<String> indicators ;
    
    
    public boolean generateChart ;

    
    public Configuration(String args[]) {
        MaxEvaluations = 300000;//600000; 50000;//
        Runs = 2;
        cores = 2;
        parameters = new HashMap<>();
        executeNewAlgorithm      = true;
        executeQualityIndicators = true;
        executeTablesComparative = false;
        generateChart = false;
        
        NameList    =  new ArrayList<>(); /*{"MOEAD","MOEADqs","NSGAII","SPEA2"};*/
        NameTagList =  new ArrayList<>(); //new String[]{"algorithm1"};
        indicators  =  new ArrayList<>();
        
        indicators.add("HV");
        indicators.add("Epsilon");
        indicators.add("IGD");
        indicators.add("Spread");
        
        newAlgorithms = NameList.size();
        paretoFrontDirectory = "/resources/pareto_fronts";
        baseDirectory = "experiment";
        experimentName = "MyExperiments";

        
        insertDefault();//init parameters
        
        //print all parameters
        for (int i = 0; i < args.length; i++) {
            System.out.print(args[i] + " ");
        }
        System.out.println("\n\nParameters:");
        if (args.length > 2) {
            
            //Find the problem/benchmark
            String s[] = args[1].split("/");//Separa o path enviado como um vetor
            String p = s[s.length - 1];//get the last string in path
            if(p.contains("@")){
                String s2[] = p.split("@");//pega o ultimo valor do vetor, e divide de acordo com '@'
                parameters.replace("--problem", s2[0]);
                parameters.replace("--varproblem", s2[1]);
                System.out.println("Problem\t: "+s2[0]+"\nvarNum\t: "+s2[1]);
            }else{
                parameters.replace("--problem", p);
                System.out.println("Problem\t: "+p);
            }
            
            //read other parameters
            System.out.print("\n--algorithm\t--tag");
            for (int i = 2; i < args.length; i++) {
                if (null != args[i]) {
                    if (args[i].startsWith("--")) {
                        String aux = args[i];
                        i++;
                        //parameters.replace(aux, args[i]);
                        parameters.put(aux, args[i]);
                        if(aux.equals("--algorithm")){
                            System.out.print("\n"+args[i]);
                            NameList.add(args[i]);
                            if(i+1<args.length && args[i+1].equals("--tag")){
                                i++;//tag
                                i++;//value
                                NameTagList.add(args[i]);
                                
                            }else{
                                NameTagList.add(args[i]);
                            }
                            System.out.print("\t"+args[i]);
                        }
                    }else if(args[i].equals("chart")){
                        generateChart = true;
                        System.out.println("Able to Generate Charts");
                    }
                }
            }
            System.out.println("");
        }
        printParameters();
    }
    
    
    
    public List<ExperimentProblem<DoubleSolution>> setProblemList(){
        List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
        switch(parameters.get("--problem")){
            case "UF":
                problemList.add(new ExperimentProblem<>(new UF1()));
                problemList.add(new ExperimentProblem<>(new UF2()));
                /*problemList.add(new ExperimentProblem<>(new UF3()));
                problemList.add(new ExperimentProblem<>(new UF4()));
                problemList.add(new ExperimentProblem<>(new UF5()));
                problemList.add(new ExperimentProblem<>(new UF6()));
                problemList.add(new ExperimentProblem<>(new UF7()));
                problemList.add(new ExperimentProblem<>(new UF8()));
                problemList.add(new ExperimentProblem<>(new UF9()));
                problemList.add(new ExperimentProblem<>(new UF10()));/**/
                break;
            case "DTLZ":
                problemList.add(new ExperimentProblem<>(new DTLZ1()));
                problemList.add(new ExperimentProblem<>(new DTLZ2()));
                problemList.add(new ExperimentProblem<>(new DTLZ3()));
                problemList.add(new ExperimentProblem<>(new DTLZ4()));
                //problemList.add(new ExperimentProblem<>(new DTLZ5()));
                problemList.add(new ExperimentProblem<>(new DTLZ6()));
                problemList.add(new ExperimentProblem<>(new DTLZ7()));
                break;
            case "MOP":
                problemList.add(new ExperimentProblem<>(new MOP1()));
                problemList.add(new ExperimentProblem<>(new MOP2()));
                problemList.add(new ExperimentProblem<>(new MOP3()));
                problemList.add(new ExperimentProblem<>(new MOP4()));
                problemList.add(new ExperimentProblem<>(new MOP5()));
                problemList.add(new ExperimentProblem<>(new MOP6()));
                problemList.add(new ExperimentProblem<>(new MOP7()));
                break;
            case "WFG":
                problemList.add(new ExperimentProblem<>(new WFG1()));
                problemList.add(new ExperimentProblem<>(new WFG2()));
                problemList.add(new ExperimentProblem<>(new WFG3()));
                problemList.add(new ExperimentProblem<>(new WFG4()));
                problemList.add(new ExperimentProblem<>(new WFG5()));
                problemList.add(new ExperimentProblem<>(new WFG6()));
                problemList.add(new ExperimentProblem<>(new WFG7()));
                problemList.add(new ExperimentProblem<>(new WFG8()));
                problemList.add(new ExperimentProblem<>(new WFG9()));
                break;
            case "ZDT":
                problemList.add(new ExperimentProblem<>(new ZDT1()));
                problemList.add(new ExperimentProblem<>(new ZDT2()));
                problemList.add(new ExperimentProblem<>(new ZDT3()));
                problemList.add(new ExperimentProblem<>(new ZDT4()));
                problemList.add(new ExperimentProblem<>(new ZDT6()));
                break;
            case "GLT":
                problemList.add(new ExperimentProblem<>(new GLT1()));
                problemList.add(new ExperimentProblem<>(new GLT2()));
                problemList.add(new ExperimentProblem<>(new GLT3()));
                problemList.add(new ExperimentProblem<>(new GLT4()));
                problemList.add(new ExperimentProblem<>(new GLT5()));
                problemList.add(new ExperimentProblem<>(new GLT6()));
                break;
        }
        return problemList;
    }
    
    
    
    public List<String> getParameter(String choice, String problem){
        if(null != problem){
            switch (problem) {
            case "ZDT":
                switch (choice) {
                    case "problems":
                        return Arrays.asList(new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6"});
                    case "paretoFront":
                        return Arrays.asList(new String[]{"ZDT1.pf", "ZDT2.pf","ZDT3.pf", "ZDT4.pf","ZDT6.pf"});
                }
            case "UF":
                switch (choice) {
                    case "problems":// });//
                        return Arrays.asList(new String[]{"UF1","UF2"});//,"UF3","UF4","UF5","UF6","UF7","UF8","UF9","UF10"});
                    case "paretoFront":
                        return Arrays.asList(new String[]{"UF1.pf","UF2.pf"});//,"UF3.pf","UF4.pf","UF5.pf","UF6.pf","UF7.pf","UF8.pf","UF9.pf","UF10.pf"});
                }
             case "WFG":
                switch (choice) {
                    case "problems":
                        return Arrays.asList(new String[]{"WFG1","WFG2","WFG3","WFG4","WFG5",
                                            "WFG6","WFG7","WFG8","WFG9"});
                    case "paretoFront":
                        return Arrays.asList(new String[]{"WFG1.2D.pf","WFG2.2D.pf","WFG3.2D.pf","WFG4.2D.pf","WFG5.2D.pf",
                            "WFG6.2D.pf","WFG7.2D.pf","WFG8.2D.pf","WFG9.2D.pf"});
                }
            case "DTLZ":
                switch (choice) {
                    case "problems":
                        return Arrays.asList(new String[]{"DTLZ1","DTLZ2","DTLZ3","DTLZ4",//"DTLZ5",
                                            "DTLZ6","DTLZ7"});
                    case "paretoFront":
                        return Arrays.asList(new String[]{"DTLZ1.pf","DTLZ2.pf","DTLZ3.pf","DTLZ4.pf",//"DTLZ5.2D.pf",
                            "DTLZ6.pf","DTLZ7.pf"});
                }
            case "MOP":
                switch (choice) {
                    case "problems":
                        return Arrays.asList(new String[]{"MOP1","MOP2","MOP3","MOP4","MOP5","MOP6","MOP7"});
                    case "paretoFront":
                        return Arrays.asList(new String[]{"MOP1.2D.pf","MOP2.2D.pf","MOP3.2D.pf","MOP4.2D.pf","MOP5.2D.pf",
                            "MOP6.2D.pf","MOP7.2D.pf"});
                }
            case "GLT":
                switch (choice) {
                    case "problems":
                        return Arrays.asList(new String[]{"GLT1","GLT2","GLT3","GLT4",
                                "GLT5","GLT6"});
                    case "paretoFront":
                        return Arrays.asList(new String[]{"GLT1.pf","GLT2.pf","GLT3.pf","GLT4.pf","GLT5.pf",
                            "GLT6.pf"});
                }
        }
        }
        return null;
    }
   
    
    
    public String problemPath(String p){
        if(getParameter("problems", "ZDT").contains(p)){
            return "zdt";
        }else if(getParameter("problems", "UF").contains(p)){
            return "UF";
        }else if(getParameter("problems", "MOP").contains(p)){
            return "mop";
        }else if(getParameter("problems", "WFG").contains(p)){
            return "wfg";
        }else if(getParameter("problems", "DTLZ").contains(p)){
            return "dtlz";
        }
        
        return null;
    }
    
    
    
    
    public Algorithm create(DoubleProblem problem, String algorithm) throws JMException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        if(algorithm == null){
            algorithm = parameters.get("--algorithm");
        }
        
        if(algorithm.startsWith("MOEAD")){
            double cr = Double.valueOf(parameters.get("--CR"));//1.0;
            double f  = Double.valueOf(parameters.get("--F"));//0.5;
            DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover(cr, f, parameters.get("--DE"));//"rand/1/bin");

            double mutationProbability = Double.valueOf(parameters.get("--pm")) / problem.getNumberOfVariables();//1.0
            double mutationDistributionIndex = Double.valueOf(parameters.get("--tau"));//20.0
            MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
            if (algorithm.equals("MOEAD") ){
                return new MOEADBuilder(problem, MOEADBuilder.Variant.MOEAD)
                    .setCrossover(crossover)
                    .setMutation(mutation)
                    .setMaxEvaluations(this.MaxEvaluations)
                    .setPopulationSize(600)
                    .setResultPopulationSize(600)
                    .setNeighborhoodSelectionProbability(Double.valueOf(parameters.get("--delta")))// 0.9)
                    .setMaximumNumberOfReplacedSolutions(Integer.valueOf(parameters.get("--nr")))//2)
                    .setNeighborSize(Integer.valueOf(parameters.get("--nrSize")))//20)
                    .setFunctionType(AbstractMOEAD.FunctionType.valueOf(parameters.get("--fun")))//AbstractMOEAD.FunctionType.TCHE)
                    .setDataDirectory("resources/MOEAD_Weights")
                    .build();
            } else if (algorithm.equals("MOEADDRAUCB")){

                MOEADDRAUCB a = (MOEADDRAUCB) new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRAUCB)
                        .setCrossover(crossover)
                        .setMutation(mutation)
                        .setMaxEvaluations(this.MaxEvaluations)
                        .setPopulationSize(600)
                        .setResultPopulationSize(600)
                        .setNeighborhoodSelectionProbability(Double.valueOf(parameters.get("--delta")))// 0.9)
                        .setMaximumNumberOfReplacedSolutions(Integer.valueOf(parameters.get("--nr")))//2)
                        .setNeighborSize(Integer.valueOf(parameters.get("--nrSize")))//20)
                        .setFunctionType(AbstractMOEAD.FunctionType.valueOf(parameters.get("--fun")))//AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("resources/MOEAD_Weights")
                        .build();
                a.setDraTime(Integer.valueOf(parameters.get("--draTime")));
                a.setName(algorithm);
                a.setConfiguration(this);
                return a;

            }  else if (algorithm.equals("MOEADDRAUCBv1")){

                MOEADDRAUCBv1 a = (MOEADDRAUCBv1) new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRAUCBv1)
                        .setCrossover(crossover)
                        .setMutation(mutation)
                        .setMaxEvaluations(this.MaxEvaluations)
                        .setPopulationSize(600)
                        .setResultPopulationSize(600)
                        .setNeighborhoodSelectionProbability(Double.valueOf(parameters.get("--delta")))// 0.9)
                        .setMaximumNumberOfReplacedSolutions(Integer.valueOf(parameters.get("--nr")))//2)
                        .setNeighborSize(Integer.valueOf(parameters.get("--nrSize")))//20)
                        .setFunctionType(AbstractMOEAD.FunctionType.valueOf(parameters.get("--fun")))//AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("resources/MOEAD_Weights")
                        .build();
                a.setDraTime(Integer.valueOf(parameters.get("--draTime")));
                a.setName(algorithm);
                a.setConfiguration(this);
                return a;

            } else if (algorithm.equals("MOEADDRAUCBv4")){

                MOEADDRAUCBv4 a = (MOEADDRAUCBv4) new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRAUCBv4)
                        .setCrossover(crossover)
                        .setMutation(mutation)
                        .setMaxEvaluations(this.MaxEvaluations)
                        .setPopulationSize(600)
                        .setResultPopulationSize(600)
                        .setNeighborhoodSelectionProbability(Double.valueOf(parameters.get("--delta")))// 0.9)
                        .setMaximumNumberOfReplacedSolutions(Integer.valueOf(parameters.get("--nr")))//2)
                        .setNeighborSize(Integer.valueOf(parameters.get("--nrSize")))//20)
                        .setFunctionType(AbstractMOEAD.FunctionType.valueOf(parameters.get("--fun")))//AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("resources/MOEAD_Weights")
                        .build();
                a.setDraTime(Integer.valueOf(parameters.get("--draTime")));
                a.setName(algorithm);
                a.setConfiguration(this);
                return a;

            }else if (algorithm.equals("MOEADDRA") ){
                 MOEADDRA a = (MOEADDRA) new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRA)
                        .setCrossover(crossover)
                        .setMutation(mutation)
                        .setMaxEvaluations(this.MaxEvaluations)
                        .setPopulationSize(600)
                        .setResultPopulationSize(600)
                        .setNeighborhoodSelectionProbability(Double.valueOf(parameters.get("--delta")))// 0.9)
                        .setMaximumNumberOfReplacedSolutions(Integer.valueOf(parameters.get("--nr")))//2)
                        .setNeighborSize(Integer.valueOf(parameters.get("--nrSize")))//20)
                        .setFunctionType(AbstractMOEAD.FunctionType.valueOf(parameters.get("--fun")))//AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("resources/MOEAD_Weights")
                        .build();
                 a.setDraTime(Integer.valueOf(parameters.get("--draTime")));
                 a.setConfiguration(this);
                return a;
                /*
                return new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRA)
                        .setCrossover(crossover)
                        .setMutation(mutation)
                        .setMaxEvaluations(this.MaxEvaluations)
                        .setPopulationSize(600)
                        .setResultPopulationSize(600)
                        .setNeighborhoodSelectionProbability(0.9)// 0.9)
                        .setMaximumNumberOfReplacedSolutions(2)//2)
                        .setNeighborSize(20)//20)
                        .setFunctionType(AbstractMOEAD.FunctionType.TCHE)//AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("resources/MOEAD_Weights")
                        .build();
                */

            }else if (algorithm.equals("MOEADDRAqs")){
                MOEADDRAqs a = (MOEADDRAqs) new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRAqs)
                        .setCrossover(crossover)
                        .setMutation(mutation)
                        .setMaxEvaluations(this.MaxEvaluations)
                        .setPopulationSize(600)
                        .setResultPopulationSize(600)
                        .setNeighborhoodSelectionProbability(Double.valueOf(parameters.get("--delta")))// 0.9)
                        .setMaximumNumberOfReplacedSolutions(Integer.valueOf(parameters.get("--nr")))//2)
                        .setNeighborSize(Integer.valueOf(parameters.get("--nrSize")))//20)
                        .setFunctionType(AbstractMOEAD.FunctionType.valueOf(parameters.get("--fun")))//AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("resources/MOEAD_Weights")
                        .build();
                a.setDraTime(Integer.valueOf(parameters.get("--draTime")));
                a.setName(algorithm);
                return a;
            }else if (algorithm.equals("MOEADDRAUCBIrace")){

                MOEADDRAUCBIrace a = (MOEADDRAUCBIrace) new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRAUCBIrace)
                        .setCrossover(crossover)
                        .setMutation(mutation)
                        .setMaxEvaluations(this.MaxEvaluations)
                        .setPopulationSize(600)
                        .setResultPopulationSize(600)
                        .setNeighborhoodSelectionProbability(Double.valueOf(parameters.get("--delta")))// 0.9)
                        .setMaximumNumberOfReplacedSolutions(Integer.valueOf(parameters.get("--nr")))//2)
                        .setNeighborSize(Integer.valueOf(parameters.get("--nrSize")))//20)
                        .setFunctionType(AbstractMOEAD.FunctionType.valueOf(parameters.get("--fun")))//AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("resources/MOEAD_Weights")
                        .build();
                a.setDraTime(Integer.valueOf(parameters.get("--draTime")));
                a.setName(algorithm);
                a.setConfiguration(this);
                return a;

            }
        }
            
        else if (algorithm.equals("NSGAII") ){
            double crossoverProbability = 0.9;
            double crossoverDistributionIndex = 30.0;
            CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

            double mutationProbability = 1.0 / problem.getNumberOfVariables();
            double mutationDistributionIndex = 20.0;
            MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

            SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>();
            return new NSGAIIIBuilder<>(problem)
                    .setCrossoverOperator(crossover)
                    .setMutationOperator(mutation)
                    .setSelectionOperator(selection)
                    .setMaxIterations(500)
                    .build();
            
            
            
        }else if (algorithm.equals("SPEA2")) {
            double crossoverProbability = 0.9 ;
            double crossoverDistributionIndex = 20.0 ;
            CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

            double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
            double mutationDistributionIndex = 20.0 ;
            MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

            SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

            return new SPEA2Builder<>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxIterations(250)
                .setPopulationSize(100)
                .build() ;
            
            
            
        }else if (algorithm.equals("IBEA")) {
            // return (new IBEA_Settings(problemName).configure());
            return null;
        }
        return null;
    }

    public void print(String date_init, String date_final, String File_name) {
        try (OutputStream os = new FileOutputStream(baseDirectory+"/"+experimentName+"/"+File_name)) {
            PrintStream ps = new PrintStream(os);

            ps.println("Evaluations: " + MaxEvaluations);
            ps.println("Runs: " + Runs);
            ps.println("\nStart: "+date_init);
            ps.println("Stop:  "+date_final+ "\n");
            ps.println("Executed new algorithm: "+executeNewAlgorithm);
            ps.println("Executed Quality Indicators: "+executeQualityIndicators+"\n\n");
            ps.println("Name list: ");
            for (int i = 0; i < NameList.size(); i++) {
                ps.println("¬ "+NameList.get(i));
            }
            ps.println("\n\nProblem: "+parameters.get("--problem"));//benchmark);
            if(executeNewAlgorithm){
                ps.printf("\nParameters:\n");
                //for (int i = 0; i < parameters.length; i++) {ps.println("[" + i + "] " + parameters[i]);}
                Set<String> keys =  parameters.keySet();
                for (String key : keys) {
                    ps.println(key+" "+parameters.get(key));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /**
     * Este método deve ser executado antes de qualquer uso 
     * dos códigos presentes nesta pasta ou na pasta "irace"
     * Recomendo utilizar na primeira linha do "main"
     */
    private void insertDefault(){
        parameters.put("--F"          , "1.0"       );
        parameters.put("--CR"         , "0.5"       );
        parameters.put("--DE"         , "rand/1/bin");
        parameters.put("--delta"      , "0.9"       );
        parameters.put("--nr"         , "2"         );
        parameters.put("--nrSize"     , "20"        );
        
        parameters.put("--pm"         , "1.0"       );
        parameters.put("--tau"        , "20.0"      );
        parameters.put("--fun"        , "TCHE"      );
        
        parameters.put("--problem"    , "UF"        );
        parameters.put("--draTime"    , "30"        );
    }
     
    
    
    private void printParameters(){
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            System.out.println(key+" : "+parameters.get(key));
        }
    }
}

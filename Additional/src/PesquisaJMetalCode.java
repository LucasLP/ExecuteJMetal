package pesquisajmetalcode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMException;

/**
 *
 * @author lucas
 */
public class PesquisaJMetalCode {
    public static String myVersion(){return "v1.3   22/8/2017";}
    

    public static void main(String[] args) throws FileNotFoundException, JMException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        //Test input
        //args = "--single-run /WFG1 --algorithm MOEADDRA --algorithm MOEADDRAUCB".split(" ");
        
        //args = "--single-run /ZDT4 --algorithm MOEADDRA".split(" ");
        
        //args = "--statistic /ZDT --algorithm MOEAD".split(" ");
        //args = "--comparative ZDT --algorithm MOEADDRAUCB --tag MOEADDRAUCBnew --algorithm MOEADDRA".split(" ");
        
        args = "--single-run /UF10 --algorithm MOEADDRA".split(" ");
        execute(args);
        
    }
    
    
    public static void execute(String[] args) throws FileNotFoundException, JMException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
        Configuration conf = new Configuration(args);
        if (args != null && args.length > 0) {
            switch (args[0]) {
                case "--single-run":
                    System.out.println("\nMode Selected: Single Run");
                    System.out.println("================================");
                    String[] args2 = new String[2];//
                    String path = conf.problemPath(conf.parameters.get("--problem"));
                    args2[0] ="org.uma.jmetal.problem.multiobjective."+path+"."+conf.parameters.get("--problem");
                    args2[1] ="/resources/pareto_fronts/"+conf.parameters.get("--problem")+".pf";
                    MyRunner runner = new MyRunner(conf);
                    runner.execute(args2);
                    break;
                case "--statistic":
                    System.out.println("\nMode Selected: Statistic");
                    System.out.println("================================");
                    MultiTest(conf);
                    break;
                case "--indicators": 
                    System.out.println("\nMode Selected: Indicators");
                    System.out.println("================================");
                    conf.parameters.replace("--problem", args[1]);
                    if(args.length > 2 ){
                        conf.executeNewAlgorithm      = false;
                        conf.executeQualityIndicators = true;
                        conf.executeTablesComparative = false;
                        MultiTest(conf);
                    }else{
                        System.out.println("Need more than one algorithm for comparison!");
                    }
                    
                    break;
                case "--comparative": 
                    System.out.println("\nMode Selected: Comparative");
                    System.out.println("================================");
                    conf.parameters.replace("--problem", args[1]);
                    if(args.length - 2 > 1){
                        conf.executeNewAlgorithm      = false;
                        conf.executeQualityIndicators = false;//false;
                        conf.executeTablesComparative = true;
                        MultiTest(conf);
                    }else{
                        System.out.println("Need more than one algorithm for comparison!");
                    }
                    
                    break;
                case "--help":
                    help();
                    break;
                default:
                    System.out.println("The arg[0] must be: --single-run|--statistic|--comparative\n");
                    break;
            }
        } else {
            help();
        }
    }
    
    
    public static void MultiTest(Configuration configuration) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String i, f;
        String log_file;
        if (configuration.executeNewAlgorithm) {
            log_file = "Execute_log_" + configuration.parameters.get("--name")
                    + "_" + configuration.parameters.get("--problem") + ".txt";
        } else {
            log_file = "Execute_log_comparative.txt";
        }
        i = dateFormat.format(new Date());
        System.out.println("Init: " + i + "\n");
        ExecuteExperiment exp = new ExecuteExperiment(configuration);

        try {
            exp.execute();
        } catch (IOException ex) {
            Logger.getLogger(PesquisaJMetalCode.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**/
        f = dateFormat.format(new Date());
        System.out.println("Final: " + f + "\n");
        configuration.print(i, f, log_file);
    }
    
    
    public static void help() {
        System.out.println("=============================");
        System.out.println("JMetal 5.2, and Modification "+myVersion());
        System.out.println("Set arg[0] with: --single-run|--statistic|--comparative");
        System.out.println("\t--single-run, is a just one execution of algorithm");
        System.out.println("\t--statistic, is executed several runs and save data files");
        System.out.println("\t--indicators, is executed only the quality indicator");
        System.out.println("\t--comparative, send in arg[1] benchmark to compare and other args the algorithms");
        System.out.println("Set arg[1] with instance to execute, example \"/UF1@1\"");
        System.out.println("Another instances are parameters to set the --algothim algorithm (optional) --tag algorithmTag"
                + "\nif you don't set, will be a default value. You can set these parameters:"
                + "\n--F, --CR, --DE, "
                + "\n--algorithm, --pm, --tau, --fun, --delta, --nr, --nrSize"
                + "\n--tag"
        );
        System.out.println("\nJMetal framework"
                + "\nThis is a branch version, modified by Lucas Prestes");
        System.out.println("=============================");
    }
}


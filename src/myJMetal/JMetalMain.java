package myJMetal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMException;

/**
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public class JMetalMain {
    public static String myVersion(){return "v1.6   31/10/2017";}
    

    public static void main(String[] args) throws FileNotFoundException, JMException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        // --algorithm MOEADDRAUCBv1 --tag UCBv1 --algorithm MOEADDRAUCB --tag UCBv3
        //args = "--single-run /UF3 --algorithm MOEADDRA --F 0.43 --CR 0.09 --DE current-to-rand/1/bin --delta 0.22 --nr 2".split(" ");
        //args = "--comparative ZDT --algorithm MOEADDRA --algorithm MOEADDRAUCB --algorithm MOEADDRAUCBIrace".split(" ");
        //args = "--statistic /ZDT --algorithm MOEADDRAUCBIrace".split(" ");
        //comparative --algorithm MOEADDRAUCBIrace
        //args = "--single-run /UF8 --algorithm IBEA ".split(" ");
        
        //args = "--single-run /UF3 --algorithm MOEADDRAUCB --F 0.43 --CR 0.09 --DE current-to-rand/1/bin --delta 0.22 --nr 2".split(" ");
        execute(args);
    }
    
    // --single-run  --statistic  --indicators   --comparative
    public static void execute(String[] args) throws FileNotFoundException, JMException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
        Configuration conf = new Configuration(args);
        if (args != null && args.length > 0) {
            switch (args[0]) {
                case "--single-run":
                    System.out.println("\nMode Selected: Single Run");
                    System.out.println("================================");
                    String[] args2 = new String[2];//problem, pf
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
        String log_file ="";
        if(configuration.executeNewAlgorithm){
            log_file= "Execute_log_"+configuration.NameTagList.get(0)+".txt";
        }else{
            log_file= "Execute_log.txt";
        }
        
        i = dateFormat.format(new Date());
        System.out.println("Init: " + i + "\n");
        ExecuteExperiment exp = new ExecuteExperiment(configuration);

        try {
            exp.execute();
        } catch (IOException ex) {
            Logger.getLogger(JMetalMain.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        System.out.println("Set arg[1] with instance to execute, example \"/UF1\"");
        System.out.println("Another instances are parameters to set the --algothim algorithm and (optional) --tag algorithmTag"
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


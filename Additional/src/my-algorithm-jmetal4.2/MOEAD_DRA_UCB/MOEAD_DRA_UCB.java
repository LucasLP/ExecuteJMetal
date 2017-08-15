

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.moead.Utils;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 *
 * @author lucas
 */
public class MOEAD_DRA_UCB extends Algorithm {

	private int populationSize_;
  /**
   * Stores the population
   */
  private SolutionSet population_;
  /**
   * Stores the values of the individuals
   */
  private Solution [] savedValues_;


  private double [] utility_;
  private int [] frequency_;

  /**
   * Z vector (ideal point)
   */
  double[] z_;
  /**
   * Lambda vectors
   */
  //Vector<Vector<Double>> lambda_ ;
  double[][] lambda_;
  /**
   * T: neighbour size
   */
  int T_;
  /**
   * Neighborhood
   */
  int[][] neighborhood_;
  /**
   * delta: probability that parent solutions are selected from neighbourhood
   */
  double delta_;
  /**
   * nr: maximal number of solutions replaced by each child solution
   */
  int nr_;
  Solution[] indArray_;
  String functionType_;
  int evaluations_;
  /**
   * Operators
   */
  Operator crossover_;
  Operator mutation_;

  String dataDirectory_;

  
  String variantSelected = "rand/1/bin";//my modification
  /**
   * Constructor
   * @param problem Problem to solve
   */
  public MOEAD_DRA_UCB(Problem problem) {
    super (problem) ;

    functionType_ = "_TCHE1";

  } // DMOEA

        @Override
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int maxEvaluations;
    functionType_ = (String) this.getInputParameter("functionType");
    evaluations_ = 0;
    maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations"));
    populationSize_ = ((Integer) this.getInputParameter("populationSize"));
    dataDirectory_ = this.getInputParameter("dataDirectory").toString();

    population_  = new SolutionSet(populationSize_);
    savedValues_ = new Solution[populationSize_];
    utility_     = new double[populationSize_];
    frequency_   = new int[populationSize_];
    for (int i = 0; i < utility_.length; i++) {
        utility_[i] = 1.0;
        frequency_[i] = 0;
    }
    indArray_    = new Solution[problem_.getNumberOfObjectives()];

    //T_ = 20;
    //delta_ = 0.9;
    //nr_ = 2;

    //T_ = (int) (0.1 * populationSize_);
    //delta_ = 0.9;
    //nr_ = (int) (0.01 * populationSize_);

    T_ = ((Integer) this.getInputParameter("T"));
    nr_ = ((Integer) this.getInputParameter("nr"));
    delta_ = ((Double) this.getInputParameter("delta"));

    neighborhood_ = new int[populationSize_][T_];

    z_ = new double[problem_.getNumberOfObjectives()];
    //lambda_ = new Vector(problem_.getNumberOfObjectives()) ;
    lambda_ = new double[populationSize_][problem_.getNumberOfObjectives()];

    crossover_ = operators_.get("crossover"); // default: DE crossover
    mutation_ = operators_.get("mutation");  // default: polynomial mutation

    // STEP 1. Initialization
    // STEP 1.1. Compute euclidean distances between weight vectors and find T
    initUniformWeight();
    initNeighborhood();

    // STEP 1.2. Initialize population
    initPopulation();

    // STEP 1.3. Initialize z_
    initIdealPoint();

    int eval = 0;
    Integer showHV ;
    if(this.getInputParameter("--showHV")!=null){
        showHV=Integer.valueOf( (String)this.getInputParameter("--showHV") );
    }else{
        showHV=0;
    }
    QualityIndicator indicators = null;
    if(showHV>0){
        String pf = "paretoFront"+File.separatorChar+problem_.getName()+".dat";
        //String pf = "paretoFront/UF9.dat";
        indicators = new QualityIndicator(problem_, pf) ;
    }
    UCB_set hh = new UCB_set(populationSize_);

     //UCBv2
  	 //hh.initDefault  = 0;
		//UCBv3
    hh.initDefault = (maxEvaluations)/4;




    UCB seletor_conf = new UCB(new Integer[]{1,2,3,4,5,6,7,8,9,10});//,11,12,13,14,15,16,17});
    Integer[] ucb_using = new Integer[10];//7];
            for (int i = 0; i < ucb_using.length; i++) {
                ucb_using[i]=0;
                
            }
    hh.addSelector("set", seletor_conf);
    Double x,y;
    //ucb_configuration_vIrace(1);
    
    int gen = 0;
    // STEP 2. Update
    do {
      int[] permutation = new int[populationSize_];
      Utils.randomPermutation(permutation, populationSize_);
      List<Integer> order = tour_selection(10);

      for (int i = 0; i < order.size(); i++) {
          if(evaluations_> hh.initDefault){
              if(!hh.isWSfull() || eval%hh.times==0){
                hh.selectOperators();
                //ucb_configuration_vIrace((Integer)hh.getOperator("set"));
                ucb_configuration((Integer)hh.getOperator("set"));
                  //System.out.println("Using: "+(Integer)hh.getOperator("set"));
                  ucb_using[(Integer)hh.getOperator("set") -1]++;
                  //System.out.println(eval+"# "+hh.toString());
              }
         }
          
        //int n = permutation[i]; // or int n = i;
        int n = order.get(i) ; // or int n = i;
        frequency_[n]++;
        x = fitnessFunction(population_.get(n), lambda_[n]);
        Solution child =  matingEvolution(n);
        
        //minha mod===
        if(evaluations_-1>  hh.initDefault ){
            if(!hh.isWSfull() || eval%hh.times==0){
                y = fitnessFunction(child, lambda_[n]);//qnto menor melhor
                hh.adjustSlidingWindow(x,y);//Adjust the sliding window
                hh.creditAssignment();//Calculate the credit assignment useing a Decaying Factor
            }
        }
        if(showHV>0){
            if(eval%showHV==0){
                System.out.println("#"+eval+" : " +indicators.getHypervolume(population_));
            }
        }
        eval++;
        //minha mod===
      } // for

      gen++;
        if(gen%30==0)
        {
            comp_utility();
        }

    } while (evaluations_ < maxEvaluations);
    //for (int i = 0; i < ucb_using.length; i++) {
    //    System.out.println( (i+1) +"= "+ucb_using[i]);
    //}
    int final_size = populationSize_;
      try {
       final_size= (Integer) (getInputParameter("finalSize"));
       //System.out.println("FINAL SOZE: " + final_size) ;
    } catch (Exception e) { // if there is an exception indicate it!
      //System.err.println("The final size paramater has been ignored");
      //System.err.println("The number of solutions is " + population_.size());
      return population_;
      
    }    
    return finalSelection(final_size); 
  }

  
  private void ucb_configuration(Integer i){
      switch(i){
            case 1://DEFAULT CONFIGURATION
                delta_ = 0.9;
                nr_ = 2;
                crossover_.setParameter("CR", 1.0);
                crossover_.setParameter("F", 0.5);
                variantSelected = "rand/1/bin";
                break;
            case 2://PARTE DA CONFIGURAÇÃO ENCONTRADA PELO IRACE
                delta_ = 0.95;
                nr_ = 2;
                crossover_.setParameter("CR", 0.4);
                crossover_.setParameter("F", 0.37);
                variantSelected = "rand/1/bin";
                break;
            case 3:
                delta_ = 0.8;
                nr_ = 3;
                crossover_.setParameter("CR", 0.8);
                crossover_.setParameter("F", 0.7);
                variantSelected = "current-to-rand/2/bin";
                break;
            case 4:
                delta_ = 1.0;
                nr_ = 1;
                crossover_.setParameter("CR", 0.6);
                crossover_.setParameter("F", 0.7);
                variantSelected = "current-to-rand/1/bin";
                break;
            case 5:
                delta_ = 0.6;
                nr_ = 2;
                crossover_.setParameter("CR", 0.4);
                crossover_.setParameter("F", 0.1);
                variantSelected = "rand/1/bin";
                break;
            case 6:
                delta_ = 0.95;
                nr_ = 1;
                crossover_.setParameter("CR", 1.0);
                crossover_.setParameter("F", 0.4);
                variantSelected = "rand/2/bin";
                break;
            case 7:
                delta_ = 0.3;
                nr_ = 6;
                crossover_.setParameter("CR", 0.2);
                crossover_.setParameter("F", 0.8);
                variantSelected = "current-to-rand/1/bin";
                break;
            case 8:
                delta_ = 0.75;
                nr_ = 10;
                crossover_.setParameter("CR", 0.8);
                crossover_.setParameter("F", 0.1);
                variantSelected = "current-to-rand/2/bin";
                break;    
            case 9:
                delta_ = 1.0;
                nr_ = 2;
                crossover_.setParameter("CR", 1.0);
                crossover_.setParameter("F", 0.7);
                variantSelected = "rand/1/bin";
                break;
            case 10:
                delta_ = 1.0;
                nr_ = 3;
                crossover_.setParameter("CR", 1.0);
                crossover_.setParameter("F", 0.9);
                variantSelected = "rand/1/bin";
                break;
      }
  }
  
  private void ucb_configuration_vIrace(Integer i){
      switch(i){
            case 1://DEFAULT CONFIGURATION
                delta_ = 0.9;
                nr_ = 2;
                crossover_.setParameter("CR", 1.0);
                crossover_.setParameter("F", 0.5);
                variantSelected = "rand/1/bin";
                break;
            case 2://Concave
                delta_ = 0.06;
                nr_ = 1;
                crossover_.setParameter("CR", 0.02);
                crossover_.setParameter("F", 0.71);
                variantSelected = "rand/1/bin";
                break;
            case 3://Convex
                delta_ = 0.66;
                nr_ = 8;
                crossover_.setParameter("CR", 0.35);
                crossover_.setParameter("F", 0.46);
                variantSelected = "current-to-rand/1/bin";
                break;
            case 4://Diconnect
                delta_ = 0.14;
                nr_ = 5;
                crossover_.setParameter("CR", 0.09);
                crossover_.setParameter("F", 0.52);
                variantSelected = "rand/1/bin";
                break;
            case 5://Linear
                delta_ = 0.33;
                nr_ = 5;
                crossover_.setParameter("CR", 0.29);
                crossover_.setParameter("F", 0.17);
                variantSelected = "rand/1/bin";
                break;
            case 6://2f
                delta_ = 0.2;
                nr_ = 9;
                crossover_.setParameter("CR", 0.93);
                crossover_.setParameter("F", 0.09);
                variantSelected = "rand/1/bin";
                break;
            case 7://3f
                delta_ = 0.74;
                nr_ = 7;
                crossover_.setParameter("CR", 0.13);
                crossover_.setParameter("F", 0.92);
                variantSelected = "current-to-rand/1/bin";
                break;
            case 8://zdt
                delta_ = 0.33;
                nr_ = 9;
                crossover_.setParameter("CR", 0.24);
                crossover_.setParameter("F", 0.61);
                variantSelected = "rand/1/bin";
                break;
            case 9://wfg
                delta_ = 0.2;
                nr_ = 10;
                crossover_.setParameter("CR", 0.47);
                crossover_.setParameter("F", 0.25);
                variantSelected = "rand/1/bin";
                break;
                //=======================================================
            case 10://Concave
                delta_ = 0.77;
                nr_ = 5;
                crossover_.setParameter("CR", 0.07);
                crossover_.setParameter("F", 0.84);
                variantSelected = "current-to-rand/1/bin";
                break;
            case 11://Convex
                delta_ = 0.22;
                nr_ = 9;
                crossover_.setParameter("CR", 0.58);
                crossover_.setParameter("F", 0.59);
                variantSelected = "current-to-rand/1/bin";
                break;
            case 12://Diconnect
                delta_ = 0.72;
                nr_ = 6;
                crossover_.setParameter("CR", 0.79);
                crossover_.setParameter("F", 0.45);
                variantSelected = "rand/1/bin";
                break;
            case 13://Linear
                delta_ = 0.66;
                nr_ = 4;
                crossover_.setParameter("CR", 0.23);
                crossover_.setParameter("F", 0.31);
                variantSelected = "current-to-rand/2/bin";
                break;
            case 14://2f
                delta_ = 0.54;
                nr_ = 4;
                crossover_.setParameter("CR", 0.14);
                crossover_.setParameter("F", 0.38);
                variantSelected = "rand/1/bin";
                break;
            case 15://3f
                delta_ = 0.70;
                nr_ = 5;
                crossover_.setParameter("CR", 0.47);
                crossover_.setParameter("F", 0.98);
                variantSelected = "current-to-rand/2/bin";
                break;
            case 16://zdt
                delta_ = 0.09;
                nr_ = 2;
                crossover_.setParameter("CR", 0.16);
                crossover_.setParameter("F", 0.41);
                variantSelected = "rand/1/bin";
                break;
            case 17://wfg
                delta_ = 0.47;
                nr_ = 2;
                crossover_.setParameter("CR", 0.3);
                crossover_.setParameter("F", 0.4);
                variantSelected = "rand/1/bin";
                break;
      }
  }
  

  
  
    /**
     * Main function for offspring generation
     *
     * strategySelected: index of the selected operator
     * 1 : "rand/1/bin"
     * 2 : "rand/2/bin"
     * 3 : "current-to-rand/2/bin"
     * 4 : "current-to-rand/1/bin"
     *#5 : "rand-to-best/bin"   Don't work
     *#6 : "rand-to-best/2/bin" Don't work
     * @param cur_id: index of the current subproblem
     * @return 
     * @throws JMException
     */
    public Solution matingEvolution(int cur_id) throws JMException {
        int type;
        double rnd;

        Solution child = null;
        Solution[] parents;
        int matingSize_ = 3;

        Vector<Integer> p = new Vector<Integer>();

        rnd = PseudoRandom.randDouble();
        /* STEP 2.1: Mating selection based on probability */
        if (rnd < delta_) {
            type = 1; // neighborhood
        } else {
            type = 2; // whole population
        }

        switch (variantSelected) {
            case "rand/1/bin": //1:
                /**
                 * DE/rand/1/bin: u = x1 + F * (x2 - x3)
                 */
                matingSize_ = 3;
                matingSelection(p, cur_id, matingSize_, type);
                /* STEP 2.2: Reproduction */
                parents = new Solution[3];
                parents[0] = population_.get(p.get(0));
                parents[1] = population_.get(p.get(1));
                parents[2] = population_.get(p.get(2));
                crossover_.setParameter("DE_VARIANT", "rand/1/bin");
                /* Apply DE operator */
                child = (Solution) crossover_.execute(new Object[]{population_.get(cur_id), parents});
                /* Apply mutation */
                mutation_.execute(child);
                /* Function evaluation */
                problem_.evaluate(child);
                evaluations_++;
                /* STEP 2.3: Repair. Not necessary */
                /* STEP 2.4: Update ideal point z_ */
                updateReference(child);
                /* STEP 2.5: Update the current subproblem */
                updateProblem(child, cur_id, type);
                p.clear();
                break;
            case "rand/2/bin"://2:
                /**
                 * DE/rand/2/bin: u = xi + F * (x1 - x2) + F * (x3 - x4)
                 */
                matingSize_ = 4;
                matingSelection(p, cur_id, matingSize_, type);
                /* STEP 2.2: Reproduction */
                parents = new Solution[5];
                parents[0] = population_.get(p.get(0));
                parents[1] = population_.get(p.get(1));
                parents[2] = population_.get(p.get(2));
                parents[3] = population_.get(p.get(3));
                parents[4] = population_.get(cur_id);
                crossover_.setParameter("DE_VARIANT", "rand/2/bin");
                /* Apply DE operator */
                //child = (Solution) crossover_.execute_5(new Object[]{population_.get(cur_id), parents});
                child = (Solution) crossover_.execute(new Object[]{population_.get(cur_id), parents});
                /* Apply mutation */
                mutation_.execute(child);
                /* Function evaluation */
                problem_.evaluate(child);
                evaluations_++;
                /* STEP 2.3: Repair. Not necessary */
                /* STEP 2.4: Update ideal point z_ */
                updateReference(child);
                /* STEP 2.5: Update the current subproblem */
                updateProblem(child, cur_id, type);
                p.clear();
                break;
            case "current-to-rand/2/bin"://3:
                /**
                 * DE/current-to-rand/2/bin: u = xi + K * (x1 - xi) + F * (x2 - x3) + F * (x4 - x5)
                 */
                matingSize_ = 5;
                matingSelection(p, cur_id, matingSize_, type);

                /* STEP 2.2: Reproduction */
                parents = new Solution[6];

                parents[0] = population_.get(p.get(0));
                parents[1] = population_.get(p.get(1));
                parents[2] = population_.get(p.get(2));
                parents[3] = population_.get(p.get(3));
                parents[4] = population_.get(p.get(4));
                parents[5] = population_.get(cur_id);

                crossover_.setParameter("DE_VARIANT", "current-to-rand/2/bin");
                /* Apply DE operator */
                //child = (Solution) crossover_.execute_6(new Object[]{population_.get(cur_id), parents});
                child = (Solution) crossover_.execute(new Object[]{population_.get(cur_id), parents});

                /* Apply mutation */
                mutation_.execute(child);

                /* Function evaluation */
                problem_.evaluate(child);
                evaluations_++;

                /* STEP 2.3: Repair. Not necessary */

                /* STEP 2.4: Update ideal point z_ */
                updateReference(child);

                /* STEP 2.5: Update the current subproblem */
                updateProblem(child, cur_id, type);

                p.clear();
                break;
            case "current-to-rand/1/bin"://4:
                /**
                 * DE/current-to-rand/1/bin: u = xi + K * (xi - x1) + F * (x2 - x3)
                 */
                matingSize_ = 3;
                matingSelection(p, cur_id, matingSize_, type);

                /* STEP 2.2: Reproduction */
                parents = new Solution[3];
                parents[0] = population_.get(p.get(0));
                parents[1] = population_.get(p.get(1));
                parents[2] = population_.get(p.get(2));

                crossover_.setParameter("DE_VARIANT", "current-to-rand/1/bin");

                /* Apply DE operator */
                child = (Solution) crossover_.execute(new Object[]{
                    population_.get(cur_id), parents});

                /* Apply mutation */
                mutation_.execute(child);

                /* Function evaluation */
                problem_.evaluate(child);

                evaluations_++;

                /* STEP 2.3: Repair. Not necessary */

                /* STEP 2.4: Update ideal point z_ */
                updateReference(child);

                /* STEP 2.5: Update the current subproblem */
                updateProblem(child, cur_id, type);

                p.clear();
                break;
        }
        return child;
    }
  /**
   * initUniformWeight
   */
  public void initUniformWeight() {
    if ((problem_.getNumberOfObjectives() == 2) && (populationSize_ <= 100)) {
      for (int n = 0; n < populationSize_; n++) {
        double a = 1.0 * n / (populationSize_ - 1);
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
  //      System.out.println(lambda_[n][0]);
  //      System.out.println(lambda_[n][1]);
      } // for
    } // if
    else {
      String dataFileName;
      dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" +
        populationSize_ + ".dat";

//      System.out.println(dataDirectory_);
//      System.out.println(dataDirectory_ + "/" + dataFileName);

      try {
        // Open the file
        FileInputStream fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        int numberOfObjectives = 0;
        int i = 0;
        int j = 0;
        String aux = br.readLine();
        while (aux != null) {
          StringTokenizer st = new StringTokenizer(aux);
          j = 0;
          numberOfObjectives = st.countTokens();
          while (st.hasMoreTokens()) {
            double value = (new Double(st.nextToken())).doubleValue();
            lambda_[i][j] = value;
            //System.out.println("lambda["+i+","+j+"] = " + value) ;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        System.err.println("initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName);
        e.printStackTrace();
      }
    } // else

    //System.exit(0) ;
  } // initUniformWeight



    public void comp_utility()
    {
        double f1, f2, uti, delta;
        for(int n=0; n<populationSize_; n++)
        {
            f1 = fitnessFunction(population_.get(n), lambda_[n]);
            f2 = fitnessFunction(savedValues_[n], lambda_[n]);
            delta = f2 - f1;
            if(delta>0.001)  
              utility_[n] = 1.0;
            else{
                // uti = 0.95*(1.0+delta/0.001)*utility_[n];
                uti = (0.95 + (0.05 * delta/0.001)) * utility_[n];
                utility_[n] = uti<1.0?uti:1.0;
            }            
            savedValues_[n] = new Solution(population_.get(n));
        }
        
    }


  /**
   *
   */
  public void initNeighborhood() {
    double[] x = new double[populationSize_];
    int[] idx = new int[populationSize_];

    for (int i = 0; i < populationSize_; i++) {
      // calculate the distances based on weight vectors
      for (int j = 0; j < populationSize_; j++) {
        x[j] = Utils.distVector(lambda_[i], lambda_[j]);
        //x[j] = dist_vector(population[i].namda,population[j].namda);
        idx[j] = j;
      //System.out.println("x["+j+"]: "+x[j]+ ". idx["+j+"]: "+idx[j]) ;
      } // for

      // find 'niche' nearest neighboring subproblems
      Utils.minFastSort(x, idx, populationSize_, T_);
      //minfastsort(x,idx,population.size(),niche);

        System.arraycopy(idx, 0, neighborhood_[i], 0, T_);
    } // for
  } // initNeighborhood

  /**
   *
   */
  public void initPopulation() throws JMException, ClassNotFoundException {
    for (int i = 0; i < populationSize_; i++) {
      Solution newSolution = new Solution(problem_);

      problem_.evaluate(newSolution);
      evaluations_++;
      population_.add(newSolution) ;
      savedValues_[i] = new Solution(newSolution);
    } // for
  } // initPopulation

  /**
   *
   */
  void initIdealPoint() throws JMException, ClassNotFoundException {
    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
      z_[i] = 1.0e+30;
      indArray_[i] = new Solution(problem_);
      problem_.evaluate(indArray_[i]);
      evaluations_++;
    } // for

    for (int i = 0; i < populationSize_; i++) {
      updateReference(population_.get(i));
    } // for
  } // initIdealPoint

  /**
   *
   */
  public void matingSelection(Vector<Integer> list, int cid, int size, int type) {
    // list : the set of the indexes of selected mating parents
    // cid  : the id of current subproblem
    // size : the number of selected mating parents
    // type : 1 - neighborhood; otherwise - whole population
    int ss;
    int r;
    int p;

    ss = neighborhood_[cid].length;
    while (list.size() < size) {
      if (type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = neighborhood_[cid][r];
      //p = population[cid].table[r];
      } else {
        p = PseudoRandom.randInt(0, populationSize_ - 1);
      }
      boolean flag = true;
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i) == p) // p is in the list
        {
          flag = false;
          break;
        }
      }

      //if (flag) list.push_back(p);
      if (flag) {
        list.addElement(p);
      }
    }
  } // matingSelection


   public List<Integer> tour_selection(int depth)
   {

	// selection based on utility
  List<Integer> selected     = new ArrayList<Integer>();
	List<Integer> candidate    = new ArrayList<Integer>();

	for(int k=0; k<problem_.getNumberOfObjectives(); k++)
      selected.add(k);   // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU (NOT SORTED!!!!)
  
  
	for(int n=problem_.getNumberOfObjectives(); n<populationSize_; n++)
            candidate.add(n);  // set of unselected weights

	while(selected.size()<(int)(populationSize_/5.0))
	{
	    //int best_idd = (int) (rnd_uni(&rnd_uni_init)*candidate.size()), i2;
            int best_idd = (int) (PseudoRandom.randDouble()*candidate.size());
            //System.out.println(best_idd);
            int i2;
	    int best_sub = candidate.get(best_idd);
            int s2;
            for(int i=1; i<depth; i++)
            {
                i2  = (int) (PseudoRandom.randDouble()*candidate.size());
                s2  = candidate.get(i2);
                //System.out.println("Candidate: "+i2);
                if(utility_[s2]>utility_[best_sub])
                {
                    best_idd = i2;
                    best_sub = s2;
                }
            }
	    selected.add(best_sub);
	    candidate.remove(best_idd);
	}
        return selected;
    }


  /**
   *
   * @param individual
   */
  void updateReference(Solution individual) {
    for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < z_[n]) {
        z_[n] = individual.getObjective(n);

        indArray_[n] = individual;
      }
    }
  } // updateReference

  /**
   * @param individual
   * @param id
   * @param type
   */
  void updateProblem(Solution indiv, int id, int type) {
    // indiv: child solution
    // id:   the id of current subproblem
    // type: update solutions in - neighborhood (1) or whole population (otherwise)
    int size;
    int time;

    time = 0;

    if (type == 1) {
      size = neighborhood_[id].length;
    } else {
      size = population_.size();
    }
    int[] perm = new int[size];

    Utils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (type == 1) {
        k = neighborhood_[id][perm[i]];
      } else {
        k = perm[i];      // calculate the values of objective function regarding the current subproblem
      }
      double f1, f2;

      f1 = fitnessFunction(population_.get(k), lambda_[k]);
      f2 = fitnessFunction(indiv, lambda_[k]);

      if (f2 < f1) {
        population_.replace(k, new Solution(indiv));
        //population[k].indiv = indiv;
        time++;
      }
      // the maximal number of solutions updated is not allowed to exceed 'limit'
      if (time >= nr_) {
        return;
      }
    }
  } // updateProblem
  
   double innerproduct(double[] vec1, double[] vec2) {
        double sum = 0;
        for (int i = 0; i < vec1.length; i++) {
            sum += vec1[i] * vec2[i];
        }
        return sum;
    }

    double norm_vector(Vector<Double> x) {
        double sum = 0.0;
        for (int i = 0; i < (int) x.size(); i++) {
            sum = sum + x.get(i) * x.get(i);
        }
        return Math.sqrt(sum);
    }

    double norm_vector(double[] z) {
        double sum = 0.0;
        for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
            sum = sum + z[i] * z[i];
        }
        return Math.sqrt(sum);
    }

  double fitnessFunction(Solution individual, double[] lambda) {
    double fitness;
    fitness = 0.0;
    //System.out.println(functionType_);
    if (functionType_.equals("_TCHE1")) {
      double maxFun = -1.0e+30;

      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
        double diff = Math.abs(individual.getObjective(n) - z_[n]);

        double feval;
        if (lambda[n] == 0) {
          feval = 0.0001 * diff;
        } else {
          feval = diff * lambda[n];
        }
        if (feval > maxFun) {
          maxFun = feval;
        }
      } // for

      fitness = maxFun;
    } // if
     else if (functionType_.equals("_TCHE2")) {
            double max_fun = -1.0e+30;
            for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
                double diff = (individual.getObjective(n) - z_[n]);
                double feval;
                if (lambda[n] == 0) {
                    feval = diff / 0.0001;
                } else {
                    feval = diff / lambda[n];
                }
                if (feval > max_fun) {
                    max_fun = feval;
                }

            }
            fitness = max_fun;
        } else if (functionType_.equals("_PBI"))// if
        {
            double theta; // penalty parameter
            theta = 5.0;

            // normalize the weight vector (line segment)
            double nd = norm_vector(lambda);
            for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
                lambda[i] = lambda[i] / nd;
            }

            double[] realA = new double[problem_.getNumberOfObjectives()];
            double[] realB = new double[problem_.getNumberOfObjectives()];

            // difference between current point and reference point
            for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
                realA[n] = (individual.getObjective(n) - z_[n]);
            }

            // distance along the line segment
            double d1 = Math.abs(innerproduct(realA, lambda));

            // distance to the line segment
            for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
                realB[n] = (individual.getObjective(n) - (z_[n] + d1
                        * lambda[n]));
            }
            double d2 = norm_vector(realB);

            fitness = d1 + theta * d2;
        }
    else {
      System.err.println("MOEAD.fitnessFunction: unknown type " + functionType_);
      System.exit(-1);
    }
    return fitness;
  } // fitnessEvaluation
  
  
  /** @author Juanjo
   * This method selects N solutions from a set M, where N <= M
   * using the same method proposed by Qingfu Zhang, W. Liu, and Hui Li in
   * the paper describing MOEA/D-DRA (CEC 09 COMPTETITION)
   * An example is giving in that paper for two objectives. 
   * If N = 100, then the best solutions  attenting to the weights (0,1), 
   * (1/99,98/99), ...,(98/99,1/99), (1,0) are selected. 
   * 
   * Using this method result in 101 solutions instead of 100. We will just 
   * compute 100 even distributed weights and used them. The result is the same
   * 
   * In case of more than two objectives the procedure is:
   * 1- Select a solution at random
   * 2- Select the solution from the population which have maximum distance to
   * it (whithout considering the already included)
   * 
   * 
   * 
   * @param n: The number of solutions to return
   * @return A solution set containing those elements
   * 
   */
   SolutionSet finalSelection(int n) throws JMException {
     SolutionSet res = new SolutionSet(n);
     if (problem_.getNumberOfObjectives() == 2) { // subcase 1                     
       double [][] intern_lambda = new double[n][2];
       for (int i = 0; i < n; i++) {
         double a = 1.0 * i / (n - 1);
         intern_lambda[i][0] = a;
         intern_lambda[i][1] = 1 - a;                
       } // for
       
       // we have now the weights, now select the best solution for each of them
       for (int i = 0; i < n; i++) {
         Solution current_best = population_.get(0);
         int index             = 0;
         double value          = fitnessFunction(current_best, intern_lambda[i]);
         for (int j = 1; j < n; j++) {           
             double aux = fitnessFunction(population_.get(j),intern_lambda[i]); // we are looking the best for the weight i
             if (aux < value) { // solution in position j is better!               
               value = aux;
               current_best = population_.get(j);           
           }
         }
         res.add(new Solution(current_best));
       }
       
     } else { // general case (more than two objectives)
       
       Distance distance_utility = new Distance();
       int random_index = PseudoRandom.randInt(0, population_.size()-1);
       
       // create a list containing all the solutions but the selected one (only references to them)
       List<Solution> candidate = new LinkedList<Solution>();
       candidate.add(population_.get(random_index));
       
       
       for (int i = 0; i< population_.size(); i++) {
         if (i != random_index)
          candidate.add(population_.get(i));
       } // for
       
       while (res.size() < n) {
         int index = 0;
         Solution selected = candidate.get(0); // it should be a next! (n <= population size!)
         double   distance_value = distance_utility.distanceToSolutionSetInObjectiveSpace(selected, res);
         int i = 1;
         while (i < candidate.size()) {
           Solution next_candidate = candidate.get(i);
           double aux = distance_value = distance_utility.distanceToSolutionSetInObjectiveSpace(next_candidate, res);
           if (aux > distance_value) {
             distance_value = aux;
             index = i;
           }
           i++;
         }
         
         // add the selected to res and remove from candidate list
         res.add(new Solution(candidate.remove(index)));                           
       } // 
     }
     return res;
   }
} // MOEAD_DRA

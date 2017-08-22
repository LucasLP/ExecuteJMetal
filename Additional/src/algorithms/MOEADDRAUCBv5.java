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

import org.uma.jmetal.algorithm.multiobjective.moead.*;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import pesquisajmetalcode.UCB.UCB;
import pesquisajmetalcode.UCB.UCB_set;

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
public class MOEADDRAUCBv5 extends MOEADDRA {
    
    String name = "";
 

    public MOEADDRAUCBv5(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations, MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
    }
  
  
  

  @Override public void run() {
    initializePopulation() ;
    initializeUniformWeight();
    initializeNeighborhood();
    initializeIdealPoint() ;

    int generation = 0 ;
    evaluations = populationSize ;
    
    
    draTime = 30;//default = 30
                            
    UCB_set hh[] = new UCB_set[populationSize];
      for (int i = 0; i < hh.length; i++) {
          hh[i] =  new UCB_set((int)(0.1*populationSize), //Window Size
                             30000,                     //Init
                             0,                     //End
                             10,                      //Step  
                             0.8,                       //C
                             0.9);                      //D
          hh[i].addSelector("set", new Integer[]{1,2,3,4,5,6,7,8,9,  /*18,19,/***/20,21,22});//,10,11,12,13,14,15,16,17});
          //System.out.println(i+"# "+hh[i].info());
      }
      boolean block = true;
      int blockTime    = 5000;
      int nonBlocktime = 10000;
      int time  = 0;
      
      //System.out.println(0+"# "+hh[0].info());
    
    ucb_configuration_vIrace(1);/**/
    //hh.addSelector("set", new Integer[]{1,2,3,4,5,6,7,8,9,10});
    //ucb_configuration(0);//_vIrace(0);
    
    
    do {
      int[] permutation = new int[populationSize];
      MOEADUtils.randomPermutation(permutation, populationSize);
      
     
      
      //if(evaluations%blockTime==0){
        //  block=!block;
          //System.out.println(evaluations+" "+block);
      //}
      
      for (int i = 0; i < populationSize; i++) {
          
           time++;
          if(block){
              if(time==blockTime){
                  time = 1;
                  block = false;
                  //System.out.println(evaluations+" "+block);
              }
          }else{
              if(time==nonBlocktime){
                  time = 1;
                  block = true;
                 // System.out.println(evaluations+" "+block);
              }
          }
          
        int subProblemId = permutation[i];
        frequency[subProblemId]++;
        
        if(!block){
        if(hh[subProblemId].isWorking(evaluations, maxEvaluations) ){
            if( !hh[subProblemId].isWSfull()  ||  hh[subProblemId].step==0){//(evaluations-populationSize)%hh[subProblemId].maxStep==0)){
                hh[subProblemId].selectOperators();
                //ucbTeste((Integer)hh.getOperator("set"));
                //ucb_configuration_vIrace((Integer)hh[subProblemId].getOperator("set"));
            }
            ucb_configuration_vIrace((Integer)hh[subProblemId].getOperator("set"));
        }
        }else{
            ucb_configuration_vIrace(1);
        }

        NeighborType neighborType = chooseNeighborType() ;
        List<DoubleSolution> parents = parentSelection(subProblemId, neighborType) ;
        //List<DoubleSolution> parents = parentSelection(subProblemId, neighborType, differentialEvolutionCrossover.getVariant()) ; 
        hh[subProblemId].setX(fitnessFunction(population.get(subProblemId), lambda[subProblemId]));
        /**/

        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        
        List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);
        DoubleSolution child = children.get(0) ;
        mutationOperator.execute(child);
        problem.evaluate(child);
        
        if(!block){
        if(hh[subProblemId].isWorking(evaluations, maxEvaluations) && (!hh[subProblemId].isWSfull() || hh[subProblemId].step==0)){//(evaluations-populationSize)%hh[subProblemId].maxStep==0)){
            hh[subProblemId].setY(fitnessFunction(child, lambda[subProblemId]));//qnto menor melhor
            hh[subProblemId].adjustSlidingWindow();//Adjust the sliding window
            hh[subProblemId].creditAssignment();//Calculate the credit assignment useing a Decaying Factor
            hh[subProblemId].nextStep();
        }else if(hh[subProblemId].step > 0){
            hh[subProblemId].nextStep();
        }/**/
         }
        
        evaluations++;
        updateIdealPoint(child);
        updateNeighborhood(child, subProblemId, neighborType);
      }

      generation++;
      if (generation % draTime == 0) {
        utilityFunction();
      }

    } while (evaluations < maxEvaluations);

    
    
    //hh[0].printHistory("set");
  }
  
  
  
  
  
  
  private void ucb_configuration_vIrace(Integer i){
      switch(i){
            case 1://DEFAULT CONFIGURATION
                neighborhoodSelectionProbability = 0.9;
                maximumNumberOfReplacedSolutions = 2;
                
                differentialEvolutionCrossover.setCr(1.0);
                differentialEvolutionCrossover.setF(0.5);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 2://Concave
                neighborhoodSelectionProbability = 0.06;
                maximumNumberOfReplacedSolutions = 1;
                differentialEvolutionCrossover.setCr(0.02);
                differentialEvolutionCrossover.setF(0.71);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 3://Convex
                neighborhoodSelectionProbability = 0.66;
                maximumNumberOfReplacedSolutions = 8;
                differentialEvolutionCrossover.setCr(0.35);
                differentialEvolutionCrossover.setF(0.46);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");
                break;
            case 4://Diconnect
                neighborhoodSelectionProbability = 0.14;
                maximumNumberOfReplacedSolutions = 5;
                differentialEvolutionCrossover.setCr(0.09);
                differentialEvolutionCrossover.setF(0.52);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 5://Linear
                neighborhoodSelectionProbability = 0.33;
                maximumNumberOfReplacedSolutions = 5;
                differentialEvolutionCrossover.setCr(0.29);
                differentialEvolutionCrossover.setF(0.17);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 6://2f
                neighborhoodSelectionProbability = 0.2;
                maximumNumberOfReplacedSolutions = 9;
                differentialEvolutionCrossover.setCr(0.93);
                differentialEvolutionCrossover.setF(0.09);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 7://3f
                neighborhoodSelectionProbability = 0.74;
                maximumNumberOfReplacedSolutions = 7;
                differentialEvolutionCrossover.setCr(0.13);
                differentialEvolutionCrossover.setF(0.92);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");
                break;
            case 8://zdt
                neighborhoodSelectionProbability = 0.33;
                maximumNumberOfReplacedSolutions = 9;
                differentialEvolutionCrossover.setCr(0.24);
                differentialEvolutionCrossover.setF(0.61);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 9://wfg
                neighborhoodSelectionProbability = 0.2;
                maximumNumberOfReplacedSolutions = 10;
                differentialEvolutionCrossover.setCr(0.47);
                differentialEvolutionCrossover.setF(0.25);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
                //=======================================================
            case 10://Concave
                neighborhoodSelectionProbability = 0.77;
                maximumNumberOfReplacedSolutions = 5;
                differentialEvolutionCrossover.setCr(0.07);
                differentialEvolutionCrossover.setF(0.84);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");
                break;
            case 11://Convex
                neighborhoodSelectionProbability = 0.22;
                maximumNumberOfReplacedSolutions = 9;
                differentialEvolutionCrossover.setCr(0.58);
                differentialEvolutionCrossover.setF(0.59);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");
                break;
            case 12://Diconnect
                neighborhoodSelectionProbability = 0.72;
                maximumNumberOfReplacedSolutions = 6;
                differentialEvolutionCrossover.setCr(0.79);
                differentialEvolutionCrossover.setF(0.45);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 13://Linear
                neighborhoodSelectionProbability = 0.74;
                maximumNumberOfReplacedSolutions = 2;
                differentialEvolutionCrossover.setCr(0.33);
                differentialEvolutionCrossover.setF(0.26);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 14://2f
                neighborhoodSelectionProbability = 0.54;
                maximumNumberOfReplacedSolutions = 4;
                differentialEvolutionCrossover.setCr(0.14);
                differentialEvolutionCrossover.setF(0.38);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 15://3f
                neighborhoodSelectionProbability = 0.70;
                maximumNumberOfReplacedSolutions = 9;
                differentialEvolutionCrossover.setCr(0.9);
                differentialEvolutionCrossover.setF(0.68);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");
                break;
            case 16://zdt
                neighborhoodSelectionProbability = 0.09;
                maximumNumberOfReplacedSolutions = 2;
                differentialEvolutionCrossover.setCr(0.16);
                differentialEvolutionCrossover.setF(0.41);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 17://wfg
                neighborhoodSelectionProbability = 0.47;
                maximumNumberOfReplacedSolutions = 2;
                differentialEvolutionCrossover.setCr(0.3);
                differentialEvolutionCrossover.setF(0.4);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
                //=======================================================
            case 18:
                neighborhoodSelectionProbability = 0.95;
                maximumNumberOfReplacedSolutions = 4;
                differentialEvolutionCrossover.setCr(0.8);
                differentialEvolutionCrossover.setF(0.1);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                break;
            case 19:
                neighborhoodSelectionProbability = 0.97;
                maximumNumberOfReplacedSolutions = 7;
                differentialEvolutionCrossover.setCr(0.4);
                differentialEvolutionCrossover.setF(0.1);
                differentialEvolutionCrossover.setVariant("rand/1/bin");//2
                break;
            case 20:
                neighborhoodSelectionProbability = 0.99;
                maximumNumberOfReplacedSolutions = 5;
                differentialEvolutionCrossover.setCr(0.2);
                differentialEvolutionCrossover.setF(0.8);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");//2
                break;
            case 21:
                neighborhoodSelectionProbability = 0.7;
                maximumNumberOfReplacedSolutions = 3;
                
                differentialEvolutionCrossover.setCr(1.0);
                differentialEvolutionCrossover.setF(0.9);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");//2
                break;
            case 22:
                neighborhoodSelectionProbability = 0.90;
                maximumNumberOfReplacedSolutions = 4;
                differentialEvolutionCrossover.setCr(0.8);
                differentialEvolutionCrossover.setF(0.4);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");//2
                break;
      }
  }
  
  
  private void ucb_configuration(Integer i){
      switch(i){
            case 1://DEFAULT CONFIGURATION
                neighborhoodSelectionProbability = 0.9;
                maximumNumberOfReplacedSolutions = 2;
                
                differentialEvolutionCrossover.setCr(1.0);
                differentialEvolutionCrossover.setF(0.5);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
              //  draTime = 30;
                break;
            case 2://PARTE DA CONFIGURAÇÃO ENCONTRADA PELO IRACE
                neighborhoodSelectionProbability = 0.95;
                maximumNumberOfReplacedSolutions = 2;
                
                differentialEvolutionCrossover.setCr(0.4);
                differentialEvolutionCrossover.setF(0.37);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
             //   draTime = 20;
                break;
            case 3:
                neighborhoodSelectionProbability = 0.8;
                maximumNumberOfReplacedSolutions = 3;
                
                differentialEvolutionCrossover.setCr(0.8);
                differentialEvolutionCrossover.setF(0.7);
                differentialEvolutionCrossover.setVariant("current-to-rand/2/bin");
               // draTime = 25;
                break;
            case 4:
                neighborhoodSelectionProbability = 1.0;
                maximumNumberOfReplacedSolutions = 1;
                
                differentialEvolutionCrossover.setCr(0.6);
                differentialEvolutionCrossover.setF(0.7);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");
           //     draTime = 40;
                break;
            case 5:
                neighborhoodSelectionProbability = 0.6;
                maximumNumberOfReplacedSolutions = 2;
                
                differentialEvolutionCrossover.setCr(0.4);
                differentialEvolutionCrossover.setF(0.1);
                differentialEvolutionCrossover.setVariant("rand/1/bin");
                //draTime = 35;
                break;
            case 6:
                neighborhoodSelectionProbability = 0.95;
                maximumNumberOfReplacedSolutions = 1;
                
                differentialEvolutionCrossover.setCr(1.0);
                differentialEvolutionCrossover.setF(0.4);
                differentialEvolutionCrossover.setVariant("rand/2/bin");
                //draTime = 33;
                break;
            case 7:
                neighborhoodSelectionProbability = 0.3;
                maximumNumberOfReplacedSolutions = 6;
                
                differentialEvolutionCrossover.setCr(0.2);
                differentialEvolutionCrossover.setF(0.8);
                differentialEvolutionCrossover.setVariant("current-to-rand/1/bin");
                //draTime = 27;
                break;
            case 8:
                neighborhoodSelectionProbability = 0.75;
                maximumNumberOfReplacedSolutions = 10;
                
                differentialEvolutionCrossover.setCr(0.8);
                differentialEvolutionCrossover.setF(0.1);
                differentialEvolutionCrossover.setVariant("current-to-rand/2/bin");
                //draTime = 24;
                break;    
            case 9:
                neighborhoodSelectionProbability = 1.0;
                maximumNumberOfReplacedSolutions = 2;
                
                differentialEvolutionCrossover.setCr(1.0);
                differentialEvolutionCrossover.setF(0.7);
                differentialEvolutionCrossover.setVariant("rand/1/bin");//1
                //draTime = 37;
                break;
            case 10:
                neighborhoodSelectionProbability = 1.0;
                maximumNumberOfReplacedSolutions = 3;
                
                differentialEvolutionCrossover.setCr(1.0);
                differentialEvolutionCrossover.setF(0.9);
                differentialEvolutionCrossover.setVariant("rand/1/bin");//1
                //draTime = 30;
                break;
      }
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

  
  
  
  
  
  
  

  @Override public String getName() {
    return name ;
  }
  
  public void setName(String str){
      name = str;
  }

  @Override public String getDescription() {
    return "Multi-Objective Evolutionary Algorithm based on Decomposition. Version with Dynamic Resource Allocation" ;
  }
}

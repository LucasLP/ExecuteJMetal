package myJMetal.QuasiSimplex;

/**
 *
 * @author lucas
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD.NeighborType;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;

/**
 * Only to problem with variable type 'Real'
 *
 * For to use this local search, you need the fitness function public
 *
 * @author lucas prestes
 * @email lucas.prestes.lp@gmail.com
 */
public class QuasiSimplex implements LocalSearchOperator {
    public enum Implementation {
        Dong, Coello;
            public int getEvaluations(){
                if(this.equals(Dong)) return 6;
                if(this.equals(Coello)) return 4;
                return 0;
            }
    }
    public enum MarkType {mark,unmark,all}
    
    
    AbstractMOEAD moead;
    protected AbstractDoubleProblem problem;
    
    boolean []mark ;
    protected Implementation implementation;
    protected Integer frequency ;
    protected MarkType select;
    protected NeighborType scope; 
    
    
    protected int lastNumImprovements = 0;
    
    
    public QuasiSimplex(AbstractMOEAD moead, AbstractDoubleProblem problem, int populationSize) {
        this.moead = moead;
        this.problem = problem;
        
        scope = NeighborType.NEIGHBOR;
        select = MarkType.mark;
        implementation = Implementation.Coello;
        frequency = 15050;
        mark = new boolean[populationSize];
        if (select.equals(QuasiSimplex.MarkType.mark) ||
            select.equals(QuasiSimplex.MarkType.all)) {
            for (int i = 0; i < populationSize; i++) {
                mark[i] = true;
            }
        } else if (select.equals(QuasiSimplex.MarkType.unmark)) {
            for (int i = 0; i < populationSize; i++) {
                mark[i] = false;
            }
        }
    }
    
    public void configurate(HashMap<String, Object> parameters){
        if(parameters!=null){
            if(parameters.containsKey("--frec")) frequency = (Integer)parameters.get("--frec");
            if(parameters.containsKey("--selection")) select = (MarkType) parameters.get("--selection");
            if(parameters.containsKey("--scope")) scope = (NeighborType) parameters.get("--scope");
            if(parameters.containsKey("--impl")) implementation = (Implementation) parameters.get("--impl");
        }
    }


    
    

    
    public class MyComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
        @Override
        public int compare(S solution1, S solution2) {
            if (solution1 == null) {
                return 1;
            } else if (solution2 == null) {
                return -1;
            }

            double fitness1 = (double) solution1.getAttribute("Fitness");
            double fitness2 = (double) solution2.getAttribute("Fitness");
            if (fitness1 < fitness2) {
                return -1;
            }

            if (fitness1 > fitness2) {
                return 1;
            }

            return 0;
        }
    }

    
    
    public List<DoubleSolution> execute(List<DoubleSolution> population, double[] lambda){
        int operationNumber = implementation.getEvaluations();
        //Passos baseados no código C
        int N = problem.getNumberOfVariables();
        int sizeBestSet = N + 1;// [0] to [N]
        int worst = sizeBestSet - 1;// is [N]
        if (population.size() < sizeBestSet) {
            sizeBestSet = population.size() - 1;
            worst = sizeBestSet - 1;
        }

        //N_op solutions
        DoubleSolution[] operacoes = new DoubleSolution[operationNumber];
        for (int i = 0; i < operationNumber; i++) {
            operacoes[i] = (DoubleSolution) population.get(0).copy();
        }

        //Passo 2: Ordenar populaçao principal
        for (int i = 0; i < population.size(); i++) {
            population.get(i).setAttribute("Fitness",moead.fitnessFunction(population.get(i), lambda));
        }
        population.sort(new MyComparator());

        //Passo 3: COPIAR os N melhores para seu conjunto
        //N melhores solution
        DoubleSolution[] Nmelhores = new DoubleSolution[sizeBestSet];
        for (int i = 0; i < sizeBestSet; i++) {
            Nmelhores[i] = (DoubleSolution) population.get(i).copy();
        }

        //Passo 4: Calcular o Centroide
        double[] centroide = new double[N];
        for (int i = 0; i < N; i++) {
            double total = 0.0;
            for (int j = 0; j < sizeBestSet; j++) {//Soma a variavel i de todas os melhores
                total += population.get(j).getVariableValue(i);
            }
            centroide[i] = total / sizeBestSet;
        }

        //Passo 5: Aplicar os operadores
        applyOperators(operacoes, centroide, population.get(worst), population.get(0), N);
        
        //Passo 6: Repair
        for (int i = 0; i < operationNumber; i++) {
            for (int j = 0; j < N; j++) {
                if (operacoes[i].getVariableValue(j) < problem.getLowerBound(j)) {
                    operacoes[i].setVariableValue(j,problem.getLowerBound(j));
                } else if (operacoes[i].getVariableValue(j) > problem.getUpperBound(j)) {
                    operacoes[i].setVariableValue(j,problem.getUpperBound(j));
                }
            }
        }
        
         //Passo 7: Substituir os piores do conjunto dos melhores 
        //pelas soluções do conjunto N_op
        List<DoubleSolution> qs = new ArrayList();
        int c = 0;
        for (int i = population.size()-operationNumber; i < population.size(); i++) {
            operacoes[c].setAttribute("Fitness", moead.fitnessFunction(operacoes[c], lambda));
            problem.evaluate(operacoes[c]);
            if ((Double) population.get(i).getAttribute("Fitness") > (Double) operacoes[c].getAttribute("Fitness")) {
                qs.add(operacoes[c]);
            }
            c++;
        }
        lastNumImprovements = qs.size();/**/
        
        
        
        
        /*DoubleSolution[] ops = new DoubleSolution[operationNumber];
        boolean[] mark = new boolean[operationNumber];
        for (int i = 0; i < operationNumber; i++) {
            ops[i] = (DoubleSolution) operacoes[i].copy();
            problem.evaluate(ops[i]);
            ops[i].setAttribute("Fitness", moead.fitnessFunction(ops[i], lambda));
             mark[i] = false;
            //Obs: (N-1) is last position from vector 'Nmelhores'
        }
        List<DoubleSolution> qs = new ArrayList();
        int num = 0;
        for (int i = population.size() - 1; i >= 0; i--) {
            for (int k = 0; k < operationNumber; k++) {
                if (mark[k] == false) {
                    if ((double) population.get(i).getAttribute("Fitness") > (double) ops[k].getAttribute("Fitness")) {
                        mark[k] = true;
                        qs.add(ops[k]);
                        num++;
                        k = operationNumber;
                    }
                }
            }
        }/**/
        
        
        
        
        
        
        return qs;
    }
    
    private void applyOperators(DoubleSolution[] operacoes, double[] centroide, DoubleSolution worst, DoubleSolution best, int N){
        Random r = new Random();
        double alfa = 1;
        double beta = 2;
        double omega = 0.5;
        double alfaE = r.nextDouble();//Math.random();
        double alfaC = r.nextDouble();//Math.random();
        if(implementation.equals(Implementation.Dong)){
            for (int i = 0; i < N; i++) {
                //centroide
                double c = centroide[i];
                //worst element
                double w = worst.getVariableValue(i);
                //best element
                double b = best.getVariableValue(i);

                operacoes[0].setVariableValue(i, (w + (w - c)));                    //Reflexao pior 
                operacoes[1].setVariableValue(i, (b + (c - b)));                    //Reflexao melhor
                operacoes[2].setVariableValue(i, (w + ((1.0 + alfaE) * (w - c))));  //Expansao pior
                operacoes[3].setVariableValue(i, (b + (alfaE * (c - b))));          //Expansao melhor
                operacoes[4].setVariableValue(i, (w - (alfaC * (w - c))));          //Compressao pior
                operacoes[5].setVariableValue(i, (b - (alfaC * (c - b))));          //Compressao melhor
            }
        }else if(implementation.equals(Implementation.Coello)){
            for (int i = 0; i < N; i++) {
                //centroide
                double c = centroide[i];
                //worst element
                double w = worst.getVariableValue(i);

                operacoes[0].setVariableValue(i,(1 + alfa) * c - alfa * w);
                operacoes[1].setVariableValue(i,(1 + alfa * omega) * c - alfa * omega * w);
                operacoes[2].setVariableValue(i,(1 + alfa * beta) * c - alfa * beta * w);
                operacoes[3].setVariableValue(i,(1 - beta) * c - beta * w);
            }
        }
    }
    
    
    @Override
    public Object execute(Object source) {
        throw new UnsupportedOperationException("Not supported yet, use another form."); //To change body of generated methods, choose Tools | Templates.        double alfa = 1;
    }

    @Override
    public int getNumberOfNonComparableSolutions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int getNumberOfImprovements() {
        return lastNumImprovements;
    }

    @Override
    public int getEvaluations() {
        return implementation.getEvaluations();// * Gen;
    }
    
    public boolean isAvaible(int l){
        return (select.equals(QuasiSimplex.MarkType.mark)&& mark[l] ) ||
                (select.equals(QuasiSimplex.MarkType.unmark)&& !mark[l] ) ||
                (select.equals(QuasiSimplex.MarkType.all) ) ;
    }
    
    public void setMark(int index, boolean b){
        mark[index] = b;
    }

    public boolean[] getMark() {
        return mark;
    }
    
    public MarkType getSelect() {
        return select;
    }

    public NeighborType getScope() {
        return scope;
    }

    public Integer getFrequency() {
        return frequency;
    }

}
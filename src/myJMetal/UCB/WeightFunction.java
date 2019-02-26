
package myJMetal.UCB;

/**
 * Define an weightFunction to use with UCB
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public enum WeightFunction {
    Null, //Normal UCB
    Linear, // has weight in SW values linearly 
    Polynomial,// has weight in SW values Polinomially
    PolynomialInvert;// has weight in SW values Polinomially

    public Double decayFactorFunction(Double i, Double max) {
        switch (this) {
            case Linear: {
                Double v = (double) (i / max);
                return v;
            }
            case Polynomial: // y = x^2
            {
                Double v = (double) (i / max);
                return (v * v);
            }
            case PolynomialInvert: // y = -x ^ 2 + 2x  
            {
                Double v = (double) (i / max);
                return ((-v * v) + (2 * v));
            }
            case Null:
                break;
            default:
                break;
        }
        return 1.0;
    }

    public void test() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i + ": " + decayFactorFunction((double) (i + 1), (double) 10));
        }
    }
}

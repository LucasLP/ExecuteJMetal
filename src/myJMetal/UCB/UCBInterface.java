package myJMetal.UCB;

/**
 *	UCB Interface
 *
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public interface UCBInterface {
    public void selectOperator();
    public void adjustSlidingWindow();
    public void creditAssignment();
    public boolean isSWsizeMax();
    public Integer getK();
    public Object getOperator();
    public void setSetUCB(UCB_set setUCB);
    public Integer[] getHistory_using() ;
    public Object[] getOperatorPool();
}

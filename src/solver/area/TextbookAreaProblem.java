package solver.area;

import representation.bounds.functions.Domain;
import solver.TextbookProblem;

/**
 *  A textbook problem consists of a set of functions over a particular interval (domain) 
 */
public class TextbookAreaProblem extends TextbookProblem
{
    public TextbookAreaProblem(String[] functions, Domain domain, String data, double answer)
    {
        super(functions, domain, data, answer);
    }

    public TextbookAreaProblem(String[] functions, Domain domain, String data)
    {
        this(functions, domain, data, DEFAULT_ANSWER);
    }
    
    /**
     * @param f -- the set of functions (in terms of variable x)
     * @param data -- metadata describing the problem source.
     */
    public TextbookAreaProblem(String[] f, String data)
    {
        this(f, null, data, DEFAULT_ANSWER);
    }
    
    /**
     * @param f -- the set of functions (in terms of variable x)
     * @param data -- metadata describing the problem source.
     * @param answer -- the numeric answer for this problem
     */
    public TextbookAreaProblem(String[] f, String data, double answer)
    {
        this(f, null, data, answer);
    }
}

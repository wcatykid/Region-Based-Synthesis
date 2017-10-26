package solver;

import java.util.Vector;

import exceptions.DomainException;
import math.integral.DefiniteIntegral;

/**
 *  A solution to these problem types consists of integral expressions
 *
 */
public abstract class Solution
{
    // The integral expressions that form the solution for a particular problem type
    protected Vector<DefiniteIntegral> _integralExps;
    public Vector<DefiniteIntegral> getIntegralExpressions() { return _integralExps; }
    
    public Solution()
    {
        this(new Vector<DefiniteIntegral>());
    }

    /**
     * @param atoms -- a set of definite integrals describing a solution
     */
    public Solution(Vector<DefiniteIntegral> atoms)
    {
        _integralExps = new Vector<DefiniteIntegral>(atoms);
    }

    /**
     * @param atoms -- a set of definite integrals describing this solution
     */
    public void add(Vector<DefiniteIntegral> atoms)
    {
        for (DefiniteIntegral atom : atoms)
        {
            add(atom);
        }
    }

    /**
     * @param atom -- a definite integral describing this solution
     */
    public void add(DefiniteIntegral atom)
    {
        _integralExps.add(atom);
    }
    
    /**
     * @param solution -- a known solution (of definite integrals)
     */
    public void add(Solution solution)
    {
        _integralExps.addAll(solution.getIntegralExpressions());
    }
    

    /**
     * @return The evaluated sum represented by the individual integral expressions 
     */
    public double evaluate()
    {
        double sum = 0;
        
        for (DefiniteIntegral integral : _integralExps)
        {
            try
            {
                sum += integral.evaluate();
            }
            catch (DomainException e)
            {
                System.err.println("Given integral was not bounded: " + integral);
            }
        }

        return sum;
    }
    
    @Override
    public String toString()
    {
        String ret = "";

        for (DefiniteIntegral atom : _integralExps)
        {
            ret += atom.toLatexString() + " + ";
        }
        
        return ret.substring(0, ret.length() - 3);
    }
}

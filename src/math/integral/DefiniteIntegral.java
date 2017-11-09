package math.integral;

import exceptions.DomainException;
import math.external_interface.LocalMathematicaCasInterface;
import representation.bounds.functions.BoundedFunction;
import representation.bounds.functions.VariableT;

public class DefiniteIntegral extends IntegralExpression
{
    enum VARIABLE { X, Y }

    protected double _lowerBound;
    protected double _upperBound;
    protected double _memoizedEvaluation;
    public static final double DEFAULT_VALUE = Double.NEGATIVE_INFINITY;

    public DefiniteIntegral()
    {
        _lowerBound = DEFAULT_VALUE;
        _upperBound = DEFAULT_VALUE;
        _memoizedEvaluation = DEFAULT_VALUE;
    }

    public DefiniteIntegral(double lower, double upper, BoundedFunction f, VariableT variable)
    {
        super(f, variable);
        
        _lowerBound = lower;
        _upperBound = upper;
        _memoizedEvaluation = DEFAULT_VALUE;
    }
    
    /**
     * @return the value of this integral (when evaluated from lower to upper)
     * @throws DomainException 
     */
    public double evaluate() throws DomainException
    {
        //
        // Memoized Check
        //
        if (_memoizedEvaluation != DEFAULT_VALUE) return _memoizedEvaluation;
        
        //
        // Error Check
        //
        if (_lowerBound == DEFAULT_VALUE) throw new DomainException("Cannot handle -\\inf as lower bound.");

        if (_upperBound == DEFAULT_VALUE) throw new DomainException("Cannot handle +\\inf as upper bound.");

        //
        // Evaluate via Mathematica: Call Mathematica to evaluate the area represented by the integral
        //
        String query = toMathematicaString();
        
        _memoizedEvaluation = LocalMathematicaCasInterface.getInstance().queryComplexNumber(query).RealPart;
        
        return _memoizedEvaluation;
    }
    
    /**
     * @return a String representing the integral from lowerBound to upperBound of function f with respect to a variable
     */
    public String toMathematicaString()
    {
        String rep = "InputForm[";
        
        rep += "Integrate[";

        rep += _function.toFullMathematicaString();
        
        rep += ", { ";

        rep += _varWRT.toString();
        
        rep += ", ";
        
        rep += _lowerBound;

        rep += ", ";

        rep += _upperBound;

        rep += " }";
        
        rep += " ]";
        
        rep += " ]";
        
        System.out.println(rep);
        
        return rep;
    }

    /**
     * @return a String representing the integral from lowerBound to upperBound of function f with respect to a variable
     */
    public String toLatexString()
    {
        String rep = "\\int";

        rep += "_{ ";

        rep += _lowerBound;

        rep += " }";

        rep += "^{ ";

        rep += _upperBound;

        rep += " } { ";

        rep += _function.toCompactLatexString();
        
        rep += "} d" + _varWRT.toString();
        
        return rep;
    }

    @Override
    public String toString()
    {
        return toLatexString();
    }
}

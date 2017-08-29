package math.integral;

import representation.bounds.functions.BoundedFunction;
import representation.bounds.functions.VariableT;

/**
 * Represents an INdefinite integral expression: function, variable 
 *
 */
public class IntegralExpression
{
    protected BoundedFunction _function;
    protected VariableT _varWRT;

    protected IntegralExpression()
    {
        _varWRT = VariableT.X;
    }

    public IntegralExpression(BoundedFunction f, VariableT variable)
    {
        _function = f;
        _varWRT = variable;
    }
    
    public String toLatexString()
    {
        String s = "\\int ";
        
        s += _function.toCompactLatexString();
        
        s += " d" + _varWRT.toString();
        
        return s;
    }
    
    @Override
    public String toString()
    {
        return toLatexString();
    }
}

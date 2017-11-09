package representation.bounds.functions;

import exceptions.DomainException;
import representation.ComplexNumber;

/**
 * Represents a function-based subtraction: f(x) - g(x)
 */
public class DifferenceBoundedFunction extends BoundedFunction
{
    private static final long serialVersionUID = 1L;

    //
    // _left(x) - _right(x)
    //
    protected BoundedFunction _left;
    protected BoundedFunction _right;    

    public DifferenceBoundedFunction(BoundedFunction left, BoundedFunction right) throws DomainException
    {
        super(FunctionT.COMPOSITE);

        _left = left;
        _right = right;
        
        if (left.variableType() != right.variableType())
        {
            System.err.println("DifferenceBoundedFunction::Variable types disagree: " + left.variableType() + " " + right.variableType());
            throw new DomainException("DifferenceBoundedFunction::Variable types disagree: " + left.variableType() + " " + right.variableType());
        }
    }
    
    /**
     * Variable: x or y; consistency is verified in the constructor
     */
    @Override
    public VariableT variableType() { return _left.variableType(); }
    
    /**
     * @return String-based representation of this function: f(x) - g(x)
     */
    @Override
    public String toFullMathematicaString() { return "(" + _left.toFullMathematicaString() + ") - (" + _right.toFullMathematicaString() + ")"; }
    
    /**
     * @return String-based representation of this function: f(x) - g(x)
     */
    @Override
    public String toCompactLatexString() { return "(" + _left.toCompactLatexString() + ") - (" + _right.toCompactLatexString() + ")"; }

    @Override
    public ComplexNumber evaluateAtPoint(double x)
    {
        return _left.evaluateAtPoint(x).subtract( _right.evaluateAtPoint(x) ) ;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;

        if (!(obj instanceof DifferenceBoundedFunction)) return false;

        DifferenceBoundedFunction that = (DifferenceBoundedFunction)obj;

        if (this._fType != that._fType) return false;

        if (!this._left.equals(that._left)) return false;

        if (!this._right.equals(that._right)) return false;
        
        if (!this._domain.equals(that._domain)) return false;

        return true;
    }

    public boolean notEquals(Object obj) { return !this.equals(obj); }
}

package representation.bounds.functions;

import math.analysis.inverses.Inverses;
import math.external_interface.LocalMathematicaCasInterface;
import representation.ComplexNumber;
import representation.bounds.Bound;
import utilities.Utilities;

/**
 * Represents a function (but as a String)
 */
public class StringBasedFunction extends BoundedFunction
{
    private static final long serialVersionUID = 1L;

    protected String _baseFunction;
    public String getBaseFunction() { return _baseFunction; }

    protected String _transformed;
    public String getFunction() { return _transformed; }

    // The on-demand computed inverse
    protected StringBasedFunction _inverse;

    public StringBasedFunction(String f)
    {
        super(FunctionT.STRING);

        _baseFunction = f;
        _transformed = f;
        _inverse = null;

        if (f.contains("y"))  _variable = VariableT.Y;
    }

    /**
     * It is useful to know what type of function this String represents
     *  Note: this could be expanded to account for all of the basic function types.
     *  
     * @return The function is horizontal if it consists of constants and not variables
     * To ensure horizontal, we evaluate the entire string to acquire a constant...possibly
     */
    @Override
    public boolean isHorizontal()
    {
        String result = LocalMathematicaCasInterface.getInstance().query(getFunction());
        
        return !result.contains(_variable.toString());
    }
    
    /**
     * To satisfy inheritance from the BoundedFunction class
     */
    public StringBasedFunction clone() { return new StringBasedFunction(getFunction()); }

    /**
     * When a new parameter is specified, we perform a complete re-computation of the String 
     * version of the transformed function
     * f(x) ---> a f(b(x - h)) + k
     */
    private void transform()
    {
        // Reset / start with the base function
        _transformed = _baseFunction;

        //
        // Apply the transformations inside and out.
        //
        // Horizontal f( b(x - h) )
        if (!Utilities.equalDoubles(this.b, 1) || !Utilities.equalDoubles(this.h, 0))
        {
            String replacement = "";
            String interior = "(x " + (this.h > 0 ? "-" : "+") + " " + Math.abs(this.h) + ")";
            
            if (!Utilities.equalDoubles(this.b, 1) && Utilities.equalDoubles(this.h, 0)) replacement = "(" + this.b + "x)";
            else if (Utilities.equalDoubles(this.b, 1) && !Utilities.equalDoubles(this.h, 0)) replacement = interior;
            else if (!Utilities.equalDoubles(this.b, 1) && !Utilities.equalDoubles(this.h, 0))
            {
                replacement += "(" + this.b + interior + ")";
            }
            else replacement += "x";

            _transformed = _transformed.replaceAll("x", replacement);
        }

        //
        // Vertical:    a f(x) + k
        //
        if (!Utilities.equalDoubles(this.a, 1)) _transformed = this.a + " (" + _transformed + ")";

        if (!Utilities.equalDoubles(this.k, 0)) _transformed += (this.k > 0 ? "+" : "-") + Math.abs(this.k);
        
        // Since this function has been transformed, reset the inverse (so it needs to be recomputed on-demand).
        _inverse = null;
    }

    /**
     * Set the parameters
     * @param vertStr -- vertical stretch:    a * f(x)
     * @param horizStr -- horizontal stretch: f(b * x)
     */
    @Override
    public void stretch(double vertStr, double horizStr)
    {
        // Define h and k for this function
        super.stretch(vertStr, horizStr);

        // String-based transformation construction
        transform();
    }

    /**
     * Set the parameters
     * @param horiz -- horizontal translation:    f((x - h))
     * @param vert -- vertical translation:       f(x) + k
     */
    @Override
    public void translate(double horiz, double vert)
    {
        // Define h and k for this function
        super.translate(horiz, vert);

        // String-based transformation construction
        transform();
    }

    /**
     * Reflect this function over the x-axis: y = -f(x)
     */
    @Override
    public void reflectOverX() { super.reflectOverX(); transform(); }

    /**
     * Reflect this function over the y-axis: y = f(-x)
     */
    @Override
    public void reflectOverY() { super.reflectOverY(); transform(); }

    /**
     * Reflect this function over the x-axis and y-axis: y = -f(-x)
     */
    @Override
    public void reflectOverXandY() { super.reflectOverXandY(); transform(); }

    /**
     * @param x -- an x-value
     * @return f(x) for this function
     */
    @Override
    public ComplexNumber evaluateAtPoint(double x)
    {
        return LocalMathematicaCasInterface.getInstance().evaluateAtPoint(getFunction(), x);
    }

    /**
     * @param y
     * @return x = f(y)  OR y = f^{-1}(x) assuming we have a 1-1 piece of a function
     */
    public ComplexNumber evaluateAtPointByY(double y)
    {
        StringBasedFunction inverse = (StringBasedFunction)inverse();
        
        return LocalMathematicaCasInterface.getInstance().evaluateAtPoint(inverse.getFunction(), y);
    }
    
    /**
     * @return the inverse of this function (computed using Mathematica)
     */
    @Override
    public Bound inverse()
    {
        if (_inverse == null) _inverse = Inverses.getInstance().computeInverse(this);
        
        return _inverse;
    }
    
    /**
     * @param function -- a String-based function
     * @return whether the function contains an imaginary component: I in Mathematica refers to \sqrt{-1}
     */
    public boolean hasImaginaryComponent()
    {
        return getFunction().indexOf('I') != -1;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;

        if (!(obj instanceof StringBasedFunction)) return false;

        StringBasedFunction that = (StringBasedFunction)obj;

        //        if (this._fType != that._fType) return false;

        if (!Utilities.equalDoubles(this.a, that.a)) return false;

        if (!Utilities.equalDoubles(this.b, that.b)) return false;

        if (!Utilities.equalDoubles(this.h, that.h)) return false;

        if (!Utilities.equalDoubles(this.k, that.k)) return false;

        if (this._baseFunction != that._baseFunction) return false;

        //        if (!this._domain.equals(that._domain)) return false;

        return true;
    }

    /**
     * @return String-based representation of this function using ALL valuesof a, b, h, k
     */
    @Override
    public String toFullMathematicaString() { return getFunction(); }

    /**
     * @return a string-based representation with identity-based information omitted (1 with multiplication and 0 with addition)
     */
    @Override
    public String toCompactLatexString() { return getFunction(); }

    /**
     * @return the function and domain for debugging purposes
     */
    @Override
    public String toString()
    {
        return toFullMathematicaString() + _domain.toString();
    }
}

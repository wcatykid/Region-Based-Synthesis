package representation.bounds.functions;

import java.io.Serializable;

import representation.ComplexNumber;
import representation.Point;
import representation.bounds.Bound;
import utilities.Utilities;

public class BoundedFunction extends Bound implements Serializable 
{
    private static final long serialVersionUID = 2883462033822784426L;

    //
    // g(x) = a f( b ( x - h )) + k
    //
    protected double a;
    protected double b;
    protected double h;
    protected double k;

    public double getA() { return a; }
    public double getB() { return b; }
    public double getH() { return h; }
    public double getK() { return k; }

    //
    // The function type
    //
    protected FunctionT _fType;
    public FunctionT functionType() { return _fType; }

    // Variable: x or y
    protected VariableT _variable;
    public VariableT variableType() { return _variable; }
    

    /**
     * @param fType -- The type of this bounded function
     * Creates an unbounded representation of a function (domain is unbounded: (-\inf, \inf) )
     */
    public BoundedFunction(FunctionT fType)
    {
        super(Bound.BoundT.FUNCTION);

        this.a = 1;
        this.b = 1;
        this.h = 0;
        this.k = 0;
        this._fType = fType;
        this._variable = VariableT.X;

        this.setDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * To satisfy inheritance from the Bound class
     */
    public BoundedFunction clone() { return new BoundedFunction(this); }

    @Override
    public boolean isHorizontal() { return _fType == FunctionT.HORIZONTAL_LINE; }
    
    /**
     * @param that -- a BoundedFunction object
     * This method facilitates cloning.
     */
    public BoundedFunction(BoundedFunction that)
    {
        super(Bound.BoundT.FUNCTION);

        this.a = that.a;
        this.b = that.b;
        this.h = that.h;
        this.k = that.k;
        this._fType = that._fType;
        this._variable = that._variable;

        this.setDomain(that.getDomain());
    }

    /**
     * Set the parameters
     * @param vertStr -- vertical stretch:    a * f(x)
     * @param horizStr -- horizontal stretch: f(b * x)
     */
    public void stretch(double vertStr, double horizStr)
    {
        this.a = vertStr;
        this.b = horizStr;
    }

    /**
     * Set the parameters
     * @param horiz -- horizontal translation:    f((x - h))
     * @param vert -- vertical translation:       f(x) + k
     */
    public void translate(double horiz, double vert)
    {
        this.h = horiz;
        this.k = vert;
    }

    /**
     * Reflect this function over the x-axis: y = -f(x)
     */
    public void reflectOverX() { this.a *= -1; }

    /**
     * Reflect this function over the y-axis: y = f(-x)
     */
    public void reflectOverY() { this.b *= -1; }

    /**
     * Reflect this function over the x-axis and y-axis: y = -f(-x)
     */
    public void reflectOverXandY()
    {
        reflectOverX();
        reflectOverY();
    }

    /**
     * Translate this function by a given origin point.
     * @param pt -- translation:       f(x-h) + k
     */
    public void translate(Point pt) { translate(pt.getX(), pt.getY()); }

    /**
     * @param pt -- a point (X, Y)
     * @return whether this point is in the domain of this function (checking only the X component)
     */
    public boolean withinBounds(Point pt)
    {
        return _domain.withinBounds(pt.getX());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;

        if (!(obj instanceof BoundedFunction)) return false;

        BoundedFunction that = (BoundedFunction)obj;

        if (this._fType != that._fType) return false;

        if (!Utilities.equalDoubles(this.a, that.a)) return false;

        if (!Utilities.equalDoubles(this.b, that.b)) return false;

        if (!Utilities.equalDoubles(this.h, that.h)) return false;

        if (!Utilities.equalDoubles(this.k, that.k)) return false;

        if (!this._domain.equals(that._domain)) return false;

        return true;
    }

    public boolean notEquals(Object obj) { return !this.equals(obj); }

    /**
     * @return String-based representation of this function using ALL valuesof a, b, h, k
     */
    @Override
    public String toFullMathematicaString() { return _fType.toFullMathematicaString(a, b, h, k, _variable); }

    /**
     * @return a string-based representation with identity-based information omitted (1 with multiplication and 0 with addition)
     */
    @Override
    public String toCompactLatexString() { return _fType.toCompactLatexString(a, b, h, k, _variable); }

    /**
     * @return the function and domain for debugging purposes
     */
    @Override
    public String toString()
    {
        return toFullMathematicaString() + _domain.toString();
    }

    public ComplexNumber evaluateAtPoint(double x)
    {
    	return new ComplexNumber(_fType.evaluate(a, b, h, k, x));
    }

    @Override
    public Bound inverse()
    {
        throw new RuntimeException( "BoundedFunction.inverse is not implemented yet!" ) ;
    }

    @Override
    public ComplexNumber evaluateAtPointByY(double y)
    {
        throw new RuntimeException( "BoundedFunction.evaluateAtPointByY is not implemented yet!" ) ;
    }
}

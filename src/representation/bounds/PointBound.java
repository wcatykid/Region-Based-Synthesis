package representation.bounds;

import java.io.Serializable;

import representation.ComplexNumber;
import representation.Point;

public class PointBound extends Bound implements Serializable
{
	private static final long serialVersionUID = 3190276295795816736L;
	protected Point _point;

	public PointBound()
	{
		super(Bound.BoundT.POINT);
		_point = null;
	}

    public PointBound(Point pt)
    {
    	super(Bound.BoundT.POINT);
    	_point = pt;
    }

    public PointBound(PointBound that)
    {
    	super(Bound.BoundT.POINT);
        _point = that._point;
    }

    @Override
    public Bound clone() { return new PointBound(this._point); }

    //
    // Evaluating f(x)
    //
    public ComplexNumber evaluateAtPoint(double x)
    {
        return new ComplexNumber(this._point.getY());
    }
    
    @Override public boolean isPoint() { return true; }
    @Override public Point getMinimum() { return _point; }
    @Override public Point getMaximum() { return _point; }
    
    /**
     *         * (x, y) ----> (y, x)
     */
    @Override
    public Bound inverse()
    {
        return new PointBound(new Point(_point.getY(), _point.getX()));
    }
    
    public String toString()
    {
        return "PointBound(" + this._point.toString() + ")";
    }

    public boolean equals(Object obj)
    {
    	if (obj == null) return false;
    	
    	if (!(obj instanceof PointBound)) return false;

    	PointBound that = (PointBound)obj;

    	return this._point.equals(that._point);
    }

    public boolean notEquals(Object obj)
    {
	    return !equals(obj);
    }

//  public Point getLeftTopPoint() {return _point; }
//  public Point getLeftBottomPoint() {return _point; }

    @Override
    public String toFullMathematicaString()
    {
        throw new RuntimeException( "PointBound.toFullMathematicaString is not implemented yet!" ) ;
    }

    @Override
    public String toCompactLatexString()
    {
        throw new RuntimeException( "PointBound.toCompactLatexString is not implemented yet!" ) ;
    }

    @Override
    public ComplexNumber evaluateAtPointByY(double y)
    {
        throw new RuntimeException( "PointBound.evaluateAtPointByY is not implemented yet!" ) ;
    }

}

package representation.bounds;

import java.io.Serializable;

import exceptions.RepresentationException;
import representation.Point;
import representation.bounds.segments.HorizontalLineSegment;

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

    public Object clone() { return new PointBound(this._point); }

    //
    // Evaluating f(x)
    //
    public double evaluateAtPoint(double x) { return this._point.getY(); }
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

    @Override
    public String toFullMathematicaString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toCompactLatexString()
    {
        // TODO Auto-generated method stub
        return null;
    }

//    public Point getLeftTopPoint() {return _point; }
//    public Point getLeftBottomPoint() {return _point; }
}

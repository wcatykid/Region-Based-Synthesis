package representation.bounds.segments;

import java.io.Serializable;

import exceptions.RepresentationException;
import representation.bounds.Bound;
import representation.bounds.functions.BoundedFunction;
import representation.bounds.functions.FunctionT;
import utilities.Utilities;
import representation.Point;

public class HorizontalLineSegment extends BoundedFunction implements Serializable
{
    private static final long serialVersionUID = 1L;

    protected Point _left;
    protected Point _right;
    
    public HorizontalLineSegment(Point p1, Point p2) throws RepresentationException
    {
        super(FunctionT.HORIZONTAL_LINE);
        
        assignPoints(p1, p2);
        
        if (!isHorizontal())
        {
            throw new RepresentationException("Horizontal Segment is not horizontal");
        }
        
        // This horizontal line is technically a vertical shift
        k = _left.getY();
    }

    /**
     * @param p1 -- a point
     * @param p2 -- a point
     * Construct this object by ordering the points lexicographically
     */
    private void assignPoints(Point p1,  Point p2)
    {
        if (p1.lessThanOrEqual(p2))
        {
            _left = p1;
            _right = p2;
        }
        else
        {
            _left = p2;
            _right = p1;
        }
    }
    
    /**
     * @return Whether the x-values align indicates horizontal
     */
    public boolean isHorizontal()
    {
        return Utilities.equalDoubles(_left.getY(), _right.getY());
    }

    //
    // Evaluating f(x)
    //
    public double slope() { return 0; }

    //
    // Evaluating y = f(x)
    //
    public double evaluateAtpoint(double x)
    {
        return _left.getY();
    }
    
    /**
     *     * (y, x2)
     *     |
     *     |        <<<-------      *-----------*
     *     |                     (x1,y)      (x2,y)
     *     * (y, x1)
     */
    @Override
    public Bound inverse()
    {
        Point newBottom = new Point(_left.getY(), _left.getX());
        Point newTop = new Point(_left.getY(), _right.getX());
        
        // Not possible to throw an exception here, but catch it.
        try { return new VerticalLineSegment(newBottom, newTop); }
        catch (RepresentationException e) { e.printStackTrace(); }
        
        return null;
    }
    
    @Override
    public BoundedFunction clone()
    {
        HorizontalLineSegment line = null;

        try
        {
            line = new HorizontalLineSegment(_left, _right);
        }
        catch (RepresentationException re)
        {
            System.err.println("Unexpected exception with horizontals");
        }

        return line;
    }
    
    public boolean equals(Object obj) 
    {
        if (obj == null) return false;
        
        if (!(obj instanceof HorizontalLineSegment)) return false;

        return super.equals(obj);
    }

    public boolean notEquals(Object that) 
    {
        return !equals(that);
    }

    @Override
    public String toString() 
    {
        return "HorizontalSegment(" + _left + ", " + _right + ")";
    }
}

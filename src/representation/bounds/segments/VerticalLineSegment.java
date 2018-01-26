package representation.bounds.segments;

import java.io.Serializable;

import exceptions.RepresentationException;
import representation.bounds.Bound;
import representation.ComplexNumber;
import representation.Point;

public class VerticalLineSegment extends LineSegment implements Serializable
{
    private static final long serialVersionUID = 1L;

    public VerticalLineSegment() { super(); }

    public VerticalLineSegment(Point p1, Point p2) throws RepresentationException
    {
        super(p1, p2, Bound.BoundT.VERTICAL_LINE);

        if (!isVertical())
        {
            throw new RepresentationException("Vertical Segment is not vertical");
        }
    }

    public VerticalLineSegment(LineSegment that) throws RepresentationException
    {
        super(that, Bound.BoundT.VERTICAL_LINE);

        if (!isVertical())
        {
            throw new RepresentationException("Vertical Segment cannot be constructed from non-vertical");
        }
    }

    //
    // Evaluating f(x) = y
    //
    @Override public ComplexNumber evaluateAtPoint(double x)
    {
        return new ComplexNumber(Double.NEGATIVE_INFINITY);
    }

    @Override public boolean isVertical() { return true; }
    @Override public Point getMinimum() { return _bottomPt; }
    @Override public Point getMaximum() { return _topPt; }

    //
    // Evaluating x = f(y)
    //
    public ComplexNumber evaluateAtPointByY(double x)
    {
        return new ComplexNumber(_bottomPt.getX());
    }

    /**
     * @param y1 -- y-valued double
     * @param y2 -- y-valued double
     * @return whether the stated y-values are within the range of this bound.
     */
    public boolean inRange(double y1, double y2)
    {
        // Acquire the range values
        double lowRangeY = _bottomPt.getY();
        double upperRangeY = _topPt.getY();

        // Check whether the given interval is within the range interval
        return inRange(y1, y2, lowRangeY, upperRangeY);
    }

    /**
     *     * (x, y2)
     *     |
     *     |        ------->      *-----------*
     *     |                   (y1,x)      (y2,x)
     *     * (x, y1)
     */
    @Override
    public Bound inverse()
    {
        Point newLeft = new Point(_bottomPt.getY(), _bottomPt.getX());
        Point newRight = new Point(_topPt.getY(), _bottomPt.getX());

        // Not possible to throw an exception here, but catch it.
        try { return new HorizontalLineSegment(newLeft, newRight); }
        catch (RepresentationException e) { e.printStackTrace(); }

        return null;
    }

    @Override
    public Object clone()
    {
        VerticalLineSegment line = null;

        try
        {
            line = new VerticalLineSegment(this._bottomPt, this._topPt);
        }
        catch (RepresentationException re)
        {
            System.err.println("Unexpected exception with verticals");
        }

        return line;
    }

    public boolean equals(Object obj) 
    {
        if (obj == null) return false;

        if (!(obj instanceof VerticalLineSegment)) return false;

        return super.equals(obj);
    }

    public boolean notEquals(Object that) 
    {
        return !equals(that);
    }

    @Override
    public String toString() 
    {
        return "VerticalSegment(" + this._bottomPt + ", " + this._topPt + ")";
    }
}

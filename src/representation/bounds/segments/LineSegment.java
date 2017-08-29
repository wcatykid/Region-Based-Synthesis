package representation.bounds.segments;
import java.io.Serializable;

import exceptions.DomainException;
import exceptions.RepresentationException;
import representation.bounds.Bound;
import representation.Point;

public class LineSegment extends Bound implements Serializable
{
    private static final long serialVersionUID = 7752196716541235809L;

    protected Point _bottomPt;
    protected Point _topPt;


    public Point getLeftTopPoint() {return _topPt; }
    public Point getLeftBottomPoint() {return _bottomPt; }

    public LineSegment() { super(Bound.BoundT.VERTICAL_LINE); }
    public LineSegment(Point e1,  Point e2)
    {
        super(Bound.BoundT.VERTICAL_LINE);

        assignPoints(e1, e2);
    }

    public LineSegment(Point e1,  Point e2, Bound.BoundT type) throws RepresentationException
    {
        super(type);

        if (e1.equals(e2)) throw new RepresentationException("Line requires distinct points: " + e1 + " " + e2);

        assignPoints(e1, e2);
    }

    @Override
    public Bound inverse()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * @param pt1 -- a point
     * @param pt2 -- a point
     * @return whether these point are the same as the endpoints of this bound
     * @throws DomainException -- if the domain has not been specified for this bound
     */
    @Override
    public boolean definedBy(Point pt1, Point pt2) throws DomainException
    {
        return _bottomPt.equals(pt1) && _topPt.equals(pt1) || _bottomPt.equals(pt2) && _topPt.equals(pt2);
    }
    
    //
    // Lexicographic ordering
    //
    private void assignPoints(Point p1,  Point p2)
    {
        if (p1.lessThanOrEqual(p2))
        {
            _bottomPt = p1;
            _topPt = p2;
        }
        else
        {
            _bottomPt = p2;
            _topPt = p1;
        }
    }

    public LineSegment(LineSegment that)
    {
        super(Bound.BoundT.LINEAR);

        assignPoints(that._bottomPt, that._topPt);
    }

    public LineSegment(LineSegment that, Bound.BoundT type)
    {
        super(type);

        assignPoints(that._bottomPt, that._topPt);
    }

    //
    // Evaluating f(x)
    //
    // y - y0 = m (x - x0)  ==>  y = m (x - x0) + y0
    //
    public double evaluateAtPoint(double x)
    {
        return slope() * (x - _bottomPt.getX()) + _bottomPt.getY();
    }

    //
    // Evaluating f(x)
    //
    public double slope()
    {
        return (_bottomPt.getY() - _topPt.getY()) / (_bottomPt.getX() - _topPt.getX());
    }

    public Object clone()
    {
        LineSegment line = null;

        try
        {
            if (this.isVertical()) line = new VerticalLineSegment(this._bottomPt, this._topPt);
            //else if (this.isHorizontal()) line = new HorizontalLineSegment(this.bottomPt, this.topPt);
            else line = new LineSegment(this._bottomPt, this._topPt);
        }
        catch (RepresentationException re)
        {
            System.err.println("Unexpected exception with horizontals");
        }

        return line;
    }

    public String toString() 
    {
        return "Segment(" + this._bottomPt + ", " + this._topPt + ")";
    }

    public boolean isHorizontal()
    {
        return utilities.Utilities.equalDoubles(_bottomPt.getY(), _topPt.getY());
    }

    public boolean isVertical()
    {
        return utilities.Utilities.equalDoubles(_bottomPt.getX(), _topPt.getX());
    }

    public boolean equals(Object obj) 
    {
        if (obj == null) return false;

        if (!(obj instanceof LineSegment)) return false;

        LineSegment that = (LineSegment)obj;

        if (this._bottomPt == that._bottomPt && this._topPt == that._topPt) return true;

        if (this._bottomPt == that._topPt && this._topPt == that._bottomPt) return true;

        return false;
    }

    public boolean notEquals(Object that) 
    {
        return !equals(that);
    }

    @Override
    public String toFullMathematicaString()
    {
        return slope() + "(x - " + _bottomPt.getX() + ")" + _bottomPt.getY();
    }
    @Override
    public String toCompactLatexString()
    {
        return toFullMathematicaString();
    }
}

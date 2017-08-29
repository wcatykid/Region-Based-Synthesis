package representation.regions;
import java.io.Serializable;

import exceptions.DomainException;
import representation.bounds.Bound;
import utilities.Utilities;
import representation.Point;

public class LeftRight extends RegionBound implements Serializable
{
	private static final long serialVersionUID = -1748487694703940271L;

	protected Bound _bound;
    public Bound getBound() { return _bound; }
	
	public LeftRight() { super(); }
    
    public LeftRight(Bound b)
    {
    	this._bound = b;
    }

    public LeftRight(LeftRight that)
    {
    	this._bound = (Bound)that._bound.clone();
    }

    public boolean isPoint() { return _bound.isPoint(); }
    public boolean isVertical() { return _bound.isVertical(); }
    public Point getMaximum() { return _bound.getMaximum(); }
    public Point getMinimum() { return _bound.getMinimum(); }
    
    /**
     * @param y1 -- a y-coordinate
     * @param y2 -- a y-coordinate
     * @return whether the left / right ends support given range [y1, y2]; we are not interested in points
     */
    public boolean inRange(double y1, double y2)
    {
        if (isPoint()) return false;
        
        if (isVertical())
        {
            return _bound.inRange(y1, y2);
        }

        return false;
    }

    /**
     * @param pt1 -- a point
     * @param pt2 -- a point
     * @return whether these point are the same as the endpoints of this bound
     * @throws DomainException -- if the domain has not been specified for this bound
     */
    public boolean definedBy(Point pt1, Point pt2) throws DomainException
    {
        return _bound.definedBy(pt1, pt2);
    }
    
    /**
     * The left / right becomes a top / bottom
     */
    @Override
    public RegionBound invert()
    {
        return new TopBottom(_bound.inverse());
    }
    
    public boolean equals(Object obj)
    {
    	if (obj == null) return false;
    	
    	if (!(obj instanceof LeftRight)) return false;

    	LeftRight that = (LeftRight)obj;
    	
        return this._bound.equals(that._bound);
    }

    public boolean notEquals(Object obj)
    {
        return !this.equals(obj);
    }
    
    public String toString()
    {
    	return (_bound != null) ? this._bound.toString() : "null";
    }
    
    @Override
    public Object clone() { return new LeftRight(_bound); }
}

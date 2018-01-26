package representation.regions;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import exceptions.DomainException;
import math.analysis.extrema.ExtremeValues;
import representation.bounds.Bound;
import representation.bounds.segments.VerticalLineSegment;
import representation.Point;

public class TopBottom extends RegionBound implements Serializable
{
    private static final long serialVersionUID = 3742451116366675419L;
    protected Vector<Bound> _bounds;
    public Vector<Bound> getBounds() { return _bounds; }

    public TopBottom()
    {
        _bounds = new Vector<Bound>();
    }

    /**
     * @param b -- a Bound
     * Construct a top/bottom bound with a single function
     */
    public TopBottom(Bound b)
    {
        this();

        _bounds.add(b);
    }

    /**
     * @param b -- a set of bounds Bound
     * Construct a top/bottom bound with that set of bounds
     */
    public TopBottom(TopBottom that)
    {
        this();

        for (Bound bound : that._bounds)
        {
            this._bounds.addElement((Bound)bound.clone());
        }
    }

    /**
     * @param x -- x-value
     * @return y-value such that y = f(x) based on the proper bound
     * @throws DomainException if the input x is not in the domain of this TopBottom bound
     */
    public double evaluateAtX(double x) throws DomainException
    {
        // Acquire the bound; evaluate
        Bound bound = getBound(x);

        return bound.evaluateAtPoint(x).getReal();
    }

    /**
     * @param x -- x-value
     * @return the first bound for which the given x is in the domain
     * @throws DomainException if the input x is not in the domain of this TopBottom bound
     *
     * It is possible a single x-value may lie in two domains; since that meeting point is equal among bounds,
     * we can safely return the first bound.
     */
    public Bound getBound(double x) throws DomainException
    {
        for (Bound bound : _bounds)
        {
            if (bound.inDomain(x)) return bound;
        }

        //        return null;
        throw new DomainException("Given x-value (" + x + ") not in the domain");
    }

    public int numberOfBounds()
    {
        return _bounds.size() ;
    }
    
    /**
     * @param x -- x-value
     * @return the first bound for which the given x is in the domain (that is not vertical)
     * @throws DomainException if the input x is not in the domain of this TopBottom bound
     *
     * It is possible a single x-value may lie in two domains; since that meeting point is equal among bounds,
     * we can safely return the first bound.
     */
    public Bound getFirstNonVerticalBoundExclusiveOfRightEndpoint(double x) throws DomainException
    {
        for (Bound bound : _bounds)
        {
            if (!(bound instanceof VerticalLineSegment))
            {
                if (bound.inDomain(x) && !bound.isRightEndpoint(x)) return bound;
            }
        }

        //        return null;
        throw new DomainException("Given x-value (" + x + ") not in the domain");
    }

    public Bound lastBound()
    {
        return _bounds.lastElement();
    }

    //    public int numberOfBounds() { return _bounds.size(); }

    public TopBottom clone() { return null; }

    /**
     * @param bound -- a bounded function or some general bound with a specific domain
     * @return true / false if addition of the bound was accepted: appending domains works.
     */
    public boolean addBound(Bound bound)
    {
        //
        // We are not allowed to add two horizontal segments in a row
        //
        if (bound.getBoundT() ==  Bound.BoundT.HORIZONTAL_LINE)
        {
            if (!_bounds.isEmpty())
            {
                if (this.lastBound().getBoundT() == Bound.BoundT.HORIZONTAL_LINE) return false;
            }
        }

        // Verify the bound aligns with the current set of bounds and, if it aligns, add to the set of bounds
        if (!verifyNewBound(bound)) return false;

        _bounds.add(bound);

        return true;
    }

    /**
     * @param bound -- a generic bound with a known domain
     * @return true / false whether
     *      (1) empty?
     *      (2) there is infinite overlap of this domain with any other domain in the existing bound
     *      (3) left endpoint matches with right endpoint of the last bound
     */
    private boolean verifyNewBound(Bound bound)
    {
        // (1) If there are no bounds in this list, the new bound is fine.
        if (_bounds.isEmpty()) return true;

        // (2) there is infinite overlap of this domain with any other domain in the existing bound
        for (int b = 0; b < _bounds.size() - 1; b++)
        {
            if (_bounds.get(b).domainFiniteOverlap(bound)) return false;
        }

        //
        // (3) left endpoint matches with right endpoint of the last bound
        //
        Bound last = lastBound();

        if (!last.domainFiniteOverlap(bound)) return false;

        return last.leftEndpointAlignsWithRightEndpointofThis(bound);
    }

    //
    // Accumulate the set of all boundary points for this top / bottom bound-set
    //
    public Vector<Point> acquirePointBoundSet()
    {
        Vector<Point> points = new Vector<Point>();

        //
        // Add far-left x as fencepost
        //
        points.add(acquireLeftBoundPoint());

        //
        // Add all remaining right bounds as rest of the fenceposts
        //
        for (Bound bound : _bounds)
        {
            double x = bound.rightBoundX();
            double fx = bound.evaluateAtPoint(x).getReal();

            points.add(new Point(x, fx));
        }

        return points;
    }

    /**
     * @return the left-most x-value of this bound
     */
    public double leftX()
    {
        return _bounds.get(0).leftBoundX();
    }

    /**
     * @return the right-most x-value of this bound
     */
    public double rightX()
    {
        return lastBound().rightBoundX();
    }

    //
    // Accumulate the set of all boundary x-values (not points) for this top / bottom bound-set
    //
    public Vector<Double> acquireBoundSet()
    {
        Vector<Double> xs = new Vector<Double>();

        xs.add(leftX());

        for (Bound bound : _bounds)
        {
            xs.add(bound.rightBoundX());
        }

        return xs;
    }

    //
    // Far-left bound only
    //
    public Point acquireLeftBoundPoint()
    {
        double x = _bounds.get(0).leftBoundX();
        double fx = _bounds.get(0).evaluateAtPoint(x).getReal();

        return new Point(x, fx);
    }

    //
    // Far-right bound only
    //
    public Point acquireRightBoundPoint()
    {
        double x = _bounds.get(_bounds.size() - 1).rightBoundX();
        double fx = _bounds.get(_bounds.size() - 1).evaluateAtPoint(x).getReal();

        return new Point(x, fx);
    }

    /**
     * @return the ordered set of x-values for which this set of bounds has extreme points (excluding endpoints)
     */
    public Vector<Double> extremaByX()
    {
        //
        // Collect all the points
        //
        Set<Double> xs = new HashSet<Double>();
        for (Bound bound : _bounds)
        {
            xs.addAll(ExtremeValues.getInstance().extrema(bound));
        }

        // Sort and return
        Vector<Double> ordered = new Vector<Double>(xs);
        Collections.sort(ordered);

        return ordered;
    }

    /**
     * The top / bottom becomes left / right 
     */
    @Override
    public RegionBound invert()
    {
        return null;
    }

    //    //
    //    //
    //    //
    //    // Instantiation
    //    //
    //    //
    //    //
    //    // Return the set of all valid functions that can be added to this set of bounds; use the restriction to determine allowable
    //    //
    //    protected Vector<Bound> constructBound(template.TemplateRestriction restrictions)
    //    {
    //        Vector<Bound> bounds = new Vector<Bound>();
    //
    //        //
    //        // Find all possible bound-types based on the restriction
    //        //   (and generate the actual bound function)
    //        //
    //        ArrayList<Bound.BoundT> types = restrictions.getAllowedBoundTypes();
    //        for (Bound.BoundT type : types)
    //        {
    //            Bound bound = null;
    //            try
    //            {
    //                bound = Bound.generate(type);
    //                bounds.add(bound);
    //            }
    //            catch (RepresentationException re)
    //            {
    //                System.err.println("Unexpected representation exception when constructing a bound");
    //            }
    //        }
    //        
    //        
    //        return bounds;
    //    }



    //
    //
    //
    //
    // Utilities
    //
    //
    //
    int length() { return _bounds.size(); }

    public boolean equals(Object obj)
    {
        if (obj == null) return false;

        if (!(obj instanceof TopBottom)) return false;

        TopBottom that = (TopBottom)obj;

        if (this.length() != that.length()) return false;

        for (int b = 0; b < _bounds.size(); b++)
        {
            // For true comparison, we should check that the domains are the same            

            if (!this._bounds.elementAt(b).equals(that._bounds.elementAt(b))) return false;
        }

        return true;
    }

    public boolean notEquals(Object obj)
    {
        return !this.equals(obj);
    }

    public String toString()
    {
        String s = "";
        s += "{ ";

        int c = 0;
        while (c < this._bounds.size())
        {
            int dupCount = 1;
            while (c + dupCount < this._bounds.size() && this._bounds.elementAt(c) == this._bounds.elementAt(c + dupCount) )
            {
                dupCount++;
            }
            s += this._bounds.elementAt(c).toString();
            if (dupCount > 1) s += dupCount;
            s += " ";

            c += dupCount;
        }

        return s + "}";
    }
}
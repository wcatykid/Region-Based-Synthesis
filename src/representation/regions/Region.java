package representation.regions;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import engine.region.Verifier;
import exceptions.DomainException;
import math.analysis.extrema.ExtremeValues;
import representation.Point;
import representation.bounds.Bound;
import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import utilities.Pair;
import utilities.Utilities;

public class Region implements Serializable
{
    protected static final long serialVersionUID = -6801870320013466345L;

    //
    // Left, Top, Right, Bottom Bounds
    //
    protected LeftRight _left;
    public LeftRight getLeft() { return _left; }

    protected TopBottom _top;
    public TopBottom getTop() { return _top; }

    protected LeftRight _right;
    public LeftRight getRight() { return _right; }

    protected TopBottom _bottom;
    public TopBottom getBottom() { return _bottom; }

    // Left / right extreme x-values
    public double leftX() { return _bottom.leftX(); }
    public double rightX() { return _bottom.rightX(); }

    // Number of 
    public int bottomLength() { return _bottom.length(); }
    public int topLength() { return _top.length(); }

    public void setRight(LeftRight right) { _right = right; }

    public Region()
    {
        this(new LeftRight(), new TopBottom(), new LeftRight(), new TopBottom());
    }

    public Region(Region that)
    {
        this(that._left, that._top, that._right, that._bottom);
    }

    /**
     * @param ell -- left bound
     * @param t -- top bound
     * @param r -- right bound
     * @param b -- bottom bound
     */
    public Region(LeftRight ell, TopBottom t, LeftRight r, TopBottom b)
    {
        super();

        _left = ell;
        _top = t;
        _right = r;
        _bottom = b;
    }

    /**
     * @return true / false whether the region is, in fact, a closed region
     */
    public boolean verify()
    {
        return Verifier.getInstance().verify(this);
    }

    /**
     * @param x -- x-value
     * @return the first bound for which the given x is in the domain
     * @throws DomainException if the input x is not in the domain of this TopBottom bound
     *
     * It is possible a single x-value may lie in two domains; since that meeting point is equal among bounds,
     * we can safely return the first bound.
     */
    public Bound getTopBound(double x) throws DomainException
    {
        return _top.getBound(x);
    }
    public Bound getBottomBound(double x) throws DomainException
    {
        return _bottom.getBound(x);
    }

    /**
     * @param x -- x-value
     * @return the first bound for which the given x is in the domain (that is not vertical)
     * @throws DomainException if the input x is not in the domain of this TopBottom bound
     *
     * It is possible a single x-value may lie in two domains; since that meeting point is equal among bounds,
     * we can safely return the first bound.
     */
    public Bound getFirstNonVerticalTopBound(double x) throws DomainException
    {
        return _top.getFirstNonVerticalBoundExclusiveOfRightEndpoint(x);
    }
    public Bound getFirstNonVerticalBottomBound(double x) throws DomainException
    {
        return _bottom.getFirstNonVerticalBoundExclusiveOfRightEndpoint(x);
    }

    //
    //
    // Standard Utilities
    //
    //
    public int hashCode()
    {
        return _left.hashCode() + _right.hashCode() + _top.hashCode() + _bottom.hashCode();
    }

    public boolean equals(Object obj)
    {
        // Check reference first before internals
        if (this == obj) return true;

        if (obj == null) return false;

        if (!(obj instanceof Region)) return false;

        Region that = (Region)obj;

        if (!this._left.equals(that._left)) return false;

        if (!this._right.equals(that._right)) return false;

        if (!this._top.equals(that._top)) return false;

        if (!this._bottom.equals(that._bottom)) return false;

        return true;
    }

    public Region clone()
    {
        Region obj = null;

        try
        {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            obj = (Region) in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }

    public String toString()
    {
        String s = "";

        s += "Left: " + this._left + "\n";
        s += "Top: " + this._top + "\n";
        s += "Right: " + this._right + "\n";
        s += "Bottom: " + this._bottom + "\n\n";

        return s;
    }

    /**
     * @param functions -- a set of String-based functions
     * @return whether the given functions exactly defines this region;
     *     ***This does not account for verticals as left / right bounds
     */
    public boolean uniquelyDefinedBy(StringBasedFunction[] functions)
    {
        if (containsVertical()) return false;

        //
        // Implemented as a subset verification mechanism
        //
        Vector<Bound> bounds = collectBounds();

        if (bounds.size() != functions.length) return false;

        for (StringBasedFunction strFunc : functions)
        {
            boolean found = false;

            for (int b = 0; b < bounds.size() && !found; b++)
            {
                if (bounds.get(b).equals(strFunc)) found = true;
            }

            if (!found) return false;
        }

        return true;
    }

    /**
     * @return returns all bounds used in this region (top / bottom only)
     */
    protected Vector<Bound> collectBounds()
    {
        Vector<Bound> bounds = new Vector<Bound>();

        bounds.addAll(_top.getBounds());
        bounds.addAll(_bottom.getBounds());

        return bounds;
    }

    /**
     * @return if the top / bottom bound contains a vertical segment
     */
    protected boolean containsVertical()
    {
        for (Bound bound : collectBounds())
        {
            if (bound.isVertical()) return true;
        }

        return false;
    }

    /**
     * @param domain -- valid domain object
     * @return whether this region is in the given domain parameter
     */
    public boolean inDomain(Domain domain)
    {
        return domain.contains(new Domain(this.leftX(), this.rightX()));
    }

    /**
     * @return the set of x-values for top and bottom bounds (excluding endpoints)
     */
    public Set<Double> extremaByX()
    {
        // Collect all extrema
        Set<Double> xs = new HashSet<Double>();
        xs.addAll(_top.extremaByX());
        xs.addAll(_bottom.extremaByX());

        return xs;
    }

    /**
     * @param region -- a region (with top and bottom)
     * @return a set of xs (since there may be top / bottom duplicates) of x-values indicating the interior points where pieces occur
     *    Indicates where a vertical segment at each x-value where pieces (functions) are combined together
     *          Segmentation is done for both the top and bottom bounds
     */
    public Set<Double> interiorPiecesByX()
    {
        Set<Double> xs = new HashSet<Double>();

        // Find all pieces: bottom + top
        xs.addAll(splitByPieces(_bottom));
        xs.addAll(splitByPieces(_top));

        return xs;
    }

    /**
     * @param bounds -- a top / bottom bound for a region
     * @return a set of x-values that indicate interior piece-wise function (a, b, c in the following): q--------a----b------c-----e
     * In order to 
     */
    private Vector<Double> splitByPieces(TopBottom bounds)
    {
        Vector<Double> xs = new Vector<Double>();

        //
        // Skip the left endpoint of the first bound; collect all other left points
        //
        Vector<Bound> boundList = bounds.getBounds();
        for (int b = 1; b < boundList.size(); b++)
        {
            xs.add(boundList.get(b).leftBoundX());
        }

        return xs;
    }

    /**
     * @return the set of corner points for this region; this really is a set since left (right) might be a point
     */
    public Vector<Point> getCorners()
    {
        Vector<Point> corners = new Vector<Point>();

        if (_left.isPoint()) corners.add(_left.getMaximum());
        else if (_left.isVertical())
        {
            corners.addElement(_left.getMinimum());
            corners.addElement(_left.getMaximum());
        }
        else System.err.println("The left bound is neither a point nor a line.");

        if (_right.isPoint()) corners.add(_right.getMaximum());
        else if (_right.isVertical())
        {
            corners.addElement(_right.getMinimum());
            corners.addElement(_right.getMaximum());
        }
        else System.err.println("The right bound is neither a point nor a line.");

        return corners;
    }

    /**
     * @param bottom -- a point
     * @param top -- a point
     * @return the bound in this region which has endpoints given by the two points;
               the bound may be from any of the 4 parts: top, bottom, left, right
     * @throws DomainException 
     */
    public Bound getBoundDefinedBy(Point bottom, Point top) throws DomainException
    {
        //
        // Check the left and right first
        //
        if (_left.isVertical())
        {
            if (_left.definedBy(bottom, top)) return _left.getBound();
        }
        if (_right.isVertical())
        {
            if (_right.definedBy(bottom, top)) return _left.getBound();
        }

        //
        // Check the bottom and tops
        //
        for (Bound bound : collectBounds())
        {
            if (bound.definedBy(bottom, top)) return bound;
        }

        return null;
    }

//    /**
//     * @param bottom -- point (x1, y1)
//     * @param top  -- point (x2, y2) such that y1 < y2 (points are ordered by Y )
//     * @param other -- a bound for which the bottom / top points are in range
//     * @return
//     */
//    public Bound getBoundInRange(Point bottom, Point top, Bound other)
//    {
//        if (Utilities.greaterThanOrEqualDoubles(bottom.getY(), top.getY()))
//        {
//            System.err.println("Region::getBoundInRange: y valeus inconsistent " + bottom + " " + top);
//        }
//        
//        //
//        // Accumulate the set of bounds that are in the range dictated by the points bottom and top.
//        //
//        Vector<Bound> inRange = new Vector<Bound>();
//        
//        //
//        // Look at the top / bottom
//        //
//        Vector<Bound> candidates = collectBounds();
//        
//        for (Bound candidate : candidates)
//        {
//            if (candidate.inRange(bottom.getY(), top.getY()))
//            {
//                inRange.add(candidate);
//            }
//        }
//        
//        //
//        // Check the left and right if they are verticals
//        //
//        if (_left.inRange(bottom.getY(), top.getY()))
//        {
//            inRange.add(_left.getBound());
//        }
//        if (_right.inRange(bottom.getY(), top.getY()))
//        {
//            inRange.add(_right.getBound());
//        }
//        
//        //
//        // Acquire the proper bound in range
//        //
//        if (inRange.size() != 2)
//        {
//            System.err.println("Region::getBoundInRange: expected to find 2 bounds in range; " + inRange.size() + " found.");
//        }
//        Bound found = null;
//        for (Bound boundInRange : inRange)
//        {
//            if (boundInRange != other)
//            {
//                if (found != null)
//                {
//                    System.err.println("Region::getBoundInRange: found 2 unique bounds in range; " + found + " " + boundInRange);
//                }
//                else found = boundInRange;
//            }
//        }
//        
//        return found;
//    }
    
    /**
     * @param bottom -- point (x1, y1)
     * @param top  -- point (x2, y2) such that y1 < y2 (points are ordered by Y )
     * @param other -- a bound for which the bottom / top points are in range
     * @return
     */
    public Pair<Bound, Bound> getBoundsInRange(Point bottom, Point top)
    {
        if (Utilities.greaterThanOrEqualDoubles(bottom.getY(), top.getY()))
        {
            System.err.println("Region::getBoundInRange: y valeus inconsistent " + bottom + " " + top);
        }
        
        //
        // Accumulate the set of bounds that are in the range dictated by the points bottom and top.
        //
        Vector<Bound> inRange = new Vector<Bound>();
        
        //
        // Look at the top / bottom
        //
        Vector<Bound> candidates = collectBounds();
        
        for (Bound candidate : candidates)
        {
            if (candidate.inRange(bottom.getY(), top.getY()))
            {
                inRange.add(candidate);
            }
        }
        
        //
        // Check the left and right if they are verticals
        //
        if (_left.inRange(bottom.getY(), top.getY()))
        {
            inRange.add(_left.getBound());
        }
        if (_right.inRange(bottom.getY(), top.getY()))
        {
            inRange.add(_right.getBound());
        }
        
        //
        // Acquire the proper bound in range
        //
        if (inRange.size() != 2)
        {
            System.err.println("Region::getBoundInRange: expected to find 2 bounds in range; " + inRange.size() + " found.");
        }
        
        return new Pair<Bound, Bound>(inRange.get(0), inRange.get(1));
    }
}
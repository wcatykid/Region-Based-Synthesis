package representation.bounds.functions;

import utilities.Utilities;

//
// Domain restriction for this function: bounds placed on x \in [leftX, rightX]
//
public class Domain
{
    protected static final String LESS_THAN_OR_EQUAL = "<=";
    protected static final String LESS_THAN = "<";
    protected static final String GREATER_THAN_OR_EQUAL = ">=";
    protected static final String GREATER_THAN = ">";

    private double _left;
    private double _right;

    public Domain()
    {
        _left = Double.NEGATIVE_INFINITY;
        _right = Double.POSITIVE_INFINITY;
    }

    public Domain(Domain that) { this(that._left, that._right); }
    public Domain(double left, double right)
    {
        _left = left;
        _right = right;
    }

    /**
     * @param right -- an x-value
     */
    public void setDomainUpperBound(double right)
    {
        if (right < _left) System.err.println("Problem with new domain upper bound: (" + right + ") < (" + right + ")");

        this._right = right;        
    }

    /**
     * @return true / false whether this domain is a single value
     */
    public boolean isFinite() { return Utilities.equalDoubles(this._left, this._right); }

    /**
     * @return whether this domain has defined endpoints
     */
    public boolean isBounded()
    {
        if (_left == Double.NEGATIVE_INFINITY) return false;
        if (_right == Double.POSITIVE_INFINITY) return false;
        
        return true;
    }
    
    public double getLowerBound() { return _left; }
    public double getUpperBound() { return _right; }

    public boolean withinBounds(double x)
    {
        //
        // CTA: Do we need to check the special case of INFINITY when comparing <, >, etc.???
        //
        return utilities.Utilities.greaterThanOrEqualDoubles(x, _left) &&
                utilities.Utilities.lessThanOrEqualDoubles(x, _right);
    }

    /**
     * @param that -- a domain
     * @return true / false if these domains actually have a non-empty intersection
     */
    public boolean intersects(Domain that)
    {
        return finiteIntersection(that) || infiniteIntersection(that);
    }

    /**
     * @param that -- a domain
     * @return the intersection of (this) domain with (that) domain
     * Those domains are intervals and nothing more complicated; we can use that fact to determine the intersection
     */
    public Domain intersection(Domain that)
    {
        //
        // Check no intersection
        //
        if (!this.intersects(that)) return null;
        
        //
        // Check containment
        //
        if (this.contains(that)) return new Domain(that);

        if (that.contains(this)) return new Domain(this);
        
        //
        // Check overlap
        //
        double left = Double.NEGATIVE_INFINITY;
        double right = Double.NEGATIVE_INFINITY;
        // Given: a------b    and c-----d
        // Intersections may look like: a----c--b-----d
        //
        // Left endpoint
        if (this.withinBounds(that._left)) left = that._left;
        else if (that.withinBounds(this._left)) left = this._left;

        // Right endpoint
        if (this.withinBounds(that._right)) right = that._right;
        else if (that.withinBounds(this._right)) right = this._right;
        
        return new Domain(left, right);
    }
    

    /**
     * @param that -- a domain
     * @return whether the domain overlap ONLY at an endpoint
     */
    public boolean sharesEndpoint(Domain that)
    {
        return sharesEndpoint(that._left) || sharesEndpoint(that._right);
    }

    /**
     * @param x -- a one-dimensional value
     * @return whether x is an endpoint or not
     */
    private boolean sharesEndpoint(double x)
    {
        if (Utilities.equalDoubles(this._left, x)) return true;
        if (Utilities.equalDoubles(this._right, x)) return true;

        return false;
    }
    
    /**
     * @param that -- a domain
     * @return true / false whether the two sets results in a single intersection point
     */
    public boolean finiteIntersection(Domain that)
    {
        // Check the obscure finite case
        if (that.isFinite() && sharesEndpoint(that)) return true;

        // At least one endpoint must be shared
        // *-----this-----*---------that---*
        if (Utilities.equalDoubles(this._right, that._left) && Utilities.between(this._left, this._right, that._right)) return true;

        // At least one endpoint must be shared
        // *-----that-----*---------this---*
        if (Utilities.equalDoubles(that._right, this._left) && Utilities.between(that._left, that._right, this._right)) return true;
        
        return false;
    }

    /**
     * @param that -- a domain
     * @return true / false whether the two sets overlap (not just at endpoints)
     */
    public boolean infiniteIntersection(Domain that)
    {
        // Check finite first
        if (this.isFinite() || that.isFinite()) return false;
        
        // Exact same domain interval
        if (this.equals(that)) return true;
        
        // That inside this
        if (Utilities.betweenExclusive(this._left, that._left, this._right)) return true;
        if (Utilities.betweenExclusive(this._left, that._right, this._right)) return true;

        // This inside that
        if (Utilities.betweenExclusive(that._left, this._left, that._right)) return true;
        if (Utilities.betweenExclusive(that._left, this._right, that._right)) return true;

        return false;
    }

    /**
     * @param that -- a domain
     * @return true / false whether this set contains (exclusively) that set
     */
    public boolean containsExclusive(Domain that)
    {
        return Utilities.betweenExclusive(this._left, that._left, this._right) &&
               Utilities.betweenExclusive(this._left, that._right, this._right);
    }

    /**
     * @param that -- a domain
     * @return true / false whether this set contains (exclusively) that set
     */
    public boolean contains(Domain that)
    {
        return Utilities.between(this._left, that._left, this._right) &&
               Utilities.between(this._left, that._right, this._right);
    }
    
    /**
     * @param that -- a domain
     * @return true / false whether that set contains (completely) this set
     */
    public boolean containedExclusivelyBy(Domain that) { return that.containsExclusive(this); }

    @Override
    public String toString()
    {
        return " { " + _left + " " + LESS_THAN_OR_EQUAL +  " x " + LESS_THAN_OR_EQUAL + " " + _right + "}" ;
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Domain)) return false;

        Domain that = (Domain) o;

        return utilities.Utilities.equalDoubles(_left, that._left) &&
                utilities.Utilities.equalDoubles(_right, that._right);
    }
}
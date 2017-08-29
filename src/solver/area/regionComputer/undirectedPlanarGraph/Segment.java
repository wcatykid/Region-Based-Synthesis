package solver.area.regionComputer.undirectedPlanarGraph;

public class Segment
{
    protected PlanarGraphPoint _point1;
    protected PlanarGraphPoint _point2;
    protected double _length;
    protected double _slope;

    public PlanarGraphPoint getPlanarGraphPoint1() { return _point1; }
    public PlanarGraphPoint getPlanarGraphPoint2() { return _point2; }
    public double length() { return _length; }
    public double slope() { return _slope; }

    public Segment(Segment in) { this(in._point1, in._point2); }
    public Segment(PlanarGraphPoint p1, PlanarGraphPoint p2)
    {
        _point1 = p1;
        _point2 = p2;
        _length = PlanarGraphPoint.calcDistance(p1, p2);
        _slope = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        //_slope = getSlope(); Removed on 11/2/2016 by Nick. Dealing with vertical slopes using Infinity instead of null. 

        //        backend.utilities.list.Utilities.addUniqueStructurally(_point1.getSuperFigures(), this);
        //        backend.utilities.list.Utilities.addUniqueStructurally(_point2.getSuperFigures(), this);


    }

//    /*
//     * Do these lines cross (arbitrarily)?
//     */
//    public boolean intersects(Segment that) { return SegmentDelegate.intersects(this, that); }
//
//    /*
//     * @param thisS -- a segment
//     * @param that -- a segment
//     * Do these lines cross in the middle of each segment?
//     */
//    public boolean middleCrosses(Segment that) { return SegmentDelegate.middleCrosses(this, that); }
//
//    /* 
//     * @return the least of the two segment points (lexicographically)
//     */
//    public PlanarGraphPoint leastPlanarGraphPoint() { return _point1.compareTo(_point2) < 0 ? _point1 : _point2; }
//
//    /*
//     * Pictorially we view the given segment as a ray of an angle
//     *              /
//     *             /  <-- This method generates this segment (with exact length)
//     *            /
//     *           /\ angle (measure)
//     *   pt --> .__\________________  <-- segment
//     * 
//     * 
//     * @param segment -- a segment we treat as a ray 
//     * @param angle -- desired angle measurement in degrees [0, 360)
//     * @param length -- desired length of the resulting segment object (User may want to specify this notion)
//     * @return a segment (corresponding to a ray) with same origin point with desired length 
//     */
//    public Segment segmentByAngle(PlanarGraphPoint origin, double angle, double length)
//    {
//        return rayByAngle(origin, angle, length).asSegment();
//    }
//    public Ray rayByAngle(PlanarGraphPoint origin, double angle, double length)
//    {
//        return RayDelegate.rayByAngle(this.asRay(origin), angle, length);
//    }
//
//    /*
//     * @param pt -- a point
//     * @return true / false if this segment (finite) contains the point
//     */
//    public boolean pointLiesOn(PlanarGraphPoint pt) { return this.pointLiesOnSegment(pt); }
//
//    /*
//     * @param pt -- a point
//     * @return true / false if this segment (finite) contains the point
//     */
//    public boolean pointLiesOnSegment(PlanarGraphPoint pt) { return SegmentDelegate.pointLiesOnSegment(this, pt); }
//
//    /*
//     * @param pt -- a point
//     * @return true / false if this segment (finite) contains the point
//     */
//    public boolean pointLiesOnLine(PlanarGraphPoint pt) { return LineDelegate.pointLiesOnLine(this, pt); }
//
//    /*
//     * @param pt -- a point
//     * @return true if the point is on the segment (EXcluding endpoints); finite examination only
//     */
//    public boolean pointLiesBetweenEndpoints(PlanarGraphPoint pt) { return SegmentDelegate.pointLiesBetweenEndpoints(this, pt); }
//
//    /*
//     * @param that -- another segment
//     * @return true / false if the two lines (infinite) are collinear
//     */
//    public boolean isCollinearWith(Segment that) { return LineDelegate.areCollinear(this, that); }
//    
//    /**
//     * 
//     * @param that -- another segment
//     * @return true / false if the two lines are collinear but do not touch
//     *       A     B
//     *       __    __
//     *      |  |__|  |
//     *      |________|
//     */
//    public boolean isCollinearWithoutOverlap(Segment that) { return !LineDelegate.areCollinear(this, that) && LineDelegate.collinear(this, that); }
//
//    /*
//     * @param that -- another segment
//     * @return true / false if this contains @that segment entirely (finite)
//     *        this:       *----------v--------v--------------*
//     *                               ---that---
//     * sub-segment?
//     */
//    public boolean contains(Segment that) { return SegmentDelegate.contains(this, that); }
//
//    /*
//     * @param that -- another segment
//     * @return true / false if this contains @that segment entirely (finite)
//     *        this:       *----------v--------v--------------*
//     *                               ---that---
//     * sub-segment?
//     */
//    public boolean strictContains(Segment that) { return SegmentDelegate.strictContains(this, that); }
//
//    /*
//     * @param that -- another segment
//     * @return true / false if @that contains @this segment entirely (finite)
//     *        that:       *----------v--------v--------------*
//     *                               ---this---
//     * super-segment?
//     */
//    public boolean containedIn(Segment that) { return SegmentDelegate.contains(that, this); }
//
//    /*
//     * @param that -- another segment
//     * @return true / false if @that contains @this segment entirely (finite)
//     *        that:       *----------v--------v--------------*
//     *                               ---this---
//     * super-segment?
//     */
//    public boolean strictContainedIn(Segment that) { return SegmentDelegate.strictContains(that, this); }
//
//    /*
//     * @param p -- a point
//     * @return true if @pt is one of the endpoints of this segment
//     */
//    public boolean has(PlanarGraphPoint p) { return _point1.equals(p) || _point2.equals(p); }
//
//    /*
//     * @return true if this segment is horizontal (by analysis of both endpoints having same y-coordinate)
//     */
//    public boolean isHorizontal() { return Utilities.equalDoubles(_point1.getY(), _point2.getY()); }
//
//    /*
//     * @return true if this segment is vertical (by analysis of both endpoints having same x-coordinate)
//     */
//    public boolean isVertical() { return Utilities.equalDoubles(_point1.getX(), _point2.getX()); }
//
//    /*
//     * @return the midpoint of this segment (finite)
//     */
//    public PlanarGraphPoint getMidpoint() { return MidpointDelegate.getMidpoint(this); }
//
//    /*
//     * @param that -- a segment (as a line: infinite)
//     * @return the intersection of this segment with that
//     */
//    public PlanarGraphPoint lineIntersection(Segment that) {  return IntersectionDelegate.lineIntersection(this, that); }
//
//    /*
//     * @param that -- a segment (as a segment: finite)
//     * @return the midpoint of this segment (finite)
//     */
//    public PlanarGraphPoint segmentIntersection(Segment that) {  return IntersectionDelegate.segmentIntersection(this, that); }
//
//    /*
//     * @param pt -- a point
//     * @return a segment perpendicular to this segment passing through @pt
//     */
//    public Segment getPerpendicularThrough(PlanarGraphPoint pt) { return LineDelegate.getPerpendicularThrough(this, pt); }
//
//    /*
//     * @return perpendicular bisector to the given segment
//     * The returned segment should be treated as a line (infinite)
//     */
//    public Segment perpendicularBisector() { return SegmentDelegate.perpendicularBisector(this); }
//
//    /*
//     * @param circle -- a circle
//     * @return true if this segment (finite) passes through this circle (intersection points are on the interior of the segment)
//     *      /     \
//     *   __/_______\____
//     *    /         \
//     */
//    public boolean isSecant(Circle circle) { return SegmentDelegate.isSecant(this, circle); }
//
//    /*
//     * @param segments -- a list of segments
//     * @return the common, coincident point of intersection; null if the segment intersection points are not mutually coincident
//     * 
//     *             |/
//     *     --------X------  In this example, three segments intersect at a common point: X
//     *            /|
//     */
//    public static PlanarGraphPoint coincident(Segment... segments) { return SegmentDelegate.coincident(segments); }

//    /*
//     * @param that -- a line (infinite)
//     * @return true if @this and @that are coinciding lines
//     */
//    public boolean coinciding(Segment that) { return isCollinearWith(that); }
//
//    /*
//     * @param that -- a segment (finite)
//     * @return true if @this and @that are coinciding segments
//     *             Yes                No
//     *     _________ _________     ______.________
//     */
//    public boolean coincidingWithoutOverlap(Segment that) { return isCollinearWith(that) && !this.contains(that) && !this.containedIn(that); }
//
//    /*
//     * @param thatt -- a line (infinite)
//     * @return true if @segment and @this are NOT coinciding, but are parallel
//     *        Yes                     No
//     * -------------------        ---------
//     *     -------
//     */
//    public boolean isParallel(Segment that) { return LineDelegate.isParallel(this, that); }
//
//    /*
//     * @param that -- a segment (line)
//     * @return true if the lines are perpendicular to one another
//     * Verified via slope-based analysis
//     */
//    public boolean isSegmentPerpendicular(Segment that) { return SegmentDelegate.isPerpendicular(this, that); }
//
//    /*
//     * @param that -- a segment (line)
//     * @return true if the lines are perpendicular to one another
//     * Verified via slope-based analysis
//     */
//    public boolean isLinePerpendicular(Segment that) { return LineDelegate.isPerpendicular(this, that); }

    /*
     * @param pt -- one of the endpoints of this segment
     * @return the 'other' endpoint of the segment (null if neither endpoint is given)
     */
    public PlanarGraphPoint other(PlanarGraphPoint p)
    {
        if (p.equals(_point1)) return _point2;
        if (p.equals(_point2)) return _point1;

        return null;
    }

//    /*
//     * @param that -- a segment
//     * @return true if the segments coincide, but do not overlap:
//     *                    this                  that
//     *             ----------------           ---------
//     * Note: the segment MAY share an endpoint
//     */
//    public boolean coincideWithoutOverlap(Segment that)
//    {
//        if (!isCollinearWith(that)) return false;
//
//        // Check the endpoints of @that 
//        if (this.pointLiesBetweenEndpoints(that.getPlanarGraphPoint1())) return false;
//
//        if (this.pointLiesBetweenEndpoints(that.getPlanarGraphPoint2())) return false;
//
//        return true;
//    }

    @Override
    public int hashCode()
    {
        return _point1.hashCode() + _point2.hashCode();
    }
    
    @Override
    public String toString() { return "Segment(" + _point1.toString() + ", " + _point2.toString() + ")"; }

    @Override
    public boolean equals(Object obj)
    {
        if (!this.equals(obj)) return false;

        Segment that = (Segment)obj;

        return (_point1.equals(that.getPlanarGraphPoint1()) && _point2.equals(that.getPlanarGraphPoint2())) ||
               (_point1.equals(that.getPlanarGraphPoint2()) && _point2.equals(that.getPlanarGraphPoint1()));
    }


    public PlanarGraphPoint sharedVertex(Segment s)
    {
        if (_point1.equals(s._point1)) return _point1;
        if (_point1.equals(s._point2)) return _point1;
        if (_point2.equals(s._point1)) return _point2;
        if (_point2.equals(s._point2)) return _point2;
        return null;
    }

//    //
//    //
//    // Coordinate-Based Computations for FactComputer (to be moved eventually)
//    //
//    //
//    //
//
//    //
//    // is this segment congruent to the given segment in terms of the coordinatization from the UI?
//    //
//    public boolean coordinateCongruent(Segment s)
//    {
//        return Utilities.equalDoubles(s.length(), this.length());
//    }
//
//    //
//    // is this segment perpendicular to the given segment in terms of the coordinatization from the UI?
//    //
//    public PlanarGraphPoint coordinatePerpendicular(Segment s)
//    {
//        if (!this.isSegmentPerpendicular(s)) return null;
//
//        return this.segmentIntersection(s);
//    }
//
//
//    //
//    //   
//    //  ------------------------ this
//    //              /
//    //             / that
//    //            /
//    public PlanarGraphPoint coordinateBisector(Segment that)
//    {
//        // Do these segments intersect within both sets of stated endpoints?
//        PlanarGraphPoint intersection = this.segmentIntersection(that);
//        
//        if(intersection == null)
//            return null;
//        
//        // Do they intersect in the middle of this segment
//        return Utilities.equalDoubles(PlanarGraphPoint.calcDistance(this.getPlanarGraphPoint1(), intersection),
//                PlanarGraphPoint.calcDistance(this.getPlanarGraphPoint2(), intersection)) ? intersection : null;
//    }
//
//    
//        // Does this segment contain a subsegment:
//        // A-------B-------C------D
//        // A subsegment is: AB, AC, AD, BC, BD, CD
//        public boolean HasSubSegment(Segment possSubSegment)
//        {
//            return this.pointLiesBetweenEndpoints(possSubSegment._point1) && this.pointLiesBetweenEndpoints(possSubSegment._point2);
//        }
//
//
//    //    
//    //    //
//    //    // Do these angles share this segment overlay this angle?
//    //    //
//        public boolean isIncludedSegment(Angle ang1, Angle ang2)
//        {
//            return this.equals(ang1.sharedRay(ang2));
//        }
}

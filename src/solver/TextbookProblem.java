package solver;

import java.util.Vector;

import math.analysis.intersection.Intersection;
import representation.Point;
import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;

/**
 *  A textbook problem consists of a set of functions over a particular interval (domain) 
 */
public class TextbookProblem
{
    // Functions in Mathematica format (sin x == Sin[x], etc.}
    protected StringBasedFunction[] _functions;
    public StringBasedFunction[] getFunctions() { return _functions; }

    // Interval over which we compute the answer
    protected Domain _domain;
    public Domain getDomain() { return _domain; }
    
    // The numeric answer to this problem
    protected double _answer;
    public double getAnswer() { return _answer; }
    public boolean answerKnown() { return _answer == DEFAULT_ANSWER; }
    public static final double DEFAULT_ANSWER = Double.NEGATIVE_INFINITY;
    
    // Metadata refers to the source of the problem (textbook and problem number).
    protected String _metadata;
    public String getMetadata() { return _metadata; }


    public TextbookProblem(String[] functions, Domain domain, String data, double answer)
    {
        //
        // Given the raw strings, create the String-based functions
        //
        _functions = new StringBasedFunction[functions.length];
        for (int index = 0; index < functions.length; index++)
        {
            _functions[index] = new StringBasedFunction(functions[index]);
            if (domain != null) _functions[index].setDomain(domain);
        }

        // Set the overall interval domain for this problem
        _domain = domain;
        //        if (_domain == null) deduceDomain();
        
        // Compute / save all points of intersections in the problem domain
        //_intersections = computeIntersectionPointsInDomain();

        _metadata = data;
        _answer = answer;
    }

    public TextbookProblem(String[] functions, Domain domain, String data)
    {
        this(functions, domain, data, DEFAULT_ANSWER);
    }
    
    /**
     * @param f -- the set of functions (in terms of variable x)
     * @param data -- metadata describing the problem source.
     */
    public TextbookProblem(String[] f, String data)
    {
        this(f, null, data, DEFAULT_ANSWER);
    }
    
    /**
     * @param f -- the set of functions (in terms of variable x)
     * @param data -- metadata describing the problem source.
     * @param answer -- the numeric answer for this problem
     */
    public TextbookProblem(String[] f, String data, double answer)
    {
        this(f, null, data, answer);
    } 

    /**
     * If a problem does not have a domain specified, we need to compute the endpoints of the region.
     * We make an assumption that the desired interval is 'near' the origin.
     * We seek out this origin by investigating larger and larger intervals; for
     * simplicity we use the origin as a center of our 'searching' interval 
     */
    protected void deduceDomain()
    {
        // Do we already know the domain? If so, no need to do anything
        if (_domain != null) return;

        // Otherwise, the domain is implied by the problem
        // Check that we have only a top and bottom function
        if (_functions.length != 2)
        {
            System.out.println("Attempting to deduce domain among " + _functions.length + "; not 2 for " + this.toString());
            return;
        }

        // Find the domain for this problem
        Domain deduced = findDomain();

        //
        // Set the overall interval domain for this problem as well as for the domain of each function 
        //
        _domain = deduced;
        for (StringBasedFunction function : _functions)
        {
            function.setDomain(deduced);
        }
    }

    //****************************************************************************************
    // CTA: Algorithm change;
    //  Ensure we capture all points in the intersection(s) by, once we find 2 intersections,
    // look at the next level (or two) to ensure we have no further points of intersection
    //****************************************************************************************

    /**
     * If a problem does not have a domain specified, we need to compute the endpoints of the region.
     * We make an assumption that the desired interval is 'near' the origin.
     * We seek out this origin by investigating larger and larger intervals; for
     * simplicity we use the origin as a center of our 'searching' interval 
     */
    private Domain findDomain()
    {
        //
        // Since this is a textbook problem we are seeking to intersection points around the origin
        // Begin with an interval centered at 0: [-5, 5]; expand by doubling each time with an upper bound of 1000
        //
        final double LEFT_MIN = -1000;
        final double RIGHT_MAX = 1000;
        double left_x = -5;
        double right_x = 5;

        while (left_x >= LEFT_MIN && right_x < RIGHT_MAX)
        {
            //
            // Compute the intersection points between the two functions over the interval [left_x, rightx]
            //
            Vector<Point> intersections = Intersection.getInstance().allIntersections(_functions[0], _functions[1], left_x, right_x);

            //
            // Do we have a successful bounding of the region?
            //
            if (intersections.size() >= 2)
            {
                // Force the domain to be the [left-most x, right-most x]
                return new Domain(intersections.firstElement().getX(), intersections.lastElement().getX());
            }
//            if (intersections.size() == 2)
//            {
//                return new Domain(intersections.get(0).getX(), intersections.get(1).getX());
//            }
//            else if (intersections.size() > 2)
//            {
//                // Choose the two points that are closest to origin
//                int closestIndexToOrigin = 0;
//                int secondClosestIndexToOrigin = 0;
//                for (int p = 1; p < intersections.size(); p++)
//                {
//                    if (Math.abs(intersections.get(p).getX()) > Math.abs(intersections.get(closestIndexToOrigin).getX()))
//                    {
//                        secondClosestIndexToOrigin = closestIndexToOrigin;
//                        closestIndexToOrigin = p;
//                    }
//                }
//
//                // Order these indices left / right
//                int leftIndex = -1;
//                int rightIndex = -1;
//                if (intersections.get(closestIndexToOrigin).getX() < intersections.get(secondClosestIndexToOrigin).getX())
//                {
//                    leftIndex = closestIndexToOrigin;    
//                    rightIndex = secondClosestIndexToOrigin;    
//                }
//                else
//                {
//                    rightIndex = closestIndexToOrigin;    
//                    leftIndex = secondClosestIndexToOrigin;    
//                }
//                return new Domain(intersections.get(leftIndex).getX(), intersections.get(rightIndex).getX());
//            }

            //
            // ELSE expand the window and search again
            //
            left_x *= 2;
            right_x *= 2;
        }

        System.err.println("Failed to find deduced domain: " + this);
        
        return null;
    }
    
//    // 
//    /**
//     * @return Compute / save all points of intersections in the problem domain
//     */
//    @SuppressWarnings("unchecked")
//    protected List<Point> computeIntersectionPointsInDomain()
//    {
//        Set<Point> points = new HashSet<Point>();
//        
//        for (int f1 = 0; f1 < _functions.length - 1; f1++)
//        {
//            for (int f2 = f1 + 1; f2 < _functions.length; f2++)
//            {
//                points.addAll(Intersection.getInstance().allIntersections(_functions[f1], _functions[f2],
//                                                                          _domain.getLowerBound(), _domain.getUpperBound()));
//            }
//        }
//
//        // Convert to a sorted set of points
//        Vector<Point> sorted = new Vector<Point>(points);
//        Collections.sort(sorted);
//        
//        return sorted;
//    }

    @Override
    public String toString()
    {
        String s = "";

        //
        // Functions
        //
        s += "{ ";

        for (int fIndex = 0; fIndex < _functions.length; fIndex++)
        {
            s += _functions[fIndex];

            if (fIndex < _functions.length - 1) s += ", ";
        }

        s += " }";

        //
        // Domain
        //
        s += "[ ";

        if (_domain != null)
        s += _domain.getLowerBound() + ", " + _domain.getUpperBound();

        s += " ]";

        //
        // Metadata
        //
        s += "//"; 

        s += _metadata;

        return s;
    }
}

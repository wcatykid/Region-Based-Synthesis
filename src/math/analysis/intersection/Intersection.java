package math.analysis.intersection;


import java.util.Vector;

import math.analysis.Analyzer;
import math.external_interface.LocalMathematicaCasInterface;
import representation.Point;
import representation.bounds.Bound;
import representation.bounds.functions.Domain;

//
//
// Bridge to math functions computed through Wolfram-Alpha 
//
//
public class Intersection extends Analyzer
{
    //
    // Singleton instance
    //
    protected static Intersection _theInstance;

    public static Intersection getInstance()
    {
        if (_theInstance != null) return _theInstance;

        _theInstance = new Intersection();

        return _theInstance;
    }

    /**
     * @param f -- a function with a domain
     * @param g -- a function with a domain
     * @return whether f and g intersect in their domains.
     */
    public boolean intersects(Bound f, Bound g)
    {
        return intersection(f, g) != null;
    }


    /**
     * @param f -- a function with a domain
     * @param g -- a function with a domain
     * @return whether f and g intersect in their domains.
     */
    public Vector<Point> allIntersections(Bound f, Bound g)
    {
        return allIntersections(f, f.leftBoundX(), f.rightBoundX(), g, g.leftBoundX(), g.rightBoundX());
    }
    
    /**
     * @param f -- a function with a domain
     * @param g -- a function with a domain
     * @return whether f and g intersect in their domains.
     */
    public Vector<Point> allIntersections(Bound f, Bound g, double leftX, double rightX)
    {
        return allIntersections(f, leftX, rightX, g, leftX, rightX);
    }
    
    /**
     * @param f -- a function (ignoring Domain)
     * @param lowerXf -- x-value such that lowerXf < upperXf 
     * @param upperXf
     * @param g -- a function (ignoring Domain)
     * @param lowerXg -- x-value such that lowerXg < upperXg
     * @param upperXg
     * @return the set of all points (x, y) of intersection in the stated intervals
     */
    private Vector<Point> allIntersections(Bound f, double lowerXf, double upperXf, Bound g, double lowerXg, double upperXg)
    {
        //
        // Construct query, act on query, parse the results
        //
        String query = constructQuery(f, lowerXf, upperXf, g, lowerXg, upperXg);

System.out.println("Query: |" + query + "|");

        String result = LocalMathematicaCasInterface.getInstance().query(query);

System.out.println("|" + result + "|");

        Vector<Point> points = parsePoints(f, result);

        return points;
    }

    /**
     * @param f -- a function with a domain
     * @param g -- a function with a domain
     * @return whether f and g intersect in their domains.
     */
    public Point intersection(Bound f, Bound g)
    {
        return intersection(f, f.leftBoundX(), f.rightBoundX(), g, g.leftBoundX(), g.rightBoundX());
    }
    
    /**
     * @param f -- a function with a domain
     * @param g -- a function with a domain
     * @param leftX -- x-value such that leftX < rightX 
     * @param rightX
     * @return whether f and g intersect in their domains.
     */
    public Point intersection(Bound f, Bound g, double leftX, double rightX)
    {
        return intersection(f, leftX, rightX, g, leftX, rightX);
    }

    /**
     * @param f -- a function (ignoring Domain)
     * @param lowerXf -- x-value such that lowerXf < upperXf 
     * @param upperXf
     * @param g -- a function (ignoring Domain)
     * @param lowerXg -- x-value such that lowerXg < upperXg
     * @param upperXg
     * @return the leftmost (x, y) point of intersection in the stated intervals
     */
    private Point intersection(Bound f, double lowerXf, double upperXf, Bound g, double lowerXg, double upperXg)
    {
        // Acquire all the intersection points
        Vector<Point> intersections = allIntersections(f, lowerXf, upperXf, g, lowerXg, upperXg);

        if (intersections.isEmpty()) return null;

        //
        // Find the point with the smallest x-value
        //
        Point min = intersections.get(0);

        for (int index = 1; index < intersections.size(); index++)
        {
            if (intersections.get(index).lessThan(min))
            {
                min = intersections.get(index);
            }
        }

        return min;
    }
    
    /**
     * @param f -- a function (ignoring Domain)
     * @param lowerXf -- x-value such that lowerXf < upperXf 
     * @param upperXf
     * @param g -- a function (ignoring Domain)
     * @param lowerXg -- x-value such that lowerXg < upperXg
     * @param upperXg
     * @return a String-based Mathematica query for intersections of two functions
     */
    private String constructQuery(Bound f, double lowerXf, double upperXf, Bound g, double lowerXg, double upperXg)
    {
        //
        // Acquire the intersection of these domains.
        //
        Domain domainF = new Domain(lowerXf, upperXf);
        Domain domainG = new Domain(lowerXg, upperXg);

        // Do these domains overlap?
        if (!domainF.intersects(domainG)) return null;        

        // Find the set-intersection of domains to use as the domain for the function-intersection query 
        Domain intersection = domainF.intersection(domainG);

        //
        // Construct
        //
        String query = "NSolve[";

        query += "{";

        // query += "Factor[" + f.toFullMathematicaString() + "]";

        query += f.toFullMathematicaString();
        
        query += " == ";

        //query += "Factor[" + g.toFullMathematicaString() + "]";
        
        query += g.toFullMathematicaString();
        
        //
        // Finite domain
        //
        if (intersection.getLowerBound() != Double.NEGATIVE_INFINITY &&
                intersection.getUpperBound() != Double.NEGATIVE_INFINITY)
        {
            query += ", " + intersection.getLowerBound() + " <= x <= " + intersection.getUpperBound();
        }
        //
        // Half finite domain
        //
        else if (intersection.getLowerBound() == Double.NEGATIVE_INFINITY &&
                intersection.getUpperBound() != Double.NEGATIVE_INFINITY)
        {
            query += ", " + " x <= " + intersection.getUpperBound();
        }
        //
        // Half finite domain
        //
        else if (intersection.getLowerBound() != Double.NEGATIVE_INFINITY &&
                intersection.getUpperBound() == Double.NEGATIVE_INFINITY)
        {
            query += ", " + intersection.getLowerBound() + " <= x ";
        }
        //
        // All Reals
        //
        else if (intersection.getLowerBound() == Double.NEGATIVE_INFINITY &&
                intersection.getUpperBound() == Double.NEGATIVE_INFINITY)
        {
            query += ", " + intersection.getLowerBound() + " <= x ";
        } 
        else System.err.println("Intersection::intersection : unexpected domain values.");

        query += "}, {x}]";
        
        return query;
    }
}

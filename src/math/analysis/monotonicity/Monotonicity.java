package math.analysis.monotonicity;


import java.util.Vector;
import java.util.stream.Collectors;

import math.analysis.Analyzer;
import math.analysis.derivatives.Derivatives;
import math.external_interface.LocalMathematicaCasInterface;
import representation.Point;
import representation.bounds.Bound;
import representation.bounds.functions.BoundedFunction;
import utilities.Utilities;

//
//
// Bridge to math functions computed through Wolfram-Alpha 
//
//
public class Monotonicity extends Analyzer
{
    //
    // Singleton instance
    //
    protected static Monotonicity _theInstance;

    public static Monotonicity getInstance()
    {
        if (_theInstance != null) return _theInstance;

        _theInstance = new Monotonicity();

        return _theInstance;
    }

    /**
     * Monotonicity is defined as being either entirely nonincreasing or nondecreasing over an interval.
     * We test this using the first derivative never changing signs in the interval.
     * 
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return if the local maximum of f on the interval [lowerX, upperX]
     * 
     * Algorithm:
     *    1) Find all zeros of derivative
     *    2) For each midpoint m between zeros
     *          * Verify f(m) is positive OR negative (no sign change)
     *          
     *  This algorithm works since we are dealing with differentiable functions over the interval
     *  (note this implies continuity)
     */
    public boolean isMonotone(Bound f)
    {
        return isMonotone(f, f.leftBoundX(), f.rightBoundX());
    }
    
    /**
     * Monotonicity is defined as being either entirely nonincreasing or nondecreasing over an interval.
     * We test this using the first derivative never changing signs in the interval.
     * 
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return if the local maximum of f on the interval [lowerX, upperX]
     * 
     * Algorithm:
     *    1) Find all zeros of derivative
     *    2) For each midpoint m between zeros
     *          * Verify f(m) is positive OR negative (no sign change)
     *          
     *  This algorithm works since we are dealing with differentiable functions over the interval
     *  (note this implies continuity)
     */
    public boolean isMonotone(Bound f, double lowerX, double upperX)
    {
        // Special case
        if (f.isVertical()) return false;
        
        // Find where f'(x) = 0
        Vector<Double> zeros = Derivatives.getInstance().zeros(f, lowerX, upperX);
        
        // If there are no zeros, there are not max / min; thus monotone
        if (zeros.isEmpty()) return true;
                
        // To compute midpoints properly, append the bounds to the zeros.
        zeros.insertElementAt(lowerX, 0);
        zeros.add(upperX);
        
        // Compute the midpoints
        Vector<Double> midpts = midpoints(zeros);

        // Compute f'(midpoints)
        Vector<Double> fPrimeOf = Derivatives.getInstance().firstDerivativeAtPoints(f, midpts);
        
        // All f'(midpoints) must be either positive or negative
        for (int i = 0; i < fPrimeOf.size() - 1; i++)
        {
            // Sign change between f'(x_1) and f'(x_2)?
            if (fPrimeOf.get(i) * fPrimeOf.get(i + 1) < 0) return false;
        }

        return true;        
    }
    
    /**
     * @param pts -- a set of x-values
     * @return
     */
    private Vector<Double> midpoints(Vector<Double> pts)
    {
        Vector<Double> mids = new Vector<Double>();
        
        for (int i = 0; i < pts.size() - 1; i++)
        {
            mids.add(Utilities.midpoint(pts.get(i), pts.get(i + 1)));
        }
        
        return mids;
    }
    
//    
//    
//    /**
//     * @param f -- a function
//     * @param lowerX -- domain lower bound
//     * @param upperX -- domain upper bound
//     * @return the local maximum of f on the interval [lowerX, upperX]
//     */
//    public Point localMaximum(BoundedFunction f, double lowerX, double upperX)
//    {
//        String query = constructBasicMathematicaQuery("FindMaximum", f.toFullMathematicaString(), lowerX, upperX, lowerX);
//
//        System.out.println("Query: |" + query + "|");
//
//        // Query Mathematica
//        String result = LocalMathematicaCasInterface.getInstance().query(query);
//
//        System.out.println("Result: |" + result + "|");
//
//        //
//        // Interpret the Mathematica result
//        //
//        //        return parsePoint(result);
//        return null;
//    }
//
//    /**
//     * @param f -- a function
//     * @return the local maximum of f on the domain of f
//     */
//    public double maxValue(BoundedFunction f)
//    {
//        return maxValue(f, f.leftBoundX(), f.rightBoundX());
//    }
//
//    /**
//     * @param f -- a function
//     * @param lowerX -- domain lower bound
//     * @param upperX -- domain upper bound
//     * @return the local maximum of f on the interval [lowerX, upperX]
//     */
//    public double maxValue(BoundedFunction f, double lowerX, double upperX)
//    {
//        String query = constructBasicMathematicaQuery("MaxValue", f.toFullMathematicaString(), lowerX, upperX, lowerX);
//
//        query += "//N"; // forces a numerical evaluation of the value
//
//        System.out.println("Query: |" + query + "|");
//
//        // Query Mathematica
//        double result = LocalMathematicaCasInterface.getInstance().queryComplexNumber(query).getReal();
//
//        System.out.println("Result: |" + result + "|");
//
//        //
//        // Interpret the Mathematica result
//        //
//        return result;
//    }
//
//    /**
//     * @param f -- a function
//     * @return the local minimum of f on the domain of f
//     */
//    public double minValue(BoundedFunction f)
//    {
//        return minValue(f, f.leftBoundX(), f.rightBoundX());
//    }
//
//    /**
//     * @param f -- a function
//     * @param lowerX -- domain lower bound
//     * @param upperX -- domain upper bound
//     * @return the local minimum of f on the interval [lowerX, upperX]
//     */
//    public double minValue(BoundedFunction f, double lowerX, double upperX)
//    {
//        String query = constructBasicMathematicaQuery("MinValue", f.toFullMathematicaString(), lowerX, upperX, lowerX);
//
//        query += "//N"; // forces a numerical evaluation of the value
//
//        System.out.println("Query: |" + query + "|");
//
//        // Query Mathematica
//        double result = LocalMathematicaCasInterface.getInstance().queryComplexNumber(query).getReal();
//
//        System.out.println("Result: |" + result + "|");
//
//        //
//        // Interpret the Mathematica result
//        //
//        return result;
//    }
//
//    /**
//     * @param mathematicaF -- string-based mathematica function (procedure name)
//     * @param function -- string-based representation of f(x)
//     * @param lowerX -- domain restriction on f
//     * @param upperX -- domain restriction on f
//     * @param start -- where to start searching
//     * @return resulting String for Mathematica query
//     */
//    public String constructBasicMathematicaQuery(String mathematicaF, String function, double lowerX, double upperX, double start)
//    {
//        //
//        // Construct the query
//        // Example:         FindMaximum[{x Cos[x], 1 <= x <= 15}, {x, 7}]
//        //                             [{Function, Restriction}, start search]
//        // https://reference.wolfram.com/language/ref/FindMaximum.html
//        //
//        String query = "InputForm[";
//
//        query += mathematicaF;
//
//        query += "[";
//
//        query += "{";
//
//        query += function;
//
//        query += ", ";
//
//        query += lowerX + " <= x <= " + upperX;
//
//        query += "}";
//
//        query += ", {";
//
//        query += "x";
//
//        query += "}";
//
//        query += "]";
//
//        query += "]";
//
//        return query;
//    }
//
//    /**
//     * @param f -- a function
//     * @param lowerX -- domain lower bound
//     * @param upperX -- domain upper bound
//     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the interval [lowerX, upperX]
//     */
//    private Vector<Double> extremaQuery(Bound f, String restriction)
//    {
//        String query = "InputForm[";
//
//        query += "NSolve[{Factor[D[";
//
//        query += f.toFullMathematicaString();
//
//        query += ", x]] == 0,";
//
//        query += restriction;
//
//        query += "}";
//
//        query += ", {";
//
//        query += "x";
//
//        query += "}";
//
//        query += "]";
//
//        query += "]";
//
//        query += "//N"; // forces a numerical evaluation of the value
//
//
//        System.out.println("Query: |" + query + "|");
//
//        // Query Mathematica
//        String result = LocalMathematicaCasInterface.getInstance().query(query);
//
//        System.out.println("|" + result + "|");
//
//        //
//        // Interpret the Mathematica result
//        //
//        Vector<Double> xs = parseXValues(result);
//
//        System.out.println("Result: |" + xs + "|");
//
//        return xs;
//    }
//
//    /**
//     * @param f -- a function
//     * @param lowerX -- domain lower bound
//     * @param upperX -- domain upper bound
//     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the interval [lowerX, upperX]
//     */
//    public Vector<Double> extrema(Bound f, double lowerX, double upperX)
//    {
//        return extremaQuery(f, lowerX + " <= x <= " + upperX);
//    }
//
//    /**
//     * @param f -- a function
//     * @param lowerX -- domain lower bound
//     * @param upperX -- domain upper bound
//     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the interval [lowerX, upperX]
//     */
//    public Vector<Double> exclusiveExtrema(Bound f, double lowerX, double upperX)
//    {
//        return extremaQuery(f, lowerX + " < x < " + upperX);
//    }
//    
//    /**
//     * @param f -- a function
//     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the domain
//     */
//    public Vector<Double> extrema(Bound f)
//    {
//        return extremaQuery(f, f.getDomain().getLowerBound() + " <= x <= " + f.getDomain().getUpperBound());
//    }
//
//    /**
//     * @param f -- a function
//     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the domain
//     */
//    public Vector<Double> exclusiveExtrema(Bound f)
//    {
//        return extremaQuery(f, f.getDomain().getLowerBound() + " < x < " + f.getDomain().getUpperBound());
//    }
//    
//    public Vector<Double> firstDerivativeAtPoints( Bound f, Vector<Double> points )
//    {
//        String query = "InputForm[D[" ; 
//        query += f.toFullMathematicaString() ;
//        query += ", {x, 1}]] /. x -> {" ;
//        query += String.join( ", ", points.stream().map( n -> n.toString() ).collect( Collectors.toList() ) ) ;  
//        query += "} //N" ; // forces a numerical evaluation of the value
//
//        System.out.println("Query: |" + query + "|");
//
//        // Query Mathematica
//        String result = LocalMathematicaCasInterface.getInstance().query( query ) ;
//
//        System.out.println( "|" + result + "|" ) ;
//
//        // Interpret the Mathematica result
//        Vector<Double> xs = parseXValues( result ) ;
//
//        System.out.println( "Result: |" + xs + "|" ) ;
//
//        return xs ;
//    }
//
//    public Vector<Double> secondDerivativeAtPoints( Bound f, Vector<Double> points )
//    {
//        String query = "InputForm[D[" ;
//        query += f.toFullMathematicaString() ;
//        query += ", {x, 2}]] /. x -> {" ;
//        query += String.join( ", ", points.stream().map( n -> n.toString() ).collect( Collectors.toList() ) ) ;  
//        query += "} //N" ; // forces a numerical evaluation of the value
//
//        System.out.println("Query: |" + query + "|");
//
//        // Query Mathematica
//        String result = LocalMathematicaCasInterface.getInstance().query( query ) ;
//
//        System.out.println( "|" + result + "|" ) ;
//
//        // Interpret the Mathematica result
//        Vector<Double> xs = parseXValues( result ) ;
//
//        System.out.println( "Result: |" + xs + "|" ) ;
//
//        return xs ;
//    }
}

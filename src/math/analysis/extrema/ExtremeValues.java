package math.analysis.extrema;


import java.util.Vector;
import java.util.stream.Collectors;

import math.analysis.Analyzer;
import math.external_interface.LocalMathematicaCasInterface;
import representation.Point;
import representation.bounds.Bound;
import representation.bounds.functions.BoundedFunction;

//
//
// Bridge to math functions computed through Wolfram-Alpha 
//
//
public class ExtremeValues extends Analyzer
{
    //
    // Singleton instance
    //
    protected static ExtremeValues _theInstance;

    public static ExtremeValues getInstance()
    {
        if (_theInstance != null) return _theInstance;

        _theInstance = new ExtremeValues();

        return _theInstance;
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return the local maximum of f on the interval [lowerX, upperX]
     */
    public Point localMaximum(BoundedFunction f, double lowerX, double upperX)
    {
        String query = constructBasicMathematicaQuery("FindMaximum", f.toFullMathematicaString(), lowerX, upperX, lowerX);

        System.out.println("Query: |" + query + "|");

        // Query Mathematica
        String result = LocalMathematicaCasInterface.getInstance().query(query);

        System.out.println("Result: |" + result + "|");

        //
        // Interpret the Mathematica result
        //
        //        return parsePoint(result);
        return null;
    }

    /**
     * @param f -- a function
     * @return the local maximum of f on the domain of f
     */
    public double maxValue(BoundedFunction f)
    {
        return maxValue(f, f.leftBoundX(), f.rightBoundX());
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return the local maximum of f on the interval [lowerX, upperX]
     */
    public double maxValue(BoundedFunction f, double lowerX, double upperX)
    {
        String query = constructBasicMathematicaQuery("MaxValue", f.toFullMathematicaString(), lowerX, upperX, lowerX);

        query += "//N"; // forces a numerical evaluation of the value

        System.out.println("Query: |" + query + "|");

        // Query Mathematica
        double result = LocalMathematicaCasInterface.getInstance().queryComplexNumber(query).RealPart;

        System.out.println("Result: |" + result + "|");

        //
        // Interpret the Mathematica result
        //
        return result;
    }

    /**
     * @param f -- a function
     * @return the local minimum of f on the domain of f
     */
    public double minValue(BoundedFunction f)
    {
        return minValue(f, f.leftBoundX(), f.rightBoundX());
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return the local minimum of f on the interval [lowerX, upperX]
     */
    public double minValue(BoundedFunction f, double lowerX, double upperX)
    {
        String query = constructBasicMathematicaQuery("MinValue", f.toFullMathematicaString(), lowerX, upperX, lowerX);

        query += "//N"; // forces a numerical evaluation of the value

        System.out.println("Query: |" + query + "|");

        // Query Mathematica
        double result = LocalMathematicaCasInterface.getInstance().queryComplexNumber(query).RealPart;

        System.out.println("Result: |" + result + "|");

        //
        // Interpret the Mathematica result
        //
        return result;
    }

    /**
     * @param mathematicaF -- string-based mathematica function (procedure name)
     * @param function -- string-based representation of f(x)
     * @param lowerX -- domain restriction on f
     * @param upperX -- domain restriction on f
     * @param start -- where to start searching
     * @return resulting String for Mathematica query
     */
    public String constructBasicMathematicaQuery(String mathematicaF, String function, double lowerX, double upperX, double start)
    {
        //
        // Construct the query
        // Example:         FindMaximum[{x Cos[x], 1 <= x <= 15}, {x, 7}]
        //                             [{Function, Restriction}, start search]
        // https://reference.wolfram.com/language/ref/FindMaximum.html
        //
        String query = "InputForm[";

        query += mathematicaF;

        query += "[";

        query += "{";

        query += function;

        query += ", ";

        query += lowerX + " <= x <= " + upperX;

        query += "}";

        query += ", {";

        query += "x";

        query += "}";

        query += "]";

        query += "]";

        return query;
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the interval [lowerX, upperX]
     */
    private Vector<Double> extremaQuery(Bound f, String restriction)
    {
        String query = "InputForm[";

        query += "NSolve[{Factor[D[";

        query += f.toFullMathematicaString();

        query += ", x]] == 0,";

        query += restriction;

        query += "}";

        query += ", {";

        query += "x";

        query += "}";

        query += "]";

        query += "]";

        query += "//N"; // forces a numerical evaluation of the value


        System.out.println("Query: |" + query + "|");

        // Query Mathematica
        String result = LocalMathematicaCasInterface.getInstance().query(query);

        System.out.println("|" + result + "|");

        //
        // Interpret the Mathematica result
        //
        Vector<Double> xs = parseXValues(result);

        System.out.println("Result: |" + xs + "|");

        return xs;
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the interval [lowerX, upperX]
     */
    public Vector<Double> extrema(Bound f, double lowerX, double upperX)
    {
        return extremaQuery(f, lowerX + " <= x <= " + upperX);
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the interval [lowerX, upperX]
     */
    public Vector<Double> exclusiveExtrema(Bound f, double lowerX, double upperX)
    {
        return extremaQuery(f, lowerX + " < x < " + upperX);
    }
    
    /**
     * @param f -- a function
     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the domain
     */
    public Vector<Double> extrema(Bound f)
    {
        return extremaQuery(f, f.getDomain().getLowerBound() + " <= x <= " + f.getDomain().getUpperBound());
    }

    /**
     * @param f -- a function
     * @return the x-values of extrema in the interval: found by solving f'(x) = 0 in the domain
     */
    public Vector<Double> exclusiveExtrema(Bound f)
    {
        return extremaQuery(f, f.getDomain().getLowerBound() + " < x < " + f.getDomain().getUpperBound());
    }
    
    public Vector<Double> secondDerivativeAtPoints( Bound f, Vector<Double> points )
    {
        String query = "InputForm[D[" ;
        query += f.toFullMathematicaString() ;
        query += ", {x, 2}]] /. x -> {" ;
        query += String.join( ", ", points.stream().map( n -> n.toString() ).collect( Collectors.toList() ) ) ;  
        query += "} //N" ; // forces a numerical evaluation of the value

        System.out.println("Query: |" + query + "|");

        // Query Mathematica
        String result = LocalMathematicaCasInterface.getInstance().query( query ) ;

        System.out.println( "|" + result + "|" ) ;

        // Interpret the Mathematica result
        Vector<Double> xs = parseXValues( result ) ;

        System.out.println( "Result: |" + xs + "|" ) ;

        return xs ;
    }
}

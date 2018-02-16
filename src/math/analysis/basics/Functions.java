package math.analysis.basics;


import java.util.Vector;
import java.util.stream.Collectors;

import math.analysis.Analyzer;
import math.external_interface.LocalMathematicaCasInterface;
import representation.bounds.Bound;

//
//
// Bridge to math functions computed through Wolfram-Alpha 
//
//
public class Functions extends Analyzer
{
    //
    // Singleton instance
    //
    protected static Functions _theInstance;

    public static Functions getInstance()
    {
        if (_theInstance != null) return _theInstance;

        _theInstance = new Functions();

        return _theInstance;
    }

    /**
     * @param func -- a function
     * @param points -- a set of values in the domain of func
     * @return f(x) for all x \in points
     */
    public Vector<Double> atPoints( String func, Vector<Double> points )
    {
        String query = func;
        query += " /. x -> {" ;
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

    /**
     * @param func -- a function
     * @param points -- a set of values in the domain of func
     * @return f(x) for all x \in points
     */
    public Vector<Double> atPoints( Bound f, Vector<Double> points )
    {
        return atPoints(f.toFullMathematicaString(), points);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// ZEROS ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
 
    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return all x in (lowerX, upperX) such that f(x) = 0
     */
    public Vector<Double> zerosExclusive(Bound f, double lowerX, double upperX)
    {
        return zeros(f.toFullMathematicaString(), lowerX + " < x < " + upperX);
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return all x in (lowerX, upperX) such that f(x) = 0
     */
    public Vector<Double> zerosInclusive(Bound f, double lowerX, double upperX)
    {
        return zeros(f.toFullMathematicaString(), lowerX + " <= x <= " + upperX);
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return all x in (lowerX, upperX) such that f(x) = 0
     */
    private Vector<Double> zeros(String f, String restriction)
    {
        String query = "InputForm[";

        query += "NSolve[{Factor[";

        query += f;

        query += "] == 0,";

        query += restriction;

        query += "}, {x}]] //N"; //  //N forces a numerical evaluation of the value

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
     * @return all x in (lowerX, upperX) such that f(x) = 0
     */
    public Vector<Double> zerosExclusive(String f, double lowerX, double upperX)
    {
        return zeros(f, lowerX + " < x < " + upperX);
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return all x in (lowerX, upperX) such that f(x) = 0
     */
    public Vector<Double> zerosInclusive(String f, double lowerX, double upperX)
    {
        return zeros(f, lowerX + " <= x <= " + upperX);
    }
}

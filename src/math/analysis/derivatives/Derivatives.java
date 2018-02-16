package math.analysis.derivatives;


import java.util.Vector;

import math.analysis.Analyzer;
import math.analysis.basics.Functions;
import math.external_interface.LocalMathematicaCasInterface;
import representation.bounds.Bound;

//
//
// Bridge to math functions computed through Wolfram-Alpha 
//
//
public class Derivatives extends Analyzer
{
    //
    // Singleton instance
    //
    protected static Derivatives _theInstance;

    public static Derivatives getInstance()
    {
        if (_theInstance != null) return _theInstance;

        _theInstance = new Derivatives();

        return _theInstance;
    }

    /**
     * @return f' (given f)
     */
    public String nthDerivative(String f, int n)
    {
        String query = "InputForm[Factor[D[" + f + ", {x, " + n + " }]]]"; // nth derivative

        System.out.println("Query: |" + query + "|");

        // Query Mathematica
        String deriv = LocalMathematicaCasInterface.getInstance().query(query);

        System.out.println("|" + deriv + "|");

        return deriv;
    }
    
    /**
     * @return f' (given f)
     */
    public String derivative(String f) { return nthDerivative(f, 1); }

    /**
     * @param f -- a function
     * @return f' (given f)
     */
    public String derivative(Bound func) { return derivative(func.toFullMathematicaString()); }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return all x in (lowerX, upperX) f'(x) = 0
     */
    public Vector<Double> zeros(Bound f, double lowerX, double upperX)
    {
        return Functions.getInstance().zerosExclusive(derivative(f), lowerX, upperX);
    }

    /**
     * @param f -- a function
     * @param lowerX -- domain lower bound
     * @param upperX -- domain upper bound
     * @return all x in (lowerX, upperX) f'(x) = 0
     */
    public Vector<Double> zeros(String f, double lowerX, double upperX)
    {
        return Functions.getInstance().zerosExclusive(derivative(f), lowerX, upperX);
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////// Evaluating at points ////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    
    public Vector<Double> firstDerivativeAtPoints( String f, Vector<Double> points )
    {
        return Functions.getInstance().atPoints(derivative(f), points);
    }
    
    public Vector<Double> firstDerivativeAtPoints( Bound f, Vector<Double> points )
    {
        return firstDerivativeAtPoints(f.toFullMathematicaString(), points);
//        
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
    }

    public Vector<Double> secondDerivativeAtPoints( String f, Vector<Double> points )
    {
        return Functions.getInstance().atPoints(nthDerivative(f, 2), points);
    }
    
    public Vector<Double> secondDerivativeAtPoints( Bound f, Vector<Double> points )
    {
        return secondDerivativeAtPoints(f.toFullMathematicaString(), points);
        
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
    }
}

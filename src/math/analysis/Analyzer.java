package math.analysis;

import java.util.Vector;
import representation.Point;
import representation.bounds.Bound;

/**
 * Base Class for front-end CAS Analysis
 *
 */
public abstract class Analyzer
{

    
    /**
     * @param f -- a function to allow us to acquire f(x) given x.
     * @param _points -- a String representation of a mathematica values; example: {{x -> 5}, {x -> 2.}}
     * @return the set of $x$-values described by the input string in the given form;
     *       this example returns
     *       index    x-value
     *       0          f(5)
     *       1          f(2)
     */
    protected Vector<Point> parsePoints(Bound f, String input)
    {
        Vector<Double> xs = parseXValues(input);
        
        //
        // Construct the set of points (x, f(x)) for each x-value parsed
        //
        Vector<Point> points = new Vector<Point>();
        for (Double x : xs)
        {
            Point pt = new Point(x, f.evaluateAtPoint(x).getReal());

            if( ! points.contains( pt ) )
            	points.add( pt ) ;
        }

        return points;
    }
    
    /**
     * @param _points -- a String representation of a mathematica values; example: {{x -> 5}, {x -> 2.}}
     * @return the set of $x$-values described by the input string in the given form;
     *       this example returns
     *       index    x-value
     *       0          5
     *       1          2
     */
    protected Vector<Double> parseXValues(String input)
    {
//        System.out.println("Input: " + input);
        
//        String values = input.substring(1, input.length()-1);
        
//        System.out.println("Values: " + values);
        
        String[] points = input.split("[{]|[}]|,");
        
        //
        // Parse the individual points: x -> 0
        //
        Vector<Double> xs = new Vector<Double>();
        for (String point : points)
        {
        	String p = point.trim();
//            System.out.print(point + "?");
        	
        	// Check for a strange boolean result from Mathematica
        	if (p.contains("True") || p.contains("False"))
        	{
        	    System.err.println("Boolean detected in |" + point + "|");
        	}

            try
            {
	            // Avoid garbage
	            if( p.length() > 5 && p.charAt(0) == 'x' )
	            {
	                // Take |x -> things| and extract: |things|
	                p = p.substring( p.indexOf( "->" ) + 3 ) ;
	            }

	            if( p.length() > 0 )
	            {
                	Double d = Double.parseDouble( p ) ;
               		xs.add( d ) ;
	            }
            }
            catch( NumberFormatException e )
            {
                System.err.println("Expected formatting issue when parsing (non)-double " + p);
            }
        }

        return xs;
    }
}

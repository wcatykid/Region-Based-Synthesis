package math.analysis;

import java.util.Vector;

import representation.Point;
import representation.bounds.Bound;
import representation.bounds.functions.BoundedFunction;

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
            Point pt = new Point(x, f.evaluateAtPoint(x));

            points.add(pt);
            
//            System.out.println("Intersection point for x (" + x + ") " + pt);
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
        
        String values = input.substring(1, input.length()-1);
        
//        System.out.println("Values: " + values);
        
        String[] points = values.split("[{]|[}]");
        
        //
        // Parse the individual points: x -> 0
        //
        Vector<Double> xs = new Vector<Double>();
        for (String point : points)
        {
//            System.out.print(point + "?");

            // Avoid garbage
            if (point.length() > 5 && point.charAt(0) == 'x')
            {
                // Take |x -> things| and extract: |things|
                String x_value = point.substring(point.indexOf("->") + 3);
                
                xs.add(Double.parseDouble(x_value));
            }
        }
        
//        //
//        // Printing...debugging
//        //
//        for (Double x : xs)
//        {
//            System.out.println("|" + x + "|");
//        }

        return xs;
    }
}

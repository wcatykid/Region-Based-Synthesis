package math.analysis.intersection;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import math.analysis.intersection.Intersection;
import math.external_interface.LocalMathematicaCasInterface;
import representation.Point;
import representation.bounds.functions.StringBasedFunction;
import utilities.Assertions;
import utilities.StringUtilities;
import utilities.Utilities;

public class IntersectionTester
{
    @Test
    public void test()
    {
        String testName = "Function Intersection Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        stringBasedIntersection(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void stringBasedIntersection(int indent)
    {
        String testName = "String-Based Intersection Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        stringBasedPolynomialIntersection(indent + 1);
        stringBasedTrigonometricIntersection(indent + 1);
        stringBasedAxesIntersection(indent + 1);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void stringBasedPolynomialIntersection(int indent)
    {
        String testName = "Intersection of String-Based Polynomial Functions";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        StringBasedFunction f = new StringBasedFunction("x");
        f.setDomain(-1, 2);
        StringBasedFunction g = new StringBasedFunction("x^2");
        g.setDomain(-1, 2);
        
        //
        // Acquire the intersection points 
        //
        Vector<Point> found = Intersection.getInstance().allIntersections(f, g);

        //
        // Verify the intersection points 
        //
        Vector<Point> expected = new Vector<Point>();
        expected.add(new Point(0, 0));
        expected.add(new Point(1, 1));
        
        Assertions.Assert(found.size(), expected.size());
        
        for (int p = 0; p < found.size(); p++)
        {
            Assertions.Assert(found.get(p), expected.get(p));
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void stringBasedTrigonometricIntersection(int indent)
    {
        String testName = "Intersection of String-Based Trigonometric Functions";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        StringBasedFunction f = new StringBasedFunction("x");
        f.setDomain(0, 2);
        StringBasedFunction g = new StringBasedFunction("3Sin[2x]");
        g.setDomain(0, 2);
        
        //
        // Acquire the intersection points 
        //
        Vector<Point> found = Intersection.getInstance().allIntersections(f, g);

        //
        // Verify the intersection points 
        //
        Vector<Point> expected = new Vector<Point>();
        expected.add(new Point(0, 0));
        expected.add(new Point(1.33939, 1.33939));
        
        Assertions.Assert(found.size(), expected.size());
        
        for (int p = 0; p < found.size(); p++)
        {
            Assertions.Assert(found.get(p), expected.get(p));
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void stringBasedAxesIntersection(int indent)
    {
        String testName = "Intersection of Functions: x-axis and polynomial";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        StringBasedFunction f = new StringBasedFunction("x^2 - 4x");
        f.setDomain(-5, 5);
        StringBasedFunction g = new StringBasedFunction("0");
        g.setDomain(-5, 5);
        
        //
        // Acquire the intersection points 
        //
        Vector<Point> found = Intersection.getInstance().allIntersections(f, g);

        //
        // Verify the intersection points 
        //
        Vector<Point> expected = new Vector<Point>();
        expected.add(new Point(0, 0));
        expected.add(new Point(4, 0));
        
        Assertions.Assert(found.size(), expected.size());
        
        for (int p = 0; p < found.size(); p++)
        {
            Assertions.Assert(found.get(p), expected.get(p));
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
}

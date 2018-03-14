package math.analysis.basics;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import utilities.Assertions;
import utilities.StringUtilities;

public class FunctionsTest
{

    @Test
    public void testZerosExclusive()
    {
        String testName = "Zeros of a Function Test";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runZerosExclusive(1, "-x^2 + 4", -5, 5, -2.0, 2.0);
        runZerosExclusive(1, "x^2 - 9", -5, 5, -3.0, 3.0);
        runZerosExclusive(1, "2^x - 1", -5, 5, 0.0);
        runZerosExclusive(1, "2^x", -5, 5);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runZerosExclusive(int indent, String func, double lowerX, double upperX, Double ... expectedXs)
    {
        String testName = func;
        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        
        StringBasedFunction function = new StringBasedFunction(func);
        function.setDomain(new Domain(lowerX, upperX));

        Vector<Double> computedXs = Functions.getInstance().zerosExclusive(function, lowerX, upperX);
        
        //
        // Check we have the same lists: expected / computed
        //
        compare(computedXs, expectedXs);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    @Test
    public void testAtPoints()
    {
        String testName = "Zeros of a Function Test";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        Double[] in = new Double[]{-2.0, -1.0, 0.0, 1.0, 2.0};
        runAtPoints(1, "x^2", -5, 5, in, 4.0, 1.0, 0.0, 1.0, 4.0);
        runAtPoints(1, "x^2 - 9", -5, 5, in, -5.0, -8.0, -9.0, -8.0, -5.0);
        runAtPoints(1, "2^x", -5, 5, in, 0.25, 0.5, 1.0, 2.0, 4.0);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void runAtPoints(int indent, String func, double lowerX, double upperX, Double[] xs, Double ... expectedXs)
    {
        String testName = func;
        System.out.println(StringUtilities.generateTestStartString(testName, 0));

       
        StringBasedFunction function = new StringBasedFunction(func);
        function.setDomain(new Domain(lowerX, upperX));

        Vector<Double> computedXs = Functions.getInstance().atPoints(function, new Vector<Double>(Arrays.asList(xs)));
        compare(computedXs, expectedXs);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void compare(Vector<Double> computedXs, Double[] expectedXs)
    {
        //
        // Check we have the same lists: expected / computed
        //
        Assertions.Assert(computedXs.size(), expectedXs.length);
        
        for (int index = 0; index < computedXs.size(); index++)
        {
            Assertions.Assert(computedXs.get(index), expectedXs[index]);
        }
    }
}

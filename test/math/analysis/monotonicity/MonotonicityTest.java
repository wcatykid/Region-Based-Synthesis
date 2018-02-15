package math.analysis.monotonicity;

import org.junit.Test;

import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import utilities.Assertions;
import utilities.StringUtilities;

public class MonotonicityTest
{

    @Test
    public void testIsMonotone()
    {
        String testName = "Monotonicity of a Function Test";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runMonotonicity(1, "x(x - 1)(x + 1)", -5, 5, false);
        runMonotonicity(1, "x(x - 1)(x + 1)(x - 2)(x + 2)", -5, 5, false);
        runMonotonicity(1, "-x^2 + 4", -5, 5, false);
        runMonotonicity(1, "x^2 - 9", -5, 5, false);
        runMonotonicity(1, "2^x - 1", -5, 5, true);
        runMonotonicity(1, "2^x", -5, 5, true);
        runMonotonicity(1, "x", -5, 5, true);
        runMonotonicity(1, "y = 5", -5, 5, true);
        runMonotonicity(1, "x = 5", 5, 5, false);

        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runMonotonicity(int indent, String func, double lowerX, double upperX, boolean expected)
    {
        String testName = func;
        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        
        StringBasedFunction function = new StringBasedFunction(func);
        function.setDomain(new Domain(lowerX, upperX));

        // Main test
        boolean isMonotone = Monotonicity.getInstance().isMonotone(function, lowerX, upperX);
        Assertions.Assert(isMonotone, expected);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
}

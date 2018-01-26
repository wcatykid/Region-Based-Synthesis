package math.external_interface;

import org.junit.Test;

import utilities.StringUtilities;
import utilities.Utilities;

public class LocalMathematicaCasInterfaceTest
{
    @Test
    public void test()
    {
        String testName = "Local Mathematica CAS Interface Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        evaluateFunctionAtAPoint(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    /**
     * @param indent -- level of indentation for this test.
     * Evaluate f(x) = x^2 for x \in [-10, 10]
     */
    private void evaluateFunctionAtAPoint(int indent)
    {
        String testName = "Evaluate a Function At a Point";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Polynomial
        //
        String function = "x^2 + 1";

        for (double x = -10; x < 10; x++)
        {
            assert(Utilities.equalDoubles(LocalMathematicaCasInterface.getInstance().evaluateAtPoint(function, x).getReal(), x * x + 1));
        }
        
        //
        // Trigonometric
        //
        function = "Sin[Pi x]";

        for (int x = -10; x < 10; x++)
        {
            assert(Utilities.equalDoubles(LocalMathematicaCasInterface.getInstance().evaluateAtPoint(function, x).getReal(), 0));
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
}




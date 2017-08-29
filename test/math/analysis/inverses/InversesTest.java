package math.analysis.inverses;

import org.junit.Test;

import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import utilities.Assertions;
import utilities.StringUtilities;

public class InversesTest
{
    @Test
    public void test()
    {
        String testName = "Extreme Values Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runInverse(1, "Sqrt[x]", 1, 5.0, "y^2");
        runInverse(1, "x^2", 0, 5.0, "Sqrt[y]");
        runInverse(1, "x^3", 0, 5.0, "y^(1/3)");
        runInverse(1, "x^(1/3)", 0, 5.0, "y^3");
        runInverse(1, "-x^3", 0, 1, "-((-1)^(2/3)*y^(1/3))");
        runInverse(1, "-x^2", 0, 1, "(-I)*Sqrt[y]");
        
//        runInverse(1, "(x-1)(x+1)(x^2-4)", -5.0, 5.0, -1.5811388300841898, 0.0, 1.5811388300841898);
//        runInverse(1, "(x-1)(x+1)(x^2-4)", -1.0, 1.0, 0.0);
//        runInverse(1, "x^2 - 6x", -5.0, 5.0, 3.0);
//        runInverse(1, "Sin[x]", -7.0, 7.0, -4.71238898038469, -1.5707963267948966, 1.5707963267948966, 4.71238898038469);
//        runInverse(1, "Sin[Pi x]", -2.0, 2.0, -1.5, -0.5, 0.5, 1.5);
//        runInverse(1, "Cos[Pi x]", -2.0, 2.0, -2.0, -1.0, 0.0, 1.0, 2.0);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runInverse(int indent, String func, double lowerX, double upperX, String expected)
    {
        String testName = func + " --> " + expected;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        StringBasedFunction function = new StringBasedFunction(func);
        function.setDomain(new Domain(lowerX, upperX));

        StringBasedFunction computedInverse = Inverses.getInstance().computeInverse(function);
        
        //
        // Check we have the same lists: expected / computed
        //
        Assertions.Assert(computedInverse.getFunction(), expected);
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
}

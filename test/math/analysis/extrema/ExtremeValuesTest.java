package math.analysis.extrema;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import math.analysis.intersection.Intersection;
import math.external_interface.LocalMathematicaCasInterface;
import representation.Point;
import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import utilities.Assertions;
import utilities.StringUtilities;
import utilities.Utilities;

public class ExtremeValuesTest
{
    @Test
    public void test()
    {
        String testName = "Extreme Values Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runExtrema(1, "-x^2 + 4", -5.0, 5.0, 0.0);
        runExtrema(1, "(x-1)(x+1)(x^2-4)", -5.0, 5.0, -1.5811388300841898, 0.0, 1.5811388300841898);
        runExtrema(1, "(x-1)(x+1)(x^2-4)", -1.0, 1.0, 0.0);
        runExtrema(1, "x^2 - 6x", -5.0, 5.0, 3.0);
        runExtrema(1, "Sin[x]", -7.0, 7.0, -4.71238898038469, -1.5707963267948966, 1.5707963267948966, 4.71238898038469);
        runExtrema(1, "Sin[Pi x]", -2.0, 2.0, -1.5, -0.5, 0.5, 1.5);
        runExtrema(1, "Cos[Pi x]", -2.0, 2.0, -2.0, -1.0, 0.0, 1.0, 2.0);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runExtrema(int indent, String func, double lowerX, double upperX, Double ... expectedXs)
    {
        String testName = func;

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        StringBasedFunction function = new StringBasedFunction(func);
        function.setDomain(new Domain(lowerX, upperX));

        Vector<Double> computedXs = ExtremeValues.getInstance().extrema(function, lowerX, upperX);
        
        //
        // Check we have the same lists: expected / computed
        //
        Assertions.Assert(computedXs.size(), expectedXs.length);
        
        for (int index = 0; index < computedXs.size(); index++)
        {
            Assertions.Assert(computedXs.get(index), expectedXs[index]);
        }

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
}

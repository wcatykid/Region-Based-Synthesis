package math.external_interface;

import org.junit.Test;

import utilities.Assertions;
import utilities.StringUtilities;

public class ComplexNumberParserTest
{
    @Test
    public void test()
    {
        String testName = "Mathematica Complex Number Parser";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        run(1, "0.000000000000000333066907387547", 0);
        run(1, "10.000000000000000333066907387547", 10);
        run(1, "-40.000000000000000333066907387547", -40);
        run(1, "0.7937005259840998 - 0.000000000000000333066907387547 I", 0.7937005259840998);
        run(1, "1.6653345369377348*^-16", 0);
        run(1, "1.6653345369377348*^-16I", 0);
        run(1, "-1.6653345369377348*^-16", 0);
        run(1, "-1.6653345369377348*^-16I", 0);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void run(int indent, String number, double expected)
    {
        String testName = number;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        Assertions.Assert(ComplexNumberParser.simplify(number).getReal(), expected);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
}

package solver.area.parser;

import org.junit.Test;

import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParser;
import utilities.Assertions;
import utilities.StringUtilities;

public class AreaProblemParserTest
{
    @Test
    public void test()
    {
        String testName = "Area Problem Parser Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testSimple(1);
        testDeduceDomain(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testSimple(int indent)
    {
        String testName = "Simple Area Problem Parsing";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testSimpleStringOne(1);
        testSimpleStringTwo(1);
        testSimpleWithExponential(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void testSimpleStringOne(int indent)
    {
        String pStr = "{ x^2 + 2 ; -x } [0, 1]  // LH, Page 379 Example 1";
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function and evaluate it: Polynomial
        //
        TextbookAreaProblem problem = makeAreaProblem(pStr);

        //
        // Check the accuracy of that problem
        //
        // Region / functions
        Assertions.Assert(problem.getFunctions().length, 2);

        // Domain
        Assertions.Assert(problem.getDomain().getLowerBound(), 0);
        Assertions.Assert(problem.getDomain().getUpperBound(), 1);

        // Metadata
        Assertions.Assert(problem.getMetadata(), "LH, Page 379 Example 1");

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testSimpleStringTwo(int indent)
    {
        String pStr = "{ x^2 - 6x ; 0 } [0, 6]  // LH, Page 383 #1";
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function and evaluate it: Polynomial
        //
        TextbookAreaProblem problem = makeAreaProblem(pStr);

        //
        // Check the accuracy of that problem
        //
        // Region / functions
        Assertions.Assert(problem.getFunctions().length, 2);

        // Domain
        Assertions.Assert(problem.getDomain().getLowerBound(), 0);
        Assertions.Assert(problem.getDomain().getUpperBound(), 6);

        // Metadata
        Assertions.Assert(problem.getMetadata(), "LH, Page 383 #1");

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void testSimpleWithExponential(int indent)
    {
        String pStr = "{ x Exp[-x^2] ; 0 } [0,1] // LH, Page 383 #133";
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function and evaluate it: Polynomial
        //
        TextbookAreaProblem problem = makeAreaProblem(pStr);

        //
        // Check the accuracy of that problem
        //
        // Region / functions
        Assertions.Assert(problem.getFunctions().length, 2);

        // Domain
        Assertions.Assert(problem.getDomain().getLowerBound(), 0);
        Assertions.Assert(problem.getDomain().getUpperBound(), 1);

        // Metadata
        Assertions.Assert(problem.getMetadata(), "LH, Page 383 #133");

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void testDeduceDomain(int indent)
    {
        String testName = "Area Problems with Domain Deduction";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runDeduceDomain(indent + 1, "{ x^2 - 4x; 0 }                       // LH, Page 383 #7; Implied Domain", 0, 4, "LH, Page 383 #7; Implied Domain");
        runDeduceDomain(indent + 1, "{ 3 - 2x - x^2; 0 }                   // LH, Page 383 #8; Implied Domain", -3, 1, "LH, Page 383 #8; Implied Domain");
        runDeduceDomain(indent + 1, "{ x^2 + 2x + 1; 3x + 3 }              // LH, Page 383 #9; Implied Domain", -1, 2, "LH, Page 383 #9; Implied Domain");
        runDeduceDomain(indent + 1, "{ Surd[x, 3] ; x }                    // LH, Page 383 #16; Implied Domain; 3 intersection points; Regions: 2", -1, 1, "LH, Page 383 #16; Implied Domain; 3 intersection points; Regions: 2");
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void runDeduceDomain(int indent, String pStr, double left, double right, String metadata)
    {
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function and evaluate it: Polynomial
        //
        TextbookAreaProblem problem = makeAreaProblem(pStr);

        //
        // Check the accuracy of that problem
        //
        // Region / functions
        Assertions.Assert(problem.getFunctions().length, 2);

        // Domain
        Assertions.Assert(problem.getDomain().getLowerBound(), left);
        Assertions.Assert(problem.getDomain().getUpperBound(), right);

        // Metadata
        Assertions.Assert(problem.getMetadata(), metadata);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    public static TextbookAreaProblem makeAreaProblem(String problemString)
    {
        AreaProblemParser parser = new AreaProblemParser(problemString);

        if (parser.verify()) parser.parse();
        else
        {
            System.err.println("Problem parse issue; not verified: |" + problemString + "|");
        }

        // Get the problem
        return parser.getProblem();
    }
}

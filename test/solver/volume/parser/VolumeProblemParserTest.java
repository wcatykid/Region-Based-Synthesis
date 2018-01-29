package solver.volume.parser;

import org.junit.Test;

import solver.volume.AxisOfRevolution;
import utilities.Pair;
import solver.volume.TextbookVolumeProblems;
import solver.volume.parser.VolumeProblemParser;
import utilities.Assertions;
import utilities.StringUtilities;

public class VolumeProblemParserTest
{
    @Test
    public void test()
    {
        String testName = "Volume Problem Parser Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testSimple(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testSimple(int indent)
    {
        String testName = "Simple Area Problem Parsing";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testOneAxis(1);
        testMultipleAxes(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void testOneAxis(int indent)
    {
        String pStr = "{ x ; x^2 } [0, 1] [<x = 0, Pi / 6>] // Stewart, Page 358 Example 4-5";
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function and evaluate it: Polynomial
        //
        TextbookVolumeProblems problem = makeVolumeProblem(pStr);

        //
        // Check the accuracy of that problem
        //
        // Region / functions
        Assertions.Assert(problem.getFunctions().length, 2);

        // Domain
        Assertions.Assert(problem.getDomain().getLowerBound(), 0);
        Assertions.Assert(problem.getDomain().getUpperBound(), 1);
        
        // Axis of Revolution / Answer
        Pair<AxisOfRevolution, Double> pair = problem.getAxisAnswerPair(0);
        Assertions.Assert(pair.getFirst(), new AxisOfRevolution("x = 0"));
        Assertions.Assert(pair.getSecond(), new Double(0.523599));
        
        // Metadata
        Assertions.Assert(problem.getMetadata(), "Stewart, Page 358 Example 4-5");

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testMultipleAxes(int indent)
    {
        String pStr = "{ x ; x^2 } [0, 1] [<x = 0, Pi / 6> ; <y = 0, 2 * Pi / 15> ; <y = 2, 8 * Pi / 15>] // Stewart, Page 358 Example 4-5";
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function and evaluate it: Polynomial
        //
        TextbookVolumeProblems problem = makeVolumeProblem(pStr);

        //
        // Check the accuracy of that problem
        //
        // Region / functions
        Assertions.Assert(problem.getFunctions().length, 2);

        // Domain
        Assertions.Assert(problem.getDomain().getLowerBound(), 0);
        Assertions.Assert(problem.getDomain().getUpperBound(), 1);

        //
        // Axes of Revolution / Answers
        //
        // (1)
        Pair<AxisOfRevolution, Double> pair = problem.getAxisAnswerPair(0);
        Assertions.Assert(pair.getFirst(), new AxisOfRevolution("x = 0"));
        Assertions.Assert(pair.getSecond(), new Double(0.523599));
        
        // (2)
        pair = problem.getAxisAnswerPair(1);
        Assertions.Assert(pair.getFirst(), new AxisOfRevolution("y = 0"));
        Assertions.Assert(pair.getSecond(), new Double(0.418879));
        
        // (3)
        pair = problem.getAxisAnswerPair(2);
        Assertions.Assert(pair.getFirst(), new AxisOfRevolution("y = 2"));
        Assertions.Assert(pair.getSecond(), new Double(1.67552));
        
        // Metadata
        Assertions.Assert(problem.getMetadata(), "Stewart, Page 358 Example 4-5");

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    public static TextbookVolumeProblems makeVolumeProblem(String problemString)
    {
        VolumeProblemParser parser = new VolumeProblemParser(problemString);

        if (parser.verify()) parser.parse();
        else
        {
            System.err.println("Problem parse issue; not verified: |" + problemString + "|");
        }

        // Get the problem
        return parser.getProblem();
    }
}

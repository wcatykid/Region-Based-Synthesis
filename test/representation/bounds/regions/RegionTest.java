package representation.bounds.regions;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import exceptions.DomainException;
import facades.AggregatorGenerator;
import representation.bounds.functions.StringBasedFunction;
import representation.regions.Region;
import solver.RegionProblemAggregator;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import solver.area.regionComputer.RegionExtractor;
import solver.area.solver.SolverMain;
import utilities.Assertions;
import utilities.StringUtilities;

public class RegionTest
{
    @Test
    public void testPassesThrough()
    {
//        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testComputeClosestHorizontally()
    {
//        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testRightOf()
    {
        String testName = "Right of (a region)";

        System.out.println(StringUtilities.generateTestStartString(testName, 1));

        //
        // Vertical side test
        //
        // 4 x 4 square at the origin (bottom left at the origin)
        RegionProblemAggregator square = AggregatorGenerator.generateSquare();
        Assertions.Assert(! square.getRegion().rightOf(new StringBasedFunction("x = -1")));
        Assertions.Assert(! square.getRegion().rightOf(new StringBasedFunction("x = 0")));
        Assertions.Assert(square.getRegion().rightOf(new StringBasedFunction("x = 4")));
        Assertions.Assert(square.getRegion().rightOf(new StringBasedFunction("x = 5")));

        System.out.println(StringUtilities.generateTestEndString(testName, 1));

    }

    @Test
    public void testLeftOf()
    {
        String testName = "Left of (a region)";

        System.out.println(StringUtilities.generateTestStartString(testName, 1));

        //
        // Vertical side test
        //
        // 4 x 4 square at the origin (bottom left at the origin)
        RegionProblemAggregator square = AggregatorGenerator.generateSquare();
        Assertions.Assert(square.getRegion().leftOf(new StringBasedFunction("x = -1")));
        Assertions.Assert(square.getRegion().leftOf(new StringBasedFunction("x = 0")));
        Assertions.Assert(! square.getRegion().leftOf(new StringBasedFunction("x = 4")));
        Assertions.Assert(! square.getRegion().leftOf(new StringBasedFunction("x = 5")));

        System.out.println(StringUtilities.generateTestEndString(testName, 1));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////// One-To-One /////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testIsOneToOne() throws DomainException
    {
        String testName = "Region Tests: Is One-To-One";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runOneToOne(1, "{ -x^2 + 4 ; x^2 - 4 }                    <21.33333>  //", false);
        runOneToOne(1, "{ -x^2 + 4 ; x^2 - 4 }           [-1, 1]  <14.66666>  //", false);
        runOneToOne(1, "{ x^2 + 2 ; -x }                 [0, 1]   <2.833333>  // LH, Page 379 Example 1;", true);
        runOneToOne(1, "{ 2 - x^2 ; x }                           <4.5>       // LH, Page 379 Example 2; Implied Domain;", false);
        runOneToOne(1, "{ x^2 ; x^3 }                             <0.083333>  // LH, Page 383 #4; Implied Domain;", true);
        runOneToOne(1, "{ 1/(x^2) ; 0 }                  [1,5]    <0.799999>  // LH, Page 383 #12;", true);
        runOneToOne(1, "{ 1/(x^2) ; 0 }                  [1, 5]   <0.79999>   // LH, Page 431 #1;", true);
        runOneToOne(1, "{ x ; x^2 }                               <0.1666666> // Stewart, Page 352 #7; Implied Domain;", true);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runOneToOne(int indent, String pStr, boolean expected) throws DomainException
    {
        System.out.println(StringUtilities.generateTestStartString(pStr, indent));

        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);

        RegionExtractor extractor = new RegionExtractor(problem);

        Vector<Region> regions = extractor.getRegions();

        for (Region region : regions)
        {
            Assertions.Assert(region.isOneToOne(), expected);
        }

        System.out.println(StringUtilities.generateTestEndString(pStr, indent));
    }
}

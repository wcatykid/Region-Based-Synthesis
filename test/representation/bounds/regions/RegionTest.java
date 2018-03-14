package representation.bounds.regions;

import org.junit.Test;

import facades.AggregatorGenerator;
import representation.bounds.functions.StringBasedFunction;
import solver.RegionProblemAggregator;
import utilities.Assertions;
import utilities.StringUtilities;

public class RegionTest
{

    @Test
    public void testIsMonotonic()
    {
        //HERE
//        fail("Not yet implemented"); // TODO
    }
    
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

}

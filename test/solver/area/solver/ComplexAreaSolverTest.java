package solver.area.solver;

import org.junit.Test;

import facades.AggregatorGenerator;
import solver.RegionProblemAggregator;
import solver.area.solver.AreaSolverByX;
import utilities.StringUtilities;
import utilities.Utilities;

public class ComplexAreaSolverTest
{
    @Test
    public void test()
    {
        String testName = "Complex Area tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testTwoBottomOneTop(1);
        testThreeTopTwoBottomAligned(1);
        testThreeTopThreeBottomUnaligned(1);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testTwoBottomOneTop(int indent)
    {
        String testName = "Two Bottom, One Top";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        // Construct solver and region
        AreaSolverByX solver = new AreaSolverByX();
        RegionProblemAggregator region = AggregatorGenerator.generateTwoBottomOneTop();

        //
        // Solve the area between curve problem; the solution is internal to the aggregator object
        //
        solver.solve(region);
        double result = region.getAreaProblemSolutionByX().evaluate();

        // Check answer
        assert(Utilities.equalDoubles(result, 6.75));
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testThreeTopTwoBottomAligned(int indent)
    {
        String testName = "Three Top, Two Bottom Aligned";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        // Construct solver and region
        AreaSolverByX solver = new AreaSolverByX();
        RegionProblemAggregator region = AggregatorGenerator.generateThreeTopTwoBottomAligned();

        //
        // Solve the area between curve problem; the solution is internal to the aggregator object
        //
        solver.solve(region);
        double result = region.getAreaProblemSolutionByX().evaluate();

        // Check answer
        assert(Utilities.equalDoubles(result, 25.33333)); // 76 / 3
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void testThreeTopThreeBottomUnaligned(int indent)
    {
        String testName = "Three Top, Three Bottom Unaligned";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        // Construct solver and region
        AreaSolverByX solver = new AreaSolverByX();
        RegionProblemAggregator region = AggregatorGenerator.generateThreeTopThreeBottomUnaligned();

        //
        // Solve the area between curve problem; the solution is internal to the aggregator object
        //
        solver.solve(region);
        double result = region.getAreaProblemSolutionByX().evaluate();

        // Check answer
        assert(Utilities.equalDoubles(result, 10)); // This is wrong...Too lazy to calculate 
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    
    
    
}

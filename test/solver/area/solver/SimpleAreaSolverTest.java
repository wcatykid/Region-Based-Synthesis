package solver.area.solver;

import org.junit.Test;

import facades.AggregatorGenerator;
import solver.RegionProblemAggregator;
import solver.area.solver.AreaSolverByX;
import utilities.StringUtilities;
import utilities.Utilities;

public class SimpleAreaSolverTest
{
    @Test
    public void test()
    {
        String testName = "Simple Area tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testSquare(1);
        testRectangle(1);
        testParabolaCappedWithLine(1);
        testCappedParabolas(1);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testSquare(int indent)
    {
        String testName = "Square";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        // Construct solver and region
        AreaSolverByX solver = new AreaSolverByX();
        RegionProblemAggregator square = AggregatorGenerator.generateSquare();

        //
        // Solve the area between curve problem; the solution is internal to the aggregator object
        //
        solver.Solution s = solver.solve( square.getRegion() ) ; 
        square.setAreaProblemSolutionByX( s ) ;
        double result = square.getAreaProblemSolutionByX().evaluate() ;

        // Check answer
        assert(Utilities.equalDoubles(result, 16));
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void testRectangle(int indent)
    {
        String testName = "Rectangle";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        // Construct solver and region
        AreaSolverByX solver = new AreaSolverByX();
        RegionProblemAggregator rectangle = AggregatorGenerator.generateRectangle();

        //
        // Solve the area between curve problem; the solution is internal to the aggregator object
        //
        solver.Solution s = solver.solve( rectangle.getRegion() ) ;
        rectangle.setAreaProblemSolutionByX( s ) ;
        double result = rectangle.getAreaProblemSolutionByX().evaluate();

        // Check answer
        assert(Utilities.equalDoubles(result, 30));
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    /**
     * @param indent -- level of testing output indentation
     * Region defined by a line on top and a parabola on the bottom
     */
    private void testParabolaCappedWithLine(int indent)
    {
        String testName = "Parabola Capped With Line";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        // Construct solver and region
        AreaSolverByX solver = new AreaSolverByX();
        RegionProblemAggregator region = AggregatorGenerator.generateParabolaCappedWithLine();

        //
        // Solve the area between curve problem; the solution is internal to the aggregator object
        //
        solver.Solution s = solver.solve( region.getRegion() ) ;
        region.setAreaProblemSolutionByX( s ) ;
        double result = region.getAreaProblemSolutionByX().evaluate();

        // Check answer
        assert(Utilities.equalDoubles(result, 1.33333));
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    /**
     * @param indent -- level of testing output indentation
     * Region defined by a parabola on top and a parabola on the bottom
     */
    private void testCappedParabolas(int indent)
    {
        String testName = "Capped Parabolas";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        // Construct solver and region
        AreaSolverByX solver = new AreaSolverByX();
        RegionProblemAggregator region = AggregatorGenerator.generateCappedParabolas();

        //
        // Solve the area between curve problem; the solution is internal to the aggregator object
        //
        solver.Solution s = solver.solve( region.getRegion() ) ;
        region.setAreaProblemSolutionByX( s ) ;
        double result = region.getAreaProblemSolutionByX().evaluate();

        // Check answer
        assert(Utilities.equalDoubles(result, 2.66667));
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
}

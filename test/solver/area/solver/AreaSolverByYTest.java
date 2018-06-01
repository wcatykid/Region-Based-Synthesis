package solver.area.solver;

import java.util.Set;
import java.util.Vector;
import org.junit.Test;

import exceptions.DomainException;
import exceptions.SolvingException;
import representation.regions.Region;
import solver.area.AreaSolutionByY;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import solver.area.regionComputer.RegionExtractor;
import solver.area.regionComputer.TextbookProblemRegionExtractor;
import solver.area.solver.AreaSolverByY;
import solver.problemRegions.ProblemRegionIdentifier;
import utilities.Assertions;
import utilities.StringUtilities;

public class AreaSolverByYTest
{
    @Test
    public void test() throws DomainException
    {
        String testName = "Region Creation w.r.t. Y (for solving with Y)";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runProblem( 1, "{ 2-.1*x+.75*Sin[x] ; 1.2-.5*x+.8*Cos[1.5*x] } [0, 7] <1000> //", true ) ;
        runProblem( 1, "{ ((x/3)-2)^2 ; 2-((x/3)-2)^2 } [3, 8] <6.22222222> //", false ) ;
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runProblem(int indent, String pStr, boolean expectFailedInversion ) throws DomainException
    {
        System.out.println(StringUtilities.generateTestStartString(pStr, indent));

        // Create Problem
        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);

        // Extract the appropriate solution regions
        RegionExtractor extractor = new TextbookProblemRegionExtractor(problem);

        Vector<Region> regions = extractor.getRegions();

        // Identify the exact region(s) to solve
        ProblemRegionIdentifier identifier = new ProblemRegionIdentifier(problem);
        
        Set<Region> solutionRegions = identifier.getProblemRegions(regions);

        AreaSolverByY solver = new AreaSolverByY();
        AreaSolutionByY solution = null;
        try
        {
            solution = (AreaSolutionByY)solver.solve(solutionRegions);
        }
        catch (SolvingException e)
        {
            System.err.println("Solving by Y failed via exception.");
            e.printStackTrace();
        }
        
        if( solution.getFailedInversionFlag() && expectFailedInversion )
        {
        	System.out.println( "No solution found due to failure to invert a function, but there exists a method to invert the function." ) ;
        }
        else
        {
        	System.out.println("Solution: " + solution);
            
	        // Check answer
	        Assertions.Assert(solution.evaluate(), problem.getAnswer());
        }

        System.out.println(StringUtilities.generateTestEndString(pStr, indent));
    }
}

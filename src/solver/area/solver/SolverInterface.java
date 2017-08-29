package solver.area.solver;

import java.util.Set;
import java.util.Vector;

import exceptions.SolvingException;
import representation.regions.Region;
import solver.area.AreaSolution;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import solver.area.regionComputer.RegionExtractor;
import utilities.Utilities;

/**
 * Given basic string input, parse, solve, and return a RegionAggregator
 */
public class SolverInterface
{
    protected int _numProblems;
    
    public SolverInterface()
    {
        _numProblems = 0;
    }

    /**
     * @param sProblem -- String statement of a problem (defines a region with, possibly, domain)
     * Passes the problem onto the main solver.
     */
    public void solve(String sProblem)
    {
        solve(AreaProblemParserTest.makeAreaProblem(sProblem));
    }

    public double solve(TextbookAreaProblem problem)
    {
        // we track the number of problems we have solved
        _numProblems++;
        
        RegionExtractor extractor = new RegionExtractor(problem);

        Vector<Region> regions = extractor.getRegions();
        
        ProblemRegionIdentifier identifier = new ProblemRegionIdentifier(problem);
        
        Set<Region> solutionRegions = identifier.getProblemRegions(regions);
        
        for (Region region : solutionRegions)
        {
            System.out.println(region);
        }

        //
        // Given the regions, solve each by X and Y; combine those solution ins 
        //
        AreaSolverByX solverX = new AreaSolverByX();
        AreaSolution byX = null;
        try
        {
            byX = (AreaSolution)solverX.solve(solutionRegions);
        }
        catch (SolvingException e)
        {
            System.err.println("Failed to solve by X");
            e.printStackTrace();
        }
        
        AreaSolverByX solverY = new AreaSolverByX();
        AreaSolution byY = null;
        try
        {
            byY = (AreaSolution)solverY.solve(solutionRegions);
        }
        catch (SolvingException e)
        {
            System.err.println("Failed to solve by Y");
            e.printStackTrace();
        }
        
        //
        // Combine the individual region solutions together for a final solution
        //
        double computedAnswerX = byX.evaluate();
        double computedAnswerY = byY.evaluate();
        
        if (!Utilities.equalDoubles(computedAnswerX,  problem.getAnswer()))
        {
            System.err.println("Expected computed answer to equate to real answer; did not: computed(" +
                               computedAnswerX + ") Expected (" + problem.getAnswer() + ")");
        }
        
        if (!Utilities.equalDoubles(computedAnswerY,  problem.getAnswer()))
        {
            System.err.println("Expected computed answer to equate to real answer; did not: computed(" +
                               computedAnswerY + ") Expected (" + problem.getAnswer() + ")");
        }
        
        if (!Utilities.equalDoubles(computedAnswerY,  computedAnswerY))
        {
            System.err.println("Solution by X and Y do not equate (" +
                               computedAnswerX + ") vs. (" + computedAnswerY + ")");
        }
        
        return computedAnswerX;
    }
}
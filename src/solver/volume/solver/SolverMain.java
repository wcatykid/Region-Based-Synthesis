package solver.volume.solver;

import java.util.Set;
import java.util.Vector;

import exceptions.DomainException;
import exceptions.SolvingException;
import representation.regions.Region;
import solver.Main;
import solver.area.AreaSolution;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParser;
import solver.area.parser.AreaProblemParserTest;
import solver.area.regionComputer.RegionExtractor;
import solver.problemRegions.ProblemRegionIdentifier;
import solver.volume.AxisOfRevolution;
import solver.volume.TextbookVolumeProblems;
import solver.volume.parser.VolumeProblemParser;
import utilities.Pair;
import utilities.Utilities;

/**
 * Given basic string input, parse, solve, and return a RegionAggregator
 */
public class SolverMain extends Main
{
    protected int _numUniqueProblems;

    public SolverMain()
    {
        super();

        _numUniqueProblems = 0;
    }

    /**
     * @param sProblem -- String statement of a problem (defines a region with, possibly, domain)
     * Passes the problem onto the main solver.
     */
    public void solve(String sProblem) throws DomainException
    {
        solve(SolverMain.makeVolumeProblem(sProblem));
    }

    public void solve(TextbookVolumeProblems problem)
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
        // A single problem may be composed of many subregions; therefore
        // solving involves is based on the axis of symmetries (and not regions, explicitly)
        //
        for (int i = 0; i < problem.numProblems(); i++)
        {
            Pair<AxisOfRevolution, Double> aas = problem.getAxisAnswerPair(i);

            solve(solutionRegions, aas.getFirst(), aas.getSecond());
        }
    }

    /**
     * 
     * @param regions -- particular solution regions
     * @param axis -- axis of revolution
     * @param answer -- overall (validation) sum we expect
     */
    private void solve(Set<Region> regions, AxisOfRevolution axis, Double answer)
    {
        //
        // Check for a valid axis (does not pass through the region)
        //
        if (!validAxis(regions, axis)) throw new IllegalArgumentException("Invalid Axis");

        //
        // Determine allowable method of solving with respect to the axis of symmetry
        //
        // Discs: The axis of revolution is perpendicular to the axis of integration (Perpen-discular)
        // Shells: The axis of revolution is parallel to the axis of integration (Para-shell)
        //

        //
        // (a) horizontal axis and \int R dx : Discs
        //
        if (axis.isHorizontal())
        {
            SolveByDiscs solver = null;
            double computed = -1;

// CTA: Supporting compilation
//            try
//            {
//                solver = new SolveByDiscs(regions, axis);
//
//                computed = solver.solve();
//            }
//            catch (SolvingException e)
//            {
//                System.err.println("Failed to solve by X with Discs");
//                e.printStackTrace();
//            }

            //
            // Compare expected and computed
            //
            if (!Utilities.looseEqualDoubles(computed,  answer))
            {
                System.err.println("Computed answer does not equate to real answer: computed(" +
                        computed + ") Expected (" + answer + ")");
            }

            //        return computed;
        }
        //
        // (b) vertical axis and \int R dx : Shells
        //
        else if (axis.isVertical())
        {
// CTA: Mark to support compilation
//            SolveByShells solver = null;
//            double computed = -1;
//
//            try
//            {
//                solver = new SolveByShells(regions, axis);
//
//                computed = solver.solve();
//            }
//            catch (SolvingException e)
//            {
//                System.err.println("Failed to solve by X with Shells");
//                e.printStackTrace();
//            }
//
//            //
//            // Compare expected and computed
//            //
//            if (!Utilities.looseEqualDoubles(computed,  answer))
//            {
//                System.err.println("Computed answer does not equate to real answer: computed(" +
//                        computed + ") Expected (" + answer + ")");
//            }
//
//            //        return computed;
        }
        //
        // For safety
        //
        else
        {
            System.err.println("Axis is neither horizontal nor vertical: " + axis);
        }
    }

    /**
     * Ansure this axes of revolution DOES NOT pass through any region
     * @param regions -- solution regions
     * @param axis -- axis of revolution
     * @return {@code true} if the axis DOES NOT pass through the region
     *         {@code false} otherwise if the construction is valid
     */
    private boolean validAxis(Set<Region> regions, AxisOfRevolution axis)
    {
        for (Region region : regions)
        {
            if (region.linePassesThrough(axis.getAxis())) return false;
        }

        return true;
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
package solver.area.solver;

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import exceptions.DomainException;
import math.integral.DefiniteIntegral;
import representation.bounds.Bound;
import representation.bounds.functions.BoundedFunction;
import representation.bounds.functions.DifferenceBoundedFunction;
import representation.regions.Region;
import solver.Solution;
import solver.Solver;
import solver.area.AreaSolution;

/**
 * This class will solve "area between curves" problems assuming the region is topologically conjugate to a square.
 * That is, there are no functions that intersect before the end of the region (from left to right).
 */
public class AreaSolverByX extends Solver
{
    public AreaSolverByX()
    {
        super();
    }

    /**
     * @param region -- a region with aggregate information
     * Will solve this "area between curves" problem with respect to x, then y (if possible)
     */
    @Override
    public Solution solve(Region region)
    {
        // Solve with respect to x
        AreaSolution solutionByX = solveWithRespectToX(region);

        if (solutionByX != null)
        {
            System.out.println("Solution (By x): " + solutionByX);

            double value = solutionByX.evaluate();
            System.out.println("Evaluated: " + value);
        }

//        // Solve with respect to x
//        AreaSolution solutionByY = solveWithRespectToY(region);
//
//        if (solutionByY != null)
//        {
//            solutions.add(solutionByY);        
//            System.out.println("Solution (By y): " + solutionByY);
//
//            double value = solutionByY.evaluate();
//            System.out.println("Evaluated: " + value);
//        }

        return solutionByX;
    }

    /**
     * @param agg -- an aggregator containing a legitimate region
     * @return solve the "Area Between Curves" problem with respect to X; save this solution into the aggregator.
     * 
     * Split the region vertically into several sub-regions at each of the bounds of the piecewise defined functions.
     */
    public AreaSolution solveWithRespectToX(Region region)
    {
        // Collect all important x-values used to split a region into atomic regions
        Vector<Double> xs = collectAllX(region);

        // Create the atomic regions as integral expressions
        Vector<DefiniteIntegral> atomRegions = buildAtomicRegions(region, xs);

        // The atomic regions constitute a solution to this "area between curves" problem.
        AreaSolution solution = new AreaSolution(atomRegions);

        return solution;
    }

    /**
     * @param region -- a region
     * @return THe set of all x-values that will bookend atomic regions
     */
    private Vector<Double> collectAllX(Region region)
    {
        // Collect the left, right endpoints as well as the interior x-values to split sub-atomic
        Set<Double> interiorXs = region.interiorPiecesByX();

        // Collect extrema points; needed?
        //interiorXs.addAll(interiorSplitXbyPieces(region));

        // Insert left and right endpoints into the list
        interiorXs.add(region.leftX());
        interiorXs.add(region.rightX());

        // Wanted a sorted list; make it
        Vector<Double> sortedXs = new Vector<Double>(interiorXs);

        // Order the x-values, to split the region
        Collections.sort(sortedXs);

        return sortedXs;
    }

    /**
     * @param region -- a region
     * @param xs -- pertinent x-values that will bookend our atomic regions (bounds of integration)
     * @return the set of integral describing these x-value bounds
     * 
     * 
     */
    private Vector<DefiniteIntegral> buildAtomicRegions(Region region, Vector<Double> xs)
    {
        Vector<DefiniteIntegral> atoms = new Vector<DefiniteIntegral>();

        //
        // Construct the atomic regions for this particular solution and their associated integral
        //
        // Atomic regions are bookends [ (a,b); (b,c); (c,d) ]: a -------- b ----- c ---------d
        //
        for (int x_index = 0; x_index < xs.size() - 1; x_index++)
        {
            // Establish the atomic region
            double left = xs.get(x_index);
            double right = xs.get(x_index + 1);

            if (left == right) System.err.println("left == right: " + left + " " + right);

            // Build the function: f - g == top - bottom
            Bound topF = null;
            Bound bottomF = null;
            try
            {
                topF = region.getFirstNonVerticalTopBound(left);
                bottomF = region.getFirstNonVerticalBottomBound(left);
            }
            catch (DomainException e)
            {
                System.err.println("Attempt to identify the proper bound for top / bottom failed due to domain " + e);
                e.printStackTrace();
            }

            // Check that we have actual functions and not other Bound types
            if (!(topF instanceof BoundedFunction)) System.err.println("Top function is not a function! " + topF);
            if (!(bottomF instanceof BoundedFunction)) System.err.println("Bottom function is not a function! " + bottomF);

            // Create (top - bottom)(x)
            DifferenceBoundedFunction f = null;
            try
            {
                f = new DifferenceBoundedFunction((BoundedFunction)topF, (BoundedFunction)bottomF);
            }
            catch (DomainException e)
            {
                System.err.println("Variable Disagreement: " + topF + " " + bottomF);
                e.printStackTrace();
            }
            f.setDomain(left, right); // Needed?

            //
            // Create the integral expression
            //
            DefiniteIntegral atom = new DefiniteIntegral(left, right, f, f.variableType());

            atoms.add(atom);
        }

        return atoms;
    }
}
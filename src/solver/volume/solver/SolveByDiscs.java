package solver.volume.solver;

import java.util.Set;

import math.integral.DefiniteIntegral;
import representation.bounds.functions.StringBasedFunction;
import representation.regions.Region;
import solver.volume.AxisOfRevolution;

/**
 * Solve using the disc / washer method of volumes of solids of revolution
 * 
 * @version 1.0 solves assuming an axis is vertical and integrating with respect to x.
 * 
 * @author calvi
 *
 */
public class SolveByDiscs
{
    protected Set<Region> _regions;
    protected AxisOfRevolution _axis;
    protected DefiniteIntegral _solution;

    public static double ERROR_VALUE = Double.NEGATIVE_INFINITY;
    
    public SolveByDiscs(Set<Region> regions, AxisOfRevolution axis)
    {
        _regions = regions;
        _axis = axis;
    }

    public double solve()
    {
        if (_axis.isVertical()) return solveWithVerticalAxis();
//        else if (_axis.isHorizontal()) return solveWithHorizontalAxis();

        return ERROR_VALUE;
    }
    
    public double solveWithVerticalAxis()
    {
        double sum = 0;
        for (Region region : _regions)
        {
            sum += solveWithVerticalAxis(region);
        }

        //
        // Multiply by the PI constant necessary for this technique (area of a circle)
        //
        return Math.PI * sum;
    }
    
    /**
     * Solve using discs with a 
     * 
     * @param region
     * @return
     */
    private double solveWithVerticalAxis(Region region)
    {
        double result = ERROR_VALUE;
        
        //
        // Determine outer and inner functions
        //                                        axis
        //     |              /|                 |\            |
        //     |             / |                 | \           |
        //     |    inner   /  | outer     outer |  \ inner    |
        //     |           /___|                 |___\         |
        //    axis   
        
        //
        // The region under consideration is 'simple' because it has already been processed
        // Hence, acquire the 'closest' and 'furthest' functions of this region. 
        //
        StringBasedFunction closest = region.computeClosestHorizontally(_axis);
        
        
        return result;
    }
}
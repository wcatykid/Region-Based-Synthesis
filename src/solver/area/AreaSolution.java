package solver.area;

import java.util.Vector;

import math.integral.DefiniteIntegral;
import solver.Solution;

/**
 * A way to represent a solution for "area between curve" problems
 */
public class AreaSolution extends Solution
{
    public AreaSolution()
    {
        super();
    }

    public AreaSolution(Vector<DefiniteIntegral> atoms)
    {
        super(atoms);
    }
}

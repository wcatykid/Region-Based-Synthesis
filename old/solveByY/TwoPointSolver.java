package solver.area.solver.solveByY;

import representation.Point;
import representation.bounds.Bound;
import representation.bounds.PointBound;
import representation.regions.LeftRight;
import representation.regions.Region;
import representation.regions.TopBottom;
import utilities.Utilities;

public class TwoPointSolver
{
    /**
     * @param inRegion
     * @param bottomAnchor
     * @param topAnchor
     * @return the inverted region when the original region is defined by two corner points:
     * 
     *       topAnchor
     *        /|
     *       / /
     *      / /
     *      /
     *      bottomAnchor
     */
    public static Region handleTwoCornerPoints(Region inRegion, Point bottomAnchor, Point topAnchor)
    {
        //
        // The anchors map directly to the left / right point bounds
        //        
        LeftRight left = new LeftRight(new PointBound(bottomAnchor));
        LeftRight right = new LeftRight(new PointBound(topAnchor));

        //
        // The actual bottom / top bounds and their inverses
        //
        Bound topBound = inRegion.getTop().lastBound();
        Bound bottomBound = inRegion.getBottom().lastBound();

        // Dictate the domain of the top and bottom according to their y-values
        Bound topInverse = topBound.inverse();
        Bound bottomInverse = bottomBound.inverse();
        // No need to invert the domain since the inverse procedures did that already
        //bottomInverse.setDomain(bottomAnchor.getY(), topAnchor.getY());
        //topInverse.setDomain(bottomAnchor.getY(), topAnchor.getY());

        // Determine whether the top -> right, bottom -> left or reversed
        // Evaluate a midpoint to see which is smaller (establishing sides of the functions)
        double midY = Utilities.midpoint(bottomAnchor.getY(), topAnchor.getY());
        double bottomInverseY = bottomInverse.evaluateAtPoint(midY);
        double topInverseY = topInverse.evaluateAtPoint(midY);

        TopBottom bottom = null;
        TopBottom top = null;
        if (bottomInverseY < topInverseY)
        {
            bottom = new TopBottom(bottomInverse);
            top = new TopBottom(topInverse);
        }
        else if (topInverseY < bottomInverseY)
        {
            bottom = new TopBottom(topInverse);
            top = new TopBottom(bottomInverse);
        }
        else
        {
            System.err.println("This is a major problem since the functions indicate an intersection at a midpoint");
        }

        return new Region(left, top, right, bottom);
    }
}

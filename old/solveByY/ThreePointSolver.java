package solver.area.solver.solveByY;

import java.util.Vector;

import representation.Point;
import representation.bounds.Bound;
import representation.bounds.PointBound;
import representation.regions.LeftRight;
import representation.regions.Region;
import representation.regions.TopBottom;
import utilities.Utilities;

public class ThreePointSolver
{
    /**
     * @param inRegion -- a region
     * @param bottomAnchor -- bottom (and left-most) point of the region
     * @param topAnchor -- top (and right-most) point of the region
     * @param corners -- (ordered) corners of inRegion
     * @return the inverted region when the original region is defined by three corner points (triangular):
     * 
     *       topAnchor        topAnchor      topAnchor  topAnchor     topAnchor               topAnchor   topAnchor             topAnchor
     *          /|             |\          _____+      _____+            +                        +          +__________
     *         / |             | \        |    /       \    |            |\                      /|           \        /            /\
     * Bottom /  |     Bottom  |  \       |   /         \   |            | \                    / |            \      /            /  \ 
     *        \  |             |  /       |  /           \  |            |  \                  /  |             \    /            /    \
     *         \ |             | /        | /             \ |            |   \                /   |              \  /            /      \
     *          \|             |/         |/               \|            +____\              +____|               \/             +_______\
     *     bottomAnchor    bottomAnchor  bottomAnchor   bottomAnchor  bottomAnchor   bottomAnchor           bottomAnchor  bottomAnchor
     */
    public static Region handleTriangularRegion(Region inRegion, Point bottomAnchor, Point topAnchor, Vector<Point> corners)
    {
        //
        // One of the sides is linear; find it and handle it.
        //
        if (inRegion.getLeft().isVertical())
        {
            return handleLeftVertical(inRegion, bottomAnchor, topAnchor);
        }
        else if (inRegion.getRight().isVertical())
        {
            return handleSingleBottomTriangularRegion(inRegion, bottomAnchor, topAnchor);
        }
        else if (inRegion.getBottom().lastBound().isHorizontal())
        {
            return handleSingleBottomTriangularRegion(inRegion, bottomAnchor, topAnchor);
        }
        else if (inRegion.getTop().lastBound().isHorizontal())
        {
            return handleSingleBottomTriangularRegion(inRegion, bottomAnchor, topAnchor);
        }
        else
        {
            System.err.println("Triangular region does not maintain a linear bound");
        }
    }        

    /**
     * @param inRegion -- a region
     * @param bottomAnchor -- bottom (and left-most) point of the region
     * @param topAnchor -- top (and right-most) point of the region
     * @return the inverted region when the original region is defined by three corner points (triangular):
     * 
     *     topAnchor      topAnchor   topAnchor    
     *         |\          _____+         +     
     *         | \        |    /          |\    
     * Bottom  |  \       |   /           | \   
     *         |  /       |  /            |  \  
     *         | /        | /             |   \  
     *         |/         |/              +____\ 
     *     bottomAnchor    bottomAnchor  bottomAnchor
     */
    private static Region handleLeftVertical(Region inRegion, Point bottomAnchor, Point topAnchor)
    {
        //
        // One of the left / right may be 
        //
        LeftRight left = new LeftRight(new PointBound(bottomAnchor));
        LeftRight right = new LeftRight(new PointBound(topAnchor));

        
        
        //
        // In all cases, we have the (original) left becoming the reoriented bottom
        //
        // Invert the left segment
        TopBottom bottom = (TopBottom)inRegion.getLeft().invert();
        TopBottom top = (TopBottom)inRegion.getLeft().invert();
        

        
        
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

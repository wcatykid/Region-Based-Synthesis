package solver.area.solver.solveByY;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import exceptions.DomainException;
import exceptions.RepresentationException;
import exceptions.SolvingException;
import math.integral.DefiniteIntegral;
import representation.MyVector;
import representation.Point;
import representation.bounds.Bound;
import representation.bounds.PointBound;
import representation.bounds.functions.BoundedFunction;
import representation.bounds.functions.DifferenceBoundedFunction;
import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import representation.bounds.segments.VerticalLineSegment;
import representation.regions.LeftRight;
import representation.regions.Region;
import representation.regions.TopBottom;
import solver.Solution;
import solver.Solver;
import solver.area.AreaSolution;
import solver.area.solver.AreaSolverByX;
import utilities.Assertions;
import utilities.Pair;
import utilities.Utilities;

/**
 * This class will solve "area between curves" problems assuming the region is topologically conjugate to a square.
 * That is, there are no functions that intersect before the end of the region (from left to right).
 */
public class AreaSolverByY extends Solver
{
    public AreaSolverByY()
    {
        super();
    }

    /**
     * @param region -- a region
     * @return the solution to this problem with respect to y (orthogonal to standard solution: by x)
     *  Thus solving this "area between curves" problem with respect to y
     */
    @Override
    public Solution solve(Region region)
    {
        return solveWithRespectToY(region);
    }

    /**
     * @param region -- a region (easily solvable wrt X)
     * @return a set of sub-regions that should be solvable using the same by-X algorithms using inverse functions
     *    Algorithm:
     *        (1) Find all internal maxima and minima of top and bottom functions
     *        (2) Find all split points (connections at piece-wise parts)
     *        (3) Draw verticals from each of these extreme points (this is implicit)
     *        (4) Find f^(-1) and g^(-1) (can we determine if these are symbolically reasonable?)
     *        (5) For n extreme values, construct the n + 1 regions for solving 
     */
    protected Solution solveWithRespectToY(Region region)
    {
        //
        // (1) Find all internal maxima and minima of top and bottom functions
        // (2) Find all split points (connections at piece-wise parts)
        //
        Vector<Double> xs = collectSplitsByX(region);

        //
        // After splitting by X, convert the regions to be solvable with respect to X.
        //
        Vector<Region> withRespectToX = constructRegions(region, xs);

        // Now that the regions are legitimate with respect to X, we solve using the existing solver algorithm (by X).
        AreaSolverByX solver = new AreaSolverByX();

        AreaSolution byX = null;
        try { byX = (AreaSolution) solver.solve(withRespectToX); }
        catch (SolvingException e) { e.printStackTrace(); }

        return byX;
    }

    /**
     * @param region -- a region
     * @return a set of x-values where we will split this region: extrema and piece-wise split-points
     */
    private Vector<Double> collectSplitsByX(Region region)
    {
        // Extrema
        Set<Double> xs = region.extremaByX();

        // Piece-wise split points
        /**
         * An inverted region shifts these responsibilities 90 degrees:
         *                top                                                     Right (Greatest y)
         *         ------------------------                                  ---------------------
         *        |                        |                                |                      |
         *        |                        |            possibly            |                      |
         * Left   |                        | Right     ============> Bottom |                      | top
         *        |                        |                      (least X) |                      | (greatest X)
         *        |                        |                                |                      |
         *         ------------------------                                  ----------------------
         *                Bottom                                                    Left (least y)
         */
        xs.addAll(region.interiorPiecesByX()); // Need this for the region code to work for 'inverted' regions'

        // Sort and return
        Vector<Double> ordered = new Vector<Double>(xs);
        Collections.sort(ordered);

        return ordered;
    }

    /**
     * @param region -- a region
     * @param xs -- pertinent x-values that will bookend our atomic regions (bounds of integration); these values become the 'top' and 'bottom'
     * @return the set of 'horizontally-based regions' for solving
     */
    private Vector<Region> constructRegions(Region region, Vector<Double> xs)
    {
        //
        // Construct all simple regions (in which all regions have at most one function or bound on each side)
        //
        Vector<Region> simpleRegions = constructSimpleRegions(region, xs);

        //
        // Re-orient those regions to be with respect to y 
        //
        return reorient(simpleRegions);
    }

    /**
     * @param region -- a set of regions with function defined in terms of X
     * @param xs -- the set of interior x-values for which this region will be split (along the X-values)
     * @return the set of simple regions from a, x1, x2, ...xn, b defined from: [a, x1] ; [x1, x2]; ... ; [xn, b]
     */
    private Vector<Region> constructSimpleRegions(Region region, Vector<Double> xs)
    {
        //
        // Bookend the interior points with the left, right values of the region; the given points are interior
        //
        Vector<Double> allXs = new Vector<Double>(xs);
        allXs.add(0, region.leftX());
        allXs.add(region.rightX());

        Vector<Region> simpleRegions = new Vector<Region>();
        //
        // Construct the rotated regions
        //
        // Atomic regions are book-ended by X [ (a,b); (b,c); (c,d) ]: a -------- b ----- c ---------d
        //
        //  The resulting regions will have [bottom; top] = [a,b] ; [b,c] ; [c,d]
        //                 The left / right will refer to f^(-1) and g^(-1) of the original region 
        //
        for (int x_index = 0; x_index < allXs.size() - 1; x_index++)
        {
            //
            // Establish the bounds (domain) of the simple region
            //
            double left_x = allXs.get(x_index);
            double right_x = allXs.get(x_index + 1);
            Domain interval = new Domain(left_x, right_x);

            if (left_x == right_x) System.err.println("left == right: " + left_x + " " + right_x);

            // Build the function: f - g == top - bottom
            Bound topF = null;
            Bound bottomF = null;
            try
            {
                topF = region.getFirstNonVerticalTopBound(left_x);
                bottomF = region.getFirstNonVerticalBottomBound(left_x);
            }
            catch (DomainException e)
            {
                System.err.println("Attempt to identify the proper bound for top / bottom failed due to domain " + e);
                e.printStackTrace();
            }

            // Check that we have actual functions and not other Bound types
            if (!(topF instanceof BoundedFunction)) System.err.println("Top function is not a function! " + topF);
            if (!(bottomF instanceof BoundedFunction)) System.err.println("Bottom function is not a function! " + bottomF);

            //
            //
            // Create the simple region
            //
            //

            //
            // Single Top Function
            //
            Bound topBound = (Bound)topF.clone();
            topBound.setDomain(interval);
            TopBottom top = new TopBottom(topBound);

            //
            // Single Bottom Function
            //
            Bound bottomBound = (Bound)bottomF.clone();
            bottomBound.setDomain(interval);
            TopBottom bottom = new TopBottom(bottomBound);

            //
            // Left Bound
            //
            // Special case of first simple region's left bound: copy original; otherwise, use vertical
            LeftRight left = null;
            if (x_index == 0) left = (LeftRight)region.getLeft().clone();
            else
            {
                Point botPoint = new Point(left_x, bottomF.evaluateAtPoint(left_x));
                Point topPoint = new Point(left_x, topF.evaluateAtPoint(left_x));

                try
                {
                    left = new LeftRight(new VerticalLineSegment(botPoint, topPoint));
                }
                catch (RepresentationException e) { e.printStackTrace(); }
            }
            //
            // Right Bound
            //
            LeftRight right = null;
            // Special case of right simple region's right bound: copy original, otherwise, use vertical
            if (x_index == xs.size()) right = (LeftRight)region.getRight().clone();
            else
            {
                Point botPoint = new Point(right_x, bottomF.evaluateAtPoint(right_x));
                Point topPoint = new Point(right_x, topF.evaluateAtPoint(right_x));

                try
                {
                    right = new LeftRight(new VerticalLineSegment(botPoint, topPoint));
                }
                catch (RepresentationException e) { e.printStackTrace(); }
            }

            // Create the simple region
            simpleRegions.add(new Region (left, top, right, bottom));
        }

        return simpleRegions;
    }

    /**
     * @param inRegion -- a set of regions with respect to X
     * @return a re-oriented region with respect to Y 
     */
    private Vector<Region> reorient(Vector<Region> regionsByX)
    {
        Vector<Region> regionsByY = new Vector<Region>();

        for (Region regionByX : regionsByX)
        {
            regionsByY.add(reorient(regionByX));
        }

        return regionsByY;
    }

    /**
     * @param inRegion -- a region with respect to X
     * @return a re-oriented region with respect to Y 
     */
    private Region reorient(Region inRegion)
    {
        // Compute the four corner points of the input simple region
        Vector<Point> corners = inRegion.getCorners();

        // Acquire the top and bottom anchor points.
        Point bottomAnchor = getBottomAnchor(corners);
        Point topAnchor = getTopAnchor(corners);

        //
        // Handle special case where the region is defined by two points: { x^2 ; x^3 }
        //
        if (corners.size() == 2) return TwoPointSolver.handleTwoCornerPoints(inRegion, bottomAnchor, topAnchor);        

        //
        // Order the corners clockwise using the bottom anchor as a center.
        //
        corners = orderCornersByAnchors(bottomAnchor, corners);

        System.out.println(corners);

        //
        // Handle 3-4 point rectangular region
        //
        switch(corners.size())
        {
            case 3: return ThreePointSolver.handleTriangularRegion(inRegion, bottomAnchor, topAnchor, corners);
            case 4:
        }

        



        //
        // Moving counter-clockwise, cycle from the bottom anchor to the top anchor
        //

        //
        // Determine which is the next from the bottom point: either bottom, left, or even top
        //
        //        Point nextPoint = corners.get(1);
        //        // For bottom, the left-most
        //        if ()
        //        {
        //            
        //        }
        //        
        //        
        //        // Will the new bottom be a horizontal because the left is a vertical
        //        //
        //        //        
        //        if (inRegion.getLeft().isVertical())
        //        {
        //            
        //        }
        //        // 
        //        else
        //        {
        //            
        //        }











        //
        // Compute the inverted left and right bounds; formerly top / bottom (kind of)
        //
        LeftRight left = computeLeftInvertedRegionBound(inRegion, bottomAnchor);
        LeftRight right = computeRightInvertedRegionBound(inRegion, topAnchor);

        //
        // From the bottom-left, we pursue clockwise
        // Check the left or bottom  of the original region to see which to pursue
        //
        TopBottom bottom = computeBottomInvertedRegion(inRegion, bottomAnchor, topAnchor, corners);
        TopBottom top = computeTopInvertedRegion(inRegion, bottomAnchor, topAnchor, corners);

        return new Region(left, top, right, bottom);
    }


    /**
     * @return a set of n+1 points where the bottom anchor bookends all other points;
     *      The points will be clockwise starting at bottom anchor as a center.
     */
    protected Vector<Point> orderCornersByAnchors(Point anchor, Vector<Point> corners)
    {
        Vector<Point> ordered = new Vector<Point>();

        //
        // Make a parallel array of vectors using the anchor as origin for each vector
        //
        MyVector[] vecs = new MyVector[corners.size()];
        for (int c = 0; c < corners.size(); c++)
        {
            vecs[c] = new MyVector(anchor, corners.get(c));
        }

        //
        // Order the points based on the vectors: we order from greatest angle to least since angles are measured from standard position
        //
        // n^2 iteration over the list for sorting
        boolean[] marked = new boolean[corners.size()];
        for (int c = 0; c < corners.size(); c++)
        {
            int maxIndex = -1;
            for (int v = 0; v < corners.size(); v++)
            {
                if (!marked[v])
                {
                    // Give a valid, default value
                    if (maxIndex == -1)
                    {
                        maxIndex = v;
                    }
                    // Check for a greater angle
                    else if (vecs[maxIndex].angle() < vecs[v].angle())
                    {
                        maxIndex = v;
                    }
                }
            }
            // Nullify the largest vector
            marked[maxIndex] = true;

            // Place the point in the ordered list
            ordered.add(corners.get(maxIndex));
        }

        ordered.add(anchor);

        // The ordered list must contain the same point at the beginning and end of the list
        Assertions.Assert(corners.size() + 1, ordered.size());

        return ordered;
    }
























/**
 * @param inRegion -- the original region
 * @param bottomAnchor -- bottom Anchor point for inverted region
 * @param topAnchor -- top anchor point for the inverted region
 * @return the bottom of the inverted region
 * 
 *       topAnchor        topAnchor        topAnchor          topAnchor      topAnchor           topAnchor
 *          /|             |\                 |\                   /|           |\                     /|
 *         / |             | \                | \                 / |           | \                   / |
 * Bottom /  |     Bottom  |  \       Bottom  |  | Top  Bottom   |  |           |  \                 /  |
 *        \  |             |  /               |  |               |  |           |   \        Bottom |   / Top
 *         \ |             | /                | /                 \ |    Bottom  \   | Top          |  /
 *          \|             |/                 |/                   \|             \  |              | /
 *     bottomAnchor    bottomAnchor       bottomAnchor        bottomAnchor         \ |              |/
 *                                                                                  \|          ottomAnchor
 *                                                                             bottomAnchor
 *                                                                             
 * The bottom of the inverted region may consist of the bottom, left, and top of the original region.

 * The inverted left bound is either a point or a vertical.
 * The left bound is vertical if (1) the bottom 
 */
private LeftRight computeLeftInvertedRegionBound(Region inRegion, Point bottomAnchor) //, Point topAnchor, Point corners)
{
    Bound bottomBound = inRegion.getTop().getBounds().get(0);

    //
    // Simple point bound is the bottom anchor
    //
    if (!bottomBound.isHorizontal()) return new LeftRight(new PointBound(bottomAnchor));

    //
    // Horizontal becomes a vertical
    // The endpoints of the vertical are the bottomAnchor and a point that is equidistant 
    //
    double topY = bottomAnchor.getY() + Utilities.distance(bottomBound.leftBoundX(), bottomBound.rightBoundX());
    Point topPt = new Point(bottomAnchor.getX(), topY);

    VerticalLineSegment left = null;
    try { left = new VerticalLineSegment(bottomAnchor, topPt); } catch (RepresentationException e) { e.printStackTrace(); }

    return new LeftRight(left);
}

private LeftRight computeRightInvertedRegionBound(Region inRegion, Point topAnchor) // bottomAnchor, Point topAnchor, Point corners)
{
    Bound topBound = inRegion.getBottom().getBounds().get(0);

    //
    // Simple point bound is the bottom anchor
    //
    if (!topBound.isHorizontal()) return new LeftRight(new PointBound(topAnchor));

    //
    // Horizontal becomes a vertical
    // The endpoints of the vertical are the bottomAnchor and a point that is equidistant 
    //
    double bottomY = topAnchor.getX() - Utilities.distance(topBound.leftBoundX(), topBound.rightBoundX());
    Point bottomPt = new Point(topAnchor.getX(), bottomY);

    VerticalLineSegment right = null;
    try { right = new VerticalLineSegment(bottomPt, topAnchor); } catch (RepresentationException e) { e.printStackTrace(); }

    return new LeftRight(right);
}

/**
 * @param points -- a set of points
 * @return the point with smallest y-value (and secondarily smallest x-value)
 */
@SuppressWarnings("unchecked")
private Point getBottomAnchor(Vector<Point> points)
{
    Point minimum = points.get(0);

    for (int index = 1; index < points.size(); index++)
    {
        if (Utilities.equalDoubles(minimum.getY(), points.get(index).getY()))
        {
            if (minimum.getX() > points.get(index).getX())
            {
                minimum = points.get(index);
            }
        }
        else if (minimum.getY() > points.get(index).getY())
        {
            minimum = points.get(index);
        }
    }

    return minimum;
}

/**
 * @param points -- a set of points
 * @return the point with the largest y-value (and secondarily greatest x-value)
 */
@SuppressWarnings("unchecked")
private Point getTopAnchor(Vector<Point> points)
{
    Point maximum = points.get(0);

    for (int index = 1; index < points.size(); index++)
    {
        if (Utilities.equalDoubles(maximum.getY(), points.get(index).getY()))
        {
            if (maximum.getX() < points.get(index).getX())
            {
                maximum = points.get(index);
            }
        }
        else if (maximum.getY() < points.get(index).getY())
        {
            maximum = points.get(index);
        }
    }

    return maximum;
}

/**
 * @param inRegion -- the original region
 * @param bottomAnchor -- bottom Anchor point for inverted region
 * @param topAnchor -- top anchor point for the inverted region
 * @return the bottom of the inverted region
 * 
 *       topAnchor        topAnchor        topAnchor          topAnchor      topAnchor           topAnchor
 *          /|             |\                 |\                   /|           |\                     /|
 *         / |             | \                | \                 / |           | \                   / |
 * Bottom /  |     Bottom  |  \       Bottom  |  | Top  Bottom   |  |           |  \                 /  |
 *        \  |             |  /               |  |               |  |           |   \        Bottom |   / Top
 *         \ |             | /                | /                 \ |    Bottom  \   | Top          |  /
 *          \|             |/                 |/                   \|             \  |              | /
 *     bottomAnchor    bottomAnchor       bottomAnchor        bottomAnchor         \ |              |/
 *                                                                                  \|          ottomAnchor
 *                                                                             bottomAnchor
 *                                                                             
 * The bottom of the inverted region may consist of the bottom, left, and top of the original region.
 */
private TopBottom computeBottomInvertedRegion(Region inRegion, Point bottomAnchor, Point topAnchor, Vector<Point> corners)
{
    // topAnchor      
    //          |\   
    //          | \  
    //  Bottom  |  \  Top
    //          |  / 
    //          | /  
    //          |/   
    //     bottomAnchor 
    TopBottom bottom = singleBottom(inRegion, bottomAnchor, topAnchor, corners);
    if (bottom != null) return bottom;

    //         topAnchor
    //             /|
    //    bottom  / |
    //            \ |
    //             \|
    //         bottomAnchor  
    bottom = dualBottom(inRegion, bottomAnchor, topAnchor, corners);
    if (bottom != null) return bottom;

    //         topAnchor
    //             /|
    //            / |
    //    bottom |  |
    //            \ |
    //             \|
    //         bottomAnchor  
    bottom = triBottom(inRegion, bottomAnchor, topAnchor, corners);
    if (bottom != null) return bottom;


    System.err.println("Failed to compute the bottom of the inverted region.");

    return null;
}

/**
 * 
 * @param inRegion
 * @param bottomAnchor
 * @param topAnchor
 * @return
 * 
    //      topAnchor      
    //          |\   
    //          | \  
    //  Bottom  |  \  Top
    //          |  / 
    //          | /  
    //          |/   
    //     bottomAnchor 
    //
    // The only way a single bottom occurs:
          (1) if the left bound of the original region is a vertical line WITH the top anchor as the other end of the vertical
          (2) if the left bound of the original region is a vertical line WITH the top bound being horizontal
 */
private TopBottom singleBottom(Region inRegion, Point bottomAnchor, Point topAnchor, Vector<Point> corners)
{
    if (!inRegion.getLeft().isVertical()) return null;

    //  (1) if the left bound of the original region is a vertical line WITH the top anchor as the other end of the vertical
    if (topAnchor.equals(inRegion.getLeft().getMaximum()))
    {
        // The bottom of the inverted region becomes a horizontal function (from the vertical of the original region)
        StringBasedFunction horizontal = new StringBasedFunction(Double.toString(topAnchor.getX()));
        horizontal.setDomain(inRegion.getLeft().getMinimum().getY(), inRegion.getLeft().getMaximum().getY());

        return new TopBottom(horizontal);
    }
    // (2) if the left bound of the original region is a vertical line WITH the top bound being horizontal
    // This means that the topAnchor point is NOT the top of the vertical
    //               ----------- topAnchor
    //              |
    //              |
    //              |
    // bottomAnchor  ----------
    else if (inRegion.getTop().getBounds().get(0).isHorizontal())
    {        
        // Find the corner point with the shared (x, y) = (bottom.x, top.y)
        Point topVertical = null;
        for (Point point : corners)
        {
            if (point.equals(new Point(bottomAnchor.getX(), topAnchor.getY())))
            {
                topVertical = point;
                break;
            }
        }

        if (topVertical == null)
        {
            System.err.println("Problem finding correct point for vertical line.");
        }

        // The bottom of the inverted region becomes a horizontal function (from the vertical of the original region)
        StringBasedFunction horizontal = new StringBasedFunction(Double.toString(bottomAnchor.getX()));
        horizontal.setDomain(inRegion.getLeft().getMinimum().getY(), topVertical.getY());

        return new TopBottom(horizontal);
    }
    return null;
}

/**
 * @param inRegion -- the original region
 * @param bottomAnchor -- bottom Anchor point for inverted region
 * @param topAnchor -- top anchor point for the inverted region
 * @return the dual bottom of the inverted region consisting of bottom and top of the original region
 * 
 *             topAnchor 
 *                 /|     
 *        OrigTop / |     
 * Bottom        /  |     
 *               \  |     
 *        OrigBot \ |     
 *                 \|
 *             bottomAnchor                                                           
 */
// The only way a dual bottom occurs is if the right bound of the original region is a vertical line
// WITH the top anchor as the other end of the vertical
private TopBottom dualBottom(Region inRegion, Point bottomAnchor, Point topAnchor, Vector<Point> corners)
{

    // TODO: HERE

    //
    // The only way a single bottom occurs is if the left bound of the original region is a vertical line
    // WITH the top anchor as the other end of the vertical
    //
    if (inRegion.getLeft().isVertical())
    {
        if (topAnchor.equals(inRegion.getLeft().getMaximum()))
        {
            // The bottom of the inverted region becomes a horizontal function (from the vertical of the original region)
            StringBasedFunction horizontal = new StringBasedFunction(Double.toString(topAnchor.getX()));

            return new TopBottom(horizontal);
        }
    }

    return null;
}

/**
 * @param inRegion -- the original region
 * @param bottomAnchor -- bottom Anchor point for inverted region
 * @param topAnchor -- top anchor point for the inverted region
 * @return the dual bottom of the inverted region consisting of bottom and top of the original region
 * 
 *             topAnchor 
 *                 /|     
 *        OrigTop / |     
 * Bottom         | |     
 *                | |     
 *        OrigBot \ |     
 *                 \|
 *             bottomAnchor                                                           
 */
// The only way a dual bottom occurs is if the right bound of the original region is a vertical line
// WITH the top anchor as the other end of the vertical
private TopBottom triBottom(Region inRegion, Point bottomAnchor, Point topAnchor, Vector<Point> corners)
{

    // TODO: HERE



    return null;
}

//    
//    
//    /**
//     * @param -- a region
//     * @return an inverted region constructed as thus:
//     * 
//     * An inverted region shifts these responsibilities 90 degrees (since we lexicographically order by y-values)
//     *                top                                                       Right
//     *         ------------------------                                  ---------------------
//     *        |                        |                                |                      |
//     *        |                        |                                |                      |
//     * Left   |                        | Right     ============> Bottom |                      | top
//     *        |                        |                                |                      |
//     *        |                        |                                |                      |
//     *         ------------------------                                  ----------------------
//     *                Bottom                                                    Left
//     */
//    private Region handleSingleAtomicRegion(Region region)
//    {
//        
//    }

/**
 * @param inRegion -- the original region
 * @param bottomAnchor -- bottom Anchor point for inverted region
 * @param topAnchor -- top anchor point for the inverted region
 * @return the bottom of the inverted region
 * 
 *       topAnchor        topAnchor        topAnchor          topAnchor      topAnchor           topAnchor
 *          /|             |\                 |\                   /|           |\                     /|
 *         / |             | \                | \                 / |           | \                   / |
 * Bottom /  |     Bottom  |  \       Bottom  |  | Top  Bottom   |  |           |  \                 /  |
 *        \  |             |  /               |  |               |  |           |   \        Bottom |   / Top
 *         \ |             | /                | /                 \ |    Bottom  \   | Top          |  /
 *          \|             |/                 |/                   \|             \  |              | /
 *     bottomAnchor    bottomAnchor       bottomAnchor        bottomAnchor         \ |              |/
 *                                                                                  \|          ottomAnchor
 *                                                                             bottomAnchor
 *                                                                             
 * The bottom of the inverted region may consist of the bottom, left, and top of the original region.
 */
private TopBottom computeTopInvertedRegion(Region inRegion, Point bottomAnchor, Point topAnchor, Vector<Point> corners)
{
    // TODO
    //        
    //        // topAnchor      
    //        //          |\   
    //        //          | \  
    //        //  Bottom  |  \  Top
    //        //          |  / 
    //        //          | /  
    //        //          |/   
    //        //     bottomAnchor 
    //        TopBottom bottom = singleBottom(inRegion, bottomAnchor, topAnchor);
    //        
    //        if (bottom != null) return bottom;
    //        
    //        bottom = dualBottom(inRegion, bottomAnchor, topAnchor);
    //        
    //        if (bottom != null) return bottom;
    //        
    //        bottom = threeBottom(inRegion, bottomAnchor, topAnchor);
    //        
    //        if (bottom != null) return bottom;
    //        
    //        System.err.println("Failed to compute the bottom of the inverted region.");
    //        
    return null;
}
}
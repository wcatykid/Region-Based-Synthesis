package solver.area.solver;

import java.util.Collections;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import exceptions.DomainException;
import exceptions.RepresentationException;
import math.analysis.extrema.ExtremeValues;
import math.analysis.intersection.Intersection;
import math.integral.DefiniteIntegral;
import representation.Point;
import representation.bounds.Bound;
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
     *        (4) Construct the integral expressions.
     */
    private Solution solveWithRespectToY(Region region)
    {
        Double leftBound  = region.getLeft ().getMaximum().getX() ;
        Double rightBound = region.getRight().getMaximum().getX() ;

        //////////////////////////////////////////////////////////////////////
        // DISECTLEFT and DISECTRIGHT from Paper
        //////////////////////////////////////////////////////////////////////

        //Find all internal maxima and minima of top and bottom functions (and their associated second derivative)
        Vector<Pair<Double,Double>> topExtremaAndDir = getExtremaAndDir( region.getTop   () ) ;
        Vector<Pair<Double,Double>> botExtremaAndDir = getExtremaAndDir( region.getBottom() ) ;

        //Get all horizontals emanating from the top and bottom points of the left and right bounds
        //Note that the bottom horizontals will be redundant (but not problematically) if the bound is a point and not a vertical
        StringBasedFunction leftTopHorizontal  = new StringBasedFunction( new Double( region.getLeft ().getMaximum().getY() ).toString() ) ;
        StringBasedFunction rightTopHorizontal = new StringBasedFunction( new Double( region.getRight().getMaximum().getY() ).toString() ) ;
        StringBasedFunction leftBotHorizontal  = new StringBasedFunction( new Double( region.getLeft ().getMinimum().getY() ).toString() ) ;
        StringBasedFunction rightBotHorizontal = new StringBasedFunction( new Double( region.getRight().getMinimum().getY() ).toString() ) ;

        //TODO: add existence check before adding to the following vectors
        
        //Get all intersections between the horizontals and the bounds
        Vector<Point> initialIntersections = new Vector<Point>() ;
        getAllIntersections( region, leftTopHorizontal , leftBound, rightBound, initialIntersections ) ;
        getAllIntersections( region, rightTopHorizontal, leftBound, rightBound, initialIntersections ) ;
        getAllIntersections( region, leftBotHorizontal , leftBound, rightBound, initialIntersections ) ;
        getAllIntersections( region, rightBotHorizontal, leftBound, rightBound, initialIntersections ) ;
        
        Predicate<Pair<Double,Double>> lessThan    = p -> p.getFirst() < p.getSecond() ;
        Predicate<Pair<Double,Double>> greaterThan = p -> p.getFirst() > p.getSecond() ;
        
        Vector<Pair<Point,Point>> horizontals = new Vector<Pair<Point,Point>>() ;
        
        //TODO: getHorizontal doesn't yet verify that the horizontal is being drawn inside the region
        //      use the direction being drawn and whether the function is increasing or decreasing

        //Get the horizontal segment going from the top left to the right
        getHorizontalSegment( topExtremaAndDir, botExtremaAndDir, initialIntersections,
        		region.getLeft().getBound().leftBoundX(), region.getRight().getBound().rightBoundX(),
        		region.getLeft().getMaximum().getY(), lessThan, horizontals ) ;
        
        //Get the horizontal segment going from the top right to the left
        getHorizontalSegment( topExtremaAndDir, botExtremaAndDir, initialIntersections, 
        		region.getRight().getBound().rightBoundX(), region.getLeft().getBound().leftBoundX(),
        		region.getRight().getMaximum().getY(), greaterThan, horizontals ) ;
        
        //Get the horizontal segment going from the bottom left to the right
        //Note that this is redundant (but not problematically) if the left bound is a point, not a line.
        getHorizontalSegment( topExtremaAndDir, botExtremaAndDir, initialIntersections,
        		region.getLeft().getBound().leftBoundX(), region.getRight().getBound().rightBoundX(),
        		region.getLeft().getMinimum().getY(), lessThan, horizontals ) ;
        
        //Get the horizontal segment going from the bottom right to the left
        //Note that this is redundant (but not problematically) if the right bound is a point, not a line.
        getHorizontalSegment( topExtremaAndDir, botExtremaAndDir, initialIntersections, 
        		region.getRight().getBound().rightBoundX(), region.getLeft().getBound().leftBoundX(),
        		region.getRight().getMinimum().getY(), greaterThan, horizontals ) ;
        
        Vector<Point> finalIntersections = new Vector<Point>() ;
        for( Pair<Point,Point> p : horizontals )
        {
        	finalIntersections.add( p.getFirst() ) ;
        	finalIntersections.add( p.getSecond() ) ;
        }

        //////////////////////////////////////////////////////////////////////
        // DISECTTOP and DISECTBOTTOM from Paper
        //////////////////////////////////////////////////////////////////////
        
        //TODO: all of this
        
        //////////////////////////////////////////////////////////////////////
        // ADDTOPVERTICALS and ADDBOTTOMVERTICALS 
        //////////////////////////////////////////////////////////////////////

        //TODO: all of this

        //////////////////////////////////////////////////////////////////////
        // COMPUTE ALL SUBREGIONS
        //////////////////////////////////////////////////////////////////////
        
        //TODO: all of this (which is still magic)
        
        //////////////////////////////////////////////////////////////////////
        // INTEGRATE ALL SUB-REGIONS W.R.T y AND SUM
        //////////////////////////////////////////////////////////////////////

        //TODO: all of this

        return null ;
    }
    
    private Vector<Pair<Double,Double>> getExtremaAndDir( TopBottom region )
    {
        Vector<Pair<Double,Double>> extremaAndDir = new Vector<Pair<Double,Double>>() ;
        
        for( Bound bound : region.getBounds() )
        {
        	Vector<Double> extrema = new Vector<Double>( ExtremeValues.getInstance().extrema( bound ) ) ;
            Collections.sort( extrema ) ;
        	Vector<Double> extremaDir = ExtremeValues.getInstance().secondDerivativeAtPoints( bound, extrema ) ;
        	
        	extremaAndDir.addAll( IntStream
        	  .range( 0, extrema.size() )
        	  .mapToObj( i -> new Pair<Double,Double>( extrema.get( i ), extremaDir.get( i ) ) )
        	  .collect( Collectors.toList() ) ) ;
        }
        
        return extremaAndDir ;
    }
    
    private void getAllIntersections( Region region, StringBasedFunction horizontal, Double leftBound, Double rightBound, Vector<Point> intersections )
    {
        intersections.addAll( Intersection.getInstance().allIntersections( region.getLeft ().getBound(), horizontal, leftBound, rightBound ) ) ;
        intersections.addAll( Intersection.getInstance().allIntersections( region.getRight().getBound(), horizontal, leftBound, rightBound ) ) ;

        for( Bound bound : region.getTop().getBounds() )
        	intersections.addAll( Intersection.getInstance().allIntersections( bound, horizontal, leftBound, rightBound ) ) ;
        
        for( Bound bound : region.getBottom().getBounds() )
        	intersections.addAll( Intersection.getInstance().allIntersections( bound, horizontal, leftBound, rightBound ) ) ;
    }

    private void getHorizontalSegment( Vector<Pair<Double,Double>> topExtremaAndDir,
    								   Vector<Pair<Double,Double>> botExtremaAndDir,
    								   Vector<Point> intersections,
    								   Double beginX, Double endX, Double y,
    								   Predicate<Pair<Double,Double>> comparisonFunc,
    								   Vector<Pair<Point,Point>> horizontals )
    {
        Double firstNonExtremaIntersection = beginX ;

        for( Point p : intersections )
        {
        	if( 	comparisonFunc.test( new Pair<Double,Double>( p.getX(), firstNonExtremaIntersection ) )
        		&&	( ! Utilities.equalDoubles( p.getX(), firstNonExtremaIntersection ) )
        		&&	topExtremaAndDir.stream().filter( pair -> Utilities.equalDoubles( pair.getFirst(), p.getY() ) ).count() == 0
        		&&	botExtremaAndDir.stream().filter( pair -> Utilities.equalDoubles( pair.getFirst(), p.getY() ) ).count() == 0 )
        	{
        		firstNonExtremaIntersection = p.getX() ;
        	}
        }

        if( ! Utilities.equalDoubles( endX, firstNonExtremaIntersection ) )
        {
        	horizontals.add( new Pair<Point,Point>( new Point( endX, y ), new Point( firstNonExtremaIntersection, y ) ) ) ;
        }
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
        Vector<Double> allXs = new Vector<Double>();
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
                Point botPoint = new Point(left_x, bottomF.evaluateAtPoint(left_x).RealPart);
                Point topPoint = new Point(left_x, topF.evaluateAtPoint(left_x).RealPart);

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
            if (x_index + 1 == xs.size() - 1) right = (LeftRight)region.getRight().clone();
            else
            {
                Point botPoint = new Point(right_x, bottomF.evaluateAtPoint(right_x).RealPart);
                Point topPoint = new Point(right_x, topF.evaluateAtPoint(right_x).RealPart);

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
     * @param regions -- a set of simple regions
     * @return a solution (of definite integrals) describing the solution of this set of simple regions
     */
    private Solution solveSimpleRegions(Vector<Region> regions)
    {
        AreaSolution solution = new AreaSolution();
        for (Region region : regions)
        {
            solution.add(solveSimpleRegion(region));
        }
        return solution;
    }

    /**
     * @param regions -- a simple region
     * @return a solution (of definite integrals) describing the solution of this single simple regions
     * 
     *    (1) Find the corner points of the region
     *    (2) Order the corner points by Y (least to greatest; ties have lesser X values first)
     *    (3) Since there are, at maximum, 4 corner points, there are, at maximum 3 integral expressions; we find them by:
     *          For each adjacent pair <less, greater>
     *              if (less.Y != greater.Y)   // Horizontal that results in no area 
     *                 bound := get bound defined at its endpoints by [less, greater]
     *                 other := get bound defined by the range [less.Y, greater.Y] (there exists 2 such functions; we choose the 'other')
     *                 Determine 'sides' of the integral expression, result is:
     *                           \int_{less.Y}^{greater.Y}(right - left) dy
     */
    private Solution solveSimpleRegion(Region region)
    {
        //    (1) Find the corner points of the region
        // Compute the (up-to) four corner points of the input simple region
        Vector<Point> corners = region.getCorners();

        //    (2) Order the corner points by Y (least to greatest; ties have lesser X values first)
        // Order the points by smallest Y, ties broken by x
        Vector<Point> ordered = orderByY(corners);

        //    (3) Since there are, at maximum, 4 corner points, there are, at maximum 3 integral expressions; we find them by:
        //          For each adjacent pair <less, greater>
        //              if (less.Y != greater.Y)   // Horizontal that results in no area 
        //                 bound := get bound defined at its endpoints by [less, greater]
        //                 other := get bound defined by the range [less.Y, greater.Y] (there exists 2 such functions; we choose the 'other')
        //                 Determine 'sides' of the integral expression, result is:
        //                           \int_{less.Y}^{greater.Y}(right - left) dy
        AreaSolution solution = new AreaSolution();
        for (int index = 0; index < ordered.size() - 1; index++)
        {
            Point bottom = ordered.get(index);
            Point top = ordered.get(index + 1);

            if (Utilities.equalDoubles(bottom.getY(), top.getY()))
            {

                //
                // A horizontal line may lead to the top / bottom points being on a horizontal;
                //
                // If this horizontal line is the top of the region, ignore that last point
                //
                if (index + 2 >= ordered.size()) break;

                //
                // else, account for the bottom horizontal by skipping over that right point
                //
                index++;
                top = ordered.get(index + 1);
            }

            if (Utilities.equalDoubles(bottom.getY(), top.getY()))
            {
                System.err.println("The region is defined by 3 collinear points along a horizontal: " + bottom + " " + top);
            }

//            Bound defined = null;
//            try
//            {
//                defined = region.getBoundDefinedBy(bottom, top);
//            }
//            catch (DomainException e)
//            {
//                System.err.println("Region domain exception error: " + region);
//                e.printStackTrace();
//            }

            //
            // other := get bound defined by the range [less.Y, greater.Y] (there exists 2 such functions; we choose the 'other')
            //
            Pair<Bound, Bound> bounds =  region.getBoundsInRange(bottom, top);

            //
            //
            // Construct the actual solution integral for these two bounds:
            //     (1) Determine the left and right bounds
            //     (2) Construct the integral
            //

            //
            //     (1) Determine the left and right bounds
            //
            // Evaluate a midpoint to see which is smaller (establishing sides of the functions)
            //
            double midY = Utilities.midpoint(bottom.getY(), top.getY());
            double firstX = bounds.getFirst().evaluateAtPointByY(midY).RealPart;
            double secondX = bounds.getSecond().evaluateAtPointByY(midY).RealPart;

            Bound left = null;
            Bound right = null;
            if (firstX < secondX)
            {
                left = bounds.getFirst().inverse();
                right = bounds.getSecond().inverse();
            }
            else if (secondX < firstX)
            {
                left = bounds.getSecond().inverse();
                right = bounds.getFirst().inverse();
            }
            else
            {
                System.err.println("SolveByY::Inverse functions indicate an intersection at a midpoint: " + left + " " + right);
            }

            //
            //  (2) Construct the integral
            //
            //  Create the function:  (right - left)(y)
            //
            DifferenceBoundedFunction f = null;
            try
            {
                f = new DifferenceBoundedFunction((BoundedFunction)right, (BoundedFunction)left);
            }
            catch (DomainException e)
            {
                System.err.println("Variable Disagreement: " + right + " " + left);
                e.printStackTrace();
            }
            f.setDomain(bottom.getY(), top.getY()); // Needed?

            //
            // The integral expression
            //
            DefiniteIntegral atom = new DefiniteIntegral(bottom.getY(), top.getY(), f, f.variableType());

            // Add the integral to the overall solution
            solution.add(atom);
        }

        return solution;
    }


    /**
     * @param corners -- set of points
     * @return -- a set of points { (X, Y) } ordered by Y (and secondarily by X)
     */
    protected Vector<Point> orderByY(Vector<Point> corners)
    {
        Vector<Point> ordered = new Vector<Point>(corners);

        //
        // Selection sort of the points
        //
        for (int i = 0; i < ordered.size() - 1; i++)
        {
            // Find the minimum element in unsorted array
            int minIndex = i;
            for (int j = i + 1; j < ordered.size(); j++)
            {
                // if (arr[j] < arr[min_idx]) min_idx = j;
                if (yBasedLessThan(ordered.get(j), ordered.get(minIndex))) minIndex = j;
            }

            // Swap the found minimum element with the first element
            Collections.swap(ordered, minIndex, i);
        }

        return ordered;
    }

    /**
     * @param pt1 -- a point
     * @param pt2 -- a point
     * @return whether pt1 < pt2 based on Y (secondarily on X)
     */
    private boolean yBasedLessThan(Point pt1, Point pt2)
    {
        if (pt1.getY() < pt2.getY()) return true;
        else if (pt1.getY() > pt2.getY()) return false;

        return pt1.lessThan(pt2);
    }
}
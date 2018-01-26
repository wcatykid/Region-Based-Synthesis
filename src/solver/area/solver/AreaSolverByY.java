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
    	if( 	( region.getTop().getBounds().size() != 1 )
    		||	( region.getBottom().getBounds().size() != 1 ) )
    		throw new RuntimeException( "Solving w.r.t. Y currently only works when the top and bottom are made of a single function." ) ;
    	//Search for ".get( 0 )" below to see those places where we rely on this assumption.
    	
        Double leftBound  = region.getLeft ().getMaximum().getX() ;
        Double rightBound = region.getRight().getMaximum().getX() ;

        //Find all internal maxima and minima of top and bottom functions (and their associated second derivative)
        Vector<Pair<Double,Double>> topExtremaAndDir = getExtremaAndDir( region.getTop   (), leftBound, rightBound ) ;
        Vector<Pair<Double,Double>> botExtremaAndDir = getExtremaAndDir( region.getBottom(), leftBound, rightBound ) ;

        //////////////////////////////////////////////////////////////////////
        // DISECTLEFT and DISECTRIGHT from Paper
        //////////////////////////////////////////////////////////////////////

        Pair<Double,Double> slopesAtTopBounds    = getSlopeAtBounds( region.getTop   (), leftBound, rightBound ) ;
        Pair<Double,Double> slopesAtBottomBounds = getSlopeAtBounds( region.getBottom(), leftBound, rightBound ) ;

        //Get all intersections between the horizontals and the bounds
        Vector<Point> initialIntersections = new Vector<Point>() ;

        if( slopesAtTopBounds.getFirst() >= 0 ) //If the top function at the left is increasing
        {
            StringBasedFunction leftTopHorizontal  = new StringBasedFunction( new Double( region.getLeft ().getMaximum().getY() ).toString() ) ;
        	getLeftRightTopBottomIntersection( region.getLeft().getMaximum(), initialIntersections ) ;
            getLeftRightBoundCommonYIntersections( region.getLeft().getMaximum().getY(), region.getRight(), initialIntersections ) ;
            getLeftRightBoundTopBottomCommonYIntersections( region, leftBound, rightBound, leftTopHorizontal, initialIntersections ) ;
        }

        if( slopesAtBottomBounds.getFirst() <= 0 ) //If the bottom function at the left is decreasing
        {
            StringBasedFunction leftBotHorizontal  = new StringBasedFunction( new Double( region.getLeft ().getMinimum().getY() ).toString() ) ;
        	getLeftRightTopBottomIntersection( region.getLeft().getMinimum(), initialIntersections ) ;
        	getLeftRightBoundCommonYIntersections( region.getLeft().getMinimum().getY(), region.getRight(), initialIntersections ) ;
        	getLeftRightBoundTopBottomCommonYIntersections( region, leftBound, rightBound, leftBotHorizontal, initialIntersections ) ;
        }
        
        if( slopesAtTopBounds.getSecond() <= 0 ) //If the top function at the right is decreasing
        {
            StringBasedFunction rightTopHorizontal = new StringBasedFunction( new Double( region.getRight().getMaximum().getY() ).toString() ) ;
        	getLeftRightTopBottomIntersection( region.getRight().getMaximum(), initialIntersections ) ;
	        getLeftRightBoundCommonYIntersections( region.getRight().getMaximum().getY(), region.getLeft(), initialIntersections ) ;
	        getLeftRightBoundTopBottomCommonYIntersections( region, leftBound, rightBound, rightTopHorizontal, initialIntersections ) ;
        }
        
        if( slopesAtBottomBounds.getSecond() >= 0 ) //If the bottom function at the right is increasing
        {
            StringBasedFunction rightBotHorizontal = new StringBasedFunction( new Double( region.getRight().getMinimum().getY() ).toString() ) ;
        	getLeftRightTopBottomIntersection( region.getRight().getMinimum(), initialIntersections ) ;
        	getLeftRightBoundCommonYIntersections( region.getRight().getMinimum().getY(), region.getLeft(), initialIntersections ) ;
        	getLeftRightBoundTopBottomCommonYIntersections( region, leftBound, rightBound, rightBotHorizontal, initialIntersections ) ;
        }
        
        Predicate<Pair<Double,Double>> lessThan    = p -> p.getFirst() < p.getSecond() ;
        Predicate<Pair<Double,Double>> greaterThan = p -> p.getFirst() > p.getSecond() ;
        
        Vector<Pair<Point,Point>> horizontals = new Vector<Pair<Point,Point>>() ;
        
        if( slopesAtTopBounds.getFirst() >= 0 ) //If the top function at the left is increasing
        {   //Get the horizontal segment going from the top left to the right
	        getHorizontalSegment( topExtremaAndDir, botExtremaAndDir, initialIntersections,
	        	leftBound, rightBound, region.getLeft().getMaximum().getY(), lessThan, horizontals ) ;
        }
        
        if( slopesAtBottomBounds.getFirst() <= 0 ) //If the bottom function at the left is decreasing
        {	//Get the horizontal segment going from the bottom left to the right
        	//Note that this is redundant (but not problematically) if the left bound is a point, not a line.
        	getHorizontalSegment( topExtremaAndDir, botExtremaAndDir, initialIntersections,
        			leftBound, rightBound, region.getLeft().getMinimum().getY(), lessThan, horizontals ) ;
        }

        if( slopesAtTopBounds.getSecond() <= 0 ) //If the top function at the right is decreasing
        {	//Get the horizontal segment going from the top right to the left
        	getHorizontalSegment( topExtremaAndDir, botExtremaAndDir, initialIntersections, 
        			rightBound, leftBound, region.getRight().getMaximum().getY(), greaterThan, horizontals ) ;
        }
        
        if( slopesAtBottomBounds.getSecond() >= 0 ) //If the bottom function at the right is increasing
        {	//Get the horizontal segment going from the bottom right to the left
        	//Note that this is redundant (but not problematically) if the right bound is a point, not a line.
        	getHorizontalSegment( topExtremaAndDir, botExtremaAndDir, initialIntersections, 
        			rightBound, leftBound, region.getRight().getMinimum().getY(), greaterThan, horizontals ) ;
        }
        
        Vector<Point> finalIntersections = new Vector<Point>() ;
        for( Pair<Point,Point> p : horizontals )
        {
        	finalIntersections.add( p.getFirst () ) ;
        	finalIntersections.add( p.getSecond() ) ;
        }

        //////////////////////////////////////////////////////////////////////
        // DISECTTOP and DISECTBOTTOM from Paper
        //////////////////////////////////////////////////////////////////////
        
        //TODO: all of this
        
        //////////////////////////////////////////////////////////////////////
        // ADDTOPVERTICALS and ADDBOTTOMVERTICALS from Paper
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
    
    private void getLeftRightTopBottomIntersection( Point pt, Vector<Point> intersections )
    {
    	if( ! intersections.contains( pt ) )
    		intersections.add( pt ) ;
    }
    
    private void getLeftRightBoundCommonYIntersections( double y, LeftRight lr, Vector<Point> intersections )
    {
        if( 		Utilities.lessThanOrEqualDoubles( y, lr.getMaximum().getY() )
            	&&	Utilities.greaterThanOrEqualDoubles( y, lr.getMinimum().getY() ) )
        {
        	Point pt = new Point( lr.getMaximum().getX(), y ) ;
        	if( ! intersections.contains( pt ) )
        		intersections.add( pt ) ;
        }
    }
    
    private void getLeftRightBoundTopBottomCommonYIntersections( Region region,
    		Double leftBound, Double rightBound, StringBasedFunction horizontal, Vector<Point> intersections )
    {
    	for( Point pt : Intersection.getInstance().allIntersections( region.getTop().getBounds().get( 0 ), horizontal, leftBound, rightBound ) )
    	{
    		if( ! intersections.contains( pt ) )
    			intersections.add( pt ) ;
    	}
        
    	for( Point pt : Intersection.getInstance().allIntersections( region.getBottom().getBounds().get( 0 ), horizontal, leftBound, rightBound ) )
    	{
    		if( ! intersections.contains( pt ) )
    			intersections.add( pt ) ;
    	}
    }
    
    private Pair<Double,Double> getSlopeAtBounds( TopBottom region, Double leftBound, Double rightBound )
    {
    	Vector<Double> pts = new Vector<Double>() ;
    	pts.add( leftBound ) ;
    	pts.add( rightBound ) ;
        
    	Vector<Double> slopes = ExtremeValues.getInstance().firstDerivativeAtPoints( region.getBounds().get( 0 ), pts ) ;

    	if( slopes.size() != 2 )
    		throw new RuntimeException( "Retrieving slopes of function at two points did not return two slopes." ) ;
    	
        return new Pair<Double,Double>( slopes.get( 0 ), slopes.get( 1 ) ) ;
    }

    private Vector<Pair<Double,Double>> getExtremaAndDir( TopBottom region, Double leftBound, Double rightBound )
    {
        Vector<Pair<Double,Double>> output = new Vector<Pair<Double,Double>>() ;
        
    	Vector<Double> extrema = new Vector<Double>( ExtremeValues.getInstance().extrema( region.getBounds().get( 0 ), leftBound, rightBound ) ) ;
        Collections.sort( extrema ) ;
    	Vector<Double> extremaDir = ExtremeValues.getInstance().secondDerivativeAtPoints( region.getBounds().get( 0 ), extrema ) ;
    	
    	if( extrema.size() != extremaDir.size() )
    		throw new RuntimeException( "Retrieving concavity of function at extrema did not return 1 and only 1 value for each extrema." ) ;

    	output.addAll( IntStream
    	  .range( 0, extrema.size() )
    	  .mapToObj( i -> new Pair<Double,Double>( extrema.get( i ), extremaDir.get( i ) ) )
    	  .collect( Collectors.toList() ) ) ;
        
        return output ;
    }
    
    private void getHorizontalSegment( Vector<Pair<Double,Double>> topExtremaAndDir,
    								   Vector<Pair<Double,Double>> botExtremaAndDir,
    								   Vector<Point> intersections,
    								   Double beginX, Double endX, Double y,
    								   Predicate<Pair<Double,Double>> comparisonFunc,
    								   Vector<Pair<Point,Point>> horizontals )
    {
        Double firstNonExtremaIntersection = endX ;

        for( Point p : intersections )
        {
        	if( 	Utilities.equalDoubles( y, p.getY() )
           		&&	( ! Utilities.equalDoubles( beginX, p.getX() ) )
        		&&	comparisonFunc.test( new Pair<Double,Double>( p.getX(), firstNonExtremaIntersection ) )
        		&&	( ! Utilities.equalDoubles( p.getX(), firstNonExtremaIntersection ) )
        		&&	topExtremaAndDir.stream().filter( pair -> Utilities.equalDoubles( pair.getFirst(), p.getY() ) ).count() == 0
        		&&	botExtremaAndDir.stream().filter( pair -> Utilities.equalDoubles( pair.getFirst(), p.getY() ) ).count() == 0 )
        	{
        		firstNonExtremaIntersection = p.getX() ;
        	}
        }

        if( ! Utilities.equalDoubles( endX, firstNonExtremaIntersection ) )
        {
        	Point a = new Point( beginX, y ) ;
        	Point b = new Point( firstNonExtremaIntersection, y ) ;
        	if( a.getX() > b.getX() )
        	{
        		Point t = a ;
        		a = b ;
        		b = t ;
        	}
        	Pair<Point,Point> pts = new Pair<Point,Point>( a, b ) ;
        	if( ! horizontals.contains( pts ) )
        		horizontals.add( pts ) ;
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
                Point botPoint = new Point(left_x, bottomF.evaluateAtPoint(left_x).getReal());
                Point topPoint = new Point(left_x, topF.evaluateAtPoint(left_x).getReal());

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
                Point botPoint = new Point(right_x, bottomF.evaluateAtPoint(right_x).getReal());
                Point topPoint = new Point(right_x, topF.evaluateAtPoint(right_x).getReal());

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
            double firstX = bounds.getFirst().evaluateAtPointByY(midY).getReal();
            double secondX = bounds.getSecond().evaluateAtPointByY(midY).getReal();

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
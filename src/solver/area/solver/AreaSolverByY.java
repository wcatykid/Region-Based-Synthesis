package solver.area.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import exceptions.DomainException;
import exceptions.SolvingException;
import math.analysis.derivatives.Derivatives;
import math.analysis.intersection.Intersection;
import math.integral.DefiniteIntegral;
import representation.ComplexNumber;
import representation.Point;
import representation.bounds.Bound;
import representation.bounds.functions.BoundedFunction;
import representation.bounds.functions.DifferenceBoundedFunction;
import representation.bounds.functions.StringBasedFunction;
import representation.regions.LeftRight;
import representation.regions.Region;
import representation.regions.TopBottom;
import solver.Solution;
import solver.Solver;
import solver.area.AreaSolution;
import solver.area.AreaSolutionByY;
import solver.area.regionComputer.GraphRegionExtractor;
import solver.area.regionComputer.undirectedPlanarGraph.NodePointT;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarEdgeAnnotation;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraph;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphPoint;
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

    @Override
    public Solution solve( Set<Region> regions ) throws SolvingException, DomainException
    {
    	AreaSolutionByY overallSolution = new AreaSolutionByY() ;

        for( Region region : regions )
        {
        	AreaSolutionByY regionSolution = this.solve( region ) ;
            
            if( regionSolution == null )
            	throw new SolvingException( "Solving region " + region + " failed." ) ;

            overallSolution.add( regionSolution.getIntegralExpressions() ) ;
            
            if( regionSolution.getFailedInversionFlag() )
            	overallSolution.setFailedInversionFlag() ;
        }
        
        return overallSolution ;
    }
    
    /**
     * @param region -- a region
     * @return the solution to this problem with respect to y (orthogonal to standard solution: by x)
     *  Thus solving this "area between curves" problem with respect to y
     * @throws DomainException 
     */
    @Override
    public AreaSolutionByY solve(Region region) throws DomainException
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
     * @throws DomainException 
     */
    private AreaSolutionByY solveWithRespectToY(Region region) throws DomainException
    {
    	if( 	( region.getTop   ().getBounds().size() != 1 )
    		||	( region.getBottom().getBounds().size() != 1 ) )
    		throw new RuntimeException( "Solving w.r.t. Y currently"
    				+ " only works when the top and bottom are made of a single function." ) ;
    	//Search for ".get( 0 )" below to see those places where we rely on this assumption.
    	
        //Need these so that various sub-algorithms don't attempt to calculate to infinity
        Double leftBound  = region.getLeft ().getMaximum().getX() ;
        Double rightBound = region.getRight().getMaximum().getX() ;

        //For storing line segments used to create sub-regions.
        HorizontalLineList horizontals = new HorizontalLineList() ;
        VerticalLineList   verticals   = new VerticalLineList  () ;
        
        //Find all internal maxima and minima of top and bottom functions (and their associated second derivative)
        //We need these for all of DISECTLEFT, DISECTRIGHT, DISECTTOP, DISECTBOTTOM, ADDTOPVERTICALS, and ADDBOTTOMVERTICALS
        ArrayList<Extrema> topExtrema = Extrema.getExtrema( region.getTop   (), leftBound, rightBound ) ;
        ArrayList<Extrema> botExtrema = Extrema.getExtrema( region.getBottom(), leftBound, rightBound ) ;

        //////////////////////////////////////////////////////////////////////
        // DISECTLEFT and DISECTRIGHT from Paper
        //////////////////////////////////////////////////////////////////////

        Pair<Double,Double> slopesAtTopBounds    = getSlopeAtBounds( region.getTop   (), leftBound, rightBound ) ;
        Pair<Double,Double> slopesAtBottomBounds = getSlopeAtBounds( region.getBottom(), leftBound, rightBound ) ;

        //This is used to store all intersections between the line defined by the y components of the left and right bounds
        // and the top and bottom functions.  The horizontal line segments we need to collect for DISECTLEFT and DISECTRIGHT
        // will start or end at these points.
        ArrayList<FunctionIntersection> allIntersections = new ArrayList<FunctionIntersection>() ;

        addPointToIntersectionList( allIntersections, new FunctionIntersection(
        		region.getLeft().getMaximum(), FunctionIntersection.Function.Top ) ) ;
        addPointToIntersectionList( allIntersections, new FunctionIntersection(
        		region.getLeft().getMinimum(), FunctionIntersection.Function.Bottom ) ) ;
        addPointToIntersectionList( allIntersections, new FunctionIntersection(
        		region.getRight().getMaximum(), FunctionIntersection.Function.Top ) ) ;
        addPointToIntersectionList( allIntersections, new FunctionIntersection(
        		region.getRight().getMinimum(), FunctionIntersection.Function.Bottom ) ) ;

        if( slopesAtTopBounds.getFirst() >= 0 ) //If the top function at the left is increasing
        {
            StringBasedFunction leftTopHorizontal = new StringBasedFunction(
            		new Double( region.getLeft ().getMaximum().getY() ).toString() ) ;
            getLeftRightBoundCommonYIntersections( region.getLeft().getMaximum().getY(),
            		region.getRight(), FunctionIntersection.Function.Top, allIntersections ) ;
            getLeftRightBoundTopBottomCommonYIntersections( region,
            		leftBound, rightBound, leftTopHorizontal, allIntersections ) ;
        }

        if( slopesAtBottomBounds.getFirst() <= 0 ) //If the bottom function at the left is decreasing
        {
            StringBasedFunction leftBotHorizontal  = new StringBasedFunction(
            		new Double( region.getLeft ().getMinimum().getY() ).toString() ) ;
        	getLeftRightBoundCommonYIntersections( region.getLeft().getMinimum().getY(),
        			region.getRight(), FunctionIntersection.Function.Bottom, allIntersections ) ;
        	getLeftRightBoundTopBottomCommonYIntersections( region,
        			leftBound, rightBound, leftBotHorizontal, allIntersections ) ;
        }
        
        if( slopesAtTopBounds.getSecond() <= 0 ) //If the top function at the right is decreasing
        {
            StringBasedFunction rightTopHorizontal = new StringBasedFunction(
            		new Double( region.getRight().getMaximum().getY() ).toString() ) ;
	        getLeftRightBoundCommonYIntersections( region.getRight().getMaximum().getY(),
	        		region.getLeft(), FunctionIntersection.Function.Top, allIntersections ) ;
	        getLeftRightBoundTopBottomCommonYIntersections( region,
	        		leftBound, rightBound, rightTopHorizontal, allIntersections ) ;
        }
        
        if( slopesAtBottomBounds.getSecond() >= 0 ) //If the bottom function at the right is increasing
        {
            StringBasedFunction rightBotHorizontal = new StringBasedFunction(
            		new Double( region.getRight().getMinimum().getY() ).toString() ) ;
        	getLeftRightBoundCommonYIntersections( region.getRight().getMinimum().getY(),
        			region.getLeft(), FunctionIntersection.Function.Bottom, allIntersections ) ;
        	getLeftRightBoundTopBottomCommonYIntersections( region,
        			leftBound, rightBound, rightBotHorizontal, allIntersections ) ;
        }
        
        if( slopesAtTopBounds.getFirst() >= 0 ) //If the top function at the left is increasing
        {   //Get the horizontal segment going from the top left to the right
        	horizontals.add( HorizontalLine.getHorizontalLineToTheRightOf( topExtrema, botExtrema,
        			allIntersections, leftBound, rightBound, region.getLeft().getMaximum().getY() ) ) ;
        }
        
        if( slopesAtBottomBounds.getFirst() < 0 ) //If the bottom function at the left is decreasing
        {	//Get the horizontal segment going from the bottom left to the right
        	//Note that this is redundant (but not problematically) if the left bound is a point, not a line.
        	horizontals.add( HorizontalLine.getHorizontalLineToTheRightOf( topExtrema, botExtrema,
        			allIntersections, leftBound, rightBound, region.getLeft().getMinimum().getY() ) ) ;
        }

        if( slopesAtTopBounds.getSecond() <= 0 ) //If the top function at the right is decreasing
        {	//Get the horizontal segment going from the top right to the left
        	horizontals.add( HorizontalLine.getHorizontalLineToTheLeftOf( topExtrema, botExtrema,
        			allIntersections, rightBound, leftBound, region.getRight().getMaximum().getY() ) ) ;
        }
        
        if( slopesAtBottomBounds.getSecond() > 0 ) //If the bottom function at the right is increasing
        {	//Get the horizontal segment going from the bottom right to the left
        	//Note that this is redundant (but not problematically) if the right bound is a point, not a line.
        	horizontals.add( HorizontalLine.getHorizontalLineToTheLeftOf( topExtrema, botExtrema,
        			allIntersections, rightBound, leftBound, region.getRight().getMinimum().getY() ) ) ;
        }
        
        //////////////////////////////////////////////////////////////////////
        // DISECTTOP and DISECTBOTTOM from Paper
        //////////////////////////////////////////////////////////////////////
        
        for( Extrema p : topExtrema )
        {
        	if( p.getType() == Extrema.Type.Min )
        	{
        		Double extremaX = p.getValue() ;
        		Double extremaY = new Double( region.getTop().evaluateAtX( p.getValue() ) ) ;
        		ArrayList<FunctionIntersection> extremaIntersections = new ArrayList<FunctionIntersection>() ;
                StringBasedFunction horizontal = new StringBasedFunction( extremaY.toString() ) ;
            	getLeftRightBoundCommonYIntersections( extremaY,
            			region.getLeft(), FunctionIntersection.Function.Neither, extremaIntersections ) ;
            	getLeftRightBoundCommonYIntersections( extremaY,
            			region.getRight(), FunctionIntersection.Function.Neither, extremaIntersections ) ;
            	getLeftRightBoundTopBottomCommonYIntersections( region,
            			leftBound, rightBound, horizontal, extremaIntersections ) ;
            	horizontals.add( HorizontalLine.getHorizontalLineToTheRightOf( topExtrema, botExtrema,
            			extremaIntersections, extremaX, rightBound, extremaY ) ) ;
            	horizontals.add( HorizontalLine.getHorizontalLineToTheLeftOf ( topExtrema, botExtrema,
            			extremaIntersections, extremaX, leftBound , extremaY ) ) ;
            	addPointToIntersectionList( allIntersections, new FunctionIntersection(
            			new Point( extremaX, extremaY ), FunctionIntersection.Function.Top ) ) ;
            	addIntersectionListToIntersectionList( allIntersections, extremaIntersections ) ;
        	}
        }
        
        for( Extrema p : botExtrema )
        {
        	if( p.getType() == Extrema.Type.Max )
        	{
        		Double extremaX = p.getValue() ;
        		Double extremaY = new Double( region.getBottom().evaluateAtX( p.getValue() ) ) ;
        		ArrayList<FunctionIntersection> extremaIntersections = new ArrayList<FunctionIntersection>() ;
                StringBasedFunction horizontal = new StringBasedFunction( extremaY.toString() ) ;
            	getLeftRightBoundCommonYIntersections( extremaY,
            			region.getLeft(), FunctionIntersection.Function.Neither, extremaIntersections ) ;
            	getLeftRightBoundCommonYIntersections( extremaY,
            			region.getRight(), FunctionIntersection.Function.Neither, extremaIntersections ) ;
            	getLeftRightBoundTopBottomCommonYIntersections( region,
            			leftBound, rightBound, horizontal, extremaIntersections ) ;
            	horizontals.add( HorizontalLine.getHorizontalLineToTheRightOf( topExtrema, botExtrema,
            			extremaIntersections, extremaX, rightBound, extremaY ) ) ;
            	horizontals.add( HorizontalLine.getHorizontalLineToTheLeftOf ( topExtrema, botExtrema,
            			extremaIntersections, extremaX, leftBound , extremaY ) ) ;
            	addPointToIntersectionList( allIntersections, new FunctionIntersection(
            			new Point( extremaX, extremaY ), FunctionIntersection.Function.Bottom ) ) ;
            	addIntersectionListToIntersectionList( allIntersections, extremaIntersections ) ;
        	 }
        }

        //////////////////////////////////////////////////////////////////////
        // ADDTOPVERTICALS and ADDBOTTOMVERTICALS from Paper
        //////////////////////////////////////////////////////////////////////

        for( Extrema p : topExtrema )
        {
        	if( p.getType() == Extrema.Type.Max )
        	{
	    		Double extremaX = p.getValue() ;
	    		Double extremaY = new Double( region.getTop().evaluateAtX( p.getValue() ) ) ;
	    		verticals.add( VerticalLine.getVerticalLineFromTop(
	    				extremaX, extremaY, horizontals, region.getBottom() ) ) ;
	            addPointToIntersectionList( allIntersections, new FunctionIntersection(
	            		new Point( extremaX, extremaY ), FunctionIntersection.Function.Top ) ) ;
        	}
        }
        
        for( Extrema p : botExtrema )
        {
        	if( p.getType() == Extrema.Type.Min )
        	{
	    		Double extremaX = p.getValue() ;
	    		Double extremaY = new Double( region.getBottom().evaluateAtX( p.getValue() ) ) ;
	    		verticals.add( VerticalLine.getVerticalLineFromBottom(
	    				extremaX, extremaY, horizontals, region.getBottom() ) ) ;
	            addPointToIntersectionList( allIntersections, new FunctionIntersection(
	            		new Point( extremaX, extremaY ), FunctionIntersection.Function.Bottom ) ) ;
        	}
        }
        
        //////////////////////////////////////////////////////////////////////
        // COMPUTE ALL SUBREGIONS
        //////////////////////////////////////////////////////////////////////

        PlanarGraph<NodePointT,PlanarEdgeAnnotation> graph = new PlanarGraph<NodePointT,PlanarEdgeAnnotation>() ;
        
        //For each horizontal line segment, compute all intersection points between it and top / bottom into a set I.
        //* for each pair of points <j, j+1> \in I
        //    * add edge <j, j+1> to P
        for( HorizontalLine p : horizontals )
        {
        	double splitPoint = .0 ;
    		boolean splitHorizontalInTwo = false ;
        	for( VerticalLine p2 : verticals )
        	{
        		if( 	( 		Utilities.equalDoubles( p.getLeft().getY(), p2.getTop().getY() )
        					||  Utilities.equalDoubles( p.getLeft().getY(), p2.getBottom().getY() ) )
        			&&  ( p.getLeft().getX() < p2.getTop().getX() )
        			&&	( p2.getTop().getX() < p.getRight().getX() ) )
        		{ // This horizontal should be split in two where the vertical hits it. 
        			splitHorizontalInTwo = true ;
        			splitPoint = p2.getTop().getX() ; 
        			break ;
        		}
        	}
        	
        	if( splitHorizontalInTwo )
        	{
        		addEdgeToGraphHorizontal( graph, p.getLeft().getX(), splitPoint         , p.getLeft() .getY() ) ;
        		addEdgeToGraphHorizontal( graph, splitPoint        , p.getRight().getX(), p.getRight().getY() ) ;
        	}
        	else
        	{
        		addEdgeToGraphHorizontal( graph, p.getLeft().getX(), p.getRight().getX(), p.getLeft().getY() ) ;
        	}
        }
        
        //For each vertical line segment, add an edge to P. (This is simple because verticals are very restricted.)
        for( VerticalLine p : verticals )
        {
    		addEdgeToGraphVertical( graph, p.getTop().getY(), p.getBottom().getY(), p.getTop().getX() ) ;
        }
        
        //TODO: tell Dr. Alvin about this added part
        if( region.getLeft().isVertical() )
        {
            ArrayList<Double> leftBoundSplits  = new ArrayList<Double>() ;
            for( HorizontalLine p : horizontals )
            {
            	if( 	Utilities.equalDoubles( p.getLeft().getX(), leftBound )
            		&&	( p.getLeft().getY() < region.getLeft().getMaximum().getY() )
            		&&	( p.getLeft().getY() > region.getLeft().getMinimum().getY() ) )
            	{
            		leftBoundSplits.add( p.getLeft().getY() ) ;
            	}
            }

            Collections.sort( leftBoundSplits ) ;
            Double lastPos = region.getLeft().getMinimum().getY() ;
            for( Double y : leftBoundSplits )
            {
            	addEdgeToGraphVertical( graph, lastPos, y, leftBound ) ;
                lastPos = y ;
            }

            addEdgeToGraphVertical( graph, lastPos, region.getLeft().getMaximum().getY(), leftBound ) ;
        }
        
        if( region.getRight().isVertical() )
        {
            ArrayList<Double> rightBoundSplits = new ArrayList<Double>() ;
            for( HorizontalLine p : horizontals )
            {
            	if( 	Utilities.equalDoubles( p.getRight().getX(), rightBound )
            		&&	( p.getRight().getY() < region.getRight().getMaximum().getY() )
            		&&	( p.getRight().getY() > region.getRight().getMinimum().getY() ) )
            	{
            		rightBoundSplits.add( p.getRight().getY() ) ;
            	}
            }

            Collections.sort( rightBoundSplits ) ;
            Double lastPos = region.getRight().getMinimum().getY() ;
            for( Double y : rightBoundSplits )
            {
            	addEdgeToGraphVertical( graph, lastPos, y, rightBound ) ;
                lastPos = y ;
            }

            addEdgeToGraphVertical( graph, lastPos, region.getRight().getMaximum().getY(), rightBound ) ;
        }
        
        //For each function F in the top and bottom (Things get a bit more complicated here.)
        //* compute all intersection points I with function F
        //* add to I the endpoints of F in the region
        //* for each pair of points <j, j+1> \in I
        //    * add edge <j, j+1> to P
        Collections.sort( allIntersections ) ; //Needed and relies on FunctionIntersection.compareTo
        FunctionIntersection lastTop = null ;
        FunctionIntersection lastBot = null ;
        for( FunctionIntersection pt : allIntersections )
        {
        	if( pt.getFunction() == FunctionIntersection.Function.Top )
        	{
        		if( lastTop != null )
        		{
            		addEdgeToGraph( graph, lastTop.getPoint().getX(), lastTop.getPoint().getY(),
            				pt.getPoint().getX(), pt.getPoint().getY(), region.getTop().getBounds().get( 0 ) ) ;
        		}

    			lastTop = pt ;
        	}

        	if( pt.getFunction() == FunctionIntersection.Function.Bottom )
        	{
        		if( lastBot != null )
        		{
            		addEdgeToGraph( graph, lastBot.getPoint().getX(), lastBot.getPoint().getY(),
            				pt.getPoint().getX(), pt.getPoint().getY(), region.getBottom().getBounds().get( 0 ) ) ;
        		}

    			lastBot = pt ;
        	}
        }
        
        GraphRegionExtractor extractor = new GraphRegionExtractor( graph ) ;
        Vector<Region> regions = extractor.getRegions() ;

        //TODO: come back to this when we flesh out how to check for monotonicity that is compatible
        //      with the way we've disected these regions
        //for( Region r : regions )
        //	if( ! r.isOneToOne() )
        //		throw new RuntimeException( "While solving w.r.t. Y:  Found a"
        //				+ " region from the extracted graph that is not one-to-one." ) ;

        AreaSolutionByY solution = solveSimpleRegions( regions ) ;
        solution.addRangeRegion( regions.toArray( new Region[ 0 ] ) ) ;
        return solution ;
    }
    
    private void addEdgeToGraphHorizontal( PlanarGraph<NodePointT,PlanarEdgeAnnotation> graph, Double x1, Double x2, Double y )
    {
    	addEdgeToGraph( graph, x1, y, x2, y, new StringBasedFunction( "y = " + y.toString() ) ) ;
    }

    private void addEdgeToGraphVertical( PlanarGraph<NodePointT,PlanarEdgeAnnotation> graph, Double y1, Double y2, Double x )
    {
    	addEdgeToGraph( graph, x, y1, x, y2, new StringBasedFunction( "x = " + x.toString() ) ) ;
    }

    private void addEdgeToGraph( PlanarGraph<NodePointT,PlanarEdgeAnnotation> graph,
    						     Double x1, Double y1, Double x2, Double y2, Bound bound )
    {
        PlanarGraphPoint first  = new PlanarGraphPoint( null, x1, y1 ) ;
        PlanarGraphPoint second = new PlanarGraphPoint( null, x2, y2 ) ;
    	graph.addNode( first , NodePointT.INTERSECTION ) ;
    	graph.addNode( second, NodePointT.INTERSECTION ) ;

        // Add the edge
        graph.addUndirectedEdge( first , second, new PlanarEdgeAnnotation( bound ) ) ;
    }

    private void addPointToIntersectionList( ArrayList<FunctionIntersection> intersections, FunctionIntersection pt )
    {
    	if( ! intersections.contains( pt ) )
    		intersections.add( pt ) ;
    }
    
    private void addIntersectionListToIntersectionList( ArrayList<FunctionIntersection> target, ArrayList<FunctionIntersection> source )
    {
    	for( FunctionIntersection pt : source )
    		addPointToIntersectionList( target, pt ) ;
    }
    
    private void getLeftRightBoundCommonYIntersections( double y, LeftRight lr,
    		FunctionIntersection.Function function, ArrayList<FunctionIntersection> intersections )
    {
        if( 	Utilities.lessThanOrEqualDoubles( y, lr.getMaximum().getY() )
            &&	Utilities.greaterThanOrEqualDoubles( y, lr.getMinimum().getY() ) )
        {
        	function = lr.isVertical() ? FunctionIntersection.Function.Neither : function ; 
        	addPointToIntersectionList( intersections, new FunctionIntersection( new Point( lr.getMaximum().getX(), y ), function ) ) ;
        }
    }
    
    private void getLeftRightBoundTopBottomCommonYIntersections( Region region,
    		Double leftBound, Double rightBound, StringBasedFunction horizontal, ArrayList<FunctionIntersection> intersections )
    {
    	for( Point pt : Intersection.getInstance().allIntersections( region.getTop().getBounds().get( 0 ), horizontal, leftBound, rightBound ) )
    	{
        	addPointToIntersectionList( intersections, new FunctionIntersection( pt, FunctionIntersection.Function.Top ) ) ;
    	}
        
    	for( Point pt : Intersection.getInstance().allIntersections( region.getBottom().getBounds().get( 0 ), horizontal, leftBound, rightBound ) )
    	{
        	addPointToIntersectionList( intersections, new FunctionIntersection( pt, FunctionIntersection.Function.Bottom ) ) ;
    	}
    }
    
    private Pair<Double,Double> getSlopeAtBounds( TopBottom region, Double leftBound, Double rightBound )
    {
    	Vector<Double> pts = new Vector<Double>() ;
    	pts.add( leftBound ) ;
    	pts.add( rightBound ) ;
        
    	Vector<Double> slopes = Derivatives.getInstance().firstDerivativeAtPoints( region.getBounds().get( 0 ), pts ) ;

    	if( slopes.size() == 1 )
    	{
    		//It's possible that the first derivative is a constant, and thus Mathematica only returns
    		// one value.
            return new Pair<Double,Double>( slopes.get( 0 ), slopes.get( 0 ) ) ;
    	}
    	else if( slopes.size() != 2 )
    	{
    		throw new RuntimeException( "Retrieving slopes of function at two points did not return two slopes." ) ;
    	}
    	
        return new Pair<Double,Double>( slopes.get( 0 ), slopes.get( 1 ) ) ;
    }

    /**
     * @param regions -- a set of simple regions
     * @return a solution (of definite integrals) describing the solution of this set of simple regions
     */
    private AreaSolutionByY solveSimpleRegions(Vector<Region> regions)
    {
    	AreaSolutionByY solution = new AreaSolutionByY();
        for (Region region : regions)
        {
        	Solution s = solveSimpleRegion( region ) ;
        	if( s != null )
        		solution.add( s ) ;
        	else
        		solution.setFailedInversionFlag() ;
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
            ComplexNumber firstXComplex  = bounds.getFirst ().evaluateAtPointByY( midY ) ;
            ComplexNumber secondXComplex = bounds.getSecond().evaluateAtPointByY( midY ) ;
            
            if( firstXComplex == null || secondXComplex == null )
            	return null ;
            
            double firstX  = firstXComplex .getReal() ;
            double secondX = secondXComplex.getReal() ;

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
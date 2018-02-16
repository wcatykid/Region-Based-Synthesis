package solver.area.solver;

import java.util.function.Predicate;

import exceptions.DomainException;
import representation.regions.TopBottom;
import representation.Point;
import utilities.Pair;
import utilities.Utilities;

public class VerticalLine implements Comparable<VerticalLine>
{
	private Point _top ;
	private Point _bot ;
	
	public VerticalLine( Point top, Point bottom )
	{
		if( ! Utilities.equalDoubles( top.getX(), bottom.getX() ) )
			throw new RuntimeException( "A vertical line must be vertical!" ) ;
				
		_top = top    ;
		_bot = bottom ;
	}
	
	public Point getTop()
	{
		return _top ;
	}
	
	public Point getBottom()
	{
		return _bot ;
	}

	@Override
	public int compareTo( VerticalLine rhs )
	{
		if( ! _top.equals( rhs._top ) )
			return _top.lessThan( rhs._top ) ? -1 : 1 ;

		if( ! _bot.equals( rhs._bot ) )
			return _bot.lessThan( rhs._bot ) ? -1 : 1 ;
        
        return 0 ;
	}

	@Override
	public boolean equals( Object rhs )
	{
		if( ! ( rhs instanceof VerticalLine ) )
			return false ;
		return compareTo( (VerticalLine) rhs ) == 0 ;
	}

	public static VerticalLine getVerticalLineFromTop( Double fromPointX, Double fromPointY,
			HorizontalLineList horizontals, TopBottom oppositeBound ) throws DomainException
	{
	    Predicate<Pair<Double,Double>> greaterThan = p -> p.getFirst() > p.getSecond() ;
		return getVerticalLine( fromPointX, fromPointY, horizontals, oppositeBound, greaterThan ) ; 
	}
	
	public static VerticalLine getVerticalLineFromBottom( Double fromPointX, Double fromPointY,
			HorizontalLineList horizontals, TopBottom oppositeBound ) throws DomainException
	{
		Predicate<Pair<Double,Double>> lessThan = p -> p.getFirst() < p.getSecond() ;
		return getVerticalLine( fromPointX, fromPointY, horizontals, oppositeBound, lessThan ) ; 
	}
	
	private static VerticalLine getVerticalLine( Double fromPointX, Double fromPointY, HorizontalLineList horizontals,
			TopBottom oppositeBound, Predicate<Pair<Double,Double>> comparisonFunc ) throws DomainException
	{
		HorizontalLine closestIntersectingHorizontal = null ;
	    for( HorizontalLine horizontal : horizontals )
	    {
	        if( 	Utilities.greaterThanOrEqualDoubles( fromPointX, horizontal.getLeft().getX() )
	            &&	Utilities.lessThanOrEqualDoubles( fromPointX, horizontal.getRight().getX() ) )
	        {
	        	if( 	( closestIntersectingHorizontal == null )
	        		||	( comparisonFunc.test( new Pair<Double,Double>( horizontal.getLeft().getY(),
	        				                   closestIntersectingHorizontal.getLeft().getY() ) ) ) )
	        	{
	        		closestIntersectingHorizontal = horizontal ;
	        	}
	        }
	    }
	    
	    Double closestIntersectingY ;
	    if( closestIntersectingHorizontal != null )
	    {
	    	closestIntersectingY = closestIntersectingHorizontal.getLeft().getY() ;
	    }
	    else
	    {
	    	closestIntersectingY = new Double( oppositeBound.evaluateAtX( fromPointX ) ) ;
	    }
	
		Point a = new Point( fromPointX, fromPointY ) ;
		Point b = new Point( fromPointX, closestIntersectingY ) ;
		if( a.getY() < b.getY() )
		{
			Point t = a ;
			a = b ;
			b = t ;
		}
		return new VerticalLine( a, b ) ;
	}
}

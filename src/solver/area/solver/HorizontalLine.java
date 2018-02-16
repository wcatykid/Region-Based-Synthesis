package solver.area.solver;

import java.util.function.Predicate;
import java.util.ArrayList;


import representation.Point;
import utilities.Pair;
import utilities.Utilities;

public class HorizontalLine implements Comparable<HorizontalLine>
{
	private Point _left  ;
	private Point _right ;
	
	public HorizontalLine( Point left, Point right )
	{
		if( ! Utilities.equalDoubles( left.getY(), right.getY() ) )
			throw new RuntimeException( "A horizontal line must be horizontal!" ) ;
				
		_left  = left  ;
		_right = right ;
	}
	
	public Point getLeft()
	{
		return _left ;
	}
	
	public Point getRight()
	{
		return _right ;
	}

	@Override
	public int compareTo( HorizontalLine rhs )
	{
		if( ! _left.equals( rhs._left ) )
			return _left.lessThan( rhs._left ) ? -1 : 1 ;

		if( ! _right.equals( rhs._right ) )
			return _right.lessThan( rhs._right ) ? -1 : 1 ;
        
        return 0 ;
	}

	@Override
	public boolean equals( Object rhs )
	{
		if( ! ( rhs instanceof HorizontalLine ) )
			return false ;
		return compareTo( (HorizontalLine) rhs ) == 0 ;
	}

	public static HorizontalLine getHorizontalLineToTheLeftOf( ArrayList<Extrema> topExtrema, ArrayList<Extrema> botExtrema,
			ArrayList<FunctionIntersection> intersections, Double beginX, Double endX, Double y )
	{
        Predicate<Pair<Double,Double>> greaterThan = p -> p.getFirst() > p.getSecond() ;
    	return getHorizontalLine( topExtrema, botExtrema, intersections, beginX, endX, y, greaterThan ) ;
	}
    
    public static HorizontalLine getHorizontalLineToTheRightOf( ArrayList<Extrema> topExtrema, ArrayList<Extrema> botExtrema,
    		ArrayList<FunctionIntersection> intersections, Double beginX, Double endX, Double y )
	{
		Predicate<Pair<Double,Double>> lessThan = p -> p.getFirst() < p.getSecond() ;
		return getHorizontalLine( topExtrema, botExtrema, intersections, beginX, endX, y, lessThan ) ;
	}

    private static HorizontalLine getHorizontalLine( ArrayList<Extrema> topExtrema, ArrayList<Extrema> botExtrema,
    		ArrayList<FunctionIntersection> intersections, Double beginX, Double endX, Double y, Predicate<Pair<Double,Double>> comparisonFunc )
    {
        Double firstNonExtremaIntersection = endX ;

        for( FunctionIntersection p : intersections )
        {
        	if( 	Utilities.equalDoubles( y, p.getPoint().getY() )
        		&&	( ! Utilities.equalDoubles( beginX, p.getPoint().getX() ) )
        		&&	comparisonFunc.test( new Pair<Double,Double>( p.getPoint().getX(), firstNonExtremaIntersection ) )
        		&&	( ! Utilities.equalDoubles( p.getPoint().getX(), firstNonExtremaIntersection ) )
        		&&	topExtrema.stream().filter( pair -> Utilities.equalDoubles( pair.getValue(), p.getPoint().getY() ) ).count() == 0
        		&&	botExtrema.stream().filter( pair -> Utilities.equalDoubles( pair.getValue(), p.getPoint().getY() ) ).count() == 0 )
        	{
        		firstNonExtremaIntersection = p.getPoint().getX() ;
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
        	return new HorizontalLine( a, b ) ;
        }
        
        return null ;
    }
}

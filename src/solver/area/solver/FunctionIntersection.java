package solver.area.solver;

import representation.Point;
import utilities.Utilities;

public class FunctionIntersection implements Comparable<FunctionIntersection>
{
	public enum Function { Top, Bottom, Neither }
	
	private Point    _point ;
	private Function _func  ;
	
	public FunctionIntersection( Point point, Function func )
	{
		_point = point ;
		_func  = func  ;
	}

	public Point getPoint()
	{
		return _point ;
	}
	
	public Function getFunction()
	{
		return _func ;
	}

	@Override
	public int compareTo( FunctionIntersection rhs )
	{
		//This specific ordering is relied upon.
		// All top functions first, then all bottom.
		// And within each, ordered by x first then by y.
		
		int funcCompare = _func.compareTo( rhs._func ) ;
		
		if( funcCompare != 0 )
			return funcCompare ;
		
		if( ! Utilities.equalDoubles( _point.getX(), rhs._point.getX() ) )
			return _point.getX() < rhs._point.getX() ? -1 : 1 ;

		if( ! Utilities.equalDoubles( _point.getY(), rhs._point.getY() ) )
			return _point.getY() < rhs._point.getY() ? -1 : 1 ;

		return 0 ;
	}
	
	@Override
	public boolean equals( Object rhs )
	{
		if( ! ( rhs instanceof FunctionIntersection ) )
			return false ;
		return compareTo( (FunctionIntersection) rhs ) == 0 ;
	}
}

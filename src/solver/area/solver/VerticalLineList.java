package solver.area.solver;

import java.util.ArrayList;
import java.util.Iterator;

public class VerticalLineList implements Iterable<VerticalLine>
{
	private ArrayList<VerticalLine> _lines = new ArrayList<VerticalLine>() ;
	
	public boolean add( VerticalLine line )
	{
		if( 	( line != null )
			&&	( ! _lines.contains( line ) ) )
		{
			_lines.add( line ) ;
			return true ;
		}
		
		return false ;
	}

	@Override
	public Iterator<VerticalLine> iterator()
	{
		return _lines.iterator() ;
	}
}

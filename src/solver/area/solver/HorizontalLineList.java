package solver.area.solver;

import java.util.ArrayList;
import java.util.Iterator;

public class HorizontalLineList implements Iterable<HorizontalLine>
{
	private ArrayList<HorizontalLine> _lines = new ArrayList<HorizontalLine>() ;
	
	public boolean add( HorizontalLine line )
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
	public Iterator<HorizontalLine> iterator()
	{
		return _lines.iterator() ;
	}
}

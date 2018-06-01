package solver.area;

import java.util.*;
import representation.regions.Region ;

public class AreaSolutionByY extends AreaSolution
{
	protected boolean           _failedInversionFlag = false ;
    protected ArrayList<Region> _simpleRegions       = new ArrayList<Region>() ;
    
    public Region[] getSimpleRegions() { return _simpleRegions.toArray( new Region[ 0 ] ) ; }
    
    public void addRegion( Region r )
    {
    	_simpleRegions.add( r ) ;
    }
    
    public void addRangeRegion( Region[] rs )
    {
    	for( Region r : rs )
    		addRegion( r ) ;
    }
    
    public void setFailedInversionFlag()
    {
    	_failedInversionFlag = true ;
    }
    
    public boolean getFailedInversionFlag()
    {
    	return _failedInversionFlag ;
    }
}

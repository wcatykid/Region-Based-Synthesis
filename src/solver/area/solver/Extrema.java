package solver.area.solver;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Collections;

import representation.regions.TopBottom;
import math.analysis.derivatives.Derivatives;
import math.analysis.extrema.ExtremeValues;

public class Extrema
{
	public enum Type { Min, Max }
	
	private Double _value ;
	private Type   _type  ;
	
	public Extrema( Double value, Double dir )
	{
		_value = value ;
		_type = dir >= 0 ? Type.Min : Type.Max ;
	}
	
	public Double getValue()
	{
		return _value ;
	}
	
	public Type getType()
	{
		return _type ;
	}

    public static ArrayList<Extrema> getExtrema( TopBottom region, Double leftBound, Double rightBound )
    {
    	ArrayList<Extrema> output = new ArrayList<Extrema>() ;
        
    	Vector<Double> extrema = new Vector<Double>( ExtremeValues.getInstance().extrema( region.getBounds().get( 0 ), leftBound, rightBound ) ) ;
    	
    	if( extrema.size() > 0 )
    	{
            Collections.sort( extrema ) ;
        	Vector<Double> extremaDir = Derivatives.getInstance().secondDerivativeAtPoints( region.getBounds().get( 0 ), extrema ) ;
        	
        	if( extrema.size() != extremaDir.size() )
        		throw new RuntimeException( "Retrieving concavity of function at extrema did not return 1 and only 1 value for each extrema." ) ;

        	for( int i = 0 ; i < extrema.size() ; ++i )
        		output.add( new Extrema( extrema.get( i ), extremaDir.get( i ) ) ) ;
    	}
        
        return output ;
    }
}

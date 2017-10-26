package engine.region;

import java.util.Vector;
import engine.Instantiable;
import representation.regions.Region;
import template.RegionTemplate;
import utilities.logger.LoggerFactory;

public class RegionInstantiator implements Instantiable
{
	//TODO: come back to this whole class

    //private static final LeftInstantiator _LEFT_INSTANTIATOR;
    //private static final TopBottomInstantiator _TOP_BOTTOM_INSTANTIATOR;
    //private static final RightInstantiator _RIGHT_INSTANTIATOR;

    //static
    //{
    //    _LEFT_INSTANTIATOR = new LeftInstantiator();
    //    _TOP_BOTTOM_INSTANTIATOR = new TopBottomInstantiator();
    //    _RIGHT_INSTANTIATOR = new RightInstantiator();
    //}

    //
    // Main instantiation routine for region synthesis; given a template, generate all regions
    //
    public Vector<Region> instantiate(RegionTemplate template)
    {
        throw new RuntimeException( "RegionInstantiator.instantiate has unresolved build problems!" ) ;

/*      
        //
        // Left side instantiation
        // Two options: 
    	//    Point:                *
    	//
    	//    Vertical Line:        |
    	//                          |
    	//                          |
    	//
    	Vector<Region> leftCompleteRegions = _LEFT_INSTANTIATOR.instantiate(template);

        //
        // Construct the top and bottom concurrently: one for the bottom then one for the top.
        //
    	// Result:
    	//                            ______*_________*_______
    	//                           /
    	//    Point:                *
    	//                           \____________*__________
    	//
    	//                          _____*_______*______________*
    	//    Vertical Line:        |
    	//                          |
    	//                          |_________*_________*________
    	//

        Vector<Region> unclosed = new Vector<Region>();

        for (Region region : leftCompleteRegions)
        {
            unclosed.addAll( _TOP_BOTTOM_INSTANTIATOR.instantiate(template, region).constructTopBottom(region, template));
        }
        
        //
        // Close each of the resulting regions
        // The important output is the set of sealed regions which have the 
        //
        Vector<Region> sealed = new Vector<Region>();
        for (Region unclose : unclosed)
        {
            // Adds the final top function
            Vector<Region> closed = closeRegion(theTemplate, unclose);

            // Establishes the right bound of the region
            for (Region close : closed)
            {
                sealed.addAll(sealRegion(theTemplate, close));
            }
        }

        Vector<Region> verified = verify(sealed);
        
        return verified;
*/
    }
   
    /**
     * @param regions -- a set of candidate regions (possibly sealed)
     * @return a set of verified, closed regions 
     */
    public Vector<Region> verify(Vector<Region> regions)
    {
    	Vector<Region> verified = new Vector<Region>();

    	// For each region, verify
    	for (Region region : regions)
    	{
    		if (region.verify()) verified.add(region);
    		else
    		{
    			LoggerFactory.getLogger(LoggerFactory.DEBUG_OUTPUT_ID).writeln("Region " + region.toString() + " was not verified.");
    		}    		
    	}
    	
    	return verified;
    }

    @Override
    public Vector<Region> instantiate(Region region, RegionTemplate template)
    {
        throw new RuntimeException( "RegionInstantiator.instantiate is not implemented yet!" ) ;
    }
}






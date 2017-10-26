package engine.bounds;

import java.util.Vector;

import engine.Instantiable;
import representation.regions.Region;
import template.RegionTemplate;

public class FunctionInstantiator implements  Instantiable
{

    @Override
    public Vector<Region> instantiate(Region region, RegionTemplate template)
    {
        throw new RuntimeException( "FunctionInstantiator.instantiate has unresolved build problems!" ) ;
    }

}

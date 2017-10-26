package engine.bounds;

import java.util.Vector;

import engine.Instantiable;
import representation.regions.Region;
import template.RegionTemplate;

public class PointInstantiator implements Instantiable
{
    //public Vector<representation.regions.Region> instantiate(template.RegionTemplate template);

    @Override
    public Vector<Region> instantiate(Region region, RegionTemplate template)
    {
        throw new RuntimeException( "PointInstantiator.instantiate has unresolved build problems!" ) ;
    }
}

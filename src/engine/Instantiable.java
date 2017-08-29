package engine;

import java.util.Vector;

import representation.regions.Region;
import template.RegionTemplate;

public interface Instantiable
{
    public abstract Vector<Region> instantiate(Region region, RegionTemplate template);
}

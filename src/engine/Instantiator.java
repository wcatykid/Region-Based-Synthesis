package engine;

import java.util.List;
import java.util.Vector;

import engine.region.RegionInstantiator;
import frontend.Options;
import representation.regions.Region;
import solver.RegionProblemAggregator;
import template.RegionTemplate;

/**
 * Front-end region instantiator
 */
public class Instantiator
{
    // The templates we will consider
    protected List<RegionTemplate> _templates;

    // Actual region instantiator (algorithms, etc).
    protected RegionInstantiator _instantiator;

    public Instantiator(List<RegionTemplate> templates)
    {
        _templates = templates;
        _instantiator = new RegionInstantiator();
    }

    /**
     * @return a set of region aggregator objects that act as a pair <template, region>;
     *         process one template at a time
     */
    public Vector<RegionProblemAggregator> instantiate()
    {
        Vector<RegionProblemAggregator> concrete = new Vector<>();

        for (RegionTemplate template : _templates)
        {
            concrete.addAll(instantiate(template));
        }

        return concrete;
    }


    /**
     * @return A set of concrete regions that follow the stated template (_template)
     */
    /**
     * @param template -- a region template
     * @return A set of 
     */
    private Vector<RegionProblemAggregator> instantiate(RegionTemplate template)
    {
        if (Options.DEBUG) System.out.println("Starting instantiation on " + template);

        //
        // Instantiate the individual region
        //
        Vector<Region> concrete = _instantiator.instantiate(template);

        if (Options.DEBUG) 
        {
            System.out.println("Instantiated: \n" + concrete);
            System.out.println("Instantiated " + concrete.size() + " regions.");
        }

        // Construct the region aggregator objects: pairs <template, region>
        Vector<RegionProblemAggregator> pairs = new Vector<RegionProblemAggregator>();
        for (Region region : concrete)
        {
            pairs.add(new RegionProblemAggregator(template, region));
        }

        return pairs;
    }
}

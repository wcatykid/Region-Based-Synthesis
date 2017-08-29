package solver;

import representation.regions.Region;
import solver.area.AreaSolution;
import template.RegionTemplate;

//
//
// A class for aggregation; contains:
//    a) Original Template
//    b) Concrete Region
//    c) Area problems
//    d) Volume problems
//
public class RegionProblemAggregator
{
    protected RegionTemplate _template;
    public RegionTemplate getTemplate() { return _template; } 

    protected Region _region;
    public Region getRegion() { return _region; }
    
    // Area Problem(s)
    protected AreaSolution _wrtX;
    public void setAreaProblemSolutionByX(AreaSolution solution) { _wrtX = solution; }
    public AreaSolution getAreaProblemSolutionByX() { return _wrtX; }
    
    // Volume Problem(s)

    public RegionProblemAggregator(Region region)
    {
        this(null, region);
    }
    
    public RegionProblemAggregator(RegionTemplate template, Region region)
    {
        _template = template;
        _region = region;
        _wrtX = null;
    }



    public void addVolumeProblem()
    {
        // TODO
    }
}

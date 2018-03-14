package solver.area.regionComputer;

import solver.area.regionComputer.undirectedPlanarGraph.NodePointT;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarEdgeAnnotation;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraph;

/**
 * Given a planar graph
 *    (1) Compute facets
 *    (2) Convert facets to area problem regions
 */
public class GraphRegionExtractor extends RegionExtractor
{
	protected PlanarGraph<NodePointT, PlanarEdgeAnnotation> _graph ;
	
    public GraphRegionExtractor( PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph )
    {
    	super() ;
    	_graph = graph ;
    }

    @Override
    protected void buildRegions()
    {
    	buildRegionsFromGraph( _graph ) ;
    }
}
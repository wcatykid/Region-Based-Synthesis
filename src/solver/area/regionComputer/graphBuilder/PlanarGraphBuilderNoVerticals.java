package solver.area.regionComputer.graphBuilder;

import representation.bounds.functions.StringBasedFunction;
import solver.area.regionComputer.undirectedPlanarGraph.NodePointT;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarEdgeAnnotation;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraph;

/**
 * Given a set of functions, construct a planar graph that uniquely identifies the regions created by the
 * intersections of all such functions
 * 
 * Algorithm:
 *    foreach function identify the set of intersection points I
 *    foreach sequential pair <i1, i2> in I, construct two planar edges: <i1, mid>, <mid, i2>
 *          where mid represents the mean x-value between i1 and i2: mid = (i1 + i2) / 2
 *          
 *   The resulting planar graph should:
 *        (1) not contain any filaments (dead-end segment) or isolated points
 *        (2) imply minimal and maximal domain and range interval values; essentially a largest Rectangular window
 */
public class PlanarGraphBuilderNoVerticals extends PlanarGraphBuilder
{
    public PlanarGraphBuilderNoVerticals(StringBasedFunction[] functions)
    {
        super(functions);
    }

    /**
     * @return on-demand: a PlanarGraph that uniquely defines the function regions
     */
    public PlanarGraph<NodePointT, PlanarEdgeAnnotation> build()
    {
        if (_graph == null) _graph = buildGraph();

        return _graph;
    }

    /**
     * 
     * @return a PlanarGraph that uniquely defines the function regions
     */
    @Override
    protected PlanarGraph<NodePointT, PlanarEdgeAnnotation> buildGraph()
    {
        PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph = new PlanarGraph<NodePointT, PlanarEdgeAnnotation>();
        
        constructStandardGraph(graph);

        return graph;
    }
}

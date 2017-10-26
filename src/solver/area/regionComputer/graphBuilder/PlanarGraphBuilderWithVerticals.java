package solver.area.regionComputer.graphBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import representation.Point;
import representation.bounds.functions.StringBasedFunction;
import solver.area.regionComputer.undirectedPlanarGraph.NodePointT;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarEdgeAnnotation;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraph;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphNode;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphPoint;

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
public class PlanarGraphBuilderWithVerticals extends PlanarGraphBuilder
{
    // The points along each vertical
    protected Vector<Vector<Point>> _verticalPoints;
    protected Vector<Double> _xForVerticals;
    
    public PlanarGraphBuilderWithVerticals(StringBasedFunction[] functions, Vector<Double> xs)
    {
        super(functions);

        _verticalPoints = new Vector<Vector<Point>>();
        for (int index = 0; index < xs.size(); index++)
        {
            _verticalPoints.add(new Vector<Point>());
        }
        _xForVerticals = xs;
    }

    /**
     * @return on-demand: a PlanarGraph that uniquely defines the function regions
     */
    public PlanarGraph<NodePointT, PlanarEdgeAnnotation> build()
    {
        //        if (_graph == null) _graph = buildGraph(); WHAT The fuck is this line?

        _graph = new PlanarGraph<NodePointT, PlanarEdgeAnnotation>();

        constructVerticals(_graph);
        constructStandardGraph(_graph);

        return _graph;
    }

    /**
     * 
     * @param graph -- a Planar graph object we (may) add to
     * 
     * Since the user can specify a domain for a problem, we need to represent those restrictions as vertical edges in the 
     * planar graph:
     *    Compute (x, f(x)) among all functions and insert edges as needed; no need for dummy points in this situation
     */
    private void constructVerticals(PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph)
    {
        // Construct all verticals on the left and right AND add the nodes / edges to the graph 
        for (Double x : _xForVerticals)
        {
            _verticalPoints.add(constructVerticals(graph, x));
        }
    }

    private Vector<Point> constructVerticals(PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph, double x)
    {
        //
        // Compute all (x, f(x)):
        //
        Set<Point> points = new HashSet<Point>();
        for (StringBasedFunction function : _functions)
        {
            points.add(new Point(x, function.evaluateAtPoint(x)));
        }

        // If all functions intersect at that point, no need for further analysis
        if (points.size() <= 1) return new Vector<Point>();

        // Order the points lexciographically
        Vector<Point> verticalPoints = new Vector<Point>(points);
        Collections.sort(verticalPoints);

        //
        // Add an edge between each function vertically
        //
        for (int p = 0; p < points.size() - 1; p++)
        {
            String name = "x = " + x;
            PlanarGraphPoint grPoint1 = new PlanarGraphPoint(name, verticalPoints.get(p).getX(), verticalPoints.get(p).getY());
            PlanarGraphPoint grPoint2 = new PlanarGraphPoint(name, verticalPoints.get(p+1).getX(), verticalPoints.get(p+1).getY());

            graph.addNode(grPoint1, NodePointT.VERTICAL);
            graph.addNode(grPoint2, NodePointT.VERTICAL);

            // We are creating a Vertical StringBasedFunction for edge annotation (although it's not a real function)
            graph.addUndirectedEdge(grPoint1, grPoint2, new PlanarEdgeAnnotation(new StringBasedFunction(name)));
        }

        // Debug printing of points
        for (PlanarGraphNode<NodePointT, PlanarEdgeAnnotation> node : graph.getNodes())
        {
            System.out.println(node.getPoint());
        }
        
        return verticalPoints;
    }
    
    /**
     * @param fIndex -- the index of the function we are interested in
     * @return the set of all points for which function f intersects all other functions (Functions \setminus {f})
     *     This includes verticals
     */
    @Override
    protected Vector<Point> collectIntersections(int fIndex)
    {
        //
        // Collect all intersection points into a set
        //
        Set<Point> pointSet = new HashSet<Point>();
        for (Vector<Point> points : _intersections.get(fIndex))
        {
            pointSet.addAll(points);
        }

        //
        // Add all vertical points
        //
        for (Double x : _xForVerticals)
        {
            pointSet.add(new Point(x, _functions[fIndex].evaluateAtPoint(x)));
        }

        //
        // Convert the Set to a lexicographically ordered Vector
        //
        Vector<Point> ordered = new Vector<Point>(pointSet);
        Collections.sort(ordered);

        return ordered;
    }
}

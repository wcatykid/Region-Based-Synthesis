package solver.area.regionComputer.graphBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import math.analysis.intersection.Intersection;
import representation.Point;
import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import solver.area.TextbookAreaProblem;
import solver.area.regionComputer.undirectedPlanarGraph.NodePointT;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarEdgeAnnotation;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraph;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphNode;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphPoint;
import utilities.Utilities;

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
public class PlanarGraphBuilder
{
    // The largest 'window' we will examine in the default case
    public final static double LOWERBOUND_X = -25;
    public final static double UPPERBOUND_X = 25;

    // The functions we will analyze
    protected StringBasedFunction[] _functions;

    // if the problem dictates a domain, this may modify the planar graph with vertical segments
    protected Domain _domain;

    // The matrix of all intersection points among all functions: N x N
    protected Vector<Vector<Vector<Point>>> _intersections;
    
    //
    // The planar graph we will construct with annotations:
    // nodes: type of point in the plane: intersection or intermittent point for uniqueness
    // edges: the name of the function the edge describes
    protected PlanarGraph<NodePointT, PlanarEdgeAnnotation> _graph;
    public PlanarGraph<NodePointT, PlanarEdgeAnnotation> getGraph() { return _graph; }

    public PlanarGraphBuilder(TextbookAreaProblem tap)
    {
        this(tap.getFunctions(), tap.getDomain());
    }

    public PlanarGraphBuilder(StringBasedFunction[] functions)
    {
        this(functions, null);
    }

    public PlanarGraphBuilder(StringBasedFunction[] functions, Domain domain)
    {
        _functions = functions;
        _domain = domain;
        _graph = null;
        
        // Construct the intersection (point) matrix and populate
        _intersections = new Vector<Vector<Vector<Point>>>();

        computeIntersections();
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
    protected PlanarGraph<NodePointT, PlanarEdgeAnnotation> buildGraph()
    {
        //
        // Analyze one function at a time: find all intersections of f with ALL g
        //
        // The nodes of the planar graph are:
        //     (1) Intersections between functions
        //     (2) Points in-between intersections: needed to uniquely define functions / regions in the corresponding planar graph
        //
        PlanarGraphBuilder builder = null;
 
        if (_domain == null) builder = new PlanarGraphBuilderNoVerticals(_functions);
        
        // Normal planar graph, but with verticals
        else
        {
            Vector<Double> xs = new Vector<Double>();
            xs.add(_domain.getLowerBound());
            xs.add(_domain.getUpperBound());
            builder = new PlanarGraphBuilderWithVerticals(_functions, _domain, xs);
        }

        PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph = builder.build();

        // Debug printing of points
        int n = 1; 
        for (PlanarGraphNode<NodePointT, PlanarEdgeAnnotation> node : graph.getNodes())
        {
            System.out.println((n++) + ": " + node.getPoint());
        }

        return graph;
    }
    
    /**
     * 
     * @param graph -- an empty Planar graph object
     * 
     * Analyze one function at a time: find all intersections of f with ALL g
     * The nodes of the planar graph are:
     *     (1) Intersections between functions
     *     (2) Points in-between intersections: needed to uniquely define functions / regions in the corresponding planar graph
     */
    protected void constructStandardGraph(PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph)
    {
        for (int f = 0; f < _functions.length; f++)
        {
            Vector<Point> fIntersections = collectIntersections(f);

            int pointCount = 1;
            for (int pIndex = 0; pIndex < fIntersections.size() - 1; pIndex++)
            {
                //
                // For each:           left --------------- mid ------- right
                // Construct add three points to the graph and 2 edges
                //
                // Left / Right
                Point leftPt = fIntersections.get(pIndex);
                PlanarGraphPoint leftGrPt = new PlanarGraphPoint(_functions[f].getFunction() + pointCount++, leftPt.getX(), leftPt.getY());
                graph.addNode(leftGrPt, NodePointT.INTERSECTION);

                Point rightPt = fIntersections.get(pIndex + 1);
                PlanarGraphPoint rightGrPt = new PlanarGraphPoint(_functions[f].getFunction() + pointCount++, rightPt.getX(), rightPt.getY());
                graph.addNode(rightGrPt, NodePointT.INTERSECTION);

                //
                // Midpoint
                //
                double midX = Utilities.midpoint(leftPt.getX(),  rightPt.getX());
                double midY = _functions[f].evaluateAtPoint(midX).RealPart;
                PlanarGraphPoint midGrPt = new PlanarGraphPoint(_functions[f].getFunction() + pointCount++, midX, midY);
                graph.addNode(midGrPt, NodePointT.MIDPOINT);

                //
                // Add the two edges
                //
                graph.addUndirectedEdge(leftGrPt, midGrPt, new PlanarEdgeAnnotation(_functions[f]));
                graph.addUndirectedEdge(midGrPt, rightGrPt, new PlanarEdgeAnnotation(_functions[f]));
            }
        }

        // Debug printing of points
        for (PlanarGraphNode<NodePointT, PlanarEdgeAnnotation> node : graph.getNodes())
        {
            System.out.println(node.getPoint());
        }
    }
    
    /**
     * To minimize external calls, compute the N x N sets of intersections one time (not twice)
     * @return a populated N x N matrix of sets of intersections
     * For simplicity, this matrix will be fully populated [m, n] mirrors [n, m]
     * Each such Set of points is lexicographically ordered (really, by x-values)
     */
    protected void computeIntersections()
    {
        //
        // Initialize the matrix
        //
        for (int row = 0; row < _functions.length; row++)
        {
            _intersections.add(new Vector<Vector<Point>>());
        }

        // Analyze one function at a time: find all intersections of f with ALL g
        for (int f = 0; f < _functions.length; f++)
        {
            for (int g = f; g < _functions.length; g++)
            {
                if (f == g) // Do not intersect a function with itself
                {
                    // For safety create an empty intersection list
                    _intersections.get(f).add(new Vector<Point>());
                }
                else
                {
                    // Identify the intersection points and add to the set; we are interested in ALL points of intersection
                    // domain with eventually narrow this down further
                    double lowerX = LOWERBOUND_X;
                    double upperX = UPPERBOUND_X;
                    if (_domain != null)
                    {
                        if (_domain.getLowerBound() > LOWERBOUND_X) lowerX = _domain.getLowerBound();
                        if (_domain.getUpperBound() < UPPERBOUND_X) upperX = _domain.getUpperBound();
                    }
                    Vector<Point> intersections = Intersection.getInstance().allIntersections(_functions[f], _functions[g], lowerX, upperX);

                    // Assign to the matrix
                    _intersections.get(f).add(intersections);
                    _intersections.get(g).add(intersections);
                }
            }
        }

        //
        // Debug printing of the contents of the intersection points
        //
        // Analyze one function at a time: find all intersections of f with ALL g
        System.out.println("Intersection matrix: ");
        for (int f = 0; f < _functions.length; f++)
        {
            System.out.print("\t");
            for (int g = 0; g < _functions.length; g++)
            {
                System.out.print(_intersections.get(f).get(g).size() + " ");
            }
            System.out.println();
        }
    }

    /**
     * @param fIndex -- the index of the function we are interested in
     * @return the set of all points for which function f intersects all other functions (Functions \setminus {f})
     */
    protected Vector<Point> collectIntersections(int fIndex)
    {
        //
        // Collect all intersection points into a set
        //
        Set<Point> intersectionSet = new HashSet<Point>();
        for (Vector<Point> intersections : _intersections.get(fIndex))
        {
            intersectionSet.addAll(intersections);
        }

        //
        // Convert the Set to a lexicographically ordered Vector
        //
        Vector<Point> ordered = new Vector<Point>(intersectionSet);
        Collections.sort(ordered);

        return ordered;
    }
}

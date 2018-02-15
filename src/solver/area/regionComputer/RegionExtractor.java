package solver.area.regionComputer;

import java.util.ArrayList;
import java.util.Vector;

import representation.bounds.functions.Domain;
import representation.regions.Region;
import solver.TextbookProblem;
import solver.area.regionComputer.calculator.FacetCalculator;
import solver.area.regionComputer.calculator.elements.MinimalCycle;
import solver.area.regionComputer.calculator.elements.Primitive;
import solver.area.regionComputer.graphBuilder.PlanarGraphBuilder;
import solver.area.regionComputer.undirectedPlanarGraph.NodePointT;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarEdgeAnnotation;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraph;

/**
 * Given a textbook problem, extract all of its regions:
 *    (1) Create planar graph
 *    (2) Compute facets
 *    (3) Convert facets to area problem regions
 */
public class RegionExtractor
{
    // The problem we are acting on
    protected TextbookProblem _problem;

    // Facets extracted from analysis
    protected ArrayList<Primitive> _primitives;
    public ArrayList<Primitive> getPrimitives() { return _primitives; }

    // Facets extracted from analysis
    protected ArrayList<MinimalCycle> _facets;
    public ArrayList<MinimalCycle> getFacets() { return _facets; }
    
    // All regions we identify through facet identification
    protected Vector<Region> _regions;

    // The domain for these functions;
    // it is to be computed through the building process (and cannot be dictated).
    // The domain is dictated by the extreme values of the resulting planar graph:
    // x-values of the least and greatest lexicographic vertices in the graph
    protected Domain _domain;
    public Domain getDeducedDomain() { return _domain; }

    public RegionExtractor(TextbookProblem tap)
    {
        _problem = tap;
        _regions = null;
        _domain = null;
    }

    /**
     * @return on-demand computation of all regions through facet identification
     */
    public Vector<Region> getRegions()
    {
        if (_regions == null) buildRegions();

        return _regions;
    }

    /**
     * Main Planar graph based algorithm for identifying all regions in the stated problem
     */
    protected void buildRegions()
    {
        //
        // (1) Based on this textbook problem, compute the planar graph 
        //
        PlanarGraphBuilder builder = new PlanarGraphBuilder(_problem);

        builder.build();

        PlanarGraph<NodePointT, PlanarEdgeAnnotation> planarGraph = builder.getGraph();
        PlanarGraph<NodePointT, PlanarEdgeAnnotation> copy = new PlanarGraph<NodePointT, PlanarEdgeAnnotation>(planarGraph);
        
        //
        // (2) Perform Facet Indentification on the Planar graph
        //
        FacetCalculator<NodePointT, PlanarEdgeAnnotation> fCalculator = new FacetCalculator<NodePointT, PlanarEdgeAnnotation>(planarGraph);

        _primitives = fCalculator.getPrimitives();
        
        _facets = refinePrimitivesToCycles(fCalculator.getPrimitives());

        //
        // (3) Convert Facets back to regions
        //
        _regions = convertFacetsToRegions(copy);
    }

    /**
     * @param primitives -- a set of primitives (from facet identification)
     * @return a refined list of primitives containing only the elementary cycles of the planar graph
     */
    protected ArrayList<MinimalCycle> refinePrimitivesToCycles(ArrayList<Primitive> primitives)
    {
        ArrayList<MinimalCycle> cycles = new ArrayList<MinimalCycle>();
        for (Primitive primitive : primitives)
        {
            if (primitive instanceof MinimalCycle)
            {
                cycles.add((MinimalCycle)primitive);
            }
        }
        return cycles;
    }       

    /**
     * @param primitives -- a set of facets describing the planar graph
     * Construct the corresponding set of Regions from the facets
     */
    protected Vector<Region> convertFacetsToRegions(PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph)
    {
        Vector<Region> regions = new Vector<Region>();
        for (MinimalCycle facet : _facets)
        {
            regions.add(facet.convertFacetToRegion(graph));
        }
        return regions;
    }
}
package solver.area.regionComputer;

import solver.TextbookProblem;
import solver.area.regionComputer.graphBuilder.PlanarGraphBuilder;

/**
 * Given a textbook problem, extract all of its regions:
 *    (1) Create planar graph
 *    (2) Compute facets
 *    (3) Convert facets to area problem regions
 */
public class TextbookProblemRegionExtractor extends RegionExtractor
{
    // The problem we are acting on
    protected TextbookProblem _problem;

    public TextbookProblemRegionExtractor( TextbookProblem tap )
    {
    	super() ;
        _problem = tap ;
    }

    /**
     * Main Planar graph based algorithm for identifying all regions in the stated problem
     */
    @Override
    protected void buildRegions()
    {
        // (1) Based on this textbook problem, compute the planar graph 
        PlanarGraphBuilder builder = new PlanarGraphBuilder( _problem ) ;

        builder.build() ;

    	buildRegionsFromGraph( builder.getGraph() ) ;
    }
}
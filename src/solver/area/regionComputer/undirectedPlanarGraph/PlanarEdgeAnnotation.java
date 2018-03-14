package solver.area.regionComputer.undirectedPlanarGraph;

import representation.bounds.Bound;

//
// General Annotation object for Planar graph edges
//
public class PlanarEdgeAnnotation
{
    // The bound for which the edge is described
    protected Bound _bound ;
    public Bound getBound() { return _bound ; }

    public PlanarEdgeAnnotation( Bound bound )
    {
    	_bound = bound ;
    }
    
    //
    // ...anything else we need? 'Level of difficulty' associated with the particular function?
    //
    public String toString()
    {
        return _bound.toCompactLatexString() ;
    }
}

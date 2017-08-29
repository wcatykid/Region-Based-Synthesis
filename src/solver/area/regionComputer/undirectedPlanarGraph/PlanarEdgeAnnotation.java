package solver.area.regionComputer.undirectedPlanarGraph;

import representation.bounds.functions.StringBasedFunction;

//
// General Annotation object for Planar graph edges
//
public class PlanarEdgeAnnotation
{
    // The function for which the edge is described
    protected StringBasedFunction _function;
    public StringBasedFunction getFunction() { return _function; }

    public PlanarEdgeAnnotation(StringBasedFunction function)
    {
        _function = function;
    }
    
    //
    // ...anything else we need? 'Level of difficulty' associated with the particular function?
    //
    public String toString()
    {
        return _function.toCompactLatexString();
    }
}

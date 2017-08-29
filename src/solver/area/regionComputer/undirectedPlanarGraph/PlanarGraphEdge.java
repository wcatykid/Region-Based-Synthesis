package solver.area.regionComputer.undirectedPlanarGraph;

/**
 * An annotated edge for a planar graph
 */
public class PlanarGraphEdge<E>
{
    // the Point of the edge
    protected PlanarGraphPoint _target;
    public PlanarGraphPoint getTarget() { return _target; }
    
    // Edge annotated with any information
    protected E _annotation;
    public E getAnnotation() { return _annotation; }
    
    // if the edge is a cycle or not
    protected boolean isCycle;
    
    /**
     * Full Constructor
     * @param targ          the target Point
     * @param type          the EdgeType
     */
    public PlanarGraphEdge(PlanarGraphPoint target, E annotation)
    {
        _target = target;
        _annotation = annotation;
        
        isCycle = false;
    }
    
    /**
     * For quick construction only
     * @param targ  the target Point
     */
    public PlanarGraphEdge(PlanarGraphPoint targ)
    {
        this._target = targ;
    }
    
    
    /**
     * Shallow Copy Constructor
     * @param that
     */
    public PlanarGraphEdge(PlanarGraphEdge<E> that)
    {
        _target = that._target;
        _annotation = that._annotation;
    }
    
    /* 
     * Overrides the Object hashCode() method
     */
    public int hashCode()
    {
        return super.hashCode();
    }
    
    /*
     * Equality is only based on the point in the graph
     */
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        
        if (!(obj instanceof PlanarGraphEdge)) return false;
        
        @SuppressWarnings("unchecked")
        PlanarGraphEdge<E> that = (PlanarGraphEdge<E>) obj;
        
        return this._target.equals(that._target);
    }
    
    public void markCyclic() { isCycle = true; }
    public void unmarkCyclic() { isCycle = false; }
    public boolean isCyclic() { return isCycle; }
    
    /* 
     * Overrides the toString method
     */
    public String toString()
    {
        return _target.toString() + "(" + _annotation + ")";
    }
}

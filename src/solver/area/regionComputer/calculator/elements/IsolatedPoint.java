package solver.area.regionComputer.calculator.elements;

import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphPoint;

/**
 * A class to store isolated Points.
 */
public class IsolatedPoint extends Primitive
{
    // Variables
    protected PlanarGraphPoint _thePoint;
    public PlanarGraphPoint getPoint() { return _thePoint; }
    
    /**
     * Constructor
     */
    public IsolatedPoint()
    {
        this(null);
    }
    
    public IsolatedPoint(PlanarGraphPoint p)
    {
        super();

        _thePoint = p;
    }
    
    /**
     * Sets the Point 
     * @param set
     */
    public void setPoint(PlanarGraphPoint set)
    {
        _thePoint = set;
    }
        
    /* 
     * Return the IsolatedPoint as a String
     */
    public String toString()
    {
        return "Point { " + _thePoint.toString() + " }";
    }

}

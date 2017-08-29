package math.external_interface;

import representation.Point;
import representation.bounds.functions.BoundedFunction;

public abstract class CasInterface
{
    // Returns a String-based representation of the two given functions
    public abstract String getIntersection(BoundedFunction func1, BoundedFunction func2);
    
    public abstract String getLagrangePolynomial(Point[] points);

    // Can we establish the CAS connection?
    public abstract boolean connection();
}
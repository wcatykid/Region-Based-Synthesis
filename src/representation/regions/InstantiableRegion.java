package representation.regions;

import java.io.Serializable;

public class InstantiableRegion extends Region implements Serializable, Cloneable
{
    private static final long serialVersionUID = -6801870320013466345L;

    public InstantiableRegion() { super(); }
                                   // _left, _top, _right, _bottom
    public InstantiableRegion(LeftRight ell, TopBottom t, LeftRight r, TopBottom b) { super(ell, t, r, b); }
    public InstantiableRegion(InstantiableRegion that) { super(that); }

    //
    //
    // Instantiation functions
    //
    //
//    public Bound last_bottomBound() { return __bottom.lastBound(); }
//    public void instantiate_left(Point pt) { _left = new LeftRight(new PointBound(pt)); }
//    public void instantiate_left(LineSegment line) { _left = new LeftRight(new LineSegment(line)); }

    //
    //
    // Accessor functions
    //
    //
    //    public boolean _leftBoundPoint() { return _left.isPoint(); }
    //    public boolean _leftBoundVertical() { return _left.isVertical(); }
    //    public boolean _rightBoundPoint() { return _right.isPoint(); }
    //    public boolean _rightBoundVertical() { return _right.isVertical(); }





    
    
    //
    //
    // Standard Utilities
    //
    //
    @Override
    public boolean equals(Object obj) { return super.equals(obj); }

    @Override
    public InstantiableRegion clone() { return (InstantiableRegion) super.clone(); }

    @Override
    public String toString(){ return super.toString(); }
}

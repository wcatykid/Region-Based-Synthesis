package representation;

/**
 * Factory to generate simple vector representation
 *
 */
public class MyVector
{
    public final static MyVector ZERO_VECTOR = new MyVector(0, 0);

    protected double _x;
    protected double _y;
    protected double _angle;

    public MyVector(double x, double y)
    {
        _x = x;
        _y = y;
        _angle = Double.NEGATIVE_INFINITY;
    }

    public MyVector(Point origin, Point other)
    {
        this (other.getX() - origin.getX(), other.getY() - origin.getY());
    }

    public MyVector add(MyVector that)
    {
        return new MyVector(_x + that._x, _y + that._y);
    }

    public MyVector subtract(MyVector that)
    {
        return new MyVector(_x - that._x, _y - that._y);
    }

    public void multiply(double scale)
    {
        _x *= scale;
        _y *= scale;
    }

    public double lengthSquared()
    {
        return _x * _x + _y * _y;
    }

    public double length() { return Math.sqrt(lengthSquared()); }

    public void normalize()
    {
        if (this.equals(ZERO_VECTOR)) return;

        multiply(1.0 / length());
    }

    public double dotProduct(MyVector that) { return _x * that._x + _y * that._y; }

    /**
     *   Returns the angle in radians between this vector and the vector
     *   parameter; the return value is constrained to the range [0,PI].
     *   @param that    the other vector
     *   @return   the angle in radians in the range [0,PI]
     */
    public final double angle(MyVector that)
    {
        double vDot = this.dotProduct(that) / (this.length() * that.length());

        if( vDot < -1.0) vDot = -1.0;

        if( vDot >  1.0) vDot =  1.0;

        return((double) (Math.acos( vDot )));
    }

    /**
     *   Returns the angle in radians between this vector and the standard position vector <1, 0>;
     *   the return value is constrained to the range [0,PI].
     *   @param that    the other vector
     *   @return   the angle in radians in the range [0,PI]
     */
    public final double angle()
    {
        if (_angle == Double.NEGATIVE_INFINITY) _angle = angle(new MyVector(1, 0));
        
        return _angle;
    }
}

package representation.bounds;

import java.io.Serializable;
import exceptions.DomainException;
import representation.ComplexNumber;
import representation.Point;
import representation.bounds.functions.Domain;
import representation.bounds.functions.FunctionT;
import utilities.Utilities;

public abstract class Bound implements Serializable
{
    private static final long serialVersionUID = 3073553057593456605L;


    protected BoundT _type;
    public BoundT getBoundT() { return _type; }

    public Bound()
    {
        _domain = new Domain();
        _type = BoundT.UNSPECIFIED;
    }
    public Bound(BoundT _type)
    {
        this._type = _type;
        _domain = new Domain();
    }


    //    public abstract Point getLeftTopPoint();
    //    public abstract Point getLeftBottomPoint();
    public abstract Object clone();

    /**
     * @param x
     * @return f(x)
     */
    public abstract ComplexNumber evaluateAtPoint(double x);

    /**
     * @param y
     * @return x = f(y)  OR y = f^{-1}(x) assuming we have a 1-1 piece of a function
     */
    public abstract ComplexNumber evaluateAtPointByY(double y);
    
    public boolean isPoint() { return false; }
    public boolean isVertical() { return false; }
    public Point getMinimum() { return null; }
    public Point getMaximum() { return null; }

    protected Domain _domain;

    public double leftBoundX() { return _domain.getLowerBound(); }
    public double rightBoundX() { return _domain.getUpperBound(); }

    public void setDomainUpperBound(double right) { _domain.setDomainUpperBound(right); }
    public void setDomain(double left, double right) { _domain = new Domain(left, right); }
    public void setDomain(Domain that) { _domain = new Domain(that); }
    public Domain getDomain() { return _domain; }
    public boolean inDomain(double x) { return _domain.withinBounds(x); }
    public boolean isHorizontal() { return false; }

    public abstract Bound inverse();

    /**
     * @param y1 -- y-valued double
     * @param y2 -- y-valued double
     * @return whether the stated y-values are within the range of this bound.
     */
    public boolean inRange(double y1, double y2)
    {
        // Acquire the range values
        double lowRangeY = evaluateAtPoint(_domain.getLowerBound()).RealPart;
        double upperRangeY = evaluateAtPoint(_domain.getUpperBound()).RealPart;

        // Check whether the given interval is within the range interval
        return inRange(y1, y2, lowRangeY, upperRangeY);
    }

    /**
     * @param y1 -- y-valued double
     * @param y2 -- y-valued double
     * @return whether the stated y-values are within the range of this bound.
     */
    protected boolean inRange(double y1, double y2, double lowRangeY, double upperRangeY)
    {
        //
        // Order the input values
        //
        double lowY = Double.NEGATIVE_INFINITY;
        double highY = Double.NEGATIVE_INFINITY;

        if (Utilities.equalDoubles(y1, y2))
        {
            lowY = y1;
            highY = y1;
        }
        else if (y1 < y2)
        {
            lowY = y1;
            highY = y2;
        }
        else if (y2 < y1)
        {
            lowY = y1;
            highY = y2;
        }

        if (lowRangeY > upperRangeY)
        {
            double temp = lowRangeY;
            lowRangeY = upperRangeY;
            upperRangeY = temp;
        }

        //
        // Check if the input is within the range values
        //
        if (!Utilities.lessThanOrEqualDoubles(highY, upperRangeY)) return false;

        if (!Utilities.lessThanOrEqualDoubles(lowRangeY, lowY)) return false;

        return true;
    }

    /**
     * @param pt1 -- a point
     * @param pt2 -- a point
     * @return whether these point are the same as the endpoints of this bound
     * @throws DomainException -- if the domain has not been specified for this bound
     */
    public boolean definedBy(Point pt1, Point pt2) throws DomainException
    {
        return endpoint(pt1) && endpoint(pt2);
    }

    /**
     * @param pt1 -- a point
     * @param pt2 -- a point
     * @return whether these point are the same as the endpoints of this bound
     * @throws DomainException -- if the domain has not been specified for this bound
     */
    public boolean endpoint(Point pt) throws DomainException
    {
        if (_domain == null) throw new DomainException("Domain not defined: null");
        if (!_domain.isBounded()) throw new DomainException("Domain is unbounded");

        // Equate x-values first; then check y-values
        if (Utilities.equalDoubles(_domain.getLowerBound(), pt.getX()))
        {
            double leftY = evaluateAtPoint(_domain.getLowerBound()).RealPart;
            return Utilities.equalDoubles(leftY, pt.getY());
        }
        else if (Utilities.equalDoubles(_domain.getUpperBound(), pt.getX()))
        {
            double rightY = evaluateAtPoint(_domain.getUpperBound()).RealPart;
            return Utilities.equalDoubles(rightY, pt.getY());
        }

        return false;
    }


    // Does the given value align with the right endpoint of this domain?
    public boolean isRightEndpoint(double x){ return Utilities.equalDoubles(x,  _domain.getUpperBound()); }

    /**
     * @return String-based representation of this function using ALL valuesof a, b, h, k
     */
    public abstract String toFullMathematicaString();

    /**
     * @return a string-based representation with identity-based information omitted (1 with multiplication and 0 with addition)
     */
    public abstract String toCompactLatexString();

    /**
     * @param that -- a bound with a domain
     * @return whether the situation look as follows:
     *                left this *---------------*right this == left that --------------------* right that
     */
    public boolean leftEndpointAlignsWithRightEndpointofThis(Bound that)
    {
        if (!_domain.finiteIntersection(that.getDomain())) return false;

        return Utilities.equalDoubles(this.rightBoundX(), that.leftBoundX());
    }

    /**
     * @param g -- a function
     * @return whether this function intersects g at all
     */
    public boolean domainIntersects(Bound g)
    {
        return this.domainFiniteOverlap(g) || this.domainInfiniteOverlap(g);
    }

    /**
     * @param that -- a bound
     * @return whether the domain of this bound overlaps with the domain at more than a single point 
     */
    public boolean domainFiniteOverlap(Bound that)
    {
        return _domain.finiteIntersection(that.getDomain());
    }

    /**
     * @param that -- a bound
     * @return whether the domain of this bound overlaps infinitely with the domain (overlapping intervals) 
     */
    public boolean domainInfiniteOverlap(Bound that)
    {
        return _domain.infiniteIntersection(that.getDomain());
    }

    public enum BoundT
    {
        POINT(0),
        VERTICAL_LINE(1),
        FUNCTION(2),
        HORIZONTAL_LINE(3),
        LINEAR(4),
        PARABOLA(5),
        CUBIC(6),
        QUARTIC(7),
        QUINTIC(8),
        EXPONENTIAL(9),
        LOGARITHMIC(10),
        SINE(11),
        COSINE(12),
        STRING(13),
        UNSPECIFIED(14);

        private final int value;
        private BoundT(int value) { this.value = value; }
        public int getValue() { return value; }

        public static BoundT convertToBound(char c) throws IllegalArgumentException
        {
            switch (c)
            {
                case 'p':
                case 'P':
                    return POINT;
                case 'v':
                case 'V':
                    return VERTICAL_LINE;
                case 'f':
                case 'F':
                    return FUNCTION;

                default:
                    throw new IllegalArgumentException(c + " not recognized " + "convertToBound");
            }
        }

        public static BoundT convertToBound(String str) throws IllegalArgumentException
        {
            if (str.equalsIgnoreCase("H_LINE") || str.equalsIgnoreCase("H"))
            {
                return HORIZONTAL_LINE;
            }
            else if (str.equalsIgnoreCase("LINE") || str.equalsIgnoreCase("L"))
            {
                return LINEAR;
            }
            else if (str.equalsIgnoreCase("PARABOLA") || str.equalsIgnoreCase("POLY_2"))
            {
                return PARABOLA;
            }
            else if (str.equalsIgnoreCase("CUBIC") || str.equalsIgnoreCase("POLY_3"))
            {
                return CUBIC;
            }
            else if (str.equalsIgnoreCase("QUARTIC") || str.equalsIgnoreCase("POLY_4"))
            {
                return QUARTIC;
            }
            else if (str.equalsIgnoreCase("QUINTIC") || str.equalsIgnoreCase("POLY_5"))
            {
                return QUINTIC;
            }
            else if (str.equalsIgnoreCase("EXPONENTIAL") || str.equalsIgnoreCase("EXP"))
            {
                return EXPONENTIAL;
            }
            else if (str.equalsIgnoreCase("LOGARITHMIC") || str.equalsIgnoreCase("LOG"))
            {
                return LOGARITHMIC;
            }
            else if (str.equalsIgnoreCase("SINE") || str.equalsIgnoreCase("SIN"))
            {
                return SINE;
            }
            else if (str.equalsIgnoreCase("COSINE") || str.equalsIgnoreCase("COS"))
            {
                return COSINE;
            }
            else if (str.equalsIgnoreCase("STRING"))
            {
                return COSINE;
            }
            else
            {
                throw new IllegalArgumentException(str + " not recognized " + "convertToBound");
            }
        }

        public static char convertFromBound(BoundT b) throws IllegalArgumentException
        {
            switch (b)
            {
                case POINT:
                    return 'P';

                case VERTICAL_LINE:
                    return 'V';

                case FUNCTION:
                    return 'F';

                case HORIZONTAL_LINE:
                    return 'H';

                case LINEAR:
                    return 'L';

                case PARABOLA:
                    return '2';

                default:
                    throw new IllegalArgumentException(b + " not recognized " + "convertFromBound");
            }
        }

        public FunctionT inFunctionTForm()
        {
            switch (value)
            {
                case 3: return FunctionT.HORIZONTAL_LINE;
                case 4: return FunctionT.LINEAR;
                case 5: return FunctionT.PARABOLA;
                case 6: return FunctionT.CUBIC;
                case 7: return FunctionT.QUARTIC;
                case 8: return FunctionT.QUINTIC;
                case 9: return FunctionT.EXPONENTIAL;
                case 10: return FunctionT.LOGARITHMIC;
                case 11: return FunctionT.SINE;
                case 12: return FunctionT.COSINE;
                case 13: return FunctionT.STRING;
                default: return null;
            }
        }
    }
}
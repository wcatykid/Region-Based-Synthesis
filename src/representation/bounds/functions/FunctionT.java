package representation.bounds.functions;

import utilities.Utilities;

public enum FunctionT
{
    HORIZONTAL_LINE(0),
    LINEAR(1),
    PARABOLA(2),
    CUBIC(3),
    QUARTIC(4),
    QUINTIC(5),
    //POLYNOMIAL(6),

    EXPONENTIAL(6),
    LOGARITHMIC(7),

    SINE(8),
    COSINE(9),

    STRING(10),

    COMPOSITE(11); // f(x) + g(x)
    //UNSPECIFIED(11);

    private final int value;
    private FunctionT(int value) { this.value = value; }
    public int getValue() { return value; }

    public double evaluate(double a, double b, double h, double k, double x)
    {
        switch (this)
        {
            case HORIZONTAL_LINE:
                return k;

            case LINEAR:
                return a * b * (x - h) + k;

            case PARABOLA:
                return a * Math.pow(b * (x - h), 2) + k;

            case CUBIC:
                return a * Math.pow(b * (x - h), 3) + k;

            case QUARTIC:
                return a * Math.pow(b * (x - h), 4) + k;

            case QUINTIC:
                return a * Math.pow(b * (x - h), 5) + k;

                //case POLYNOMIAL:
                //  throw new IllegalArgumentException(POLYNOMIAL + " not recognized FunctionT.evaluate()");

            case EXPONENTIAL:
                return a * Math.pow(Math.E, b * (x - h)) + k;

            case LOGARITHMIC:
                return a * Math.log(b * (x - h)) + k;

            case SINE:
                return a * Math.sin(b * (x - h)) + k;

            case COSINE:
                return a * Math.cos(b * (x - h)) + k;

                //case UNSPECIFIED:
            default:
                throw new IllegalArgumentException(this.value + " not recognized FunctionT.evaluate()");
        }
    }

    /**
     * @param a -- vertical stretch / shrink
     * @param b -- horizontal stretch / shrink
     * @param h -- horizontal shift
     * @param k -- vertical shift
     * @return a string-based representation with the 4 constants appearing (regardless of identity value or not )
     */
    public String toFullMathematicaString(double a, double b, double h, double k, VariableT variable)
    {
        String horizontal = horizontal(b, h, variable);

        switch (this)
        {
            case HORIZONTAL_LINE:
                return Double.toString(k);

            case LINEAR:
                return a + " * ( " + horizontal + " ) + " + k;

            case PARABOLA:
                return a + " * ( " + horizontal + " )^2 + " + k;

            case CUBIC:
                return a + " * ( " + horizontal + " )^3 + " + k;

            case QUARTIC:
                return a + " * ( " + horizontal + " )^4 + " + k;

            case QUINTIC:
                return a + " * ( " + horizontal + " )^5 + " + k;

                //case POLYNOMIAL:
                //  throw new IllegalArgumentException(POLYNOMIAL + " not recognized FunctionT.toFunctionString()");

            case EXPONENTIAL:
                return a + " * Exp [ " + horizontal + " ] + " + k;

            case LOGARITHMIC:
                return a + " * Log [ " + horizontal + " ] + " + k; // Natural log in Mathematica

            case SINE:
                return a + " * Sin [ " + horizontal + " ] + " + k; // Sine in Mathematica

            case COSINE:
                return a + " * Cos [ " + horizontal + " ] + " + k; // Cosine in Mathematica

                //case UNSPECIFIED:
            default:
                throw new IllegalArgumentException(this.value + " not recognized FunctionT.toString()");
        }
    }

    /**
     * @param a -- vertical stretch / shrink
     * @param b -- horizontal stretch / shrink
     * @param h -- horizontal shift
     * @param k -- vertical shift
     * @return a string-based representation with identity-based information omitted (1 with multiplication and 0 with addition)
     */
    public String toCompactLatexString(double a, double b, double h, double k, VariableT variable)
    {
        String horizS = horizontalCompact(b, h, variable);

        //
        // Apply the actual base function
        //
        String baseFunction = "";
        switch (this)
        {
            case HORIZONTAL_LINE:
                baseFunction += Double.toString(k);
                break;
            case LINEAR:
                baseFunction += horizS;
                break;
            case PARABOLA:
                baseFunction += "(" + horizS + ")^2";
                break;
            case CUBIC:
                baseFunction += "(" + horizS + ")^3";
                break;
            case QUARTIC:
                baseFunction += "(" + horizS + ")^4";
                break;
            case QUINTIC:
                baseFunction += "(" + horizS + ")^5";
                break;
                //case POLYNOMIAL:
                //  throw new IllegalArgumentException(POLYNOMIAL + " not recognized FunctionT.toFunctionString()");
            case EXPONENTIAL:
                baseFunction += "e^{ " + horizS + " }";
                break;
            case LOGARITHMIC:
                baseFunction += "\\log{ " + horizS + " }";
                break;
            case SINE:
                baseFunction += "\\sin{ " + horizS + " }";
                break;
            case COSINE:
                baseFunction += "\\cos{ " + horizS + " }";
                break;
                //case UNSPECIFIED:
            default:
                throw new IllegalArgumentException(this.value + " not recognized FunctionT.toString()");
        }

        String function = baseFunction;
        if (this != HORIZONTAL_LINE) function = applyCompactVertical(a, k, baseFunction);

        return function;
    }

    /**
     * @param b -- horizontal stretch / shrink
     * @param h -- horizontal shift
     * @param variable -- X or Y
     * @return a string-based representation of horizontal components of a function
     */
    private String horizontal(double b, double h, VariableT variable)
    {
        return b + " * (" + variable.toString() + " - " + h + ")";
    }

    /**
     * @param b -- horizontal stretch / shrink
     * @param h -- horizontal shift
     * @param variable -- X or Y
     * @return a string-based representation of horizontal components of a function
     */
    private String horizontalCompact(double b, double h, VariableT variable)
    {
        String s = "";

        // b * 
        if (!Utilities.equalDoubles(b, 1))
        {
            s += b + " * ( ";
        }

        s += variable.toString();

        if (!Utilities.equalDoubles(h, 0)) s += " - " + h;     

        if (!Utilities.equalDoubles(b, 1))
        {
            s += " ) ";
        }

        return s;
    }

    /**
     * @param a -- vertical stretch / shrink
     * @param k -- vertical shift
     * @param fx -- String representation of f(b(x - h)) 
     * @return String representation of a * f(b(x - h)) + k
     */
    private String applyCompactVertical(double a, double k, String fx)
    {
        String s = "";

        // b * 
        if (!Utilities.equalDoubles(a, 1))
        {
            s += a + " * ";
        }

        s += fx;

        if (!Utilities.equalDoubles(k, 0)) s += " + " + k;

        return s;
    }

    @Override
    public String toString()
    {
        switch (this)
        {
            case HORIZONTAL_LINE: return "horizontal line";
            case LINEAR: return "linear";
            case PARABOLA: return "parabola";
            case CUBIC: return "cubic";
            case QUARTIC: return "quartic";
            case QUINTIC: return "quintic";
            //case POLYNOMIAL: return "polynomial";
            //return "";

            case EXPONENTIAL:
                return "e";

            case LOGARITHMIC:
                return "ln";

            case SINE:
                return "sin";

            case COSINE:
                return "cos";

                //case UNSPECIFIED:
            default:
                throw new IllegalArgumentException(this.value + " not recognized FunctionT.toString()");
        }
    }
}
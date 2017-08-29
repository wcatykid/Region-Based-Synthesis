package utilities;

import java.util.ArrayList;

public class Utilities
{
    // Handles negatives and positives.
    public static int modulus(int x, int m) { return (x % m + m) % m; }

    public static boolean isInteger(double x) { return Utilities.equalDoubles(x, (int)x); }
    public static int GCD(int a, int b) { return b == 0 ? a : GCD(b, a % b); }

    //
    // Comparing double within a threshold
    //
    private static double EPSILON = 0.00001;
    public static boolean equalDoubles(double a, double b)
    {
        // Check the special case of positive and negative infinity
        if (Double.isInfinite(a)) return a == b;
        
        // Check the epsilon-ball
        return Math.abs(a - b) < EPSILON;
    }
    
    //
    // Comparing double within a threshold
    //
    private static double LOOSE_EPSILON = 0.001;
    public static boolean looseEqualDoubles(double a, double b)
    {
        // Check the special case of positive and negative infinity
        if (Double.isInfinite(a)) return a == b;
        
        // Check the epsilon-ball
        return Math.abs(a - b) < LOOSE_EPSILON;
    }
    
    public static double midpoint(double a, double b) {
        return a + ((b - a) / 2);
    }

    public static boolean lessThanOrEqualDoubles(double less, double greater) {
        return equalDoubles(less, greater) || less < greater;
    }
    public static boolean greaterThanOrEqualDoubles(double greater, double less) {
        return equalDoubles(less, greater) || less < greater;
    }

    // -1 is an error
    public static int integerRatio(double x, double y)
    {
        return Utilities.equalDoubles(x / y, Math.floor(x / y)) ? (int)Math.floor(x / y) : -1;
    }

    // -1 is an error
    // A reasonable value for geometry problems must be less than 10 for a ratio
    // This is arbitrarily chosen and can be modified
    private static int RATIO_MAX = 10;
    public static Pair<Integer, Integer> rationalRatio(double x, double y)
    {
        for (int numer = 2; numer < RATIO_MAX; numer++)
        {
            for (int denom = 1; denom < RATIO_MAX; denom++)
            {
                if (numer != denom)
                {
                    if (Utilities.equalDoubles(x / y, (double)(numer) / denom))
                    {
                        int gcd = GCD(numer, denom);
                        return numer > denom ? new Pair<Integer, Integer>(numer / gcd, denom / gcd)
                                : new Pair<Integer, Integer>(denom / gcd, numer / gcd);
                    }
                }
            }
        }

        return new Pair<Integer, Integer>(-1, -1);
    }

    //
    //
    //
    public static Pair<Integer, Integer> rationalRatio(double x)
    {
        for (int val = 2; val < RATIO_MAX; val++)
        {
            // Do we acquire an integer?
            if (Utilities.equalDoubles(x * val, Math.floor(x * val)))
            {
                int gcd = GCD(val, (int)Math.round(x * val));
                return x < 1 ? new Pair<Integer, Integer>(val / gcd, (int)Math.round(x * val) / gcd) :
                    new Pair<Integer, Integer>((int)Math.round(x * val) / gcd, val / gcd);
            }
        }

        return new Pair<Integer, Integer>(-1, -1);
    }

    //
    // Is the given double a rational according to our 'controlled' algorithm?
    //
    public static boolean isRational(double x)
    {
        Pair<Integer, Integer> result = rationalRatio(x);

        return result.getFirst() != -1 || result.getSecond() != -1;
    }

    public static double removeFloatingError(double d) {
        return (d * 0.00000000000000000001) / 0.00000000000000000001;
    }

    public static <T> ArrayList<T> setDifference(ArrayList<T> larger, ArrayList<T> smaller)
    {
        ArrayList<T> difference = new ArrayList<T>();

        for (T item : smaller)
        {
            if (!larger.contains(item)) difference.add(item);
        }

        return difference;
    }

    /**
     * @param left -- a one-dimensional value
     * @param right -- a one-dimensional value
     * @return distance between them: the one-dimensional distance formula
     */
    public static double distance(double left, double right)
    {
        return Math.abs(left - right);
    }

    /**
     * @param left -- a one-dimensional value
     * @param val -- a one-dimensional value
     * @param right -- a one-dimensional value
     * @return if left, val, right are collinear (one-dimensionally)
     */
    public static boolean between(double left, double val, double right)
    {
        // Handle infinite cases; if (-inf -inf) or (+inf +inf) not between
        if (Double.isInfinite(left) && Double.isInfinite(right)) return left != right;
        
        // One-sided infinite intervals
        if (Double.isInfinite(left) && left == Double.NEGATIVE_INFINITY) return lessThanOrEqualDoubles(val, right);
        if (Double.isInfinite(right) && right == Double.POSITIVE_INFINITY) return greaterThanOrEqualDoubles(val, left);
        
        return equalDoubles(distance(left, val) + distance(val, right), distance(left, right));
    }

    /**
     * @param left -- a one-dimensional value
     * @param val -- a one-dimensional value
     * @param right -- a one-dimensional value
     * @return if left, val, right are collinear (one-dimensionally), val is distinct from the endpoints (left, right)
     */
    public static boolean betweenExclusive(double left, double val, double right)
    {
        // An infinite 'middle' value is not exclusively between anything
        if (Double.isInfinite(val)) return false;
        
        if (equalDoubles(left, val) || equalDoubles(val, right)) return false;

        return between(left, val, right);
    }
}

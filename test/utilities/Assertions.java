package utilities;

public class Assertions
{
    public static void Assert(double a, double b)
    {
        if (!Utilities.looseEqualDoubles(a, b))
        {
            System.out.print("Discrepancy:");
            System.out.println("\t" + a + " != " + b);
        }
        assert(Utilities.looseEqualDoubles(a, b));
    }
    
    public static void Assert(boolean left, boolean right)
    {
        if (left != right)
        {
            System.out.print("Discrepancy:");
            System.out.println("\t" + left + " != " + right);
        }
        assert(left == right);
    }
    
    public static void Assert(Object a, Object b)
    {
        if (!a.equals(b))
        {
            System.out.print("Discrepancy:");
            System.out.println("\t" + a + " != " + b);
        }
        assert(a.equals(b));
    }
}

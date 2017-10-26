package globals;
import representation.bounds.functions.FunctionT;

public final class Constants
{
	private Constants() {} // To ensure no instances of this class


    public final static int MIN_HORIZONTAL_SHIFT = 1;
    public final static int MAX_HORIZONTAL_SHIFT = 10;
    public final static int MIN_VERTICAL_SHIFT = 1;
    public final static int MAX_VERTICAL_SHIFT = 4;
    public final static int MIN_HORIZONTAL_STRETCH = 1;
    public final static int MAX_HORIZONTAL_STRETCH = 4;
    public final static int MIN_VERTICAL_STRETCH = 1;
    public final static int MAX_VERTICAL_STRETCH = 4;
    
	public static String APPID = "V7VR6U-2PAEQALEU5";
	// region-synth: V7VR6U-YT5QEVGQAG
	// WolframTest:  V7VR6U-2PAEQALEU5
	
	public static boolean CONTACT_WA_ENGINE = false;
	
	// Use this to limit the algorithm to a few functions. To be used with the ALLOWED_FUNCTIONS array.
	public static boolean LIMITED_FUNCTIONS = true;
	
    // The one below works fine, but it's silly how large the numbers get.
	public static FunctionT[] ALLOWED_FUNCTIONS = { FunctionT.HORIZONTAL_LINE, FunctionT.LINEAR, FunctionT.PARABOLA };
	
	//static final FunctionT[] ALLOWED_FUNCTIONS = { FunctionT.HORIZONTAL_LINE, FunctionT.LINEAR, FunctionT.PARABOLA, FunctionT.CUBIC, FunctionT.QUARTIC, FunctionT.QUINTIC}; //Only to be used in the WIP version of a program.

	// Vertical line segment bounds must be at least this long
    public static double MIN_VERTICAL_LINE_DELTA = 1.0;
    
	// The function can only be bound from n < x <= n + MAX_DOMAIN_SIZE where n is the starting bound.
    public static double MAX_VERTICAL_DELTA = 10.0;
    
	// The function can only be bound from n < x <= n + MAX_DOMAIN_SIZE where n is the starting bound.
	public static double MAX_RIGHT_X = 10.0;

	// This is here to have the code able to limit the functions to only ints.  This does not guarantee int intersect points.
	public static boolean INTEGERS_ONLY = true;

	// If you want the bounds to be the same length between functions.  This also requires the top and bottom to have the same number of functions.
	public static boolean SAME_LENGTH_BOUNDS = false;

	// Max number of attempts allowed to try and generate a top function.
	public static int MAX_ATTEMPTS = 50;

	// Limits b value to either 1 or pi.
	public static boolean SIMPLE_COEFFICIENTS = true;

	// top can intersect a bottom function.
	public static boolean ALLOW_INTERSECTIONS = false;

    // Path for Mathematica
    public static String MATHEMATICA_PATH = "C:\\Program Files\\Wolfram Research\\Mathematica\\11.2\\MathKernel.exe";

    // Whether top / bottom functions must begin and end on same x-values
    public static boolean ENFORCE_FUNCTION_BOUND_ALIGNMENT = false;
}

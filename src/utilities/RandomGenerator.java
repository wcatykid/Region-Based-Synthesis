package utilities;
import java.util.Random;

//
// A static class implementing a consistent random number generator
//
public final class RandomGenerator
{
    private static Random rng;
    
    static
    {
        rng = new Random();        
    }

	private RandomGenerator() { super(); }
	
    // Generates a random integer from [0, RAND_MAX]
    public static int nextInt()
    {
        return rng.nextInt();
    }

    // Generates a random integer from [0, max]
    public static int nextInt(int max)
    {
        return rng.nextInt(max);
    }

    // Generates a random integer from [min, max]
    public static int nextInt(int min, int max)
    {
        return min + (rng.nextInt() % (int)(max - min + 1));
    }
    
    /**
     * @param min
     * @param max
     * @return two unique integers in intervals in the range [min, max]
     */
    public static Pair<Integer, Integer> nextUnique(int min, int max)
    {
    	int val1 = nextInt(min, max);
    	int val2 = nextInt(min, max);

    	while (val1 == val2)
    	{
    		val2 = nextInt(min, max);
    	}

    	return new Pair<Integer, Integer>(val1, val2);
    }
    
    /**
     * @param min
     * @param max
     * @return two unique integers in intervals in the range [min, max]
     * Those values must have gcd(x, y) == 1
     */
    public static Pair<Integer, Integer> nextIrreducibleUnique(int min, int max)
    {
    	Pair<Integer, Integer> pair = nextUnique(min, max);

    	while (Utilities.GCD(pair.getFirst(), pair.getSecond()) != 1)
    	{
        	pair = nextUnique(min, max);
    	}
    	
        return pair;
    }
}

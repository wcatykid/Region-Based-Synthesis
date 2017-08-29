package utilities;

public class StringUtilities
{
    private final static String PREFIX = "--------------";
    private final static String SUFFIX = "--------------";
    private final static int INDENT_SIZE = 4;
    private final static char INDENT_CHAR = ' ';
    private final static String INDENT_STR;

    /**
     * @param sz -- width of the indentation (in no. of characters)
     * @param c -- the actual character to use for indentation
     * @return a String of length (sz) containing all characters (c)
     */
    private static String generateIndentString(int sz, char c)
    {
        String s = "";
        for (int i = 0; i < sz; i++)
        {
            s += c;
        }
        return s;
    }

    static
    {
        INDENT_STR = generateIndentString(INDENT_SIZE, INDENT_CHAR);
    }
    
    /**
     * @param sz -- width of the indentation (in no. of characters)
     * @param c -- the character to use for construction
     * @param level -- the number of times to indent
     * @return a String containing (level) number of indentation strings (specific)
     */
    private static String constrIndentString(int sz, char c, int level)
    {
        // Construct the basic string and repeat that string (level) number of times
        String indentStr = generateIndentString(sz, c);

        // Force a positive value
        level = Math.abs(level);
        String ret = "";
        for (int i = 0; i < level; i++)
        {
            ret += indentStr;
        }
        
        return ret;
    }

    /**
     * @param level -- the number of times to indent
     * @return a String containing (level) number of indentation strings (default)
     */
    private static String indentString(int level)
    {
        return constrIndentString(INDENT_SIZE, INDENT_CHAR, level);
    }
    

    /**
     * @param middle -- string to be sandwiched
     * @param indent -- level of indentation
     * @return a String with the middle sandwiched; this is a utility function
     */
    private static String sandwich(String middle, int indent)
    {
        return indentString(indent) + PREFIX + middle + SUFFIX;
    }
    
    public static String generateTestStartString(String name, int indent)
    {
        return sandwich(" Starting test: " + name, indent);
    }
    
    public static String generateTestEndString(String name, int indent)
    {
        return sandwich(" Ending test: " + name, indent);
    }
}

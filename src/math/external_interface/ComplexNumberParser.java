package math.external_interface;

import java.math.BigDecimal;

import representation.ComplexNumber;
import utilities.Utilities;

/**
 * For parsing Mathematica returned complex numbers
 */
public class ComplexNumberParser
{
    /**
     * @param complexNumber
     * @return -- a complex number rounded for small imaginary values: 
     */

    /**
     * @param number -- a complex number in String form
     * @return -- a complex number in String form that simplifies values:
     *      * removes dangling decimal values: 0.0000023234434234 becomes 0
     *      * 1.6653345369377348*^-16 becomes 0
     *      "1. - 0.000000000000000333066907387547 I" --> "1."
     *      "0.7937005259840998 - 0.000000000000000333066907387547 I" --> "0.7937005259840998"
     */
    public static ComplexNumber simplify(String number)
    {
    	ComplexNumber output = new ComplexNumber() ;
    	if( number.indexOf("Infinity") >= 0 )
    	{
    		output.IsInfinity = true ;
    	}
    	else
    	{
            output.RealPart = simplifyConstant(getRealPart(number));
            output.ImaginaryPart = simplifyConstant(getImaginaryPart(number));
    	}
        return output ;
    }

    /**
     * @param number -- a complex number in String form
     * @return -- String-based representation of the real number: a + bi -> a
     */
    private static String getRealPart(String number)
    {
        //
        // If there is no imaginary part, simply return the number itself
        //
        int indexI = number.indexOf('I');
        
        if (indexI == -1) return number;

        //
        // Otherwise, we have an imaginary part (and not necessarily a real part)
        // The last two elements of the split are the imaginary parts
        String[] split = number.split("\\s+");

//        for (String s : split)
//        {
//            System.out.println("|" + s + "|");
//        }
        
        // No real part (even though there is an imaginary part)
        if (split.length <= 2) return "0";
        
        // Real part is first
        return split[0];
    }
    
    /**
     * @param number -- a complex number in String form
     * @return -- String-based representation of the real number: a + bi -> b
     */
    private static String getImaginaryPart(String number)
    {
        //
        // If there is no imaginary part, simply return zero
        //
        int indexI = number.indexOf('I');
        if (indexI == -1) return "0";

        // Work on a substring that excludes I
        String noI = number.substring(0, indexI).trim();
        
        // Parse the string seeking the real and imaginary parts
        String[] split = noI.split("\\s+");

//        for (String s : split)
//        {
//            System.out.println("|" + s + "|");
//        }
        
        // The imaginary part is then the last String in the split
        return split[split.length - 1];
    }
    
    /**
     *      "0.000000000000000333066907387547" --> "0"
     *      "0.7937005259840998 - 0.000000000000000333066907387547 I" --> "0.7937005259840998"
     *      "1.6653345369377348*^-16" -> "0"
     */
    private static double simplifyConstant(String number)
    {
        // Check if we're dealing with 0 right away
        if (number == "0") return 0.0 ;
        
        //
        // Handle scientific form
        //
        int indexHat = number.indexOf('^');

        if (indexHat != -1)
        {
            // Acquire the exponent and verify it is negative
            String exponent = number.substring(indexHat + 1).trim();

            // If there is a lingering * (which is possible: 1.6653345369377348*^-16* because an I was removed), remove trailing information
            int starIndex = exponent.indexOf('*');
            if (starIndex != -1) exponent = exponent.substring(0, starIndex);
            
            System.out.println(exponent);
            
            int exp = Integer.parseInt(exponent);

            if (exp > 0)
            {
                System.err.println("ComplexNumberParser::simplify: Positive exponent in scientific notation.");
                return Double.MIN_VALUE ;
            }
            
            return 0.0 ;
        }

        //
        // Handle decimal (fractional values)
        //
        BigDecimal bd = new BigDecimal(number);
        double doubleVal = bd.doubleValue();
        int integerVal = bd.intValue();

        if (Utilities.equalDoubles(doubleVal - integerVal, 0)) return integerVal ;
        
        return doubleVal ;
    }
}

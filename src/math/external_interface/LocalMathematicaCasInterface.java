package math.external_interface;

import java.math.BigDecimal;
import com.wolfram.jlink.*;
import globals.Constants;
import representation.Point;
import representation.bounds.functions.BoundedFunction;

//
// Implements a singleton pattern
//
public class LocalMathematicaCasInterface extends CasInterface
{
    //
    // No public instances allowed
    //
    private LocalMathematicaCasInterface(String path)
    {
        //
        // Open the mathematica link.
        //
        // See: https://reference.wolfram.com/language/JLink/tutorial/WritingJavaProgramsThatUseTheWolframLanguage.html
        //   (Windows)
        //   java -classpath .;..\..\JLink.jar SampleProgram -linkmode launch -linkname "c:\program files\wolfram research\mathematica\10.0\mathkernel.exe"
        String[] args = { "-linkmode",  "launch",  "-linkname", Constants.MATHEMATICA_PATH};

        //
        // For debugging, print the options we used to call Mathematica
        // 
        //
        System.out.println("Calling the Kernel link with options: ");
        for (String arg : args)
        {
            System.out.print(arg + " ");
        }
        System.out.println();

        try
        {
            _mathematicaLink = MathLinkFactory.createKernelLink(args);

            // Get rid of the initial InputNamePacket the kernel will send when it is launched.
            _mathematicaLink.discardAnswer();
        }
        catch (MathLinkException e)
        {
            System.out.println("Fatal error opening Mathlink in Synthesizer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //
    // The main object-based link to local software
    //
    private KernelLink _mathematicaLink;

    // Singleton instance
    private static LocalMathematicaCasInterface _theInstance;

    //
    // We must initialize our CAS interface with a path
    //
    // This method MUST be called if there is a path specified in the command-line.
    //
    public static void initialize(String path)
    {
        _theInstance = new LocalMathematicaCasInterface(path);
    }

    public static LocalMathematicaCasInterface getInstance()
    {
        if (_theInstance != null) return _theInstance;

        _theInstance = new LocalMathematicaCasInterface(Constants.MATHEMATICA_PATH);

        return _theInstance;
    }

    /**
     * @param q -- a String-based query for mathematica
     * @return the Mathematica result of the query; user is responsible for parsing
     */
    public String query(String q)
    {
        String result = _mathematicaLink.evaluateToOutputForm(q, 0);

        return result;
        //        try
        //        {
        //            //_mathematicaLink.discardAnswer();
        //
        //            String result = _mathematicaLink.evaluateToOutputForm(q, 0);
        //
        //            return result;
        //            
        //        }
        //        catch (MathLinkException e)
        //        {
        //            System.out.println("Mathlink exception on query: " + q);
        //            e.printStackTrace();
        //        }
        //        return null;
    }

    /**
     * @param q -- a String-based query for mathematica
     * @return the Mathematica result of the query; user is responsible for parsing
     */
    public double queryDouble(String q)
    {
        System.out.println("Query: \"" + q + "\"");

        String result = _mathematicaLink.evaluateToOutputForm(q, 0);

        System.out.println("|" + result + "|");

        // In case we have a value such as: |1. - 0.000000000000000333066907387547 I|, remove the I component
        result = ComplexNumberParser.simplify(result);
        
        System.out.println("|" + result + "|");
        
        BigDecimal decimal = null;
        
        try
        {
            decimal = new BigDecimal(result.trim());
        }
        catch(NumberFormatException nfe)
        {
            System.err.println("Number format exception: " + nfe + " ; \"" + result + "\"");
            return Double.NEGATIVE_INFINITY;
        }

        return decimal.doubleValue();
    }

    /**
     * @param queries -- a sequence of String queries where the last query returns a double
     * @return the result of performing q[0] followed by q[1] in sequence, etc.
     * 
     * Example: Evaluating f(k) given f(x) and k:
     *          q1: f[x_]:=x^2
     *          q2: f[4]
     *          results in 16
     */
    public double querySequence(String[] queries)
    {
        String overallQuery = "";

        for (int q = 0; q < queries.length - 1; q++)
        {
            overallQuery += queries[q] + " ; ";
        }
        
        overallQuery += queries[queries.length - 1];
        
//        //
//        // Call all of the queries in sequence (and later evaluate the last query to return a value) 
//        //
//        for (int q = 0; q < queries.length - 1; q++)
//        {
//            try
//            {
//                System.err.println("Evaluating: " + queries[q]);
//                
//                // Evaluate the queries: 0 to n-1
//                _mathematicaLink.evaluate(queries[q]);
//            }
//            catch (MathLinkException e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        //
//        // Enact / evaluate the second query
//        //

        //System.err.println("Evaluating: " + overallQuery);
        
        return queryDouble(overallQuery);
    }

    /**
     * @param function -- a String-based function
     * @param q2 -- a value to evalute
     * @return the result of performing q1 followed by q2 in sequence
     * 
     * Example: Evaluating f(k) given f(x) and k:
     *          q1: f[x_]:=x^2
     *          q2: f[4]
     *          results in 16
     */
    public double evaluateAtPoint(String function, double x)
    {
        final String fName = "fLocal";

        String[] queries = new String[3];
        
        //
        // Clear
        //
        queries[0] = "Clear[" + fName + "]";

        //
        // Define the function
        //
        // Determine the variable of the function
        char variable = function.contains("x") ? 'x' : 'y';
        
        queries[1] = fName + "[" + variable + "_]";

        queries[1] += ":=";
        
        queries[1] += function;

        //
        // Evaluative expression: f(x)
        //
        // NumberForm Forces a decimal evaluation:
        // https://mathematica.stackexchange.com/questions/24208/how-do-i-get-mathematica-to-show-a-number-in-non-exponential-form
        queries[2] = "NumberForm[";
        
        queries[2] += fName + "[" + x + "]";
        
        queries[2] += ", Infinity, ExponentFunction -> (Null &)]";
        
        return querySequence(queries);
    }

    //
    // Returns a String-based representation of the two given functions
    //
    public String getIntersection(BoundedFunction func1, BoundedFunction func2)
    {
        try
        {
            _mathematicaLink.discardAnswer();
            String func1Str = func1.toFullMathematicaString();
            String func2Str = func2.toFullMathematicaString();

            String result = _mathematicaLink.evaluateToOutputForm("NSolve[" + func1Str + "==" + func2Str + ",x]", 0);
            return result;

        }
        catch (MathLinkException e)
        {
            System.out.println("Mathlink");
            e.printStackTrace();
        }
        return "Invalid Points";
    }

    //
    // Can we establish the CAS connection with installed Mathematica?
    //
    public boolean connection()
    {
        try
        {
            _mathematicaLink.discardAnswer();

            String result = _mathematicaLink.evaluateToOutputForm("2 * 3", 0);
            String expected = "6";

            if (result != expected)
            {
                System.err.println("In simple query, expected result of " + expected + "; acquired |" + result + "|");
                return false;
            }
        }
        catch (MathLinkException e)
        {
            System.err.println("Mathlink: ");
            e.printStackTrace();
            return false;
        }

        return true;
    }


    //
    //  Example usage of Mathematica
    //
    //	try {
    //			KernelLink ml = MathLinkFactory.createKernelLink(argv1);
    //			ml.discardAnswer();
    //
    //            //ml.evaluate("<<MyPackage.m");
    //            //ml.discardAnswer();
    //
    //            //ml.evaluate("solve x^2==x^3");
    //            //ml.waitForAnswer();
    //
    //            //Expr e1 = new Expr(new Expr(Expr.SYMBOL, "Plus"), new Expr[] {new Expr(2), new Expr(new Expr(Expr.SYMBOL, "Plus"), new Expr[]{new Expr(2), new Expr(2)})});
    //            //System.out.println(e1.toString());
    //            //Expr e1 = new Expr("NSolve[x == x^2, x]");//"Plus[2, Plus[2, 2]]");
    //            //String result = ml.getString();
    //            //ml.evaluate(e1);
    //            //ml.waitForAnswer();
    //            String str = ml.evaluateToOutputForm("NSolve[x==-x^2+4,x]", 0);
    //            System.out.println("Intersects of f(x) = x and g(x) = -x^2+4 : " + str);



    public String getLagrangePolynomial(Point[] points)
    {
        String[] argv1 = {
                //"-linkmode",  "launch",  "-linkname",
                Constants.MATHEMATICA_PATH };
        try {
            KernelLink ml = MathLinkFactory.createKernelLink(argv1);
            ml.discardAnswer();

            String str = "InterpolatingPolynomial[{";
            for (Point p : points)
            {
                str += "{" + p.getX() + "," + p.getY() + "},";
            }
            str = str.substring(0,str.length()-1); //Remove last character because of an extra comma.
            str += "}, x]";
            return str;
        } catch (MathLinkException e) {
            // TODO Auto-generated catch block
            System.out.println("Mathlink");
            e.printStackTrace();
        }
        return "Invalid Points for Lagrange Polynomial";
    }

    public BoundedFunction parseLagrangePolynomial(String str) {
        return null;
    }

}
package math.analysis.inverses;


import java.util.Vector;

import math.analysis.Analyzer;
import math.external_interface.LocalMathematicaCasInterface;
import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import utilities.Utilities;

//
//
// Bridge to math functions computed through Wolfram-Alpha 
//
//
public class Inverses extends Analyzer
{
    //
    // Singleton instance
    //
    protected static Inverses _theInstance;

    public static Inverses getInstance()
    {
        if (_theInstance != null) return _theInstance;

        _theInstance = new Inverses();

        return _theInstance;
    }

    /**
     * @param function
     * @return the inverse of the given function along with the codomain as domain of the inverse
     */
    public StringBasedFunction computeInverse( StringBasedFunction function )
    {
    	try
    	{
    		return getInverse( function, getInverses( function ) ) ;
    	}
    	catch( Exception exc )
    	{
    		System.out.println( "Failed function inversion for function: " + function ) ;
    		return null ;
    	}
    }

    /**
     * @param function
     * @return the inverse of the given function along with the codomain as domain of the inverse
     */
    public StringBasedFunction computeRealInverse(StringBasedFunction function)
    {
        return getNonImaginaryInverse(function, getNonImaginaryInverses(function));
    }
    
    /**
     * @param function
     * @return the inverse of the given function along with the codomain as domain of the inverse
     */
    private StringBasedFunction getInverse(StringBasedFunction function, Vector<String> candidates)
    {
        // Refine the candidates to a single function
        String inverse = acquireCandidateString(function, candidates, function.getDomain());

        StringBasedFunction theInverse = new StringBasedFunction(inverse);
        theInverse.setDomain(codomain(function));
        
        return theInverse;
    }
    
    /**
     * @param function
     * @return the inverse of the given function along with the codomain as domain of the inverse
     */
    private StringBasedFunction getNonImaginaryInverse(StringBasedFunction function, Vector<String> candidates)
    {
        // Refine the candidates to a single function
        String inverse = acquireCandidateString(function, candidates, function.getDomain());

        // If we have an imaginary inverse, we have no real inverse
        if (hasImaginaryComponent(inverse)) return null;

        StringBasedFunction theInverse = new StringBasedFunction(inverse);
        theInverse.setDomain(codomain(function));
        
        return theInverse;
    }
    
    /**
     * @param function
     * @return the inverse of the given function along with the codomain as domain of the inverse
     */
    private Domain codomain(StringBasedFunction function)
    {
        Domain domain = function.getDomain();

        if (domain == null) return null;

        // Acquire (x1, f(x1)) and (x2, f(x2)) so the domain is (f(x1), f(x2))
        double y1 = function.evaluateAtPoint(domain.getLowerBound()).getReal();
        double y2 = function.evaluateAtPoint(domain.getUpperBound()).getReal();

        // Order the values
        if (y1 > y2)
        {
            double temp = y1;
            y1 = y2;
            y2 = temp;
        }

        return new Domain(y1, y2);
    }

    /**
     * @param function
     * @return the inverse of the given function along with the codomain as domain of the inverse
     */
    public boolean inverseIsRealFunction(StringBasedFunction function)
    {
        return getNonImaginaryInverses(function).size() == 1;
    }

    /**
     * @param function
     * @return the set of inverse functions not containing references to I, the imaginary value
     */
    private Vector<String> getNonImaginaryInverses(StringBasedFunction function)
    {
        Vector<String> candidates = getInverses(function);
        Vector<String> nonImaginary = new Vector<String>();

        for (String candidate : candidates)
        {
            // I in Mathematica refers to \sqrt{-1}
            if (!hasImaginaryComponent(candidate)) nonImaginary.add(candidate);
        }

        return nonImaginary;
    }

    /**
     * @param function
     * @return the inverse of the given function along with the codomain as domain of the inverse
     */
    private Vector<String> getInverses(StringBasedFunction function)
    {
        // Find the Mathematica-based inverse
        String query = inverseQuery(function);

        System.out.println("Query: |" + query + "|");

        // Query Mathematica
        String result = LocalMathematicaCasInterface.getInstance().query(query);

        return parseFunctions(result);
    }

    /**
     * @param function
     * @return the inverse of the given function along with the codomain as domain of the inverse
     */
    public int inverseComplexity(StringBasedFunction function)
    {
        return getInverses(function).size();
    }

    /**
     * @param functionList -- a String representation of a mathematica functions; example:
     *           {{x -> -(Sqrt[5 - Sqrt[9 + 4*y]]/Sqrt[2])}, {x -> Sqrt[5 - Sqrt[9 + 4*y]]/Sqrt[2]},
     *            {x -> -(Sqrt[5 + Sqrt[9 + 4*y]]/Sqrt[2])}, {x -> Sqrt[5 + Sqrt[9 + 4*y]]/Sqrt[2]}}
     * @return the set of Functions described by the input string in the given form;
     *       this example returns
     *       index    x-value
     *       0        -(Sqrt[5 - Sqrt[9 + 4*y]]/Sqrt[2])
     *       1        Sqrt[5 - Sqrt[9 + 4*y]]/Sqrt[2]
     *       2        -(Sqrt[5 + Sqrt[9 + 4*y]]/Sqrt[2])
     *       3        Sqrt[5 + Sqrt[9 + 4*y]]/Sqrt[2]
     */
    protected Vector<String> parseFunctions(String functionList)
    {
        String input = functionList.trim();

        System.out.println("Input: " + input);

        String[] functions = input.split("[{]|[}]");

        //
        // Parse the individual points: x -> 0
        //
        Vector<String> theFunctions = new Vector<String>();
        for (String function : functions)
        {
            function = function.replace( "#1", "x" ) ;
            function = function.replace( "& [x]", "" ).trim() ;
            theFunctions.add( function ) ;
        }

        // Printing...debugging
        for (String function : theFunctions)
        {
            System.out.println("|" + function + "|");
        }

        return theFunctions;
    }

    /**
     * @param function -- a String-based function
     * @return whether the function contains an imaginary component: I in Mathematica refers to \sqrt{-1}
     */
    protected boolean hasImaginaryComponent(String function)
    {
        return function.indexOf('I') != -1;
    }

    /**
     * @param function -- the function for which we seek an inverse
     * @param candidates -- String-based Mathematica candidate strings
     * @param domain -- domain of the original function: leads to domain of the inverse
     * @return String-based representation of the inverse
     */
    private String acquireCandidateString(StringBasedFunction function, Vector<String> candidates, Domain domain)
    {
        // If the domain has not be specified, this is a significant problem
        if (domain == null)
        {
            System.err.println("Domain has not been specified while an inverse is requested: " + function.getFunction());
        }

        //
        // Seek the set of candidate functions; should be one and only one such function
        //
        Vector<String> acceptable = new Vector<String>();
        for (String candidate : candidates)
        {
            if (isCandidate(function, candidate, domain)) acceptable.add(candidate);
        }

        if (acceptable.size() > 1)
        {
            System.out.println("More than one candidate inverse function found for " + function.getFunction());
        }
        else if (acceptable.isEmpty())
        {
            System.out.println("No candidates found for inverse: " + function.getFunction());
        }

        System.out.println("Chose inverse: " + acceptable.get(0));
        
        return acceptable.get(0);
    }

    /**
     * @param function -- the function for which we seek an inverse
     * @param inverse -- candidate inverse function
     * @param domain -- domain of the original function
     * @return whether the inverse maps properly to the 'domain' of the original
     *     That is,
     *         y1 = function(x1)
     *         y2 = function(x2)
     *     Must ensure:
     *         x1 = inverse(y1) 
     *         x2 = inverse(y2) 
     */
    private boolean isCandidate(StringBasedFunction function, String inverse, Domain domain)
    {
        StringBasedFunction invFunction = new StringBasedFunction(inverse);
        invFunction.setDomain( domain ) ;

        StringBasedFunction flippedInvFunction = null ;
        if( inverse.indexOf( "-Sqrt" ) >= 0 )
        	flippedInvFunction = new StringBasedFunction( inverse.replace( "-Sqrt", "Sqrt" ) ) ;
        else
        	flippedInvFunction = new StringBasedFunction( inverse.replace( "Sqrt", "(-Sqrt)" ) ) ;
        flippedInvFunction.setDomain( domain ) ;
        	
        // Check both endpoints of the domain
        double y1 = function.evaluateAtPoint(domain.getLowerBound()).getReal();
        if (!inverseSatisfies(invFunction, domain.getLowerBound(), y1)) return false;

        double y2 = function.evaluateAtPoint(domain.getUpperBound()).getReal();
        if(     ! inverseSatisfies( invFunction, domain.getUpperBound(), y2 )
        	&&  ! inverseSatisfies( flippedInvFunction, domain.getUpperBound(), y2 ) )
            	return false ;

        // Check a midpoint (for safety)
        double midX = Utilities.midpoint(domain.getLowerBound(), domain.getUpperBound());
        double midY = function.evaluateAtPoint(midX).getReal();
        if(     ! inverseSatisfies( invFunction, midX, midY )
        	&&	! inverseSatisfies( flippedInvFunction, midX, midY ) )
        	return false ;

        return true;
    }

    private boolean inverseSatisfies(StringBasedFunction inverse, double x, double y)
    {
    	double xComp = inverse.evaluateAtPoint( y ).getReal() ;
        return Utilities.looseEqualDoubles( xComp, x ) ;
    }
    
    private String inverseQuery(StringBasedFunction function)
    {
        String query = "InputForm[InverseFunction[";

        query += function.toFullMathematicaString().replaceAll( "x", "#" ) ;

        query += " &]][x]" ;

        return query;
    }
}

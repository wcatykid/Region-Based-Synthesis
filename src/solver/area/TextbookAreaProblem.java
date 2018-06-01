package solver.area;

import representation.bounds.functions.Domain;
import solver.TextbookProblem;

/**
 *  A textbook problem consists of a set of functions over a particular interval (domain) 
 */
public class TextbookAreaProblem extends TextbookProblem
{
	private boolean _attemptSolveByY            = false ;
	private boolean _expectInvertibilitySuccess = false ;
	
    /**
     * @param f -- the set of functions (in terms of variable x)
     * @param data -- metadata describing the problem source.
     */
    public TextbookAreaProblem( String[] f, String data )
    {
        this( f, null, data, DEFAULT_ANSWER ) ;
    }
    
    /**
     * @param f -- the set of functions (in terms of variable x)
     * @param data -- metadata describing the problem source.
     * @param answer -- the numeric answer for this problem
     */
    public TextbookAreaProblem( String[] f, String data, double answer )
    {
        this( f, null, data, answer ) ;
    }

    public TextbookAreaProblem( String[] functions, Domain domain, String data )
    {
        this( functions, domain, data, DEFAULT_ANSWER ) ;
    }
    
    public TextbookAreaProblem( String[] functions, Domain domain, String data, double answer )
    {
        super( functions, domain, data, answer ) ;
    }
    
    public boolean getAttemptSolveByY() { return _attemptSolveByY ; }
    public void setAttemptSolveByY( boolean value ) { _attemptSolveByY = value ; }

    public boolean getExpectInvertibilitySuccess() { return _expectInvertibilitySuccess ; }
    public void setExpectInvertibilitySuccess( boolean value ) { _expectInvertibilitySuccess = value ; }
}

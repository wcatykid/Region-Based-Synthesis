package solver.area.parser;

import representation.bounds.functions.Domain;
import solver.area.TextbookAreaProblem;
import solver.parser.ProblemParseUtilities;
import solver.parser.ProblemParser;

/**
 * Given a complete file of Mathematica encoded problems in String form, return the indicated set of area problems.
 */
public class AreaProblemParser implements ProblemParser
{
    protected String _problemString;

    // The actual problem extracted from the file.
    protected TextbookAreaProblem _problem;
    public TextbookAreaProblem getProblem() { return _problem; }

    public AreaProblemParser(String line)
    {
        _problemString = line;
    }

    /**
     * @param line -- a String-based representation of the problem in the format discussed below
     * @return a textbook problem object
     * Format:
     * 
     * { Function-1; Function-2 ; ... ; Function-n } [x-left, x-right] // Metadata
     * 
     */
    public void parse()
    {
        String[] functions = ProblemParseUtilities.extractFunctions(_problemString);

        System.out.println(functions);

        Domain domain = ProblemParseUtilities.extractDomain(_problemString);

        System.out.println("Domain: " + domain);

        String metadata = ProblemParseUtilities.extractMetadata(_problemString);

        System.out.println("Metadata: " + metadata);
        
        double answer = ProblemParseUtilities.extractAnswer(_problemString);

        System.out.println("Answer: " + answer);
        
        _problem = new TextbookAreaProblem(functions, domain, metadata, answer);
    }
    
    /**
     * @return T/F whether the string contains cursory information required of a problem.
     */
    public boolean verify()
    {
        // Verify that the region is specified
        if (_problemString.indexOf('{') == -1) return false; 
        if (_problemString.indexOf('}') == -1) return false;

        // The metadata should be there for information-tracking purposes
        if (_problemString.indexOf("//") == -1) return false;

        return true;
    }
}

package solver.area.parser;

import math.external_interface.LocalMathematicaCasInterface;
import representation.bounds.functions.Domain;
import solver.area.TextbookAreaProblem;

/**
 * Given a complete file of Mathematica encoded problems in String form, return the indicated set of area problems.
 */
public class AreaProblemParser
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
    protected void parse()
    {
        String[] functions = extractFunctions(_problemString);

        System.out.println(functions);

        Domain domain = extractDomain(_problemString);

        System.out.println("Domain: " + domain);

        String metadata = extractMetadata(_problemString);

        System.out.println("Metadata: " + metadata);
        
        double answer = extractAnswer(_problemString);

        System.out.println("Answer: " + answer);
        
        _problem = new TextbookAreaProblem(functions, domain, metadata, answer);
    }

    /**
     * @param line -- a String-based representation of the problem in the format discussed below
     * @return The functions involved in a textbook problem
     * Format:
     * 
     * { Function-1; Function-2 ; ... ; Function-n } [x-left, x-right] // Metadata
     * 
     */
    private String[] extractFunctions(String line)
    {
        int beginFunctionsIndex = line.indexOf('{');
        int endFunctionsIndex = line.indexOf('}');

        String functionStr = line.substring(beginFunctionsIndex + 1, endFunctionsIndex);
        return functionStr.split(";");
    }

    /**
     * @param line -- a String-based representation of the problem in the format discussed below
     * @return The domain involved in a textbook problem
     * Format:
     * 
     * { Function-1; Function-2 ; ... ; Function-n } [x-left, x-right] // Metadata
     * 
     */
    private Domain extractDomain(String line)
    {
        //
        // Find the beginning of the domain: after the region specification
        //
        String dStr = line.substring(line.indexOf('}') + 1);

        //
        // Parse the domain
        //
        int beginDomainIndex = dStr.indexOf('[');
        double left_x = Double.POSITIVE_INFINITY;
        double right_x = Double.NEGATIVE_INFINITY;

        // The domain may not have been provided; if it has, read it
        if (beginDomainIndex != -1)
        {
            int endDomainIndex = dStr.indexOf(']');

            String domainStr = dStr.substring(beginDomainIndex + 1, endDomainIndex);
            domainStr = replaceConstants(domainStr);
            String[] bounds = domainStr.split(",");

            left_x = LocalMathematicaCasInterface.getInstance().queryDouble(bounds[0]);
            right_x = LocalMathematicaCasInterface.getInstance().queryDouble(bounds[1]); // Double.parseDouble(bounds[1])
        }

        // No domain was 
        if (right_x == Double.NEGATIVE_INFINITY) return null;

        return new Domain(left_x, right_x);
    }

    /**
     * Replace all constants with evaluative Mathematica expressions
     * @param s
     * @return
     */
    private String replaceConstants(String start)
    {
        String s = start.replaceAll("Pi", "N[Pi]");
        
        s = s.replaceAll("PI", "N[Pi]");
        s = s.replaceAll("pi", "N[Pi]");

        s = s.replaceAll("E", "N[E]");
        s = s.replaceAll("e", "N[E]");

        return s;
    }
    
    /**
     * @param line -- a String-based representation of the problem in the format discussed below
     * @return The metadata involved in a textbook problem
     * Format:
     * 
     * { Function-1; Function-2 ; ... ; Function-n } [x-left, x-right] // Metadata
     * 
     */
    private String extractMetadata(String line)
    {
        // Seek the comment
        int beginDataIndex = line.indexOf("//");

        // Empty comment
        if (beginDataIndex == -1) return "";
        
        String data = line.substring(beginDataIndex + 2);

        return data.trim();
    }

    /**
     * @param line -- a String-based representation of the problem in the format discussed below
     * @return The numeric value indicating the answer to this textbook problem
     * Format:
     * 
     * { Function-1; Function-2 ; ... ; Function-n } [x-left, x-right] <Answer> // Metadata
     * 
     */
    private double extractAnswer(String line)
    {
        //
        // Identify the answer substring
        //
        int beginAnswerIndex = line.indexOf("<");
        if (beginAnswerIndex == -1) return TextbookAreaProblem.DEFAULT_ANSWER;

        int endAnswerIndex = line.indexOf(">");
        if (endAnswerIndex == -1) return TextbookAreaProblem.DEFAULT_ANSWER;
        
        // This substring will be a mathematica based expression: evaluate Pi etc.
        String answer = line.substring(beginAnswerIndex+1, endAnswerIndex);
        answer = replaceConstants(answer);
        
        return LocalMathematicaCasInterface.getInstance().queryDouble(answer);
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

package solver.volume.parser;

import java.util.ArrayList;

import math.external_interface.LocalMathematicaCasInterface;
import representation.bounds.functions.Domain;
import solver.area.TextbookAreaProblem;
import solver.parser.ProblemParseUtilities;
import solver.parser.ProblemParser;
import solver.volume.AxisOfRevolution;
import solver.volume.TextbookVolumeProblems;
import utilities.Pair;

/**
 * Given a complete file of Mathematica encoded problems in String form, return the indicated set of area problems.
 * 
 * Sample format (injects axis / answer information from the "Area Between Curves" Problem):
 *   { Function-1; Function-2 ; ... ; Function-n } [x-left, x-right] [<axis-1, answer-1>; <axis-2, answer-2>; ... >] // Metadata
 * 
 * Note: axes and answers are uniquely identified by the 2-character sequence "[<" and ended by ">]"
 */
public class VolumeProblemParser implements ProblemParser
{
    // Problem string for parsing.
    protected String _problemString;

    public VolumeProblemParser(String line) { _problemString = line; }
    
    // Extracted volume problem(s)
    protected TextbookVolumeProblems _problem;
    public TextbookVolumeProblems getProblem() { return _problem; }

    // AXES / ANSWERS delimiters
    private static final String BEGIN_AXES_DELIMITER = "[<";
    private static final String END_AXES_DELIMITER = ">]";

    /**
     * @param line -- a String-based representation of the problem in the format discussed above / below
     * 
     * Format:
     * { Function-1; Function-2 ; ... ; Function-n } [x-left, x-right] [<axis-1, answer-1>; <axis-2, answer-2>; ... >] // Metadata
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

        Pair<ArrayList<AxisOfRevolution>, ArrayList<Double>> aas = extractAxesAndAnswers(_problemString);

        for (int index = 0; index < aas.getFirst().size(); index++)
        {
            System.out.print("< " + aas.getFirst().get(index) + ", " + aas.getSecond().get(index));
        }

        System.out.println();

        _problem = new TextbookVolumeProblems(functions, domain, aas.getFirst(), metadata, aas.getSecond());
    }

    Pair<ArrayList<AxisOfRevolution>, ArrayList<Double>> extractAxesAndAnswers(String line)
    {
        //
        // Find the beginning of the domain: after the region specification
        //
        int beginIndex = line.indexOf(BEGIN_AXES_DELIMITER) + BEGIN_AXES_DELIMITER.length() - 1; // We want the beginning '<'
        int endIndex = line.indexOf(END_AXES_DELIMITER) + 1;                                     // We want the final '>'
        String aas = ProblemParseUtilities.replaceConstants(line.substring(beginIndex, endIndex));
        
        //
        // <Axis / Answer> pairs
        //
        ArrayList<AxisOfRevolution> axes = new ArrayList<AxisOfRevolution>();
        ArrayList<Double> answers = new ArrayList<Double>();
        
        //
        // Split the string by Parse the pairs
        //
        String[] pairs = aas.split(";");
        for (String pair : pairs)
        {
            // Axis
            String axisStr = pair.substring(pair.indexOf('<') + 1, pair.indexOf(','));            
            axes.add(new AxisOfRevolution(axisStr));

            // Answer
            String answer = pair.substring(pair.indexOf(',') + 1, pair.indexOf('>'));
            answers.add(LocalMathematicaCasInterface.getInstance().queryComplexNumber(answer).getReal());
        }
        
        return new Pair<ArrayList<AxisOfRevolution>, ArrayList<Double>>(axes, answers);
    }

    /**
     * @return T/F whether the string contains cursory information required of a problem.
     */
    public boolean verify()
    {
        // Verify that the region is specified
        if (_problemString.indexOf('{') == -1) return false; 
        if (_problemString.indexOf('}') == -1) return false;

        // Verify that the axes is/are specified
        if (_problemString.indexOf("[<") == -1) return false; 
        if (_problemString.indexOf(">]") == -1) return false;
        
        // The metadata should be there for information-tracking purposes
        if (_problemString.indexOf("//") == -1) return false;

        return true;
    }
}

package solver.area.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import exceptions.ParseException;
import solver.area.TextbookAreaProblem;

/**
 * Given a complete file of Mathematica encoded problems in String form, return the indicated set of area problems.
 */
public class AreaProblemFileParser
{
    protected List<String> _files;

    // The actual problems extracted from the file.
    protected Vector<TextbookAreaProblem> _problems;
    public Vector<TextbookAreaProblem> getProblems() {return _problems; }

    public AreaProblemFileParser(String file)
    {
        _files = new ArrayList<String>();
        _problems = new Vector<TextbookAreaProblem>();

        _files.add(file);
    }

    /**
     * @param files -- paths for all files we wish to parse
     */
    public AreaProblemFileParser(List<String> files)
    {
        _files = files;
        _problems = new Vector<TextbookAreaProblem>();
    }

    /**
     * Invoke parsing of all files
     * @throws ParseException if a parse error occurs (library based problem: file not found, etc.)
     */
    public void parse() throws ParseException
    {
        for (String file : _files)
        {
            parse(file);
        }
    }

    /**
     * @param file -- path to a text file containing problems
     * @throws ParseException if a problem arises
     */
    protected void parse(String file)
    {
        //
        // Open the file
        //
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(file));
        }
        catch (FileNotFoundException e)
        {
            System.err.format("File not found in parsing area problems:'%s'.", file);
        }

        //
        // Read the file line by line
        //
        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                //
                // Parse the individual string to create the problem
                //
                AreaProblemParser problemParser = new AreaProblemParser(line.trim());
                if (problemParser.verify())
                {
                    problemParser.parse();
                    _problems.add(problemParser.getProblem());
                }
                else
                {
                    System.err.println("Problem parse issue; not verified: |" + line + "|");
                }
            }

            // Close the file we just read
            reader.close();
        }
        catch (IOException ioe)
        {
            System.err.format("Exception occurred trying to read '%s'.", file);
        }
    }
}

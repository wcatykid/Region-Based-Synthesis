package frontend;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import globals.Constants;
import representation.bounds.functions.FunctionT;

public class OptionsFileParser
{
    private File filepath;

    public OptionsFileParser(String filepath)
    {
        this.filepath = new File(filepath);
    }

    public boolean parseFile()
    {
        Scanner in = null;

        try
        {
            in = new Scanner(filepath);

            while(in.hasNextLine())
            {
                processString(in.nextLine());
            }
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Options file not found.");
            return false;
        }
        finally
        {
            in.close();
        }

        return true;
    }

    // 0 = variable type; 1 = unused; 2 = value OR beginning of array, 3 = element, 4 = unused, 5 = element, etc.
    //
    // 
    //
    private void processString(String str)
    {
        String[] strings = str.split(" ");

        if (strings.length < 3) return;

        switch (strings[0])
        {
            case "LIMITED_FUNCTIONS":
                Constants.LIMITED_FUNCTIONS = new Boolean(strings[2]);
                break;

            case "ALLOWED_FUNCTIONS":
                globals.Constants.ALLOWED_FUNCTIONS = processFunctions(strings);
                break;

            case "MAX_RIGHT_X":
                globals.Constants.MAX_RIGHT_X = new Double(strings[2]);
                break;

            case "INTS_ONLY":
                Constants.INTEGERS_ONLY = new Boolean(strings[2]);
                break;

            case "SAME_LENGTH_BOUNDS":
                Constants.SAME_LENGTH_BOUNDS = new Boolean(strings[2]);
                break;

            case "MAX_ATTEMPTS":
                Constants.MAX_ATTEMPTS = new Integer(strings[2]);
                break;

            case "SIMPLE_COEFFICIENTS":
                Constants.SIMPLE_COEFFICIENTS = new Boolean(strings[2]);
                break;

            case "ALLOW_INTERSECTIONS":
                Constants.ALLOW_INTERSECTIONS = new Boolean(strings[2]);
                break;
                
            case "ENFORCE_FUNCTION_BOUND_ALIGNMENT":
                Constants.ENFORCE_FUNCTION_BOUND_ALIGNMENT = new Boolean(strings[2]);
                break;
        }
    }

    private FunctionT[] processFunctions(String[] strs)
    {
        Vector<FunctionT> temps = new Vector<>();
        
        for (int i = 2; i < strs.length; i++)
        {
            switch (strs[i])
            {
                case "HORIZONTAL_LINE":
                    temps.addElement(FunctionT.HORIZONTAL_LINE);
                    break;
                    
                case "LINEAR":
                    temps.addElement(FunctionT.LINEAR);
                    break;
                
                case "PARABOLA":
                    temps.addElement(FunctionT.PARABOLA);
                    break;
                
                case "CUBIC":
                    temps.addElement(FunctionT.CUBIC);
                    break;
                
                case "QUARTIC":
                    temps.addElement(FunctionT.QUARTIC);
                    break;
                
                case "QUINTIC":
                    temps.addElement(FunctionT.QUINTIC);
                    break;
                    //case "POLYNOMIAL":
                    //	temps.addElement(FunctionT.POLYNOMIAL);
                    //	break;
                
                case "EXPONENTIAL":
                    temps.addElement(FunctionT.EXPONENTIAL);
                    break;
                
                case "SINE":
                    temps.addElement(FunctionT.COSINE);
                    break;
            }
        }
 
        return temps.toArray(new FunctionT[temps.size()]);
    }
}

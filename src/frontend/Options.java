package frontend;

import java.util.Vector;

import globals.Constants;

//
// A aggregation class for options specified in the command-line
//
public class Options
{
    private String[] args;

    private String optionsFile;
    public boolean hasOptionFile() { return optionsFile == null; }
    public String getOptionFile() { return optionsFile; }

    private Vector<String> templateFiles;
    public Vector<String> getTemplateFiles() { return templateFiles; }

    private String _mathematicaPath;
    public String getMathematicaPath()
    {
        // If specified, use that path; if not, use a default constant path
        return  _mathematicaPath == null ? Constants.MATHEMATICA_PATH : _mathematicaPath;
    }

    public static boolean DEBUG;
    protected static Options _instance;
    static { _instance = null; }
    public static Options getInstance()
    {
        if (_instance == null) _instance = new Options(new String[1]);

        return _instance;
    }

    protected Options(String[] args)
    {
        this.args = args;
        this.optionsFile = null;
        this.templateFiles = new Vector<String>();
        this._mathematicaPath = null;
        Options.DEBUG = false;
    }

    public static void initialize(String[] args)
    {
        _instance = new Options(args);
    }

    public boolean parseCommandLine()
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].charAt(0) == '-')
            {
                if (!handleOption(i)) return false;
            }

            //
            // Assume all non-specified arguments are template files
            //
            else
            {
                templateFiles.add(args[i]);
            }
        }

        return true;
    }

    //
    // Deal with the actual options specified on the command-line.
    //
    private boolean handleOption(int index)
    {
        //
        // Debug
        //
        if (args[index].equalsIgnoreCase("-d") || args[index].equalsIgnoreCase("-debug"))
        {
            Options.DEBUG = true;
            return true;
        }

        //
        // Options file
        //
        else if (args[index].equalsIgnoreCase("-opt") || args[index].equalsIgnoreCase("-options"))
        {
            optionsFile = args[index];
            return true;
        }

        //
        // Specifiying path of mathematica
        //
        else if (args[index].equalsIgnoreCase("-mathematica") || args[index].equalsIgnoreCase("-math-wa"))
        {
            _mathematicaPath = args[index];
            return true;
        }

        //
        // Forces contact to the Wolfram / Alpha Engine
        //
        else if (args[index].equalsIgnoreCase("-f") || args[index].equalsIgnoreCase("-force-contact"))
        {

            Constants.CONTACT_WA_ENGINE = true;
            return true;
        }

        return false;
    }
}
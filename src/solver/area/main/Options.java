package solver.area.main;

import java.util.Vector;

//
// A aggregation class for options specified in the command-line
//
public class Options
{
    private String[] _args;

    private Vector<String> _problemFiles;
    public Vector<String> getProblemFiles() { return _problemFiles; }

    protected Options(String[] args)
    {
        this._args = args;
        this._problemFiles = new Vector<String>();
    }

    public boolean parse()
    {
        for (int i = 0; i < _args.length; i++)
        {
            if (_args[i].charAt(0) == '-')
            {
                if (!handleOption(i)) return false;
            }

            //
            // Assume all non-specified arguments are template files
            //
            else
            {
                _problemFiles.add(_args[i]);
            }
        }

        return true;
    }

    //
    // Deal with the actual options specified on the command-line.
    //
    private boolean handleOption(int index)
    {
//        //
//        // Debug
//        //
//        if (_args[index].equalsIgnoreCase("-d") || _args[index].equalsIgnoreCase("-debug"))
//        {
//            Options.DEBUG = true;
//            return true;
//        }
//
//        //
//        // Options file
//        //
//        else if (_args[index].equalsIgnoreCase("-opt") || _args[index].equalsIgnoreCase("-options"))
//        {
//            optionsFile = _args[index];
//            return true;
//        }
//
//        //
//        // Specifiying path of mathematica
//        //
//        else if (_args[index].equalsIgnoreCase("-mathematica") || _args[index].equalsIgnoreCase("-math-wa"))
//        {
//            _mathematicaPath = _args[index];
//            return true;
//        }
//
//        //
//        // Forces contact to the Wolfram / Alpha Engine
//        //
//        else if (_args[index].equalsIgnoreCase("-f") || _args[index].equalsIgnoreCase("-force-contact"))
//        {
//
//            Constants.CONTACT_WA_ENGINE = true;
//            return true;
//        }

        return false;
    }
}
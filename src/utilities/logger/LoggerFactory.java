package utilities.logger;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import utilities.IdFactory;

//
// Factory design patter for all logging channels in this project
//
public class LoggerFactory
{
    private static IdFactory _ids;

    public static final int DEBUG_OUTPUT_ID = 0;
    public static final int MATLAB_RECORDER_OUTPUT_ID = 1;

    // The set of loggers
    protected static ArrayList<Logger> _loggers;

    // A default logger where output goes to die
    protected static Logger _deathLogger;

    // This will be a static class
    protected LoggerFactory() {}

    // A constructor of sorts
    static
    {
        _ids = new IdFactory(MATLAB_RECORDER_OUTPUT_ID + 1);

        _loggers = new ArrayList<Logger>();

        // Initializing debug stream to print to std::out
        _loggers.add(LoggerFactory.buildLogger());
        
        // Initializing the null output stream
        _deathLogger = new Logger(new NullOutputStreamWriter());
    }

    //
    // Acquire a given logger based on a key value
    // If an id is not recognized return the null logger
    //
    public static Logger getLogger(int id)
    {
        if (id >= _loggers.size()) return _deathLogger;

        return _loggers.get(id);
    }

    // A constructor of sorts
    public static int addLogger(Writer writer)
    {
        return addLogger(new Logger(writer));
    }

    public static int addLogger(Logger logger)
    {
        _loggers.add(logger);

        return _ids.getNextId();
    }

    //
    // Logging to System.out
    //
    public static Logger buildLogger()
    {
        return new Logger();
    }

    //
    // Logging to a particular file
    //
    public static Logger buildLogger(String filePath)
    {
        return new Logger(filePath);
    }

    // A constructor of sorts
    public static void close()
    {
        try
        {
            _deathLogger.close();
            
            for (Logger logger : _loggers)
            {
                try
                {
                    if (logger != null) logger.close();
                }
                catch (IOException ioe)
                {
                    System.err.println(ioe.getMessage());
                    ioe.printStackTrace();
                }
            }
        }
        catch (IOException ioe)
        {
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
        }
    }
}

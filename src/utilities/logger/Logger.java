package utilities.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Logger
{
    // The actual output file we will write to
    protected String _theFilePath;
    protected BufferedWriter _writer;

    public Logger()
    {
        _theFilePath = null;
        
        open(new OutputStreamWriter(System.out));
    }
    
    public Logger(String f)
    {
        _theFilePath = f;
        
        //
        // Create a Writer
        //
        FileWriter fstream = null;
        try  
        {
            fstream = new FileWriter(f);
        }
        catch (IOException e)
        {
            System.err.println("Output logging file initialization failed: " + _theFilePath);
            System.err.println("Error: " + e.getMessage());
        }
        
        open(fstream);
    }
    
    public Logger(Writer writer)
    {
        _theFilePath = null;
        
        open(writer);
    }

    //
    // Open the logging file
    //
    protected boolean open(Writer writer)
    {
        _writer = new BufferedWriter(writer);

        if (_writer == null)
        {
            System.err.println("Output logging file (BufferedWriter) initialization failed: " + _theFilePath);
        }
        
        return _writer != null;
    }

    //
    // Writing methods
    //
    public boolean write(String str)
    {
        try
        {
            _writer.write(str);
        }
        catch (IOException ioe)
        {
            System.err.println("Logging problem: " + ioe.getMessage());
            return false;
        }
        
        return true;
    }
    
    //
    // Writing methods
    //
    public boolean writeln(String str)
    {
        return write(str + "\n");
    }
    
    //
    // Close this writer from logging
    //
    public void close() throws IOException
    {
        _writer.close();
    }
}

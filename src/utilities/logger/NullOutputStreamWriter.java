package utilities.logger;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class NullOutputStreamWriter extends OutputStreamWriter
{
    public NullOutputStreamWriter()
    {
        // Send an existing output stream; we won't be using it
        super(System.out);
    }
    
    public NullOutputStreamWriter(OutputStream arg0)
    {
        super(arg0);
    }

    public NullOutputStreamWriter(OutputStream arg0, String arg1) throws UnsupportedEncodingException
    {
        super(arg0, arg1);
    }

    public NullOutputStreamWriter(OutputStream arg0, Charset arg1)
    {
        super(arg0, arg1);
    }

    public NullOutputStreamWriter(OutputStream arg0, CharsetEncoder arg1)
    {
        super(arg0, arg1);
    }

    @Override
    public void write(String str)
    {
        // Do nothing
    }

    @Override
    public void write(char[] cbuf, int off, int len)
    {
        // Do nothing
    }
    
    @Override
    public void write(int c)
    {
        // Do nothing
    }
    
    
    @Override
    public void write(String str, int off, int len)
    {
        // Do nothing
    }
}

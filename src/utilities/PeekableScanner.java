package utilities;
import java.io.*;
import java.util.Scanner;

//
// Provides peek() functionality.
// Converts all returned strings to uppercase.
//
public class PeekableScanner
{
    private Scanner scan;
    private String next;

    public PeekableScanner(String theFile)  throws FileNotFoundException
    {
    	File file = new File(theFile);
        scan = new Scanner(file);
        next = scan.hasNext() ? scan.next() : null;
    }
    
    public PeekableScanner(String theFile, String delimiters)  throws FileNotFoundException
    {
    	File file = new File(theFile);
        scan = new Scanner(file);
        scan.useDelimiter(delimiters);
        next = scan.hasNext() ? scan.next() : null;
        scan.close();
    }

    public boolean hasNext()
    {
        return next != null;
    }

    public String next()
    {
        String current = next;
        next = scan.hasNext() ? scan.next() : null;
        return current.toUpperCase();
    }

    public String peek()
    {
        return next.toUpperCase();
    }
    
    public void close()
    {
        scan.close();
    }
}
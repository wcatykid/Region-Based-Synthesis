package utilities;

//
// A factory model to generate unique identifiers (default starts at 0)
//
public class IdFactory
{
    public static int DEFAULT_ID = 0;

    protected int _minId;
    protected int _current;
    
    public IdFactory()
    {
        _minId = IdFactory.DEFAULT_ID;
        _current = IdFactory.DEFAULT_ID;
    }

    public IdFactory(int dictatedMin)
    {
        _minId = dictatedMin;
        _current = dictatedMin;
    }
    
    public int getNextId() { return _current++; }

    public int min() { return _minId; } 
    
    public void reset() { _current = _minId; }
}

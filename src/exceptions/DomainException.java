package exceptions;

public class DomainException extends Exception
{
	private static final long serialVersionUID = 1933789546446012376L ;

	/**
     * 
     */
    public DomainException()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public DomainException(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public DomainException(Throwable arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public DomainException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    public DomainException(String arg0, Throwable arg1, boolean arg2, boolean arg3)
    {
        super(arg0, arg1, arg2, arg3);
        // TODO Auto-generated constructor stub
    }

}

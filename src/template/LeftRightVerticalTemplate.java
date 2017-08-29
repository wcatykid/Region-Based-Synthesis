package template;

public class LeftRightVerticalTemplate extends LeftRightTemplate
{
    public LeftRightVerticalTemplate() { super(); }

    public String toString() { return "V"; }

    public boolean equals(Object obj)
    {
    	if (obj == null) return false;
    	
    	return obj instanceof LeftRightVerticalTemplate;
    }
}

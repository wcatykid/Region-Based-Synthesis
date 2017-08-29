package template;

public class LeftRightPointTemplate extends LeftRightTemplate
{
    public LeftRightPointTemplate() { super(); }

    public String toString() { return "P"; }

    public boolean equals(Object obj)
    {
    	if (obj == null) return false;
    	
    	return obj instanceof LeftRightPointTemplate;
    }
}

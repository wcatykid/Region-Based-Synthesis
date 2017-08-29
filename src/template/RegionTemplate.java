package template;

public class RegionTemplate
{
    protected LeftRightTemplate _left;
    protected TopBottomTemplate _top;
    protected LeftRightTemplate _right;
    protected TopBottomTemplate _bottom;
    
    public RegionTemplate()
    {
        _left = null;
        _top = null;
        _right = null;
        _bottom = null;
    }

    // Left, Top, Right, Bottom
    public RegionTemplate(LeftRightTemplate ell, TopBottomTemplate t,
    		              LeftRightTemplate r, TopBottomTemplate b)
    {
    	_left = ell;
    	_top = t;
    	_right = r;
    	_bottom = b;
    }

    public boolean leftIsPoint() { return _left instanceof LeftRightPointTemplate; }
    public boolean leftIsVertical() { return _left instanceof LeftRightVerticalTemplate; }
    public boolean rightIsPoint() { return _right instanceof LeftRightPointTemplate; }
    public boolean rightIsVertical() { return _right instanceof LeftRightVerticalTemplate; }

    public TopBottomTemplate bottom() { return _bottom; }
    public TopBottomTemplate top() { return _top; }
    
    //
    // A reasonable template defines:
    //     * a single template for left / right.
    //     * at least one function in the top / bottom.
    //
    public boolean verify()
    {
        if (_left == null || _top == null || _right == null || _bottom == null) return false;
        
        return _left.verify() && _top.verify() && _right.verify() && _bottom.verify();
    }
    
    public boolean equals(Object obj)
    {
    	if (obj == null) return false;
    	
    	if (!(obj instanceof RegionTemplate)) return false;

    	RegionTemplate that = (RegionTemplate)obj;
    	
        if (!this._left.equals(that._left)) return false;

        if (!this._top.equals(that._top)) return false;

        if (!this._right.equals(that._right)) return false;

        if (!this._bottom.equals(that._bottom)) return false;

        return true;
    }
    
    public String toString()
    {
	    String s = "";
	    s = "Left: " + this._left + "\n";
	    s += "Top: " + this._top + "\n";
	    s += "Right: " + this._right + "\n";
	    s += "Bottom: " + this._bottom + "\n";
        return s;
    }
    
    public enum SideT
    {
        LEFT(0),
        TOP(1),
        RIGHT(2),
        BOTTOM(3),
        UNRECOGNIZED(4);

        private final int value;
        private SideT(int value) { this.value = value; }
        public int getValue() { return value; }
        public static SideT convertToSide(char c) throws IllegalArgumentException
        {
            switch (c)
            {
                case 'l':
                case 'L':
                    return SideT.LEFT;
                case 't':
                case 'T':
                    return SideT.TOP;
                case 'r':
                case 'R':
                    return SideT.RIGHT;
                case 'b':
                case 'B':
                    return SideT.BOTTOM;

                default:
                   throw new IllegalArgumentException(c + " not recognized " + "convertToSide");
            }
        }
    }
}

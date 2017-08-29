package representation.bounds.functions;


public enum VariableT
{
    X(0), Y(1);

    private final int _value;
    private VariableT(int value) { this._value = value; }
    public int getValue() { return _value; }

    /**
     * @return variable, string-based representation
     */
    @Override
    public String toString()
    {
        switch (this)
        {
            case X: return "x";
            case Y: return "y";

            default:
                throw new IllegalArgumentException(this._value + " not recognized VariableT.toString()");
        }
    }
}

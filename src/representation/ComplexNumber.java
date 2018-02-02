package representation;

import utilities.Utilities;

public class ComplexNumber
{
    protected boolean _isInfinite;
    public boolean isInfinite() { return _isInfinite; }
    
    protected double _realPart;
    public double getReal() { return _realPart; }

    protected double _imagPart;
    public double getImaginary() { return _imagPart; }

    /**
     * Defaults to an infinite initialization
     */
    public ComplexNumber()
    {
        _realPart = Double.POSITIVE_INFINITY;
        _imagPart = Double.POSITIVE_INFINITY;
        _isInfinite = true;
    }
    
    /**
     * @param real -- real part (a in a + bi)
     * @param imag -- imaginary part (b in a + bi)
     */
    public ComplexNumber(double real, double imag)
    {
        _realPart = real;
        _imagPart = imag;
        _isInfinite = false;
    }

    public ComplexNumber(double real)
    {
        this (real, 0);
    }

    public boolean hasImaginaryPart()
    {
        return ! Utilities.equalDoubles( _imagPart, 0.0 ) ;
    }

    public ComplexNumber subtract( ComplexNumber rhs )
    {
        if( _isInfinite || rhs._isInfinite ) throw new RuntimeException("Subtraction involving infinity is undefined.") ;

        return new ComplexNumber(_realPart - rhs._realPart, _imagPart - rhs._imagPart);
    }

    public ComplexNumber add( ComplexNumber rhs )
    {
        if( _isInfinite || rhs._isInfinite ) throw new RuntimeException("Addition involving infinity is undefined.") ;

        return new ComplexNumber(_realPart + rhs._realPart, _imagPart + rhs._imagPart);
    }
    
    @Override
    public String toString()
    {
        return "ComplexNumber [ (" + _realPart + (_imagPart < 0 ? " - " : " + ") + Math.abs(_imagPart) + "i "
                + ", IsInfinity = " + _isInfinite + " ]" ;
    }
}

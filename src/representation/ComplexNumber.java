package representation;

import utilities.Utilities;

public class ComplexNumber
{
	public boolean IsInfinity    ;
	public double  RealPart      ;
	public double  ImaginaryPart ;
	
	public boolean hasImaginaryPart()
	{
		return ! Utilities.equalDoubles( ImaginaryPart, 0.0 ) ;
	}
	
	public ComplexNumber subtract( ComplexNumber rhs )
	{
		if( IsInfinity || rhs.IsInfinity )
			throw new RuntimeException( "Subtraction involving infinity is undefined.  Google 'Hilbert's Hotel'." ) ;
		
		ComplexNumber output = new ComplexNumber() ;
		output.RealPart = RealPart - rhs.RealPart ;
		output.ImaginaryPart = ImaginaryPart - rhs.ImaginaryPart ;
		return output ;
	}
	
	@Override
	public String toString()
	{
		return "ComplexNumber [ RealPart = " + RealPart
				+ ", ImaginaryPart = " + ImaginaryPart
				+ ", IsInfinity = " + IsInfinity + " ]" ;
	}
}

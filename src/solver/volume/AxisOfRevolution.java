package solver.volume;

import java.util.IllegalFormatException;

import exceptions.RepresentationException;
import representation.Point;
import representation.bounds.Bound;
import representation.bounds.functions.StringBasedFunction;
import representation.bounds.segments.HorizontalLineSegment;
import representation.bounds.segments.LineSegment;
import representation.bounds.segments.VerticalLineSegment;

/**
 * The axis of revolution is an infinite line by which we revolve
 * a plane curve around to construct a "solid of revolution".
 * 
 * For simplicity, we maintain a string-based representation as well as function-based representation.
 * 
 * @author calvi
 *
 */
public class AxisOfRevolution
{
    protected Bound _axis;                     // Treat it as a line; Vertical and Horizontal lines are both Bounds in the hierarchy (not LineSegment)
    protected StringBasedFunction _strAxis;    // Treat the axis as a function wrt a particular variable

    /**
     *     
     * @param axis -- string-based representation of a linear expression of the form x = C or y = C
     */
    public AxisOfRevolution(String axis) throws IllegalFormatException
    {
        final double COORDINATE_1 = -100;
        final double COORDINATE_2 = 100;
        axis = axis.trim().toLowerCase();
        
        //
        // Strip apart (1) variable and (2) constant:
        //
        String[] split = axis.split("=");
        char variable = split[0].charAt(0);
        double constant = Double.parseDouble(split[1]);

        switch (variable)
        {
            case 'x':
                try
                {
                    _axis = new VerticalLineSegment(new Point(constant, COORDINATE_1),
                                                    new Point(constant, COORDINATE_2));
                }
                catch (RepresentationException e) { e.printStackTrace(); }
                break;

            case 'y':
                try
                {
                    _axis = new HorizontalLineSegment(new Point(COORDINATE_1, constant),
                                                      new Point(COORDINATE_2, constant));
                } catch (RepresentationException e) { e.printStackTrace(); }
                break;

            default:
                throw new IllegalArgumentException("Unrecognized variable " + variable);
        }

        //
        // Construct a 'refined' string-based function representation
        //
        String function = variable + " = " + constant;

        _strAxis = new StringBasedFunction(function);
    }

    public boolean isVertical() { return _axis.isVertical(); }
    public boolean isHorizontal() { return _axis.isHorizontal(); }

    public String toFullMathematicaString()
    {
        return _strAxis.toFullMathematicaString();
    }
    
    @Override
    public String toString()
    {
        return _strAxis.toString();
    }
}

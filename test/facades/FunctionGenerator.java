package facades;

import exceptions.RepresentationException;
import representation.Point;
import representation.bounds.PointBound;
import representation.bounds.segments.HorizontalLineSegment;
import representation.bounds.segments.VerticalLineSegment;

public class FunctionGenerator
{
    /**
     * @return a bound based on a point
     */
    public static PointBound genPointBound(double x, double y)
    {
        return genPointBound(new Point(x, y));
    }
    public static PointBound genPointBound(Point pt)
    {
        return new PointBound(pt);
    }
    
    /**
     * @param bottom -- a point
     * @param top -- a point
     * @return a vertical line between the two points
     * @throws RepresentationException if the points are not vertical
     */
    public static VerticalLineSegment genVerticalSegment(Point bottom, Point top) throws RepresentationException
    {
        //
        // Create the line, catch, and re-throw
        //
        VerticalLineSegment line = null;
        try
        {
            line = new VerticalLineSegment(bottom, top);
        }
        catch(RepresentationException e)
        {
            System.err.println("X-values disagree with vertical" + bottom.getX() + " " + top.getX());
            throw e;
        }

        return line;
    }

    /**
     * @param x -- an x-coordinate
     * @param y1 -- an y-coordinate
     * @param y2 -- an y-coordinate
     * @return a vertical lines segment connecting: (x, y1) to (x, y2)
     */
    public static VerticalLineSegment genVerticalSegment(double x, double y1, double y2)
    {
        VerticalLineSegment line = null;
        try
        {
            line = genVerticalSegment(new Point(x, y1), new Point(x, y2));
        }
        catch(RepresentationException e)
        {
            System.err.println("X-values disagree with vertical: this is not possible.");
        }

        return line;
    }
    
    /**
     * @param left -- a point
     * @param right -- a point
     * @return a horizontal line between the two points
     * @throws RepresentationException if the points are not vertical
     */
    public static HorizontalLineSegment genHorizontalSegment(Point left, Point right) throws RepresentationException
    {
        //
        // Create the line, catch, and re-throw
        //
        HorizontalLineSegment line = null;
        try
        {
            line = new HorizontalLineSegment(left, right);
        }
        catch(RepresentationException e)
        {
            System.err.println("Y-values disagree with horizontal" + left.getY() + " " + right.getY());
            throw e;
        }

        return line;
    }

    /**
     * @param x1 -- an x-coordinate
     * @param x2 -- an x-coordinate
     * @param y -- an y-coordinate
     * @return a horizontal lines segment connecting: (x1, y) to (x2, y)
     */
    public static HorizontalLineSegment genHorizontalSegment(double x1, double x2, double y)
    {
        HorizontalLineSegment line = null;
        try
        {
            line = genHorizontalSegment(new Point(x1, y), new Point(x2, y));
        }
        catch(RepresentationException e)
        {
            System.err.println("X-values disagree with vertical: this is not possible.");
        }

        return line;
    }
}

package facades;

import representation.Point;
import representation.bounds.PointBound;
import representation.bounds.functions.BoundedFunction;
import representation.bounds.functions.FunctionT;
import representation.bounds.segments.HorizontalLineSegment;
import representation.bounds.segments.VerticalLineSegment;
import representation.regions.LeftRight;
import representation.regions.Region;
import representation.regions.TopBottom;

public class RegionGenerator
{
    /**
     * @return a region that is strictly a rectangle: verticals and horizontals
     */
    public static Region generateOriginRectangleRegion()
    {
        return generateRectangleRegion(0, 10, 0, 3);
    }

    /**
     * @return a region that is strictly a square: verticals and horizontals
     */
    public static Region generateOriginSquareRegion()
    {
        return generateRectangleRegion(0, 4, 0, 4);
    }

    /**
     * @param left_x -- left x bound
     * @param right_x -- right x bound
     * @param bottom_y -- bottom y bound
     * @param top_y -- top y bound
     * @return a region described by a rectangle
     */
    private static Region generateRectangleRegion(double left_x, double right_x, double bottom_y, double top_y)
    {
        VerticalLineSegment left = FunctionGenerator.genVerticalSegment(left_x, bottom_y, top_y);
        
        HorizontalLineSegment top = FunctionGenerator.genHorizontalSegment(left_x, right_x, top_y);
        top.setDomain(left_x, right_x);

        VerticalLineSegment right = FunctionGenerator.genVerticalSegment(right_x, bottom_y, top_y);
        
        HorizontalLineSegment bottom = FunctionGenerator.genHorizontalSegment(left_x, right_x, bottom_y);
        bottom.setDomain(left_x, right_x);
        
        LeftRight leftBound = new LeftRight(left);
        TopBottom topBound = new TopBottom(top);
        LeftRight rightBound = new LeftRight(right);
        TopBottom bottomBound = new TopBottom(bottom);
        
        Region region = new Region(leftBound, topBound, rightBound, bottomBound);

        return region;
    }
    
    /**
     * @return a region described by:
     *  The line:    y = 2x  AND
     *  Parabola:    y = x^2
     *  
     */
    public static Region generateParabolaCappedWithLine()
    {
        PointBound left = FunctionGenerator.genPointBound(0, 0);
        PointBound right = FunctionGenerator.genPointBound(2, 4);

        BoundedFunction line = new BoundedFunction(FunctionT.LINEAR);
        line.stretch(2, 1);
        line.setDomain(0, 2);
        
        BoundedFunction parabola = new BoundedFunction(FunctionT.PARABOLA);
        parabola.setDomain(0, 2);
        
        LeftRight leftBound = new LeftRight(left);
        TopBottom topBound = new TopBottom(line);
        LeftRight rightBound = new LeftRight(right);
        TopBottom bottomBound = new TopBottom(parabola);
        
        Region region = new Region(leftBound, topBound, rightBound, bottomBound);

        return region;
    }
    
    /**
     * @return a region described by:
     *  Parabola:    y = - (x - 2)^2 + 4  AND
     *  Parabola:    y = x^2
     *  
     */
    public static Region generateCappedParabolas()
    {
        // LEFT / RIGHT
        PointBound left = FunctionGenerator.genPointBound(0, 0);
        PointBound right = FunctionGenerator.genPointBound(2, 4);

        // TOP
        BoundedFunction top = new BoundedFunction(FunctionT.PARABOLA);
        top.reflectOverX();
        top.translate(new Point(2, 4));
        top.setDomain(0, 2);

        // BOTTOM
        BoundedFunction bottom = new BoundedFunction(FunctionT.PARABOLA);
        bottom.setDomain(0, 2);

        //
        // REGION
        //
        LeftRight leftBound = new LeftRight(left);
        TopBottom topBound = new TopBottom(top);
        LeftRight rightBound = new LeftRight(right);
        TopBottom bottomBound = new TopBottom(bottom);
        
        Region region = new Region(leftBound, topBound, rightBound, bottomBound);

        return region;
    }
     
    /**
     * @return a region described by:
     *  Top Parabola:    y = - (x - 1)^2 + 5
     *  Bottom 1 : y = x^3 [0, 1]
     *  Bottom 2 : y = x^2 [1, 2]
     *  
     */
    public static Region generateTwoBottomOneTop()
    {
        // LEFT / RIGHT
        VerticalLineSegment left = FunctionGenerator.genVerticalSegment(0, 0, 4);
        PointBound right = FunctionGenerator.genPointBound(2, 4);

        // TOP
        BoundedFunction top = new BoundedFunction(FunctionT.PARABOLA);
        top.reflectOverX();
        top.translate(new Point(1, 5));
        top.setDomain(0, 2);

        // BOTTOM
        BoundedFunction bottom1 = new BoundedFunction(FunctionT.CUBIC);
        bottom1.setDomain(0, 1);
        BoundedFunction bottom2 = new BoundedFunction(FunctionT.PARABOLA);
        bottom2.setDomain(1, 2);
        
        //
        // REGION
        //
        LeftRight leftBound = new LeftRight(left);
        TopBottom topBound = new TopBottom(top);
        LeftRight rightBound = new LeftRight(right);
        TopBottom bottomBound = new TopBottom();
        bottomBound.addBound(bottom1);
        bottomBound.addBound(bottom2);
        
        Region region = new Region(leftBound, topBound, rightBound, bottomBound);

        return region;
    }

    /**
     * @return a region described by:
     *  Top 1:    y = sin (\pi x) + 4    [0, 2]
     *  Top 2:    y = 4                  [2, 4]
     *  Top 3:    y = x                  [4, 6]
     *  Bottom 1 : y = -1/4(x - 2)^2 + 1 [0, 4]
     *  Bottom 2 : y = 3(x - 6) + 6      [4, 6]
     */
    public static Region generateThreeTopTwoBottomAligned()
    {
        // LEFT / RIGHT
        VerticalLineSegment left = FunctionGenerator.genVerticalSegment(0, 0, 4);
        PointBound right = FunctionGenerator.genPointBound(6, 6);

        //
        // TOP
        //
        BoundedFunction top1 = new BoundedFunction(FunctionT.SINE);
        top1.translate(new Point(0, 4));
        top1.setDomain(0, 2);

        BoundedFunction top2 = new BoundedFunction(FunctionT.HORIZONTAL_LINE);
        top2.translate(new Point(0, 4));
        top2.setDomain(2, 4);
        
        BoundedFunction top3 = new BoundedFunction(FunctionT.LINEAR);
        top3.setDomain(4, 6);
        
        //
        // BOTTOM
        //
        BoundedFunction bottom1 = new BoundedFunction(FunctionT.PARABOLA);
        bottom1.translate(2, 1);
        bottom1.reflectOverX();
        bottom1.stretch(-0.25, 1);
        bottom1.setDomain(0, 4);

        BoundedFunction bottom2 = new BoundedFunction(FunctionT.LINEAR);
        bottom2.stretch(3, 1);
        bottom2.translate(6, 6);
        bottom2.setDomain(4, 6);
        
        //
        // REGION
        //
        LeftRight leftBound = new LeftRight(left);
        TopBottom topBound = new TopBottom();
        topBound.addBound(top1);
        topBound.addBound(top2);
        topBound.addBound(top3);
        
        LeftRight rightBound = new LeftRight(right);
        TopBottom bottomBound = new TopBottom();
        bottomBound.addBound(bottom1);
        bottomBound.addBound(bottom2);
        
        Region region = new Region(leftBound, topBound, rightBound, bottomBound);

        return region;
    }
    
    /**
     * @return a region described by:
     *  Top 1:    y = -1/2 (x - 2)^2 + 2     [0, 3]
     *  Top 2:    y = 1/2(x - 4)^4 + 1       [3, 6]
     *  Top 3:    y = -(x - 6)^3 + 9         [6, 9]
     *  
     *  Bottom 1 : y = 1/2(cos \pi(x - 0.5)) [0, 5]
     *  Bottom 2 : y = 0                     [5, 7]
     *  Bottom 3 : y = -9x + 63              [7, 9]
     */
    public static Region generateThreeTopThreeBottomUnaligned()
    {
        // LEFT / RIGHT
        PointBound left = FunctionGenerator.genPointBound(0, 0);
        PointBound right = FunctionGenerator.genPointBound(9, -18);

        //
        // TOP
        //
        BoundedFunction top1 = new BoundedFunction(FunctionT.PARABOLA);
        top1.translate(2, 2);
        top1.stretch(0.5, 1);
        top1.reflectOverX();
        top1.setDomain(0, 3);

        BoundedFunction top2 = new BoundedFunction(FunctionT.QUARTIC);
        top2.translate(4, 1);
        top2.stretch(0.5, 1);
        top2.setDomain(3, 6);
        
        BoundedFunction top3 = new BoundedFunction(FunctionT.CUBIC);
        top3.translate(6, 9);
        top3.reflectOverX();
        top3.setDomain(6, 9);
        
        //
        // BOTTOM
        //
        BoundedFunction bottom1 = new BoundedFunction(FunctionT.COSINE);
        bottom1.translate(0.5, 0);
        bottom1.stretch(0.5, Math.PI);
        bottom1.setDomain(0, 5);

        BoundedFunction bottom2 = new BoundedFunction(FunctionT.HORIZONTAL_LINE);
        bottom2.setDomain(5, 7);
        
        BoundedFunction bottom3 = new BoundedFunction(FunctionT.LINEAR);
        bottom3.translate(0, 63);
        bottom3.stretch(-9, 1);
        bottom3.setDomain(7, 9);
        
        //
        // REGION
        //
        LeftRight leftBound = new LeftRight(left);
        TopBottom topBound = new TopBottom();
        topBound.addBound(top1);
        topBound.addBound(top2);
        topBound.addBound(top3);
        
        LeftRight rightBound = new LeftRight(right);
        TopBottom bottomBound = new TopBottom();
        bottomBound.addBound(bottom1);
        bottomBound.addBound(bottom2);
        bottomBound.addBound(bottom3);
        
        Region region = new Region(leftBound, topBound, rightBound, bottomBound);

        return region;
    }
    
    /**
     * @return a region that is strictly a square: verticals and horizontals
     */
    public static Region generateBlockBasedRegion()
    {
        return generateBlockBasedRegion(0, 5, 10,   0, 4, 8);
    }
    
    /**
     * @param left_x -- left x bound
     * @param right_x -- right x bound
     * @param bottom_y -- bottom y bound
     * @param top_y -- top y bound
     * @return a region described by a tetris piece:
     * _________________
     * |                |
     * |        ________|
     * |        |
     * |        |
     * |________|
     */
    private static Region generateBlockBasedRegion(double left_x, double middle_x, double right_x,
                                                   double bottom_y, double middle_y, double top_y)
    {
        //
        // Lines
        //
        VerticalLineSegment left = FunctionGenerator.genVerticalSegment(left_x, bottom_y, top_y);
        HorizontalLineSegment top = FunctionGenerator.genHorizontalSegment(left_x, right_x, top_y);
        top.setDomain(left_x, right_x);

        VerticalLineSegment right = FunctionGenerator.genVerticalSegment(right_x, middle_y, top_y);

        VerticalLineSegment middle = FunctionGenerator.genVerticalSegment(middle_x, bottom_y, middle_y);
        
        HorizontalLineSegment bottom1 = FunctionGenerator.genHorizontalSegment(left_x, middle_x, bottom_y);
        bottom1.setDomain(left_x, middle_x);
        
        HorizontalLineSegment bottom2 = FunctionGenerator.genHorizontalSegment(middle_x, right_x, middle_y);
        bottom2.setDomain(middle_x, right_x);
        
        //
        // Bounds
        //
        LeftRight leftBound = new LeftRight(left);
        TopBottom topBound = new TopBottom(top);
        LeftRight rightBound = new LeftRight(right);

        TopBottom bottomBound = new TopBottom();
        bottomBound.addBound(bottom1);
        bottomBound.addBound(middle);
        bottomBound.addBound(bottom2);

        //
        // Region
        //
        Region region = new Region(leftBound, topBound, rightBound, bottomBound);

        return region;
    }
}

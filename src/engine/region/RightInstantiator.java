package engine.region;

import java.util.Vector;

import engine.Instantiable;
import exceptions.RepresentationException;
import representation.regions.Region;
import template.RegionTemplate;
import representation.bounds.Bound;
import representation.bounds.segments.VerticalLineSegment;
import representation.regions.LeftRight;
import globals.Constants;
import representation.Point;

public class RightInstantiator implements Instantiable
{
    //
    //
    /////////////////     Instantiation functions     /////////////////
    //
    //
    // Seals the _right side of a region with a segment
    //
    // Uses the following algorithm:
    //
    // If the _right bound is a vertical line segment, construct that segment by:
    //    1) Acquire the _right-most bound from both region.getTop() (t) and region.getBottom() (b)
    //    2) Find the _left bound of each: lb, lt
    //    3) Use the start_x := max(lb, lt) as the starting point to begin searching for proper closure
    //    4) Generate a small random number so that start_x := start_x + randInt()
    //    5) Acquire _right-most bound for the entire region (if it has been defined): MAX
    //    6) int_save_x = -1; rat_save_x = -1;
    //    7) for all  close_x := start_x to MAX
    //         Check for proper closure 
    //            * Proper closure is:
    //              double f1x = f1.evaluate(close_x))
    //              double f2x = f2.evaluate(close_x))
    //              if (IsInteger( f1x ) && IsInteger( f2x )) return close_x
    //              if (rat_save_x == -1 && IsRational( f1x ) && IsRational( f2x )) rat_save_x := close_x
    //              if (int_save_x == -1 && IsInteger( f1x ) || IsInteger( f2x )) int_save_x := close_x
    //    8) If integers only : problem
    //    9) If rational only: If rat_save_x == -1 : problem
    //    10) If rat_save_x != -1 then return rat_save_x
    //        else if int_save_x != -1 then return int_save_x
    //    11) return start_x
    //
    public double findRightSealX(Region region) throws exceptions.OptionsException
    {
        // 1) Acquire the _right-most bound from both region.getTop() and region.getBottom()
    	//TODO: come back to this (these next two are declared but never used)
		//Bound last_top = region.getTop().lastBound();
        //Bound last_bottom = region.getBottom().lastBound();

        // 2) Find the _left point of the domain for each bound lb, lt
        Point _left_topDomain = region.getTop().acquireLeftBoundPoint(); 
        Point _left_bottomDomain = region.getBottom().acquireLeftBoundPoint();

        // 3) Use the start_x := max(lb, lt) as the starting point to begin searching for proper closure
        int start_X = (int)Math.ceil(Math.max(_left_topDomain.getX(), _left_bottomDomain.getX()));

        // 4) Generate a small random number so that start_x := start_x + randInt()
        start_X += utilities.RandomGenerator.nextInt(3);

        return findRightSealX(region, start_X);
    }
    
    public double findRightSealX(Region region, double start) throws exceptions.OptionsException
    {
        return findRightSealX(region, start, globals.Constants.MAX_RIGHT_X);
    }
    
    public double findRightSealX(Region region, double start, double S_top) throws exceptions.OptionsException
    {
        // 1) Acquire the _right-most bound from both region.getTop() and region.getBottom()
        Bound last_top = region.getTop().lastBound();
        Bound last_bottom = region.getBottom().lastBound();

        // 3) Use the start_x := max(lb, lt) as the starting point to begin searching for proper closure
        int start_X = (int)Math.ceil(start);

        // 6) int_save_x = -1; rat_save_x = -1;
        int firstValidSingleInt_X = -1;
        int firstValidDualRational_X = -1;
        int firstValidSingleRational_X = -1;

        // 7) for all  close_x := start_x to MAX
        for (int close_X = start_X; close_X <= S_top; close_X++)
        {
            // Check for proper closure; Proper closure is:
            //   double f1y = f1.evaluate(close_x))
            double _top_Y = last_top.evaluateAtPoint(close_X); 

            //   double f2y = f2.evaluate(close_x))
            double _bottom_Y = last_bottom.evaluateAtPoint(close_X);

            // Save the x-value for later if one or both y-values are integer values
            if (firstValidSingleInt_X == -1)
            {
                boolean _topYinteger = utilities.Utilities.isInteger(_top_Y);
                boolean botYinteger = utilities.Utilities.isInteger(_bottom_Y);

                // If the x-value results in both y-values being integers, use it.
                if (_topYinteger && botYinteger) return close_X;

                if (_topYinteger || botYinteger) firstValidSingleInt_X = close_X;
            }
            
            // Save the x-value for later if both y-values are rational values
            if (firstValidSingleRational_X == -1)
            {
                boolean _topYrational = utilities.Utilities.isRational(_top_Y);
                boolean botYrational = utilities.Utilities.isRational(_bottom_Y);

                if (_topYrational && botYrational)
                {
                    firstValidDualRational_X = close_X;
                    firstValidSingleRational_X = close_X;
                }

                if (_topYrational || botYrational) firstValidSingleRational_X = close_X;
            }
        }

        //
        // Constants only option results in a problem here
        //
        if (Constants.INTEGERS_ONLY) throw new exceptions.OptionsException("Option dictated integers only; not possible in this case.");
        
        //
        // Select which is the best x based on integer / rational values
        //   Priorities, in order:
        //      a) both rationals
        //      b) one integer
        //      c) one rational
        //      d) default is where we started searching: start_x
        //
        if (firstValidDualRational_X != -1) return firstValidDualRational_X;
            
        if (firstValidSingleInt_X != -1) return firstValidSingleInt_X;
        
        if (firstValidSingleRational_X != -1) return firstValidSingleRational_X;

        return start;
    }

    public void constructRightSegment(Region region, double x)
    {
        // Acquire the _right-most bounds from both region.getTop() and region.getBottom()
        Bound last_top = region.getTop().lastBound();
        Bound last_bottom = region.getBottom().lastBound();

        // Acquire y-values
        double _top_Y = last_top.evaluateAtPoint(x); 
        double _bottom_Y = last_bottom.evaluateAtPoint(x);
        
        Point _topPt = new Point(x, _top_Y);
        Point _bottomPt = new Point(x, _bottom_Y);
        
        VerticalLineSegment line = null;
        try 
        {
            line = new VerticalLineSegment(_bottomPt, _topPt);
        }
        catch(RepresentationException re)
        {
            System.err.println("How can this happen? It's crazy vertical stuff.");
        }
        
        region.setRight(new LeftRight(line));
    }

    @Override
    public Vector<Region> instantiate(Region region, RegionTemplate template)
    {
        throw new RuntimeException( "RightInstantiator.instantiate is not implemented yet!" ) ;
    }
}





//    //
//    // Seals a region with right template being a vertical line segment
//    //
//    private Vector<Region> sealRegion(RegionTemplate theTemplate, Region region)
//    {
//        Vector<Region> regions = new Vector<Region>();
//
//        //
//        // Synthesis up to this point should have a verified 'left' seal.
//        //
//
//        //
//        // If the right bound is a point, we don't need to do anything
//        //
//        if (theTemplate.rightIsPoint())
//        {
//            if (region.verifySeals()) regions.add(region);
//
//            return regions;
//        }
//
//        //
//        // If the right bound is a vertical line segment, construct that segment
//        //     (do so for all valid possible regions)
//        if (theTemplate.rightIsVertical())
//        {
//            double seal_X = -1;
//
//            try
//            {
//                seal_X = region.findRightSealX();
//            }
//            catch (FrontEnd.OptionsException oe)
//            {
//                System.out.println("Constants-only option could not be satisfied.");
//            }
//
//            double prevSeal_X = -1;
//            while (!Utilities.compareValues(prevSeal_X, seal_X))
//            {
//                // Save the x-value
//                prevSeal_X = seal_X;
//
//                // Create the corresponding region
//                Region copy = new Region(region);
//                copy.constructRightSegment(seal_X);
//                if (copy.verifySeals()) regions.add(copy);
//
//                try
//                {
//                    seal_X = region.findRightSealX(seal_X);
//                }
//                catch (FrontEnd.OptionsException oe)
//                {
//                    System.out.println("Constants-only option could not be satisfied.");
//                }
//            }
//        }
//
//        return regions;
//    }
//
//    //
//    // Closes a region
//    //
//    //     For this function, a region will be:
//    //        a) require a top function with right vertical line
//    //        b) require a top function to close with a point (Lagrange polynomial)
//    //
//    private Vector<Region> closeRegion(RegionTemplate theTemplate, Region region)
//    {
//        //
//        // Does the top require one more function? 
//        //
//        region.top.length()    == theTemplate.top.length())
//
//// Make sure the template is satisfied with the number of functions.
//if (region.bottomLength() == theTemplate.bottomLength() &&
//region.top.length()    == theTemplate.top.length())
//{
//    if (theTemplate.rightIsVertical()) 
//    {
//
//    }
//    else
//    {
//        ////////////////////RIGHT POINT BOUND
//        if (((BoundedFunction)region.top.bounds.lastElement()).getFunc() != FunctionT.HORIZONTAL_LINE && 
//                ((BoundedFunction)region.bottom.bounds.lastElement()).getFunc() != FunctionT.HORIZONTAL_LINE) {
//
//            if (((BoundedFunction) region.top.bounds.lastElement()).getFunc() == FunctionT.LINEAR) {
//                BoundedFunction top = (BoundedFunction) region.top.bounds.lastElement();
//                BoundedFunction bottom = (BoundedFunction) region.bottom.bounds.lastElement();
//
//                Point topLeftPoint = new Point(top.leftX, top.evaluateAtPoint(top.leftX));
//                Point bottomRightPoint = new Point(bottom.rightX, bottom.evaluateAtPoint(bottom.rightX));
//
//                BoundedFunction newTop = generateLineBetweenPoints(topLeftPoint, bottomRightPoint);
//                region.top.bounds.remove(top);
//                region.top.bounds.add(newTop);
//
//                region.right.bound = new PointBound(bottomRightPoint);
//                regions.add(region);
//            } else if (((BoundedFunction) region.bottom.bounds.lastElement()).getFunc() == FunctionT.LINEAR) {
//                BoundedFunction top = (BoundedFunction) region.top.bounds.lastElement();
//                BoundedFunction bottom = (BoundedFunction) region.bottom.bounds.lastElement();
//
//                Point bottomLeftPoint = new Point(bottom.leftX, bottom.evaluateAtPoint(bottom.leftX));
//                Point topRightPoint = new Point(top.rightX, top.evaluateAtPoint(top.rightX));
//
//                BoundedFunction newBottom = generateLineBetweenPoints(bottomLeftPoint, topRightPoint);
//                region.bottom.bounds.remove(bottom);
//                region.bottom.bounds.add(newBottom);
//
//                region.right.bound = new PointBound(topRightPoint);
//                regions.add(region);
//            } else if (((BoundedFunction) region.top.bounds.lastElement()).getFunc() == FunctionT.PARABOLA) {
//                BoundedFunction top = (BoundedFunction) region.top.bounds.lastElement();
//                BoundedFunction bottom = (BoundedFunction) region.bottom.bounds.lastElement();
//
//                Point leftPoint = new Point(top.leftX, top.evaluateAtPoint(top.leftX));
//                Point rightPoint = new Point(bottom.rightX, bottom.evaluateAtPoint(bottom.rightX));
//
//                double d = rightPoint.y - leftPoint.y;
//
//                Point middlePoint = new Point ((leftPoint.x + rightPoint.x)/2, leftPoint.y + (d / 3));
//
//                Point[] points = {leftPoint, middlePoint, rightPoint};
//
//                String mathematicaOutput = new CasInterface().getLagrangePolynomial(points);
//                //Will return null because parser needs to be finished.
//                //BoundedFunction newTop = new CasInterface().parseLagrangePolynomial(mathematicaOutput);
//
//                region.top.bounds.remove(top);
//                //region.top.bounds.add(newTop);
//
//                region.right.bound = new PointBound(rightPoint);
//                regions.add(region);
//            } else if (((BoundedFunction) region.bottom.bounds.lastElement()).getFunc() == FunctionT.PARABOLA) {
//                //TODO: Implement Lagrange Polynomial interpolation for bottom function.
//            }
//        }
//    }
//}
//else  //Else, keep constructing
//{
//    regions = constructTopBottom(theTemplate, regions, tempWorkingRegion);
//}
//
//
//
//
//
//
//
//
//
//
//
//
//private BoundedFunction generateLineBetweenPoints(Point p1, Point p2) {
//    double leftBound = p1.x, rightBound = p2.x;
//
//    double b = 1;  //No need to get crazy here, or anything.
//
//    double a = (p2.y - p1.y)/(p2.x - p1.x);
//
//    double h = 0;
//
//    double k = a * (- p2.x) + p2.y;
//
//    return new BoundedFunction(FunctionT.LINEAR, a, b, h, k, leftBound, rightBound);
//} 
//
//
////Checks to make sure the BoundedFunction does not intersect any of the bounds.
//public boolean satisfies(BoundedFunction f, Vector<Bound> bounds)
//{
//    // An empty set by default satisfies the function
//    if (bounds.isEmpty()) return true;
//
//    //
//    // Check in reverse to ensure the function satisfies each function in the bounds 
//    //
//    for (int index = bounds.size() - 1; index >= 0; index++)
//    {
//        //            if (bounds[index].intersects(f))
//        //            {
//        //                
//        //            }
//    }
//
//    return true;
//}


package engine.bounds;

import representation.bounds.Bound;
import representation.bounds.segments.VerticalLineSegment;
import representation.regions.LeftRight;
import representation.regions.Region;
import template.RegionTemplate;

import java.util.Vector;

import engine.Instantiable;
import exceptions.RepresentationException;
import globals.Constants;
import representation.Point;

public class SegmentInstantiator implements Instantiable
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
            double _top_Y = last_top.evaluateAtPoint(close_X).getReal(); 

            //   double f2y = f2.evaluate(close_x))
            double _bottom_Y = last_bottom.evaluateAtPoint(close_X).getReal();

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
        double _top_Y = last_top.evaluateAtPoint(x).getReal(); 
        double _bottom_Y = last_bottom.evaluateAtPoint(x).getReal();
        
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
        throw new RuntimeException( "SegmentInstantiator.instantiate has unresolved build problems!" ) ;
    }
}

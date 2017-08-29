package engine.region;

import java.util.Vector;

import engine.Instantiable;
import representation.bounds.segments.LineSegment;
import representation.regions.Region;
import globals.Constants;
import representation.Point;
import template.RegionTemplate;

public class LeftInstantiator implements  Instantiable
{
    //
    // Instantiate the left bound according to the template: either point or line segment
    // Since there is not room for variance in synthesis, the return is a single region with left bound defined.
    //
    public Vector<Region> instantiate(RegionTemplate theTemplate)
    {
        Region region = new Region();

        // Begin with the origin as the bottom-left point by default
        Point topLeft = null;

        //
        // If a point, we default to the origin
        //
        if (theTemplate.leftIsPoint()) region.instantiateLeft(Point.ORIGIN);

        //
        // Handle vertical segment at left
        //
        else
        {
            int vertical = utilities.RandomGenerator.nextInt(1, Constants.MAX_VERTICAL_SHIFT);
            topLeft = bottomLeft.plus(new Point(0, vertical));

            workingRegion.instantiateLeft(new LineSegment(bottomLeft, topLeft));
        }

        return workingRegion;
    }
}
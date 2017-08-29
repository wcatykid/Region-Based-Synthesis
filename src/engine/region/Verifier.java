package engine.region;

import representation.regions.Region;
import representation.Point;

//
//
// Instantiation verification functions: Singleton pattern
//
//
public class Verifier
{
	//
	// Singleton Pattern (Begin)
	//
	private static Verifier _instance;
	
	public static Verifier getInstance()
	{
		if (_instance == null) _instance = new Verifier();

		return _instance;
	}
	
	public Verifier() {}
	// Singleton Pattern (End)

	/**
	 * @param region -- a 'complete' region we wish to verify is complete
	 * @return true / false whether the region is legitimate
	 */
	public boolean verify(Region region)
	{
		// Verify Left / Right Boundaries
		boolean seals = verifySeals(region);

		// Verify Top / Bottom Boundaries
		boolean tb = true; // verifyTopBottom(region);

		// Combined result
		return seals && tb;
	}
	
    //
    // Ensure the region is closed properly on the left and right
    //
    public boolean verifySeals(Region region)
    {
        boolean result = true;

        if (region.getLeft().isVertical()) result = result && verifyTopBottomLeftSegmentSeal(region);
        if (region.getLeft().isPoint()) result = result && verifyTopBottomLeftPointSeal(region);

        if (region.getRight().isVertical()) result = result && verifyTopBottomRightSegmentSeal(region);
        if (region.getRight().isPoint()) result = result && verifyTopBottomRightPointSeal(region);

        return result;
    } 
    
    //
    // Ensure the region is closed on the right with a point
    //
    private boolean verifyTopBottomRightPointSeal(Region region)
    {
        if (!region.getRight().isPoint()) return false;

        // Acquire the last point from both the last top / bottom: compare
        Point rightBottom = region.getBottom().acquireRightBoundPoint();
        Point rightTop = region.getTop().acquireRightBoundPoint();

        return rightBottom.equals(rightTop);
    }

    private boolean verifyTopBottomLeftPointSeal(Region region)
    {
        if (!region.getLeft().isPoint()) return false;

        // Acquire the last point from both the last top / bottom: compare
        Point leftBottom = region.getBottom().acquireLeftBoundPoint();
        Point leftTop = region.getTop().acquireLeftBoundPoint();

        return leftBottom.equals(leftTop);
    }

    //
    // Ensure the region is closed on the right with a segment
    //
    private boolean verifyTopBottomRightSegmentSeal(Region region)
    {
        if (!region.getRight().isVertical()) return false;

        // Acquire the last point from both the last top / bottom: compare
        Point rightBottom = region.getBottom().acquireRightBoundPoint();
        Point rightTop = region.getTop().acquireRightBoundPoint();

        return rightBottom.equals(region.getRight().getMinimum()) && rightTop.equals(region.getRight().getMaximum());
    }

    private boolean verifyTopBottomLeftSegmentSeal(Region region)
    {
        if (!region.getLeft().isVertical()) return false;

        // Acquire the last point from both the last top / bottom: compare
        Point leftBottom = region.getBottom().acquireLeftBoundPoint();
        Point leftTop = region.getTop().acquireLeftBoundPoint();

        return leftBottom.equals(region.getLeft().getMinimum()) && leftTop.equals(region.getLeft().getMaximum());
    }  
}

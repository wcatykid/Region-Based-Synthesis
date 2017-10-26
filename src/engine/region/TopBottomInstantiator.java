package engine.region;

import java.util.Vector;

import engine.Instantiable;
import frontend.Options;
import representation.Point;
import representation.bounds.Bound;
import representation.regions.Region;
import representation.regions.TopBottom;
import template.RegionTemplate;

public class TopBottomInstantiator implements Instantiable
{
	//
	// Generate all top / bottom functions (save one)
	//
	// Decide either _top of _bottom to add a function to:
	//    * Rule: must leave a final function on _top or _bottom;
	//            specifically, must leave a final polynomial, if possible. 
	//    * Otherwise, choose the _top or _bottom based on which has 'fewer' functions _left to process.
	//                 if a tie, choose _bottom over _top.
	//
	@Override
	public Vector<Region> instantiate(Region startRegion, RegionTemplate template)
	{
		// This vector will be populated with more and more  
		Vector<Region> regions = new Vector<Region>();

		instantiateHelper(startRegion, template, regions);

		return regions;
	}

	//
	// Recursive, depth-first (tree-based) analysis / instantiation of a single function
	//
	private void instantiateHelper(Region region, RegionTemplate template, Vector<Region> completed)
	{
		// Number of remaining functions to append
		int bottomRemaining = template.bottom().length() - region.bottomLength();
		int topRemaining = template.top().length() - region.topLength();

		//
		// We are done synthesizing if we have one more function to append
		// This is a 'leaf' in the tree; thus add this region to the list of completed regions
		//
		if (bottomRemaining + topRemaining == 1)
		{
			completed.add(region);
			return;
		}

		//
		// Choose the top or bottom: which one has 'more' functions to fill in? Favor bottom.
		//
		Vector<Region> appended = null;
		// TOP
		if (topRemaining > bottomRemaining)
		{
			appended = instantiateTop(region, template);
		}
		// BOTTOM
		else
		{
			appended = instantiateBottom(region, template);
		}

		//
		// For each of the new region options (from appending a single function), instantiate further
		//
		for (Region r : appended)
		{
			instantiateHelper(r, template, completed);
		}
	}

	/**
	 * @param region -- a region with n functions on top
	 * @param template
	 * @return a set of regions with (n+1) functions on top
	 * 
	 * In order to synthesize a function, we must have a start point and the type desired
	 */
	private Vector<Region> instantiateTop(Region region, RegionTemplate template)
	{
        // Acquire the starting point
		Point startPoint = region.getTop().acquireRightBoundPoint();
		
		// Index of the next function to instantiate in the top
		int index = region.topLength() + 1;

		//
		// For each function type, acquire the set of possible regions
		//
		Vector<Bound> newBounds = new Vector<Bound>();
		for (Bound.BoundT type : template.top().getRestrictionTypes(index))
		{
			if (Options.DEBUG) { System.out.println("Considering top function type: " + type); }
			
			newBounds.addAll(instantiateBound(startPoint, region.getTop(), type));
		}
		
		//
		// For each new 'appendable' function, create a copy of the region and append this new function 
        // Create region copies with all of these new bounds
		//
        Vector<Region> regions = new Vector<Region>();
        for (Bound bound : newBounds)
        {
            Region copy = region.clone();

            copy.getTop().addBound(bound);

            regions.add(copy);
        }
        
		return regions;
	}

	/**
	 * @param region -- a region with n functions on bottom
	 * @param template
	 * @return a set of regions with (n+1) functions on bottom
	 * 
	 * In order to synthesize a function, we must have a start point and the type desired
	 */
	private Vector<Region> instantiateBottom(Region region, RegionTemplate template)
	{
        // Acquire the starting point
		Point startPoint = region.getBottom().acquireRightBoundPoint();
		
		// Index of the next function to instantiate in the top
		int index = region.bottomLength() + 1;

		//
		// For each function type, acquire the set of possible regions
		//
		Vector<Region> regions = new Vector<Region>();
		for (Bound.BoundT type : template.bottom().getRestrictionTypes(index))
		{
			if (Options.DEBUG) { System.out.println("Considering bottom function type: " + type); }
			
			// For each new 'appendable' function, create a copy of the region and append this new function 
			for (Bound bound : instantiateBound(startPoint, region.getBottom(), type))
			{
				Region copy = region.clone();

				copy.getBottom().addBound(bound);

				regions.add(copy);
			}
		}
		
		return regions;
	}

	   /**
     * @param pt -- a left bound to attach the new function to (on the left)
     * @param other -- the top (or bottom
     * @param type -- The type of bound we are attempting to append
     * @return set of allowable functions that start at (pt) and do not intersect the (other) bound at all
     */
    private Vector<Bound> instantiateBound(Point pt, TopBottom other, Bound.BoundT type)
    {
        Vector<Bound> bounds = new Vector<>();

        // Special Case: Vertical Line
        if (type == Bound.BoundT.VERTICAL_LINE)
        {
        	//TODO: come back to this
            //bounds.addAll(BoundGenerator.getInstance().instantiateVertical(pt, other));
        }
        else if (type == Bound.BoundT.HORIZONTAL_LINE)
        {
        	//TODO: come back to this
        	//bounds.add(BoundGenerator.getInstance().instantiateHorizontal(pt, other));
        }
        else
        {
        	//TODO: come back to this
        	// This does not re-generate functions....needs to check the (other) Top/Bottom bound
        	////bounds.addAll(BoundGenerator.getInstance().generateAll(type, pt));
        }
        
        return bounds ;
    }

	/**
	 * @param pt -- a left bound to attach the new function to (on the left)
	 * @param other -- the top (or bottom
	 * @param type -- The type of bound we are attempting to append
	 * @return set of allowable functions that start at (pt) and do not intersect the (other) bound at all
	 */
	//private Vector<Bound> instantiateBound(Point pt, TopBottom other, Bound.BoundT type)
	//{
	//    Vector<BoundedFunction>
	//	// HERE:
	//	//  For each version of the function (reflected, stretched, etc.)
	//	//     Morph function so that it does not intersect the (other) set of bounds (within a reasonable tolerance)
	//	//
	//}

	//
	//
	//
	//
	//	// For this template, decide whether _top or _bottom is 'first' or 'last'
	//	boolean _bottom_FIRST = true; 
	//
	//	// if ()
	//
	//
	//	// Call helper with 'first' and 'last'
	//	Vector<Region> threeSided = constructTopBottom(theTemplate, first, last)
	//
	//			// 'Close' the regions with Lagrange polynomials
	//			Vector<Region> closed = new Vector<Region>();
	//	for (Region region : threeSided)
	//	{
	//		closed.addAll(region.close());
	//	}
	//}
//
//	// Given, the choice of _top or _bottom, pass a support function:
//	//        1) the _top / _bottom bound and
//	//        2) the associated function type to generate
//	//             (if no function type specified, loop to pass all types and generate all)
//	//
//	// Call recursively to perform the same set of operations.
//	//
//	// Close each region
//	//
//	@Override
//	public Vector<Region> instantiate(Region workingRegion, RegionTemplate theTemplate)
//	{
//		// For this template, decide whether _top or _bottom is 'first' or 'last'
//		boolean _bottom_FIRST = true; 
//
//		// if ()
//
//
//		// Call helper with 'first' and 'last'
//		Vector<Region> threeSided = constructTopBottom(theTemplate, first, last)
//
//				// 'Close' the regions with Lagrange polynomials
//				Vector<Region> closed = new Vector<Region>();
//		for (Region region : threeSided)
//		{
//			closed.addAll(region.close());
//		}
//	}
//
//	private Vector<Region> constructTopBottom(RegionTemplate theTemplate, boolean _bottom_FIRST)
//	{
//		Vector<Region> regions = new Vector<Region>();
//
//
//
//
//
//		return regions;
//	}

	//
	// We construct the top / bottom in parallel:
	//    That is,
	//      while (true)
	//      {
	//          if (bottom functions can be added)
	//          {
	//              (1) construct a single top function for each (resulting in a set of regions with a single top / bottom)
	//          }
	//          if (more than one top function remains)
	//          {
	//              (2) construct a single bottom function (actually a set of regions with a single bottom function)
	//          }
	//      }
	//
	//
	//      @return regions that can be sealed 
	//
	//
	public Vector<Region> constructTopBottom(RegionTemplate theTemplate, Region parentRegion)
	{
        throw new RuntimeException( "TopBottomInstantiator.constructTopBottom has unresolved build problems!" ) ;

/*
        // The set of regions, we continue to build
		Vector<Region> regions = new Vector<>();

		// Default construction begins with the parent region
		regions.add(parentRegion);


		boolean topSingleFunctionRemaining = false;
		boolean bottomComplete = false;
		while (!topSingleFunctionRemaining || !bottomComplete)
		{
			// The next phase of regions
			Vector<Region> processed = new Vector<>();

			//
			// For each working region, add on a bottom function
			//
			if (region.bottomLength() < theTemplate.bottomLength())
			{
				for (Region region : regions)
				{
					processed.addAll(generateSingleBottom(theTemplate, region));
				}
			}
			else
			{
				processed = 
			}

			//
			// For each working region, add on a top function
			//
			for (Region region : regions)
			{
				if (region.bottomLength() < theTemplate.bottomLength())
				{
					processed.addAll(generateSingleBottom(theTemplate, region));
				}
			}   




		}

		//
		// Check to see if we're done with bottom and / or top
		//
		bottomComplete
*/
	}

	//
	// Takes the input set of regions and adds a top function to each.
	// Returns that new set of regions (with new top)
	//
/*
	public Vector<Region> constructTop(RegionTemplate theTemplate, Vector<Region> regions)
	{
        throw new RuntimeException( "TopBottomInstantiator.constructTop has unresolved build problems!" ) ;

        Vector<Region> regionsWithTops = new Vector<Region>();

		for (Region region : regions)
		{
			regionsWithTops.addAll(generateSingleTop(theTemplate, region));
		}

		return regionsWithTops;
	}
*/

	//
	// Given a new region, add a top function.
	// That is, for each region, add all possible top functions increasing the number of regions exponentially.
	//
/*
    private void generateSingleBottom(RegionTemplate theTemplate, Vector<Region> regions, Region workingRegion)
    {
        int currentBoundsInBottom = workingRegion.bottomLength();
    
    	if (currentBoundsInBottom < 0) currentBoundsInBottom = 0;
    
    	if (theTemplate.bottom.templateBounds.get(currentBoundsInBottom) != Bound.BoundT.VERTICAL_LINE)
    	{
    
    		// If the template doesn't specify a function
    		if (theTemplate.bottom.templateBounds.get(currentBoundsInBottom) == Bound.BoundT.FUNCTION)
    		{
    			for (int i = 0; i < (Constants.LIMITED_FUNCTIONS ? Constants.ALLOWED_FUNCTIONS.length : FunctionT.values().length); i++)
    			{
    				Region temp = workingRegion.clone();
    				tempWorkingRegions.add(temp);
    			}
    
    			for (int i = 0; i < tempWorkingRegions.size(); i++) {
    				Region tempWorkingRegion = tempWorkingRegions.get(i);
    
    				FunctionT function;
    				if (Constants.LIMITED_FUNCTIONS) {
    					function = Constants.ALLOWED_FUNCTIONS[i];
    				} else {
    					function = FunctionT.values()[i];
    				}
    				BoundedFunction bottom = generateFunction(true, tempWorkingRegion, function);
    				tempWorkingRegion.bottom.addBound(bottom);
    			}
    		}
    		else
    		{
    			System.out.println("Generating a specific function!" );
    			//If the template has a specific function
    			if (tempWorkingRegions.isEmpty()) tempWorkingRegions.add(workingRegion.clone());
    			Region tempWorkingRegion = tempWorkingRegions.lastElement();
    
    			Bound.BoundT boundT = theTemplate.bottom.templateBounds.get(currentBoundsInBottom);
    
    			BoundedFunction bottom = generateFunction(true, tempWorkingRegion, boundT.inFunctionTForm());
    			tempWorkingRegion.bottom.addBound(bottom);
    		}
    	}
    	else
    	{
    		//Generate vertical line
    	}
    }
*/

}





//    //
//    // If the template requires another bottom, add it.
//    //
//    if (workingRegion.bottomLength() < theTemplate.bottomLength())
//    {
//        generateSingleBottom(theTemplate, regions, workingRegion);
//    }
//
//    Vector<Region> topTempWorkingRegions = new Vector<>();
//
//    //
//    // If the template requires another top, add it and make sure it doesn't intersect.
//    //
//    if (workingRegion.topLength() < theTemplate.topLength())
//    {
//        generateSingleTop(theTemplate, regions, workingRegion);
//    }
//
//    if (topTempWorkingRegions.size() == 0) topTempWorkingRegions = tempWorkingRegions;
//
//    //
//    // Work to close each region.
//    //
//    for (Region region : )
//
//        for (int i = 0; i < topTempWorkingRegions.size(); i++)
//        {
//        }
//
//    //Remove all of those that are not similar to the template, ie, if not enough functions were generated.  
//    /*for (Region region : regions) {
//            if (region.top.bounds.size() != theTemplate.top.templateBounds.size() || 
//                region.bottom.bounds.size() != theTemplate.bottom.templateBounds.size()) {
//                regions.remove(region);
//            }
//        }*/
//    return regions;
//}


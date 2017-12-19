package solver.area.solver;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import representation.bounds.functions.Domain;
import representation.bounds.functions.StringBasedFunction;
import representation.regions.Region;
import solver.area.TextbookAreaProblem;
import utilities.Assertions;

/**
 * Given all regions acquired through 
 *
 */
public class ProblemRegionIdentifier
{
    // The problem functions
    protected StringBasedFunction[] _functions;

    // The problem domain (if it has been specified)    
    protected Domain _domain;
    protected boolean domainSpecified() { return _domain != null; }

    public ProblemRegionIdentifier(TextbookAreaProblem tap) { this(tap.getFunctions(), tap.getDomain()); }
    public ProblemRegionIdentifier(StringBasedFunction[] functions) { this(functions, null); }
    public ProblemRegionIdentifier(StringBasedFunction[] functions, Domain domain)
    {
       _functions = functions;
       _domain = domain;
    }

    protected Set<Region> getProblemRegions(Vector<Region> inRegions)
    {
        //
        // If the domain has not been dictacted, we seek all uniquely defined regions as solution regions
        //
        if (!domainSpecified()) return computeUniqueProblemRegions(inRegions);

        // Filter the regions based on the domain of the problem;
        // seek only regions that are completely within the domain.
        Vector<Region> inDomain = filterForDomain(inRegions);
        
        //
        // Otherwise, if the domain is specified the solution may consist of regions that do not uniquely define the region
        // Imagine {x^2 - 4 and y = 0 with domain [-1, 1]}: one clear region is defined for this problem
        // However, with { y = x ; y = -x with domain [-1, 1]} has two regions where the domain creates left and right bounds for distinct regions
        //
        return handleVerticals(inDomain);
    }

    /**
     * @param inRegions -- a set of regions this method will parse for uniquely defined regions
     * @return the subset of regions that are uniquely defined by the problem functions; uniqueness
     *         refers to top / bottom only (not left / right point or verticals)
     */
    protected Set<Region> computeUniqueProblemRegions(Vector<Region> inRegions)
    {
        Set<Region> applicable = new HashSet<Region>();

        for (Region region : inRegions)
        {
            if (region.uniquelyDefinedBy(_functions))
            {
                applicable.add(region);
            }
        }

        return applicable;
    }
    
    /**
     * Imagine {x^2 - 4 and y = 0 with domain [-1, 1]}: one clear region is defined for this problem
     * However, with { y = x ; y = -x with domain [-1, 1]} has two regions where the domain creates left and right bounds for distinct regions
     * @param inRegions
     * @return
     */
    protected Set<Region> handleVerticals(Vector<Region> inRegions)
    {
        Set<Region> solutionRegions = new HashSet<Region>();
       
        // Seek a single, uniquely defined region: functions top / bottom AND left / right verticality
        Region unique = computeTotalUniqueRegion(inRegions);
        if (unique != null)
        {
            solutionRegions.add(unique);
            return solutionRegions;
        }

        //
        // Otherwise, seek a combination of regions (minimally (1) and (2) below:
        //    (1) A region with a left vertical
        //    (2) A region with a right vertical
        //    (3) Intermediary 
        //
        // Otherwise, seek a left vertical bound region.
        Region leftRegion = computeUniqueRegion(inRegions, true);

        // Otherwise, seek a right vertical bound region.
        Region rightRegion = computeUniqueRegion(inRegions, false);
        
        //In addition to checking that they are equal, we also check that at least one of them is not null (chose left arbitrarily).
        //This is because there could be neither a left vertical nor a right vertical bound, but rather two point bounds.
        //Though possible, this is unlikely to happen because the only reason we'd get here is if the function was given an explicit
        // set of x-bounds but the top and bottom bounding functions create an implied domain entirely inside that explicit specification.
        if(  	( leftRegion != null )
        	&&	( leftRegion == rightRegion ) )
        	System.err.println("Left and right vertical region deduced the same...shouldn't happen since computed above");
        
        // It's possible to have a single region with a left or right vertical: { x ; 0  domain : [0, 1] }
        if (leftRegion != null) solutionRegions.add(leftRegion);
        if (rightRegion != null) solutionRegions.add(rightRegion);

        //
        // Determine if there are any intermediary regions between the left and right bounds we need to include.
        // Think of the problem given by {-x^2 + 4 ; x^2 - 4 domain [-3, 3]}:
        //           this has three regions: two bookends and a center region
        //
        solutionRegions.addAll(computeUniqueProblemRegions(inRegions));
        
        return solutionRegions;
    }
    

    /**
     * Seek a single, uniquely defined region: functions top / bottom AND left / right verticality
     * @return the unique region
     * This code is redundant to the left / right uniqueness checker, but right now making the region-finding algorithm work is more important.
     */
    protected Region computeTotalUniqueRegion(Vector<Region> inRegions)
    {
        // For safety, we will collect all unique regions even though there should be 1
        Vector<Region> unique = new Vector<Region>();

        Set<Region> regionsWithVerticals = regionsWithVerticals(inRegions);
        for (Region region : regionsWithVerticals)
        {
            // Want a singular region that has verticals
            if (region.getLeft().isVertical() && region.getRight().isVertical())
            {
                // Does the remaining top / bottom correspond to our problem?
                if (region.uniquelyDefinedBy(_functions)) unique.add(region);
            }
        }
          
        if (unique.isEmpty()) return null;

        if (unique.size() > 1) System.err.println("There is more than a single unique region");
        Assertions.Assert(unique.size(), 1);
        
        return unique.get(0);
    }
    
    /**
     * Seek a single, uniquely defined region: functions top / bottom AND left / right verticality
     * @return
     */
    protected Region computeUniqueRegion(Vector<Region> inRegions, boolean leftVertical)
    {
        // For safety, we will collect all unique regions even though there should be 1
        Vector<Region> unique = new Vector<Region>();

        Set<Region> regionsWithVerticals = regionsWithVerticals(inRegions, leftVertical);
        for (Region region : regionsWithVerticals)
        {
            // Does the remaining top / bottom correspond to our problem?
            if (region.uniquelyDefinedBy(_functions))
            {
                // Does left or right vertical apply?
                if (leftVertical && region.getLeft().isVertical())
                {
                    unique.add(region);
                }
                else if (!leftVertical && region.getRight().isVertical())
                {
                    unique.add(region);
                }
            }
        }

        if (unique.isEmpty()) return null;

        if (unique.size() > 1) System.err.println("There is more than a single unique region (left / right)");
        Assertions.Assert(unique.size(), 1);
        
        return unique.get(0);
    }
    
    /**
     * @param inRegions -- a set of regions this method will parse for uniquely defined regions
     * @return the subset of regions that are uniquely defined by the problem functions; uniqueness
     *         refers to top / bottom only (not left / right point or verticals)
     */
    protected Set<Region> regionsWithVerticals(Vector<Region> inRegions)
    {
        Set<Region> applicable = regionsWithVerticals(inRegions, true); // Regions with left verticals
        applicable.addAll(regionsWithVerticals(inRegions, false));         // Regions with right verticals
        
        return applicable;
    }
    
    /**
     * @param inRegions -- a set of regions this method will parse for uniquely defined regions
     * @return the subset of regions that are uniquely defined by the problem functions; uniqueness
     *         refers to top / bottom only (not left / right point or verticals)
     */
    protected Set<Region> regionsWithVerticals(Vector<Region> inRegions, boolean leftVertical)
    {
        Set<Region> applicable = new HashSet<Region>();

        for (Region region : inRegions)
        {
            if (leftVertical && region.getLeft().isVertical())
            {
                applicable.add(region);
            }
            else if (!leftVertical && region.getRight().isVertical())
            {
                applicable.add(region);
            }
        }

        return applicable;
    }
    
    /**
     * @param inRegions -- a set of regions this method will parse for uniquely defined regions
     * @return the subset of regions that are uniquely defined by the problem functions; uniqueness
     *         refers to top / bottom only (not left / right point or verticals)
     */
    protected Vector<Region> filterForDomain(Vector<Region> inRegions)
    {
        Vector<Region> inDomain = new Vector<Region>();

        for (Region region : inRegions)
        {
            if (region.inDomain(_domain)) inDomain.add(region);
        }

        return inDomain;
    }
}

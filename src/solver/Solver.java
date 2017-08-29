package solver;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import exceptions.SolvingException;
import representation.regions.Region;
import solver.area.AreaSolution;

public abstract class Solver
{
   public abstract Solution solve(Region region);
   
   /**
    * @param regions -- a set of solvable regions
    * @return the solution of all regions appended together by X or Y (since this is inheritable)
    */
   public Solution solve(Set<Region> regions) throws SolvingException
   {
       AreaSolution overallSolution = new AreaSolution();

       for (Region region : regions)
       {
           Solution regionSolution = this.solve(region);
           
           if (regionSolution == null) throw new SolvingException("Solving region " + region + " failed.");

           overallSolution.add(regionSolution.getIntegralExpressions());
       }
       
       return overallSolution;
   }
   
   /**
    * @param regions -- a set of solvable regions
    * @return the solution of all regions appended together by X or Y (since this is inheritable)
    */
   public Solution solve(Vector<Region> regions) throws SolvingException
   {
       Set<Region> set = new HashSet<Region>(regions);
       
       return solve(set);
   }
}

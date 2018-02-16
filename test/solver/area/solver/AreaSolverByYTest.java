package solver.area.solver;

import java.util.Set;
import java.util.Vector;
import org.junit.Test;

import exceptions.DomainException;
import exceptions.SolvingException;
import representation.regions.Region;
import solver.area.AreaSolution;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import solver.area.regionComputer.RegionExtractor;
import solver.area.solver.AreaSolverByY;
import solver.problemRegions.ProblemRegionIdentifier;
import utilities.Assertions;
import utilities.StringUtilities;

public class AreaSolverByYTest
{
    @Test
    public void test() throws DomainException
    {
        String testName = "Region Creation w.r.t. Y (for solving with Y)";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runProblem( 1, "{ 2-.1*x+.75*Sin[x] ; 1.2-.5*x+.8*Cos[1.5*x] } [0, 7] <1000> //" ) ;
        
        //testSingleBottom(1);
        //testDualBottoms(1);
        //testTriBottoms(1);
        //stillArentWorking(1) ;

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testSingleBottom(int indent) throws DomainException
    {
        String testName = "Inverted Region Single-Bound Bottoms";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runProblem(1, "{ x^2 ; x^3 } <0.083333> //");
        runProblem(1, "{ -x^2 ; -x^3 } <0.083333> //");
        runProblem(1, "{ 2 ; 0 } [0, 4] <8.0> //");
        //runProblem(1, "{ -x^2 + 4 ; x^2 - 4 } <21.33333> //");
        runProblem(1, "{ -x + 2 ; x } [0, 1] <1.0> //");

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testDualBottoms(int indent) throws DomainException
    {
        String testName = "Inverted Region Single-Bound Bottoms";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runProblem(1, "{ x + 2 ; -x + 2 } [0, 1] <1.0> //");
       
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void testTriBottoms(int indent) throws DomainException
    {
        String testName = "Inverted Region Single-Bound Bottoms";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runProblem(1, "{ x + 2 ; -x + 2 } [1, 2] <3.0> //");
        //runProblem(1, "{ -x^2 + 4 ; x^2 - 4 } <21.33333> //");
        //runProblem(1, "{ -x^2 + 4 ; x^2 - 4 } [-1, 1] <14.66666> //");
        runProblem(1, "{ x^2 + 2 ; -x } [0, 1]  <2.833333>    // LH, Page 379 Example 1");
        runProblem(1, "{ 2 - x^2 ; x } [-2, 1]  <4.5>         // LH, Page 379 Example 2");
        runProblem(1, "{ x^2 - 4x; 0 }                           <10.66666>      // LH, Page 383 #7; Implied Domain");
        runProblem(1, "{ 3 - 2x - x^2; 0 }                       <10.66666>      // LH, Page 383 #8; Implied Domain");
        runProblem(1, "{ x^2 + 2x + 1; 3x + 3 }                  <4.5>           // LH, Page 383 #9; Implied Domain");
        runProblem(1, "{-x^2 + 4x + 2; x + 2 }                   <4.5>           // LH, Page 383 #10; Implied Domain");
        runProblem(1, "{ x ; 2 - x ; 0}                          <1>            // LH, Page 383 #11; Implied Domain with 3 functions");

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    //All of these came from testTriBottoms
    private void stillArentWorking(int indent) throws DomainException
    {
        String testName = "Ones that still aren't working";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        //These five seem to work but cause warnings to be printed
		//runProblem(1, "{ x^2 - 6x ; 0 } [0, 6]                   <36>            // LH, Page 383 #1");// Left and right vertical region deduced the same...shouldn't happen since computed above
		//runProblem(1, "{ x^2 + 2x + 1 ; 2x + 5 } [-2, 2]         <10.66666>      // LH, Page 383 #2");// Left and right vertical region deduced the same...shouldn't happen since computed above
		//runProblem(1, "{ x^2 - 4x + 3 ; -x^2 + 2x + 3 } [0, 3]   <9>             // LH, Page 383 #3");// Left and right vertical region deduced the same...shouldn't happen since computed above
		//runProblem(1, "{ x^2 ; x^3 } [0, 1]                      <0.083333>      // LH, Page 383 #4");// Left and right vertical region deduced the same...shouldn't happen since computed above
        //Additionally, this one takes quite awhile to integrate each region.
        //runProblem(1, "{ 3 (x^3 - x); 0 } [-1, 1]                <1.5>           // LH, Page 383 #5 ; Regions: 2"); // Left and right vertical region deduced the same...shouldn't happen since computed above

        //These two halt without printing out a stack trace after a call to Mathematica
        runProblem(1, "{ Surd[x, 3] ; x }  <0.5> //");
        //runProblem(1, "{ Surd[x, 3] ; x } [0, 1] <0.25> //"); // Left and right vertical region deduced the same...shouldn't happen since computed above
        //runProblem(1, "{ (x - 1)^3 ; x - 1 } [0, 2]              <0.5>           // LH, Page 383 #6 ; Regions: 2");  // Left and right vertical region deduced the same...shouldn't happen since computed above

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void runProblem(int indent, String pStr) throws DomainException
    {
        System.out.println(StringUtilities.generateTestStartString(pStr, indent));

        // Create Problem
        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);

        // Extract the appropriate solution regions
        RegionExtractor extractor = new RegionExtractor(problem);

        Vector<Region> regions = extractor.getRegions();

        // Identify the exact region(s) to solve
        ProblemRegionIdentifier identifier = new ProblemRegionIdentifier(problem);
        
        Set<Region> solutionRegions = identifier.getProblemRegions(regions);

        AreaSolverByY solver = new AreaSolverByY();
        AreaSolution solution = null;
        try
        {
            solution = (AreaSolution)solver.solve(solutionRegions);
        }
        catch (SolvingException e)
        {
            System.err.println("Solving by Y failed via exception.");
            e.printStackTrace();
        }
        
        System.out.println("Solution: " + solution);
        
        // Check answer
        Assertions.Assert(solution.evaluate(), problem.getAnswer());

        System.out.println(StringUtilities.generateTestEndString(pStr, indent));
    }
}

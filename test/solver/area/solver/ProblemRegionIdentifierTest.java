package solver.area.solver;

import java.util.Set;
import java.util.Vector;

import org.junit.Test;

import representation.regions.Region;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import solver.area.regionComputer.RegionExtractor;
import solver.problemRegions.ProblemRegionIdentifier;
import utilities.Assertions;
import utilities.StringUtilities;

public class ProblemRegionIdentifierTest
{
    @Test
    public void test()
    {
        String testName = "Solver Interface Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testSingleRegions(1);
        testSingleRegionsWithVerticals(1);
        testMultipleRegions(1);
        testMultipleRegionsWithVerticals(1);
        testSequenceRegions(1); // sin x with y = 0 over [0, 10PI]
//        testSequenceRegionsWithVerticals(1);// sin x with y = 0 over [0, 9.5 PI]
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testSingleRegions(int indent)
    {
        String testName = "Single Regions (no verticals)";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        runRegionVerification(indent + 1, "{ x^2 - 4x; 0 }                       // LH, Page 383 #7; Implied Domain", 1);
        runRegionVerification(indent + 1, "{ 3 - 2x - x^2; 0 }                   // LH, Page 383 #8; Implied Domain", 1);
        runRegionVerification(indent + 1, "{ x^2 + 2x + 1; 3x + 3 }              // LH, Page 383 #9; Implied Domain", 1);
        runRegionVerification(indent + 1, "{ x ; 2 - x ; 0}                      // LH, Page 383 #11; Implied Domain with 3 functions", 1);
        runRegionVerification(indent + 1, "{ -x^2 + 4 ; x^2 - 4 } //", 1);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testSingleRegionsWithVerticals(int indent)
    {
        String testName = "Single Regions With Verticals";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        runRegionVerification(indent + 1, "{ x ; 0 } [0,1]          // Right Triangle to the right", 1);
        runRegionVerification(indent + 1, "{ -x ; 0 } [-1,0]          // Right Triangle to the left", 1);
        runRegionVerification(indent + 1, "{ -x^2 + 4 ; 0 } [-1, 0] // Region with two verticals left and right", 1);
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void testMultipleRegions(int indent)
    {
        String testName = "Multiple Regions (NO Verticals)";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        runRegionVerification(indent + 1, "{ Surd[x, 3] ; x }                    // LH, Page 383 #16; Implied Domain; 3 intersection points", 2);
        runRegionVerification(indent + 1, "{ x^3 ; x }                    // 3 intersection points", 2);
        runRegionVerification(indent + 1, "{ x(x - 2)(x + 2) ; 0 }           // 3 intersection points", 2);
        runRegionVerification(indent + 1, "{ x(x - 1)(x + 1)(x - 2)(x + 2) ; 0 }           // 3 intersection points", 4);
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void testMultipleRegionsWithVerticals(int indent)
    {
        String testName = "Multiple Regions (with Verticals)";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        runRegionVerification(indent + 1, "{ -x^2 + 4 ; x^2 - 4 } [-3,3] //", 3);
        runRegionVerification(indent + 1, "{ x^3 ; 0 } [-3,3] //", 2);
        runRegionVerification(indent + 1, "{ x(x - 2)(x + 2) ; 0 } [-5,5]           // 3 intersection points", 4);
        runRegionVerification(indent + 1, "{ x(x - 1)(x + 1)(x - 2)(x + 2) ; 0 }  [-3,3]   // 3 intersection points", 6);
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void testSequenceRegions(int indent)
    {
        String testName = "Sequence Regions (NO Verticals)";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        // THIS is a MAJOR problem...infinite cycling in Facet Identification
//      runRegionVerification(indent + 1, "{ Sin [x] ; 0 }  [0, Pi]                 // LH, Page 383 #16; Implied Domain; 3 intersection points", 2);
//      HERE: Handling Trigonometric functions 
//      Limit domain always not just the default.domain How to do that?
        
        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    private void runRegionVerification(int indent, String pStr, int numSolutionRegions)
    {
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // (1) Parse and acquire the problem from the String
        // (2) Build Planar graph
        // (3) Extract regions from the graph
        // (4) Prune the regions to solution regions
        //
        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);
        
        RegionExtractor extractor = new RegionExtractor(problem);

        Vector<Region> regions = extractor.getRegions();
        
        ProblemRegionIdentifier identifier = new ProblemRegionIdentifier(problem);
        
        Set<Region> solutionRegions = identifier.getProblemRegions(regions);
        
        System.out.println("Solution regions: ");
        for (Region region : solutionRegions)
        {
            System.out.println(region);
        }

        Assertions.Assert(solutionRegions.size(), numSolutionRegions);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
}

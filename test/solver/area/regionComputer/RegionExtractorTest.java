package solver.area.regionComputer;

import java.util.Vector;
import org.junit.Test;
import representation.regions.Region;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import utilities.Assertions;
import utilities.StringUtilities;

public class RegionExtractorTest
{
    @Test
    public void test()
    {
        String testName = "Region Extractor Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testNumberOfRegions(1);
        testRegionDetails(1);
        testMultiRegionDetails(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void testNumberOfRegions(int indent)
    {
        String testName = "Number of Regions (Extractor Tests)";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testZeroRegions(1);
        testSingleRegion(1);
        testMultipleRegion(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testZeroRegions(int indent)
    {
        String testName = "Zero Regions Extraction Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runRegionVerification(indent + 1, "{ x ; x - 1 ; 0}       // Parallel", 0);
        runRegionVerification(indent + 1, "{ x }            [0,1] // Parallel", 0);
        runRegionVerification(indent + 1, "{ x ; x - 1 ; 0}       // Parallel", 0);


        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testSingleRegion(int indent)
    {
        String testName = "Single Region Extraction Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runRegionVerification(indent + 1, "{ x^2 - 4x; 0 }                // LH, Page 383 #7; Implied Domain", 1);
        runRegionVerification(indent + 1, "{ 3 - 2x - x^2; 0 }            // LH, Page 383 #8; Implied Domain", 1);
        runRegionVerification(indent + 1, "{ x^2 + 2x + 1; 3x + 3 }       // LH, Page 383 #9; Implied Domain", 1);
        runRegionVerification(indent + 1, "{ x ; 2 - x ; 0}               // LH, Page 383 #11; Implied Domain with 3 functions", 1);
        runRegionVerification(indent + 1, "{ x ; 0 }                [0,1] // Parallel", 1);
        runRegionVerification(indent + 1, "{ -x^2 + 4 ; x^2 - 4 }         //", 1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testMultipleRegion(int indent)
    {
        String testName = "Multiple Region Extraction Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runRegionVerification(indent + 1, "{ Surd[x, 3] ; x }                                    // LH, Page 383 #16; Implied Domain; 3 intersection points", 2);
        runRegionVerification(indent + 1, "{ x ; 1/2 x - 4 ; 2 - x ; 0}                          // Made up: 3 regions", 3);
        runRegionVerification(indent + 1, "{ x ; x - 2 ; 0 ; 2 }                         [0, 1]  // ", 3);
        runRegionVerification(indent + 1, "{ x ; x - 1 ; x - 2 ; x - 3 ; 0 ; 1 ; 2 ; 3 }         // Larger Parallel; 9 regions", 9);
        runRegionVerification(indent + 1, "{ -x^2 + 4 ; x^2 - 4 }                        [-1, 1] //", 1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runRegionVerification(int indent, String pStr, int numFacets)
    {
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // (1) Parse and acquire the problem from the String
        // (2) Build Planar graph
        // (3) Analyze some characteristics of Planar Graph
        //
        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);

        RegionExtractor extractor = new TextbookProblemRegionExtractor(problem);

        //   Graph, No. nodes, No. Edges
        Vector<Region> regions = extractor.getRegions();
        
        regions.forEach((region)-> region.verify());

        Assertions.Assert(extractor.getFacets().size(), numFacets);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void testRegionDetails(int indent)
    {
        String testName = "Exact Region Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testSingleRegionDetails(indent + 1);
        testSingleVerticalRegionDetails(indent + 1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testSingleRegionDetails(int indent)
    {
        String testName = "Exact Single (Point bounds) Region Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        runRegionDetailVerification(indent + 1, "{ x^2 - 4x; 0 }          // LH, Page 383 #7; Implied Domain", 1, true, 1, true);
        runRegionDetailVerification(indent + 1, "{ 3 - 2x - x^2; 0 }      // LH, Page 383 #8; Implied Domain", 1, true, 1, true);
        runRegionDetailVerification(indent + 1, "{ x^2 + 2x + 1; 3x + 3 } // LH, Page 383 #9; Implied Domain", 1, true, 1, true);
        runRegionDetailVerification(indent + 1, "{ x ; 2 - x ; 0}         // LH, Page 383 #11; Implied Domain with 3 functions", 2, true, 1, true);  
        runRegionDetailVerification(indent + 1, "{ -x^2 + 4 ; x^2 - 4 }   //", 1, true, 1, true);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testSingleVerticalRegionDetails(int indent)
    {
        String testName = "Exact Single (Vertical bounds) Region Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        runRegionDetailVerification(indent + 1, "{ x ; 0 } [0,1]   // Parallel", 1, true, 1, false);
        runRegionDetailVerification(indent + 1, "{ 0; 1 }  [-1, 1] //", 1, false, 1, false);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void runRegionDetailVerification(int indent, String sProblem, double numTop, boolean leftIsPoint, double numBottom, boolean rightIsPoint)
    {
        System.out.println(StringUtilities.generateTestStartString(sProblem, indent));

        //
        // (1) Parse and acquire the problem from the String
        // (2) Build Planar graph
        // (3) Analyze some characteristics of Planar Graph
        //
        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(sProblem);

        RegionExtractor extractor = new TextbookProblemRegionExtractor(problem);

        Vector<Region> regions = extractor.getRegions();

        System.out.println("The region: \n" + regions.get(0));

        // Check single region
        Assertions.Assert(regions.size(), 1);

        verifyRegion(regions.get(0), numTop, leftIsPoint, numBottom, rightIsPoint);

        System.out.println(StringUtilities.generateTestEndString(sProblem, indent));
    }

    private void verifyRegion(Region region, double numTop, boolean leftIsPoint, double numBottom, boolean rightIsPoint)
    {
        Assertions.Assert(region.getTop().numberOfBounds(), numTop);
        Assertions.Assert(region.getRight().isPoint(), rightIsPoint);
        Assertions.Assert(region.getBottom().numberOfBounds(), numBottom);
        Assertions.Assert(region.getLeft().isPoint(), leftIsPoint);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void testMultiRegionDetails(int indent)
    {
        String testName = "Multi Region Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runMultiVerification(indent + 1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runMultiVerification(int indent)
    {
        //HERE: Problem with top / bottom being switched in BOTH regions
        String sProblem = "{ Surd[x, 3] ; x }                    // LH, Page 383 #16; Implied Domain; 3 intersection points; Regions: 2";

        System.out.println(StringUtilities.generateTestStartString(sProblem, indent));

        //
        // (1) Parse and acquire the problem from the String
        // (2) Build Planar graph
        // (3) Analyze some characteristics of Planar Graph
        //
        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(sProblem);

        RegionExtractor extractor = new TextbookProblemRegionExtractor(problem);

        Vector<Region> regions = extractor.getRegions();

        // Check single region
        Assertions.Assert(regions.size(), 2);
        
        for (Region region : regions)
        {
            System.out.println("The region: \n" + region);
        }

        verifyRegion(regions.get(0), 1, true, 1, true);
        verifyRegion(regions.get(1), 1, true, 1, true);
        
        System.out.println(StringUtilities.generateTestEndString(sProblem, indent));
    }

}

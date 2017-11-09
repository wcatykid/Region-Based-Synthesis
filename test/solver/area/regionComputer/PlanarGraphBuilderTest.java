package solver.area.regionComputer;

import org.junit.Test;

import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import solver.area.regionComputer.graphBuilder.PlanarGraphBuilder;
import solver.area.regionComputer.undirectedPlanarGraph.NodePointT;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarEdgeAnnotation;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraph;
import utilities.Assertions;
import utilities.StringUtilities;

public class PlanarGraphBuilderTest
{
    @Test
    public void test()
    {
        String testName = "Planar Graph Area Builder Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testNonVerticalRegions(1);
        testVerticalRegions(1);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void testNonVerticalRegions(int indent)
    {
        String testName = "Non-vertical Region-Based Planar Graphs";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runGraphVerification(indent + 1, "{ x^2 - 4x; 0 }              // LH, Page 383 #7; Implied Domain", 4, 4);
        runGraphVerification(indent + 1, "{ 3 - 2x - x^2; 0 }          // LH, Page 383 #8; Implied Domain", 4, 4);
        runGraphVerification(indent + 1, "{ x^2 + 2x + 1; 3x + 3 }     // LH, Page 383 #9; Implied Domain", 4, 4);
        runGraphVerification(indent + 1, "{ Surd[x, 3] ; x }           // LH, Page 383 #16; Implied Domain; 3 intersection points; Regions: 2", 7, 8);
        runGraphVerification(indent + 1, "{ x ; 2 - x ; 0}             // LH, Page 383 #11; Implied Domain with 3 functions", 6, 6);
        runGraphVerification(indent + 1, "{ x ; 1/2 x - 4 ; 2 - x ; 0} // Made up: 3 regions", 14, 16);
        runGraphVerification(indent + 1, "{ x ; x - 1 ; 0}             // Parallel", 3, 2);
        //runGraphVerification(indent + 1, "{ x ; x - 1 ; x - 2 ; x - 3 ; 0 ; 1 ; 2 ; 3 }          // Larger Parallel; 9 regions", 40, 48);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void testVerticalRegions(int indent)
    {
        String testName = "Vertical Region-Based Planar Graphs";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runGraphVerification(indent + 1, "{ x^2 - 4x; 0 }       [1,3]  // LH, Page 383 #7; Domain results in verticals", 6, 6);
        runGraphVerification(indent + 1, "{ 3 - 2x - x^2; 0 }   [-2,0] // LH, Page 383 #8; Implied Domain", 6, 6);
        runGraphVerification(indent + 1, "{ x^2 - 4x; 0 }       [0,3]  // LH, Page 383 #7; Domain results in verticals on right only", 5, 5);
        runGraphVerification(indent + 1, "{ x^2 - 4x; 0 }       [0,4]  // LH, Page 383 #7; Domain results in no verticals", 4, 4);
        runGraphVerification(indent + 1, "{ x ; x - 1 ; 0 ; 1 }        // ", 8, 8);
        runGraphVerification(indent + 1, "{ x ; x - 2 ; 0 ; 2 } [0, 1] // ", 11, 13);
        runGraphVerification(indent + 1, "{ x ; 0 }             [0,1]  // Parallel", 5, 5);
        
        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void runGraphVerification(int indent, String pStr, int numNodes, int numEdges)
    {
        String testName = pStr;

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // (1) Parse and acquire the problem from the String
        // (2) Build Planar graph
        // (3) Analyze some characteristics of Planar Graph
        //
        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);

        PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph = buildGraph(problem);
        
        System.out.println(graph);
        
        //   Graph, No. nodes, No. Edges
        verifyGraph(graph, numNodes, numEdges);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
    
    public static PlanarGraph<NodePointT, PlanarEdgeAnnotation> buildGraph(TextbookAreaProblem tap)
    {
        PlanarGraphBuilder builder = new PlanarGraphBuilder(tap);
        
        builder.build();

        return builder.getGraph();
    }
    
    private static void verifyGraph(PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph, int numNodes, int numEdges)
    {
        Assertions.Assert(graph.size(), numNodes);
        
        Assertions.Assert(graph.numEdges(), numEdges);
    }
}

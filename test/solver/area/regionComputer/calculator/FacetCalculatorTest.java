package solver.area.regionComputer.calculator;

import org.junit.Test;
import utilities.StringUtilities;

public class FacetCalculatorTest
{
    @Test
    public void test()
    {
        String testName = "Facet Calculator Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        //upwardTest(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    
//    private void upwardTest(int indent)
//    {
//        String testName = "First point is up-and to right";
//
//        System.out.println(StringUtilities.generateTestStartString(testName, 0));
//
//        String testName = pStr;
//
//        System.out.println(StringUtilities.generateTestStartString(testName, indent));
//
//        //
//        // (1) Parse and acquire the problem from the String
//        // (2) Build Planar graph
//        // (3) Analyze some characteristics of Planar Graph
//        //
//        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);
//
//        PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph = buildGraph(problem);
//        
//        
//        
//
//        System.out.println(StringUtilities.generateTestEndString(testName, 0));
//    }
}

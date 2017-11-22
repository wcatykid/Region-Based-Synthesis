package solver.area.solver;

import org.junit.Test;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import utilities.Assertions;
import utilities.StringUtilities;

public class SolverInterfaceTest
{
    @Test
    public void test()
    {
        String testName = "Solving Area-Based Problems: Solver Interface Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runProblemArea(1, "{ -x^2 + 4 ; x^2 - 4 }                    <21.33333>  //");
        runProblemArea(1, "{ -x^2 + 4 ; x^2 - 4 }           [-1, 1]  <14.66666>  //");
        runProblemArea(1, "{ x^2 + 2 ; -x }                 [0, 1]   <2.833333>  // LH, Page 379 Example 1;");
        runProblemArea(1, "{ 2 - x^2 ; x }                  [-2, 1]  <4.5>       // LH, Page 379 Example 2;");
        runProblemArea(1, "{ x^2 - 6x ; 0 }                 [0, 6]   <36>        // LH, Page 383 #1;");// Left and right vertical region deduced the same...shouldn't happen since computed above
        runProblemArea(1, "{ x^2 + 2x + 1 ; 2x + 5 }        [-2, 2]  <10.66666>  // LH, Page 383 #2;");// Left and right vertical region deduced the same...shouldn't happen since computed above
        runProblemArea(1, "{ x^2 - 4x + 3 ; -x^2 + 2x + 3 } [0, 3]   <9>         // LH, Page 383 #3;");// Left and right vertical region deduced the same...shouldn't happen since computed above
        runProblemArea(1, "{ x^2 ; x^3 }                    [0, 1]   <0.083333>  // LH, Page 383 #4;");// Left and right vertical region deduced the same...shouldn't happen since computed above
        runProblemArea(1, "{ 3 (x^3 - x) ; 0 }              [-1, 1]  <1.5>       // LH, Page 383 #5; Regions: 2;"); // Left and right vertical region deduced the same...shouldn't happen since computed above
        runProblemArea(1, "{ (x - 1)^3 ; x - 1 }            [0, 2]   <0.5>       // LH, Page 383 #6; Regions: 2;");  // Left and right vertical region deduced the same...shouldn't happen since computed above
        runProblemArea(1, "{ x^2 - 4x ; 0 }                          <10.66666>  // LH, Page 383 #7; Implied Domain;");
        runProblemArea(1, "{ 3 - 2x - x^2 ; 0 }                      <10.66666>  // LH, Page 383 #8; Implied Domain;");
        runProblemArea(1, "{ x^2 + 2x + 1 ; 3x + 3 }                 <4.5>       // LH, Page 383 #9; Implied Domain;");
        runProblemArea(1, "{-x^2 + 4x + 2 ; x + 2 }                  <4.5>       // LH, Page 383 #10; Implied Domain;");
        runProblemArea(1, "{ x ; 2 - x ; 0}                          <1>         // LH, Page 383 #11; Implied Domain with 3 functions;");
        runProblemArea(1, "{ 1/(x^2) ; 0 }                  [1,5]    <0.799999>  // LH, Page 383 #12;");
        runProblemArea(1, "{ 3x^2 + 2x ; 8 }                         <18.51858>  // LH, Page 383 #13; Implied Domain;");
        runProblemArea(1, "{ x(x^2 -3x + 3) ; x^2 }                  <3.083333>  // LH, Page 383 #14; Implied Domain;");
        runProblemArea(1, "{ x^3 -2x + 1 ; -2x }            [-1,1]   <2>         // LH, Page 383 #15;");
        runProblemArea(1, "{ Surd[x, 3] ; x }               [0, 1]   <0.25>      // LH, Page 383 #16;"); // Left and right vertical region deduced the same...shouldn't happen since computed above
        runProblemArea(1, "{ Surd[x, 3] ; x }                        <0.5>       // LH, Page 383 #16; Implied Domain; 3 intersection points");
        runProblemArea(1, "{ Surd[3x, 2] + 1 ; x + 1 }               <1.5>       // LH, Page 384 #17; Implied Domain;");
        runProblemArea(1, "{ x^2 + 5x - 6 ; 6x - 6 }                 <0.1666666> // LH, Page 384 #18; Implied Domain;");
        runProblemArea(1, "{ x^2 - 4x + 3 ; 3 + 4x - x^2 }           <21.333333> // LH, Page 384 #19; Implied Domain;");
        runProblemArea(1, "{ x^4 - 2x^2 ; 2x^2 }                     <8.5333333> // LH, Page 384 #20; Implied Domain;");
        runProblemArea(1, "{ x^2 ; x + 2 }                           <4.5>       // LH, Page 384 #21; Implied Domain;");
        runProblemArea(1, "{ x(2-x) ; -x }                           <4.5>       // LH, Page 384 #22; Implied Domain;");
        runProblemArea(1, "{ x^2 + 1 ; 0 }                  [-1,2]   <6>         // LH, Page 384 #23;");
        runProblemArea(1, "{ x/(Surd[16-x^2,2]) ; 0 }       [0,3]    <1.3542486> // LH, Page 384 #24;");
        runProblemArea(1, "{ 1/x ; -x^2+4x-2 }              [1,4]    <3.0671354> // LH, Page 384 #25;");
        runProblemArea(1, "{ 3^x ; 2x+1 }                            <0.1795215> // LH, Page 384 #26; Implied Domain;");
        runProblemArea(1, "{ 1 / ( 1 + x^2 ) ; x^2 / 2 }             <1.2374629> // LH, Page 384 #27; Implied Domain;");
//        runProblemArea(1, "{ 2 ; sec[ x ] }            [-PI/3,PI/3]  <100000000> // LH, Page 384 #28;");
//        runProblemArea(1, "{ 2*sin[x] ; tan[ x ] }       [-1.5,1.5]  <100000000> // LH, Page 384 #29;");
//        runProblemArea(1, "{ sin[2x] ; cos[x] }          [-1.5,1.5]  <100000000> // LH, Page 384 #30;");
//        runProblemArea(1, "{ 2*sin[x] + sin[2*x] ; 0 }      [0,3.5]  <100000000> // LH, Page 384 #31;");
//        runProblemArea(1, "{ 2*sin[x] + cos[2*x] ; 0 }      [0,3.5]  <100000000> // LH, Page 384 #32;");
        runProblemArea(1, "{ x*(E^(-x^2)) ; 0 }             [0,1]    <0.3160602> // LH, Page 384 #33;");
        runProblemArea(1, "{ (1/x^2)*(E^(1/x)) ; 0 }        [1,3]    <1.3226694> // LH, Page 384 #34;");
        runProblemArea(1, "{ (6*x)/(x^2+1) ; 0 }            [0,3]    <6.9077552> // LH, Page 384 #35;");
        runProblemArea(1, "{ 4/x ; 1 ; 4 }                  [0,4]    <5.5451774> // LH, Page 384 #36;");
        runProblemArea(1, "{ 1/(x^2) ; 0 }                  [1, 5]   <0.79999>   // LH, Page 431 #1;");
        runProblemArea(1, "{ 1/(x^2) ; 4 }                  [0.5, 5] <16.2>      // LH, Page 431 #2;");
        runProblemArea(1, "{ 1/(x^2+1) ; 0 }                [-1, 1]  <1.57079>   // LH, Page 431 #3;");
        runProblemArea(1, "{ 1-(x/2) ; x-2 ; 1 }                     <1.5>       // LH, Page 431 #4; Implied Domain;");
        runProblemArea(1, "{ x ; x^3 }                               <0.5>       // LH, Page 431 #7; Implied Domain;");
        runProblemArea(1, "{ x^2-8x+3 ; 3+8x-x^2 }                   <170.66666> // LH, Page 431 #9; Implied Domain;");
        runProblemArea(1, "{ x^2-4x+3 ; x^3 }               [0, 1]   <1.29440>   // LH, Page 431 #10;");
        runProblemArea(1, "{ Sqrt[x-1] ; 2 ; 0 }            [0, 5]   <4.66666>   // LH, Page 431 #11;");
        runProblemArea(1, "{ .5x-.5 ; 2 ; 0 }               [0, 5]   <6>         // Similar to LH, Page 431 #11 (but without a square root and thus imaginary parts)");

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runProblemArea(int indent, String pStr)
    {
        System.out.println(StringUtilities.generateTestStartString(pStr, indent));

        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);

        SolverInterface solver = new SolverInterface();

        double answer = solver.solve(problem);

        Assertions.Assert(answer, problem.getAnswer());

        System.out.println(StringUtilities.generateTestEndString(pStr, indent));
    }
}

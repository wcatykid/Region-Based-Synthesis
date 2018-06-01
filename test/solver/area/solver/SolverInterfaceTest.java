package solver.area.solver;

import org.junit.Test;

import exceptions.DomainException;
import solver.area.TextbookAreaProblem;
import solver.area.parser.AreaProblemParserTest;
import utilities.Assertions;
import utilities.StringUtilities;

public class SolverInterfaceTest
{
    @Test
    public void test() throws DomainException
    {
    	//TODO: This stuff:
    	//***************************************************************************************
    	// Sometimes Mathematica gives back something in scientific notation ...need to fix this
    	//  |               -6
    	//  {0., 4.69282 10  }|
    	//  Result: |[-6.0, 0.0]|
    	//  Expected formatting issue when parsing (non)-double 4.69282 10
    	//***************************************************************************************
    	// Everywhere it says DEBUGGABLE gives us this message ...I expect it's all related
    	//  Region::getBoundInRange: expected to find 2 bounds in range; 1 found.
    	//***************************************************************************************
    	
        String testName = "Solving Area-Based Problems: Solver Interface Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        runProblemArea( 1, true, true, "{ -x^2 + 4 ; x^2 - 4 }                    <21.33333>  //" ) ;
        runProblemArea( 1, true, true, "{ -x^2 + 4 ; x^2 - 4 }           [-1, 1]  <14.66666>  //" ) ;
        runProblemArea( 1, true, true, "{ x^2 + 2 ; -x }                 [0, 1]   <2.833333>  // LH, Page 379 Example 1;" ) ;
        runProblemArea( 1, true, true, "{ 2 - x^2 ; x }                           <4.5>       // LH, Page 379 Example 2; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ 2 - x^2 ; x }                  [-2, 1]  <4.5>       // LH, Page 379 Example 2; Repeated but with explicit domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 - 6x ; 0 }                          <36>        // LH, Page 383 #1; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 + 2x + 1 ; 2x + 5 }                 <10.66666>  // LH, Page 383 #2; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 - 4x + 3 ; -x^2 + 2x + 3 }          <9>         // LH, Page 383 #3; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 ; x^3 }                             <0.083333>  // LH, Page 383 #4; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ 3 (x^3 - x) ; 0 }                       <1.5>       // LH, Page 383 #5; Regions: 2; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ (x - 1)^3 ; x - 1 }                     <0.5>       // LH, Page 383 #6; Regions: 2; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 - 4x ; 0 }                          <10.66666>  // LH, Page 383 #7; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ 3 - 2x - x^2 ; 0 }                      <10.66666>  // LH, Page 383 #8; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 + 2x + 1 ; 3x + 3 }                 <4.5>       // LH, Page 383 #9; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{-x^2 + 4x + 2 ; x + 2 }                  <4.5>       // LH, Page 383 #10; Implied Domain;" ) ;
        runProblemArea( 1, false, true, "{ x ; 2 - x ; 0}                          <1>         // LH, Page 383 #11; Implied Domain with 3 functions;" ) ;
        runProblemArea( 1, true, true, "{ 1/(x^2) ; 0 }                  [1,5]    <0.799999>  // LH, Page 383 #12;" ) ;
        runProblemArea( 1, true, true, "{ 3x^2 + 2x ; 8 }                         <18.51858>  // LH, Page 383 #13; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x(x^2 -3x + 3) ; x^2 }                  <3.083333>  // LH, Page 383 #14; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^3 -2x + 1 ; -2x }            [-1,1]   <2>         // LH, Page 383 #15;" ) ;
        runProblemArea( 1, true, true, "{ Surd[x, 3] ; x }                        <0.5>       // LH, Page 383 #16; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ Surd[x, 3] ; x }                        <0.5>       // LH, Page 383 #16; Implied Domain; 3 intersection points" ) ;
        runProblemArea( 1, true, true, "{ Surd[3x, 2] + 1 ; x + 1 }               <1.5>       // LH, Page 384 #17; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 + 5x - 6 ; 6x - 6 }                 <0.1666666> // LH, Page 384 #18; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 - 4x + 3 ; 3 + 4x - x^2 }           <21.333333> // LH, Page 384 #19; Implied Domain;" ) ;
        
        //DEBUGGABLE
        //runProblemArea( 1, true, true, "{ x^4 - 2x^2 ; 2x^2 }                     <8.5333333> // LH, Page 384 #20; Implied Domain;" ) ;
        
        runProblemArea( 1, true, true, "{ x^2 ; x + 2 }                           <4.5>       // LH, Page 384 #21; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x(2-x) ; -x }                           <4.5>       // LH, Page 384 #22; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 + 1 ; 0 }                  [-1,2]   <6>         // LH, Page 384 #23;" ) ;
        runProblemArea( 1, true, true, "{ x/(Surd[16-x^2,2]) ; 0 }       [0,3]    <1.3542486> // LH, Page 384 #24;" ) ;

        //DEBUGGABLE
        //runProblemArea( 1, true, true, "{ 1/x ; -x^2+4x-2 }              [1,4]    <3.0671354> // LH, Page 384 #25;" ) ;
        
        runProblemArea( 1, true, true, "{ 3^x ; 2x+1 }                            <0.1795215> // LH, Page 384 #26; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ 1 / ( 1 + x^2 ) ; x^2 / 2 }             <1.2374629> // LH, Page 384 #27; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ 2 ; Sec[x] }              [-PI/3,PI/3]  <1.5548744> // LH, Page 384 #28;" ) ;
		
        //DEBUGGABLE
        //runProblemArea( 1, true, true, "{ 2*Sin[x] ; Tan[ x ] }       [-1.5,1.5]  <2.807927>  // LH, Page 384 #29;" ) ;
		
        runProblemArea( 1, true, true, "{ Sin[2x] ; Cos[x] }          [-1.5,1.5]  <2.489992>  // LH, Page 384 #30;" ) ;
        runProblemArea( 1, true, true, "{ 2*Sin[x] + Sin[2*x] ; 0 }      [0,3.5]  <4.004037>  // LH, Page 384 #31;" ) ;
        runProblemArea( 1, true, true, "{ 2*Sin[x] + Cos[2*x] ; 0 }      [0,3.5]  <4.2014066> // LH, Page 384 #32;" ) ;
        runProblemArea( 1, true, true, "{ x*(E^(-x^2)) ; 0 }             [0,1]    <0.3160602> // LH, Page 384 #33;" ) ;
        runProblemArea( 1, true, true, "{ (1/x^2)*(E^(1/x)) ; 0 }        [1,3]    <1.3226694> // LH, Page 384 #34;" ) ;
        runProblemArea( 1, true, true, "{ (6*x)/(x^2+1) ; 0 }            [0,3]    <6.9077552> // LH, Page 384 #35;" ) ;
        runProblemArea( 1, false, true, "{ 4/x ; 1 ; 4 }                  [0,4]    <5.5451774> // LH, Page 384 #36;" ) ;
        runProblemArea( 1, true, true, "{ 1/(x^2) ; 0 }                  [1, 5]   <0.79999>   // LH, Page 431 #1;" ) ;
        runProblemArea( 1, true, true, "{ 1/(x^2) ; 4 }                  [0.5, 5] <16.2>      // LH, Page 431 #2;" ) ;
        runProblemArea( 1, true, true, "{ 1/(x^2+1) ; 0 }                [-1, 1]  <1.57079>   // LH, Page 431 #3;" ) ;
        runProblemArea( 1, false, true, "{ 1-(x/2) ; x-2 ; 1 }                     <1.5>       // LH, Page 431 #4; Implied Domain;" ) ;

        runProblemArea( 1, true, true, "{ x ; x^3 }                               <0.5>       // LH, Page 431 #7; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2-8x+3 ; 3+8x-x^2 }                   <170.66666> // LH, Page 431 #9; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2-4x+3 ; x^3 }               [0, 1]   <1.29440>   // LH, Page 431 #10;" ) ;
        runProblemArea( 1, false, true, "{ Sqrt[x-1] ; 2 ; 0 }            [0, 5]   <4.66666>   // LH, Page 431 #11;" ) ;
        runProblemArea( 1, false, true, "{ .5x-.5 ; 2 ; 0 }               [0, 5]   <6>         // Similar to LH, Page 431 #11 (but without a square root and thus imaginary parts)" ) ;
        runProblemArea( 1, true, true, "{ Sqrt[x-1] ; ( x - 1 ) / 2 }             <1.33333>   // LH, Page 431 #12; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ (1-Sqrt[x])^2 ; 0 }            [0, 1]   <0.16666>   // LH, Page 431 #13;" ) ;

        //DEBUGGABLE
        //runProblemArea( 1, true, true, "{ x^4 - 2*x^2 ; 2*x^2 }                   <8.53333>   // LH, Page 431 #14; Implied Domain;" ) ;
        
        runProblemArea( 1, true, true, "{ E^x ; 7.38905609893 }          [0, 2]   <8.38905>   // LH, Page 431 #15;" ) ;
        runProblemArea( 1, true, true, "{ Csc[x] ; 2 }              [PI/6,5*PI/6] <1.5548744> // LH, Page 431 #16;" ) ;
        runProblemArea( 1, true, true, "{ Sin[x] ; Cos[x] }      [.25*PI,1.25*PI] <2.8284271> // LH, Page 431 #17;" ) ;
        runProblemArea( 1, true, true, "{ Cos[x] ; .5 }             [PI/3,7*PI/3] <4.5112991> // LH, Page 431 #18;" ) ;
        runProblemArea( 1, true, true, "{ 5x-x^2 ; x }                            <10.666666> // Stewart, Page 352 #1; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ Surd[x+2,2] ; 1/(x+1) }        [0,2]    <2.3491029> // Stewart, Page 352 #2;" ) ;
        runProblemArea( 1, true, true, "{ x^2-1 ; Surd[x,2] }            [0,1]    <1.3333333> // Stewart, Page 352 #3;" ) ;
        runProblemArea( 1, true, true, "{ x^2-4x ; 2x-x^2 }                       <9>         // Stewart, Page 352 #4; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x+1 ; 9-x^2 }                  [-1,2]   <19.5>      // Stewart, Page 352 #5;" ) ;
        runProblemArea( 1, true, true, "{ Sin[x] ; x }                  [PI/2,PI] <2.7010912> // Stewart, Page 352 #6;" ) ;
        runProblemArea( 1, true, true, "{ x ; x^2 }                               <0.1666666> // Stewart, Page 352 #7; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2-2x ; x+4 }                          <20.833333> // Stewart, Page 352 #8; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ Surd[x+3,2] ; (x+3)/2 }                 <1.3333333> // Stewart, Page 352 #9; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ 1+Surd[x,2] ; (3+x)/3 }                 <4.5>       // Stewart, Page 352 #10; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 ; Surd[x,2] }                       <0.3333333> // Stewart, Page 352 #11; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^2 ; 4x-x^2 }                          <2.6666666> // Stewart, Page 352 #12; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ 12-x^2 ; x^2 - 6 }                      <72>        // Stewart, Page 352 #13; Implied Domain;" ) ;

        //DEBUGGABLE
        //runProblemArea( 1, true, true, "{ Cos[x] ; 2-Cos[x] }            [0,PI*2] <12.566370> // Stewart, Page 352 #14;" ) ;
        //runProblemArea( 1, true, true, "{ Sec[x]^2 ; 8*Cos[x] }      [-PI/3,PI/3] <10.392304> // Stewart, Page 352 #15;" ) ;

        runProblemArea( 1, true, true, "{ x^3-x ; 3x }                            <8>         // Stewart, Page 352 #16; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ Surd[x,2] ; .5x }              [0,9]    <4.9166666> // Stewart, Page 352 #17;" ) ;
        runProblemArea( 1, true, true, "{ 8-x^2 ; x^2 }                  [-3,3]   <30.666666> // Stewart, Page 352 #18;" ) ;

        //DEBUGGABLE
        //runProblemArea( 1, true, true, "{ 2x^2 ; 4+x^2 }                          <10.666666> // Stewart, Page 352 #19; Implied Domain;" ) ;
        
        runProblemArea( 1, true, true, "{ .25*(12 - x^2) ; x }                    <21.333333> // Stewart, Page 352 #20; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ 1-x^2 ; x^2-1 }                         <2.6666666> // Stewart, Page 352 #21; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ Sin[1.57079633*x] ; x }                 <0.2732395> // Stewart, Page 352 #22; Implied Domain; The original problem had PI/2, rather than 1.57079633, but Mathematica wouldn't answer that;" ) ;
        runProblemArea( 1, true, true, "{ Cos[x] ; Sin[2x] }             [0,PI/2] <0.4999999> // Stewart, Page 352 #23;" ) ;
        runProblemArea( 1, true, true, "{ Cos[x] ; 1 - Cos[x] }          [0,PI]   <4.5112912> // Stewart, Page 352 #24;" ) ;
        runProblemArea( 1, true, true, "{ Cos[x] ; 1-0.63661977*x }               <0.4292036> // Stewart, Page 352 #25; Implied Domain; The original problem had 2/PI, rather than 0.63661977, but Mathematica wouldn't answer that;" ) ;

        //Mathematica seems to be mad at this.
        // Abs[] with a range returns zero results 
        //runProblemArea( 1, true, true, "{ Abs[x] + 2 ; x^2 }                      <6.6666666> // Stewart, Page 352 #26; Implied Domain;" ) ;
        
        runProblemArea( 1, false, true, "{ 1/x^2 ; x ; x/8 }                       <0.7499999> // Stewart, Page 352 #27; Implied Domain;" ) ;
        runProblemArea( 1, false, true, "{ 3x^2 ; 8x^2 ; 4-4x }           [0,1]    <1.6666666> // Stewart, Page 352 #28;" ) ;
        runProblemArea( 1, true, true, "{ x*Sin[x^2] ; x^4 }                      <0.0371612> // Stewart, Page 352 #35; Implied Domain;" ) ;
        runProblemArea( 1, true, true, "{ x^4 ; 3x-x^3 }                          <1.1464674> // Stewart, Page 352 #36; Implied Domain;" ) ;
        
        //HANGS
        //runProblemArea( 1, true, true, "{ 3x^2-2x ; x^3-3x+4 }                    <8.3780257> // Stewart, Page 352 #37; Implied Domain;" ) ;
        
        runProblemArea( 1, true, true, "{ x*Cos[x] ; x^10 }                       <0.3028864> // Stewart, Page 352 #38; Implied Domain;" ) ;

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }

    private void runProblemArea( int indent, boolean attemptByY,
    		boolean expectInvertibility, String pStr ) throws DomainException
    {
        System.out.println(StringUtilities.generateTestStartString(pStr, indent));

        TextbookAreaProblem problem = AreaProblemParserTest.makeAreaProblem(pStr);
        problem.setAttemptSolveByY( attemptByY ) ;
        problem.setExpectInvertibilitySuccess( expectInvertibility ) ;

        SolverMain solver = new SolverMain();

        double answer = solver.solve(problem);

        Assertions.Assert(answer, problem.getAnswer());

        System.out.println(StringUtilities.generateTestEndString(pStr, indent));
    }
}

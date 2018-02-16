package representation.bounds.functions;

import org.junit.Test;

import utilities.Assertions;
import utilities.StringUtilities;

public class StringBasedFunctionTest
{
    @Test
    public void testHorizontal()
    {
        String testName = "String-based Function: Horizontal";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        Assertions.Assert(new StringBasedFunction("y = 0").isHorizontal(), true);
        Assertions.Assert(new StringBasedFunction("y = -1").isHorizontal(), true);
        Assertions.Assert(new StringBasedFunction("y=-1").isHorizontal(), true);
        Assertions.Assert(new StringBasedFunction("y=1").isHorizontal(), true);
        Assertions.Assert(new StringBasedFunction("x = 0").isHorizontal(), false);
        Assertions.Assert(new StringBasedFunction("x = -1").isHorizontal(), false);
        Assertions.Assert(new StringBasedFunction("x=-1").isHorizontal(), false);
        Assertions.Assert(new StringBasedFunction("x=1").isHorizontal(), false);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    @Test
    public void testVertical()
    {
        String testName = "String-based Function: Vertical";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        Assertions.Assert(new StringBasedFunction("y = 0").isVertical(), false);
        Assertions.Assert(new StringBasedFunction("y = -1").isVertical(), false);
        Assertions.Assert(new StringBasedFunction("y=-1").isVertical(), false);
        Assertions.Assert(new StringBasedFunction("y=1").isVertical(), false);
        Assertions.Assert(new StringBasedFunction("x = 0").isVertical(), true);
        Assertions.Assert(new StringBasedFunction("x = -1").isVertical(), true);
        Assertions.Assert(new StringBasedFunction("x=-1").isVertical(), true);
        Assertions.Assert(new StringBasedFunction("x=1").isVertical(), true);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    @Test
    public void test()
    {
        String testName = "String-based Function Tests";

        System.out.println(StringUtilities.generateTestStartString(testName, 0));

        testEvaluateAtPointByY(1);
        testEvaluateAtPoint(1);
        testTranslation(1);
        testStretching(1);
        testShrinking(1);
        testAll(1);

        System.out.println(StringUtilities.generateTestEndString(testName, 0));
    }
    
    private void testEvaluateAtPointByY(int indent)
    {
        String testName = "Evaluate at a Point By Y";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function and evaluate it: Polynomial
        //
        StringBasedFunction f = new StringBasedFunction("x^2");
        f.setDomain(0, 20);
        
        for (int y = 0; y < 20; y++)
        {
            Assertions.Assert(f.evaluateAtPointByY(y*y).getReal(), y);
        }
        
        //
        // Define a function and evaluate it: Polynomial
        //
        f = new StringBasedFunction("Sqrt[x]");
        f.setDomain(0, 20);
        
        for (int y = 0; y < 20; y++)
        {
            Assertions.Assert(f.evaluateAtPointByY(y).getReal(), y*y);
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testEvaluateAtPoint(int indent)
    {
        String testName = "Evaluate at a Point";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function and evaluate it: Polynomial
        //
        StringBasedFunction f = new StringBasedFunction("2x(1-x)");

        for (int x = -10; x < 10; x++)
        {
            Assertions.Assert(f.evaluateAtPoint(x).getReal(), 2 * x * (1 - x));
        }

        //
        // Define a function and evaluate it: Trigonometric
        //
        StringBasedFunction trig = new StringBasedFunction("Sin[Pi x]");

        for (int x = -10; x < 10; x++)
        {
            Assertions.Assert(trig.evaluateAtPoint(x).getReal(), 0);
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testTranslation(int indent)
    {
        String testName = "Translating a String-Based Function";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        testTranslationNone(indent + 1);
        testTranslationHorizontally(indent + 1);
        testTranslationVertically(indent + 1);
        testTranslationCombined(indent + 1);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testTranslationNone(int indent)
    {
        String testName = "No Translation";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        //
        // Define a function with two x-values and translate (0, 0): Polynomial
        //
        StringBasedFunction f = new StringBasedFunction("2x (1-x)");

        f.translate(0, 0);

        System.out.println("Function: |" + f + "|");

        for (int x = -10; x < 10; x++)
        {
            Assertions.Assert(f.evaluateAtPoint(x).getReal(), 2 * x * (1 - x));
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testTranslationHorizontally(int indent)
    {
        String testName = "Translation Horizontally";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        StringBasedFunction f = new StringBasedFunction("2x (1-x)");
        double h = 3;

        f.translate(h, 0);

        System.out.println("Function: |" + f.getFunction() + "|");

        for (int x = -10; x < 10; x++)
        {
            Assertions.Assert(f.evaluateAtPoint(x).getReal(), 2 * (x - h) * (1 - (x - h)));
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testTranslationVertically(int indent)
    {
        String testName = "Translation Vertically";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        StringBasedFunction f = new StringBasedFunction("2x (1-x)");
        double k = -10;
        double h = 0;

        f.translate(h, k);

        for (int x = -10; x < 10; x++)
        {
            Assertions.Assert(f.evaluateAtPoint(x).getReal(), 2 * (x - h) * (1 - (x - h)) + k);
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    /**
     * @param indent -- level of indentation for testing output
     * Test horizontal and vertical translations (combined)
     */
    private void testTranslationCombined(int indent)
    {
        String testName = "Translation Combined";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        for (int k = -100; k < 50; k++)
        {
            for (int h = -10; h < 10; h++)
            {
                StringBasedFunction f = new StringBasedFunction("x^2 (1-x)");

                f.translate(h, k);

                for (int x = -10; x < 10; x++)
                {
                    Assertions.Assert(f.evaluateAtPoint(x).getReal(), (x - h) * (x - h) * (1 - (x - h)) + k);
                }
            }
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    private void testStretching(int indent)
    {
        String testName = "Stretching (and Reflecting) a String-Based Function";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        testStretchVertically(indent + 1);
        testStretchHorizontally(indent + 1);
        testStretchCombined(indent + 1);

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    /**
     * @param indent -- level of indentation for testing output
     * Test vertical stretching (with reflections)
     */
    private void testStretchVertically(int indent)
    {
        String testName = "Vertical Stretch";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        double h = 0;
        double k = 0;
        for (int a = -50; a < 50; a++)
        {
            StringBasedFunction f = new StringBasedFunction("x^2 (1-x)");

            f.stretch(a, 1);

            for (int x = -30; x < 30; x++)
            {
                Assertions.Assert(f.evaluateAtPoint(x).getReal(), a * (x - h) * (x - h) * (1 - (x - h)) + k);
            }

            f.reflectOverX();

            for (int x = -30; x < 30; x++)
            {
                Assertions.Assert(f.evaluateAtPoint(x).getReal(), - a * (x - h) * (x - h) * (1 - (x - h)) + k);
            }

            f.reflectOverY();

            for (int x = -30; x < 30; x++)
            {
                Assertions.Assert(f.evaluateAtPoint(x).getReal(), -a * (x - h) * (x - h) * (1 + (x - h)) + k);
            }
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    /**
     * @param indent -- level of indentation for testing output
     * Test vertical stretching (with reflections)
     */
    private void testStretchHorizontally(int indent)
    {
        String testName = "Horizontal Stretch";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        double h = 0;
        double k = 0;
        for (int b = -50; b < 50; b++)
        {
            StringBasedFunction f = new StringBasedFunction("x^2 (1-x)");

            f.stretch(1, b);

            for (int x = -30; x < 30; x++)
            {
                Assertions.Assert(f.evaluateAtPoint(x).getReal(), (b * (x - h)) * (b * (x - h)) * (1 - (b * (x - h))) + k);
            }

            f.reflectOverX();

            for (int x = -30; x < 30; x++)
            {
                Assertions.Assert(f.evaluateAtPoint(x).getReal(), -(b * (x - h)) * (b * (x - h)) * (1 - (b * (x - h))) + k);
            }

            f.reflectOverY();

            for (int x = -30; x < 30; x++)
            {
                Assertions.Assert(f.evaluateAtPoint(x).getReal(), -(-b * (x - h)) * (-b * (x - h)) * (1 - (-b * (x - h))) + k);
            }
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    /**
     * @param indent -- level of indentation for testing output
     * Test vertical stretching (with reflections)
     */
    private void testStretchCombined(int indent)
    {
        String testName = "Combined Stretch";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        double h = 0;
        double k = 0;
        for (int a = -30; a < 50; a++)
        {
            for (int b = -30; b < 50; b++)
            {
                StringBasedFunction f = new StringBasedFunction("x^2 (1-x)");

                f.stretch(a, b);

                for (int x = -10; x < 10; x++)
                {
                    Assertions.Assert(f.evaluateAtPoint(x).getReal(), a * (b * (x - h)) * (b * (x - h)) * (1 - (b * (x - h))) + k);
                }

                f.reflectOverX();

                for (int x = -10; x < 10; x++)
                {
                    Assertions.Assert(f.evaluateAtPoint(x).getReal(), -a * (b * (x - h)) * (b * (x - h)) * (1 - (b * (x - h))) + k);
                }

                f.reflectOverY();

                for (int x = -10; x < 10; x++)
                {
                    Assertions.Assert(f.evaluateAtPoint(x).getReal(), -a * (-b * (x - h)) * (-b * (x - h)) * (1 - (-b * (x - h))) + k);
                }
            }
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    /**
     * @param indent -- level of indentation for testing output
     * Test vertical stretching (with reflections)
     */
    private void testShrinking(int indent)
    {
        String testName = "Combined Shrinking";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        double h = 0;
        double k = 0;
        for (int a = -10; a < 10; a++)
        {
            for (int b = -10; b < 10; b++)
            {
                if (a != 0 && b != 0)
                {
                    StringBasedFunction f = new StringBasedFunction("x^2");

                    f.stretch(1.0 / a, 1.0 / b);

                    for (int x = -30; x < 30; x++)
                    {
                        Assertions.Assert(f.evaluateAtPoint(x).getReal(), (1.0 / a) * ((1.0/b) * (x - h)) * ((1.0/b) * (x - h)) + k);
                    }
                }
            }
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }

    /**
     * @param indent -- level of indentation for testing output
     * Test vertical stretching (with reflections)
     */
    private void testAll(int indent)
    {
        String testName = "Combined All Transformations";

        System.out.println(StringUtilities.generateTestStartString(testName, indent));

        for (int h = -50; h < 50; h += 3)
        {
            for (int k = -50; k < 50; k += 4)
            {
                for (int a = -10; a < 10; a++)
                {
                    for (int b = -10; b < 10; b++)
                    {
                        StringBasedFunction f = new StringBasedFunction("x^2 (1-x)");

                        f.translate(h, k);
                        f.stretch(a, b);

                        for (int x = -10; x < 10; x++)
                        {
                            Assertions.Assert(f.evaluateAtPoint(x).getReal(), a * (b * (x - h)) * (b * (x - h)) * (1 - (b * (x - h))) + k);
                        }
                    }
                }
            }
        }

        System.out.println(StringUtilities.generateTestEndString(testName, indent));
    }
}

package facades;

import representation.bounds.Bound;
import template.LeftRightPointTemplate;
import template.LeftRightVerticalTemplate;
import template.RegionTemplate;
import template.TemplateRestriction;
import template.TopBottomTemplate;

public class TemplateGenerator
{
    private static final TemplateRestriction H_RESTRICTION;
    //TODO: come back to this
    //private static final TemplateRestriction V_RESTRICTION;
    private static final TemplateRestriction DEGREE_1_RESTRICTION;
    private static final TemplateRestriction DEGREE_2_RESTRICTION;
    private static final TemplateRestriction DEGREE_3_RESTRICTION;
    private static final TemplateRestriction DEGREE_4_RESTRICTION;
    
    private static final TemplateRestriction SINE_RESTRICTION;
    private static final TemplateRestriction COSINE_RESTRICTION;
    
    static
    {
        H_RESTRICTION = new TemplateRestriction(Bound.BoundT.HORIZONTAL_LINE, null, null);
        //V_RESTRICTION = new TemplateRestriction(Bound.BoundT.VERTICAL_LINE, null, null);

        DEGREE_1_RESTRICTION = new TemplateRestriction(Bound.BoundT.LINEAR, null, null);
        DEGREE_2_RESTRICTION = new TemplateRestriction(Bound.BoundT.PARABOLA, null, null);
        DEGREE_3_RESTRICTION = new TemplateRestriction(Bound.BoundT.CUBIC, null, null);
        DEGREE_4_RESTRICTION = new TemplateRestriction(Bound.BoundT.QUARTIC, null, null);

        SINE_RESTRICTION = new TemplateRestriction(Bound.BoundT.SINE, null, null);
        COSINE_RESTRICTION = new TemplateRestriction(Bound.BoundT.COSINE, null, null);
    }
    
    /**
     * @return the template for a strictly rectangular region: verticals and horizontals
     */
    public static RegionTemplate generateRectangle()
    {
        LeftRightVerticalTemplate left = new LeftRightVerticalTemplate();
        LeftRightVerticalTemplate right = new LeftRightVerticalTemplate();
 
        TopBottomTemplate top = new TopBottomTemplate(H_RESTRICTION);
        TopBottomTemplate bottom = new TopBottomTemplate(H_RESTRICTION);
        
        RegionTemplate template = new RegionTemplate(left, top, right, bottom);

        return template;
    }
    
    /**
     * @return the template for a strictly rectangular region: verticals and horizontals
     */
    public static RegionTemplate generateParabolaCappedWithLine()
    {
        // L / R
        LeftRightPointTemplate left = new LeftRightPointTemplate();
        LeftRightPointTemplate right = new LeftRightPointTemplate();

        //
        // Top / Bottom
        //
        TopBottomTemplate top = new TopBottomTemplate(DEGREE_1_RESTRICTION);
        TopBottomTemplate bottom = new TopBottomTemplate(DEGREE_2_RESTRICTION);

        // Template
        return new RegionTemplate(left, top, right, bottom);
    }
    
    /**
     * @return the template for one parabola on top and one on bottom
     */
    public static RegionTemplate generateCappedParabolas()
    {
        // L / R
        LeftRightPointTemplate left = new LeftRightPointTemplate();
        LeftRightPointTemplate right = new LeftRightPointTemplate();

        //
        // Top / Bottom
        //
        TopBottomTemplate top = new TopBottomTemplate(DEGREE_2_RESTRICTION);
        TopBottomTemplate bottom = new TopBottomTemplate(DEGREE_2_RESTRICTION);

        // Template
        return new RegionTemplate(left, top, right, bottom);
    }
    
    /**
     * @return the template for one parabola on top and two functions on bottom
     */
    public static RegionTemplate generateTwoBottomOneTop()
    {
        // L / R
        LeftRightVerticalTemplate left = new LeftRightVerticalTemplate();
        LeftRightPointTemplate right = new LeftRightPointTemplate();

        //
        // Top / Bottom
        //
        TopBottomTemplate top = new TopBottomTemplate(DEGREE_2_RESTRICTION);
        TopBottomTemplate bottom = new TopBottomTemplate();
        bottom.addBound(DEGREE_3_RESTRICTION);
        bottom.addBound(DEGREE_2_RESTRICTION);
        
        // Template
        return new RegionTemplate(left, top, right, bottom);
    }
    
    /**
     * @return the template for:
     * Top: Sine, Horizontal, Line
     * Bottom: Parabola, Line
     */
    public static RegionTemplate generateThreeTopTwoBottomAligned()
    {
        // L / R
        LeftRightVerticalTemplate left = new LeftRightVerticalTemplate();
        LeftRightPointTemplate right = new LeftRightPointTemplate();

        //
        // Top / Bottom
        //
        TopBottomTemplate top = new TopBottomTemplate();
        top.addBound(SINE_RESTRICTION);
        top.addBound(H_RESTRICTION);
        top.addBound(DEGREE_1_RESTRICTION);
        
        TopBottomTemplate bottom = new TopBottomTemplate();
        bottom.addBound(DEGREE_2_RESTRICTION);
        bottom.addBound(DEGREE_1_RESTRICTION);
        
        // Template
        return new RegionTemplate(left, top, right, bottom);
    }
    
    /**
     * @return the template for:
     * Top: Sine, Horizontal, Line
     * Bottom: Parabola, Line
     */
    public static RegionTemplate generateThreeTopThreeBottomUnaligned()
    {
        // L / R
        LeftRightPointTemplate left = new LeftRightPointTemplate();
        LeftRightPointTemplate right = new LeftRightPointTemplate();

        //
        // Top / Bottom
        //
        TopBottomTemplate top = new TopBottomTemplate();
        top.addBound(DEGREE_2_RESTRICTION);
        top.addBound(DEGREE_4_RESTRICTION);
        top.addBound(DEGREE_3_RESTRICTION);
        
        TopBottomTemplate bottom = new TopBottomTemplate();
        bottom.addBound(COSINE_RESTRICTION);
        bottom.addBound(H_RESTRICTION);
        bottom.addBound(DEGREE_1_RESTRICTION);
        
        // Template
        return new RegionTemplate(left, top, right, bottom);
    }
    
    /**
     * @return the template for a block-based rectangular region: vertical in the middle
     */
    public static RegionTemplate generateBlockBasedRectangle()
    {
        // For top / bottom bounds
        TemplateRestriction hRestriction = new TemplateRestriction(Bound.BoundT.HORIZONTAL_LINE, null, null);
        TemplateRestriction vRestriction = new TemplateRestriction(Bound.BoundT.VERTICAL_LINE, null, null);
        
        // Left / Right
        LeftRightVerticalTemplate left = new LeftRightVerticalTemplate();
        LeftRightVerticalTemplate right = new LeftRightVerticalTemplate();
 
        // Top
        TopBottomTemplate top = new TopBottomTemplate(hRestriction);

        //
        // Bottom
        //
        TopBottomTemplate bottom = new TopBottomTemplate();
        bottom.addBound(hRestriction);
        bottom.addBound(vRestriction);
        bottom.addBound(hRestriction);

        return new RegionTemplate(left, top, right, bottom);
    }
}

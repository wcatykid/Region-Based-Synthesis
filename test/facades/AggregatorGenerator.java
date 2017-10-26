package facades;

import representation.regions.Region;
import solver.RegionProblemAggregator;
import template.RegionTemplate;

public class AggregatorGenerator
{
    /**
     * @param template -- a template describing the region
     * @param region -- a region described by the template
     * @return a region aggregator that has been verified
     */
    private static RegionProblemAggregator generate(RegionTemplate template, Region region)
    {
        assert( region.verify() );
        
        return new RegionProblemAggregator(template, region);
    }

    /**
     * @return a region aggregator that is strictly a rectangle: verticals and horizontals
     */
    public static RegionProblemAggregator generateRectangle()
    {
        return generate(TemplateGenerator.generateRectangle(),
                        RegionGenerator.generateOriginRectangleRegion());
    }

    /**
     * @return a region aggregator that is strictly a square: verticals and horizontals
     */
    public static RegionProblemAggregator generateSquare()
    {
        return generate(TemplateGenerator.generateRectangle(),
                RegionGenerator.generateOriginSquareRegion());
    }
    
    public static RegionProblemAggregator generateParabolaCappedWithLine()
    {
        return generate(TemplateGenerator.generateParabolaCappedWithLine(),
                RegionGenerator.generateParabolaCappedWithLine());
    }
    
    public static RegionProblemAggregator generateCappedParabolas()
    {
        return generate(TemplateGenerator.generateCappedParabolas(),
                RegionGenerator.generateCappedParabolas());
    }
    
    public static RegionProblemAggregator generateTwoBottomOneTop()
    {
        return generate(TemplateGenerator.generateTwoBottomOneTop(),
                RegionGenerator.generateTwoBottomOneTop());
    }
    
    /**
     * @return a region described by:
     *  Top 1:    y = sin (\pi x) + 4    [0, 2]
     *  Top 2:    y = 4                  [2, 4]
     *  Top 3:    y = x                  [4, 6]
     *  Bottom 1 : y = -1/4(x - 2)^2 + 1 [0, 4]
     *  Bottom 2 : y = 3(x - 6) + 6      [4, 6]
     */
    public static RegionProblemAggregator generateThreeTopTwoBottomAligned()
    {
        return generate(TemplateGenerator.generateThreeTopTwoBottomAligned(),
                RegionGenerator.generateThreeTopTwoBottomAligned());
    }

    /**
     * @return a region described by:
     *  Top 1:    y = -1/2 (x - 2)^2 + 2     [0, 3]
     *  Top 2:    y = 1/2(x - 4)^4 + 1       [3, 6]
     *  Top 3:    y = -(x - 6)^3 + 9         [6, 9]
     *  
     *  Bottom 1 : y = 1/2(cos \pi(x - 0.5)) [0, 5]
     *  Bottom 2 : y = 0                     [5, 7]
     *  Bottom 3 : y = -9x + 63              [7, 9]
     */
    public static RegionProblemAggregator generateThreeTopThreeBottomUnaligned()
    {
        return generate(TemplateGenerator.generateThreeTopThreeBottomUnaligned(),
                RegionGenerator.generateThreeTopThreeBottomUnaligned());
    }
    
    /**
     * @return a region aggregator that is strictly a square: verticals and horizontals
     */
    public static RegionProblemAggregator generateBlockBasedRectangle()
    {
        return generate(TemplateGenerator.generateBlockBasedRectangle(),
                RegionGenerator.generateBlockBasedRegion());
    }
}

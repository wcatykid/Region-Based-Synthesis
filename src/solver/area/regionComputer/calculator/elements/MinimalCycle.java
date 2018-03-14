/*
iTutor – an intelligent tutor of mathematics
Copyright (C) 2016-2017 C. Alvin and Bradley University CS Students (list of students)
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package solver.area.regionComputer.calculator.elements;

import java.util.ArrayList;

import exceptions.RepresentationException;
import representation.Point;
import representation.bounds.Bound;
import representation.bounds.PointBound;
import representation.bounds.segments.VerticalLineSegment;
import representation.regions.LeftRight;
import representation.regions.Region;
import representation.regions.TopBottom;
import solver.area.regionComputer.undirectedPlanarGraph.NodePointT;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarEdgeAnnotation;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraph;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphEdge;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphNode;
import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphPoint;
import utilities.Utilities;

public class MinimalCycle extends Primitive
{
    //
    // We can view the points in a minimal cycle as homeomorphic to a rectangle
    // The first point connects the left with the bottom and each subsequent point continues counter-clockwise
    //
    protected ArrayList<PlanarGraphPoint> _bottom;
    protected ArrayList<PlanarGraphPoint> _right;
    protected ArrayList<PlanarGraphPoint> _top;
    protected ArrayList<PlanarGraphPoint> _left;
    
    public MinimalCycle()
    {
        super();

        _bottom = new ArrayList<PlanarGraphPoint>();
        _right = new ArrayList<PlanarGraphPoint>();
        _top = new ArrayList<PlanarGraphPoint>();
        _left = new ArrayList<PlanarGraphPoint>();
    }

    public void add(PlanarGraphPoint pt)
    {
        _points.add(pt);
    }

    public void addAll(ArrayList<PlanarGraphPoint> pts)
    {
        for (PlanarGraphPoint point : pts)
        {
            add(point);
        }
    }

    /**
     * Prints all points in the cycle
     */
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        str.append("Cycle { ");
        for (int p = 0; p < _points.size(); p++)
        {
            str.append(_points.get(p).toString());
            if (p < _points.size() - 1) str.append(", ");
        }
        str.append(" }");

        return str.toString();
    }    

    /**
     * @param graph -- a planar graph (for which this minimal cycle belongs)
     * @return -- region corresponding to this minimal cycle
     */
    public Region convertFacetToRegion(PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph)
    {
        System.out.println("Cycle: " + this);

        //
        // Identify all such points in the Rectangle
        //
        parsePoints();
        
        
        // We pursue the points in the ordered specified by facet identification: lexicographically.
        // Because of the orientation, we go counter-clockwise
        TopBottom bottom = identifyTopBottom(graph, _bottom);
        LeftRight left = identifyLeftRight(graph, _left);

        TopBottom top = identifyTopBottom(graph, _top);
        LeftRight right = identifyLeftRight(graph, _right);

        return new Region(left, top, right, bottom);
    }

    /**
     * Pre-process indices of points in the minimal cycle to clearly distinguish indices for
     * bottom, right, top, left
     */
    private void parsePoints()
    {
        // To ensure the cyclic nature, we append the first point to the end as well
        ArrayList<PlanarGraphPoint> local = new ArrayList<PlanarGraphPoint>(_points);
        local.add(_points.get(0));

        // Save the last point (for comparison)
        PlanarGraphPoint lastPoint = local.get(0);

        //
        // Bottom
        //
        _bottom.add(lastPoint);
        int index = 1;        
        while (lastPoint.getX() < local.get(index).getX())
        {
            _bottom.add(local.get(index));
            lastPoint = local.get(index);
            index++;
        }
        
        //
        // Right
        //
        _right.add(lastPoint);

        // Check if the next point is vertical or a point
        if (Utilities.equalDoubles(lastPoint.getX(), local.get(index).getX()))
        {
            _right.add(local.get(index));
            lastPoint = local.get(index);
            index++;
        }

        //
        // Top (in proper order: reversed on the fly)
        //
        _top.add(lastPoint);
        
        while (index < local.size() && lastPoint.getX() > local.get(index).getX())
        {
            _top.add(0, local.get(index)); // The top points are right -> left; reverse them: left -> right
            lastPoint = local.get(index);
            index++;
        }
        
        //
        // Left: bottom to top (lexicographically)
        //
        _left.add(lastPoint);
        if (index < local.size())
        {
            _left.add(0, local.get(index));
        }
    }

    /**
     * @param graph -- a Planar graph
     * @param points -- a Set of points lexicographically ordered from left to right corresponding to edges in the given planar graph
     * @return Given the list of points, return a region-based representation of that boundary: top or bottom
     */
    private TopBottom identifyTopBottom(PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph, ArrayList<PlanarGraphPoint> points)
    {
        TopBottom tb = new TopBottom();
        
        //
        // The bottom of the region can be a sequence of functions: maintain [left, right]
        //
        PlanarGraphPoint left = points.get(0);
        PlanarGraphPoint right = null;

        // As a means of acquiring the edge annotation function
        Bound bound = null;
        for (int index = 1; index < points.size(); index++)
        {
            //
            // We want to observe the annotation of the point in order to decide if it is the end of the interval
            //
            PlanarGraphNode<NodePointT, PlanarEdgeAnnotation> priorNode   = graph.getNode( points.get( index - 1 ) ) ;
            PlanarGraphNode<NodePointT, PlanarEdgeAnnotation> currentNode = graph.getNode( points.get( index     ) ) ;

            //This is for graphs built without midpoints between intersections
            //In this case we can do in one loop iteration what the two else ifs below have to do in two loop iterations
            if( priorNode.getAnnotation() == NodePointT.INTERSECTION && currentNode.getAnnotation() == NodePointT.INTERSECTION )
            {
                right = points.get( index ) ;

                PlanarGraphEdge<PlanarEdgeAnnotation> edge = graph.getEdge( left,  right ) ;
    
                if( edge == null )
                	System.err.println( "Edge does not exist in the graph: " + left + " " + right ) ;
                
                bound = edge.getAnnotation().getBound().clone() ;
            	
                bound.setDomain( left.getX(), right.getX() ) ;

                if( ! tb.addBound( bound ) )
                	System.err.println( "Bound addition failed: " + bound ) ;
                
                // Update for the next interval
                left  = right ;
                right =  null ;
            }
            else if (currentNode.getAnnotation() == NodePointT.MIDPOINT)
            {
                PlanarGraphEdge<PlanarEdgeAnnotation> edge = graph.getEdge(left,  points.get(index));

                if (edge == null) System.err.println("Edge does not exist in the graph: " + left + " " + right);
                
                bound = edge.getAnnotation().getBound().clone() ;
            }
            else if (currentNode.getAnnotation() == NodePointT.INTERSECTION || currentNode.getAnnotation() == NodePointT.VERTICAL)
            {
                right = points.get(index);
                
                if (bound == null) System.err.println("Bound is null; should not be.");

                bound.setDomain(left.getX(), right.getX());

                if (!tb.addBound(bound)) System.err.println("Bound addition failed: " + bound);
                
                // Update for the next interval
                left = right;
                right = null;
            }
        }
        
        return tb;
    }

    /**
     * @param graph -- a Planar graph
     * @param points -- a Set of points lexicographically ordered from left to right corresponding to edges in the given planar graph
     * @return Given the list of points, return a region-based representation of that boundary: top or bottom
     */
    private LeftRight identifyLeftRight(PlanarGraph<NodePointT, PlanarEdgeAnnotation> graph, ArrayList<PlanarGraphPoint> points)
    {
        Bound bound = null;

        //
        // Point Bound
        //
        if (points.size() == 1)
        {
            Point pt = new Point(points.get(0).getX(), points.get(0).getY());
            bound = new PointBound(pt);
        }
        //
        // Vertical Bound
        //
        else if (points.size() == 2)
        {
            Point bottom = new Point(points.get(0).getX(), points.get(0).getY());
            Point top = new Point(points.get(1).getX(), points.get(1).getY());

            try
            {
                bound = new VerticalLineSegment(bottom, top);
            }
            catch (RepresentationException e)
            {
                System.err.println("Left / Right bound is not vertical" + points.get(0) + " " + points.get(1));
            }
        }
        else
        {
            System.err.println("Left / Right bound has more than 2 points specified: " + points.size());
        }

        //
        // Return a bound
        //
        return new LeftRight(bound);
    }






    //
    //    public boolean HasExtendedSegment(PlanarGraph graph)
    //    {
    //        return GetExtendedSegment(graph) != null;
    //    }
    //
    //    public Segment GetExtendedSegment(PlanarGraph graph)
    //    {
    //        for (int p = 0; p < _points.size() - 1; p++)
    //        {
    //            //System.out.println("GetExtendedSegment: from = " + p);
    //            //System.out.println("GetExtendedSegment: to = " + (p + 1) % points.size());
    //            if (graph.getEdgeType(_points.get(p), _points.get((p + 1) % _points.size())) == EdgeType.EXTENDED_SEGMENT)
    //            {
    //                return new Segment(_points.get(p), _points.get(p + 1 < _points.size() ? p + 1 : 0));
    //            }
    //        }
    //
    //        return null;
    //    }
    //
    //    public boolean HasThisExtendedSegment(PlanarGraph graph, Segment segment)
    //    {
    //        if (!_points.contains(segment.getPoint1())) return false;
    //        if (!_points.contains(segment.getPoint2())) return false;
    //
    //        return graph.getEdgeType(segment.getPoint1(), segment.getPoint2()) == EdgeType.EXTENDED_SEGMENT;
    //    }
    //
    //    private ArrayList<PlanarGraphPoint> GetPointsBookEndedBySegment(Segment segment)
    //    {
    //        int index1 = _points.indexOf(segment.getPoint1());
    //        int index2 = _points.indexOf(segment.getPoint2());
    //
    //        // Are the points book-ended properly already?
    //        if (index1 == 0 && index2 == _points.size() - 1) return new ArrayList<PlanarGraphPoint>(_points);
    //
    //        // The set to be returned.
    //        ArrayList<PlanarGraphPoint> ordered = new ArrayList<PlanarGraphPoint>();
    //
    //        // Are the points book-ended in reverse?
    //        if (index1 == _points.size() - 1 && index2 == 0)
    //        {
    //            for (int p = _points.size() - 1; p >= 0; p--)
    //            {
    //                ordered.add(_points.get(p));
    //            }
    //
    //            return ordered;
    //        }
    //
    //        // The order is the same as specified by the segment; just cycle the points.
    //        if (index1 < index2)
    //        {
    //            for (int p = 0; p < _points.size(); p++)
    //            {
    //                @SuppressWarnings("unused")
    //                int tempIndex = (index1 - p) < 0 ? _points.size() + (index1 - p) : (index1 - p);
    //                ordered.add(_points.get((index1 - p) < 0 ? _points.size() + (index1 - p) : (index1 - p)));
    //            }
    //        }
    //        // The order is NOT the same as specified by the segment (it's reversed).
    //        else
    //        {
    //            for (int p = 0; p < _points.size(); p++)
    //            {
    //                @SuppressWarnings("unused")
    //                int tempIndex = (index1 + p) % _points.size();
    //                ordered.add(_points.get((index1 + p) % _points.size()));
    //            }
    //        }
    //
    //        return ordered;
    //    }
    //
    //    public MinimalCycle Compose(MinimalCycle thatCycle, Segment extended)
    //    {
    //        MinimalCycle composed = new MinimalCycle();
    //
    //        ArrayList<PlanarGraphPoint> thisPts = this.GetPointsBookEndedBySegment(extended);
    //        ArrayList<PlanarGraphPoint> thatPts = thatCycle.GetPointsBookEndedBySegment(extended);
    //
    //        // Add all points from this;
    //        composed.AddAll(thisPts);
    //
    //        // Add all points from that (excluding endpoints)
    //        for (int p = thatPts.size() - 2; p > 0; p--)
    //        {
    //            composed.Add(thatPts.get(p));
    //        }
    //
    //        return composed;
    //    }



    //    //
    //    //
    //    // Create the actual set of atomic regions for this cycle.
    //    //
    //    //   We need to check to see if any of the cycle segments are based on arcs.
    //    //   We have to handle the degree of each segment: do many circles intersect at these points?
    //    //
    //    public ArrayList<AtomicRegion> ConstructAtomicRegions(ArrayList<Circle> circles, PlanarGraph graph)
    //    {
    //        ArrayList<AtomicRegion> regions = new ArrayList<>();
    //
    //        AtomicRegion region = null;
    //
    //        //
    //        // Check for a direct polygon (no arcs).
    //        //
    //        region = PolygonDefinesRegion(graph);
    //        if (region != null)
    //        {
    //            regions.add(region);
    //            return regions;
    //        }
    //
    //        //
    //        // Does this region define a sector? 
    //        //
    //        ArrayList<AtomicRegion> sectors = SectorOrTruncationDefinesRegion(circles, graph);
    //        if (sectors != null && !sectors.isEmpty())
    //        {
    //            regions.addAll(sectors);
    //            return regions;
    //        }
    //
    //        //
    //        // Do we have a set of regions defined by a polygon in which circle(s) cut out some of that region? 
    //        //
    //        regions.addAll(MixedArcChordedRegion(circles, graph));
    //
    //        return regions;
    //    }
    //
    //    @SuppressWarnings("unused")
    //    private AtomicRegion PolygonDefinesRegion(PlanarGraph graph)
    //    {
    //        ArrayList<Segment> sides = new ArrayList<Segment>();
    //
    //        //
    //        // All connections between adjacent connections MUST be segments.
    //        //
    //        for (int p = 0; p < _points.size(); p++)
    //        {
    //            Segment segment = new Segment(_points.get(p), _points.get((p + 1) % _points.size()));
    //
    //            sides.add(segment);
    //
    //            if (graph.getEdge(_points.get(p), _points.get((p + 1) % _points.size()))._annotation != EdgeType.REAL_SEGMENT) return null;
    //        }
    //
    //        //
    //        // All iterative connections cannot be arcs.
    //        //
    //        for (int p1 = 0; p1 < _points.size() - 1; p1++)
    //        {
    //            // We want to check for a direct cycle, therefore, p2 starts at p1 not p1 + 1
    //            for (int p2 = p1; p2 < _points.size(); p2++)
    //            {
    //                PlanarGraphEdge edge = graph.getEdge(_points.get(p1), _points.get((p2 + 1) % _points.size()));
    //
    //                if (edge != null)
    //                {
    //                    if (edge.edgeType == EdgeType.REAL_ARC) return null;
    //                }
    //            }
    //        }
    //
    //        //
    //        // Make the Polygon
    //        //
    //        //System.out.println("MinimalCycle.PolygonDefinesRegion: sides = " + sides);
    //        Polygon poly = Polygon.MakePolygon(sides);
    //        //System.out.println("MinimalCycle.PolygonDefinesRegion: poly.orderedSides = " + poly.getOrderedSides());
    //        if (poly == null) ExceptionHandler.throwException(new AtomicRegionException("Real segments should define a polygon; they did not."));
    //
    //        return new ShapeAtomicRegion(poly);
    //    }
    //
    //    private ArrayList<AtomicRegion> SectorOrTruncationDefinesRegion(ArrayList<Circle> circles, PlanarGraph graph) 
    //    {
    //        //
    //        // Do there exist any real-dual edges or extended segments? If so, this is not a sector.
    //        //
    //        for (int p = 0; p < _points.size(); p++)
    //        {
    //            PlanarGraphEdge edge = graph.getEdge(_points.get(p), _points.get((p + 1) % _points.size()));
    //
    //            if (edge.edgeType == EdgeType.EXTENDED_SEGMENT) return null;
    //            else if (edge.edgeType == EdgeType.REAL_DUAL) return null;
    //        }
    //
    //        //
    //        // Collect all segments; split into two collinear lists.
    //        //
    //        ArrayList<Segment> segments = CollectSegments(graph);
    //        ArrayList<ArrayList<Segment>> collinearSegmentSet = SplitSegmentsIntoCollinearSequences(segments);
    //
    //        // A sector requires one (semicircl) or two sets of segments ('normal' arc).
    //        if (collinearSegmentSet.size() > 2) return null;
    //
    //        //
    //        // Collect all arcs.
    //        //
    //        ArrayList<MinorArc> arcs = CollectStrictArcs(circles, graph);
    //        ArrayList<ArrayList<MinorArc>> collinearArcSet = SplitArcsIntoCollinearSequences(arcs);
    //
    //        // A sector requires one set of arcs (no more, no less).
    //        if (collinearArcSet.size() != 1) return null;
    //
    //        // Semicircle has one set of sides
    //        if (collinearSegmentSet.size() == 1) return ConvertToTruncationOrSemicircle(collinearSegmentSet.get(0), collinearArcSet.get(0));
    //
    //        // Pacman shape created with a circle results in Sector
    //        return ConvertToGeneralSector(collinearSegmentSet.get(0), collinearSegmentSet.get(1), collinearArcSet.get(0));
    //    }
    //
    //    //
    //    // Collect all segments attributed to this this cycle
    //    //
    //    private ArrayList<Segment> CollectSegments(PlanarGraph graph)
    //    {
    //        ArrayList<Segment> segments = new ArrayList<>();
    //
    //        for (int p = 0; p < _points.size(); p++)
    //        {
    //            PlanarGraphEdge edge = graph.getEdge(_points.get(p), _points.get((p + 1) % _points.size()));
    //
    //            if (edge.edgeType == EdgeType.REAL_SEGMENT)
    //            {
    //                segments.add(new Segment(_points.get(p), _points.get((p + 1) % _points.size())));
    //            }
    //        }
    //
    //        return segments;
    //    }
    //
    //    //
    //    // Split the segments into sets of collinear segments.
    //    // NOTE: This code assumes an input ordering of segments and returns sets of ordered collinear segments.
    //    //
    //    private ArrayList<ArrayList<Segment>> SplitSegmentsIntoCollinearSequences(ArrayList<Segment> segments)
    //    {
    //        ArrayList<ArrayList<Segment>> collinearSet = new ArrayList<ArrayList<Segment>>();
    //
    //        for (Segment segment : segments)
    //        {
    //            boolean collinearFound = false;
    //            for (ArrayList<Segment> collinear : collinearSet)
    //            {
    //                // Find the set of collinear segments
    //                if (segment.isCollinearWith(collinear.get(0)))
    //                {
    //                    collinearFound = true;
    //                    int i = 0;
    //                    for (i = 0; i < collinear.size(); i++)
    //                    {
    //                        if (segment.getPoint2().structurallyEquals(collinear.get(i).getPoint1())) break;
    //                    }
    //                    collinear.add(i, segment);
    //                }
    //            }
    //
    //            if (!collinearFound) collinearSet.add((ArrayList<Segment>) Utilities.MakeList(segment));
    //        }
    //
    //        return collinearSet;
    //    }
    //
    //    //
    //    // Collect all arcs attributed to this this cycle; 
    //    //
    //    private ArrayList<MinorArc> CollectStrictArcs(ArrayList<Circle> circles, PlanarGraph graph)
    //    {
    //        ArrayList<MinorArc> minors = new ArrayList<MinorArc>();
    //
    //        for (int p = 0; p < _points.size(); p++)
    //        {
    //            PlanarGraphEdge edge = graph.getEdge(_points.get(p), _points.get((p + 1) % _points.size()));
    //
    //            if (edge.edgeType == EdgeType.REAL_ARC)
    //            {
    //                // Find the applicable circle.
    //                Circle theCircle = null;
    //                for (Circle circle : circles)
    //                {
    //                    if (circle.HasArc(_points.get(p), _points.get((p + 1) % _points.size())))
    //                    {
    //                        theCircle = circle;
    //                        break;
    //                    }
    //                }
    //
    //                minors.add(new MinorArc(theCircle, _points.get(p), _points.get((p + 1) % _points.size())));
    //            }
    //        }
    //
    //        return minors;
    //    }
    //
    //    //
    //    // Split the segments into sets of collinear segments.
    //    // NOTE: This code assumes an input ordering of segments and returns sets of ordered collinear segments.
    //    //
    //    private ArrayList<ArrayList<MinorArc>> SplitArcsIntoCollinearSequences(ArrayList<MinorArc> minors) 
    //    {
    //        ArrayList<ArrayList<MinorArc>> collinearSet = new ArrayList<ArrayList<MinorArc>>();
    //
    //        //
    //        // Collect all the related arcs
    //        //
    //        for (MinorArc minor : minors)
    //        {
    //            boolean collinearFound = false;
    //            for (ArrayList<MinorArc> collinear : collinearSet)
    //            {
    //                // Do the arcs belong to the same circle?
    //                if (minor.getCircle().structurallyEquals(collinear.get(0).getCircle()))
    //                {
    //                    collinearFound = true;
    //                    int i = 0;
    //                    for (i = 0; i < collinear.size(); i++)
    //                    {
    //                        if (minor.getEndpoint2().structurallyEquals(collinear.get(i).getEndpoint1())) break;
    //                    }
    //                    collinear.add(i, minor);
    //                }
    //            }
    //
    //            if (!collinearFound) collinearSet.add((ArrayList<MinorArc>) Utilities.MakeList(minor));
    //        }
    //
    //        //
    //        // Sort each arc set.
    //        //
    //        for (int arcSetIndex = 0; arcSetIndex < collinearSet.size(); arcSetIndex++)
    //        {
    //            //collinearSet[arcSetIndex] = SortArcSet(collinearSet[arcSetIndex]);
    //            // This should be checked to make sure it's doing what the above line does in C#
    //            // Drew Whitmire
    //            collinearSet.get(arcSetIndex).clear();
    //            collinearSet.get(arcSetIndex).addAll(SortArcSet(collinearSet.get(arcSetIndex)));
    //        }
    //
    //        return collinearSet;
    //    }
    //
    //    //
    //    // Order the arcs so the endpoints are clear in the first in last positions.
    //    //
    //    private ArrayList<MinorArc> SortArcSet(ArrayList<MinorArc> arcs) 
    //    {
    //        if (arcs.size() <= 2) return arcs;
    //
    //        boolean[] marked = new boolean[arcs.size()];
    //        ArrayList<MinorArc> sorted = new ArrayList<MinorArc>();
    //
    //        //
    //        // Find the 'first' endpoint of the arc.
    //        //
    //        int sharedCount = 0;
    //        int arcIndex = -1;
    //        for (int a1 = 0; a1 < arcs.size(); a1++)
    //        {
    //            sharedCount = 0;
    //            for (int a2 = 0; a2 < arcs.size(); a2++)
    //            {
    //                if (a1 != a2)
    //                {
    //                    if (arcs.get(a1).SharedEndpoint(arcs.get(a2)) != null) sharedCount++;
    //                }
    //            }
    //            arcIndex = a1;
    //            if (sharedCount == 1) break;
    //        }
    //
    //        // An 'end'-arc found; book-ends of list.
    //        switch(sharedCount)
    //        {
    //            case 0:
    //                ExceptionHandler.throwException(new ArgumentException("Expected a shared count of 1 or 2, not 0"));
    //            case 1:
    //                sorted.add(arcs.get(arcIndex));
    //                marked[arcIndex] = true;
    //                break;
    //            case 2:
    //                // Middle arc
    //                break;
    //            default:
    //                ExceptionHandler.throwException(new ArgumentException("Expected a shared count of 1 or 2, not (" + sharedCount + ")"));
    //        }
    //
    //        MinorArc working = sorted.get(0);
    //        while (Arrays.asList(marked).contains(false)) 
    //        {
    //            PlanarGraphPoint shared;
    //            for (arcIndex = 0; arcIndex < arcs.size(); arcIndex++)
    //            {
    //                if (!marked[arcIndex])
    //                {
    //                    shared = working.SharedEndpoint(arcs.get(arcIndex));
    //                    if (shared != null) break;
    //                }
    //            }
    //            marked[arcIndex] = true;
    //            sorted.add(arcs.get(arcIndex));
    //            working = arcs.get(arcIndex);
    //        }
    //
    //        return sorted;
    //    }
    //
    //    private ArrayList<AtomicRegion> ConvertToGeneralSector(ArrayList<Segment> sideSet1, ArrayList<Segment> sideSet2, ArrayList<MinorArc> arcs)
    //    {
    //        Segment side1 = ComposeSegmentsIntoSegment(sideSet1);
    //        Segment side2 = ComposeSegmentsIntoSegment(sideSet2);
    //        Arc theArc = ComposeArcsIntoArc(arcs);
    //
    //        //
    //        // Verify that both sides of the sector contains the center.
    //        // And many other tests to ensure proper sector acquisition.
    //        //
    //        if (!side1.has(theArc.getCircle().getCenter())) return null;
    //        if (!side2.has(theArc.getCircle().getCenter())) return null;
    //
    //        PlanarGraphPoint sharedCenter = side1.sharedVertex(side2);
    //        if (sharedCenter == null)
    //        {
    //            ExceptionHandler.throwException(new AtomicRegionException("Sides do not share a vertex as expected; they share " + sharedCenter));
    //        }
    //
    //        if (!sharedCenter.structurallyEquals(theArc.getCircle().getCenter()))
    //        {
    //            ExceptionHandler.throwException(new Exception("Center and deduced center do not equate: " + sharedCenter + " " + theArc.getCircle().getCenter()));
    //        }
    //
    //        PlanarGraphPoint segEndpoint1 = side1.other(sharedCenter);
    //        PlanarGraphPoint segEndpoint2 = side2.other(sharedCenter);
    //
    //        if (!theArc.HasEndpoint(segEndpoint1) || !theArc.HasEndpoint(segEndpoint2))
    //        {
    //            ExceptionHandler.throwException(new Exception("Side endpoints do not equate to the arc endpoints"));
    //        }
    //
    //        // Satisfied constraints, create the actual sector.
    //        Sector sector = new Sector(theArc);
    //
    //        return Utilities.MakeList(new ShapeAtomicRegion(sector));
    //    }
    //
    //    private ArrayList<AtomicRegion> ConvertToTruncationOrSemicircle(ArrayList<Segment> sideSet, ArrayList<MinorArc> arcs) 
    //    {
    //        Segment side = ComposeSegmentsIntoSegment(sideSet);
    //        Arc theArc = ComposeArcsIntoArc(arcs);
    //
    //        // Verification Step 1.
    //        if (!theArc.HasEndpoint(side.getPoint1()) || !theArc.HasEndpoint(side.getPoint2()))
    //        {
    //            ExceptionHandler.throwException(new AtomicRegionException("Semicircle / Truncation: Side endpoints do not equate to the arc endpoints"));
    //        }
    //
    //        if (theArc != null && theArc instanceof Semicircle) 
    //            {
    //                Semicircle theSemiCircle = (Semicircle) theArc;
    //                return ConvertToSemicircle(side, theSemiCircle);
    //            }
    //        if (theArc != null && theArc instanceof MinorArc)
    //        {
    //            MinorArc theMinorArc = (MinorArc) theArc;
    //            return ConvertToTruncation(side, theMinorArc);
    //        }
    //        else
    //        {
    //            return null;
    //        }
    //    }
    //
    //    private ArrayList<AtomicRegion> ConvertToTruncation(Segment chord, MinorArc arc)
    //    {
    //        AtomicRegion atom = new AtomicRegion();
    //
    //        atom.AddConnection(new Connection(chord.getPoint1(), chord.getPoint2(), ConnectionType.SEGMENT, chord));
    //
    //        atom.AddConnection(new Connection(chord.getPoint1(), chord.getPoint2(), ConnectionType.ARC, arc));
    //
    //        return Utilities.MakeList(atom);
    //    }
    //
    //    private ArrayList<AtomicRegion> ConvertToSemicircle(Segment diameter, Semicircle semi) 
    //    {
    //        // Verification Step 2.
    //        if (!diameter.pointLiesBetweenEndpoints(semi.getCircle().getCenter()))
    //        {
    //            ExceptionHandler.throwException(new AtomicRegionException("Semicircle: expected center between endpoints."));
    //        }
    //
    //        Sector sector = new Sector(semi);
    //
    //        return Utilities.MakeList(new ShapeAtomicRegion(sector));
    //    }
    //
    //    private Segment ComposeSegmentsIntoSegment(ArrayList<Segment> segments)
    //    {
    //        return new Segment(segments.get(0).getPoint1(), segments.get(segments.size() - 1).getPoint2());
    //    }
    //
    //    private Arc ComposeArcsIntoArc(ArrayList<MinorArc> minors)
    //    {
    //        // if (minors.Count == 1) return minors[0];
    //
    //        // Determine what type of arc to create.
    //        double arcMeasure = 0;
    //        for (MinorArc minor : minors)
    //        {
    //            arcMeasure += minor.getMinorMeasure();
    //        }
    //
    //        //
    //        // Create the arc
    //        //
    //
    //        // Determine the proper endpoints.
    //        if (minors.isEmpty()) { return null; }
    //        PlanarGraphPoint endpt1 = minors.get(0).OtherEndpoint(minors.get(0).SharedEndpoint(minors.get(1)));
    //        PlanarGraphPoint endpt2 = minors.get(minors.size()-1).OtherEndpoint(minors.get(minors.size()-1).SharedEndpoint(minors.get(minors.size()-2)));
    //
    //        // Create the proper arc.
    //        Circle theCircle = minors.get(0).getCircle();
    //
    //        if (Utilities.CompareValues(arcMeasure, 180))
    //        {
    //            Segment diameter = new Segment(endpt1, endpt2);
    //
    //            // Get the midpoint that is on the same side.
    //            PlanarGraphPoint midpt = theCircle.Midpoint(diameter.getPoint1(), diameter.getPoint2(), minors.get(0).getEndpoint2());
    //            return new Semicircle(minors.get(0).getCircle(), diameter.getPoint1(), diameter.getPoint2(), midpt, diameter);
    //        }
    //        else if (arcMeasure < 180) return new MinorArc(theCircle, endpt1, endpt2);
    //        else if (arcMeasure > 180) return new MajorArc(theCircle, endpt1, endpt2);
    //
    //        return null;
    //    }
    //
    //    private ArrayList<Circle> GetAllApplicableCircles(ArrayList<Circle> circles, PlanarGraphPoint pt1, PlanarGraphPoint pt2)
    //    {
    //        ArrayList<Circle> applicCircs = new ArrayList<Circle>();
    //
    //        for (Circle circle : circles)
    //        {
    //            if (circle.pointLiesOn(pt1) && circle.pointLiesOn(pt2))
    //            {
    //                applicCircs.add(circle);
    //            }
    //        }
    //
    //        return applicCircs;
    //    }
    //
    //    private ArrayList<AtomicRegion> MixedArcChordedRegion(ArrayList<Circle> thatCircles, PlanarGraph graph)
    //    {
    //        ArrayList<AtomicRegion> regions = new ArrayList<AtomicRegion>();
    //
    //        // Every segment may be have a set of circles. (on each side) surrounding it.
    //        // Keep parallel lists of: (1) segments, (2) (real) arcs, (3) left outer circles, and (4) right outer circles
    //        Segment[] regionsSegments = new Segment[_points.size()];
    //        Arc[] arcSegments = new Arc[_points.size()];
    //        Circle[] leftOuterCircles = new Circle[_points.size()];
    //        Circle[] rightOuterCircles = new Circle[_points.size()];
    //
    //        //
    //        // Populate the parallel arrays.
    //        //
    //        int currCounter = 0;
    //        for (int p = 0; p < _points.size(); )
    //        {
    //            PlanarGraphEdge edge = graph.getEdge(_points.get(p), _points.get((p + 1) % _points.size()));
    //            Segment currSegment = new Segment(_points.get(p), _points.get((p + 1) % _points.size()));
    //
    //            //
    //            // If a known segment, seek a sequence of collinear segments.
    //            //
    //            if (edge.edgeType == EdgeType.REAL_SEGMENT)
    //            {
    //                Segment actualSeg = currSegment;
    //
    //                boolean collinearExists = false;
    //                int prevPtIndex;
    //                for (prevPtIndex = p + 1; prevPtIndex < _points.size(); prevPtIndex++)
    //                {
    //                    // Make another segment with the next point.
    //                    Segment nextSeg = new Segment(_points.get(p), _points.get((prevPtIndex + 1) % _points.size()));
    //
    //                    // CTA: This criteria seems invalid in some cases....; may not have collinearity
    //
    //                    // We hit the end of the line of collinear segments.
    //                    if (!currSegment.isCollinearWith(nextSeg)) break;
    //
    //                    collinearExists = true;
    //                    actualSeg = nextSeg;
    //                }
    //
    //                // If there exists an arc over the actual segment, we have an embedded circle to consider.
    //                regionsSegments[currCounter] = actualSeg;
    //
    //                if (collinearExists)
    //                {
    //                    PlanarGraphEdge collEdge = graph.getEdge(actualSeg.getPoint1(), actualSeg.getPoint2());
    //                    if (collEdge != null)
    //                    {
    //                        if (collEdge.edgeType == EdgeType.REAL_ARC)
    //                        {
    //                            // Find all applicable circles
    //                            ArrayList<Circle> circles = GetAllApplicableCircles(thatCircles, actualSeg.getPoint1(), actualSeg.getPoint2());
    //
    //                            // Get the exact outer circles for this segment (and create any embedded regions).
    //                            OutSingle<Circle> leftOuterCircleCurrCount = new OutSingle<Circle>(); 
    //                            leftOuterCircleCurrCount.set(leftOuterCircles[currCounter]);
    //                            OutSingle<Circle> rightOuterCircleCurrCount = new OutSingle<Circle>();
    //                            rightOuterCircleCurrCount.set(rightOuterCircles[currCounter]);
    //                            regions.addAll(ConvertToCircleCircle(actualSeg, circles, leftOuterCircleCurrCount, rightOuterCircleCurrCount));
    //                        }
    //                    }
    //                }
    //
    //                currCounter++;
    //                p = prevPtIndex;
    //            }
    //            else if (edge.edgeType == EdgeType.REAL_DUAL)
    //            {
    //                regionsSegments[currCounter] = new Segment(_points.get(p), _points.get((p + 1) % _points.size()));
    //
    //                // Get the exact chord and set of circles
    //                Segment chord = regionsSegments[currCounter];
    //
    //                // Find all applicable circles
    //                ArrayList<Circle> circles = GetAllApplicableCircles(thatCircles, _points.get(p), _points.get((p + 1) % _points.size()));
    //
    //                // Get the exact outer circles for this segment (and create any embedded regions).
    //                OutSingle<Circle> leftOuterCircleCurrCount = new OutSingle<Circle>(); 
    //                leftOuterCircleCurrCount.set(leftOuterCircles[currCounter]);
    //                OutSingle<Circle> rightOuterCircleCurrCount = new OutSingle<Circle>(); ;
    //                rightOuterCircleCurrCount.set(rightOuterCircles[currCounter]);
    //                regions.addAll(ConvertToCircleCircle(chord, circles, leftOuterCircleCurrCount, rightOuterCircleCurrCount));
    //
    //                currCounter++;
    //                p++;
    //            }
    //            else if (edge.edgeType == EdgeType.REAL_ARC)
    //            {
    //                //
    //                // Find the unique circle that contains these two points.
    //                // (if more than one circle has these points, we would have had more intersections and it would be a direct chorded region)
    //                //
    //                ArrayList<Circle> circles = GetAllApplicableCircles(thatCircles, _points.get(p), _points.get((p + 1) % _points.size()));
    //
    //                if (circles.size() != 1) 
    //                    ExceptionHandler.throwException( new AtomicRegionException("Need ONLY 1 circle for REAL_ARC atom id; found (" + circles.size() + ")") );
    //
    //                arcSegments[currCounter++] = new MinorArc(circles.get(0), _points.get(p), _points.get((p + 1) % _points.size()));
    //
    //                p++;
    //            }
    //        }
    //
    //        //
    //        // Check to see if this is a region in which some connections are segments and some are arcs.
    //        // This means there were no REAL_DUAL edges.
    //        //
    //        ArrayList<AtomicRegion> generalRegions = GeneralAtomicRegion(regionsSegments, arcSegments);
    //        if (!generalRegions.isEmpty()) return generalRegions;
    //
    //        // Copy the segments into a list (ensuring no nulls)
    //        ArrayList<Segment> actSegments = new ArrayList<Segment>();
    //        for (Segment side : regionsSegments)
    //        {
    //            if (side != null) actSegments.add(side);
    //        }
    //
    //        // Construct a polygon out of the straight-up segments
    //        // This might be a polygon that defines a pathological region.
    //        Polygon poly = Polygon.MakePolygon(actSegments);
    //
    //        // Determine which outermost circles apply inside of this polygon.
    //        Circle[] circlesCutInsidePoly = new Circle[actSegments.size()];
    //        for (int p = 0; p < actSegments.size(); p++)
    //        {
    //            if (leftOuterCircles[p] != null && rightOuterCircles[p] == null)
    //            {
    //                circlesCutInsidePoly[p] = CheckCircleCutInsidePolygon(poly, leftOuterCircles[p], actSegments.get(p).getPoint1(), actSegments.get(p).getPoint2());
    //            }
    //            else if (leftOuterCircles[p] == null && rightOuterCircles[p] != null)
    //            {
    //                circlesCutInsidePoly[p] = CheckCircleCutInsidePolygon(poly, rightOuterCircles[p], actSegments.get(p).getPoint1(), actSegments.get(p).getPoint2());
    //            }
    //            else if (leftOuterCircles[p] != null && rightOuterCircles[p] != null)
    //            {
    //                circlesCutInsidePoly[p] = CheckCircleCutInsidePolygon(poly, leftOuterCircles[p], actSegments.get(p).getPoint1(), actSegments.get(p).getPoint2());
    //
    //                if (circlesCutInsidePoly[p] == null) circlesCutInsidePoly[p] = rightOuterCircles[p];
    //            }
    //            else
    //            {
    //                circlesCutInsidePoly[p] = null;
    //            }
    //        }
    //
    //        boolean isStrictPoly = true;
    //        for (int p = 0; p < actSegments.size(); p++)
    //        {
    //            if (circlesCutInsidePoly[p] != null || arcSegments[p] != null)
    //            {
    //                isStrictPoly = false;
    //                break;
    //            }
    //        }
    //
    //        // This is just a normal shape region: polygon.
    //        if (isStrictPoly)
    //        {
    //            regions.add(new ShapeAtomicRegion(poly));
    //        }
    //        // A circle cuts into the polygon.
    //        else
    //        {
    //            //
    //            // Now that all interior arcs have been identified, construct the atomic (probably pathological) region
    //            //
    //            AtomicRegion pathological = new AtomicRegion();
    //            for (int p = 0; p < actSegments.size(); p++)
    //            {
    //                //
    //                // A circle cutting inside the polygon
    //                //
    //                if (circlesCutInsidePoly[p] != null)
    //                {
    //                    Arc theArc = null;
    //
    //                    if (circlesCutInsidePoly[p].DefinesDiameter(regionsSegments[p]))
    //                    {
    //                        PlanarGraphPoint midpt = circlesCutInsidePoly[p].getMidpoint(regionsSegments[p].getPoint1(), regionsSegments[p].getPoint2());
    //
    //                        if (!poly.pointLiesIn(midpt)) midpt = circlesCutInsidePoly[p].OppositePoint(midpt);
    //
    //                        theArc = new Semicircle(circlesCutInsidePoly[p], regionsSegments[p].getPoint1(), regionsSegments[p].getPoint2(), midpt, regionsSegments[p]);
    //                    }
    //                    else
    //                    {
    //                        theArc = new MinorArc(circlesCutInsidePoly[p], regionsSegments[p].getPoint1(), regionsSegments[p].getPoint2());
    //                    }
    //
    //                    pathological.addConnection(regionsSegments[p].getPoint1(), regionsSegments[p].getPoint2(), ConnectionType.ARC, theArc);
    //                }
    //                //
    //                else
    //                {
    //                    // We have a direct arc
    //                    if (arcSegments[p] != null)
    //                    {
    //                        pathological.addConnection(regionsSegments[p].getPoint1(), regionsSegments[p].getPoint2(),
    //                                                   ConnectionType.ARC, arcSegments[p]);
    //                    }
    //                    // Use the segment
    //                    else
    //                    {
    //                        pathological.addConnection(regionsSegments[p].getPoint1(), regionsSegments[p].getPoint2(),
    //                                                   ConnectionType.SEGMENT, regionsSegments[p]);
    //                    }
    //                }
    //            }
    //
    //            regions.add(pathological);
    //        }
    //
    //
    //        return regions;
    //    }
    //
    //    //
    //    // Determine if this is a true polygon situation or if it is a sequence of segments and arcs.
    //    //
    //    private ArrayList<AtomicRegion> GeneralAtomicRegion(Segment[] segments, Arc[] arcs)
    //    {
    //        ArrayList<AtomicRegion> regions = new ArrayList<AtomicRegion>();
    //
    //        //
    //        // Determine if the parts are all segments.
    //        // Concurrently determine the proper starting point in the sequence to construct the atomic region.
    //        //
    //        boolean hasArc = false;
    //        boolean hasSegment = false;
    //        int startIndex = 0;
    //        for (int i = 0; i < segments.length && i < arcs.length; i++)
    //        {
    //            // Both an arc and a segment.
    //            if (segments[i] != null && arcs[i] != null) return regions;
    //
    //            // Determine if we have an arc and/or a segment.
    //            if (segments[i] != null) hasSegment = true;
    //            if (arcs[i] != null) hasArc = true;
    //
    //            // A solid starting point is an arc right after a null.
    //            if (arcs[i] == null && arcs[(i + 1) % arcs.length] != null)
    //            {
    //                // Assign only once to the startIndex
    //                if (startIndex == 0) startIndex = (i + 1) % arcs.length;
    //            }
    //        }
    //
    //        // If only segments, we have a polygon.
    //        if (hasSegment && !hasArc) return regions;
    //
    //        //
    //        // If the set ONLY consists of arcs, ensure we have a good starting point.
    //        //
    //        if (hasArc && !hasSegment)
    //        {
    //            // Seek the first index where a change among arcs occurs.
    //            for (int i = 0; i < arcs.length; i++)
    //            {
    //                // A solid starting point is an arc right after a null.
    //                if (!arcs[i].getCircle().structurallyEquals(arcs[(i + 1) % arcs.length].getCircle()))
    //                {
    //                    startIndex = (i + 1) % arcs.length;
    //                    break;
    //                }
    //            }
    //        }
    //
    //        AtomicRegion theRegion = new AtomicRegion();
    //        for (int i = 0; i < segments.length && i < arcs.length; i++)
    //        {
    //            int currIndex = (i + startIndex) % arcs.length;
    //
    //            if (segments[currIndex] == null && arcs[currIndex] == null) { /* No-Op */ }
    //
    //            if (segments[currIndex] != null)
    //            {
    //                theRegion.AddConnection(new Connection(segments[currIndex].getPoint1(),
    //                                                       segments[currIndex].getPoint2(), ConnectionType.SEGMENT, segments[currIndex]));
    //            }
    //            else if (arcs[currIndex] != null)
    //            {
    //                //
    //                // Compose the arcs (from a single circle) together.
    //                //
    //                ArrayList<MinorArc> sequentialArcs = new ArrayList<MinorArc>();
    //                if (arcs[currIndex] != null && arcs[currIndex] instanceof MinorArc)
    //                {
    //                    MinorArc arcCurrIndex = (MinorArc) arcs[currIndex];
    //                    sequentialArcs.add(arcCurrIndex);
    //                }
    //
    //                int seqIndex;
    //                for (seqIndex = (currIndex + 1) % arcs.length; ; seqIndex = (seqIndex + 1) % arcs.length, i++)
    //                {
    //                    if (arcs[seqIndex] == null) break;
    //
    //                    if (arcs[currIndex].getCircle().structurallyEquals(arcs[seqIndex].getCircle()))
    //                    {
    //                        if (arcs[seqIndex] != null && arcs[seqIndex] instanceof MinorArc)
    //                        {
    //                            MinorArc arcSeqIndex = (MinorArc) arcs[seqIndex];
    //                            sequentialArcs.add(arcSeqIndex);
    //                        }
    //                    }
    //                    else break;
    //                }
    //
    //                Arc composed;
    //                if (sequentialArcs.size() > 1) composed = this.ComposeArcsIntoArc(sequentialArcs);
    //                else composed = arcs[currIndex];
    //
    //                //
    //                // Add the connection.
    //                //
    //                theRegion.AddConnection(new Connection(composed.getEndpoint1(), composed.getEndpoint2(), ConnectionType.ARC, composed));
    //            }
    //        }
    //
    //        return Utilities.MakeList(theRegion);
    //    }
    //
    //    private Circle CheckCircleCutInsidePolygon(Polygon poly, Circle circle, PlanarGraphPoint pt1, PlanarGraphPoint pt2)
    //    {
    //        Segment diameter = new Segment(pt1, pt2);
    //
    //        // A semicircle always cuts into the polygon.
    //        if (circle.DefinesDiameter(diameter)) return circle;
    //        else
    //        {
    //            // Is the midpoint on the interior of the polygon?
    //            PlanarGraphPoint midpt = circle.getMidpoint(pt1, pt2);
    //
    //            // Is this point in the interior of this polygon?
    //            if (poly.pointLiesIn(midpt)) return circle;
    //        }
    //
    //        return null;
    //    }
    //
    //    //
    //    // This is a complex situation because we need to identify situations where circles intersect with the resultant regions:
    //    //    (|     (|)
    //    //   ( |    ( | )
    //    //  (  |   (  |  )
    //    //   ( |    ( | )
    //    //    (|     (|)
    //    //
    //    // Note: There will always be a chord because of our implied construction.
    //    // We are interested in only minor arcs of the given circles.
    //    //
    //    @SuppressWarnings("null")
    //    private ArrayList<AtomicRegion> ConvertToCircleCircle(Segment chord,
    //                                                              ArrayList<Circle> circles,
    //                                                              OutSingle<Circle> leftOuterCircle,
    //                                                              OutSingle<Circle> rightOuterCircle)
    //    {
    //        ArrayList<AtomicRegion> regions = new ArrayList<AtomicRegion>();
    //        leftOuterCircle = null;
    //        rightOuterCircle = null;
    //
    //        //
    //        // Simple cases that require no special attention.
    //        //
    //        if (circles.isEmpty()) return null;
    //        if (circles.size() == 1)
    //        {
    //            leftOuterCircle.set(circles.get(0));
    //
    //            regions.addAll(ConstructBasicLineCircleRegion(chord, circles.get(0)));
    //
    //            return regions;
    //        }
    //
    //        // All circles that are on each side of the chord 
    //        ArrayList<Circle> leftSide = new ArrayList<Circle>();
    //        ArrayList<Circle> rightSide = new ArrayList<Circle>();
    //
    //        // For now, assume max, one circle per side.
    //        // Construct a collinear list of points that includes all circle centers as well as the single intersection point between the chord and the line passing through all circle centers.
    //        // This orders the sides and provides implied sizes.
    //
    //        Segment centerLine = new Segment(circles.get(0).getCenter(), circles.get(1).getCenter());
    //        for (int c = 2; c < circles.size(); c++)
    //        {
    //            centerLine.addCollinearPoint(circles.get(c).getCenter());
    //        }
    //        // Find the intersection between the center-line and the chord; add that to the list.
    //        PlanarGraphPoint intersection = centerLine.segmentIntersection(chord);
    //        centerLine.addCollinearPoint(intersection);
    //
    //        List<PlanarGraphPoint> collPoints = centerLine.getCollinear();
    //        int interIndex = collPoints.indexOf(intersection);
    //
    //        for (int i = 0; i < collPoints.size(); i++)
    //        {
    //            // find the circle based on center
    //            int c;
    //            for (c = 0; c < circles.size(); c++)
    //            {
    //                if (circles.get(c).getCenter().structurallyEquals(collPoints.get(i))) break;
    //            }
    //
    //            // Add the circle in order
    //            if (i < interIndex) leftSide.add(circles.get(c));
    //            else if (i > interIndex) rightSide.add(circles.get(c));
    //        }
    //
    //        // the outermost circle is first in the left list and last in the right list.
    //        if (!leftSide.isEmpty()) leftOuterCircle.set(leftSide.get(0));
    //        if (!rightSide.isEmpty()) rightOuterCircle.set(rightSide.get(rightSide.size() - 1));
    //
    //        //
    //        // Main combining algorithm:
    //        //     Assume: Increasing Arc sequence A \in A_1, A_2, ..., A_n and the single chord C
    //        //
    //        //     Construct region B = (C, A_1)
    //        //     For the increasing Arc sequence (k subscript)  A_2, A_3, ..., A_n
    //        //         B = Construct ((C, A_k) \ B)
    //        //         
    //        // Alternatively:
    //        //     Construct(C, A_1)
    //        //     for each pair Construct (A_k, A_{k+1})
    //        //
    //        //
    //        // Handle each side: left and right.
    //        //
    //        if (!leftSide.isEmpty()) regions.addAll(ConstructBasicLineCircleRegion(chord, leftSide.get(leftSide.size() - 1)));
    //        for (int ell = 0; ell < leftSide.size() - 2; ell++)
    //        {
    //            regions.add(ConstructBasicCircleCircleRegion(chord, leftSide.get(ell), leftSide.get(ell + 1)));
    //        }
    //
    //        if (!rightSide.isEmpty()) regions.addAll(ConstructBasicLineCircleRegion(chord, rightSide.get(0)));
    //        for (int r = 1; r < rightSide.size() - 1; r++)
    //        {
    //            regions.add(ConstructBasicCircleCircleRegion(chord, rightSide.get(r), rightSide.get(r + 1)));
    //        }
    //
    //        return regions;
    //    }
    //
    //    // Construct the region between a chord and the circle arc:
    //    //    (|
    //    //   ( |
    //    //  (  |
    //    //   ( |
    //    //    (|
    //    //
    //    private ArrayList<AtomicRegion> ConstructBasicLineCircleRegion(Segment chord, Circle circle)
    //    {
    //        //
    //        // Standard
    //        //
    //        if (!circle.DefinesDiameter(chord))
    //        {
    //            AtomicRegion region = new AtomicRegion();
    //
    //            Arc theArc = new MinorArc(circle, chord.getPoint1(), chord.getPoint2());
    //
    //            region.addConnection(chord.getPoint1(), chord.getPoint2(), ConnectionType.ARC, theArc);
    //
    //            region.addConnection(chord.getPoint1(), chord.getPoint2(), ConnectionType.SEGMENT, chord);
    //
    //            return Utilities.MakeList(region);
    //        }
    //
    //        //
    //        // Semi-circles
    //        //
    //
    //        PlanarGraphPoint midpt = circle.getMidpoint(chord.getPoint1(), chord.getPoint2());
    //        Arc semi1 = new Semicircle(circle, chord.getPoint1(), chord.getPoint2(), midpt, chord);
    //        ShapeAtomicRegion region1 = new ShapeAtomicRegion(new Sector(semi1));
    //
    //        PlanarGraphPoint opp = circle.OppositePoint(midpt);
    //        Arc semi2 = new Semicircle(circle, chord.getPoint1(), chord.getPoint2(), opp, chord);
    //        ShapeAtomicRegion region2 = new ShapeAtomicRegion(new Sector(semi2));
    //
    //        ArrayList<AtomicRegion> regions = new ArrayList<AtomicRegion>();
    //        regions.add(region1);
    //        regions.add(region2);
    //
    //        return regions;
    //    }
    //
    //    // Construct the region between a circle and circle:
    //    //     __
    //    //    ( (
    //    //   ( ( 
    //    //  ( (  
    //    //   ( ( 
    //    //    ( (
    //    //     --
    //    private AtomicRegion ConstructBasicCircleCircleRegion(Segment chord, Circle smaller, Circle larger)
    //    {
    //        AtomicRegion region = new AtomicRegion();
    //
    //        Arc arc1 = null;
    //        if (smaller.DefinesDiameter(chord))
    //        {
    //            PlanarGraphPoint midpt = smaller.Midpoint(chord.getPoint1(), chord.getPoint2(), larger.getMidpoint(chord.getPoint1(), chord.getPoint2()));
    //
    //            arc1 = new Semicircle(smaller, chord.getPoint1(), chord.getPoint2(), midpt, chord);
    //        }
    //        else
    //        {
    //            arc1 = new MinorArc(smaller, chord.getPoint1(), chord.getPoint2());
    //        }
    //
    //        MinorArc arc2 = new MinorArc(larger, chord.getPoint1(), chord.getPoint2());
    //
    //        region.addConnection(chord.getPoint1(), chord.getPoint2(), ConnectionType.ARC, arc1);
    //
    //        region.addConnection(chord.getPoint1(), chord.getPoint2(), ConnectionType.ARC, arc2);
    //
    //        return region;
    //    }

}

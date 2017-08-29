/*
iTutor – an intelligent tutor of mathematics
Copyright (C) 2016-2017 C. Alvin and Bradley University CS Students (list of students)
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package solver.area.regionComputer.undirectedPlanarGraph;

import java.util.ArrayList;

import utilities.Utilities;

/**
 * An annotated planar graph given by <N, E> : nodes, edges (annotations)
 */
public class PlanarGraph<N, E>
{
    //
    // The graph is stored as a list of nodes (nodes contain edges)
    //
    private ArrayList<PlanarGraphNode<N, E>> _nodes;
    public ArrayList<PlanarGraphNode<N, E>> getNodes() { return _nodes; }

    /**
     * Constructor
     */
    public PlanarGraph()
    {
        _nodes = new ArrayList<PlanarGraphNode<N, E>>();
    }

    /**
     * Shallow copy constructor
     * @param thatG     the graph to copy from
     */
    public PlanarGraph(PlanarGraph<N, E> that)
    {
        this();
        
        for (PlanarGraphNode<N, E> node : that.getNodes())
        {
            _nodes.add(new PlanarGraphNode<N, E>(node));
        }
    }
    
    /**
     * Add a node to the graph
     * @param value     the Point to add
     */
    public void addNode(PlanarGraphPoint value, N annotation)
    {
        // Avoid redundant additions.
        if (indexOf(value) != -1) return;

        // make PlanarGraphNode
        PlanarGraphNode<N, E> node = new PlanarGraphNode<N, E>(value, annotation);

        _nodes.add(node);
    }

    /**
     * @param pt    the given point
     * @return      the index of the point or -1 if the node is not in the graph
     * Get the index of the given Point
     */
    public int indexOf(PlanarGraphPoint that)
    {
        for (int i = 0; i < _nodes.size(); i++)
        {
            if (Utilities.equalDoubles(that.getX(), _nodes.get(i).getPoint().getX()) &&
                Utilities.equalDoubles(that.getY(), _nodes.get(i).getPoint().getY()))
            {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * @param pt    the given point
     * @return      the node or null if the node is not in the graph
     */
    public PlanarGraphNode<N, E> getNode(PlanarGraphPoint that)
    {
        int index = indexOf(that);

        // if index = -1, node is not in the graph
        if (index == -1) return null;
        
        return _nodes.get(index);
    }

    //    /**
    //     * Determine the new, updated edge type.
    //     * @param oldType   the old edge type
    //     * @param newType   the new edge type
    //     * @return          the updated edge type
    //     */
    //    private EdgeType updateEdge(EdgeType oldType, EdgeType newType)
    //    {
    //        if (oldType == EdgeType.REAL_SEGMENT && newType == EdgeType.REAL_SEGMENT)
    //        {
    //            return EdgeType.REAL_SEGMENT;
    //            //throw new ArgumentException("Cannot have two edges defined by a real segment.");
    //        }
    //
    //        if (oldType == EdgeType.EXTENDED_SEGMENT || newType == EdgeType.EXTENDED_SEGMENT)
    //        {
    //            return EdgeType.EXTENDED_SEGMENT;
    ////            throw new ArgumentException("Cannot change an edge to / from an extended segment type.");
    //        }
    //
    //        if (newType == EdgeType.REAL_DUAL)
    //        {
    //            return EdgeType.REAL_DUAL;
    //
    ////            throw new ArgumentException("Cannot change an edge to be dual.");
    //        }
    //
    //        // DUAL + ARC / SEGMENT = DUAL
    //        if (oldType == EdgeType.REAL_DUAL) return EdgeType.REAL_DUAL;
    //
    //        // SEGMENT + ARC = DUAL
    //        if (oldType == EdgeType.REAL_SEGMENT && newType == EdgeType.REAL_ARC) return EdgeType.REAL_DUAL;
    //
    //        // ARC + SEGMENT = DUAL
    //        if (oldType == EdgeType.REAL_ARC && newType == EdgeType.REAL_SEGMENT) return EdgeType.REAL_DUAL;
    //
    //        // ARC + ARC = ARC
    //        if (oldType == EdgeType.REAL_ARC && newType == EdgeType.REAL_ARC) return EdgeType.REAL_ARC;
    //
    //        // default should not be reached.
    //        return EdgeType.REAL_DUAL;
    //    }

    /**
     * Add an undirected edge to the graph
     * @param from                  the Point the edge is from
     * @param to                    the Point the edge is going to
     * @param annotation            The annotation for this edge
     * @throws IllegalArgumentException
     */
    public void addUndirectedEdge(PlanarGraphPoint from, PlanarGraphPoint to, E annotation) throws IllegalArgumentException
    {
        //
        // Are these nodes in the graph?
        //
        int fromNodeIndex = indexOf(from);
        int toNodeIndex = indexOf(to);

        if (fromNodeIndex == -1 || toNodeIndex == -1)
        {
            throw new IllegalArgumentException("Edge uses undefined nodes: " + from + " " + to);
        }

        //
        // Add the edge if it doesn't already exist
        //
        if (hasEdge(from, to)) return;

        // Undirected: add in both directions
        _nodes.get(fromNodeIndex).addEdge(to, annotation);
        _nodes.get(toNodeIndex).addEdge(from, annotation);
    }

    //    /**
    //     * Return true if the given Point is contained in the graph
    //     * @param value     the given Point
    //     * @return          true if the Point is contained in the graph
    //     */
    //    public boolean contains(PlanarGraphPoint value)
    //    {
    //        //return nodes.contains(new PlanarGraphNode(value));
    //        if (indexOf(value) == -1)
    //        {
    //            return false;
    //        }
    //        return true;
    //    }

    /**
     * Remove the given Point form the graph
     * @param value     the given Point
     * @return          true if the Point was successfully removed from the graph
     */
    public boolean removeNode(PlanarGraphPoint value)
    {
        //if (!nodes.remove(new PlanarGraphNode(value))) return false;
        // get the node index
        int index = indexOf(value);

        // if index = -1, node is not in the graph
        if (index == -1) return false;

        // remove the edge
        _nodes.remove(index);

        // enumerate through each node in the nodes, removing edges to this node
        for (PlanarGraphNode<N, E> node : _nodes)
        {
            node.removeEdge(value);
        }

        return true;
    }

    /**
     * Remove the given range of Points from the graph
     * @param from      the start of the range
     * @param to        the end of the range
     * @return          true if the range of Points was successfully removed from the graph
     */
    public boolean removeEdge(PlanarGraphPoint from, PlanarGraphPoint to)
    {
        // Does this edge exist already?
        int fromNodeIndex = indexOf(from);
        if (fromNodeIndex == -1) return false;
        _nodes.get(fromNodeIndex).removeEdge(to);

        int toNodeIndex = indexOf(to);
        if (toNodeIndex == -1) return false;
        _nodes.get(toNodeIndex).removeEdge(from);

        return true;
    }

    /**
     * Get the edge between the from and two Points
     * @param from      the from Point
     * @param to        the to Point
     * @return          the edge
     */
    public PlanarGraphEdge<E> getEdge(PlanarGraphPoint from, PlanarGraphPoint to)
    {
        // Does this edge exist already?
        int fromNodeIndex = indexOf(from);
        if (fromNodeIndex == -1) return null;

        int toNodeIndex = indexOf(to);
        if (toNodeIndex == -1) return null;

        return _nodes.get(fromNodeIndex).getEdge(to);
    }

    /**
     * @param from      the from Point
     * @param to        the to Point
     * @return          if this edge exists in the graph or not
     */
    public boolean hasEdge(PlanarGraphPoint from, PlanarGraphPoint to)
    {
        return getEdge(from, to) != null;
    }

    /**
     * @param from      the from Point
     * @param to        the to Point
     * @return          the edge annotation of the edge between the from and two Points
     */
    public E getEdgeAnnotation(PlanarGraphPoint from, PlanarGraphPoint to)
    {
        PlanarGraphEdge<E> edge = getEdge(from, to);

        return edge != null ? edge.getAnnotation() : null;
    }

    /**
     * Mark the edge between the from and to Points a cycle
     * @param from      the from Point
     * @param to        the to Point
     */
    public void markCycleEdge(PlanarGraphPoint from, PlanarGraphPoint to)
    {
        int fromNodeIndex = indexOf(from);
        if (fromNodeIndex == -1) return;
        _nodes.get(fromNodeIndex).markEdge(to);

        int toNodeIndex = indexOf(to);
        if (toNodeIndex == -1) return;
        _nodes.get(toNodeIndex).markEdge(from);
    }

    /**
     * Return true if the edge between the from and to Points has been marked a cycle edge
     * @param from      the from Point
     * @param to        the to Point
     * @return          True if the edge has been marked a cycle edge
     */
    public boolean isCycleEdge(PlanarGraphPoint from, PlanarGraphPoint to)
    {
        // Does this edge exist already?
        int fromNodeIndex = indexOf(from);

        if (fromNodeIndex == -1) return false;

        return _nodes.get(fromNodeIndex).isCyclicEdge(to);
    }

    /**
     * Unmark any marked nodes and edges
     */
    public void reset()
    {
        for (PlanarGraphNode<N, E> node : _nodes)
        {
            node.clear();
        }
    }

    /**
     * Return the number of nodes in the graph
     * @return  the number of nodes in the graph
     */
    public int size()
    {
        return _nodes.size();
    }

    /**
     * @return  the number of edges in the graph
     */
    public int numEdges()
    {
        int edges = 0;
        for (PlanarGraphNode<N, E> node : _nodes)
        {
            edges += node.getEdges().size();
        }
        // Each edge will be counted twice; account for this
        return edges / 2;
    }

    /* 
     * Return the graph as a string
     */
    public String toString()
    {
        String retS = "";

        for(PlanarGraphNode<N, E> node : _nodes)
        {
            retS += node.toString() + "\n";
        }

        return retS;
    }
}

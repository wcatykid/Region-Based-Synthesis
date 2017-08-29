/*
iTutor – an intelligent tutor of mathematics
Copyright (C) 2016-2017 C. Alvin and Bradley University CS Students (list of students)
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package solver.area.regionComputer.undirectedPlanarGraph;

import java.util.ArrayList;


/**
 * An annotated planar graph given by <N, E> : nodes, edges (annotations)
 */
public class PlanarGraphNode<N, E>
{
    //
    // Edges where this node implies from in a <from, to> edge
    //
    protected ArrayList<PlanarGraphEdge<E>> _edges;
    public ArrayList<PlanarGraphEdge<E>> getEdges() { return _edges; }
    
    protected PlanarGraphPoint _thePoint;
    public PlanarGraphPoint getPoint() { return _thePoint; }

    //
    // The general annotation for this point
    //
    protected N _annotation;
    public N getAnnotation() { return _annotation; }
    
    /**
     * Constructor
     * @param value     the point to construct the node with
     */
    public PlanarGraphNode(PlanarGraphPoint value, N ann)
    {
        _thePoint = value;
        _annotation = ann;
        _edges = new ArrayList<PlanarGraphEdge<E>>();
    }

    /**
     * Shallow copy constructor
     * @param thatNode
     */
    public PlanarGraphNode(PlanarGraphNode<N, E> that)
    {
        this(that.getPoint(), that.getAnnotation());

        for (PlanarGraphEdge<E> e : that.getEdges())
        {
            _edges.add(new PlanarGraphEdge<E>(e));
        }
    }

/**
 * @param target -- in an edge <source, target>; target is the 'to' node
 * @param annotation
 */
    public void addEdge(PlanarGraphPoint target, E annotation)
    {
        if (hasEdge(target)) return;
        
        _edges.add(new PlanarGraphEdge<E>(target, annotation));
    }

    /**
     * Get the edge of the target Point
     * @param targ      the target Point
     * @return          the edge of the target Point
     */
    public PlanarGraphEdge<E> getEdge(PlanarGraphPoint targ)
    {
        int index = _edges.indexOf(new PlanarGraphEdge<E>(targ));

        return index == -1 ? null : _edges.get(index);
    }

    /**
     * @param targ -- target Point
     * @return if this node has an edge such that <source, target>
     */
    public boolean hasEdge(PlanarGraphPoint target)
    {
        return getEdge(target) != null;
    }
    
    /**
     * Remove the target Point from the graph
     * @param targetNode    the target Point
     */
    public void removeEdge(PlanarGraphPoint targetNode)
    {
        _edges.remove(new PlanarGraphEdge<E>(targetNode));
    }


    /**
     * Mark the edge of the target Point in the graph
     * @param targetNode    the target Point
     */
    public void markEdge(PlanarGraphPoint targetNode)
    {
        PlanarGraphEdge<E> edge = getEdge(targetNode);

        if (edge == null) return;

        edge.markCyclic();
    }

    /**
     * Return true if the target Point is a cyclic edge
     * @param targetNode    the target Point
     * @return              true if the target Point is a cyclic edge
     */
    public boolean isCyclicEdge(PlanarGraphPoint targetNode)
    {
        PlanarGraphEdge<E> edge = getEdge(targetNode);

        if (edge == null) return false;

        return edge.isCyclic();
    }

    /**
     * get the degree of the node as an int
     * @return  the degree of the node
     */
    public int nodeDegree()
    {
        return _edges.size();
    }

    /**
     * Clear the edges of the graph
     */
    public void clear()
    {
        for (PlanarGraphEdge<E> e : _edges)
        {
            e.unmarkCyclic();
        }
    }

    /**
     * Get the hash code of the graph
     * @return  the hash code of the graph
     */
    public int getHashCode() { return super.hashCode(); }

    /* 
     * Equality is only based on the point in the graph.
     */
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        
        if (!(obj instanceof PlanarGraphNode)) return false;
        
        @SuppressWarnings("unchecked")
        PlanarGraphNode<N,E> that = (PlanarGraphNode<N,E>) obj;
        
        return _thePoint.equals(that._thePoint);
    }

    /* 
     * Return the graph as a string
     */
    public String toString()
    {
        String retS = "";
        
        retS += "<" + _thePoint.toString() + "(" + _edges.size() + "): ";
        for (PlanarGraphEdge<E> edge : _edges)
        {
            retS += edge.toString() + "\t";
        }
        
        return retS;
    }

}

/*
iTutor – an intelligent tutor of mathematics
Copyright (C) 2016-2017 C. Alvin and Bradley University CS Students (list of students)
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package solver.area.regionComputer.calculator.elements;

import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphPoint;

/**
 * A filament is a sequence of points that does not result in a facet.
 */
public class Filament extends Primitive
{
    /**
     * Instantiate a filament
     */
    public Filament() { super(); }
    
    /**
     * Add a Point to the filament
     * @param pt    the Point to be added
     */
    public void add(PlanarGraphPoint pt)
    {
        _points.add(pt);
    }
    
    public String toString() 
    {
        String retS = "Filament { ";
        
        // traverse the array and add each point
        for (int i = 0; i < _points.size(); i++)
        {
            retS += _points.get(i);
            if (i < (_points.size() - 1))
                retS += ", ";
        }
        
        retS += " }";
        
        return retS;
    }   
}

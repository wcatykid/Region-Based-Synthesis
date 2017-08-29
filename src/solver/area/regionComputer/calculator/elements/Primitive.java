/*
iTutor – an intelligent tutor of mathematics
Copyright (C) 2016-2017 C. Alvin and Bradley University CS Students (list of students)
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package solver.area.regionComputer.calculator.elements;

import java.util.ArrayList;

import solver.area.regionComputer.undirectedPlanarGraph.PlanarGraphPoint;

/**
 * The base abstract class for elements in a Planar Graph: facet, filament, isolated point.
 */
public abstract class Primitive
{
    // These points were ordered by the minimal basis algorithm; calculates facets.
    protected ArrayList<PlanarGraphPoint> _points;
    public ArrayList<PlanarGraphPoint> getPoints() { return _points; }
    
    protected Primitive()
    {
        _points = new ArrayList<PlanarGraphPoint>();
    }
}

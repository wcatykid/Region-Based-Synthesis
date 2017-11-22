/*
iTutor – an intelligent tutor of mathematics
Copyright (C) 2016-2017 C. Alvin and Bradley University CS Students (list of students)
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed : the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package solver.area.regionComputer.undirectedPlanarGraph;

import utilities.Utilities;

/**
 * A 2D Point (x, y) only
 * Points are ordered lexicographically (thus implementing the Comparable interface)
 * 
 * @author Nick Celiberti
 * @author C. Alvin
 */
public class PlanarGraphPoint implements Comparable<PlanarGraphPoint>
{
    public static final int NUM_SEGS_TO_APPROX_ARC = 0;
    private static int CURRENT_ID = 0;
    public static final PlanarGraphPoint ORIGIN;

    static
    {
        ORIGIN = new PlanarGraphPoint("origin", 0, 0);
    }

    private double X;
    public double getX() { return this.X; }
    private double Y; 
    public double getY() { return this.Y; }

    private int ID; 
    /**
     * Get the unique identifier for this point.
     * @return 
     */
    public int getID() { return this.ID; }

    public String name; 
    /**
     * Create a new Point with the specified coordinates.
     * @author Nick Celiberti
     * @param n The name of the point. (Assigned by the UI)
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    public PlanarGraphPoint(String n, double x, double y)
    {
        this.ID = CURRENT_ID++;
        name = n != null ? n : "";
        this.X = x;
        this.Y = y;
    }

    /**
     * Expects a radian angle measurement
     * @param center Center
     * @param radius Radius
     * @param angle Angle
     * @return Point
     */
    public static PlanarGraphPoint GetPointFromAngle(PlanarGraphPoint center, double radius, double angle)
    {
        return new PlanarGraphPoint("", center.X + radius * Math.cos(angle), center.Y + radius * Math.sin(angle));
    }

    /**
     * Assumes our points represent vectors in standard position.
     * @param thisPoint 
     * @param thatPoint
     * @return Cross Product
     */
    public static double CrossProduct(PlanarGraphPoint thisPoint, PlanarGraphPoint thatPoint)
    {
        return thisPoint.X * thatPoint.Y - thisPoint.Y * thatPoint.X;
    }

    /*
     * @param M -- a point
     * @param A -- a point
     * @param B -- a point
     * @return true if the three points are (1) collinear and (2) M is between A and B
     *                                     A-------------M---------B
     * Note: returns true if M is one of the endpoints
     */
    public static boolean Between(PlanarGraphPoint M, PlanarGraphPoint A, PlanarGraphPoint B)
    {
        return Utilities.equalDoubles(PlanarGraphPoint.calcDistance(A, M) + PlanarGraphPoint.calcDistance(M, B), PlanarGraphPoint.calcDistance(A, B));
    }
    
    /**
     * Assumes our points represent vectors in standard position.
     * @param first
     * @param second
     * @return
     */
    public static boolean OppositeVectors(PlanarGraphPoint first, PlanarGraphPoint second)
    {
        PlanarGraphPoint origin = new PlanarGraphPoint("", 0, 0);

        return Between(origin, first, second);
    }

    /**
     * Angle measure (in degrees) between two vectors in standard position.
     * @param thisPoint
     * @param thatPoint
     * @return Angle measure (in degrees) between two vectors in standard position.
     */
    public static double AngleBetween(PlanarGraphPoint thisPoint, PlanarGraphPoint thatPoint)
    {
        PlanarGraphPoint origin = new PlanarGraphPoint("", 0, 0);

        if (Between(origin, thisPoint, thatPoint)) return 180;
        if (Between(thisPoint, origin, thatPoint)) return 0;
        if (Between(thatPoint, origin, thisPoint)) return 0;

        return findMeasure(thisPoint, origin, thatPoint);
    }

    /**
     * Find the measure of the angle (in radians) specified by the three points.
     * @param a A point defining the angle.
     * @param b A point defining the angle. This is the point the angle is actually at.
     * @param c A point defining the angle.
     * @return The measure of the angle (in radians) specified by the three points.
     * Uses Law of Cosines to compute angle
     */
    private static double findMeasure(PlanarGraphPoint a, PlanarGraphPoint b, PlanarGraphPoint c)
    {
        double v1x = a.getX() - b.getX();
        double v1y = a.getY() - b.getY();
        double v2x = c.getX() - b.getX();
        double v2y = c.getY() - b.getY();
        double dotProd = v1x * v2x + v1y * v2y;
        double cosAngle = dotProd / (PlanarGraphPoint.calcDistance(a, b) * PlanarGraphPoint.calcDistance(b, c));

        // Avoid minor calculation issues and retarget the given value to specific angles. 
        // 0 or 180 degrees
        if (Utilities.equalDoubles(Math.abs(cosAngle), 1) || Utilities.equalDoubles(Math.abs(cosAngle), -1))
        {
            cosAngle = cosAngle < 0 ? -1 : 1;
        }

        // 90 degrees
        if (Utilities.equalDoubles(cosAngle, 0)) cosAngle = 0;

        return Math.acos(cosAngle);
    }
    
    /**
     * 
     * @param A Point A
     * @param B Point B
     * @param C Point C
     * @return True if angle is Counter-Clockwise : False if angle is Clockwise.
     */
    public static boolean CounterClockwise(PlanarGraphPoint A, PlanarGraphPoint B, PlanarGraphPoint C)
    {
        // Define two vectors: vect1: A----->B
        //                     vect2: B----->C
        // Cross product vect1 and vect2. 
        // If the result is negative, the sequence A-->B-->C is Counter-clockwise. 
        // If the result is positive, the sequence A-->B-->C is clockwise.
        PlanarGraphPoint vect1 = PlanarGraphPoint.MakeVector(A, B);
        PlanarGraphPoint vect2 = PlanarGraphPoint.MakeVector(B, C);

        return PlanarGraphPoint.CrossProduct(vect1, vect2) < 0;
    }

    /**
     * Calculates the magnitude.
     * @param vector 
     * @return Magnitude of given vector
     */
    public static double Magnitude(PlanarGraphPoint vector) { return Math.sqrt(Math.pow(vector.X, 2) + Math.pow(vector.Y, 2)); }
    /**
     * Creates a vector
     * @param tail 
     * @param head
     * @return A point representing the vector
     */
    public static PlanarGraphPoint MakeVector(PlanarGraphPoint tail, PlanarGraphPoint head) { return new PlanarGraphPoint("", head.X - tail.X, head.Y - tail.Y); }
    /**
     * Finds the opposite vector
     * @param v vector
     * @return Opposite vector of v
     */
    public static PlanarGraphPoint GetOppositeVector(PlanarGraphPoint v) { return new PlanarGraphPoint("", -v.X, -v.Y); }

    /**
     * Normalize a vector
     * @param vector assumed unnormalized vector
     * @return Normalized vector
     */
    public static PlanarGraphPoint Normalize(PlanarGraphPoint vector)
    {
        double magnitude = PlanarGraphPoint.Magnitude(vector);
        return new PlanarGraphPoint("", vector.X / magnitude, vector.Y / magnitude);
    }

    /**
     * Scalar Multiply a vector
     * @param vector 
     * @param scalar
     * @return vector that has been multiplied.
     */
    public static PlanarGraphPoint ScalarMultiply(PlanarGraphPoint vector, double scalar) { return new PlanarGraphPoint("", scalar * vector.X, scalar * vector.Y); }

    //    // CTA: Used? NC: Not that I'm aware of
    //    public int Quadrant()
    //    {
    //        if (backend.utilities.math.Utilities.doubleEquals(X, 0) && backend.utilities.math.Utilities.doubleEquals(Y, 0)) return 0;
    //        if (backend.utilities.math.Utilities.greaterThan(X, 0) && backend.utilities.math.Utilities.greaterThan(Y, 0)) return 1;
    //        if (backend.utilities.math.Utilities.doubleEquals(X, 0) && backend.utilities.math.Utilities.greaterThan(Y, 0)) return 12;
    //        if (backend.utilities.math.Utilities.lessThan(X, 0) && backend.utilities.math.Utilities.greaterThan(Y, 0)) return 2;
    //        if (backend.utilities.math.Utilities.lessThan(X, 0) && backend.utilities.math.Utilities.doubleEquals(Y, 0)) return 23;
    //        if (backend.utilities.math.Utilities.lessThan(X, 0) && backend.utilities.math.Utilities.doubleEquals(Y, 0)) return 3;
    //        if (backend.utilities.math.Utilities.doubleEquals(X, 0) && backend.utilities.math.Utilities.lessThan(Y, 0)) return 34;
    //        if (backend.utilities.math.Utilities.greaterThan(X, 0) && backend.utilities.math.Utilities.lessThan(Y, 0)) return 4;
    //        if (backend.utilities.math.Utilities.greaterThan(X, 0) && backend.utilities.math.Utilities.doubleEquals(Y, 0)) return 41;
    //
    //        return -1;
    //    }

    /**
     * Returns a degree angle measurement between [0, 360]. 
     * @param center
     * @param other
     * @return degree angle measurement between [0, 360]
     */
    public static double GetDegreeStandardAngleWithCenter(PlanarGraphPoint center, PlanarGraphPoint other)
    {
        return GetRadianStandardAngleWithCenter(center, other) / Math.PI * 180;
    }

    /**
     * Returns a radian angle measurement between [0, 2PI]. 
     * @param center
     * @param other
     * @return Radian angle measurement between [0, 2PI]. 
     */
    public static double GetRadianStandardAngleWithCenter(PlanarGraphPoint center, PlanarGraphPoint other)
    {
        PlanarGraphPoint stdVector = new PlanarGraphPoint("", other.X - center.X, other.Y - center.Y);

        double angle = Math.atan2(stdVector.Y, stdVector.X);

        return angle < 0 ? angle + 2 * Math.PI : angle;
    }

    //    /**
    //     * Maintain a public repository of all segment objects in the figure.
    //     */
    //    public static void clear() { figurePoints.clear(); }
    //    public static ArrayList<Point> figurePoints = new ArrayList<Point>();
    //
    //    public static void Record(GroundedClause clause)
    //    {
    //        // Record uniquely? For right angles, etc?
    //        if (clause instanceof Point) figurePoints.add((Point)clause);
    //    }
    //
    //    public static Point GetFigurePoint(Point candPoint)
    //    {
    //        for (Point p : figurePoints)
    //        {
    //            if (p.structurallyEquals(candPoint)) return p;
    //        }
    //
    //        return null;
    //    }

    /**
     * Calculates the distance between 2 points
     * @param p1 Point 1
     * @param p2 Point 2
     * @return The distance between points
     */
    public static double calcDistance(PlanarGraphPoint p1, PlanarGraphPoint p2)
    {
        return Math.sqrt(Math.pow(p2.X - p1.X, 2) + Math.pow(p2.Y - p1.Y, 2));
    }

    /**
     * Determines if value is between a and b
     * @param val Value to test
     * @param a Bound 1
     * @param b Bound 2
     * @return True if a <= val <= b or b<= val <= a : False if (val < a and val < b) or (a < val and b < val)
     */
    public static boolean Between(double val, double a, double b)
    {
        if (a >= val && val <= b) return true;
        if (b >= val && val <= a) return true;

        return false;
    }
    
    @Override
    public int hashCode()
    {
        return new Double(X).hashCode() + new Double(Y).hashCode();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        
        if (!(obj instanceof PlanarGraphPoint)) return false;
        
        PlanarGraphPoint that = (PlanarGraphPoint)obj;
        
        return Utilities.equalDoubles(this.X, that.X) && Utilities.equalDoubles(this.Y, that.Y);
    }

    @Override
    public String toString()
    {
        if (X == (int) X && Y == (int) Y)
        {
            return "Point(" + name + ")(" + X + ", " + Y + ")";
        }
        return "Point(" + name + ")(" + String.format("%1$.6f", X) + ", " + String.format("%1$.6f", Y) + ")"; 
    }

    /**
     * 
     * @param p1 Point 1
     * @param p2 Point 2
     * @return Lexicographically: p1 < p2 return -1 : p1 == p2 return 0 : p1 > p2 return 1
     *         Order of X-coordinates first; order of Y-coordinates second
     */
    public static int LexicographicOrdering(PlanarGraphPoint p1, PlanarGraphPoint p2)
    {
        // Epsilon-based equality of both coordinates
        if (Utilities.equalDoubles(p1.X, p2.X) &&
            Utilities.equalDoubles(p1.Y, p2.Y)) return 0;

        // X's first
        if (p1.X < p2.X) return -1;

        if (p1.X > p2.X) return 1;

        // Y's second
        if (p1.Y < p2.Y) return -1;

        if (p1.Y > p2.Y) return 1;

        // Equal points: this should NOT happen
        return 0;
    }

    @Override
    public int compareTo(PlanarGraphPoint that)
    {
        if (that == null) return 1;

        return PlanarGraphPoint.LexicographicOrdering(this, that);
    }
}
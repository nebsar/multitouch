/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.telekom.laboratories.multitouch;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
class InertiaTensor2D 
{
            
    private final double massX;
    private final double massY;

    private double mXX, mXY, mYY; // = 0;

    public InertiaTensor2D(final double massX, final double massY) 
    throws IllegalArgumentException 
    {
        this.massX = massX;
        this.massY = massY;
    }


    public void integrate(double x, double y, double density) 
    {
        // 2D moment of inertia for  a continuous mass distribution 
        //      := integral ( integral {  y^2, -x*y },
        //                             { -x*y, x^2  }} dx ) dy
        // see also: 
        // http://scienceworld.wolfram.com/physics/MomentofInertia.html

        final double dX = x-massX, dY = y-massY;
        mXX += density * dX*dX;
        mXY += density * dX*dY;
        mYY += density * dY*dY;                
    }

    public void integrate(double x, double y)
    {
        // 2D moment of inertia for  a continuous mass distribution 
        //      := integral ( integral {  y^2, -x*y },
        //                             { -x*y, x^2  }} dx ) dy
        // see also: 
        // http://scienceworld.wolfram.com/physics/MomentofInertia.html

        final double dX = x-massX, dY = y-massY;
        mXX += dX*dX;
        mXY += dX*dY;
        mYY += dY*dY;                
    }

    public double orientation()
    {
        // Inertia Tensor as 2x2 Matrix:
        //
        // let m = {{ mXX, mXY },
        //          { mXY, mYY }} in
        //

        //  EigenValues
        //
        // Solve:
        //  {{ m11 - value,     m12     },   
        //   {     m21    , m22 - value }}  * { vector_x, vector_y } = { 0, 0 }
        //
        // Solution:
        // value_(1,2) = 0.5 * ( a ± b )
        // where a = ( m11 + m22 )
        //       b = sqrt( ( 4 * m12 * m21 ) + c^2 )
        //       whrere c = ( m11 - m22 )

        final double c = ( mXX - mYY );
        final double b = Math.sqrt( ( 4*mXY*mXY ) + ( c*c ) );
        final double a = ( mXX + mXY );

        final double[] values = { 0.5*(a+b), 0.5*(a-b) }; // 1st + 2nd eigen-values

        // EigenVectors
        //
        // Solve:
        // {{ ( m11-value ) * vector_x,                        m12        },
        //  {               m21               ,  ( m22-value ) * vector_y }} = { 0, 0};
        //
        // Note: 
        // The vector elements are not unique, the define a ray,
        // because: if M*vector = value*vector, so does value' := x*value, x elem |R;
        // Further, one line/equation should sufficient, 
        // we chose (1) for vector_1 and (2) for vector_2                
        //
        // Solution: 
        // vector_(i) = { ( m11 - values[i] ), - m12 };
        // or
        // vector_(i) = { m12, - ( m22 - values[i] ) };

        final double[][] vectors = { { mXX - values[0], -mXY }, // 1st eigen-vector
                                     { mXY,  values[1]-mYY } }; // 2nd eigen-vector

        // Unify directions and length of the eigen-vectors                
        for(int i=0; i<vectors.length; i++) {
            final double[] vector = vectors[i];

            // direction
            if(vector[0] < 0.0) {
                // negate
                vector[0] = -vector[0];
                vector[1] = -vector[1];
            }   

            // length
            final double length = Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1]);
            if(length >= 0.000001) {
                // normalize
                final double invLength = 1.0 / length;
                vector[0] *= invLength;
                vector[1] *= invLength;
            }
        }


        // Compute orientation;
        //
        final double[] rotation = ( Math.abs(values[0]) > Math.abs(values[1]) ) 
                                ? vectors[0] : vectors[1];

        // Note: subtract 90DEG: '- 0.5*PI'
        final double orientation = Math.atan2(rotation[0], rotation[1]) - 0.5*Math.PI; 

        return orientation;
    }
}

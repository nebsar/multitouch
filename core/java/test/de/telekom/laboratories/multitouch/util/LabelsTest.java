/*
 * Copyright (C) 2007 Deutsche Telekom AG Laboratories
 *
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.telekom.laboratories.multitouch.util;

import java.util.Arrays;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import static org.junit.Assert.*;




/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class LabelsTest {
    
    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    
    private final static int[][] image =
    {
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 },
        {  0,  1,  0,  1,  0,  2,  2,  2,  0,  3,  3,  3,  0 },
        {  0,  1,  0,  1,  0,  2,  0,  0,  0,  3,  0,  3,  0 },
        {  0,  1,  0,  1,  0,  2,  2,  2,  0,  3,  3,  3,  0 },
        {  0,  1,  0,  1,  0,  0,  0,  2,  0,  3,  0,  3,  0 },
        {  0,  1,  1,  1,  0,  2,  2,  2,  0,  3,  0,  3,  0 },
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 },  
    };
    
    private final static int[][] bounds =
    {
        {  1,  1,  3,  5 },
        {  5,  1,  7,  5 },
        {  9,  1, 11,  5 },
    };    
    
    // </editor-fold> 
        
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    public LabelsTest() {
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    @Test
    public void testCount() {

        final int[][] copy = image.clone();
        for(int i=0; i<copy.length; i++) {
            copy[i] = image[i].clone();
        }
        
        final Labels labels = new Labels(copy);
        assertEquals(labels.getCount(), 0);        
        assertEquals(labels.count(), 3);
        assertEquals(labels.getCount(), 3);
        assertTrue(
            "Update doesn't behave as identity for a neutral image", 
            Arrays.deepEquals(image, copy)
        );
    }
    
    @Test
    public void testBounds() {

        final int[][] copy = image.clone();
        for(int i=0; i<copy.length; i++) {
            copy[i] = image[i].clone();
        }
        
        final Labels labels = new Labels(copy);
        assertEquals(labels.getCount(), 0);
        final int[][] b = labels.bounds();
        Arrays.deepEquals(bounds, b);        
        assertEquals(labels.getCount(), bounds.length);
        assertTrue(
            "Update doesn't behave as identity for a neutral image", 
            Arrays.deepEquals(image, copy)
        );
    }    
    
    private static void print(int[][] image) {
        for(int[] line : image) {
            for(int pixel : line) {
                System.out.printf("%4d ", pixel);
            }
            System.out.println();
        }
        System.out.println();
    }   
    
    public static junit.framework.Test suite() { 
        return new JUnit4TestAdapter(LabelsTest.class); 
    }    
    
    // </editor-fold>
}

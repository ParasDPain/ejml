/*
 * Copyright (c) 2009-2010, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * EJML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * EJML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EJML.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ejml.alg.dense.misc;

import org.ejml.data.DenseMatrix64F;


/**
 * Low level transpose algorithms.  No sanity checks are performed.
 *
 * @author Peter Abeles
 */
public class TransposeAlgs {

    /**
     * In-place transpose for a square matrix.  The most efficient algorithm but can
     * only be used on square matrices.
     *
     * @param mat The matrix that is transposed in-place.  Modified.
     */
    public static void square( DenseMatrix64F mat )
    {
        final double data[] = mat.data;

        for( int i = 0; i < mat.numRows; i++ ) {
            int index = i*mat.numCols+i+1;
            for( int j = i+1; j < mat.numCols; j++ ) {
                int otherIndex = j*mat.numCols+i;
                double val = data[index];
                data[index] = data[otherIndex];
                data[otherIndex] = val;
                index++;
            }
        }
    }

    /**
     * Performs a transpose across block sub-matrices.  Reduces
     * the number of cache misses on larger matrices.
     *
     * @param A Original matrix.  Not modified.
     * @param A_tran Transposed matrix.  Modified.
     * @param blockLength Length of a block.
     */
    public static void block( DenseMatrix64F A , DenseMatrix64F A_tran ,
                              final int blockLength )
    {
        for( int i = 0; i < A_tran.numRows; i += blockLength ) {
            int blockHeight = Math.min( blockLength , A_tran.numRows - i);

            for( int j = 0; j < A_tran.numCols; j += blockLength ) {
                int blockWidth = Math.min( blockLength , A_tran.numCols - j);

                int indexDst = i*A_tran.numCols + j;
                int indexSrc = j*A.numCols + i;

                for( int l = 0; l < blockWidth; l++ ) {
                    int rowSrc = indexSrc + l*A.numCols;
                    int rowDst = indexDst + l;
                    for( int k = 0; k < blockHeight; k++ , rowDst += A_tran.numCols ) {
                        A_tran.data[ rowDst ] = A.data[rowSrc++];
                    }
                }
            }
        }
    }

    /**
     * A straight forward transpose.  Good for small non-square matrices.
     *
     * @param A Original matrix.  Not modified.
     * @param A_tran Transposed matrix.  Modified.
     */
    public static void standard( DenseMatrix64F A, DenseMatrix64F A_tran)
    {
        final double rdata[] = A_tran.data;
        final double data[] = A.data;

        int index = 0;
        for( int i = 0; i < A_tran.numRows; i++ ) {
            int index2 = i;

            for( int j = 0; j < A_tran.numCols; j++ ) {
                rdata[index++] = data[index2];
                index2 += A.numCols;
            }
        }
    }
}

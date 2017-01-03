/*
 * Copyright 2006-2016 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.mcLeanRegression.core;

import Jama.Matrix;
import org.cirdles.mcLeanRegression.algorithms.LevenbergMarquardt;

/**
 *
 * @author James F. Bowring
 *
 * Per Noah Mclean's MatLab code: Linear regression in multiple dimensions using
 * Levenberg-Marquardt optimization
 *
 * % INPUTS: % 'dataunct' is the dataset, formatted as in the provided Excel
 * spreadhsheet: % Each measured data point is placed in a separate row. The
 * first 2*n % columns are the measured data points, each followed by % its
 * one-sigma uncertainty (absolute or percent). The final n(n-1)/2 % columns are
 * the correlation coefficients. If the variables are listed in % alphabetical
 * order, then these correlation coefficients would be in % alphabetical order
 * as well (e.g. rho-xz comes before rho-yz). % For 2D data, the columns of
 * dataunct are (x-y) % x,	±1sigmax,	y,	±1sigmay,	rho-xy % For 3D data, the
 * columns of dataunct are (x-y-z) % x,	±1sigmax,	y,	±1sigmay,	z,	±1sigmaz,
 * rho-xy,	rho-xz,	rho-yz % For 4D data, the columns of dataunct are (x-y-z-w) %
 * x,	±1sigmax,	y,	±1sigmay,	z,	±1sigmaz,	w,	±1sigmaw,	rho-xy,	rho-xz,	rho-xw,
 * rho-yz,	rho-yw,	rho-zw % For 5D data, the columns of dataunct are (o-p-q-r-s)
 * % o,	±1sigmao,	p,	±1sigmap,	q,	±1sigmaq,	r,	±1sigmar,	s,	±1sigmas,	rho-op
 * rho-oq	rho-or	rho-os	rho-pq	rho-pr	rho-ps	rho-qr	rho-qs	rho-rs % where rho-xy
 * is the correlation coefficient between the uncertainties in x and y
 *
 * % 'skipv' is a vector of length n where '1' indicates inclusion in % the
 * calculation, and '0' indicates rejection. % 'a1' and 'v1' are the assumed
 * values for the first components of the % two vectors required to describe a
 * line in multiple dimensions: a point % on the line, and the direction vector,
 * respectively. % 'abspct' has a value of 1 if the input uncertainties are
 * absolute, and % a value of 2 if the input uncertainties are expressed as
 * percent.
 *
 * % OUTPUTS: % 'a' is the point on the line with first component a1 specified
 * above % 'a2s' contains the two-sigma uncertainties in the free components of
 * a % 'v' is the direction vector of the line with first component v1 as input
 * % 'v2s' contains the two-sigma uncertainties in the free components of v %
 * 'Sav' contains the covariance matrix for the free components of a and v %
 * 'MSWD' is the mean of the squared weighted deviates (reduced chi-square) %
 * 'n' is the number of analyses included in the calculation, = sum(skipv)
 */
public class McLeanRegressionLineFitEngine implements McLeanRegressionLineFitEngineInterface {

    private Matrix data;
    private Matrix unct;
    private Matrix assump;
    private Matrix initAV;
    private Matrix[] covMats;

    private McLeanRegressionLineFitEngine() {
        this(new Matrix(0,0), new Matrix(0,0));
    }

    public McLeanRegressionLineFitEngine(Matrix data, Matrix unct) {
        this.data = data;
        this.unct = unct;
        this.assump = new Matrix(0, 0);
        this.initAV = new Matrix(0, 0);
        this.covMats = new Matrix[0];
    }

    @Override
    public void initializeRegressesionParams() {
        //initialize regression params with OLS regression on each marginal 
        int dimensionLessOne = data.getColumnDimension() - 1;
        int rows = data.getRowDimension();

        Matrix a0 = new Matrix(dimensionLessOne, 1);
        Matrix v0 = new Matrix(dimensionLessOne, 1);
        double a1 = 0.0;
        double v1 = 1.0;
        assump = new Matrix(new double[]{a1, v1}, 1);

        for (int dimI = 0; dimI < dimensionLessOne; dimI++) {
            Matrix lsi = new Matrix(rows, 2, 1.0);
            lsi.setMatrix(0, rows - 1, new int[]{1}, data.getMatrix(0, rows - 1, 0, 0));
            Matrix lsiDiv = lsi.solve(data.getMatrix(0, rows - 1, dimI + 1, dimI + 1));
            v0.set(dimI, 0, lsiDiv.get(1, 0) * v1);
            a0.set(dimI, 0, lsiDiv.get(0, 0) + v0.get(dimI, 0) / v1 * a1);
        }

        initAV = new Matrix(1, dimensionLessOne * 2);
        initAV.setMatrix(0, 0, 0, dimensionLessOne - 1, a0.transpose());
        initAV.setMatrix(0, 0, dimensionLessOne, dimensionLessOne * 2 - 1, v0.transpose());
        initAV = initAV.transpose();
    }

    @Override
    public void initializeUnertaintyCovarianceMatrices() {

        int rowCount = unct.getRowDimension();
        int dimensionCount = data.getColumnDimension();

        // covMats is an array of covMatrices dimensionCount x dimensionCount - one for each data point or row
        covMats = new Matrix[rowCount];
        // spots to place covariance terms
        Matrix logicalIndices = Matrix.identity(dimensionCount, dimensionCount);
        // fill bottom triangle
        for (int row = 0; row < dimensionCount; row++) {
            for (int col = 0; col < row; col++) {
                logicalIndices.set(row, col, 1);
            }
        }

        // just to hold form of d-by-d matrix
        Matrix halfCov = new Matrix(dimensionCount, dimensionCount, 0.0);
        // number of elements in lower triangular covariance matrix
        int trinum_d = dimensionCount * (dimensionCount + 1) / 2;

        // determine the indices of the variance terms in the bottom triangle represented as an array
        // thus for dimensionCount = 5, we get {14, 9, 5, 2, 0}
        int[] indicesOfVarianceTerms = new int[dimensionCount];
        for (int i = dimensionCount; i > 0; i--) {
            int indexOfDiagonalMember = i * (i + 1) / 2 - 1;
            indicesOfVarianceTerms[i - 1] = trinum_d - indexOfDiagonalMember - 1;
        }

        for (int i = 0; i < rowCount; i++) {
            // vectors of sigmas and rhos
            Matrix sigmaVectorI = new Matrix(1, dimensionCount);
            sigmaVectorI.setMatrix(0, 0, 0, dimensionCount - 1, unct.getMatrix(i, i, 0, dimensionCount - 1));

            Matrix rhoVectorI = new Matrix(1, unct.getColumnDimension() - dimensionCount);
            rhoVectorI.setMatrix(0, 0, 0, unct.getColumnDimension() - dimensionCount - 1, unct.getMatrix(i, i, dimensionCount, unct.getColumnDimension() - 1));

            // for indices of covariance matrix
            Matrix v1I = new Matrix(1, trinum_d);
            Matrix v2I = new Matrix(1, trinum_d);
            Matrix v3I = new Matrix(1, trinum_d);
            Matrix covVec = new Matrix(1, trinum_d);

            // half the variance so that you can add transpose
            for (int j = 0; j < indicesOfVarianceTerms.length; j++) {
                v1I.set(0, indicesOfVarianceTerms[j], 0.5);
            }
            // fill in remaining cells with entries from rhoVectorI
            int indexInRhoVectorI = 0;
            for (int j = 0; j < v1I.getColumnDimension(); j++) {
                if (v1I.get(0, j) == 0.0) {
                    v1I.set(0, j, rhoVectorI.get(0, indexInRhoVectorI));
                    indexInRhoVectorI++;
                }
            }

            // populate v2I with values from sigmaVector using repeated indices per pattern for dim = 5: 0,0,0,0,0,1,1,1,1,2,2,2,3,3,4
            int index = 0;
            for (int dim = dimensionCount; dim > 0; dim--) {
                for (int k = 0; k < dim; k++) {
                    v2I.set(0, index, sigmaVectorI.get(0, dimensionCount - dim));
                    index++;
                }
            }

            // populate v3I with values from sigmaVector using repeated indices per pattern for dim = 5: 0,1,2,3,4,1,2,3,4,2,3,4,3,4,4
            index = 0;
            for (int dim = dimensionCount; dim > 0; dim--) {
                for (int k = dimensionCount - dim; k < dimensionCount; k++) {
                    v3I.set(0, index, sigmaVectorI.get(0, k));
                    index++;
                }
            }

            // rho_xy * sigma_x * sigma_y
            for (int j = 0; j < trinum_d; j++) {
                covVec.set(0, j, v1I.get(0, j) * v2I.get(0, j) * v3I.get(0, j));
            }

            // place in lower triangular matrix
            // each value of covVec is placed in halfCov where logicalIndices is 1 (true) in column-first manner
            index = 0;
            for (int col = 0; col < dimensionCount; col++) {
                for (int row = 0; row < dimensionCount; row++) {
                    if (logicalIndices.get(row, col) == 1) {
                        halfCov.set(row, col, covVec.get(0, index));
                        index++;
                    }
                }
            }

            // duplicate to above diagonal and add together
            covMats[i] = halfCov.plus(halfCov.transpose());
        }
    }

    @Override
    public McLeanRegressionLineInterface fitLine(){
        initializeRegressesionParams();

        initializeUnertaintyCovarianceMatrices();

        LevenbergMarquardt levenbergMarquardt = new LevenbergMarquardt();

        // % maximum iterations before solver quits
        int maxIterations = 10000;
        //% tolerance of chi-square
        double chiTolerance = 1e-12;
        // % initial L-M damping parameter
        double lambda0 = 1000;
        // %set this to true to see results of each L-M iteration
        boolean verbose = false;

        McLeanRegressionLineInterface fitLine = 
                levenbergMarquardt.LevenbergMarquardt_LinearRegression_v2(this, maxIterations, chiTolerance, lambda0, verbose);
        
        return fitLine;
    }
    
    @Override
    public String showSummaryOfFIt(){
        String retVal = "Summary of line fit ...";
        
        
        return retVal;
    }

    /**
     * @return the data
     */
    @Override
    public Matrix getData() {
        return data;
    }

    /**
     * @return the assump
     */
    @Override
    public Matrix getAssump() {
        return assump;
    }

    /**
     * @return the initAV
     */
    @Override
    public Matrix getInitAV() {
        return initAV;
    }

    /**
     * @return the covMats
     */
    @Override
    public Matrix[] getCovMats() {
        return covMats.clone();
    }
}

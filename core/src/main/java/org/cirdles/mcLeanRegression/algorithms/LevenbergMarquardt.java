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
package org.cirdles.mcLeanRegression.algorithms;

import Jama.Matrix;

/**
 *
 * @author James F. Bowring
 */
public class LevenbergMarquardt {

    private Matrix G;
    private Matrix H;

    public void LevenbergMarquardt_LinearRegression_v2(//
            McLeanRegressionSetup mcLeanRegressionSetup,
            int maxIterations,
            double chiTolerance,
            double lambda0,
            boolean verbose) {

        Matrix initAV = mcLeanRegressionSetup.getInitAV();
        Matrix data = mcLeanRegressionSetup.getData();
        Matrix[] covMats = mcLeanRegressionSetup.getCovMats();
        Matrix assump = mcLeanRegressionSetup.getAssump();

        double lambda = lambda0;
        int dimensionCount = data.getColumnDimension();

        Matrix a = new Matrix(dimensionCount, 1);
        a.set(0, 0, assump.get(0, 0));
        a.setMatrix(1, dimensionCount - 1, 0, 0, initAV.getMatrix(0, dimensionCount - 1, 0, 0));

        Matrix v = new Matrix(dimensionCount, 1);
        v.set(0, 0, assump.get(0, 1));
        v.setMatrix(1, dimensionCount - 1, 0, 0, initAV.getMatrix(dimensionCount - 1, initAV.getRowDimension() - 1, 0, 0));

        int rowCount = covMats.length;

        double L = calculateLogLikelihood(a, v, data, covMats);
        if (verbose) {
            System.out.println("  L0 = " + L);
        }

        calculateGradientAndHessianH(a, v, data, covMats);

        Matrix Sav = new Matrix(0,0);
        double MSWD = 0;
        double BIC = 0;

        int iterations = 0;

        while (iterations < maxIterations) {

            // calculate update
            Matrix HDiagLambda = new Matrix(dimensionCount * 2 - 2, dimensionCount * 2 - 2);
            for (int i = 0; i < dimensionCount * 2 - 2; i++) {
                HDiagLambda.set(i, i, H.get(i, i) * lambda);
            }
            //  -(H.plus(HDiagLambda)) \ G; 
            Matrix h = H.plus(HDiagLambda).inverse().times(G).times(-1.0);

            Matrix avNew = initAV.plus(h);
            // anew = [assump(1); avnew(1:(d-1))];  %intercept with x=0 axis/(hyper)plane
            Matrix aNew = new Matrix(dimensionCount, 1);
            aNew.set(0, 0, assump.get(0, 0));
            aNew.setMatrix(1, dimensionCount - 1, 0, 0, avNew.getMatrix(0, dimensionCount - 1, 0, 0));

            Matrix vNew = new Matrix(dimensionCount, 1);
            // vnew = [assump(2); avnew(d:end)];  %v is direction vector (slope when v1=1)
            vNew.set(0, 0, assump.get(0, 1));
            vNew.setMatrix(1, dimensionCount - 1, 0, 0, avNew.getMatrix(dimensionCount - 1, dimensionCount * 2 - 2 - 1, 0, 0));

            double Lnew = calculateLogLikelihood(aNew, vNew, data, covMats);
            iterations++;

            if (Lnew < L) // if things got worse, try again
            {
                if (verbose) {
                    System.out.println("Lnew = " + Lnew + "  rejected");
                }
                lambda *= 10;
            } else {
                if (StrictMath.abs(1.0 - L / Lnew) < chiTolerance) {
                    // things got better and solved
                    a.setMatrix(1, dimensionCount - 1, 0, 0, avNew.getMatrix(0, dimensionCount - 1, 0, 0));
                    v.setMatrix(1, dimensionCount - 1, 0, 0, avNew.getMatrix(dimensionCount - 1, initAV.getRowDimension() - 1, 0, 0));
                    calculateGradientAndHessianH(a, v, data, covMats);
                    Sav = H.inverse().times(-1.0);
                    L = Lnew;
                    MSWD = -2 * L / ((dimensionCount - 1) * (rowCount - 2));
                    BIC = -2 * L + (2 * dimensionCount - 2) * StrictMath.log(rowCount);

                    if (verbose) {
                        System.out.println("Lnew = " + L + "  accepted and solved in " + iterations + "  iterations");
                    }
                    iterations = maxIterations;
                } else {
                    //things got better but not solved, accept values, lambda /= 10
                    if (verbose) {
                        System.out.println("Lnew = " + Lnew + "  accepted and go again");
                    }

                    lambda /= 10;
                    initAV = avNew;
                    a.setMatrix(1, dimensionCount - 1, 0, 0, initAV.getMatrix(0, dimensionCount - 1, 0, 0));
                    v.setMatrix(1, dimensionCount - 1, 0, 0, initAV.getMatrix(dimensionCount - 1, initAV.getRowDimension() - 1, 0, 0));
                    L = Lnew;
                    calculateGradientAndHessianH(a, v, data, covMats);
                }
            } // things got better
        } //while

        //a, v, Sav, L, MSWD, BIC
        a. print(10,5);
        v. print(10,5);
        Sav. print(10,5);
        System.out.println ("L = " + L + "  MSWD = " + MSWD + "   BIC = " + BIC);
        
                
    }

    /**
     * Calculate gradient and Hessian for log-Likelihood.
     *
     * @param a
     * @param v
     * @param data
     * @param covMats
     */
    private void calculateGradientAndHessianH(Matrix a, Matrix v, Matrix data, Matrix[] covMats) {

        int dimensionCount = data.getColumnDimension();

        Matrix dlda = new Matrix(1, dimensionCount);
        Matrix dldv = new Matrix(dimensionCount, 1);
        Matrix d2LdaaT = new Matrix(dimensionCount, dimensionCount);
        Matrix d2LdvvT = new Matrix(dimensionCount, dimensionCount);
        Matrix d2LdavT = new Matrix(dimensionCount, dimensionCount);

        for (int i = 0; i < data.getRowDimension(); i++) {

            Matrix xi = data.getMatrix(i, i, 0, dimensionCount - 1).transpose().plus(a.times(-1.0));
            Matrix si = covMats[i];

            // common terms in derivatives
            Matrix invsi = si.inverse();
            Matrix siv = invsi.times(v);
            Matrix vTsi = si.transpose().inverse().times(v).transpose(); // v'/si;
            Matrix xiTsi = si.transpose().inverse().times(xi).transpose(); // xi'/si;
            Matrix sixi = si.inverse().times(xi);  // si\xi;
            double xiTsiv = xiTsi.times(v).get(0, 0); //scalar
            double vTsiv = vTsi.times(v).get(0, 0); // scalar
            double vTsixi = vTsi.times(xi).get(0, 0); // scalar

            //  dlda = dlda + xi'*(invsi - (siv*vTsi)/vTsiv);
            dlda.plusEquals(xi.transpose().times(invsi.plus(siv.times(vTsi).times(1.0 / vTsiv).times(-1.0))));
            //  dldv = dldv + (vTsiv * vTsixi * sixi - vTsixi^2*siv)/(vTsiv^2);
            dldv.plusEquals(sixi.times(vTsiv * vTsixi).plus(siv.times(-1.0 * vTsixi * vTsixi)).times(1.0 / (vTsiv * vTsiv)));
            //   d2LdaaT = d2LdaaT + -(invsi - ( siv * vTsi )/vTsiv);
            d2LdaaT.plusEquals(invsi.plus((siv.times(vTsi).times(-1.0 / vTsiv))).times(-1.0));
            //  d2LdavT = d2LdavT + -( vTsiv * ( siv * xiTsi + xiTsiv * invsi ) - 2*xiTsiv * siv * vTsi ) /vTsiv^2;
            d2LdavT.plusEquals(siv.times(xiTsi).plus(invsi.times(xiTsiv)).times(vTsiv).plus(siv.times(vTsi).times(-2.0 * xiTsiv)).times(-1.0 / (vTsiv * vTsiv)));

            //  d2LdvvT = d2LdvvT + (vTsiv^2*   (2 * vTsixi * sixi * vTsi + vTsiv * sixi * xiTsi - vTsixi^2*invsi - 2*vTsixi * siv * xiTsi )    -4* vTsiv * ( vTsiv * vTsixi * sixi - vTsixi^2* siv) * vTsi       )/vTsiv^4;
            Matrix term1 = sixi.times(2.0 * vTsixi).times(vTsi).plus(sixi.times(vTsiv).times(xiTsi)).plus(invsi.times(-1.0 * vTsixi * vTsixi)).plus(siv.times(xiTsi).times(-2.0 * xiTsiv));
            Matrix term2 = sixi.times(vTsiv * vTsixi).plus(siv.times(-1.0 * vTsixi * vTsixi)).times(vTsi).times(-4.0 * vTsiv);
            d2LdvvT.plusEquals(term1.times(vTsiv * vTsiv).plus(term2).times(1.0 / StrictMath.pow(vTsiv, 4)));
        }

        // % Trim off gradient and Hessian elements for fixed vector components 
        // G = [dlda(2:end)'; dldv(2:end)];
        G = new Matrix(dimensionCount * 2 - 2, 1);
        G.setMatrix(0, dimensionCount - 2, 0, 0, dlda.transpose().getMatrix(1, dimensionCount - 1, 0, 0));
        G.setMatrix(dimensionCount - 1, dimensionCount * 2 - 2 - 1, 0, 0, dldv.getMatrix(1, dimensionCount - 1, 0, 0));

        // H = [d2LdaaT(2:end,2:end)  d2LdavT(2:end,2:end); ...
        //      d2LdavT(2:end,2:end)' d2LdvvT(2:end,2:end)];
        H = new Matrix(dimensionCount * 2 - 2, dimensionCount * 2 - 2);
        H.setMatrix(0, dimensionCount - 2, 0, dimensionCount - 2, d2LdaaT.getMatrix(1, dimensionCount - 1, 1, dimensionCount - 1));
        H.setMatrix(0, dimensionCount - 2, dimensionCount - 1, dimensionCount * 2 - 2 - 1, d2LdavT.getMatrix(1, dimensionCount - 1, 1, dimensionCount - 1));
        H.setMatrix(dimensionCount - 1, dimensionCount * 2 - 2 - 1, 0, dimensionCount - 2, d2LdavT.getMatrix(1, dimensionCount - 1, 1, dimensionCount - 1));
        H.setMatrix(dimensionCount - 1, dimensionCount * 2 - 2 - 1, dimensionCount - 1, dimensionCount * 2 - 2 - 1, d2LdvvT.getMatrix(1, dimensionCount - 1, 1, dimensionCount - 1));
    }

    /**
     * Evaluate log-liklihood function for vectors a and v given measured data.
     *
     * @param a
     * @param v
     * @param data
     * @param covMats
     * @return
     */
    private double calculateLogLikelihood(Matrix a, Matrix v, Matrix data, Matrix[] covMats) {

        int rowCount = data.getRowDimension();
        int dimensionCount = data.getColumnDimension();

        double L = 0.0;
        for (int i = 0; i < rowCount; i++) {
            // % pi - a, used often  
            Matrix xi = data.getMatrix(i, i, 0, dimensionCount - 1).transpose().plus(a.times(-1.0));
            Matrix si = covMats[i];

            L = L + xi.transpose().times(si.inverse().times(xi)).get(0, 0)
                    - StrictMath.pow(v.transpose().times(si.inverse().times(xi)).get(0, 0), 2)
                    / v.transpose().times(si.inverse().times(v)).get(0, 0);
        }
        // Leaving out log(det(si)) and constant terms,
        L = -0.5 * L;

        return L;
    }
}
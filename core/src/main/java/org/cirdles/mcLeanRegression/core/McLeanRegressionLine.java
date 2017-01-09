/* 
 * Copyright 2006-2017 CIRDLES.org.
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

/**
 *
 * @author James F. Bowring
 */
public class McLeanRegressionLine implements McLeanRegressionLineInterface {
    
    /**
     * the point on the line with first component a1
     */
    private double[][] a;
    /**
     * the direction vector of the line with first component v1 as input
     */
    private double[][] v;
    /**
     * contains the covariance matrix for the free components of a and v
     */
    private double [][] Sav;
    /**
     * the mean of the squared weighted deviates (reduced chi-square)
     */
    private double MSWD;
    /**
     * the number of analyses included in the calculation
     */
    private int n;
    
    private McLeanRegressionLine (){
    }

    public McLeanRegressionLine(double[][] a, double[][] v, double[][] Sav, double MSWD, int n) {
        this.a = a.clone();
        this.v = v.clone();
        this.Sav = Sav.clone();
        this.MSWD = MSWD;
        this.n = n;
    }

    /**
     * @return the a
     */
    @Override
    public double[][] getA() {
        return a.clone();
    }

    /**
     * @return the v
     */
    @Override
    public double[][] getV() {
        return v.clone();
    }

    /**
     * @return the Sav
     */
    @Override
    public double[][] getSav() {
        return Sav.clone();
    }

    /**
     * @return the MSWD
     */
    @Override
    public double getMSWD() {
        return MSWD;
    }

    /**
     * @return the n
     */
    @Override
    public int getN() {
        return n;
    }
    


}

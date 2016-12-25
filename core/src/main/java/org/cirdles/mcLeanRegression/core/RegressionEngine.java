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

import java.io.IOException;
import org.cirdles.mcLeanRegression.algorithms.LevenbergMarquardt;
import org.cirdles.mcLeanRegression.algorithms.McLeanRegressionSetup;
import org.cirdles.mcLeanRegression.utilities.ImportDataTableFromCSV;

/**
 *
 * @author James F. Bowring
 */
public class RegressionEngine {

    public void calculateMcLeanRegression(String myDataFilePath)
            throws IOException {

        String dataFilePath = myDataFilePath;
        dataFilePath = "/Users/sbowring/Development/McLeanRegression/app/ExampleDataFiles/dataunct_2D.csv";

        McLeanRegressionSetup mcLeanRegressionSetup = ImportDataTableFromCSV.extractDataAndUnctMatricesFromCsvFile(dataFilePath);

        mcLeanRegressionSetup.initializeRegressesionParams();

        mcLeanRegressionSetup.initializeUnertaintyCovarianceMatrices();

        LevenbergMarquardt levenbergMarquardt = new LevenbergMarquardt();

        // % maximum iterations before solver quits
        int maxIterations = 10000;
        //% tolerance of chi-square
        double chiTolerance = 1e-12;
        // % initial L-M damping parameter
        double lambda0 = 1000;
        // %set this to true to see results of each L-M iteration
        boolean verbose = true;

        levenbergMarquardt.LevenbergMarquardt_LinearRegression_v2(mcLeanRegressionSetup, maxIterations, chiTolerance, lambda0, verbose);
    }

    public static void main(String[] args) {
        try {
            new RegressionEngine().calculateMcLeanRegression("");
        } catch (IOException iOException) {
        }
    }

}

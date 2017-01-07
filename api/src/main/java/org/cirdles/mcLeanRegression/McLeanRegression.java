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
package org.cirdles.mcLeanRegression;

import Jama.Matrix;
import java.io.IOException;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineFitEngine;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineFitEngineInterface;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineInterface;
import org.cirdles.mcLeanRegression.utilities.DataFileHandler;
import org.cirdles.mcLeanRegression.utilities.DataFileHandlerInterface;


/**
 *
 * @author CIRDLES.org
 */
public class McLeanRegression implements McLeanRegressionInterface {

    /**
     *
     * @param myDataFilePath
     * @return McLeanRegressionLineInterface fitLine
     * @throws IOException
     */
    @Override
    public McLeanRegressionLineInterface fitLineToDataFor2or3or4or5DFromCSV(String myDataFilePath)
            throws IOException {

        String dataFilePath = myDataFilePath;
        DataFileHandlerInterface dataPrep = new DataFileHandler();
        McLeanRegressionLineFitEngineInterface mcLeanRegressionLineFitEngine = dataPrep.extractDataAndUnctMatricesFromCsvFile(dataFilePath);

        McLeanRegressionLineInterface fitLine = mcLeanRegressionLineFitEngine.fitLine();

        return fitLine;
    }

    @Override
    public McLeanRegressionLineInterface fitLineToDataFor2D(double[] x, double[] y, double[] x1SigmaAbs, double[] y1SigmaAbs, double[] rhos) {
        int rowCount = x.length;
        Matrix data = new Matrix(rowCount, 2);
        data.setMatrix(0, rowCount - 1, 0, 0, new Matrix(x, rowCount));
        data.setMatrix(0, rowCount - 1, 1, 1, new Matrix(y, rowCount));
        
        Matrix unct = new Matrix(rowCount, 3);
        unct.setMatrix(0, rowCount - 1, 0, 0, new Matrix(x1SigmaAbs, rowCount));
        unct.setMatrix(0, rowCount - 1, 1, 1, new Matrix(y1SigmaAbs, rowCount));
        unct.setMatrix(0, rowCount - 1, 2, 2, new Matrix(rhos, rowCount));
        
        McLeanRegressionLineFitEngineInterface mcLeanRegressionLineFitEngine = new McLeanRegressionLineFitEngine(data, unct);
        McLeanRegressionLineInterface fitLine = mcLeanRegressionLineFitEngine.fitLine();

        return fitLine;
    }

    @Override
    public McLeanRegressionLineInterface fitLineToDataFor2D(double[][] xy, double[][] xy1SigmaAbsAndRhos) {
        Matrix data = new Matrix(xy);
        Matrix unct = new Matrix(xy1SigmaAbsAndRhos);
        
        McLeanRegressionLineFitEngineInterface mcLeanRegressionLineFitEngine = new McLeanRegressionLineFitEngine(data, unct);
        McLeanRegressionLineInterface fitLine = mcLeanRegressionLineFitEngine.fitLine();

        return fitLine;
    }
}

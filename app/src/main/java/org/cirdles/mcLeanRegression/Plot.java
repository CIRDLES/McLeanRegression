package org.cirdles.mcLeanRegression;

import Jama.Matrix;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineFitEngineInterface;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineInterface;

import java.io.IOException;

/**
 * Created by Emily on 10/9/17.
 */
public class Plot {
    /**
     * Plots the first variable ("x") against each of the remaining dimension variables.
     * @param lineFitEngine
     * @param lineFitParameters
     * @throws IOException
     */
    public void producePlots(
            McLeanRegressionLineFitEngineInterface lineFitEngine, McLeanRegressionLineInterface lineFitParameters)
            throws IOException {

        int dimCount = lineFitParameters.getA().length;

        Matrix x = lineFitEngine.getData().getMatrix(0, lineFitParameters.getN() - 1, 0, 0);
        Matrix xUnct = lineFitEngine.getUnct().getMatrix(0, lineFitParameters.getN() - 1, 0, 0);

        TopsoilPlotter [] plots = new TopsoilPlotter[dimCount - 1];

        for (int dim = 1; dim < dimCount; dim++) {
            Matrix y = lineFitEngine.getData().getMatrix(0, lineFitParameters.getN() - 1, dim, dim);
            Matrix yUnct = lineFitEngine.getUnct().getMatrix(0, lineFitParameters.getN() - 1, dim, dim);
            Matrix rho = lineFitEngine.getUnct().getMatrix(0, lineFitParameters.getN() - 1, dimCount + dim - 1, dimCount + dim - 1);

            Matrix data = new Matrix(lineFitParameters.getN(), 2);
            Matrix unct = new Matrix(lineFitParameters.getN(), 3);

            data.setMatrix(0, lineFitParameters.getN() - 1, 0, 0, x);
            data.setMatrix(0, lineFitParameters.getN() - 1, 1, 1, y);

            unct.setMatrix(0, lineFitParameters.getN() - 1, 0, 0, xUnct);
            unct.setMatrix(0, lineFitParameters.getN() - 1, 1, 1, yUnct);
            unct.setMatrix(0, lineFitParameters.getN() - 1, 2, 2, rho);

            plots[dim] = new TopsoilPlotter(data.getArrayCopy(), unct.getArrayCopy());

        }
    }

}

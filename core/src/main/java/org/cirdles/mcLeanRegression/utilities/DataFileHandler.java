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
package org.cirdles.mcLeanRegression.utilities;

import Jama.Matrix;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineFitEngine;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineFitEngineInterface;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineInterface;
import org.cirdles.mcLeanRegression.core.McLeanRegressionReportsEngine;

/**
 *
 * @author James F. Bowring
 */
public class DataFileHandler implements DataFileHandlerInterface {

    private transient Consumer<Integer> progressSubscriber;
    private transient McLeanRegressionReportsEngine reportsEngine;
    private String currentDataFileLocation;

    /**
     * Creates a new {@link DataFileHandler} using a new reports engine.
     */
    public DataFileHandler() {
        this(new McLeanRegressionReportsEngine());
    }

    /**
     * Creates a new {@link DataFileHandler}.
     *
     * @param reportsEngine the reports engine to use
     */
    public DataFileHandler(McLeanRegressionReportsEngine reportsEngine) {
        this.reportsEngine = reportsEngine;
    }

    @Override
    public McLeanRegressionLineFitEngineInterface extractDataAndUnctMatricesFromCsvFile(String dataFileLocation)
            throws IOException {

        currentDataFileLocation = dataFileLocation;

        Path pathToLocalDataFile = FileSystems.getDefault().getPath(dataFileLocation);

        List<String> lines = Files.readAllLines(pathToLocalDataFile, Charset.defaultCharset());

        int columnCount = lines.get(0).split(",").length;
        int dimensionCount = (int) ((StrictMath.sqrt(8 * columnCount + 9) - 3) / 2);

        Matrix data = new Matrix(lines.size(), dimensionCount);
        // assume absolute 1-sigma uncertainties followed by RHOs
        Matrix unct = new Matrix(lines.size(), columnCount - dimensionCount);

        // data pattern is d,u,d,u,d,u... (data, uncert) followed by RHOs
        // separate data from uncertainties
        for (int row = 0; row < lines.size(); row++) {
            String dataStrings[] = lines.get(row).split(",");
            for (int col = 0; col < dimensionCount; col++) {
                data.set(row, col, Double.parseDouble(dataStrings[col * 2]));
                unct.set(row, col, Double.parseDouble(dataStrings[col * 2 + 1]));
            }
            // now RHOs
            for (int col = dimensionCount * 2; col < columnCount; col++) {
                unct.set(row, col - dimensionCount, Double.parseDouble(dataStrings[col]));
            }

            if (progressSubscriber != null) {
                int progress = (row + 1) * 100 / lines.size();
                progressSubscriber.accept(progress);
            }
        }

        return new McLeanRegressionLineFitEngine(data, unct);
    }

    public void writeReportsFromDataFile(String dataFileLocation)
            throws IOException {
        McLeanRegressionLineFitEngineInterface lineFitEngine = extractDataAndUnctMatricesFromCsvFile(dataFileLocation);
        McLeanRegressionLineInterface lineFitParameters = lineFitEngine.fitLine();
        reportsEngine.produceReports(lineFitParameters);
        
        //reportsEngine.producePlots(lineFitEngine, lineFitParameters);
    }

    public boolean currentDataFileLocationIsFile() {
        return new File(currentDataFileLocation).isFile();
    }

    public void initReportsEngineWithCurrentDataFileName() {
        // strip .csv from file name
        reportsEngine.setNameOfCsvDataFile(new File(currentDataFileLocation).getName().split("\\.")[0]);
    }

    public void initReportsEngineWithCurrentDataFileName(String dataFileLocation) {
        // strip .csv from file name
        reportsEngine.setNameOfCsvDataFile(new File(dataFileLocation).getName().split("\\.")[0]);
    }

    /**
     * @return the currentDataFileLocation
     */
    public String getCurrentDataFileLocation() {
        return currentDataFileLocation;
    }

    /**
     * @param currentDataFileLocation the currentDataFileLocation to set
     */
    public void setCurrentDataFileLocation(String currentDataFileLocation) {
        this.currentDataFileLocation = currentDataFileLocation;
    }

    /**
     * @return the reportsEngine
     */
    public McLeanRegressionReportsEngine getReportsEngine() {
        return reportsEngine;
    }

    /**
     * @param progressSubscriber the progressSubscriber to set
     */
    public void setProgressSubscriber(Consumer<Integer> progressSubscriber) {
        this.progressSubscriber = progressSubscriber;
    }
}

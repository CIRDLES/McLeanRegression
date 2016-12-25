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
package org.cirdles.mcLeanRegression.utilities;

import Jama.Matrix;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.cirdles.mcLeanRegression.algorithms.McLeanRegressionSetup;

/**
 *
 * @author James F. Bowring
 */
public class ImportDataTableFromCSV {

    public static McLeanRegressionSetup extractDataAndUnctMatricesFromCsvFile(String dataFileLocation)
            throws IOException {

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
        }
        
        return new McLeanRegressionSetup(data, unct);
    }
}

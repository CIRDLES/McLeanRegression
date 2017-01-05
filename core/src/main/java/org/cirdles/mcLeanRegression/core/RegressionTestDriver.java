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
import org.cirdles.mcLeanRegression.utilities.DataFileHandler;
import org.cirdles.mcLeanRegression.utilities.DataFileHandlerInterface;

/**
 *
 * @author James F. Bowring
 */
public class RegressionTestDriver {

    public McLeanRegressionLineInterface calculateMcLeanRegression()
            throws IOException {

        String dataFilePath = "/Users/sbowring/Development/McLeanRegression/app/ExampleDataFiles/dataunct_2D.csv";

        DataFileHandlerInterface dataPrep = new DataFileHandler();
        McLeanRegressionLineFitEngineInterface mcLeanRegressionLineFitEngine = dataPrep.extractDataAndUnctMatricesFromCsvFile(dataFilePath);

        return mcLeanRegressionLineFitEngine.fitLine();
        
    }

    public static void main(String[] args) {
        try {
            McLeanRegressionLineInterface mcLeanRegressionLine = new RegressionTestDriver().calculateMcLeanRegression();
            
            System.out.println(mcLeanRegressionLine.getA()[1][0]);
        } catch (IOException iOException) {
        }
    }

}

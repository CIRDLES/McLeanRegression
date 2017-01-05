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

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;

/**
 *
 * @author CIRDLES.org
 */
public class McLeanRegressionReportsEngine {

    private File folderToWriteMcLeanRegressionReports;
    private String nameOfCsvDataFile;

    public McLeanRegressionReportsEngine() {
        folderToWriteMcLeanRegressionReports = new File(System.getProperty("user.dir"));
        nameOfCsvDataFile = "";
    }

    /**
     * ReportsEngine to test results
     *
     * @param lineFitParameters
     * @param shrimpFractions the value of shrimpFractions
     * @throws java.io.IOException
     */
    public void produceReports(McLeanRegressionLineInterface lineFitParameters) throws IOException {
        System.out.println("Reporting line slope = " + lineFitParameters.getV()[1][0]);
        System.out.flush();

        StringBuilder params = new StringBuilder();
        params.append("Reporting line slope = ").append(lineFitParameters.getV()[1][0]);
        Files.write(new File("TEST.txt").toPath(), params.toString().getBytes(UTF_8));
    }

    /**
     * @return the folderToWriteMcLeanRegressionReports
     */
    public File getFolderToWriteMcLeanRegressionReports() {
        return folderToWriteMcLeanRegressionReports;
    }

    /**
     * @param folderToWriteMcLeanRegressionReports the
     * folderToWriteMcLeanRegressionReports to set
     */
    public void setFolderToWriteMcLeanRegressionReports(File folderToWriteMcLeanRegressionReports) {
        this.folderToWriteMcLeanRegressionReports = folderToWriteMcLeanRegressionReports;
    }

    /**
     * @return the nameOfCsvDataFile
     */
    public String getNameOfCsvDataFile() {
        return nameOfCsvDataFile;
    }

    /**
     * @param nameOfCsvDataFile the nameOfCsvDataFile to set
     */
    public void setNameOfCsvDataFile(String nameOfCsvDataFile) {
        this.nameOfCsvDataFile = nameOfCsvDataFile;
    }

}

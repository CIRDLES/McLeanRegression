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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineFitEngine;
import org.cirdles.mcLeanRegression.utilities.DataFileHandler;
import org.cirdles.mcLeanRegression.utilities.FileUtilities;

/**
 *
 * @author James F. Bowring
 */
public class McLeanRegression {

    public static final String VERSION;
    public static final String RELEASE_DATE;

    static {
        ResourceExtractor mcLeanRegressionResourceExtractor
                = new ResourceExtractor(McLeanRegression.class);

        String version = "version";
        String releaseDate = "date";

        // get version number and release date written by pom.xml
        Path resourcePath = mcLeanRegressionResourceExtractor.extractResourceAsPath("version.txt");
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {
            String line = reader.readLine();
            if (line != null) {
                String[] versionText = line.split("=");
                version = versionText[1];
            }

            line = reader.readLine();
            if (line != null) {
                String[] versionDate = line.split("=");
                releaseDate = versionDate[1];
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        VERSION = version;
        RELEASE_DATE = releaseDate;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // set up folder of example data files
        ResourceExtractor mcLeanRegressionResourceExtractor
                = new ResourceExtractor(McLeanRegressionLineFitEngine.class);

        DataFileHandler dataFileHandler = new DataFileHandler();

        Path listOfDataFiles = mcLeanRegressionResourceExtractor.extractResourceAsPath("listOfDataFiles.txt");
        if (listOfDataFiles != null) {
            File exampleFolder = new File("ExampleDataFiles");
            try {
                if (exampleFolder.exists()) {
                    FileUtilities.recursiveDelete(exampleFolder.toPath());
                }
                if (exampleFolder.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfDataFiles, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File dataFileResource = mcLeanRegressionResourceExtractor.extractResourceAsFile(fileNames.get(i));
                            File dataFile = new File(exampleFolder.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (dataFileResource.renameTo(dataFile)) {
                                System.out.println("DataFile added: " + fileNames.get(i));
                            } else {
                                System.out.println("DataFile failed to add: " + fileNames.get(i));
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
            }

            try {
                // point to directory, but no default choice
                dataFileHandler.setCurrentDataFileLocation(exampleFolder.getCanonicalPath());
            } catch (IOException iOException) {
            }
        }

        // Set up default folder for reports
        File defaultMcLeanRegressionReportsFolder = new File("McLeanRegressionReports_v" + VERSION);
        dataFileHandler.getReportsEngine().setFolderToWriteMcLeanRegressionReports(defaultMcLeanRegressionReportsFolder);
        if (!defaultMcLeanRegressionReportsFolder.exists()) {
            if (!defaultMcLeanRegressionReportsFolder.mkdir()) {
                System.out.println("Failed to make McLeanRegression Reports directory");
            }
        }

        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Metal is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) { //Nimbus (original), Motif, Metal
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(org.cirdles.mcLeanRegression.userInterface.McLeanRegressionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new org.cirdles.mcLeanRegression.userInterface.McLeanRegressionUI(dataFileHandler).setVisible(true);
        });
    }

}

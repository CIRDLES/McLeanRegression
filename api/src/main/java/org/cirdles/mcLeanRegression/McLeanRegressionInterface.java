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

import java.io.IOException;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineInterface;

/**
 *
 * @author CIRDLES.org
 */
public interface McLeanRegressionInterface {
    public McLeanRegressionLineInterface fitLineToDataFor2or3or4or5DFromCSV(String myDataFilePath)
            throws IOException;
    
    public McLeanRegressionLineInterface fitLineToDataFor2D(double[] x, double[] y, double[] x1SigmaAbs, double[] y1SigmaAbs, double[] rhos) ;
    public McLeanRegressionLineInterface fitLineToDataFor2D(double[][] xy, double[][] xy1SigmaAbsAndRhos);
}

/*
 * Copyright 2006-2017 CIRDLES.
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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.cirdles.topsoil.plot.Plot;
//import org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlot;
import org.cirdles.topsoil.plot.base.BasePlot;

/**
 *
 * @author bowring
 */
public class TopsoilPlotter {

    private static final String X = "x";
    private static final String SIGMA_X = "sigma_x";
    private static final String Y = "y";
    private static final String SIGMA_Y = "sigma_y";
    private static final String RHO = "rho";

    public TopsoilPlotter(double[][] data, double[][] unct) {

        Plot myChart = null;
        try {
            myChart = new BasePlot();
        } catch (Exception e) {
        }

        List<Map<String, Object>> myData = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Map<String, Object> datum = new HashMap<>();
            myData.add(datum);
            datum.put(X, data[i][0]);
            datum.put(SIGMA_X, unct[i][0]);
            datum.put(Y, data[i][1]);
            datum.put(SIGMA_Y, unct[i][1]);
            datum.put(RHO, unct[i][2]);
        }

        myChart.setData(myData);

        JComponent jc = myChart.displayAsJComponent();

        jc.createToolTip().setTipText("Testing the tool tip feature...");

        jc.setBounds(50, 50, 700, 500);

        class TestTopsoilDialog extends javax.swing.JDialog {

            public TestTopsoilDialog(Dialog owner, boolean modal) {
                super(owner, modal);
            }
        }

        TestTopsoilDialog testTopsoilDialogDialog = new TestTopsoilDialog(null, false);
        testTopsoilDialogDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        testTopsoilDialogDialog.setBounds( //
                400,
                200, //
                800,
                600);

        JLayeredPane pane = new JLayeredPane();
        pane.add(jc);
        
        JButton button = new JButton("Reset");
        button.setBounds(0,0,150,30);
        pane.add(button);
        
        testTopsoilDialogDialog.setTitle("Topsoil plot of McLean Regression");
        
        testTopsoilDialogDialog.add(pane);

        // time out for thread
        try {
            Thread.sleep(7500);
        } catch (InterruptedException interruptedException) {
        }

        testTopsoilDialogDialog.setVisible(true);
    }
}

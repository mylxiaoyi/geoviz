/* -------------------------------------------------------------------
 GeoVISTA Center (Penn State, Dept. of Geography)
 Java source file for the class StarPlot
 Copyright (c), 2003, Frank Hardisty
 All Rights Reserved.
 Original Author: Frank Hardisty
 $Author: hardisty $
 $Id: StarPlot.java,v 1.4 2006/02/17 17:21:23 hardisty Exp $
 $Date: 2006/02/17 17:21:23 $
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 -------------------------------------------------------------------   */
package geovista.geoviz.star;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import geovista.common.event.ColorArrayEvent;
import geovista.common.event.ColorArrayListener;
import geovista.common.event.DataSetEvent;
import geovista.common.event.DataSetListener;
import geovista.common.event.DataSetModifiedEvent;
import geovista.common.event.DataSetModifiedListener;
import geovista.common.event.IndicationEvent;
import geovista.common.event.IndicationListener;
import geovista.common.event.SubspaceEvent;
import geovista.common.event.SubspaceListener;
import geovista.geoviz.visclass.VisualClassifier;

/**
 * Paints an array of StarPlot. Responds to and broadcasts DataSetChanged,
 * IndicationChanged etc. events.
 * 
 * @author Frank Hardisty
 * @version $Revision: 1.4 $
 */
public class StarPlot extends JPanel implements DataSetListener,
		DataSetModifiedListener, IndicationListener, SubspaceListener,
		ColorArrayListener, TableModelListener {

	StarPlotCanvas starCan;
	StarPlotLegend starLeg;
	VisualClassifier vc;
	int indication;
	final static Logger logger = Logger.getLogger(StarPlot.class.getName());

	JComboBox methodCombo;

	@SuppressWarnings("deprecation")
	public StarPlot() {
		vc = new VisualClassifier();
		starCan = new StarPlotCanvas();
		starLeg = new StarPlotLegend();
		setLayout(new BorderLayout());
		methodCombo = new JComboBox(StarPlotLayer.ScaleMethod.values());
		// this.add(methodCombo, BorderLayout.WEST);
		methodCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (methodCombo.getSelectedItem().equals(
						StarPlotLayer.ScaleMethod.Linear)) {
					starCan.getPlotLayer().setMethod(
							StarPlotLayer.ScaleMethod.Linear);
				}
				if (methodCombo.getSelectedItem().equals(
						StarPlotLayer.ScaleMethod.Rank_Order)) {
					starCan.getPlotLayer().setMethod(
							StarPlotLayer.ScaleMethod.Rank_Order);
				}
				if (methodCombo.getSelectedItem().equals(
						StarPlotLayer.ScaleMethod.Normalized)) {
					starCan.getPlotLayer().setMethod(
							StarPlotLayer.ScaleMethod.Normalized);
				}
				if (methodCombo.getSelectedItem().equals(
						StarPlotLayer.ScaleMethod.Log)) {
					starCan.getPlotLayer().setMethod(
							StarPlotLayer.ScaleMethod.Log);
				}

				StarPlot.this.repaint();
			}
		});

		this.add(starCan, BorderLayout.CENTER);
		this.add(starLeg, BorderLayout.SOUTH);
		this.add(vc, BorderLayout.NORTH);
		starCan.addIndicationListener(this);
		vc.addColorArrayListener(this);

		String[] names = { "toy", "data" };
		double[] toy = { 1, 2, 3 };
		double[] set = { 4, 5, 6 };
		Object[] toySet = { names, toy, set };
		dataSetChanged(new DataSetEvent(this, toySet));
	}

	public void dataSetChanged(DataSetEvent e) {
		e.getDataSetForApps().addTableModelListener(this);
		starCan.dataSetChanged(e);
		vc.setDataSet(e.getDataSetForApps());

		Color[] starColors = vc.getColorForObservations();
		starCan.setStarFillColors(starColors);
		int nNumericVars = starCan.getDataSet().getNumberNumericAttributes();
		if (nNumericVars > 6) {
			nNumericVars = 6;
		}
		int[] selectedVars = new int[nNumericVars];
		for (int i = 0; i < nNumericVars; i++) {
			selectedVars[i] = i;
		}
		SubspaceEvent subE = new SubspaceEvent(this, selectedVars);
		subspaceChanged(subE);
		// this.setLegendIndication(0);
	}

	public void dataSetModified(DataSetModifiedEvent e) {

	}

	public void subspaceChanged(SubspaceEvent e) {
		starCan.subspaceChanged(e);
		setLegendIndication(indication);
	}

	public void colorArrayChanged(ColorArrayEvent e) {
		Color[] starColors = e.getColors();
		starCan.setStarFillColors(starColors);
	}

	public void indicationChanged(IndicationEvent e) {
		int ind = e.getIndication();
		if (e.getSource() != starCan) {
			starCan.indicationChanged(e);
		}
		setLegendIndication(ind);

	}

	private void setLegendIndication(int ind) {

		if (ind > starCan.getDataSet().getNumObservations()) {
			logger.severe("got indication greater than data set size, ind = "
					+ ind);
			return;
		}
		if (ind >= 0) {
			starLeg.setObsName(starCan.getObservationName(ind));
			String[] varNames = starCan.getVarNames();
			double[] values = starCan.getValues(ind);
			if (values == null) {
				return;
			}
			int[] spikeLengths = starCan.getSpikeLengths(ind);
			starLeg.setValues(values);
			starLeg.setVariableNames(varNames);
			if (spikeLengths == null) {
				return;
			}
			starLeg.setSpikeLengths(spikeLengths);
			Color starColor = starCan.getStarFillColor(ind);
			if (starColor == null) {
				return;
			}
			starLeg.setStarFillColor(starColor);

		}

	}

	/**
	 * adds an IndicationListener to the component
	 */
	public void addIndicationListener(IndicationListener l) {
		starCan.addIndicationListener(l);
	}

	/**
	 * removes an IndicationListener from the component
	 */
	public void removeIndicationListener(IndicationListener l) {
		starCan.removeIndicationListener(l);
	}

	public void tableChanged(TableModelEvent e) {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Starplot, got a data set, id = "
					+ starCan.getDataSet().hashCode());
		}
		vc.setDataSet(starCan.getDataSet());

	}

	public StarPlotCanvas getStarCan() {
		return starCan;
	}

} // end class

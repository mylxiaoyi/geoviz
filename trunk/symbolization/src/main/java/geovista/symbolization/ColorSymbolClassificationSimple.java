/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.symbolization;

import java.awt.Color;

import geovista.common.classification.Classifier;
import geovista.common.classification.ClassifierRawQuantiles;

public class ColorSymbolClassificationSimple implements
		ColorSymbolClassification {

	private ColorSymbolizer colorer;
	private Classifier classer;

	public ColorSymbolClassificationSimple() {
		// defaults
		colorer = new ColorSymbolizerLinear();
		classer = new ClassifierRawQuantiles();
	}

	public Color[] symbolize(double[] data) {
		Color[] colors = colorer.symbolize(colorer.getNumClasses());
		int[] classes = classer.classify(data, colorer.getNumClasses());
		Color[] returnColors = new Color[data.length];
		int myClass = 0;
		for (int i = 0; i < classes.length; i++) {
			myClass = classes[i];
			if (myClass == Classifier.NULL_CLASS) {
				returnColors[i] = ColorSymbolizer.DEFAULT_NULL_COLOR;
			} else {
				returnColors[i] = colors[classes[i]];
			}
		}
		return returnColors;
	}

	public void setColorer(ColorSymbolizer colorer) {
		this.colorer = colorer;
	}

	public ColorSymbolizer getColorer() {
		return colorer;
	}

	public void setClasser(Classifier classer) {
		this.classer = classer;
	}

	public Classifier getClasser() {
		return classer;
	}

}
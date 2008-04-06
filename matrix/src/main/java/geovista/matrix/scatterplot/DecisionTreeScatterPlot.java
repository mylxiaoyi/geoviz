package geovista.matrix.scatterplot;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.EventListenerList;

import geovista.common.classification.BoundaryClassifier;
import geovista.common.data.DataSetForApps;
import geovista.common.event.IndicationListener;
import geovista.common.ui.ExcentricLabelClient;
import geovista.common.ui.ExcentricLabels;
import geovista.geoviz.scatterplot.DataArray;
import geovista.geoviz.scatterplot.Histogram;
import geovista.matrix.MatrixElement;
import geovista.symbolization.BivariateColorSymbolClassification;
import geovista.symbolization.BivariateColorSymbolClassificationSimple;

/**
 * put your documentation comment here
 */
public class DecisionTreeScatterPlot extends JPanel implements
		ComponentListener, MouseListener, MouseMotionListener, MatrixElement,
		Serializable, ExcentricLabelClient {

	public static final double AXISSPACEPORTION = 1.0 / 6.0;
	public static final String COMMAND_POINT_SELECTED = "cmdSel";
	public static final String COMMAND_DATARANGE_SET = "cmdset";
	private static final int RADIUS = 3; // Glyph size
	transient private int plotOriginX;
	transient private int plotOriginY;
	transient private int plotEndX;
	transient private int plotEndY;
	// transient private Object[] dataObject;//XXX eliminate this data structure
	transient private DataSetForApps dataSet;

	transient private double[][] doubleDataArrays;
	transient private int[] dataIndices;
	transient private double[] dataX;
	transient private double[] dataY;
	transient private int[] exsint;
	transient private int[] whyint;
	private String[] variableNames;
	private String[] observNames;
	transient private String attributeX;
	transient private String attributeY;
	transient private boolean axisOn;
	transient private Color background;
	private Color foreground;
	private Color selectionColor = Color.blue;
	transient private Color[] multipleSelectionColors;
	transient private Color[] colorArrayForObs;
	transient private DataArray dataArrayX;
	transient private DataArray dataArrayY;
	private double[] xAxisExtents = new double[2];
	private double[] yAxisExtents = new double[2];
	// private MaxMinCoordinateValue MMexs;
	// private MaxMinCoordinateValue MMwhy;
	transient private int selectX = 0;
	transient private int selectY = 0;
	transient private int selectWidth = 0;
	transient private int selectHeight = 0;
	private Vector selRecords = new Vector();// retiring
	transient private int[] selections;
	transient private int[] conditionArray;
	transient private int mouseX1, mouseX2, mouseY1, mouseY2;
	private JPopupMenu popup;
	private JTextField xAxisMinField = new JTextField(16);
	private JTextField xAxisMaxField = new JTextField(16);
	private JTextField yAxisMinField = new JTextField(16);
	private JTextField yAxisMaxField = new JTextField(16);
	private EventListenerList listenerListAction = new EventListenerList();

	// stuff added for colors
	transient private Color[] pointColors;
	transient private BivariateColorSymbolClassification bivarColorClasser = new BivariateColorSymbolClassificationSimple();
	private Histogram histogram = new Histogram();
	transient private ExcentricLabels exLabels;
	transient private BoundaryClassifier xClasser = null;
	transient private BoundaryClassifier yClasser = null;
	transient private double[] xBoundaries;
	transient private double[] yBoundaries;
	transient private int[] xBoundariesInt;
	transient private int[] yBoundariesInt;
	transient private Color[][] classColors;
	transient int count = 0;

	transient int firstBar;
	transient int lastBar;
	transient double yBarDistance;
	transient double xBarDistance;
	transient Vector treeVector;
	Vector xDecision = new Vector();
	Vector yDecision = new Vector();
	transient int[] xDecisionInt;
	transient int[] yDecisionInt;
	transient boolean selOriginalColorMode;
	final static Logger logger = Logger.getLogger(DecisionTreeScatterPlot.class
			.getName());

	/**
	 * put your documentation comment here
	 */
	public DecisionTreeScatterPlot() {
		super();
		// this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setPreferredSize(new Dimension(300, 300));
		// ...where the GUI is constructed:
		// Create the popup menu.
		popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Set Range");
		// menuItem.addActionListener(this);
		popup.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			/**
			 * put your documentation comment here
			 * 
			 * @param e
			 */
			public void actionPerformed(ActionEvent e) {
				showDialog(400, 400);
			}
		});
		menuItem = new JMenuItem("Edit");

		popup.add(menuItem);
		this.addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param String
	 *            attributeX
	 * @param String
	 *            attributeY
	 * @param double[]
	 *            dataX
	 * @param double[]
	 *            dataY
	 * @param boolean
	 *            axisOn
	 */
	public DecisionTreeScatterPlot(Object[] dataObject, int[] dataIndices,
			boolean axisOn, Color c) {
		logger.finest("go into scatterplot...");
		// this.doubleDataArrays = doubleDataArrays;
		// this.attributeArrays = (String[])variableNames.clone();
		this.dataSet = new DataSetForApps(dataObject);// XXX trying to
														// eliminate these...
		this.variableNames = (String[]) dataObject[0];
		int len = variableNames.length;
		if (dataObject[len + 1] == null) {
			this.observNames = null;
		} else {
			this.observNames = (String[]) dataObject[len + 1];
		}
		this.dataIndices = dataIndices;
		// convert Object array to double arrays.
		AxisDataSetup();
		// initialize();
		this.axisOn = axisOn;
		this.background = c;
		if (c == Color.black) {
			this.foreground = Color.white;
		} else {
			this.foreground = Color.black;
		}
		initialize();
	}

	/**
	 * @param data
	 * 
	 * This method is deprecated becuase it wants to create its very own pet
	 * DataSetForApps. This is no longer allowed, to allow for a mutable, common
	 * data set. Use of this method may lead to unexpected program behavoir.
	 * Please use setDataSet instead.
	 */
	@Deprecated
	public void setDataObject(Object[] data) {
		this.setDataSet(new DataSetForApps(data));

	}

	public void setDataSet(DataSetForApps data) {
		if (data == null) {
			logger.finest("data null!");
			return;
		}
		this.dataSet = data;

		this.variableNames = data.getObservationNames();

		this.observNames = data.getObservationNames();
		this.initExcentricLabels();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param doubleDataArrays
	 */
	public void setDoubleDataArrays(double[][] doubleDataArrays) {
		this.doubleDataArrays = doubleDataArrays;
	}

	/**
	 * Not used in dataObject version.
	 * 
	 * @param dataIndices
	 */
	public void setDataIndices(int[] dataIndices) {
		this.dataIndices = dataIndices.clone();
		this.dataX = doubleDataArrays[dataIndices[0]];
		this.dataY = doubleDataArrays[dataIndices[1]];
		this.attributeX = variableNames[dataIndices[0]];
		this.attributeY = variableNames[dataIndices[1]];
		initialize();
	}

	/**
	 * Set up x and y axises in each element by setting the attributes
	 * displayed.
	 * 
	 * @param indices
	 */
	public void setElementPosition(int[] indices) {
		this.dataIndices = indices;
		AxisDataSetup();
		initialize();
	}

	private void AxisDataSetup() {
		boolean[] dataBoolean;
		int[] dataInt;
		int len = 0;
		Object[] dataObject = dataSet.getDataSetNumericAndSpatial();
		if (dataObject[dataIndices[0]] instanceof double[]) {
			this.dataX = (double[]) (dataObject[dataIndices[0]]);
			len = dataX.length;
		} else if (dataObject[dataIndices[0]] instanceof int[]) {
			dataInt = (int[]) dataObject[dataIndices[0]];
			len = dataInt.length;
			dataX = new double[len];
			for (int i = 0; i < len; i++) {
				dataX[i] = dataInt[i];
			}
		} else if (dataObject[dataIndices[0]] instanceof boolean[]) {
			dataBoolean = (boolean[]) dataObject[dataIndices[0]];
			len = dataBoolean.length;
			dataX = new double[len];
			for (int i = 0; i < len; i++) {
				if (dataBoolean[i] == true) {
					dataX[i] = 1;
				} else {
					dataX[i] = 0;
				}
			}
		}
		if (dataObject[dataIndices[1]] instanceof double[]) {
			this.dataY = (double[]) dataObject[dataIndices[1]];
			len = this.dataY.length;
		} else if (dataObject[dataIndices[1]] instanceof int[]) {
			dataInt = (int[]) dataObject[dataIndices[1]];
			len = dataInt.length;
			dataY = new double[len];
			for (int i = 0; i < len; i++) {
				dataY[i] = dataInt[i];
			}
		} else if (dataObject[dataIndices[1]] instanceof boolean[]) {
			dataBoolean = (boolean[]) dataObject[dataIndices[1]];
			len = dataBoolean.length;
			dataY = new double[len];
			for (int i = 0; i < len; i++) {
				if (dataBoolean[i] == true) {
					dataY[i] = 1;
				} else {
					dataY[i] = 0;
				}
			}
		}
		this.attributeX = variableNames[dataIndices[0] - 1]; // Minus 1
		// because
		// dataObject[0]
		// is attribute
		// names.
		this.attributeY = variableNames[dataIndices[1] - 1]; // and real data
		// begin from
		// dataObject[1].
		this.selections = new int[len];
		this.exsint = new int[len];
		this.whyint = new int[len];
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public int[] getElementPosition() {
		return this.dataIndices;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param variableNames
	 */
	public void setAttributeArrays(String[] attributeArrays) {
		this.variableNames = attributeArrays;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param dataX
	 */
	public void setX(double[] dataX) {
		this.dataX = dataX;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param dataY
	 */
	public void setY(double[] dataY) {
		this.dataY = dataY;
	}

	// public void setCoordinate (double[][] coordinate){
	// this.dataX = coordinate[0][];
	// this.dataY = coordinate[1][];
	// }
	public void setAxisOn(boolean axisOn) {
		this.axisOn = axisOn;
	}

	/**
	 * Minimum and maximum values for xAxis. xAxisExtents[0] = min,
	 * xAxisExtents[1] = max.
	 * 
	 * @param xAxisExtents
	 */
	public void setXAxisExtents(double[] xAxisExtents) {
		logger.finest("set up axis ..." + xAxisExtents[0]);
		this.xAxisExtents = xAxisExtents.clone();
		logger.finest("set up axis ..." + xAxisExtents[0]);
		this.setupDataforDisplay();
		repaint();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param yAxisExtents
	 */
	public void setYAxisExtents(double[] yAxisExtents) {
		logger.finest("set up axis ...");
		this.yAxisExtents = yAxisExtents.clone();
		this.setupDataforDisplay();
		repaint();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public double[] getXAxisExtents() {
		return this.xAxisExtents;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @return
	 */
	public double[] getYAxisExtents() {
		return this.yAxisExtents;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param conditionArray
	 */
	public void setConditionArray(int[] conditionArray) {
		this.conditionArray = conditionArray;
	}

	public void setColorArrayForObs(Color[] colorArray) {
		this.colorArrayForObs = colorArray;
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param c
	 */
	@Override
	public void setBackground(Color c) {
		if (c == null) {
			return;
		}
		this.background = c;
		int colorTotal = c.getRed() + c.getGreen() + c.getBlue();
		int greyColor = 128 * 3;
		if (colorTotal < greyColor) {
			this.foreground = Color.white;
		} else {
			this.foreground = Color.black;
		}
		this.repaint();
	}

	public boolean getSelOriginalColorMode() {
		return selOriginalColorMode;
	}

	public void setSelOriginalColorMode(boolean selOriginalColorMode) {
		this.selOriginalColorMode = selOriginalColorMode;
		repaint();
	}

	public void setSelectionColor(Color c) {
		this.selectionColor = c;
		this.repaint();
	}

	public Color getSelectionColor() {
		return this.selectionColor;
	}

	public void setMultipleSelectionColors(Color[] c) {
		this.multipleSelectionColors = c;
	}

	public Color[] getColors() {
		return this.pointColors;
	}

	/**
	 * Return itself.
	 * 
	 * @return
	 */
	public MatrixElement getThis() {
		return this;
	}

	public String getShortDiscription() {
		return "XYP";
	}

	public JToolBar getTools() {
		return null;
	}

	/**
	 * Set up data and axis for drawing the scatter plot.
	 */
	private void initialize() {
		this.setBackground(background);
		this.dataArrayX = new DataArray(dataX);
		this.dataArrayY = new DataArray(dataY);
		this.conditionArray = new int[dataX.length];
		this.setBorder(BorderFactory.createLineBorder(Color.gray));
		if (axisOn) {
			xAxisExtents = this.dataArrayX.getMaxMinCoorValue().clone();
			yAxisExtents = this.dataArrayY.getMaxMinCoorValue().clone();
		} else {
			xAxisExtents[0] = dataArrayX.getExtent()[0];
			xAxisExtents[1] = dataArrayX.getExtent()[1];
			yAxisExtents[0] = dataArrayY.getExtent()[0];
			yAxisExtents[1] = dataArrayY.getExtent()[1];
		}
		// added for colors
		this.makeColors();
		this.setupDataforDisplay();
	}

	/**
	 * Set the size of plot area for both axis on and axis off situations.
	 * 
	 * @param axisOn
	 */
	private void setVisibleAxis(boolean axisOn) {
		if (axisOn) {
			plotOriginX = (int) (this.getWidth() * AXISSPACEPORTION);
			plotOriginY = (int) (this.getHeight() * (1 - AXISSPACEPORTION));
			plotEndX = (int) (this.getSize().getWidth())
					- (int) (this.getSize().getWidth() * AXISSPACEPORTION / 2);
			plotEndY = (int) (this.getSize().getHeight() / 12);
			logger.finest("size width: " + this.getSize().getWidth());
			logger.finest("plot Origin X: " + plotOriginX);
		} else {
			plotOriginX = 2;
			plotOriginY = (int) (this.getSize().getHeight() - 2);
			plotEndX = (int) (this.getSize().getWidth()) - 3;
			plotEndY = 3;
		}

		// set the location of bars and Strings.
		// firstBar = (int)(yAxisExtents[0]/(this.dataArrayY.getMajorTick()));
		// lastBar = (int)(yAxisExtents[1]/(this.dataArrayY.getMajorTick()));
		// double yBarNumber = (yAxisExtents[1] -
		// yAxisExtents[0])/(this.dataArrayY.getMajorTick());
		// yBarDistance = ((plotOriginY - plotEndY)/yBarNumber);
		// double xBarNumber = (xAxisExtents[1] -
		// xAxisExtents[0])/(this.dataArrayX.getMajorTick());
		// xBarDistance = ((plotEndX - plotOriginX)/xBarNumber);
	}

	/**
	 * Draw the scatter plot.
	 * 
	 * @param g
	 */
	@Override
	public void paintComponent(Graphics g) {
		logger.finest("paint track..." + count++);
		// draw a black border and a white background
		if (this.dataIndices == null) {
			return;
		}
		g.setColor(background);
		g.fillRect(0, 0, getSize().width, getSize().height);
		g.setColor(foreground);
		this.paintBorder(g);
		// size = this.getSize();
		// setVisibleAxis(axisOn);
		// if(DefaultBackground == null) DefaultBackground =
		// this.getBackground();
		if (axisOn) {
			drawAxis(g);
		}
		drawPlot(g);
		Graphics2D g2 = (Graphics2D) g;
		if (exLabels != null && this.axisOn == true) {
			this.setToolTipText("");
			exLabels.paint(g2, getBounds());
		}

	}

	/**
	 * Draw pot (points) on the screen.
	 * 
	 * @param g
	 */
	private void drawPlot(Graphics g) {
		int plotWidth, plotHeight;
		plotWidth = this.getWidth();
		plotHeight = this.getHeight();
		int size;
		size = (plotWidth < plotHeight) ? plotWidth : plotHeight;
		logger.finest("attribute equal? " + attributeX.equals(attributeY));
		if (this.dataIndices[0] == this.dataIndices[1]) {
			logger.finest("In scatterplot, draw histogram..." + dataIndices[0]);
			histogram.setAxisOn(false);
			histogram.setVariableName(this.attributeX);
			histogram.setData(this.dataX);
			histogram.setXAxisExtents(this.xAxisExtents);
			histogram.setBackground(background);
			histogram.setSize(this.getWidth(), this.getHeight());
			// XXX breaking for now, frank july 07
			// histogram.setSelections(this.selections);
			histogram.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			Color half = new Color(255, 255, 255, 100);
			g2.setColor(half);
			g.fillRect(0, 0, getSize().width, getSize().height);
			g2.setColor(foreground);
			Font font = new Font("", Font.PLAIN, size / 8);
			g.setFont(font);
			/*
			 * if (attributeX.length()>12){ g.drawString(attributeX, 2,
			 * plotHeight/2); }else if (attributeX.length()<=7){
			 * g.drawString(attributeX, plotWidth/4, plotHeight/2); }else {
			 * g.drawString(attributeX, plotWidth/8, plotHeight/2); }
			 */
			// font.this.getSize() = (int)plotWidth/12;
			Font font1 = new Font("", Font.PLAIN, size / 12);
			g.setFont(font1);
			g.drawLine(0, 0, 5, 5);
			String maxString = Float.toString((float) (xAxisExtents[1]));
			g.drawString(maxString, 6,
					(int) (plotHeight * AXISSPACEPORTION / 2) + 2);
			g.drawLine(0, plotHeight, 5, plotHeight - 5);
			String minString = Float.toString((float) (xAxisExtents[0]));
			g.drawString(minString, 6, plotHeight - 5);
			g.drawLine(plotWidth, plotHeight, plotWidth - 5, plotHeight - 5);
			g.drawString(maxString, plotWidth
					- (int) (plotWidth * AXISSPACEPORTION + 5), plotHeight - 5);
		} else {
			Graphics2D g2 = (Graphics2D) g;
			int len = dataArrayX.length();
			// draw the points

			if (this.colorArrayForObs != null) {
				for (int i = 0; i < len; i++) {
					if ((this.colorArrayForObs[i] != null)
							&& (conditionArray[i] > -1)) {
						g.setColor(this.colorArrayForObs[i]);
						g
								.drawOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
										RADIUS);
						g
								.fillOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
										RADIUS);
					}
				}
			} else {
				if (this.xClasser != null) {
					logger.finest("classer is not null");
					for (int i = 0; i < this.classColors.length; i++) {
						for (int j = 0; j < this.classColors[0].length; j++) {
							g.setColor(this.classColors[i][j]);
							g.drawRect(this.xBoundariesInt[i],
									this.yBoundariesInt[j],
									this.xBoundariesInt[i + 1]
											- this.xBoundariesInt[i],
									this.yBoundariesInt[j + 1]
											- this.yBoundariesInt[j]);
						}
					}
				}
				for (int i = 0; i < len; i++) {
					if ((exsint[i] <= this.plotEndX)
							&& (exsint[i] >= plotOriginX)
							&& (whyint[i] <= plotOriginY)
							&& (whyint[i] >= plotEndY)
							&& (conditionArray[i] > -1)) {

						g.setColor(this.pointColors[i]);
						g
								.drawOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
										RADIUS);
						// g.fillOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
						// RADIUS);
					}
				}
			}
			for (int i = 0; i < len; i++) {
				if ((exsint[i] <= this.plotEndX) && (exsint[i] >= plotOriginX)
						&& (whyint[i] <= plotOriginY)
						&& (whyint[i] >= plotEndY) && (conditionArray[i] > -1)) {

					if (this.selections[i] == 1) {
						if (this.selectionColor != this.background) {
							g.setColor(this.selectionColor);
							g.drawOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
									RADIUS);
							g.fillOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
									RADIUS);

						} else {
							g2.setColor(this.pointColors[i]);
							// g.setColor(this.foreground);
							g2.drawOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
									RADIUS);

							g2.fillOval(exsint[i] - 2, whyint[i] - 2,
									RADIUS + 4, RADIUS + 4);
							// g2.fillRect(exsint[i] - 1, whyint[i] - 1, RADIUS,
							// RADIUS);
							// g.drawOval(exsint[i] - 1, whyint[i] - 1,
							// RADIUS+1, RADIUS+1);
						}
					}
				}
			}

			Rectangle rec = new Rectangle(selectX, selectY, selectWidth,
					selectHeight);

			Graphics2D g2d = (Graphics2D) g;
			this.drawSelectRectangle(g2d, rec);
			if (this.multipleSelectionColors != null) {
				for (int i = 0; i < this.dataX.length; i++) {
					if (this.multipleSelectionColors[i] != null) {
						g.setColor(multipleSelectionColors[i]);
						g
								.drawOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
										RADIUS);
						g
								.fillOval(exsint[i] - 1, whyint[i] - 1, RADIUS,
										RADIUS);
					}
				}
			}

			this.drawDecisionRules(g);

		}
	}

	private void drawDecisionRules(Graphics g) {

		if (this.xDecisionInt != null) {
			g.setColor(this.foreground);
			logger.finest("x decision int not null.." + this.attributeX + " "
					+ this.xDecisionInt.length);
			for (int i = 0; i < this.xDecisionInt.length; i++) {
				g.drawLine(this.xDecisionInt[i], this.plotOriginY,
						this.xDecisionInt[i], this.plotEndY);
				logger.finest(" " + this.xDecisionInt[i] + " ");
			}
		}
		if (this.yDecisionInt != null) {
			for (int i = 0; i < this.yDecisionInt.length; i++) {
				g.drawLine(this.plotOriginX, this.yDecisionInt[i],
						this.plotEndX, this.yDecisionInt[i]);
			}
		}
	}

	/**
	 * Draw axises for scatterplot.
	 * 
	 * @param g
	 */
	private void drawAxis(Graphics g) {
		int plotWidth, plotHeight;
		plotWidth = this.getWidth();
		plotHeight = this.getHeight();
		logger.finest("plot height" + plotHeight);
		if (!(this.dataIndices[0] == this.dataIndices[1])) {

			logger.finest("origin x = " + plotOriginX);
			// draw the lines
			g.setColor(foreground);
			g.drawLine(plotOriginX, plotEndY, plotOriginX, plotOriginY);
			g.drawLine(plotOriginX, plotOriginY, plotEndX, plotOriginY);
			// draw tick bars for scales on Y coordinate
			int fontSize;
			fontSize = (plotWidth < plotHeight) ? plotWidth : plotHeight;
			fontSize = ((fontSize / 32) < 9) ? 9 : fontSize / 32;
			/*
			 * if (plotWidth < plotHeight){ if (plotWidth < 300){ fontSize = 9; }
			 * else { fontSize = (int)(plotWidth/32); } }else { if (plotHeight <
			 * 300){ fontSize = 9; } else { fontSize = (int)(plotHeight/32); } }
			 */
			Font font = new Font("", Font.PLAIN, fontSize);
			g.setFont(font);
			String scaleStringY;
			int i;
			int realBarNum = 0;
			double barNumber = this.dataArrayY.getTickNumber();
			int firstBar = (int) (yAxisExtents[0] / (this.dataArrayY
					.getMajorTick()));
			int lastBar = (int) (yAxisExtents[1] / (this.dataArrayY
					.getMajorTick()));
			// double barNumber = (yAxisExtents[1] -
			// yAxisExtents[0])/(this.dataArrayY.getMajorTick());
			double yBarDistance = ((plotOriginY - plotEndY) / barNumber);
			for (i = firstBar; i <= lastBar + 0.00001; i++) {
				// for (i = 0; i <= barNumber; i++) {
				g.drawLine(plotOriginX - 3, plotEndY
						+ (int) (realBarNum * yBarDistance), plotOriginX,
						plotEndY + (int) (realBarNum * yBarDistance));
				if (Math.abs(this.dataArrayY.getMajorTick()) <= 1) {
					scaleStringY = Float
							.toString((float) (yAxisExtents[1] - realBarNum
									* this.dataArrayY.getMajorTick()));
				} else {
					scaleStringY = Integer
							.toString((int) (yAxisExtents[1] - realBarNum
									* this.dataArrayY.getMajorTick()));
				}
				g
						.drawString(
								scaleStringY,
								plotOriginX
										- (int) (plotWidth * AXISSPACEPORTION / 2),
								plotEndY
										+ (int) (realBarNum * yBarDistance + yBarDistance * 1 / 6));

				// draw background grid
				g.setColor(Color.lightGray);
				g.drawLine(plotOriginX, plotEndY
						+ (int) (realBarNum * yBarDistance), plotEndX, plotEndY
						+ (int) (realBarNum * yBarDistance));
				g.setColor(this.foreground);
				realBarNum++;
			}
			// draw tick bars for scales on X coordinate
			realBarNum = 0;
			barNumber = this.dataArrayX.getTickNumber();
			barNumber = (xAxisExtents[1] - xAxisExtents[0])
					/ (this.dataArrayX.getMajorTick());
			double xBarDistance = ((plotEndX - plotOriginX) / barNumber);
			String scaleStringX;
			for (i = (int) (xAxisExtents[0] / (this.dataArrayX.getMajorTick())); i <= (int) (xAxisExtents[1] / (this.dataArrayX
					.getMajorTick())) + 0.0001; i++) {
				// for (i = 0; i <= barNumber; i++) {
				g.drawLine(plotOriginX + (int) (realBarNum * xBarDistance),
						plotOriginY, plotOriginX
								+ (int) (realBarNum * xBarDistance),
						plotOriginY + 3);
				if (Math.abs(this.dataArrayX.getMajorTick()) <= 1) {
					scaleStringX = Float
							.toString((float) (xAxisExtents[0] + realBarNum
									* this.dataArrayX.getMajorTick()));
				} else {
					scaleStringX = Integer
							.toString((int) (xAxisExtents[0] + realBarNum
									* this.dataArrayX.getMajorTick()));
				}
				Graphics2D g2d = (Graphics2D) g;
				g2d.rotate(-Math.PI / 2, plotOriginX - 2
						+ (int) (realBarNum * xBarDistance), plotOriginY
						+ plotHeight * AXISSPACEPORTION * 2 / 3);
				g.drawString(scaleStringX, plotOriginX - 2
						+ (int) (realBarNum * xBarDistance), plotOriginY
						+ (int) (plotHeight * AXISSPACEPORTION * 2 / 3 - 1));
				g2d.rotate(Math.PI / 2, plotOriginX - 2
						+ (int) (realBarNum * xBarDistance), plotOriginY
						+ plotHeight * AXISSPACEPORTION * 2 / 3);
				// graw background grid
				g.setColor(Color.lightGray);
				g.drawLine(plotOriginX + (int) (realBarNum * xBarDistance),
						plotOriginY, plotOriginX
								+ (int) (realBarNum * xBarDistance), plotEndY);
				g.setColor(this.foreground);
				realBarNum++;
			}
			font = new Font("", Font.PLAIN, fontSize + 3);
			g.setFont(font);
			// draw X axis attribute string
			g.drawString(attributeX, plotOriginX + (plotEndX - plotOriginX) / 2
					- plotWidth / 12, plotOriginY + plotHeight / 6 - 5);
			// draw Y axis attribute string. Need rotation for drawing the
			// string vertically.
			Graphics2D g2d = (Graphics2D) g;
			g2d.rotate(-Math.PI / 2, plotOriginX - plotWidth / 9, plotOriginY
					- (plotOriginY - plotEndY) / 3);
			g2d.drawString(attributeY, plotOriginX - plotWidth / 9, plotOriginY
					- (plotOriginY - plotEndY) / 3);
			g2d.rotate(+Math.PI / 2, plotOriginX - plotWidth / 9, plotOriginY
					- (plotOriginY - plotEndY) / 3);
		}
	}

	/**
	 * Return selections from this scatterplot.
	 * 
	 * @return
	 */
	public Vector getSelectedObservations() {
		return selRecords;
	}

	/**
	 * Set up selections from other components.
	 * 
	 * @param selectedObservations
	 */
	public void setSelectedObservations(Vector selectedObservations) {
		selRecords = selectedObservations;
	}

	public void setSelections(int[] selectedObservations) {
		if (selections.length != selectedObservations.length) {
			for (int i = 0; i < selections.length; i++) {
				selections[i] = 0;
			}
			for (int i = 0; i < selectedObservations.length; i++) {
				selections[selectedObservations[i]] = -1;
			}
		} else {
			selections = selectedObservations;
		}
	}

	/**
	 * Return selections from this scatterplot.
	 * 
	 * @return
	 */
	public int[] getSelections() {
		return this.selections;
	}

	public void setIndication(int indication) {
		// noop
	}

	/**
	 * Calculate scale between real data and integer data for showing up on
	 * screen.
	 * 
	 * @param min
	 * @param max
	 * @param dataMin
	 * @param dataMax
	 * @return scale
	 */
	private double getScale(int min, int max, double dataMin, double dataMax) {
		double scale;
		scale = (max - min) / (dataMax - dataMin);
		return scale;
	}

	/**
	 * Convert the single value to integer value worked on screen.
	 * 
	 * @param data
	 * @param scale
	 * @param min
	 * @param dataMin
	 * @return valueScreen
	 */
	private int getValueScreen(double data, double scale, int min,
			double dataMin) {
		int valueScreen;
		if (Double.isNaN(data)) {
			valueScreen = Integer.MIN_VALUE;
		} else {
			valueScreen = (int) ((data - dataMin) * scale + min);
		}
		return valueScreen;
	}

	/**
	 * Convert the numeric values of observations to integer value worked on
	 * screen.
	 * 
	 * @param dataArray
	 * @param scale
	 * @param min
	 * @param dataMin
	 * @return valueScreen
	 */
	private int[] getValueScreen(double[] dataArray, double scale, int min,
			double dataMin) {
		int[] valueScreen = new int[dataArray.length];
		for (int i = 0; i < dataArray.length; i++) {
			if (Double.isNaN(dataArray[i])) {
				valueScreen[i] = Integer.MIN_VALUE;
			} else {
				valueScreen[i] = (int) ((dataArray[i] - dataMin) * scale + min);
			}
		}
		return valueScreen;
	}

	private void setupDataforDisplay() {
		logger.finest("In setup data for display ...");
		this.setVisibleAxis(axisOn);
		if (dataArrayX == null) {
			return;
		}
		int len = dataArrayX.length();
		if (len != dataArrayY.length()) {
			return;
		}
		// exsint = new int[len];
		// whyint = new int[len];
		// get positions on screen
		double xScale;
		double yScale;
		xScale = getScale(plotOriginX, plotEndX, xAxisExtents[0],
				xAxisExtents[1]);
		exsint = getValueScreen(dataX, xScale, plotOriginX, xAxisExtents[0]);
		yScale = getScale(plotOriginY, plotEndY, yAxisExtents[0],
				yAxisExtents[1]);
		whyint = getValueScreen(dataY, yScale, plotOriginY, yAxisExtents[0]);
		// get class boundaries' positions on screen
		if (this.xBoundaries != null && this.yBoundaries != null) {
			logger.finest("x and y boundaries are not null.");
			this.xBoundariesInt = new int[this.xBoundaries.length];
			this.yBoundariesInt = new int[this.yBoundaries.length];
			this.xBoundariesInt = getValueScreen(this.xBoundaries, xScale,
					plotOriginX, xAxisExtents[0]);
			this.yBoundariesInt = getValueScreen(this.yBoundaries, yScale,
					plotOriginY, yAxisExtents[0]);
		}

		for (int i = 0; i < this.xDecision.size(); i++) {
			if (this.xDecision.elementAt(i) instanceof Double) {
				this.xDecisionInt[i] = this.getValueScreen(
						((Double) (this.xDecision.get(i))).doubleValue(),
						xScale, plotOriginX, xAxisExtents[0]);
			} else if (this.xDecision.elementAt(i) instanceof Integer) {
				this.xDecisionInt[i] = this.getValueScreen(
						((Integer) (this.xDecision.get(i))).doubleValue(),
						xScale, plotOriginX, xAxisExtents[0]);
				logger
						.finest("In setup data for display ..."
								+ xDecisionInt[i]);
			} else if (this.xDecision.elementAt(i) instanceof Vector) {
				this.xDecisionInt[i] = this.getValueScreen(
						((Integer) (((Vector) (this.xDecision.get(i))).get(0)))
								.doubleValue(), xScale, plotOriginX,
						xAxisExtents[0]);
			}
			logger.finest("In setup data for display ..." + xDecisionInt[i]);
		}
		for (int i = 0; i < this.yDecision.size(); i++) {
			if (this.yDecision.elementAt(i) instanceof Double) {
				this.yDecisionInt[i] = this.getValueScreen(
						((Double) (this.yDecision.get(i))).doubleValue(),
						yScale, plotOriginY, yAxisExtents[0]);
			} else if (this.yDecision.elementAt(i) instanceof Integer) {
				this.yDecisionInt[i] = this.getValueScreen(
						((Integer) (this.yDecision.get(i))).doubleValue(),
						yScale, plotOriginY, yAxisExtents[0]);
			} else if (this.xDecision.elementAt(i) instanceof Vector) {
				this.yDecisionInt[i] = this.getValueScreen(
						((Integer) (((Vector) (this.xDecision.get(i))).get(0)))
								.doubleValue(), yScale, plotOriginY,
						yAxisExtents[0]);
			}
		}
	}

	// start excentric labeling stuff
	private void initExcentricLabels() {
		this.exLabels = new ExcentricLabels();
		exLabels.setComponent(this);
		exLabels.setOpaque(true);
		Color halfWhite = new Color(255, 255, 255, 123);
		exLabels.setBackgroundColor(halfWhite);
		this.addMouseListener(exLabels);

	}

	public String getObservationLabel(int i) {
		String[] labels = this.observNames;
		String label = labels[i];
		return label;
	}

	public Shape getShapeAt(int i) {
		int x = this.exsint[i];
		int y = this.whyint[i];
		Ellipse2D circle = new Ellipse2D.Float(x, y,
				DecisionTreeScatterPlot.RADIUS, DecisionTreeScatterPlot.RADIUS);

		return circle;
	}

	public int[] pickAll(Rectangle2D hitBox) {
		Vector hits = new Vector();
		for (int i = 0; i < dataX.length; i++) {
			if (hitBox.contains(exsint[i], whyint[i])
					&& (conditionArray[i] > -1)) {
				Integer bigI = new Integer(i);
				hits.add(bigI);
			}
		}
		int[] intHits = new int[hits.size()];
		for (int i = 0; i < hits.size(); i++) {

			intHits[i] = ((Integer) hits.get(i)).intValue();
		}
		return intHits;
	}

	// end excentric labeling stuff

	public void componentHidden(ComponentEvent e) {

	}

	public void componentMoved(ComponentEvent e) {

	}

	public void componentResized(ComponentEvent e) {
		logger.finest("in component resized");
		this.setupDataforDisplay();
		this.repaint();
	}

	public void componentShown(ComponentEvent e) {
	}

	/**
	 * Begin the drawing of selection region (box).
	 * 
	 * @param e
	 */
	public void mousePressed(MouseEvent e) {
		if (dataIndices[0] == dataIndices[1]) {
			return;
		}
		if (e.isPopupTrigger()) {
			maybeShowPopup(e);
		}
		// selRecords.clear();
		mouseX1 = e.getX();
		mouseY1 = e.getY();
	}

	/**
	 * Work with mouseDragged to draw a selection region (box) for selection.
	 * 
	 * @param e
	 */
	public void mouseReleased(MouseEvent e) {
		if (dataIndices[0] == dataIndices[1]) {
			return;
		}
		if (e.isPopupTrigger()) {
			maybeShowPopup(e);
		}
		mouseX2 = e.getX();
		mouseY2 = e.getY();
		if ((Math.abs(mouseX1 - mouseX2) < 3)
				&& (Math.abs(mouseY1 - mouseY2) < 3)) {
			return;
		}
		// With shift pressed, it will continue to select.
		if (!(e.isShiftDown())) {
			// selRecords.clear();
			// zero all selection indication to deselect them.
			for (int i = 0; i < this.selections.length; i++) {
				this.selections[i] = 0;
			}
		}
		if (mouseX1 <= mouseX2 && mouseY1 <= mouseY2) {
			selectX = mouseX1;
			selectY = mouseY1;
			selectWidth = mouseX2 - mouseX1;
			selectHeight = mouseY2 - mouseY1;
		}
		if (mouseX2 < mouseX1 && mouseY1 <= mouseY2) {
			selectX = mouseX2;
			selectY = mouseY1;
			selectWidth = mouseX1 - mouseX2;
			selectHeight = mouseY2 - mouseY1;
		}
		if (mouseX1 <= mouseX2 && mouseY2 < mouseY1) {
			selectX = mouseX1;
			selectY = mouseY2;
			selectWidth = mouseX2 - mouseX1;
			selectHeight = mouseY1 - mouseY2;
		}
		if (mouseX2 < mouseX1 && mouseY2 < mouseY1) {
			selectX = mouseX2;
			selectY = mouseY2;
			selectWidth = mouseX1 - mouseX2;
			selectHeight = mouseY1 - mouseY2;
		}
		Rectangle rec = new Rectangle(selectX, selectY, selectWidth,
				selectHeight);
		int j = 0;
		for (int i = 0; i < dataX.length; i++) {
			if (rec.contains(exsint[i], whyint[i]) && (conditionArray[i] > -1)) {

				this.selections[i] = 1;// new selection struction int[]
				j++;
			}
		}
		selectWidth = 0;
		selectHeight = 0;
		// selRecords.trimToSize();
		this.multipleSelectionColors = null;
		// this.drawSelectRectangle(rec);
		repaint();
		logger.finest("about to fire mouse released");
		fireActionPerformed(COMMAND_POINT_SELECTED);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param e
	 */
	public void mouseExited(MouseEvent e) {
		logger.finest("mouse exited: ");
	}

	/**
	 * Work with mouseReleased to draw a selection region (box) for selection.
	 * 
	 * @param e
	 */
	public void mouseDragged(MouseEvent e) {
		if (dataIndices[0] == dataIndices[1]) {
			return;
		}
		mouseX2 = e.getX();
		mouseY2 = e.getY();
		if ((Math.abs(mouseX1 - mouseX2) < 3)
				&& (Math.abs(mouseY1 - mouseY2) < 3)) {
			return;
		}
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("mouse dragged: " + "mouseX2" + mouseX2 + "mouseY2"
					+ mouseY2);
		}
		if (mouseX1 <= mouseX2 && mouseY1 <= mouseY2) {
			selectX = mouseX1;
			selectY = mouseY1;
			selectWidth = mouseX2 - mouseX1;
			selectHeight = mouseY2 - mouseY1;
		}
		if (mouseX2 < mouseX1 && mouseY1 <= mouseY2) {
			selectX = mouseX2;
			selectY = mouseY1;
			selectWidth = mouseX1 - mouseX2;
			selectHeight = mouseY2 - mouseY1;
		}
		if (mouseX1 <= mouseX2 && mouseY2 < mouseY1) {
			selectX = mouseX1;
			selectY = mouseY2;
			selectWidth = mouseX2 - mouseX1;
			selectHeight = mouseY1 - mouseY2;
		}
		if (mouseX2 < mouseX1 && mouseY2 < mouseY1) {
			selectX = mouseX2;
			selectY = mouseY2;
			selectWidth = mouseX1 - mouseX2;
			selectHeight = mouseY1 - mouseY2;
		}

		repaint();
	}

	/**
	 * Mouse over, it will show the values for current point by tool tip.
	 * 
	 * @param e
	 */
	public void mouseMoved(MouseEvent e) {
		if (e != null && this.axisOn == false) {
			this.makeToolTip(e.getX(), e.getY());
			e.consume();
		}
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param e
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Mouse click for selecting or brushing points (observations).
	 * 
	 * @param e
	 */
	public void mouseClicked(MouseEvent e) {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("mouse clicked: ");
		}
		int count = e.getClickCount();
		int[] mousePos = new int[2];
		mousePos[0] = e.getX();
		mousePos[1] = e.getY();
		// single click, select performed.
		if (count == 1) {
			if (dataIndices[0] != dataIndices[1]) {
				for (int i = 0; i < dataX.length; i++) {
					if ((exsint[i] - 5 < mousePos[0])
							&& (mousePos[0] < exsint[i] + 5)
							&& (whyint[i] - 5 < mousePos[1])
							&& (mousePos[1] < whyint[i] + 5)
							&& (conditionArray[i] > -1)) {
						this.selections[i] = 1;
					}
				}

				fireActionPerformed(COMMAND_POINT_SELECTED);
			}
		}
		// double click, pop up a detail scatter plot.
		if (count == 2) // This is a double-click or triple...
		{
			if (dataIndices[0] != dataIndices[1]) {

				DecisionTreeScatterPlot detailSP = new DecisionTreeScatterPlot();
				detailSP.setDataSet(dataSet);
				detailSP.setAxisOn(true);
				detailSP.setBackground(background);
				detailSP.setElementPosition(dataIndices);
				detailSP.setBivarColorClasser(this.bivarColorClasser, false);
				detailSP.setColorArrayForObs(this.colorArrayForObs);
				detailSP.setSelectionColor(this.selectionColor);
				detailSP.setSelections(this.selections);
				detailSP.setXAxisExtents(this.xAxisExtents);// ?
				detailSP.setYAxisExtents(this.yAxisExtents);
				JFrame dummyFrame = new JFrame();
				JDialog dlgSP = new JDialog(dummyFrame,
						"Detailed Scatter Plot", true);
				dlgSP.setLocation(300, 300);
				dlgSP.setSize(300, 300);
				dlgSP.getContentPane().setLayout(new BorderLayout());
				dlgSP.getContentPane().add(detailSP, BorderLayout.CENTER);
				detailSP.addActionListener(new ActionListener() {

					/**
					 * put your documentation comment here
					 * 
					 * @param e
					 */
					public void actionPerformed(ActionEvent e) {
						if (logger.isLoggable(Level.FINEST)) {
							System.out
									.println("something came from detailed one.");
						}
						DecisionTreeScatterPlot detailSP = (DecisionTreeScatterPlot) e
								.getSource();
						String command = e.getActionCommand();
						if (command
								.compareTo(DecisionTreeScatterPlot.COMMAND_POINT_SELECTED) == 0) {
							if (logger.isLoggable(Level.FINEST)) {
								System.out
										.println("DecisionTreeSP.mouseClicked.actionPerformed(), point selected");
							}

							int[] selections = detailSP.getSelections();
							// Don't recall the scatterplot which generated the
							// original event

							DecisionTreeScatterPlot.this
									.setSelections(selections);
							DecisionTreeScatterPlot.this
									.fireActionPerformed(COMMAND_POINT_SELECTED);
						} else if (command
								.compareTo(DecisionTreeScatterPlot.COMMAND_DATARANGE_SET) == 0) {
							double[] dataArrayX = detailSP.getXAxisExtents();
							double[] dataArrayY = detailSP.getYAxisExtents();
							DecisionTreeScatterPlot.this
									.setXAxisExtents(dataArrayX);
							DecisionTreeScatterPlot.this
									.setYAxisExtents(dataArrayY);
							fireActionPerformed(COMMAND_DATARANGE_SET);
						}
						// System.err.println("Unknown command! = " + command);
					}
				});
				dlgSP.setVisible(true);
			} else {
				Histogram histogram = new Histogram();
				histogram.setVariableName(this.attributeX);
				histogram.setData(this.dataX);
				// histogram.setSelections(this.selections);
				histogram.setBackground(background);
				JFrame dummyFrame = new JFrame();
				JDialog dlgSP = new JDialog(dummyFrame, "Histogram", true);
				dlgSP.setLocation(300, 300);
				dlgSP.setSize(300, 300);
				dlgSP.getContentPane().setLayout(new BorderLayout());
				dlgSP.getContentPane().add(histogram, BorderLayout.CENTER);
				histogram.addActionListener(new ActionListener() {

					/**
					 * put your documentation comment here
					 * 
					 * @param e
					 */
					public void actionPerformed(ActionEvent e) {
						Histogram histogram = (Histogram) e.getSource();
						BitSet bSet = histogram.getSelections();
						int[] selObs = new int[bSet.size()];

						for (int i = 0; i < bSet.size(); i++) {
							if (bSet.get(i)) {
								selObs[i] = 1;
							} else {
								selObs[i] = -1;
							}
						}

						DecisionTreeScatterPlot.this.setSelections(selObs);
						DecisionTreeScatterPlot.this
								.fireActionPerformed(COMMAND_POINT_SELECTED);
					}
				});
				dlgSP.setVisible(true);
			}
		}
	}

	private void drawSelectRectangle(Graphics2D g2d, Rectangle rec) {

		Stroke tempStroke = g2d.getStroke();
		float[] dash = new float[3];
		dash[0] = (float) 5.0;
		dash[1] = (float) 7.0;
		dash[2] = (float) 5.0;
		BasicStroke dashStroke = new BasicStroke((float) 1.0,
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, (float) 10.0,
				dash, 0);
		g2d.setStroke(dashStroke);
		g2d.setPaintMode();
		g2d.setColor(foreground);
		g2d.drawRect(selectX, selectY, selectWidth, selectHeight);
		// Draw selected observations.
		// if (!selRecords.isEmpty()) {
		// g2d.setXORMode(background);
		g2d.drawRect(selectX, selectY, selectWidth, selectHeight);
		g2d.setStroke(tempStroke);
		// g2d.setColor(selectionColor);
		// int i = 0;
		// for (Enumeration selEnum = selRecords.elements();
		// selEnum.hasMoreElements();) {
		// i = ((Integer)selEnum.nextElement()).intValue();
		// if ((exsint[i] != Integer.MIN_VALUE) && (whyint[i] !=
		// Integer.MIN_VALUE)
		// && (conditionArray[i] > -1)) {
		// g.drawOval(exsint[i] - 1, whyint[i] - 1, RADIUS, RADIUS);
		// g.fillOval(exsint[i] - 1, whyint[i] -1 , RADIUS, RADIUS);
		// }
		// }
		// g.setColor(foreground);
		// }

		/*
		 * if (g == null) { return; } BasicStroke strokeSave = (BasicStroke)
		 * g.getStroke();
		 * 
		 * float[] dash = new float[3]; dash[0] = (float)5.0; dash[1] =
		 * (float)7.0; dash[2] = (float)5.0; if (strokeDashed == null) { // We
		 * only need to create this once. We can't do it in our initialize(),
		 * since // we don't have a Graphics2D yet strokeDashed = new
		 * BasicStroke( strokeSave.getLineWidth(), strokeSave.getEndCap(),
		 * strokeSave.getLineJoin(), strokeSave.getMiterLimit(), dash, 0); } //
		 * Now, set our stroke to a dashed line g.setStroke(strokeDashed);
		 * 
		 * g.setColor(this.foreground); g.setXORMode(this.background); //
		 * Actually draw the rectangle g.drawRect(rec.x, rec.y, rec.width,
		 * rec.height); // Restore our original stroke g.setStroke(strokeSave);
		 */
	}

	private void makeToolTip(int x, int y) {
		int arrayIndex = -1;
		// boolean pointMove = false;
		if (dataIndices == null) {
			return;
		}
		if (exsint == null) {
			return;
		}
		if ((dataIndices[0] != dataIndices[1]) && (exsint != null)
				&& (whyint != null)) {
			for (int i = 0; i < dataX.length; i++) {
				if ((exsint[i] - 3 < x) && (x < exsint[i] + 3)
						&& (whyint[i] - 3 < y) && (y < whyint[i] + 3)
						&& (conditionArray[i] > -1)) {
					// pointMove = true;
					arrayIndex = i;
				}
			}
		}
		if (arrayIndex >= 0) {
			// setting multi-line tool tip
			String xVal = Double.toString(dataX[arrayIndex]);
			String yVal = Double.toString(dataY[arrayIndex]);
			String s = "<html> ";
			if (this.observNames != null) {
				s = s + "Name = " + observNames[arrayIndex] + "<br>";
			}

			s = s + attributeX + " = " + xVal + "<br>" + attributeY + " = "
					+ yVal + "</html>";

			this.setToolTipText(s);
		} // end if
	}

	/**
	 * New data ranges setup dialog.
	 * 
	 * @param x
	 * @param y
	 */
	private void showDialog(int x, int y) {
		JFrame dummyFrame = new JFrame();
		JDialog dialog = new JDialog(dummyFrame, "Data Range Configuer", true);
		JButton actionButton;
		JButton resetButton;
		dialog.setLocation(x, y);
		dialog.setSize(300, 150);
		dialog.getContentPane().setLayout(new GridLayout(5, 2));
		xAxisMinField.setText(Double.toString(xAxisExtents[0]));
		xAxisMaxField.setText(Double.toString(xAxisExtents[1]));
		yAxisMinField.setText(Double.toString(yAxisExtents[0]));
		yAxisMaxField.setText(Double.toString(yAxisExtents[1]));
		// create buttons for action
		actionButton = new JButton("OK");
		actionButton.addActionListener(new java.awt.event.ActionListener() {

			/**
			 * Button to set up new data ranges shown up in scatter plot.
			 * 
			 * @param e
			 */
			public void actionPerformed(ActionEvent e) {
				try {
					actionButton_actionPerformed(e);
				} catch (Exception exception) {
				}
			}
		});
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new java.awt.event.ActionListener() {

			/**
			 * put your documentation comment here
			 * 
			 * @param e
			 */
			public void actionPerformed(ActionEvent e) {
				resetButton_actionPerformed(e);
			}
		});
		dialog.getContentPane().add(new JLabel((this.attributeX + " Min")));
		dialog.getContentPane().add(xAxisMinField);
		dialog.getContentPane().add(new JLabel((this.attributeX + " Max")));
		dialog.getContentPane().add(xAxisMaxField);
		dialog.getContentPane().add(new JLabel((this.attributeY + " Min")));
		dialog.getContentPane().add(yAxisMinField);
		dialog.getContentPane().add(new JLabel((this.attributeY + " Max")));
		dialog.getContentPane().add(yAxisMaxField);
		dialog.getContentPane().add(actionButton);
		dialog.getContentPane().add(resetButton);
		dialog.setVisible(true);
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param e
	 */
	private void maybeShowPopup(MouseEvent e) {
		{
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Set up new data ranges to show.
	 * 
	 * @param e
	 */
	private void actionButton_actionPerformed(ActionEvent e) {
		// get the input data from text field
		xAxisExtents[0] = Double.parseDouble(xAxisMinField.getText());
		xAxisExtents[1] = Double.parseDouble(xAxisMaxField.getText());
		yAxisExtents[0] = Double.parseDouble(yAxisMinField.getText());
		yAxisExtents[1] = Double.parseDouble(yAxisMaxField.getText());
		this.dataArrayX.setExtent(xAxisExtents);
		this.dataArrayY.setExtent(yAxisExtents);
		this.setupDataforDisplay();
		fireActionPerformed(COMMAND_DATARANGE_SET);
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("ok, fire event.");
		}
		repaint();
	}

	/**
	 * put your documentation comment here
	 * 
	 * @param e
	 */
	private void resetButton_actionPerformed(ActionEvent e) {
		this.dataArrayX.setDataExtent();
		this.dataArrayY.setDataExtent();
		if (axisOn) {
			xAxisExtents = this.dataArrayX.getMaxMinCoorValue().clone();
			yAxisExtents = this.dataArrayY.getMaxMinCoorValue().clone();
		} else {
			xAxisExtents[0] = dataArrayX.getExtent()[0];
			xAxisExtents[1] = dataArrayX.getExtent()[1];
			yAxisExtents[0] = dataArrayY.getExtent()[0];
			yAxisExtents[1] = dataArrayY.getExtent()[1];
		}
		xAxisMinField.setText(Double.toString(xAxisExtents[0]));
		xAxisMaxField.setText(Double.toString(xAxisExtents[1]));
		yAxisMinField.setText(Double.toString(yAxisExtents[0]));
		yAxisMaxField.setText(Double.toString(yAxisExtents[1]));
		this.setupDataforDisplay();
		fireActionPerformed(COMMAND_DATARANGE_SET);
		repaint();
	}

	/**
	 * adds an IndicationListener to the button
	 */
	public void addIndicationListener(IndicationListener l) {
		listenerList.add(IndicationListener.class, l);
	}

	/**
	 * removes an IndicationListener from the button
	 */
	public void removeIndicationListener(IndicationListener l) {
		listenerList.remove(IndicationListener.class, l);
	}

	/**
	 * adds an ActionListener to the button
	 */
	public void addActionListener(ActionListener l) {
		listenerListAction.add(ActionListener.class, l);
	}

	/**
	 * removes an ActionListener from the button
	 */
	public void removeActionListener(ActionListener l) {
		listenerListAction.remove(ActionListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	public void fireActionPerformed(String command) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerListAction.getListenerList();
		ActionEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
							command);
				}
				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}

	/**
	 * If AxisOn is true, it will be a detailed version of scatterplot with
	 * axises.
	 * 
	 * @return
	 */
	public boolean isAxisOn() {
		return axisOn;
	}

	/**
	 * Sets colors for the current data.
	 */
	public void setBivarColorClasser(
			BivariateColorSymbolClassification bivarColorClasser,
			boolean reverseColor) {
		this.bivarColorClasser = bivarColorClasser;
		this.makeColors();
		classColors = this.bivarColorClasser.getClassColors();
		int numClasses;

		try {
			xClasser = (BoundaryClassifier) this.bivarColorClasser
					.getClasserX();
			numClasses = this.bivarColorClasser.getXColorSymbolizer()
					.getNumClasses();
			logger.finest("num classes" + numClasses);
			xBoundaries = xClasser.getEqualBoundaries(this.dataX, numClasses);
			yClasser = (BoundaryClassifier) this.bivarColorClasser
					.getClasserY();
			numClasses = this.bivarColorClasser.getYColorSymbolizer()
					.getNumClasses();
			yBoundaries = yClasser.getEqualBoundaries(this.dataY, numClasses);
		} catch (ClassCastException ex) {

		}
		repaint();
	}

	public BivariateColorSymbolClassification getBivarColorClasser() {
		return this.bivarColorClasser;
	}

	public void makeColors() {
		if (this.dataX != null && this.dataY != null) {
			this.pointColors = this.bivarColorClasser.symbolize(dataX, dataY);
		}
	}

	/**
	 * Test file.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame app = new JFrame();
		DecisionTreeScatterPlot sp = new DecisionTreeScatterPlot();
		app.getContentPane().add(sp);
		app.setVisible(true);
	}

	public Vector getTreeVector() {
		return treeVector;
	}

	public void setTreeVector(Vector treeVector) {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("in scatterplot, set decision tree..."
					+ treeVector.size());
		}
		this.treeVector = treeVector;
		this.xDecision.clear();
		this.yDecision.clear();
		if (this.treeVector != null) {
			for (int i = 0; i < this.treeVector.size(); i++) {
				String att = (String) (((Vector) (this.treeVector.get(i)))
						.get(2));
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest(this.attributeX + " " + att);
				}
				if (this.attributeX.equals(att)) {
					xDecision.add(((Vector) (this.treeVector.get(i))).get(4));
					logger.finest("treeVector"
							+ ((Vector) (this.treeVector.get(i))).get(4));
				}
				if (this.attributeY.equals(att)) {
					yDecision.add(((Vector) (this.treeVector.get(i))).get(4));
				}
			}
			this.xDecision.trimToSize();
			this.yDecision.trimToSize();
		}
		this.xDecisionInt = new int[this.xDecision.size()];
		this.yDecisionInt = new int[this.yDecision.size()];
		this.setupDataforDisplay();
		this.repaint();
	}

}

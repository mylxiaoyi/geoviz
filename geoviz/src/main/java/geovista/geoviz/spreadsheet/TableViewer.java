package geovista.geoviz.spreadsheet;

/*
 * Description:
 * Date: Feb 9, 2003
 * Time: 12:09:50 PM
 * @author Jin Chen
 */

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.EventListenerList;

import geovista.common.data.DataSetForApps;
import geovista.common.event.DataSetEvent;
import geovista.common.event.DataSetListener;
import geovista.common.event.SelectionEvent;
import geovista.common.event.SelectionListener;
import geovista.coordination.CoordinationManager;
import geovista.geoviz.descriptive.DescriptiveStats;
import geovista.geoviz.sample.GeoDataGeneralizedStates;
import geovista.geoviz.shapefile.ShapeFileProjection;
import geovista.toolkitcore.ToolkitBean;

public class TableViewer extends JPanel implements SelectionListener,
		DataSetListener {

	private DescriptiveStats stats;
	private transient DataSetForApps dataSet;

	private JTable table;
	JScrollPane scrollPane;

	public TableViewer() {
		// super(new GridLayout(1, 0));
		super(new BorderLayout());
		init();
		setPreferredSize(new Dimension(300, 200));
	}

	private void init() {
		if (dataSet == null) {
			dataSet = getDefaultData();
		}

		// clear out whatever was there before
		removeAll();

		table = new JTable(dataSet);
		// table.setColumnSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(new Dimension(300, 70));
		table.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		// Add the scroll pane to this panel.
		add(scrollPane);
		stats = new DescriptiveStats();
		add(stats, BorderLayout.SOUTH);
		setVisible(true);
	}

	DataSetForApps getDefaultData() {
		String[] columnNames = { "First Name", "Age", "Weight" };

		String[] names = { "Frank", "HyangJa", "Hannah", "Lena", "Alex" };
		double[] ages = { 40, 35, 6, 3, 1 };
		double[] weight = { 140, 104, 45, 30, 20 };

		Object[] dataArrays = { columnNames, names, ages, weight };
		DataSetForApps data = new DataSetForApps(dataArrays);

		return data;
	}

	/*
	 * private DefaultTableModel getTableModel(){ SSTableModel
	 * sstbm=(SSTableModel) SSTableModel.getInstance() ; return sstbm; }
	 */
	/***************************************************************************
	 * Bean property
	 **************************************************************************/
	public void dataSetChanged(DataSetEvent e) {

		dataSet = e.getDataSetForApps();
		setDataSet(e.getDataSetForApps());
		e.getDataSetForApps().addTableModelListener(table);
		stats.dataSetChanged(e);
	}

	public void selectionChanged(SelectionEvent e) {
		table.clearSelection();
		int[] selVals = e.getSelection();
		for (int i : selVals) {
			table.addRowSelectionInterval(i, i);
		}
		// this.setSelectedIndex(e.getSelection());
		stats.selectionChanged(e);

	}

	public SelectionEvent getSelectionEvent() {
		return new SelectionEvent(this, stats.getSelectionEvent()
				.getSelection());
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	private void fireSelectionChanged(int[] newSelection) {

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		SelectionEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new SelectionEvent(this, newSelection);
				}
				((SelectionListener) listeners[i + 1]).selectionChanged(e);
			}
		}// next i

	}

	/**
	 * adds an SelectionListener
	 */
	public void addSelectionListener(SelectionListener l) {
		listenerList.add(SelectionListener.class, l);
	}

	/**
	 * removes an SelectionListener from the component
	 */
	public void removeSelectionListener(SelectionListener l) {
		listenerList.remove(SelectionListener.class, l);

	}

	/***************************************************************************
	 * GeoVista Function/interface
	 **************************************************************************/
	/*
	 * for use with coordinator
	 */
	public void setDataSet(DataSetForApps dataSet) {
		this.dataSet = dataSet;

		init();

		/*
		 * 
		 * String[] attributeNames = dataSet.getAttributeNamesOriginal(); int
		 * numObs = dataSet.getNumObservations(); Object[][] tablesData = new
		 * Object[numObs][attributeNames.length];
		 * 
		 * double[] doubleData = null; int[] intData = null; String[] stringData =
		 * null; boolean[] boolData = null;
		 * 
		 * for (int column = 0; column < attributeNames.length; column++) { //
		 * Object datum = data[column+1]; Object datum =
		 * dataSet.getNamedArrays()[column];// XXX is this the // right
		 * accessor? if (datum instanceof double[]) { doubleData = (double[])
		 * datum; for (int row = 0; row < numObs; row++) { double dat =
		 * doubleData[row]; Double d = new Double(dat); tablesData[row][column] =
		 * d; } }
		 * 
		 * if (datum instanceof int[]) { intData = (int[]) datum; for (int row =
		 * 0; row < numObs; row++) { int dat = intData[row]; Integer d = new
		 * Integer(dat); tablesData[row][column] = d; } }
		 * 
		 * if (datum instanceof String[]) {
		 * 
		 * stringData = (String[]) datum; for (int row = 0; row < numObs; row++) {
		 * String dat = stringData[row]; tablesData[row][column] = dat; } }
		 * 
		 * if (datum instanceof boolean[]) { boolData = (boolean[]) datum; for
		 * (int row = 0; row < numObs; row++) { boolean dat = boolData[row];
		 * Boolean d = new Boolean(dat); tablesData[row][column] = d; } } }
		 * 
		 * this.setData(tablesData, attributeNames);
		 */
	}

	/***************************************************************************
	 * Test
	 **************************************************************************/
	public static void main(String args[]) {

		JFrame mf = new JFrame();
		TableViewer tView = new TableViewer();
		mf.getContentPane().add(tView);

		CoordinationManager coord = new CoordinationManager();
		ShapeFileProjection shpProj = new ShapeFileProjection();
		GeoDataGeneralizedStates stateData = new GeoDataGeneralizedStates();
		coord.addBean(tView);
		coord.addBean(shpProj);
		shpProj.setInputDataSet(stateData.getDataSet());

		DataSetForApps dataSet = shpProj.getOutputDataSetForApps();
		for (int row = 0; row < dataSet.getRowCount(); row++) {
			for (int column = 0; column < dataSet.getColumnCount(); column++) {
				Object obj = dataSet.getValueAt(row, column);
				System.out.println("row = " + row + ", col = " + column
						+ ", obj = " + obj);
			}
		}

		mf.pack();
		mf.setVisible(true);

		JTable table = new JTable(dataSet);
		JFrame fram = new JFrame();
		JDesktopPane top = new JDesktopPane();
		ToolkitBean tBean = new ToolkitBean(table, "table");
		top.add(tBean.getInternalFrame());
		fram.getContentPane().add(top);
		fram.pack();
		// fram.setVisible(true);

	}
}

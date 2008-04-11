/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.common.event;

import java.util.EventObject;

import geovista.common.data.DataSetForApps;

/**
 * An DataSetEvent signals that a new data set is available.
 * 
 */
public class DataSetEvent extends EventObject {

	private final Object[] dataSet;
	private transient DataSetForApps dataSetForApps;

	/**
	 * The constructor is the same as that for EventObject, except that the
	 * dataSet values are indicated.
	 */
	@Deprecated
	public DataSetEvent(Object source, Object[] dataSet) {
		super(source);
		this.dataSet = dataSet;
	}

	/**
	 * Note that the DataSetForApps is first. This allows us to pass in a null
	 * DataSetForApps if desired, without being confused with the previous cntr.
	 */
	public DataSetEvent(DataSetForApps dataSetForApps, Object source) {
		super(source);
		this.dataSetForApps = dataSetForApps;
		dataSet = dataSetForApps.getDataObjectOriginal();
	}

	public Object[] getDataSet() {
		return dataSet;
	}

	public DataSetForApps getDataSetForApps() {
		// let's be lazy
		if (dataSetForApps == null) {
			dataSetForApps = new DataSetForApps(dataSet);
		}
		return dataSetForApps;
	}
}

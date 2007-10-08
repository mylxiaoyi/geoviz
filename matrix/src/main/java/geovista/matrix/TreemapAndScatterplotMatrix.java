/* -------------------------------------------------------------------
 GeoVISTA Center (Penn State, Dept. of Geography)
 Java source file for the class TreemapAndScatterplotMatrix
 Copyright (c), 2003, Frank Hardisty
 All Rights Reserved.
 Original Author: Frank Hardisty
 $Author: hardisty $
 $Id: TreemapAndScatterplotMatrix.java,v 1.1 2005/03/19 00:48:39 hardisty Exp $
 $Date: 2005/03/19 00:48:39 $
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
package geovista.matrix;

import geovista.matrix.scatterplot.ScatterPlot;
import geovista.matrix.treemap.TreeMapMatrixElement;


/**
 *
 * @author Frank Hardisty
 * @version $Revision: 1.1 $
 */
public class TreemapAndScatterplotMatrix
    extends BiPlotMatrix
    //implements DataSetListener, DataSetModifiedListener,
    //IndicationListener, SubspaceListener,
    //ColorArrayListener
{

  public TreemapAndScatterplotMatrix() {
    super();
    this.setElementClass1(ScatterPlot.class);
    this.setElementClass2(TreeMapMatrixElement.class);

  }



} //end class
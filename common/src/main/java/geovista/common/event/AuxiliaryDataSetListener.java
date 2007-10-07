/* -------------------------------------------------------------------
 GeoVISTA Center (Penn State, Dept. of Geography)
 Java source file for the class AuxiliaryDataSetListener
 Copyright (c), 2002, GeoVISTA Center
 Original Author: Frank Hardisty
 $Author: hardisty $
 $Id: AuxiliaryDataSetListener.java,v 1.1 2005/02/19 02:17:05 hardisty Exp $
 $Date: 2005/02/19 02:17:05 $
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


package geovista.common.event;

import java.util.EventListener;


/**
 * This interface enables listening to senders of AuxiliaryDataSetEvents.
 *
 *
 */
public interface AuxiliaryDataSetListener extends EventListener {
    /**
     * Addded instead of changed fits the semantics better.
     *
     *
 */

  public void dataSetAdded(AuxiliaryDataSetEvent e);


}

/* -------------------------------------------------------------------
 Java source file for the class RemoteSpatialExtentListener
 Original Authors: Frank Hardisty and Linna Li
 $Author: hardisty $
 $Id: RemoteSpatialExtentListener.java,v 1.2 2006/02/27 19:28:41 hardisty Exp $
 $Date: 2006/02/27 19:28:41 $
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

package geovista.collaboration;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteSpatialExtentListener
    extends Remote {
  public void extentChanged(String source, double[] extent) throws RemoteException;
}

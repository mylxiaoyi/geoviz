/* -------------------------------------------------------------------
 GeoVISTA Center (Penn State, Dept. of Geography)
 Java source file for the class ClassColorListener
 Copyright (c), 2002, GeoVISTA Center
 All Rights Reserved.
 Original Author: Xiping Dai
 $Author: xpdai $
 $Id: ClassColorListener.java,v 1.1 2004/03/03 18:17:07 xpdai Exp $
 $Date: 2004/03/03 18:17:07 $
 Reference:		Document no:
 ___				___
 -------------------------------------------------------------------  *
 */


package edu.psu.geovista.common.event;

import java.util.EventListener;


/**
 * This interface enables listening to senders of IndicationEvents.
 *
 * This interface also enables "fireClassColorChanged" methods in classes
 * that generate and broadcast ClassColorEvents.
 *
 */
public interface ClassColorListener extends EventListener {


  public void classColorChanged(ClassColorEvent e);


}
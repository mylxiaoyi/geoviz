/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.matrix;

import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;

public class MapMatrixBeanInfo extends SimpleBeanInfo {
	Class beanClass = MapMatrix.class;
	String iconColor16x16Filename = "resources/matrix16.gif";
	String iconColor32x32Filename = "resources/matrix32.gif";
	String iconMono16x16Filename;
	String iconMono32x32Filename;

	@Override
	public java.awt.Image getIcon(int iconKind) {
		switch (iconKind) {
		case BeanInfo.ICON_COLOR_16x16:
			return iconColor16x16Filename != null
					? loadImage(iconColor16x16Filename) : null;
		case BeanInfo.ICON_COLOR_32x32:
			return iconColor32x32Filename != null
					? loadImage(iconColor32x32Filename) : null;
		case BeanInfo.ICON_MONO_16x16:
			return iconMono16x16Filename != null
					? loadImage(iconMono16x16Filename) : null;
		case BeanInfo.ICON_MONO_32x32:
			return iconMono32x32Filename != null
					? loadImage(iconMono32x32Filename) : null;
		}
		return null;
	}

}
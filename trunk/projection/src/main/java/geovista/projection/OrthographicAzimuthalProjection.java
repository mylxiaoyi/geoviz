/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

/*
 * This file was semi-automatically converted from the public-domain USGS PROJ source.
 */
package geovista.projection;

import java.awt.geom.Point2D;

import geovista.projection.units.MapMath;
import geovista.projection.units.ProjectionException;

/**
 * The Orthographic Azimuthal or Globe map projection.
 */
public class OrthographicAzimuthalProjection extends AzimuthalProjection {

	public OrthographicAzimuthalProjection() {
		initialize();
	}

	@Override
	public Point2D.Double project(double lam, double phi, Point2D.Double xy) {
		double sinphi;
		double cosphi = Math.cos(phi);
		double coslam = Math.cos(lam);

		// Theoretically we should throw the ProjectionExceptions below, but for
		// practical purposes
		// it's better not to as they tend to crop up a lot up due to rounding
		// errors.
		switch (mode) {
		case EQUATOR:
			// if (cosphi * coslam < - EPS10)
			// throw new ProjectionException();
			xy.y = Math.sin(phi);
			break;
		case OBLIQUE:
			sinphi = Math.sin(phi);
			// if (sinphi0 * (sinphi) + cosphi0 * cosphi * coslam < - EPS10)
			// ;
			// throw new ProjectionException();
			xy.y = cosphi0 * sinphi - sinphi0 * cosphi * coslam;
			break;
		case NORTH_POLE:
			coslam = -coslam;
			//$FALL-THROUGH$
		case SOUTH_POLE:
			// if (Math.abs(phi - projectionLatitude) - EPS10 > MapMath.HALFPI)
			// throw new ProjectionException();
			xy.y = cosphi * coslam;
			break;
		}
		xy.x = cosphi * Math.sin(lam);
		return xy;
	}

	@Override
	public Point2D.Double projectInverse(double x, double y, Point2D.Double lp) {
		double rh, cosc, sinc;

		if ((sinc = (rh = MapMath.distance(x, y))) > 1.) {
			if ((sinc - 1.) > EPS10) {
				throw new ProjectionException();
			}
			sinc = 1.;
		}
		cosc = Math.sqrt(1. - sinc * sinc); /* in this range OK */
		if (Math.abs(rh) <= EPS10) {
			lp.y = projectionLatitude;
		} else {
			switch (mode) {
			case NORTH_POLE:
				y = -y;
				lp.y = Math.acos(sinc);
				break;
			case SOUTH_POLE:
				lp.y = -Math.acos(sinc);
				break;
			case EQUATOR:
				lp.y = y * sinc / rh;
				x *= sinc;
				y = cosc * rh;
				if (Math.abs(lp.y) >= 1.) {
					lp.y = lp.y < 0. ? -MapMath.HALFPI : MapMath.HALFPI;
				} else {
					lp.y = Math.asin(lp.y);
				}
				break;
			case OBLIQUE:
				lp.y = cosc * sinphi0 + y * sinc * cosphi0 / rh;
				y = (cosc - sinphi0 * lp.y) * rh;
				x *= sinc * cosphi0;
				if (Math.abs(lp.y) >= 1.) {
					lp.y = lp.y < 0. ? -MapMath.HALFPI : MapMath.HALFPI;
				} else {
					lp.y = Math.asin(lp.y);
				}
				break;
			}
		}
		lp.x = (y == 0. && (mode == OBLIQUE || mode == EQUATOR)) ? (x == 0.
				? 0. : x < 0. ? -MapMath.HALFPI : MapMath.HALFPI) : Math.atan2(
				x, y);
		return lp;
	}

	@Override
	public boolean hasInverse() {
		return true;
	}

	@Override
	public String toString() {
		return "Orthographic Azimuthal";
	}

}

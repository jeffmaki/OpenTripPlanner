package org.opentripplanner.model;

import com.vividsolutions.jts.geom.Coordinate;

public class Shed {
	public String permit_id;
	
	public Boolean evenSide;
	
	public Double lat;
	
	public Double lon;
	
	public Coordinate getLocationAsCoordinate() {
		return new Coordinate(lon, lat);
	}

	@Override
	public int hashCode() {
		return permit_id.hashCode();
	}
	
	@Override
	public boolean equals(Object arg0) {
		Shed otherShed = (Shed)arg0;
		return permit_id.equals(otherShed.permit_id);
	}

}
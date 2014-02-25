/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.routing.edgetype;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.opentripplanner.gtfs.GtfsLibrary;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Vertex;

import com.vividsolutions.jts.geom.LineString;

/**
 *  Models waiting in a station on a vehicle. 
 */
public class Dwell extends Edge implements DwellEdge {

    /*
     * Models waiting in a station where passengers may remain on the vehicle. This may be useful
     * for its simplicity, but has been largely replaced by PatternDwell
     */


    private static final long serialVersionUID = -7761092317912812048L;

    private StopTime stopTime;

    private AgencyAndId _serviceId;

    private int elapsed;

    public AgencyAndId getServiceId() {
        return _serviceId;
    }

    public Dwell(Vertex startJourney, Vertex endJourney, StopTime stopTime) {
        super(startJourney, endJourney);
        this.stopTime = stopTime;
        this._serviceId = stopTime.getTrip().getServiceId();
        this.elapsed = stopTime.getDepartureTime() - stopTime.getArrivalTime();
    }

    public State traverse(State state0) {
    	StateEditor state1 = state0.edit(this);
        state1.incrementTimeInSeconds(elapsed);
        state1.incrementWeight(elapsed);
        state1.setBackMode(getMode());
        return state1.makeState();
    }

    public String toString() {
        return "Dwell(" + this.stopTime + ")";
    }

    public String getDirection() {
        return stopTime.getTrip().getTripHeadsign();
    }

    @Override
    public Trip getTrip() {
        return stopTime.getTrip();
    }

    public double getDistance() {
        return 0;
    }

    public TraverseMode getMode() {
        return GtfsLibrary.getTraverseMode(stopTime.getTrip().getRoute());
    }

    public String getName() {
        return GtfsLibrary.getRouteName(stopTime.getTrip().getRoute());
    }

    public LineString getGeometry() {
        return null;
    }
}

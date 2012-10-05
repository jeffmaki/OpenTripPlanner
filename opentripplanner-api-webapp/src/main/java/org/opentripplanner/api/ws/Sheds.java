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
package org.opentripplanner.api.ws;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.opentripplanner.routing.edgetype.PlainStreetEdge;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.services.GraphService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.spring.Autowire;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

@Path("/shed") 
@XmlRootElement
@Autowire
public class Sheds {

    @Autowired 
    GraphService _graphService;
	
    @GET
    @Path("/load")
    public String load() throws JSONException, URISyntaxException, ClientProtocolException, IOException {
    	URL url = new URL("http://walk-shed.jeffmaki.com/sheds.php");
    	
    	DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url.toURI());
        HttpResponse response = client.execute(request);
        Reader reader = new InputStreamReader(response.getEntity().getContent());

        Gson gson = new Gson();
        List<Shed> sheds = gson.fromJson(reader, new TypeToken<List<Shed>>(){}.getType());

        for(Shed shed : sheds) {
            add(shed.lat, shed.lon);
        }
        
        return "OK";
    }
    
    @GET
    @Path("/clear")
    public String clear() throws JSONException {
    	for(Vertex v : _graphService.getGraph().getVertices()) {
    		List<Edge> edges = new ArrayList<Edge>();
    		edges.addAll(v.getIncoming());
    		edges.addAll(v.getOutgoing());
    		
    		for(Edge e : edges) {
    			if(e instanceof PlainStreetEdge) {
    				PlainStreetEdge pse = (PlainStreetEdge)e;
    				pse.setHasSidewalkShed(false);
    			}
    		}
    	}
    	
    	return "OK";
    }

    @GET
    @Path("/add")
    public String add(@QueryParam("lat") double lat, @QueryParam("lon") double lon) throws JSONException {
    	GeometryFactory geomFactory = new GeometryFactory();

    	Coordinate targetCoordinate = new Coordinate(lon, lat);
    	Geometry targetGeometry = geomFactory.createPoint(targetCoordinate).buffer(.0002);
    	
    	Integer i = 0;
    	for(Vertex v : _graphService.getGraph().getVertices()) {
    		List<Edge> edges = new ArrayList<Edge>();
    		edges.addAll(v.getIncoming());
    		edges.addAll(v.getOutgoing());
    		
    		for(Edge e : edges) {
    			if(e instanceof PlainStreetEdge) {
    				Coordinate to = e.getToVertex().getCoordinate();
    				Coordinate from = e.getFromVertex().getCoordinate();
    				
    				Coordinate[] coords = { to, from };
    				LineString lineGeometry = geomFactory.createLineString(coords);
    				
    				if(lineGeometry.intersects(targetGeometry)) {
    					PlainStreetEdge pse = (PlainStreetEdge)e;
        				pse.setHasSidewalkShed(true);
    					
        				System.out.println("Set sidewalkshed=true on edge, name=" + e.getName());
        				i++;
    				}
    			}
    		}
    	}
    	
    	return "OK " + i;
    }
        
    private class Shed {
    	public Double lat;

    	public Double lon;
    }
    
}

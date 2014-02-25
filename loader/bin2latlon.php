<?php
	function addr2latlon($number, $street, $boro) {
		$number = urlencode($number);
		$street = urlencode($street);
		
		$url = "http://gis.nyc.gov/doitt/webmap/Geoparser?_v=%2214.1.11-50%22&input=%7B%22addressNumber%22%3A%22" . $number . "%22%2C%22street%22%3A%22" . $street . "%22%2C%22borough%22%3A%22" . strtoupper($boro) . "%22%2C%22declaredClass%22%3A%22ADR%22%2C%22makeCenter%22%3Atrue%2C%22searchType%22%3A%22AddressSearch%22%7D&busy=true";

		echo "Fetching lat/long for standardized address via Geoparser method...";

		$ch = curl_init(); 
		curl_setopt($ch, CURLOPT_URL, $url); 
		curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1');            
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE); 
	    	$body = curl_exec($ch); 
		curl_close($ch); 

		echo "done.\n";

		$new_body = substr($body, 4);
		$response = json_decode($new_body);

		@$x = $response->searches[0]->response->data->items[0]->onStreetX;
		@$y = $response->searches[0]->response->data->items[0]->onStreetY;

		if($x == null || $y == null) {
			echo "Failed to get lat/lon.\n";
			return null;
		}

		$latlong = exec('echo "' . $x . ' ' . $y . '" | cs2cs -f "%.6f" +proj=lcc +lat_1=40.66666666666666 +lat_2=41.03333333333333 +lat_0=40.16666666666666 +lon_0=-74 +x_0=300000 +y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192 +no_defs +to +proj=latlong +datum=NAD83');
		$latlong_parts = split("\n|\r|\t| ", $latlong);

		sleep(2);

		return $latlong_parts;
	}

/*
	function bin2latlon($bin) {
		if(!is_numeric($bin) || strlen($bin) != 7)
			die("Invalid BIN");

		$url = "http://gis.nyc.gov/doitt/webmap/Geoparser?_v=%2214.1.11-50%22&input={%22input%22%3A%22" . $bin . "%22%2C%22declaredClass%22%3A%22SFR%22%2C%22searchType%22%3A%22SingleFieldSearch%22%2C%22makeCenter%22%3Atrue}&busy=true";

		echo "Fetching lat/lon for BIN with Geoparser method...";

		$ch = curl_init(); 
		curl_setopt($ch, CURLOPT_URL, $url); 
		curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1');            
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE); 
	    $body = curl_exec($ch); 
		curl_close($ch); 

		echo "done.\n";

		$new_body = substr($body, 4);
		$response = json_decode($new_body);

		@$x = $response->searches[0]->response->data->items[0]->onStreetX;
		@$y = $response->searches[0]->response->data->items[0]->onStreetY;

		if($x == null || $y == null) {
			echo "Failed to get lat/lon.\n";
			return null;
		}

		$latlong = exec('echo "' . $x . ' ' . $y . '" | cs2cs -f "%.6f" +proj=lcc +lat_1=40.66666666666666 +lat_2=41.03333333333333 +lat_0=40.16666666666666 +lon_0=-74 +x_0=300000 +y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192 +no_defs +to +proj=latlong +datum=NAD83');
		$latlong_parts = split("\n|\r|\t| ", $latlong);

		sleep(2);

		return $latlong_parts;
	}
*/

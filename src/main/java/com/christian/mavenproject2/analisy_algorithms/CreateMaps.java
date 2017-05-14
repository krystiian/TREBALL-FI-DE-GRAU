package com.christian.mavenproject2.analisy_algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateMaps {

	public String heat_map = "<!DOCTYPE html>\n" + 
			"<html>\n" + 
			"  <head>\n" + 
			"    <meta charset=\"utf-8\">\n" + 
			"    <title>Heatmaps</title>\n" + 
			"    <style>\n" + 
			"      html, body {\n" + 
			"        height: 100%;\n" + 
			"        margin: 0;\n" + 
			"        padding: 0;\n" + 
			"      }\n" + 
			"      #map {\n" + 
			"        height: 100%;\n" + 
			"      }\n" + 
			"#floating-panel {\n" + 
			"  position: absolute;\n" + 
			"  top: 10px;\n" + 
			"  left: 25%;\n" + 
			"  z-index: 5;\n" + 
			"  background-color: #fff;\n" + 
			"  padding: 5px;\n" + 
			"  border: 1px solid #999;\n" + 
			"  text-align: center;\n" + 
			"  font-family: 'Roboto','sans-serif';\n" + 
			"  line-height: 30px;\n" + 
			"  padding-left: 10px;\n" + 
			"}\n" + 
			"\n" + 
			"      #floating-panel {\n" + 
			"        background-color: #fff;\n" + 
			"        border: 1px solid #999;\n" + 
			"        left: 25%;\n" + 
			"        padding: 5px;\n" + 
			"        position: absolute;\n" + 
			"        top: 10px;\n" + 
			"        z-index: 5;\n" + 
			"      }\n" + 
			"    </style>\n" + 
			"  </head>\n" + 
			"\n" + 
			"  <body>\n" + 
			"    <div id=\"floating-panel\">\n" + 
			"      <button onclick=\"toggleHeatmap()\">Toggle Heatmap</button>\n" + 
			"      <button onclick=\"changeGradient()\">Change gradient</button>\n" + 
			"      <button onclick=\"changeRadius()\">Change radius</button>\n" + 
			"      <button onclick=\"changeOpacity()\">Change opacity</button>\n" + 
			"    </div>\n" + 
			"    <div id=\"map\"></div>\n" + 
			"    <script>\n" + 
			"\n" + 
			"var map, heatmap;\n" + 
			"\n" + 
			"function initMap() {\n" + 
			"  map = new google.maps.Map(document.getElementById('map'), {\n" + 
			"    zoom: 13,\n" + 
			"    center: {lat: 41.403184, lng: 2.187346},\n" + 
			"    mapTypeId: google.maps.MapTypeId.SATELLITE\n" + 
			"  });\n" + 
			"\n" + 
			"  heatmap = new google.maps.visualization.HeatmapLayer({\n" + 
			"    data: getPoints(),\n" + 
			"    map: map\n" + 
			"  });\n" + 
			"}\n" + 
			"\n" + 
			"function toggleHeatmap() {\n" + 
			"  heatmap.setMap(heatmap.getMap() ? null : map);\n" + 
			"}\n" + 
			"\n" + 
			"function changeGradient() {\n" + 
			"  var gradient = [\n" + 
			"    'rgba(0, 255, 255, 0)',\n" + 
			"    'rgba(0, 255, 255, 1)',\n" + 
			"    'rgba(0, 191, 255, 1)',\n" + 
			"    'rgba(0, 127, 255, 1)',\n" + 
			"    'rgba(0, 63, 255, 1)',\n" + 
			"    'rgba(0, 0, 255, 1)',\n" + 
			"    'rgba(0, 0, 223, 1)',\n" + 
			"    'rgba(0, 0, 191, 1)',\n" + 
			"    'rgba(0, 0, 159, 1)',\n" + 
			"    'rgba(0, 0, 127, 1)',\n" + 
			"    'rgba(63, 0, 91, 1)',\n" + 
			"    'rgba(127, 0, 63, 1)',\n" + 
			"    'rgba(191, 0, 31, 1)',\n" + 
			"    'rgba(255, 0, 0, 1)'\n" + 
			"  ]\n" + 
			"  heatmap.set('gradient', heatmap.get('gradient') ? null : gradient);\n" + 
			"}\n" + 
			"\n" + 
			"function changeRadius() {\n" + 
			"  heatmap.set('radius', heatmap.get('radius') ? null : 70);\n" + 
			"}\n" + 
			"\n" + 
			"function changeOpacity() {\n" + 
			"  heatmap.set('opacity', heatmap.get('opacity') ? null : 0.3);\n" + 
			"}\n" + 
			"\n" + 
			"// Heatmap data: 500 Points\n" + 
			"function getPoints() {\n" + 
			"  return [\n" + 
			"\n" + 
			"	//remember the format:\n" + 
			"	//new google.maps.LatLng(41.403184, -2.187346),\n" + 
			"  _replacehere_\n" + 
			"  ];\n" + 
			"}\n" + 
			"	//replace K-E-Y with your key \n" + 
			"    </script>\n" + 
			"    <script async defer\n" + 
			"        src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyCkA4-LXR_b_PZ_qKf9oNy2Mvs9YyTRpiU&signed_in=true&libraries=visualization&callback=initMap\">\n" + 
			"    </script>\n" + 
			"  </body>\n" + 
			"</html>";
	public String circle_map = "<!DOCTYPE html>\n" + 
			"<html>\n" + 
			"  <head>\n" + 
			"    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">\n" + 
			"    <meta charset=\"utf-8\">\n" + 
			"    <title>Circles</title>\n" + 
			"    <style>\n" + 
			"      html, body {\n" + 
			"        height: 100%;\n" + 
			"        margin: 0;\n" + 
			"        padding: 0;\n" + 
			"      }\n" + 
			"      #map {\n" + 
			"        height: 100%;\n" + 
			"      }\n" + 
			"	div.info {\n" + 
			"    position: fixed;\n" + 
			"    bottom: 0;\n" + 
			"    left: 0;\n" + 
			"\n" + 
			"	opacity: 0.8;\n" + 
			"	background-color: #7cafc0;\n" + 
			"    border: 1px solid #005d6d;\n" + 
			"\n" + 
			"	font-size: 18px;\n" + 
			"	font-family: 'Roboto','sans-serif';\n" + 
			"    }\n" + 
			"    </style>\n" + 
			"  </head>\n" + 
			"  <body>\n" + 
			"<div id=\"map\"></div>\n" + 
			"\n" + 
			"\n" + 
			"<div id=\"info\" class=\"info\">\n" + 
			"Click on the circle to see the information!\n" + 
			"</div>\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"    <script>\n" + 
			"var citymap = {\n" + 
			"  _replacehere_\n" + 
			"};\n" + 
			"\n" + 
			"function showNewRect(url, country, ip, link) {\n" + 
			"	document.getElementById('info').innerHTML = \n" + 
			"							\"<b>DNS:</b> <a href=\\\"link\"+link+\"\\\" target=\\\"_blank\\\">\"+url+\"</a>\"+\n" + 
			"							'<br><b>Country: </b>' + country +\n" + 
			"							'<br><b>IP: </b>' + ip;\n" + 
			"}\n" + 
			"\n" + 
			"function initMap() {\n" + 
			"  var map = new google.maps.Map(document.getElementById('map'), {\n" + 
			"    zoom: 13,\n" + 
			"    center: {lat: 41.403184, lng: 2.187346},\n" + 
			"    mapTypeId: google.maps.MapTypeId.ROADMAP\n" + 
			"  });\n" + 
			"  for (var city in citymap) {\n" + 
			"    var cityCircle = new google.maps.Circle({\n" + 
			"      strokeColor: citymap[city].color,\n" + 
			"      strokeOpacity: 0.8,\n" + 
			"      strokeWeight: 2,\n" + 
			"      fillColor: citymap[city].color,\n" + 
			"      fillOpacity: 0.35,\n" + 
			"      map: map,\n" + 
			"      center: citymap[city].center,\n" + 
			"      radius: 100\n" + 
			"    });\n" + 
			"\n" + 
			"\n" + 
			" 	var createCallback = function(innerCity){\n" + 
			"    	return function() {showNewRect(	citymap[innerCity].URL,\n" + 
			"										citymap[innerCity].Country,\n" + 
			"										citymap[innerCity].IP, \n" + 
			"										citymap[innerCity].Link);};\n" + 
			"	};\n" + 
			"\n" + 
			"    cityCircle.addListener(\"click\", createCallback(city), false);\n" + 
			"\n" + 
			"  }\n" + 
			"}\n" + 
			"\n" + 
			"var createAdd = function () {\n" + 
			"    var counter = 0;\n" + 
			"    return function () {return counter += 1;}\n" + 
			"};\n" + 
			"\n" + 
			"var add = createAdd();\n" + 
			"    </script>\n" + 
			"    <script async defer\n" + 
			"        src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyCkA4-LXR_b_PZ_qKf9oNy2Mvs9YyTRpiU&signed_in=true&callback=initMap\"></script>\n" + 
			"  </body>\n" + 
			"</html>\n" + 
			"";
	
	public void createHeatMap(String s,String fileName){
		String htmlOutput = heat_map.replaceAll("_replacehere_", s);
	   try {
	        File newTextFile = new File(fileName+"_heatMap.html");
	        FileWriter fw = new FileWriter(newTextFile);
	        fw.write(htmlOutput);
	        fw.close();
	
	    } catch (IOException iox) {
	        //do stuff with exception
	        iox.printStackTrace();
	    }
	}
	
	public void createCircleMap(String s,String fileName){
		String htmlOutput = circle_map.replaceAll("_replacehere_", s);
	   try {
	        File newTextFile = new File(fileName+"_circleMap.html");
	        FileWriter fw = new FileWriter(newTextFile);
	        fw.write(htmlOutput);
	        fw.close();
	
	    } catch (IOException iox) {
	        //do stuff with exception
	        iox.printStackTrace();
	    }
	}
	
	public String addHeatMApValue(String lat, String lng){
		return "new google.maps.LatLng(" + lat + ", " + lng + "),";
	}
	
	public String addCircleMApValue(String country, String ip, String lat, String lng, int analizados, String url, String link){
        String s = analizados + ": {center: {lat: " +lat+", lng: " + lng + "},color: '#0052A4',URL:'<a href=\"" + link +"\"" + ">" + url + "</a>',Country:'" + country + "',IP:'"+ ip +"',Link:'"+ link+"'},";
        return s;
	}
	
}



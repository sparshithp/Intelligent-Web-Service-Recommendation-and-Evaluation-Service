package soc.samsung.discovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import soc.samsung.context.UserContext;
import soc.samsung.dto.StreetSegment;
import soc.samsung.po.serviceTrustPO;

public class ServicesData {

	private String url;
	private double duration;
	private StreetSegment segment;
	
	public String getServiceUrl(){
		return url;
	}
	
	public double getDurationForSegment(){
		return duration;
	}
	
	public StreetSegment getStreetSegment(){
		return segment;
	}

	/**
	 * Get any service data
	 * @param serviceTrust
	 * @param segment
	 * @param context
	 */
	public void getServiceData(serviceTrustPO serviceTrust, StreetSegment segment, UserContext context){

		String resJsonString;

		if(serviceTrust == null || segment == null)
			return; 
		else{
			if (serviceTrust.getServiceName().equals("Bing")){
				this.url = "http://dev.virtualearth.net/REST/V1/Routes/Driving?o=json&" +
						"wp.0=" + segment.getPointA().getLatitude() + "," + segment.getPointA().getLongitude() + "&" +
						"wp.1=" + segment.getPointB().getLatitude() + "," + segment.getPointB().getLongitude() + "&" +
						"avoid=minimizeTolls&key=" + context.getBingKey();
				
				resJsonString = getData(this.url);
				parseBingJson(resJsonString);
			}
			
			else if(serviceTrust.getServiceUrl().equals("http://open.mapquestapi.com/directions/v2/route")){
				this.url = "http://open.mapquestapi.com/directions/v2/route?"
						+ "key="+ context.getMapQuestKey() + "&"
						+ "outFormat=json&routeType=fastest&timeType=1&enhancedNarrative=false&shapeFormat=raw&"
						+ "generalize=0&locale=en_US&unit=m&"
						+ "from=" + segment.getPointA().getLatitude() + "," + segment.getPointA().getLongitude() + "&"
						+ "to=" + segment.getPointB().getLatitude() + "," + segment.getPointB().getLongitude() + "&"
						+ "drivingStyle=2&highwayEfficiency=21.0";
				
				//Map Quest API provides an embedded link that can be generated

				resJsonString = getData(url);
				parseMapQuestJson(resJsonString);
			}
			
			else if(serviceTrust.getServiceUrl().equals("http://maps.googleapis.com/maps/api/directions/output")){
				this.url = "https://maps.googleapis.com/maps/api/directions/json?"
						+ "origin=" + segment.getPointA().getLatitude() + "," + segment.getPointA().getLongitude()
						+ "&destination=" + segment.getPointB().getLatitude() + "," + segment.getPointB().getLongitude() + "&"
						+ "key=" + context.getGoogleKey();
				
				resJsonString = getData(this.url);
				parseGoogleJson(resJsonString);
			}

			else
				return;
		}
	}

	/**
	 * HTTP URL call to get Service data
	 */
	private String getData(String url){

		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
						System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			if (responseCode == 404) {
				System.out.println("Received 404");
			}

			BufferedReader in;
			in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(response != null)
			return response.toString();
		else
			return null;
	}
	
	/**
	 * parse google JSON
	 * @param jsonString
	 */
	private void parseGoogleJson(String jsonString){

		JsonElement jelement = new JsonParser().parse(jsonString);
		JsonObject  jobject = jelement.getAsJsonObject();

		//	    Get the first route
		JsonArray routes = jobject.getAsJsonArray("routes");
		if (routes.size() == 0) {
			System.out.println("Google returned nothing");
			duration = 0;
		}
		JsonObject route = routes.get(0).getAsJsonObject();

		//	    Get the main leg of route from start to finish
		JsonArray legs =route.getAsJsonArray("legs");
		JsonObject leg = legs.get(0).getAsJsonObject();

		JsonObject objDuration = leg.getAsJsonObject("duration");
		duration = objDuration.get("value").getAsDouble();

		/* TODO: debug */
		//String durationTxt = objDuration.get("text").toString();
		//System.out.println(duration + " Seconds\n" + durationTxt);

	}

	/**
	 * parse Bing JSon
	 * @param jsonString
	 * @return
	 */
	private void parseBingJson(String jsonString){
		JsonElement jelement = new JsonParser().parse(jsonString);

		JsonObject  jobject = jelement.getAsJsonObject();

		JsonArray resourceSet = jobject.getAsJsonArray("resourceSets");

		JsonArray resources = resourceSet.get(0).getAsJsonObject().getAsJsonArray("resources");

		duration = resources.get(0).getAsJsonObject().get("travelDurationTraffic").getAsDouble();

	}

	/**
	 * parse MapQuest JSON
	 * @param jsonString
	 * @return
	 */
	private void parseMapQuestJson(String jsonString){

		JsonElement jelement = new JsonParser().parse(jsonString);
		duration = jelement.getAsJsonObject().getAsJsonObject("route").get("time").getAsDouble();
	}
}

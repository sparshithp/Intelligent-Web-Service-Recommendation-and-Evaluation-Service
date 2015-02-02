package soc.samsung.context;

public class UserContext {
	private String currLocation;
	private String modeOfTransport;
	private String toLocation;
	
//	This is the google API Key
	private String googleApiKey;
	private String bingApiKey;
	private String mapQuestApiKey;
	
	
	public UserContext(){
		googleApiKey = "AIzaSyDVWOo1J7jswc1NKmrN93beWMpobhlzOEE";
		bingApiKey = "AlLa5UfaAcFnbEAyay9ONEPGObgEUHeAebVptAGB0pWPkIpgvPFeCk8gvoKC8TKn";
		mapQuestApiKey = "Fmjtd%7Cluurn9ubn0%2C2l%3Do5-9wz5lf";
		currLocation = "Mountainview";
		toLocation = "Sanjose"; 
	}
	
	public String getCurrentLocation(){
		return currLocation;
	}
	
	public String getModeofTravel(){
		return modeOfTransport;
	}
	
	public String getToLocation(){
		return toLocation;
	}
	
	public String getGoogleKey(){
		return googleApiKey;
	}
	
	public String getBingKey(){
		return bingApiKey;
	}
	
	public String getMapQuestKey(){
		return mapQuestApiKey;
	}
}

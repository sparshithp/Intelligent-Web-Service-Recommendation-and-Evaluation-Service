package soc.samsung.dto;

import com.google.gson.Gson;

public class Evaluation {
	private String streetName;
    private double startlong;
    private double startlat;
    private double endlong;
    private double endlat;
    private long milliseconds;


    public void setStreetName(String streetName) {
    	this.streetName = streetName;
    }
    
    public String getStreetName() {
    	return streetName;
    }

    public double getStartlong() {
        return startlong;
    }

    public void setStartlong(double startlong) {
        this.startlong = startlong;
    }

    public double getStartlat() {
        return startlat;
    }

    public void setStartlat(double startlat) {
        this.startlat = startlat;
    }

    public double getEndlong() {
        return endlong;
    }

    public void setEndlong(double endlong) {
        this.endlong = endlong;
    }

    public double getEndlat() {
        return endlat;
    }

    public void setEndlat(double endlat) {
        this.endlat = endlat;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}

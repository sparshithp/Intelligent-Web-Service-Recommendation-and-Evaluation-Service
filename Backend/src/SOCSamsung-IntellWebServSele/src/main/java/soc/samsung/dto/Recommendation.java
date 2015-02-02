package soc.samsung.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soc.samsung.po.serviceTrustPO;

import com.google.gson.Gson;

public class Recommendation {
    private String recommendedURI;
    private String serviceName;
    
    public void setRecommendedURI(String uri) {
        this.recommendedURI = uri;
    }

    public void setServiceName(String name) {
        this.serviceName = name;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getRecommendedURI() {
        return recommendedURI;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    
}

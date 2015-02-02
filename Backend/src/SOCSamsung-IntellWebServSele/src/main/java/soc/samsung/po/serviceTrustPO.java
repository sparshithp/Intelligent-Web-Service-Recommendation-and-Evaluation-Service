package soc.samsung.po;

public class serviceTrustPO implements Comparable<serviceTrustPO> {
	private String serviceName;
	private String serviceUrl;
	private double trustValue;
	
	public String getServiceUrl(){
		return serviceUrl;
	}
	
	public String getServiceName(){
		return serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public double getServiceTrustValue(){
		return trustValue;
	}
	
	public void setServiceUrl(String url){
		this.serviceUrl = url;
	}
	
	public void setServiceTrustValue(double value){
		trustValue = value;
	}

	public int compareTo(serviceTrustPO o) {
		// TODO Auto-generated method stub, Descending Order
		if(this.getServiceTrustValue() < o.getServiceTrustValue())
			return 1;
		else if(this.getServiceTrustValue() > o.getServiceTrustValue())
			return -1;
		else
			return 0;
	}
}

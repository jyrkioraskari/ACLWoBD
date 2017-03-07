package fi.aalto.drumbeat.rest;

import javax.xml.bind.annotation.XmlElement;

public class ServiceQuery {
	@XmlElement(name="alt_name")
	public String alt_name;
	@XmlElement(name="requestURL")
	public String  requestURL;
	
	public ServiceQuery() {
		
	}
	
	public String getAlt_name() {
		return alt_name;
	}
	public void setAlt_name(String alt_name) {
		this.alt_name = alt_name;
	}
	public String getRequestURL() {
		return requestURL;
	}
	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}
	
	@Override
	public String toString() {
		return "ServiceQuery [alt_name=" + alt_name + ", requestURL=" + requestURL + "]";
	}
	
	

}

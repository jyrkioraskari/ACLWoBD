package fi.aalto.drumbeat.rest;

import javax.xml.bind.annotation.XmlElement;

public class ServiceResponce {
	@XmlElement(name="roles")
	public String roles="";
	@XmlElement(name="status")
	public String  status="";
	
	public ServiceResponce() {
		
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ServiceResponce [roles=" + roles + ", status=" + status + "]";
	}
	
	
	
	

}

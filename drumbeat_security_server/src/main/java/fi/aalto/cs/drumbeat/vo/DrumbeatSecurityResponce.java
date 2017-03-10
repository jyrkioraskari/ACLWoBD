package fi.aalto.cs.drumbeat.vo;

import javax.xml.bind.annotation.XmlElement;

public class DrumbeatSecurityResponce {
	@XmlElement(name="roles")
	public String roles="";
	@XmlElement(name="status")
	public String  status="";
	
	public DrumbeatSecurityResponce() {
		
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

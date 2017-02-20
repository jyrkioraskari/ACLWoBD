package fi.aalto.drumbeat.webid;

import java.net.URI;

public class WebIDCertificate {

	private final URI webid_uri;
	private final String name;
	private final String public_key;
	
	
	
	public WebIDCertificate(URI webid_uri, String name, String public_key) {
		super();
		this.webid_uri = webid_uri;
		this.name = name;
		this.public_key = public_key;
	}



	public URI getWebid_uri() {
		return webid_uri;
	}

	
	
	public String getName() {
		return name;
	}

	public String getPublic_key() {
		return public_key;
	}
	
	

}

package fi.ni.webid;

import java.net.URI;

public class WebIDCertificate {

	private final URI webid_uri;
	private final String public_key;
	public WebIDCertificate(URI webid_uri, String public_key) {
		super();
		this.webid_uri = webid_uri;
		this.public_key = public_key;
	}
	
	public URI getWebid_uri() {
		return webid_uri;
	}

	public String getPublic_key() {
		return public_key;
	}
	
	

}

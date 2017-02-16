package fi.ni.webid;

public class WebIDProfile {
	
	private final String uri;
	private final String public_key;
	public WebIDProfile(String uri, String public_key) {
		super();
		this.uri = uri;
		this.public_key = public_key;
	}
	public String getUri() {
		return uri;
	}
	public String getPublic_key() {
		return public_key;
	}

	

}

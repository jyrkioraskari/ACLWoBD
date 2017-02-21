package fi.aalto.drumbeat.webid;

public class WebIDProfile {
	
	private final String uri;
	private final String name;
	private final String public_key;
	
	
	
	public WebIDProfile(String uri, String name, String public_key) {
		super();
		this.uri = uri;
		this.name = name;
		this.public_key = public_key;
	}

	public String getUri() {
		return uri;
	}
	
	public String getName() {
		return name;
	}
	public String getPublic_key() {
		return public_key;
	}

	

}

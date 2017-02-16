package fi.ni;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import fi.ni.concepts.Organization;

public class Internet {
	static Map<String, Fetchable> adresses = new HashMap<String, Fetchable>();

	public static Object get(String address) {
		try {
			URI uri = new URI(address);
			System.out.println("HTTP GET host:" + uri.getHost());
			System.out.println("HTTP GET path:" + uri.getPath());

			Fetchable f = adresses.get(uri.getHost());
			/*if (f != null) {
				return f.get(uri.getPath());
			}*/
			return f;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return "not found";
	}

	static {
		Organization c1 = new Organization("http://company1/", "company 1");
		adresses.put(c1.getUri(), c1);
		
		Organization c2 = new Organization("http://company2/", "company 2");
		adresses.put(c2.getUri(), c2);
		
		Organization c3 = new Organization("http://sub1/", "subcontractor 1");
		adresses.put(c3.getUri(), c3);
	}

	public static void main(String[] args) {

	}
}

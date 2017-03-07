package fi.ni.network_integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

public class WebIDConnectionTests {

	@Test
	public void test() {
		System.setProperty("javax.net.ssl.trustStore","c:\\jo\\certs\\keystore.jks");
		URI webid=null;
		URL purl = null;
		try {
			webid = new URI("https://jyrkio2.databox.me/profile/card#me");
			purl = webid.toURL();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		URLConnection conn = null;
		try {
			conn = purl.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (conn instanceof HttpURLConnection) {
			HttpURLConnection hconn = (HttpURLConnection) conn;
			hconn.setInstanceFollowRedirects(true);
		}
		try {
			conn.connect();
		} catch (IOException e) {
			if (e.getMessage().startsWith("sun.security.validator.ValidatorException"))
				System.out.println("A valid root certificate is missing in the Java keystore for URL: "+webid.toString());
			else
				System.out.println(e.getMessage());
			// e.printStackTrace();
		}
	}

}

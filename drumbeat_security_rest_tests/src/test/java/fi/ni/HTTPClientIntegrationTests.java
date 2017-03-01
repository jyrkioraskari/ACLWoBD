package fi.ni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.junit.Test;

//http://java.globinch.com/enterprise-java/security/fix-java-security-certificate-exception-no-matching-localhost-found/
public class HTTPClientIntegrationTests {

	@Test
	public void test() {
		try {
			String httpsURL = "https://architect.local.org:8443/security/rest/organization/hello";
			URL myurl = new URL(httpsURL);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			InputStream ins = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader in = new BufferedReader(isr);

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}

			in.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}

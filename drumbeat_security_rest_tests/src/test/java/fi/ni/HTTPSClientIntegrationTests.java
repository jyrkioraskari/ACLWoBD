package fi.ni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Test;

//http://java.globinch.com/enterprise-java/security/fix-java-security-certificate-exception-no-matching-localhost-found/
public class HTTPSClientIntegrationTests {

	
	//http://stackoverflow.com/questions/1666052/java-https-client-certificate-authentication
	@Test
	public void test1() {
		System.setProperty("javax.net.ssl.trustStore", "c:\\jo\\keystore.jks");
		try {
			String httpsURL = "https://architect.local.org:8443/protected/data/hello";
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
			System.out.println("passwed");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/*@Test
	public void test2() {
		
		try {
		    org.apache.http.ssl.SSLContextBuilder context_b = SSLContextBuilder.create();
			context_b.loadTrustMaterial(new org.apache.http.conn.ssl.TrustSelfSignedStrategy());
			SSLContext ssl_context = context_b.build();
			org.apache.http.conn.ssl.SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
					ssl_context, new org.apache.http.conn.ssl.DefaultHostnameVerifier());

			HttpClientBuilder builder = HttpClients.custom().setSSLSocketFactory(sslSocketFactory);
			CloseableHttpClient httpclient = builder.build();

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet("https://architect.local.org:8443/security/rest/organization/hello");

			CloseableHttpResponse response = httpClient.execute(httpGet);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}*/

}

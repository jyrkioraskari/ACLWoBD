package fi.ni;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.junit.Test;

//http://java.globinch.com/enterprise-java/security/fix-java-security-certificate-exception-no-matching-localhost-found/
public class HTTPSClientIntegrationTests {

	
	//http://stackoverflow.com/questions/1666052/java-https-client-certificate-authentication
	/*@Test
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
			System.out.println("passed");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}*/
	
	@Test
	public void testconnection() {
		try {

			JSONObject obj = new JSONObject();
	        obj.put("alt_name", "alt");
	        obj.put("requestURL", "URL");
	        
			String httpsURL = "http://localhost:8080/security/organization/hello";
			URL myurl = new URL(httpsURL);
			HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
			conn.setDoOutput( true );
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", MediaType.APPLICATION_JSON); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( obj.toJSONString().length() ));
			conn.setUseCaches( false );
			try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
			   wr.write( obj.toJSONString().getBytes() );
			}
			InputStream ins = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader in = new BufferedReader(isr);

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}

			in.close();
			System.out.println("passed");
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

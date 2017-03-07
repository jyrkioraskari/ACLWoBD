package fi.aalto.cs.drumbeat.tests.network_integration;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class AuthBackEndIntegration {

	@Test
	public void testconnection() {
		try {

			JSONObject obj = new JSONObject();
			obj.put("alt_name", "alt");
			obj.put("requestURL", "URL");

			String httpsURL = "http://localhost/security/";
			URL myurl = new URL(httpsURL);
			HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(obj.toJSONString().length()));
			conn.setUseCaches(false);
			try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
				wr.write(obj.toJSONString().getBytes());
			}
			InputStream ins = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader in = new BufferedReader(isr);

			String inputLine;

			String response="";
			while ((inputLine = in.readLine()) != null) {
				response+=inputLine;
			}
			JSONParser parser = new JSONParser();
			try {
				JSONObject response_obj = (JSONObject)parser.parse(response);
				String status=(String) response_obj.get("status");
				String roles=(String) response_obj.get("roles");
				System.out.println(roles);
			} catch (ParseException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}

			in.close();
			System.out.println("passed");
		} catch (IOException e) {

			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}

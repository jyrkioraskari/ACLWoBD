package fi.ni;

import static org.junit.Assert.*;

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

public class BackEndIntegrationTests {

	@Test
	public void testconnection() {
		try {

			JSONObject obj = new JSONObject();
			obj.put("alt_name", "alt");
			obj.put("requestURL", "URL");

			String httpsURL = "http://localhost:8080/security/server/hello";
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

			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}

			in.close();
			System.out.println("passed");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}

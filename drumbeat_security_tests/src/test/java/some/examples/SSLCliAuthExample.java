
package some.examples;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.BasicConfigurator;

public class SSLCliAuthExample {

	private static final Logger LOG = Logger.getLogger(SSLCliAuthExample.class.getName());

	private static final String CA_KEYSTORE_TYPE = KeyStore.getDefaultType(); // "JKS";
	private static final String CA_KEYSTORE_PATH = "c:\\jo\\keystore.jks";
	private static final String CA_KEYSTORE_PASS = "ahgahgahga";

	private static final String CLIENT_KEYSTORE_TYPE = "PKCS12";
	private static final String CLIENT_KEYSTORE_PATH = "c:\\jo\\certs\\Jyrki_Oraskari.p12";
	private static final String CLIENT_KEYSTORE_PASS = "J^@i9hUu!Wdlkcd5Vjsh2Ew";

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		Logger l = Logger.getLogger("org.apache");
		l.setLevel(Level.INFO);

		requestTimestamp();
	}

	public final static void requestTimestamp() throws Exception {
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(createSslCustomContext(),
				new String[] { "TLSv1" }, // Allow TLSv1 protocol only
				null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		try (CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(csf).build()) {
			System.out.println("1");
			HttpGet req = new HttpGet("https://architect.local.org:8443/protected/data/hello");
			System.out.println("2");

			CloseableHttpResponse response1 = httpclient.execute(req);
			System.out.println("3");
			int code1 = response1.getStatusLine().getStatusCode();
			System.out.println("4:" + code1);
			EntityUtils.consume(response1.getEntity());

		}
	}

	public static SSLContext createSslCustomContext() throws KeyStoreException, IOException, NoSuchAlgorithmException,
			CertificateException, KeyManagementException, UnrecoverableKeyException {
		// Trusted CA keystore
		KeyStore tks = KeyStore.getInstance(CA_KEYSTORE_TYPE);
		tks.load(new FileInputStream(CA_KEYSTORE_PATH), CA_KEYSTORE_PASS.toCharArray());

		// Client keystore
		KeyStore cks = KeyStore.getInstance(CLIENT_KEYSTORE_TYPE);
		cks.load(new FileInputStream(CLIENT_KEYSTORE_PATH), CLIENT_KEYSTORE_PASS.toCharArray());

		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(tks, new TrustSelfSignedStrategy()) // use
																											// it
																											// to
																											// customize
																											// //TODO
																											// check
																											// if
																											// needed
				.loadKeyMaterial(cks, CLIENT_KEYSTORE_PASS.toCharArray()) // load
																			// client
																			// certificate
				.build();
		return sslcontext;
	}

}
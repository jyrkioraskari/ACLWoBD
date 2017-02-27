package fi.aalto.cs.drumbeat;

// Code based on http://www.programcreek.com/java-api-examples/index.php?source_dir=mockserver-master/mockserver-core/src/main/java/org/mockserver/socket/KeyStoreFactory.java
import static fi.aalto.cs.drumbeat.CertificateCommons.createSubjectKeyIdentifier;
import static fi.aalto.cs.drumbeat.CertificateCommons.generateKeyPair;
import static fi.aalto.cs.drumbeat.CertificateCommons.signCertificate;
import static fi.aalto.cs.drumbeat.CertificateFileUtils.loadCertificateFromKeyStore;
import static fi.aalto.cs.drumbeat.CertificateFileUtils.loadPrivateKeyFromPEMFile;
import static fi.aalto.cs.drumbeat.CertificateFileUtils.saveCertificateAsKeyStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;

import fi.aalto.cs.drumbeat.vo.Data;

/*
* 
Jyrki Oraskari, Aalto University, 2016 

This research has partly been carried out at Aalto University in DRUMBEAT 
“Web-Enabled Construction Lifecycle” (2014-2017) —funded by Tekes, 
Aalto University, and the participating companies.

The MIT License (MIT)
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

public class ClientCertificateCreator {
	private static final int FAKE_KEYSIZE = 1024;
	Data data_store = Data.Singleton.INSTANCE.getSingleton();

	public ClientCertificateCreator() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		try {
			KeyStore clientKeyStore = KeyStore.getInstance("pkcs12");
			clientKeyStore.load(null, data_store.getCERT_AUTH_KEYSTORE_PASSWORD().toCharArray());
			generateCertificate(clientKeyStore,data_store.getCLIENT_ALIAS(),
					data_store.getCERT_AUTH_ALIAS(), data_store.getCERT_AUTH_KEYSTORE_PASSWORD().toCharArray());
			//saveAsCrt(clientKeyStore, "certificationAlias", "certfile.crt");   
			} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public KeyStore generateCertificate(KeyStore keyStore, String certificationAlias, String certificateAuthorityAlias,
			char[] keyStorePassword) throws Exception {

		KeyPair keyPair = generateKeyPair(FAKE_KEYSIZE);
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		RSAPublicKey certRsakey = (RSAPublicKey) publicKey;
		data_store.getClient_certificate().setModulus(certRsakey.getModulus().toString(16));		
		data_store.getClient_certificate().setExponent(certRsakey.getPublicExponent().toString());

		PrivateKey caPrivateKey = loadPrivateKeyFromPEMFile(data_store.getCERT_AUTH_PRIVATE_KEY_FILE());
		X509Certificate caCert = (X509Certificate) loadCertificateFromKeyStore(data_store.getCERT_AUTH_KEYSTORE_FILE(),
				certificateAuthorityAlias, keyStorePassword);

		X509Certificate clientCert = createClientCert(publicKey, caCert, caPrivateKey, caCert.getPublicKey());
		
		

		return saveCertificateAsKeyStore(keyStore, false, data_store.getCLIENT_KEYSTORE_FILE(), certificationAlias, privateKey,
				keyStorePassword, new X509Certificate[] { clientCert, caCert }, caCert);
	}

	public X509Certificate createClientCert(PublicKey publicKey, X509Certificate certificateAuthorityCert,
			PrivateKey certificateAuthorityPrivateKey, PublicKey certificateAuthorityPublicKey)
			throws Exception {
		X500Name issuer = new X509CertificateHolder(certificateAuthorityCert.getEncoded()).getSubject();
		X500Name subject = new X500Name("CN="+data_store.getClient_certificate().getCommon_name()+", O="+data_store.getClient_certificate().getOrganization()+", L="+data_store.getClient_certificate().getCity()+", ST="+data_store.getClient_certificate().getCountry().getCountry_Name()+", C="+data_store.getClient_certificate().getCountry().getCountry_Code());

		BigInteger serial = BigInteger.valueOf(new Random().nextInt());
		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer, serial, CertificateCommons.NOT_BEFORE, CertificateCommons.NOT_AFTER,
				subject, publicKey);

		addURI(data_store.getCLIENT_SUBJECT_ALT_NAME_URI());
		fillInto(builder);
		builder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(publicKey));
		builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
		X509Certificate cert = signCertificate(builder, certificateAuthorityPrivateKey);

		cert.checkValidity(new Date());
		cert.verify(certificateAuthorityPublicKey);

		return cert;
	}

	// http://www.programcreek.com/java-api-examples/index.php?source_dir=LittleProxy-mitm-master/src/main/java/org/littleshoot/proxy/mitm/SubjectAlternativeNameHolder.java
	private final List<ASN1Encodable> sans = new ArrayList<ASN1Encodable>();

	public void addURI(String subjectAlternativeName) {
		sans.add(new GeneralName(GeneralName.uniformResourceIdentifier, subjectAlternativeName));
	}

	public void fillInto(X509v3CertificateBuilder certGen) throws CertIOException {
		if (!sans.isEmpty()) {
			ASN1Encodable[] encodables = sans.toArray(new ASN1Encodable[sans.size()]);
			certGen.addExtension(Extension.subjectAlternativeName, false, new DERSequence(encodables));
		}
	}

	public static void saveAsCrt(KeyStore keystore, String alias, String certFileName) {
		Certificate cert;
		try {
			cert = keystore.getCertificate(alias);
			File file = new File(certFileName);
			byte[] buf = cert.getEncoded();

			FileOutputStream os = new FileOutputStream(file);
			os.write(buf);
			os.close();

			// Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"));
			// wr.write(new sun.misc.BASE64Encoder().encode(buf));
			// wr.flush();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	


}

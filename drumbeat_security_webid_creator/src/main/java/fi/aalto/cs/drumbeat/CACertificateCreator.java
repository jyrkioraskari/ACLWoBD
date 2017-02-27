package fi.aalto.cs.drumbeat;

// Code based on http://www.programcreek.com/java-api-examples/index.php?source_dir=mockserver-master/mockserver-core/src/main/java/org/mockserver/socket/KeyStoreFactory.java
import static fi.aalto.cs.drumbeat.CertificateCommons.createSubjectKeyIdentifier;
import static fi.aalto.cs.drumbeat.CertificateCommons.generateKeyPair;
import static fi.aalto.cs.drumbeat.CertificateCommons.signCertificate;
import static fi.aalto.cs.drumbeat.CertificateFileUtils.existsFileFromClassPathOrPath;
import static fi.aalto.cs.drumbeat.CertificateFileUtils.readFileFromClassPathOrPath;
import static fi.aalto.cs.drumbeat.CertificateFileUtils.saveCertificateAsKeyStore;
import static fi.aalto.cs.drumbeat.CertificateFileUtils.saveCertificateAsPEMFile;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
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

public class CACertificateCreator {
	private static final int ROOT_KEYSIZE = 2048;
	
	Data data_store = Data.Singleton.INSTANCE.getSingleton();
	
	public CACertificateCreator() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		 generate_CA_certificate();
	}
	
	
	public void generate_CA_certificate(){
		try
		{
		KeyPair caKeyPair = generateKeyPair(ROOT_KEYSIZE);
		PublicKey caPublicKey = caKeyPair.getPublic();
		PrivateKey caPrivateKey = caKeyPair.getPrivate();
		X509Certificate caCert = createCACert(caPublicKey, caPrivateKey);

		KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		if(existsFileFromClassPathOrPath(data_store.getCERT_AUTH_KEYSTORE_FILE()) )
		 caKeyStore.load(readFileFromClassPathOrPath(data_store.getCERT_AUTH_KEYSTORE_FILE()),
				 data_store.getCERT_AUTH_KEYSTORE_PASSWORD().toCharArray());
		else
		{
			caKeyStore.load(null, data_store.getCERT_AUTH_KEYSTORE_PASSWORD().toCharArray());
		}
		
		saveCertificateAsKeyStore(caKeyStore, false, data_store.getCERT_AUTH_KEYSTORE_FILE(),
				data_store.getCERT_AUTH_ALIAS(), caPrivateKey, data_store.getCLIENT_KEYSTORE_PASSWORD().toCharArray(), new X509Certificate[] { caCert },
				caCert);
		//saveCertificateAsPEMFile(caCert, data_store.getCERT_AUTH_CERTIFICATE_FILE());
		//saveCertificateAsPEMFile(caPublicKey, data_store.getCERT_AUTH_PUBLIC_KEY_FILE());
		saveCertificateAsPEMFile(caPrivateKey, data_store.getCERT_AUTH_PRIVATE_KEY_FILE());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public X509Certificate createCACert(PublicKey publicKey, PrivateKey privateKey)  {

		X509Certificate ca_cert=null;
		try
		{
		X500Name issuerName = new X500Name("CN="+data_store.getCa_certificate().getCommon_name()+", O="+data_store.getCa_certificate().getOrganization()+", L="+data_store.getCa_certificate().getCity()+", ST="+data_store.getCa_certificate().getCountry().getCountry_Name()+", C="+data_store.getCa_certificate().getCountry().getCountry_Code());
		X500Name subjectName = issuerName;
		BigInteger serial = BigInteger.valueOf(new Random().nextInt());
		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuerName, serial, CertificateCommons.NOT_BEFORE, CertificateCommons.NOT_AFTER,
				subjectName, publicKey);
		builder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(publicKey));
		builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

		KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.digitalSignature | KeyUsage.keyEncipherment
				| KeyUsage.dataEncipherment | KeyUsage.cRLSign);
		builder.addExtension(Extension.keyUsage, false, usage);

		ASN1EncodableVector purposes = new ASN1EncodableVector();
		purposes.add(KeyPurposeId.id_kp_serverAuth);
		purposes.add(KeyPurposeId.id_kp_clientAuth);
		purposes.add(KeyPurposeId.anyExtendedKeyUsage);
		builder.addExtension(Extension.extendedKeyUsage, false, new DERSequence(purposes));

		ca_cert = signCertificate(builder, privateKey);
		ca_cert.checkValidity(new Date());
		ca_cert.verify(publicKey);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return ca_cert;
	}


}

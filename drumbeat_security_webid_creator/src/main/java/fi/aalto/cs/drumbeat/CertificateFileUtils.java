package fi.aalto.cs.drumbeat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

//Code based on http://www.programcreek.com/java-api-examples/index.php?source_dir=mockserver-master/mockserver-core/src/main/java/org/mockserver/socket/KeyStoreFactory.java

public class CertificateFileUtils {
	
	public static KeyStore saveCertificateAsKeyStore(KeyStore existingKeyStore, boolean deleteOnExit, String keyStoreFileName,
			String certificationAlias, Key privateKey, char[] keyStorePassword, Certificate[] chain,
			X509Certificate caCert) {
		try {
			KeyStore keyStore = existingKeyStore;
			if (keyStore == null) {
				keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				keyStore.load(null, keyStorePassword);
			}

			try {
				keyStore.deleteEntry(certificationAlias);
			} catch (KeyStoreException kse) {
				// ignore as may not exist in keystore yet
			}
			keyStore.setKeyEntry(certificationAlias, privateKey, keyStorePassword, chain);
			File keyStoreFile = new File(keyStoreFileName);
			FileOutputStream fileOutputStream = null;
			try {
				System.out.println("writing keystore file: "+keyStoreFile.getAbsolutePath());
				fileOutputStream = new FileOutputStream(keyStoreFile);
				keyStore.store(fileOutputStream, keyStorePassword);
			} finally {
				fileOutputStream.close();
			}
			if (deleteOnExit) {
				keyStoreFile.deleteOnExit();
			}
			return keyStore;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while saving KeyStore", e);
		}
	}

    public static void saveCertificateAsPEMFile(Object x509Certificate, String filename) throws IOException { 
        FileWriter fileWriter = new FileWriter(filename); 
        JcaPEMWriter jcaPEMWriter = null; 
        try { 
            jcaPEMWriter = new JcaPEMWriter(fileWriter); 
            jcaPEMWriter.writeObject(x509Certificate); 
        } finally { 
            jcaPEMWriter.close(); 
            fileWriter.close(); 
        } 
    } 
	
    public static InputStream readFileFromClassPathOrPath(String keyStoreFileName) throws FileNotFoundException { 
        InputStream inputStream = CertificateFileUtils.class.getClassLoader().getResourceAsStream(keyStoreFileName); 
        if (inputStream == null) { 
            // load from path if not found in classpath 
            inputStream = new FileInputStream(keyStoreFileName); 
        } 
        return inputStream; 
    } 
  
    public static boolean existsFileFromClassPathOrPath(String keyStoreFileName)  {
    	try {
            InputStream inputStream = CertificateFileUtils.class.getClassLoader().getResourceAsStream(keyStoreFileName); 
            if (inputStream == null) { 
                inputStream = new FileInputStream(keyStoreFileName); 
            } 
            inputStream.close();
            
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {			
			e.printStackTrace();
			return false;
		}
        return true; 
    } 
    
    
    
    public static RSAPrivateKey loadPrivateKeyFromPEMFile(String privateKeyFileName) { 
    	System.out.println("reading private key file: "+privateKeyFileName);
        try { 
        	InputStream inputStream=CertificateFileUtils.class.getClassLoader().getResourceAsStream(privateKeyFileName);
        	 if (inputStream == null) { 
                 inputStream = new FileInputStream(privateKeyFileName); 
             } 
        	InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            String publicKeyFile = IOUtils.toString(inputStreamReader); 
            byte[] publicKeyBytes = DatatypeConverter.parseBase64Binary(publicKeyFile.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "")); 
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(publicKeyBytes)); 
        } catch (Exception e) { 
            throw new RuntimeException("Exception reading private key from PEM file", e); 
        } 
    } 
 
    
    public static Certificate loadCertificateFromKeyStore(String keyStoreFileName, String certificationAlias, char[] keyStorePassword) { 
        try { 
            InputStream inputStream = readFileFromClassPathOrPath(keyStoreFileName); 
            try {                  
                KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType()); 
                keystore.load(inputStream, keyStorePassword); 
                return keystore.getCertificate(certificationAlias); 
            } finally { 
                inputStream.close(); 
            } 
        } catch (Exception e) { 
            throw new RuntimeException("Exception while loading KeyStore from " + keyStoreFileName, e); 
        } 
    } 
 

}

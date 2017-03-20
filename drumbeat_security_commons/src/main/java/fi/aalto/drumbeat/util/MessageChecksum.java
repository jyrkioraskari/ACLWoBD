package fi.aalto.drumbeat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/*
 * Adapted from: http://www.asjava.com/core-java/java-md5-example/
 */

public class MessageChecksum {

	char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	MessageDigest md;

	public MessageChecksum() {
		try {
			md = MessageDigest.getInstance("MD5"); // SHA-512 MD5
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
	}

	public String getChecksumValue(String txt) {
		String result=null;
		try {
			md.reset();
			md.update(txt.getBytes());
			byte temp[] = md.digest();
			char str[] = new char[temp.length * 2];
			int k = 0;
			for (int i = 0; i < temp.length; i++) {
				byte byte0 = temp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			result = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		MessageChecksum md5 = new MessageChecksum();
		for (int n = 0; n < 10; n++) {
			System.out.println(md5.getChecksumValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"));
		}

	}

}

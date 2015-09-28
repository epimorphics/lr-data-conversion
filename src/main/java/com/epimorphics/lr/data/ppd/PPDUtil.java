package com.epimorphics.lr.data.ppd;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PPDUtil {	

	private static final String HASH_ALGORITHM = "SHA";
	
	public static String getAddressHash(PPDCSVLine line) {
		return generateHash(serializeAddress(line));
	}
	
	private static String serializeAddress(PPDCSVLine line) {
	      StringBuffer hashBuffer = new StringBuffer();
	      hashBuffer.append(substituteDefault(line.getPaon(),      "noPaon"));
	      hashBuffer.append(substituteDefault(line.getSaon(),      "noSaon"));
	      hashBuffer.append(substituteDefault(line.getStreet(),    "A"));
	      hashBuffer.append(substituteDefault(line.getLocality(),  "B"));
	      hashBuffer.append(substituteDefault(line.getTown(),      "C"));
	      hashBuffer.append(substituteDefault(line.getDistrict(),  "D"));
	      hashBuffer.append(substituteDefault(line.getCounty(),    "E"));
	      hashBuffer.append(substituteDefault(line.getPostCode(),  "F"));
	      return hashBuffer.toString();
	}
	
	private static String generateHash(String plaintext) {		
	    MessageDigest m;
	    try {
	       m = MessageDigest.getInstance(HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new Error("unknown hash algorthm: " + HASH_ALGORITHM, e);
		}
		m.reset();
		m.update(plaintext.getBytes());
		byte[] digest = m.digest();
		return bytesToHex(digest);
    }
	
	final private static char[] hexArray = "0123456789abcdef".toCharArray();
	private static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String substituteDefault(String value, String def) {
		String result = value;
		if (result == null || result.trim().length() == 0) {
			result = def;
		}
		return result;
	}
}

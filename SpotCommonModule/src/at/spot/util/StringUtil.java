package at.spot.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import at.spot.log.Log;

public class StringUtil {
	public static boolean check(String string) {
		return string != null && !string.equals("");
	}
	
	public static String toString(InputStream is) {
		String ret = null;
		
		try {
			ret = IOUtils.toString(is);
		} catch (IOException e) {
			Log.error("StringUtil.check", e, true);
		}
		
		return ret;
	}
}

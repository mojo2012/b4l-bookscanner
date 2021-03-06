package at.spot.b4lbookscanner.googlebooks.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.spot.b4lbookscanner.googlebooks.VolumeList;
import at.spot.util.StringUtil;

import com.google.gson.Gson;

public class Util {
	private final static String	TAG					= "Util";

	private final static String	API_KEY				= "AIzaSyADMb1ZO28YsK3J1bVwiKJaxD5FUexEunk";

	static final String			URL_ISBN_10			= "https://www.googleapis.com/books/v1/volumes?q=isbn:%s&key=" + API_KEY;
	// static final String URL_ISBN_13 =
	// "https://www.googleapis.com/books/v1/volumes?q=isbn-13:%s";
	static final String			URL_GENERAL_SEARCH	= "https://www.googleapis.com/books/v1/volumes?q=%s&key=" + API_KEY;
	static final String 		IN_AUTHOR			= "inauthor:%s";

	public static VolumeList getBookByISBN(String isbn) throws Exception {
		return getBookByISBN10(isbn);
	}

	public static VolumeList getBookByISBN10(String isbn) throws Exception {
		Gson gson = new Gson();
		VolumeList b = null;

		try {
			String json = getStringContent(String.format(URL_ISBN_10, isbn));

			b = gson.fromJson(json, VolumeList.class);
		} catch (Exception e) {
			throw e;
		}

		return b;
	}

	public static VolumeList getBookByTitle(String title) throws Exception {
		return getBookByTitle(title, null);
	}
	
	public static VolumeList getBookByTitle(String title, String authors) throws Exception {
		Gson gson = new Gson();
		VolumeList b = null;

		String encodedString = title;
		String endcodedAuthrors = (StringUtil.check(authors) ? String.format(IN_AUTHOR, authors) : "");

		try {
			encodedString = URLEncoder.encode(title, "utf-8");
			endcodedAuthrors = URLEncoder.encode(endcodedAuthrors, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			String json = getStringContent(String.format(URL_GENERAL_SEARCH + "&" + endcodedAuthrors, encodedString));

			b = gson.fromJson(json, VolumeList.class);
		} catch (Exception e) {
			throw e;
		}

		return b;
	}

	// public static VolumeList getBookByISBN13(String isbn) {
	// Gson gson = new Gson();
	// VolumeList b = null;
	//
	// try {
	// String json = getStringContent(String.format(URL_ISBN_13, isbn));
	//
	// b = gson.fromJson(json, VolumeList.class);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return b;
	// }

	public static String getStringContent(String url) throws Exception {
		BufferedReader buf = null;
		InputStream in = null;

		String ret = null;

		try {
			URL u = new URL(url);
			in = u.openStream();

			buf = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String s;
			while (true) {
				s = buf.readLine();
				if (s == null || s.length() == 0)
					break;
				sb.append(s);
			}

			ret = sb.toString();

		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				buf.close();
				in.close();
			} catch (Exception e) {
			}
		}

		return ret;
	}

	public static boolean isValidISBN(String isbn) {
		String regex = "(?=.{17}$)97(?:8|9)([ -])\\d{1,5}\\1\\d{1,7}\\1\\d{1,6}\\1\\d$";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(isbn);

		boolean matches = m.matches();

		return matches;
	}

	public static String correctISBN(String isbn) {
		String ret = "";

		switch (isbn.length()) {
			case 10:
				ret = isbn.substring(0, 1) + "-" + isbn.substring(1, 4) + "-" + isbn.substring(4, 9) + "-"
						+ isbn.substring(9, 10);
				break;
			case 13:
				ret = isbn.substring(0, 3) + "-" + isbn.substring(3, 4) + "-" + isbn.substring(4, 7) + "-"
						+ isbn.substring(7, 12) + "-" + isbn.substring(12, 13);
				break;
			default:
				ret = isbn;
		}

		return ret;
	}
}

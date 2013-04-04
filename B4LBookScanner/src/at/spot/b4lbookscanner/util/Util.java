package at.spot.b4lbookscanner.util;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import at.spot.b4lbookscanner.googlebooks.VolumeList;

public class Util extends at.spot.b4lbookscanner.googlebooks.Util {
	private final static String	TAG					= "Util";

	static final String			URL_ISBN_10			= "https://www.googleapis.com/books/v1/volumes?q=isbn:%s";
	// static final String URL_ISBN_13 =
	// "https://www.googleapis.com/books/v1/volumes?q=isbn-13:%s";
	static final String			URL_GENERAL_SEARCH	= "https://www.googleapis.com/books/v1/volumes?q=%s";

	public static VolumeList getBookByISBN(String isbn) {
		return getBookByISBN10(isbn);
	}

	public static Bitmap getImageContent(String url) {
		Bitmap image = null;

		try {
			InputStream in = new java.net.URL(url).openStream();
			image = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		return image;
	}
}

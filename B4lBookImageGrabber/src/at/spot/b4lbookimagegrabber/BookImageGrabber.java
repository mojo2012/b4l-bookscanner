package at.spot.b4lbookimagegrabber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import at.spot.b4lbookscanner.googlebooks.Book;
import at.spot.b4lbookscanner.googlebooks.Util;
import at.spot.b4lbookscanner.googlebooks.VolumeInfo;
import at.spot.b4lbookscanner.googlebooks.VolumeList;

public class BookImageGrabber {

	public static void main(String[] args) {
		try {
			List<Book> books = readBookListCSV("/Users/matthias/Desktop/temp/scanned_books.csv");
			List<Book> updatedBooks = new ArrayList<>();

			VolumeList googleBook = null;

			int count = 1;

			for (Book e : books) {
				System.out.println("Updating " + (count++) + "/" + books.size());

				if (e.getImageUrl() == null || e.getImageUrl().equals("")) {
					if (e.getIsbnType() != null && e.getIsbnType().equals("ISBN_10")) {
						googleBook = Util.getBookByISBN10(e.getIsbn());
					} else if (e.getIsbnType() != null && e.getIsbnType().equals("ISBN_13")) {
						googleBook = Util.getBookByISBN(e.getIsbn());
					} else {
						googleBook = Util.getBookByTitle(e.getTitle());
					}

					if (googleBook != null && googleBook.getItems().size() >= 1) {

						VolumeInfo info = googleBook.getItems().get(0).getVolumeInfo();

						if (info != null && info.getImageLinks() != null) {
							e.setImageUrl(info.getImageLinks().getThumbnail());
						} else {
							e.setImageUrl("http://shop.b4l-wien.at/images/nopic.png");
						}
					}
				}

				downloadImageToFolder(e.getImageUrl(), "/Users/matthias/Desktop/temp/images/" + e.getIsbn() + ".jpg");
				e.setImageUrl("http://shop.b4l-wien.at/images/" + e.getIsbn());
				updatedBooks.add(e);

				if (count % 5 == 0) {
					writeBookListCSV(updatedBooks, "/Users/matthias/Desktop/temp/updated_books.csv",
							"/Users/matthias/Desktop/temp/images/");
					updatedBooks.clear();
					Thread.sleep(2000);
				}

				Thread.sleep(1000);
			}

			System.out.println("Finished");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void writeBookListCSV(List<Book> books, String csvFile, String imagePath) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(csvFile, true));

			String csvLine = "";

			for (Book e : books) {
				csvLine = e.getId()
						+ ";"
						+ e.getTitle()
						+ ";"
						+ ""
						+ ";"
						+ e.getPrice()
						+ ";"
						+ 1
						+ ";"
						+ "Code: "
						+ e.getIsbn()
						+
						(e.getPublisher() != null && !e.getPublisher().equals("") ? ", Publisher: "
								+ e.getPublisher() : "")
						+
						(e.getReleaseDate() != null && !e.getReleaseDate().equals("") ? ", Veröffentlichung: "
								+ e.getReleaseDate() : "") +
						";" + e.getSummary() + ";" + e.getImageUrl();

				writer.append(csvLine);
				writer.newLine();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
	}

	public static void downloadImageToFolder(String imageUrl, String filePath) throws IOException {
		File outputFile = new File(filePath);

		if (!outputFile.exists()) {
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.addRequestProperty(
						"Accept",
						"image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/x-shockwave-flash, */*");
				connection.addRequestProperty("Accept-Language", "en-us,zh-cn;q=0.5");
				connection.addRequestProperty("Accept-Encoding", "gzip, deflate");
				connection.addRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 2.0.50727; MS-RTC LM 8)");
				connection.connect();

				InputStream is = connection.getInputStream();
				OutputStream os = new FileOutputStream(filePath);

				byte[] buffer = new byte[1024];
				int byteReaded = is.read(buffer);
				while (byteReaded != -1) {
					os.write(buffer, 0, byteReaded);
					byteReaded = is.read(buffer);
				}

				os.close();
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public static List<Book> readBookListCSV(String csvFile) throws Exception {
		List<Book> books = new ArrayList<>();

		String line;
		boolean firstLine = true;
		Book b = null;

		BufferedReader stream = null;

		try {
			stream = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)));

			while ((line = stream.readLine()) != null) {

				if (firstLine) {
					firstLine = false;
					continue;
				}

				String[] columns = line.split(";");

				b = new Book();

				b.setId(Integer.parseInt(columns[0]));
				b.setIsbn(columns[1].replace("/", "_"));
				b.setIsbnType(columns[2]);
				b.setAuthors(columns[3]);
				b.setTitle(columns[4]);
				b.setSummary(columns[5]);
				b.setPublisher(columns[6]);
				b.setReleaseDate(columns[7]);
				b.setPrice(Float.parseFloat(columns[8]));
				b.setNumber(columns[9]);

				if (columns.length >= 11)
					b.setImageUrl(columns[10]);

				books.add(b);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stream.close();
			} catch (Exception e) {
			}
		}

		return books;
	}
}

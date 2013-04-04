package at.spot.b4lbookimagegrabber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
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
							e.setImageUrl("http://www.ditech.at/image_bw?type=webroot&name=img/nopic.png&width=220&height=220");
						}
					}
				}

				Thread.sleep(3000);
			}

			writeBookListCSV(books, "/Users/matthias/Desktop/temp/updated_books.csv");

			System.out.println("Finished");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void writeBookListCSV(List<Book> books, String csvFile) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(csvFile));

			for (Book e : books) {
				writer.append(e.toCSVLine());
			}
		} catch (Exception ex) {

		} finally {
			try {
				writer.close();
			} catch (Exception e) {
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
				b.setIsbn(columns[1]);
				b.setIsbnType(columns[2]);
				b.setAuthors(columns[4]);
				b.setTitle(columns[5]);
				b.setSummary(columns[6]);
				b.setPublisher(columns[7]);
				b.setReleaseDate(columns[8]);
				b.setPrice(Float.parseFloat(columns[9]));

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

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import at.spot.b4lbookscanner.googlebooks.Book;
import at.spot.b4lbookscanner.googlebooks.IndustryIdentifiers;
import at.spot.b4lbookscanner.googlebooks.VolumeInfo;
import at.spot.b4lbookscanner.googlebooks.VolumeList;
import at.spot.b4lbookscanner.googlebooks.util.Util;
import at.spot.util.StringUtil;

public class BookImageGrabber {

	private final static String DB_CONNECTION_URL = "jdbc:sqlite:/Users/matthias/Desktop/b4l_temp/db/db.sqlite";
	
	public static void fillUpMissingData() {
		List<Book> books = null;
		List<Book> updatedBooks = new ArrayList<>();
		
		try {
//			books = readBookListCSV("/Users/matthias/Desktop/b4l_temp/to_import.csv");
			books = readBookListDB();

			VolumeList googleBook = null;
			VolumeInfo info = null;
			
			int count = 1;

			for (Book e : books) {
				info = null;
				
				System.out.println("Updating " + (count++) + "/" + books.size());

				if (e.isFilledOut()) {
					continue;
				} else {
					updatedBooks.add(e);
				}
				
				if (e.getIsbnType() != null && e.getIsbnType().equals("ISBN_10")) {
					googleBook = Util.getBookByISBN10(e.getIsbn());
				} else if (e.getIsbnType() != null && e.getIsbnType().equals("ISBN_13")) {
					googleBook = Util.getBookByISBN(e.getIsbn());
				} else {
					googleBook = Util.getBookByTitle(e.getTitle());
				}
				
				if (googleBook != null && googleBook.getItems() != null && googleBook.getItems().size() >= 1) {
					info = googleBook.getItems().get(0).getVolumeInfo();
				} else {
					System.out.println("Could not retrieve book infos from Google!");
				}
				
				if (info != null) {
					if (!StringUtil.check(e.getImageUrl())) {
						if (info.getImageLinks() != null) {
							e.setImageUrl(info.getImageLinks().getThumbnail());
						} else {
							e.setImageUrl("http://shop.b4l-wien.at/images/nopic.png");
						}
					}
					
					if (!StringUtil.check(e.getIsbn())) {
						String isbn = "";
						String isbnType = "";
						
						if (info.getIndustryIdentifiers() != null && info.getIndustryIdentifiers().size() > 0) {
						
							for (IndustryIdentifiers ii : info.getIndustryIdentifiers()) {
								if (ii.getType().contains("13")) {
									isbn = ii.getIdentifier();
									isbnType = ii.getType();
									break;
								}
							}
							
							if (!StringUtil.check(isbn)) {
								isbn = info.getIndustryIdentifiers().get(0).getIdentifier();
								isbnType = info.getIndustryIdentifiers().get(0).getType();
							}
						
							e.setIsbn(isbn);;
							e.setIsbnType(isbnType);
						}
					}
					
					if (!StringUtil.check(e.getPublisher())) {
						if (info.getPublisher() != null) {
							e.setPublisher(info.getPublisher());
						}
					}
					
					if (!StringUtil.check(e.getReleaseDate())) {
						if (info.getPublisher() != null) {
							e.setPublisher(info.getPublishedDate());
						}
					}
				}
				
				//downloadImageToFolder(e.getImageUrl(), "/Users/matthias/Desktop/b4l_temp/images/" + e.getIsbn() + ".jpg");
//				e.setImageUrl("http://shop.b4l-wien.at/images/" + e.getIsbn());
//				updatedBooks.add(e);

//				if (count % 5 == 0) {
//					writeBookListCSV(books, "/Users/matthias/Desktop/b4l_temp/updated_books.csv",
//							"/Users/matthias/Desktop/b4l_temp/images/");
//					updatedBooks.clear();
////					Thread.sleep(2000);
//				}

				Thread.sleep(1000);
				
				saveToDB(e);
			}

			System.out.println("Finished");

		} catch (Exception e) {
			if (e instanceof IOException) {
				IOException ex = (IOException) e;
				System.out.println("Google interruption ...");
			} else {
				e.printStackTrace();
			}
				
			System.exit(0);
		} finally {
//			if (updatedBooks != null && updatedBooks.size() > 0) {
//				writeBookListCSV(books, "/Users/matthias/Desktop/b4l_temp/updated_books.csv",
//						"/Users/matthias/Desktop/b4l_temp/images/");
//			}	
		}
	}

	public static void writeBookListCSV(List<Book> books, String csvFile, String imagePath) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(csvFile, true));

			String csvLine = "";

			for (Book e : books) {
				csvLine = 
						e.getId() + ";" +
						e.getId() + ";" +
						e.getIsbn() + ";" +
						e.getIsbnType() + ";" +
						e.getAuthors() + ";" +
						"\"" + e.getTitle() + "\";" +
						"\"" + (StringUtil.check(e.getSummary()) ? e.getSummary() : "") + "\";" +
						(StringUtil.check(e.getPublisher()) ? e.getPublisher() : "") + ";" + 
						(StringUtil.check(e.getReleaseDate()) ? e.getReleaseDate() : "") + ";" +
						(e.getPrice() != null ? e.getPrice() : "" + ";" + 
						(StringUtil.check(e.getStorageLocation()) ? e.getStorageLocation() : "") + ";" +
						e.getImageUrl());

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

//				if (!columns[0].equals("x")) {
				if (true) {
					if (StringUtil.check(columns[2]))
						b.setId(Integer.parseInt(columns[2]));
						
//					b.setNumber(columns[2]);
					
					if (StringUtil.check(columns[3]))
						b.setIsbn(columns[3].replace("/", "_"));
					
					b.setIsbnType(columns[4]);
					b.setAuthors(columns[5]);
					b.setTitle(columns[6]);
					b.setSummary(columns[7]);
					b.setPublisher(columns[8]);
					b.setReleaseDate(columns[9]);
					
					if (StringUtil.check(columns[10])) {
						columns[10] = columns[10].replace(",", ".");
						b.setPrice(Float.parseFloat(columns[10]));
					}
					
					if (columns.length >= 14)
						b.setStorageLocation(columns[13]);
	
					if (columns.length >= 15) {
						b.setImageUrl(columns[14]);
					}
	
					books.add(b);
				} else {
					System.out.println("Ignoring book with number=" + columns[2]);
				}
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
	
	public static List<Book> readBookListDB() throws Exception {
		List<Book> books = new ArrayList<>();
		
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet r = null;
		
		try {
			con = getConnection();
			pstat = con.prepareStatement("SELECT * FROM book; -- WHERE isbn is null or isbn = '';");
			
			r = pstat.executeQuery();
			
			while (r.next()) {
				Book b = new Book();
				
				b.setId(r.getInt("number"));
				b.setNumber(r.getInt("number") + "");
				b.setIsbn(r.getString("isbn"));
				b.setIsbnType(r.getString("isbn_type"));
				b.setTitle(r.getString("title"));
				b.setSummary(r.getString("summary"));
				b.setAuthors(r.getString("authors"));
				b.setPublisher(r.getString("publisher"));
				b.setReleaseDate(r.getString("release_date"));
				b.setPrice(r.getFloat("price"));
				b.setImageUrl(r.getString("image_url"));
				b.setStorageLocation(r.getString("storage_location"));
				
				books.add(b);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				con.close();
			} catch (Exception e) {}
		}
		
		return books;
	}

	public static void saveToDB(Book book) throws Exception {
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet r = null;
		
		try {
			con = getConnection();
			pstat = con.prepareStatement(
					"UPDATE book " +
					"SET " +
					"	isbn = ?, isbn_type = ?, authors = ?, title = ?, summary = ?, " +
					"	publisher = ?, release_date = ?, price = ?, image_url = ?, storage_location = ? " +
					"WHERE number = ?;");
			
			int x = 1;
			
			pstat.setString(x++, book.getIsbn());
			pstat.setString(x++, book.getIsbnType());
			pstat.setString(x++, book.getAuthors());
			pstat.setString(x++, book.getTitle());
			pstat.setString(x++, book.getSummary());
			pstat.setString(x++, book.getPublisher());
			pstat.setString(x++, book.getReleaseDate());
			pstat.setFloat(x++, book.getPrice());
			pstat.setString(x++, book.getImageUrl());
			pstat.setString(x++, book.getStorageLocation());
			pstat.setInt(x++, book.getId());
			
			pstat.execute();
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				con.close();
			} catch (Exception e) {}
		}
	}

	public static void saveToDB(List<Book> books) {
		try {
			
//		    Statement stat = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws Exception {
		Class.forName("org.sqlite.JDBC");
		Connection con = DriverManager.getConnection(DB_CONNECTION_URL);
		
		return con;
	}
	
	public static void downloadMissingImages() {
		try {
			List<Book> books = readBookListDB();
			
			int x = 1;
			
			for (Book b : books) {
				System.out.println("Downloading image " + x++);
				
				File f = new File("/Users/matthias/Desktop/b4l_temp/images/" + b.getIsbn().replace(":", "_") + ".jpg");
				
				if (StringUtil.check(b.getImageUrl()) && !b.getImageUrl().contains("nopic") && !f.exists())
					downloadImageToFolder(b.getImageUrl(), f.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
//		fillUpMissingData();
		downloadMissingImages();
	}
}

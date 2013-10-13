package at.spot.b4lbookscanner.googlebooks;

import at.spot.util.StringUtil;


public class Book {

	Integer id;
	String isbn;
	String isbnType;
	String authors;
	String title;
	String summary;
	String publisher;
	String releaseDate;
	String number;
	String imageUrl;
	String storageLocation;

	Float	price;

	public Book() {

	}

	public Book(String isbn, String isbnType, String title, String authors, String publisher,
			String releaseDate, String summary) {

		this.isbn = isbn;
		this.isbnType = isbnType;
		this.authors = authors;
		this.title = title;
		this.summary = summary;
		this.publisher = publisher;
		this.releaseDate = releaseDate;
	}

	public Book(int id, String isbn, String isbnType, String title, String authors, String publisher,
			String releaseDate, String summary) {

		this(isbn, isbnType, title, authors, publisher, releaseDate, summary);

		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getIsbn() {
		return this.isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getIsbnType() {
		return this.isbnType;
	}

	public void setIsbnType(String isbnType) {
		this.isbnType = isbnType;
	}

	public String getAuthors() {
		return this.authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPublisher() {
		return this.publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getReleaseDate() {
		return this.releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Float getPrice() {
		return this.price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getNumber() {
		return this.number;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return getTitle() + "\nAuthor: " + (getAuthors() != null ? getAuthors() : "<null>") +
				", Barcode: " + (getIsbn() != null ? getIsbn() : "<null>") +
				", Price: " + getPrice() +
				", Number: " + getNumber();
	}

	public String toCSVLine() {
		return getId() + ";" + getIsbn() + ";" + getIsbnType() + ";" + getAuthors() + ";" + getTitle() + ";"
				+ getSummary() + ";" + getPublisher() + ";" + getReleaseDate() + ";" + getPrice() + ";"
				+ (getNumber() != null ? getNumber() : "") + ";" + getImageUrl();
	}
	
	public boolean isFilledOut() {
		return StringUtil.check(getIsbn()) & StringUtil.check(getPublisher()) & StringUtil.check(getAuthors()) &
				StringUtil.check(getIsbnType()) & StringUtil.check(getTitle()) & StringUtil.check(getReleaseDate()) &
				StringUtil.check(getImageUrl()); 
	}
}

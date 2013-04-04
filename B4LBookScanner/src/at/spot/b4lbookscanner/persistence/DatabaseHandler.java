package at.spot.b4lbookscanner.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import at.spot.b4lbookscanner.googlebooks.Book;

public class DatabaseHandler extends SQLiteOpenHelper {
	private final String		TAG					= "DatabaseHandler";

	final SimpleDateFormat		parser				= new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

	// All Static variables
	// Database Version
	private static final int	DATABASE_VERSION	= 8;

	// Database Name
	private static final String	DATABASE_NAME		= "b4l_book_index";

	// Contacts table name
	private static final String	TABLE_BOOKS			= "book";

	// Contacts Table Columns names
	private static final String	KEY_ID				= "id";
	private static final String	KEY_ISBN			= "isbn";
	private static final String	KEY_ISBN_TYPE		= "isbn_type";
	private static final String	KEY_AUTHORS			= "authors";
	private static final String	KEY_SUMMARY			= "summary";
	private static final String	KEY_TITLE			= "title";
	private static final String	KEY_PUBLISHER		= "publisher";
	private static final String	KEY_RELEASE_DATE	= "release_date";
	private static final String	KEY_PRICE			= "price";
	private static final String	KEY_NUMBER			= "number";
	private static final String	KEY_IMAGE_URL		= "image_url";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		initDB(db);
	}

	private void initDB(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "(" +
				KEY_ID + " INTEGER PRIMARY KEY, " +
				KEY_ISBN + " TEXT, " +
				KEY_ISBN_TYPE + " TEXT, " +
				KEY_AUTHORS + " TEXT, " +
				KEY_TITLE + " TEXT, " +
				KEY_SUMMARY + " TEXT, " +
				KEY_PUBLISHER + " TEXT, " +
				KEY_RELEASE_DATE + " TEXT, " +
				KEY_PRICE + " TEXT, " +
				KEY_NUMBER + " TEXT, " +
				KEY_IMAGE_URL + " TEXT " +
				")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);

		// Create tables again
		initDB(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	public void addBook(int id, String isbn, String isbnType, String title, String authors, String publisher,
			String releaseDate, String summary, float price) {

		Book b = new Book(id, isbn, isbnType, title, authors, publisher, releaseDate, summary);
		b.setPrice(price);

		addBook(b);
	}

	public void addBook(Book book) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ISBN, book.getIsbn());
		values.put(KEY_ISBN_TYPE, book.getIsbnType());
		values.put(KEY_TITLE, book.getTitle());
		values.put(KEY_AUTHORS, book.getAuthors());
		values.put(KEY_PUBLISHER, book.getPublisher());
		values.put(KEY_RELEASE_DATE, book.getReleaseDate());
		values.put(KEY_PRICE, book.getPrice());
		values.put(KEY_NUMBER, book.getNumber());
		values.put(KEY_SUMMARY, book.getSummary());
		values.put(KEY_IMAGE_URL, book.getImageUrl());

		// Inserting Row
		db.insert(TABLE_BOOKS, null, values);
		db.close(); // Closing database connection
	}

	public Book getBook(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_BOOKS, new String[] {
				KEY_ID, KEY_ISBN, KEY_ISBN_TYPE, KEY_TITLE, KEY_AUTHORS, KEY_PUBLISHER, KEY_RELEASE_DATE,
				KEY_SUMMARY, KEY_PRICE, KEY_NUMBER, KEY_IMAGE_URL }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		Book book = null;

		try {
			book = newBook(cursor);

		} catch (Exception e) {
			Log.e(TAG, e.getStackTrace().toString());
		}

		return book;
	}

	public Book getBookByTitle(String title) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_BOOKS, new String[] {
				KEY_ID, KEY_ISBN, KEY_ISBN_TYPE, KEY_TITLE, KEY_AUTHORS, KEY_PUBLISHER, KEY_RELEASE_DATE,
				KEY_SUMMARY, KEY_PRICE, KEY_NUMBER }, KEY_TITLE + "=?",
				new String[] { title }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		Book book = null;

		try {
			book = newBook(cursor);

		} catch (Exception e) {
			Log.e(TAG, e.getStackTrace().toString());
		}

		return book;
	}

	public List<Book> getAllBooks() {
		List<Book> books = new ArrayList<Book>();
		String selectQuery = "SELECT id, isbn, isbn_type, title, authors, publisher, release_date, summary, price, number FROM "
				+ TABLE_BOOKS;

		SQLiteDatabase db = null;

		try {
			db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Book book = newBook(cursor);
					books.add(book);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getStackTrace().toString());
		} finally {
			if (db != null)
				db.close();
		}

		return books;
	}

	private Book newBook(Cursor bookCursor) throws ParseException {
		Book b = new Book(bookCursor.getInt(0),
							bookCursor.getString(1),
							bookCursor.getString(2),
							bookCursor.getString(3),
							bookCursor.getString(4),
							bookCursor.getString(5),
							bookCursor.getString(6),
							bookCursor.getString(7));

		b.setPrice(bookCursor.getFloat(8));
		b.setNumber(bookCursor.getString(9));
		b.setImageUrl(bookCursor.getString(10));

		return b;
	}

	public int updateBook(Book book) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ISBN, book.getIsbn());
		values.put(KEY_ISBN_TYPE, book.getIsbnType());
		values.put(KEY_TITLE, book.getTitle());
		values.put(KEY_AUTHORS, book.getAuthors());
		values.put(KEY_PUBLISHER, book.getPublisher());
		values.put(KEY_RELEASE_DATE, book.getReleaseDate());
		values.put(KEY_SUMMARY, book.getSummary());
		values.put(KEY_PRICE, book.getPrice());
		values.put(KEY_NUMBER, book.getNumber());
		values.put(KEY_IMAGE_URL, book.getImageUrl());

		// updating row

		int ret = db.update(TABLE_BOOKS, values, KEY_ID + " = ?", new String[] { String.valueOf(book.getId()) });
		db.close();

		return ret;
	}

	public void deleteContact(Book book) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BOOKS, KEY_ID + " = ?", new String[] { String.valueOf(book.getId()) });
		db.close();
	}

	public int getLastBookNumber() {
		String countQuery = "SELECT max(number) FROM " + TABLE_BOOKS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int ret = -1;

		if (cursor != null)
			cursor.moveToFirst();

		try {
			ret = cursor.getInt(0);
		} catch (Exception e) {
			Log.e(TAG, e.getStackTrace().toString());
		} finally {
			if (cursor != null)
				cursor.close();
			if (db != null)
				db.close();
		}

		return ret;
	}

	public int getBookCount() {
		String countQuery = "SELECT  * FROM " + TABLE_BOOKS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int ret = cursor.getCount();
		cursor.close();
		db.close();

		return ret;
	}

}

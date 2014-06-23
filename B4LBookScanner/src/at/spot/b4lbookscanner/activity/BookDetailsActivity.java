package at.spot.b4lbookscanner.activity;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import at.spot.b4lbookscanner.R;
import at.spot.b4lbookscanner.googlebooks.Book;
import at.spot.b4lbookscanner.googlebooks.IndustryIdentifiers;
import at.spot.b4lbookscanner.googlebooks.VolumeInfo;
import at.spot.b4lbookscanner.googlebooks.VolumeList;
import at.spot.b4lbookscanner.persistence.DataStore;
import at.spot.b4lbookscanner.persistence.DatabaseHandler;
import at.spot.b4lbookscanner.util.Util;
import at.spot.prestashop.service.PrestaShopClient;
import at.spot.prestashop.service.json.Category;
import at.spot.prestashop.service.json.EStatus;
import at.spot.prestashop.service.json.Product;
import at.spot.util.HttpUtil;
import at.spot.util.StringUtil;

public class BookDetailsActivity extends Activity {
	TextView						title					= null;
	TextView						authors					= null;
	TextView						summary					= null;
	TextView						publishedDate			= null;
	TextView						publisher				= null;
	ImageView						bookCover				= null;

	Button							saveButton				= null;

	String							isbn					= "";
	String							isbnType				= "";

	Book							book					= null;

	SimpleDateFormat				parser					= new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

	private static final float[]	prices					= new float[] { 0.5f, 1f, 2f, 3f, 4f, 5f };

	Category						selectedBookCategory	= null;

	DatabaseHandler					db						= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_details);

		db = new DatabaseHandler(this);

		setupUI();
		fillDetails();
	}

	protected void setupUI() {
		title = (TextView) findViewById(R.id.book_title);
		authors = (TextView) findViewById(R.id.book_author);
		summary = (TextView) findViewById(R.id.book_summary);
		summary = (TextView) findViewById(R.id.book_summary);
		publishedDate = (TextView) findViewById(R.id.book_publised_date);
		publisher = (TextView) findViewById(R.id.book_publisher);
		bookCover = (ImageView) findViewById(R.id.book_image);

		saveButton = (Button) findViewById(R.id.save_book);

		final Context context = this;

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveBook();
			}
		});
	}

	private void saveBook() {
		showCategorySelectionDialog();
	}

	private void showCategorySelectionDialog() {
		List<String> cats = new ArrayList<String>();
		final Context context = this;

		for (Category p : DataStore.categories) {
			cats.add(String.format("%s [%s]", p.getName(), p.getId()));
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Choose a category");
		builder.setItems(cats.toArray(new String[cats.size()]),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						selectedBookCategory = DataStore.categories.get(item);

						dialog.dismiss();

						showPriceSelectionDialog();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showPriceSelectionDialog() {
		List<String> pricesToChoose = new ArrayList<String>();
		final Context context = this;

		for (float p : prices) {
			pricesToChoose.add(new DecimalFormat("#.##").format(p));
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Choose a price");
		builder.setItems(pricesToChoose.toArray(new String[pricesToChoose.size()]),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						book.setPrice(prices[item]);

						dialog.dismiss();
						// setBookNumber();

						try {
							saveBook(book);
							Toast.makeText(BookDetailsActivity.this, "Book saved successfully", Toast.LENGTH_SHORT)
									.show();
							finish();
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(BookDetailsActivity.this, "Error while saving the book",
									Toast.LENGTH_SHORT)
									.show();
						}
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void setBookNumber() {
		final Context context = this;

		final AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);

		inputDialog.setTitle("Enter book number");
		inputDialog.setMessage("Please enter the number for this book here:");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setSelectAllOnFocus(true);

		try {
			input.setText((db.getLastBookNumber() + 1) + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		input.setInputType(InputType.TYPE_CLASS_NUMBER);

		inputDialog.setView(input);

		inputDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String number = input.getText().toString();

				if (number == null || number.equals("")) {
					setBookNumber();
					return;
				}

				book.setNumber(number);

				Book b = db.getBookByTitle(book.getTitle());

				// if (b != null) {
				// book.setId(b.getId());
				//
				// db.updateBook(book);
				// } else {
				// db.addBook(book);
				// }

				try {
					saveBook(b);
				} catch (Exception e) {
					e.printStackTrace();
				}

				dialog.dismiss();
				finish();
			}
		});

		AlertDialog d = inputDialog.create();
		d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		d.show();
	}

	private void saveBook(Book book) throws Exception {
		try {
			PrestaShopClient pc = new PrestaShopClient("http://shop.b4l-wien.at/service",
														"NL0VF0CJ48EE3QVNHO4W0ZWCZ6UY1VPN");

			Product p = new Product();
			p.setTitle(book.getTitle());
			p.setAmount(1);
			p.setShortDescription(""); // getSummary(book, 100)
			p.setLongDescription(getSummary(book, null));
			p.setPrice(book.getPrice());
			p.setTags(getTags(book));
			p.setCategoryId(Integer.parseInt(selectedBookCategory.getId()));
			p.setOnline(false);
			p.setStatus(EStatus.Used);
			// p.setSupplierId(32);
			p.setStockLocationId(37);
			p.setVatRate(10f);
			p.setEan13(book.getIsbn());

			try {
				byte[] image = null;// Files.readAllBytes(Paths.get("/Users/matthias/Desktop/913.jpg"));

				if (book.getImageUrl() != null) {
					image = HttpUtil.download(book.getImageUrl());
				}

				if (image != null) {
					p.getImages().add(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			pc.addProduct(p);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private List<String> getTags(Book book) {
		List<String> tags = new ArrayList<String>();

		if (book.getAuthors() != null)
			tags.add(book.getAuthors());

		if (book.getPublisher() != null)
			tags.add(book.getPublisher());

		if (book.getTitle() != null)
			tags.add(book.getTitle());

		return tags;
	}

	private String getSummary(Book book, Integer length) {
		String ret = "";

		if (book.getSummary() != null) {
			ret = book.getSummary();
		} else {
			if (book.getAuthors() != null) {
				ret += "\nAuthors: " + book.getAuthors();
			}

			if (book.getPublisher() != null) {
				ret += "\nPublisher: " + book.getPublisher();
			}

			if (book.getReleaseDate() != null) {
				ret += "\nRelease date: " + book.getReleaseDate();
			}
		}

		ret = ret.trim();

		if (StringUtil.check(ret)) {
			if (length != null && ret.length() > length) {
				ret = ret.substring(0, length);
			}
		}

		return ret;
	}

	private void fillDetails() {
		VolumeList b = (VolumeList) getIntent().getSerializableExtra("book_details");
		int itemIndex = (Integer) getIntent().getSerializableExtra("book_details_item_index");

		if (b != null && b.getItems() != null) {
			VolumeInfo i = b.getItems().get(itemIndex).getVolumeInfo();

			String isbn = i.getIndustryIdentifiers().get(0).getIdentifier();

			for (IndustryIdentifiers ii : i.getIndustryIdentifiers()) {
				if (ii.getType().contains("13")) {
					isbn = ii.getIdentifier();
					break;
				}
			}

			String isbnType = i.getIndustryIdentifiers().get(0).getType();

			title.setText(i.getTitle());
			summary.setText(i.getDescription() != null ? i.getDescription() : "Keine Zusammenfassung vorhanden");

			String bookAuthors = "";

			if (i.getAuthors() != null) {
				for (String a : i.getAuthors()) {
					bookAuthors += a + ", ";
				}

				bookAuthors = bookAuthors.substring(0, bookAuthors.length() - 2).trim();
			} else {
				bookAuthors = "Unbekannt";
			}

			authors.setText(bookAuthors);
			publishedDate.setText(i.getPublishedDate() != null ? i.getPublishedDate() : "Unbekannt");
			publisher.setText(i.getPublisher() != null ? i.getPublisher() : "Unbekannt");

			if (i.getImageLinks() != null) {
				Bitmap image = Util.getImageContent(i.getImageLinks().getThumbnail());

				if (image != null) {
					bookCover.setImageBitmap(image);
				}
			}

			book = new Book(isbn,
							isbnType,
							i.getTitle(),
							i.getAuthors() != null ? authors.getText().toString() : null,
							i.getPublisher(),
							i.getPublishedDate(),
							i.getDescription());

			book.setImageUrl(i.getImageLinks() != null ? i.getImageLinks().getThumbnail() : null);

		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_details, menu);
		return true;
	}
}

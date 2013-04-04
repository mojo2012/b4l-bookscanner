package at.spot.b4lbookscanner.activity;

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
import at.spot.b4lbookscanner.R;
import at.spot.b4lbookscanner.google.service.book.IndustryIdentifiers;
import at.spot.b4lbookscanner.google.service.book.Util;
import at.spot.b4lbookscanner.google.service.book.VolumeInfo;
import at.spot.b4lbookscanner.google.service.book.VolumeList;
import at.spot.b4lbookscanner.persistence.Book;
import at.spot.b4lbookscanner.persistence.DatabaseHandler;

public class BookDetailsActivity extends Activity {
	TextView						title			= null;
	TextView						authors			= null;
	TextView						summary			= null;
	TextView						publishedDate	= null;
	TextView						publisher		= null;
	ImageView						bookCover		= null;

	Button							saveButton		= null;

	String							isbn			= "";
	String							isbnType		= "";

	Book							book			= null;

	SimpleDateFormat				parser			= new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

	private static final float[]	prices			= new float[] { 0.5f, 1f, 2f, 3f, 4f, 5f };

	DatabaseHandler					db				= null;

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
				showPriceSelectionDialog();
			}
		});
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
						setBookNumber();
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

				if (b != null) {
					book.setId(b.getId());

					db.updateBook(book);
				} else {
					db.addBook(book);
				}

				dialog.dismiss();
				finish();
			}
		});

		AlertDialog d = inputDialog.create();
		d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		d.show();
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

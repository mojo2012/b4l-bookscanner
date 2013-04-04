package at.spot.b4lbookscanner.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.spot.b4lbookscanner.R;
import at.spot.b4lbookscanner.google.service.book.Items;
import at.spot.b4lbookscanner.google.service.book.Util;
import at.spot.b4lbookscanner.google.service.book.VolumeList;
import at.spot.b4lbookscanner.persistence.DatabaseHandler;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {
	private static final String	TAG					= "MainActivity";

	private Button				saveChanges			= null;
	private Button				scanISBN			= null;
	private Button				enterISBN			= null;
	private Button				searchByTitle		= null;
	private Button				showScannedBarcodes	= null;

	private String				lastEnteredISBN		= "";
	private String				lastEnteredTitle	= "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupUI();
	}

	protected void setupUI() {
		final Context context = this;

		saveChanges = (Button) findViewById(R.id.upload);
		scanISBN = (Button) findViewById(R.id.scan_isbn);
		enterISBN = (Button) findViewById(R.id.enter_isbn);

		scanISBN.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startScanning();
			}
		});

		enterISBN.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enterISBN();
			}
		});

		searchByTitle = (Button) findViewById(R.id.search_by_title);
		searchByTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchByTitle();
			}
		});

		showScannedBarcodes = (Button) findViewById(R.id.show_scanned_barcodes);
		showScannedBarcodes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (new DatabaseHandler(context).getBookCount() > 0) {
					startActivity(new Intent(getBaseContext(), ShowAllScannedBarcodesActivity.class));
				} else {
					showToast("There are no books in database!");
				}
			}
		});
	}

	protected void enterISBN() {
		final AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);

		inputDialog.setTitle("Enter ISBN");
		inputDialog.setMessage("Please enter the ISBN code here:");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(lastEnteredISBN);
		input.setSelectAllOnFocus(true);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(13) });

		inputDialog.setView(input);

		inputDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();

				String value = input.getText().toString();
				lastEnteredISBN = value;
				isbnFound(value);
			}
		});

		AlertDialog d = inputDialog.create();
		d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		d.show();
	}

	protected void searchByTitle() {
		final AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);

		inputDialog.setTitle("Enter title");
		inputDialog.setMessage("Please enter parts of the title here:");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(lastEnteredTitle);
		input.setSelectAllOnFocus(true);
		input.setInputType(InputType.TYPE_CLASS_TEXT);

		inputDialog.setView(input);

		inputDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();

				String value = input.getText().toString();
				lastEnteredTitle = value;

				showBookDetailsView(Util.getBookByTitle(value));
			}
		});

		AlertDialog d = inputDialog.create();
		d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		d.show();
	}

	private void isbnFound(String rawISBN) {
		// if (Util.isValidISBN(Util.correctISBN(rawISBN))) {
		// openBookDetailsView(rawISBN);
		// } else {
		// showToast("The entered ISBN is not valid!");
		// }

		openBookDetailsView(rawISBN);
	}

	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	protected void startScanning() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

		if (scanResult != null & scanResult.getContents() != null) {
			String msg = "Found barcode :" + scanResult.getContents();
			Log.i(TAG, msg);

			isbnFound(scanResult.getContents());
		} else {
			showToast("Nothing found!");
		}
	}

	protected void openBookDetailsView(String barcode) {
		VolumeList b = Util.getBookByISBN(barcode);

		showBookDetailsView(b);
	}

	private void showBookDetailsView(VolumeList volumeList) {
		if (volumeList != null && volumeList.getItems() != null && volumeList.getItems().size() > 0) {
			if (volumeList.getItems().size() > 1) {
				List<String> items = new ArrayList<String>();

				for (Items i : volumeList.getItems()) {
					// int isbnType = -1;
					//
					// for (IndustryIdentifiers ii :
					// i.getVolumeInfo().getIndustryIdentifiers()) {
					// if (ii.getType().toLowerCase().contains("isbn")) {
					// isbnType =
					// i.getVolumeInfo().getIndustryIdentifiers().indexOf(ii);
					// break;
					// }
					// }
					//
					// if (isbnType > -1)

					String isbn = ((i.getVolumeInfo().getIndustryIdentifiers() != null && i.getVolumeInfo()
							.getIndustryIdentifiers()
							.size() > 0) ? i.getVolumeInfo().getIndustryIdentifiers().get(0).getIdentifier() : "");

					items.add(i.getVolumeInfo().getTitle() + "\n" + isbn + ", "
							+ i.getVolumeInfo().getAuthors() + ", "
							+ i.getVolumeInfo().getPublisher() + ", " + i.getVolumeInfo().getPublishedDate());
				}

				showVolumeSelectionListDialog(volumeList, items.toArray(new String[items.size()]));
				return;
			} else {
				showBookDetailsView(volumeList, 0);
			}
		} else {
			showToast("Sorry. Not book details have been found!");
		}
	}

	private void showBookDetailsView(VolumeList volumeList, int itemIndex) {
		Intent intent = new Intent(getBaseContext(), BookDetailsActivity.class);
		intent.putExtra("book_details", volumeList);
		intent.putExtra("book_details_item_index", itemIndex);
		startActivity(intent);
	}

	protected void showVolumeSelectionListDialog(VolumeList volumes, String[] listEntries) {
		final VolumeList volumeList = volumes;
		final String[] items = listEntries;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Make your selection");
		builder.setItems(listEntries, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				showBookDetailsView(volumeList, item);

				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

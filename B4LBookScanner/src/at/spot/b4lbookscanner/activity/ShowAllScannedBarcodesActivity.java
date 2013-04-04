package at.spot.b4lbookscanner.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.spot.b4lbookscanner.R;
import at.spot.b4lbookscanner.persistence.Book;
import at.spot.b4lbookscanner.persistence.DatabaseHandler;

public class ShowAllScannedBarcodesActivity extends Activity {

	ListView		list		= null;

	DatabaseHandler	db			= null;

	List<String>	books		= new ArrayList<String>();
	List<Book>		bookList	= new ArrayList<Book>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_all_scanned_barcodes);

		// List<Book> books =
		db = new DatabaseHandler(this);

		setupUI();
	}

	private void setupUI() {
		list = (ListView) findViewById(R.id.scanned_barcodes_list);

		books = new ArrayList<String>();

		bookList = getStoredBooks();

		for (Book b : bookList) {
			books.add(b.getTitle() + "\nAuthor: " + (b.getAuthors() != null ? b.getAuthors() : "<null>") +
					", Barcode: " + (b.getIsbn() != null ? b.getIsbn() : "<null>") +
					", Price: " + b.getPrice() +
					", Number: " + b.getNumber()
					);
		}

		ArrayAdapter<Book> adapter = new ArrayAdapter<Book>(this,
															android.R.layout.simple_list_item_1,
															android.R.id.text1,
															bookList);

		list.setAdapter(adapter);
		registerForContextMenu(list);
	}

	private List<Book> getStoredBooks() {
		return db.getAllBooks();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_all_scanned_barcodes, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(bookList.get(info.position).getTitle());
		String[] menuItems = new String[] { "Delete" };

		for (int i = 0; i < menuItems.length; i++) {
			menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();

		Book selected = (Book) list.getAdapter().getItem(info.position);

		switch (menuItemIndex) {
			case 0:
				bookList.remove(selected);
				list.invalidateViews();

				db.deleteContact(selected);
				break;
		}

		return true;
	}
}

package ch.hearc.devmobile.travelnotebook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class HomeActivity extends Activity {

	private DatabaseHelper databaseHelper = null;
	private List<MenuElement> drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		buildDrawer();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(HomeActivity.this,
					((TextView) view).getText().toString() + id + position,
					Toast.LENGTH_LONG).show();
			HomeActivity.this.drawerLayout.closeDrawer(drawerListView);
		}
	}

	private void buildDrawer() {
		// Drawer menu
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerListViewItems = new ArrayList<MenuElement>();

		
		// Create a new notebook
		MenuElement newNotebookAction = new MenuElement(getResources()
				.getString(R.string.new_notebook), new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO change notebookActivity to newNotebook
				Intent intent = new Intent(HomeActivity.this,
						NotebookActivity.class);
				startActivity(intent);
				HomeActivity.this.drawerLayout.closeDrawer(drawerListView);
			}
		});
		drawerListViewItems.add(newNotebookAction);
		
		// Go To settings
		MenuElement settingsAction = new MenuElement(getResources().getString(R.string.settings), new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
				startActivity(intent);
				HomeActivity.this.drawerLayout.closeDrawer(drawerListView);
			}
		});
		drawerListViewItems.add(settingsAction);
		
		// TODO add notebook in the drawer from the database here :)
		
		
		// gets ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

		// Sets the adapter for the list view
		drawerListView.setAdapter(new MenuElementArrayAdapter(this,
				drawerListViewItems));

		drawerListView.setOnItemClickListener(new DrawerItemClickListener());
	}

}

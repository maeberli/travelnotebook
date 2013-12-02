package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class HomeActivity extends Activity {

	/********************
	 * Static
	 ********************/
	public static final String NOTEBOOK_ID = "notebookId";
	
	/********************
	 * Private members
	 ********************/
	private DatabaseHelper databaseHelper = null;
	private List<MenuElement> drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	
	/********************
	 * Public methods
	 ********************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/********************
	 * Protected methods
	 ********************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Hide application title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide status bar	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_home);

		buildDrawer();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	
	/********************
	 * Private methods
	 ********************/
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	private void buildDrawer() {
		// Travel list
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerListViewItems = new ArrayList<MenuElement>();
		final RelativeLayout drawerPanel = (RelativeLayout) findViewById(R.id.right_drawer);
		
				
				
		
		// New notebook button
		Button btnNewNotebook = (Button) findViewById(R.id.btn_new_notebook);
		btnNewNotebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						NotebookActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				HomeActivity.this.drawerLayout.closeDrawer(drawerPanel);				
			}
		});
		
		// Settings button
		Button btnSettings = (Button) findViewById(R.id.btn_settings);
		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						SettingsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				HomeActivity.this.drawerLayout.closeDrawer(drawerPanel);				
			}
		});
		
		
		

		// Add voyages in the list from the database
		try {
			
			MenuElement voyageMenuElement = null;
			for(final Voyage voyage : getHelper().getVoyageDao().queryForAll() ) {
				
				voyageMenuElement = new MenuElement(voyage.getTitle(), new OnClickListener() {
		
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(HomeActivity.this,
								NotebookActivity.class);
						intent.putExtra(HomeActivity.this.NOTEBOOK_ID, voyage.getId());
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(intent);
						HomeActivity.this.drawerLayout.closeDrawer(drawerPanel);
					}
		
				});
				drawerListViewItems.add(voyageMenuElement);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		// gets ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.right_drawer_list);

		// Sets the adapter for the list view
		drawerListView.setAdapter(new MenuElementArrayAdapter(this,
				drawerListViewItems));

	}
	
	private void databaseStub(){
		Voyage voyage = new Voyage("My voyage", Color.rgb(220, 12, 123));
		Voyage voyage2 = new Voyage("India", Color.rgb(123, 112, 123));
		try {
			getHelper().getVoyageDao().create(voyage);
			getHelper().getVoyageDao().create(voyage2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

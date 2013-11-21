package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import ch.hearc.devmobile.travelnotebook.R;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Image;
import ch.hearc.devmobile.travelnotebook.database.Post;
import ch.hearc.devmobile.travelnotebook.database.Tag;
import ch.hearc.devmobile.travelnotebook.database.TagType;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;
import ch.hearc.devmobile.travelnotebook.database.Voyage;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private DatabaseHelper databaseHelper = null;
	private String[] drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// Drawer menu
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		// gets list items from strings.xml
        drawerListViewItems = getResources().getStringArray(R.array.homeMenuItems);
 
        // gets ListView defined in activity_main.xml
        drawerListView = (ListView) findViewById(R.id.left_drawer);
 
        // Sets the adapter for the list view
        drawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, drawerListViewItems));
		
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
		
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
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(HomeActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
            HomeActivity.this.drawerLayout.closeDrawer(drawerListView);
 
        }

    }

}

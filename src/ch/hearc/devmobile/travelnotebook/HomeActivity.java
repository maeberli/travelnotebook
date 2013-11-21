package ch.hearc.devmobile.travelnotebook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
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
		
		// Drawer menu
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		// gets list items from strings.xml
        String menuElementLabel = getResources().getString(R.string.new_notebook);
        
        OnClickListener action = new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				 Toast.makeText(HomeActivity.this, ((TextView)view).getText().toString(), Toast.LENGTH_LONG).show();
		          HomeActivity.this.drawerLayout.closeDrawer(drawerListView);			
			}
		};
        
        MenuElement debugGoToNotebook = new MenuElement(menuElementLabel, action);
        
        drawerListViewItems = new ArrayList<MenuElement>();
        drawerListViewItems.add( debugGoToNotebook );
 
        // gets ListView defined in activity_main.xml
        drawerListView = (ListView) findViewById(R.id.left_drawer);
 
        // Sets the adapter for the list view
        drawerListView.setAdapter(new MenuElementArrayAdapter(this, drawerListViewItems));
		
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
            Toast.makeText(HomeActivity.this, ((TextView)view).getText().toString()+id+position, Toast.LENGTH_LONG).show();
            HomeActivity.this.drawerLayout.closeDrawer(drawerListView);
 
        }

    }

}

package ch.hearc.devmobile.travelnotebook;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NotebookActivity extends Activity {

	private DatabaseHelper databaseHelper = null;
	private String[] drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notebook);

		// Drawer menu
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// gets list items from strings.xml
		drawerListViewItems = getResources().getStringArray(R.array.notebookMenuItems);

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
		getMenuInflater().inflate(R.menu.notebook, menu);
		return true;
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(NotebookActivity.this, ((TextView) view).getText(),
					Toast.LENGTH_LONG).show();
			NotebookActivity.this.drawerLayout.closeDrawer(drawerListView);

		}

	}

}

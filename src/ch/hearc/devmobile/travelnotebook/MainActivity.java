package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Image;
import ch.hearc.devmobile.travelnotebook.database.Post;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private DatabaseHelper databaseHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView displayInformation = (TextView) findViewById(R.id.displayInformation);

		try {
			Dao<Post, Integer> postDao = getHelper().getPostDao();
			Post newPost = new Post("Title" + System.currentTimeMillis(),
					"longer description", "Zurich, Suisse");

			Image image1 = new Image("new URI1", newPost);
			Image image2 = new Image("new URI2", newPost);

			getHelper().getImageDao().create(image1);
			getHelper().getImageDao().create(image2);

			List<Post> postList = postDao.queryForAll();
			StringBuilder sb = new StringBuilder();

			for (Post post : postList) {
				sb.append("------------------------------------------\n");
				for (Image image : post.getImages()) {
					sb.append(image).append("\n");
				}
			}

			displayInformation.setText(sb.toString());
		} catch (SQLException e) {
			displayInformation.setText(e.toString());
			e.printStackTrace();
		}
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

}

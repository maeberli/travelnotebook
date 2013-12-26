package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewNotebookActivity extends Activity {

	/********************
	 * Private members
	 ********************/
	private DatabaseHelper databaseHelper = null;
	public static final int RESULT_SQL_FAIL = -500;
	public static final String NOTEBOOK_ID_KEY = "notebookId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide application title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide status bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_new_notebook);

		databaseHelper = OpenHelperManager
				.getHelper(this, DatabaseHelper.class);
		
		// Cancel button
		Button btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NewNotebookActivity.this.setResult(RESULT_CANCELED);
				NewNotebookActivity.this.finish();
			}
		});

		// Save button
		Button btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {

					int newID = NewNotebookActivity.this.createNotebook();
					NewNotebookActivity.this.setResult(RESULT_OK);
					NewNotebookActivity.this.finish();

				} catch (SQLException e) {

					Toast.makeText(getApplicationContext(),
							"Creation failed !", Toast.LENGTH_SHORT).show();

					NewNotebookActivity.this.setResult(RESULT_SQL_FAIL);
					e.printStackTrace();
					NewNotebookActivity.this.finish();

				} catch (Exception e) {

					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();

				}

			}
		});

	}

	protected int createNotebook() throws Exception {
		EditText tvName = (EditText) findViewById(R.id.notebook_name);
		String name = tvName.getText().toString();
		if (name.length() == 0)
			throw new Exception("Invalide name");
		Dao<Voyage, Integer> voyageDao = databaseHelper.getVoyageDao();
		Voyage voyage = new Voyage(name, Color.RED);
		voyageDao.create(voyage);
		return voyage.getId();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_notebook, menu);
		return true;
	}

}
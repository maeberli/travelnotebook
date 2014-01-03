package ch.hearc.devmobile.travelnotebook;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MenuElementArrayAdapter extends ArrayAdapter<MenuElement> {

	/********************
	 * Private members
	 ********************/
	private final Context context;
	private final List<MenuElement> values;

	/********************
	 * Constructor
	 ********************/
	public MenuElementArrayAdapter(Context context, List<MenuElement> values) {
		super(context, R.layout.drawer_listview_item, values);
		this.context = context;
		this.values = values;
	}

	/********************
	 * Public methods
	 ********************/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.drawer_listview_item, parent, false);

		view.setOnClickListener(values.get(position).getActionListener());
		((TextView) view).setText(values.get(position).getLabel());

		return view;
	}

}

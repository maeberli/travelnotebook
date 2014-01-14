package ch.hearc.devmobile.travelnotebook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ch.hearc.devmobile.travelnotebook.database.TagType;

public class TagTypeSelectorAdapter extends BaseAdapter {

	/********************
	 * Private members
	 ********************/
	private Context context;
	private TagType[] tagTypes;

	/********************
	 * Constructor
	 ********************/
	public TagTypeSelectorAdapter(Context context, TagType[] tagTypes) {
		this.context = context;
		this.tagTypes = tagTypes;
	}

	/********************
	 * Public methods
	 ********************/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView image = new ImageView(this.context);
		image.setImageResource(TagType.getIconRessource(getItem(position)));

		return image;
	}

	@Override
	public int getCount() {
		return tagTypes.length;
	}

	@Override
	public TagType getItem(int position) {
		return tagTypes[position];
	}

	@Override
	public long getItemId(int position) {
		return tagTypes[position].hashCode();
	}

	public int getPosition(TagType tagType) {

		for(int i =0; i < tagTypes.length;++i)
		{
			if(tagTypes[i] == tagType)
				return i;
		}
		return -1;
	}
}
